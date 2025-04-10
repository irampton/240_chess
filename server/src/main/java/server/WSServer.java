package server;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.ChessGameDeserializer;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import websocket.commands.ConnectCommand;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

@WebSocket
public class WSServer {
    private final AuthDAO authDAO = new AuthDAO();
    private final GameDAO gameDAO = new GameDAO();

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer()).create();

    private static final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private static final Map<Session, Integer> sessionToGameMap = new ConcurrentHashMap<>();

    public WSServer() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        AuthData auth;
        GameData game;
        try {
            switch (command.getCommandType()) {
                case CONNECT:
                    auth = checkAuth(command.getAuthToken());
                    ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                    try {
                        game = gameDAO.getGame(connectCommand.getGameID());
                    } catch (Exception e) {
                        throw new Exception("Invalid game");
                    }
                    switch (connectCommand.getType()) {
                        case BLACK:
                            if (!game.getBlackUsername().equals(auth.getUsername())) {
                                throw new Exception("Unauthorized");
                            }
                            break;
                        case WHITE:
                            if (!game.getWhiteUsername().equals(auth.getUsername())) {
                                throw new Exception("Unauthorized");
                            }
                            break;
                    }
                    String joinMessage = auth.getUsername() + " joined the game as ";
                    System.out.print("User: " + auth.getUsername()
                            + " connected to game " + game.getGameName() + " as ");
                    switch (connectCommand.getType()) {
                        case BLACK:
                            System.out.print("black");
                            joinMessage += "black";
                            break;
                        case WHITE:
                            System.out.print("white");
                            joinMessage += "white";
                            break;
                        case OBSERVER:
                            System.out.print("an observer");
                            joinMessage += "an observer";
                            break;
                    }
                    System.out.print("\n");

                    gameSessions.computeIfAbsent(connectCommand.getGameID(), k -> new CopyOnWriteArraySet<>())
                            .add(session);
                    sessionToGameMap.put(session, connectCommand.getGameID());

                    session.getRemote().sendString(gson.toJson(new LoadGameMessage(game.getGame())));

                    broadcastToOtherInGame(connectCommand.getGameID(), session,
                            gson.toJson(new NotificationMessage(joinMessage)));
                    break;
                case MAKE_MOVE:
                    auth = checkAuth(command.getAuthToken());
                    try {
                        game = gameDAO.getGame(command.getGameID());
                    } catch (Exception e) {
                        throw new Exception("Invalid game");
                    }

                    if (game.getGameOver()) {
                        throw new Exception("Game over");
                    }

                    ChessGame.TeamColor playerColor;
                    // Make sure user is authorized to make a move
                    if (auth.getUsername().equalsIgnoreCase(game.getWhiteUsername())) {
                        playerColor = ChessGame.TeamColor.WHITE;
                    } else if (auth.getUsername().equalsIgnoreCase(game.getBlackUsername())) {
                        playerColor = ChessGame.TeamColor.BLACK;
                    } else {
                        throw new Exception("You cannon make a move. You are not a player in this game.");
                    }

                    if (playerColor != game.getGame().getTeamTurn()) {
                        throw new Exception("It is not your turn.");
                    }

                    MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
                    ChessMove playerMove = moveCommand.getMove();

                    game.getGame().makeMove(playerMove);
                    try {
                        gameDAO.updateGame(game);
                    } catch (Exception e) {
                        throw new Exception("Error saving game data");
                    }
                    broadcastToGame(moveCommand.getGameID(), gson.toJson(new LoadGameMessage(game.getGame())));
                    String moveMessage = playerColor == ChessGame.TeamColor.WHITE ? "White" : "Black";
                    moveMessage += " moved from " + friendlyPosition(playerMove.getStartPosition())
                            + " to " + friendlyPosition(playerMove.getEndPosition());
                    broadcastToOtherInGame(moveCommand.getGameID(), session,
                            gson.toJson(new NotificationMessage(moveMessage)));
                    break;
                case LEAVE:
                    auth = checkAuth(command.getAuthToken());
                    LeaveCommand leaveMsg = gson.fromJson(message, LeaveCommand.class);
                    broadcastToGame(leaveMsg.getGameID(),
                            gson.toJson(new NotificationMessage(auth.getUsername() + " has left the game")));
                    removeSession(session);
                    break;
                case RESIGN:
                    break;
                default:
                    throw new Exception("Invalid Command");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            session.getRemote().sendString(gson.toJson(new ErrorMessage(e.getMessage())));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        // Log or handle the error as needed.
        System.err.println("WebSocket Error: " + error.getMessage());
        error.printStackTrace();
    }

    AuthData checkAuth(String authToken) throws Exception {
        try {
            return authDAO.getAuth(authToken);
        } catch (Exception e) {
            throw new Exception("Unauthorized");
        }
    }

    private void removeSession(Session session) {
        // Retrieve the game id associated with this session.
        Integer gameID = sessionToGameMap.remove(session);
        if (gameID != null) {
            Set<Session> sessions = gameSessions.get(gameID);
            if (sessions != null) {
                sessions.remove(session);
                // Optionally remove the game entry if no one remains.
                if (sessions.isEmpty()) {
                    gameSessions.remove(gameID);
                }
            }
        }
    }

    public void broadcastToGame(Integer gameID, String message) {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                try {
                    s.getRemote().sendString(message);
                } catch (Exception e) {
                    System.err.println("Error sending to session: " + e.getMessage());
                }
            }
        }
    }

    public void broadcastToOtherInGame(Integer gameID, Session currentSession, String message) {
        Set<Session> sessions = gameSessions.get(gameID);
        if (sessions != null) {
            for (Session s : sessions) {
                try {
                    if (!currentSession.equals(s)) {
                        s.getRemote().sendString(message);
                    }
                } catch (Exception e) {
                    System.err.println("Error sending to session: " + e.getMessage());
                }
            }
        }
    }

    private String friendlyPosition(ChessPosition position) {
        int col = 'a' + position.getColumn() - 1;
        return (char) col + String.valueOf(position.getRow());
    }
}
