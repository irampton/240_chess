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

    private final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private final Map<Session, Integer> sessionToGameMap = new ConcurrentHashMap<>();

    public WSServer() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        //System.out.println(message);
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        AuthData auth;
        GameData game;
        ChessGame.TeamColor playerColor = null;
        try {
            switch (command.getCommandType()) {
                case CONNECT:
                    auth = checkAuth(command.getAuthToken());
                    game = getGameData(command);

                    if (auth.getUsername().equalsIgnoreCase(game.getWhiteUsername())) {
                        playerColor = ChessGame.TeamColor.WHITE;
                    } else if (auth.getUsername().equalsIgnoreCase(game.getBlackUsername())) {
                        playerColor = ChessGame.TeamColor.BLACK;
                    }

                    String joinMessage = auth.getUsername() + " joined the game as ";
                    if (playerColor == ChessGame.TeamColor.BLACK) {
                        joinMessage += "black";
                    } else if (playerColor == ChessGame.TeamColor.WHITE) {
                        joinMessage += "white";
                    } else {
                        joinMessage += "an observer";
                    }

                    gameSessions.computeIfAbsent(
                            command.getGameID(), k -> new CopyOnWriteArraySet<>())
                            .add(session);
                    sessionToGameMap.put(session, command.getGameID());

                    session.getRemote().sendString(gson.toJson(new LoadGameMessage(game.getGame())));
                    broadcastToOtherInGame(command.getGameID(), session,
                            gson.toJson(new NotificationMessage(joinMessage)));
                    break;
                case MAKE_MOVE:
                    auth = checkAuth(command.getAuthToken());
                    game = getGameData(command);
                    if (game.getGameOver()) {
                        throw new Exception("Game over");
                    }
                    playerColor = getTeamColor(auth, game);
                    if (playerColor != game.getGame().getTeamTurn()) {
                        throw new Exception("It is not your turn.");
                    }

                    makeMove(session, message, game, playerColor);
                    break;
                case LEAVE:
                    auth = checkAuth(command.getAuthToken());
                    LeaveCommand leaveMsg = gson.fromJson(message, LeaveCommand.class);
                    game = getGameData(command);

                    if (auth.getUsername().equalsIgnoreCase(game.getWhiteUsername())) {
                        playerColor = ChessGame.TeamColor.WHITE;
                    } else if (auth.getUsername().equalsIgnoreCase(game.getBlackUsername())) {
                        playerColor = ChessGame.TeamColor.BLACK;
                    }

                    if (playerColor == ChessGame.TeamColor.WHITE) {
                        game.setWhiteUsername(null);
                    } else if (playerColor == ChessGame.TeamColor.BLACK) {
                        game.setBlackUsername(null);
                    }

                    saveGameData(game);
                    broadcastToOtherInGame(leaveMsg.getGameID(), session,
                            gson.toJson(new NotificationMessage(auth.getUsername() + " has left the game")));
                    removeSession(session);
                    break;
                case RESIGN:
                    auth = checkAuth(command.getAuthToken());
                    game = getGameData(command);
                    playerColor = getTeamColor(auth, game);
                    if (game.getGameOver()) {
                        throw new Exception("Game over");
                    }
                    game.setGameOver(true);
                    saveGameData(game);
                    broadcastToGame(command.getGameID(),
                            gson.toJson(new NotificationMessage(auth.getUsername()
                                    + "(" + (playerColor == ChessGame.TeamColor.WHITE ? "white" : "black")
                                    + ") has resigned")));
                    break;
                default:
                    throw new Exception("Invalid Command");
            }
        } catch (Exception e) {
            //e.printStackTrace();
            session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid Command")));
        }
    }

    private void makeMove(Session session, String message, GameData game, ChessGame.TeamColor playerColor)
            throws Exception {
        MakeMoveCommand moveCommand = gson.fromJson(message, MakeMoveCommand.class);
        ChessMove playerMove = moveCommand.getMove();

        game.getGame().makeMove(playerMove);
        boolean checkmate = game.getGame().isInCheckmate(game.getGame().getTeamTurn());
        boolean check = game.getGame().isInCheck(game.getGame().getTeamTurn());
        boolean stalemate = game.getGame().isInStalemate(game.getGame().getTeamTurn());
        if (checkmate || stalemate) {
            game.setGameOver(true);
        }
        saveGameData(game);

        broadcastToGame(moveCommand.getGameID(), gson.toJson(new LoadGameMessage(game.getGame())));

        String moveMessage = playerColor == ChessGame.TeamColor.WHITE ? "White" : "Black";
        moveMessage += " moved from " + friendlyPosition(playerMove.getStartPosition()) + " to "
                + friendlyPosition(playerMove.getEndPosition());
        String checkMessage = "";
        if (checkmate) {
            checkMessage += (game.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE ? "White" : "Black")
                    + " is in checkmate - Game Over\n"
                    + (playerColor == ChessGame.TeamColor.WHITE ? "White" : "Black") + " wins!";
        } else if (stalemate) {
            checkMessage += "Stalemate - Game Over";
        } else if (check) {
            checkMessage += (game.getGame().getTeamTurn() == ChessGame.TeamColor.WHITE ? "White" : "Black")
                    + " is in check";
        }
        //moveMessage += "\n" + checkMessage;

        broadcastToOtherInGame(moveCommand.getGameID(), session,
                gson.toJson(new NotificationMessage(moveMessage)));
        if (checkmate || check || stalemate) {
            //session.getRemote().sendString(gson.toJson(new NotificationMessage(checkMessage)));
            broadcastToGame(game.getGameID(), gson.toJson(new NotificationMessage(checkMessage)));
        }
    }

    private void saveGameData(GameData game) throws Exception {
        try {
            gameDAO.updateGame(game);
        } catch (Exception e) {
            throw new Exception("Error saving game data");
        }
    }

    private GameData getGameData(UserGameCommand command) throws Exception {
        GameData game;
        try {
            game = gameDAO.getGame(command.getGameID());
        } catch (Exception e) {
            throw new Exception("Invalid game");
        }
        return game;
    }

    private static ChessGame.TeamColor getTeamColor(AuthData auth, GameData game) throws Exception {
        ChessGame.TeamColor playerColor;
        // Make sure user is authorized to make a move
        if (auth.getUsername().equalsIgnoreCase(game.getWhiteUsername())) {
            playerColor = ChessGame.TeamColor.WHITE;
        } else if (auth.getUsername().equalsIgnoreCase(game.getBlackUsername())) {
            playerColor = ChessGame.TeamColor.BLACK;
        } else {
            throw new Exception("You cannot make a move. You are not a player in this game.");
        }

        return playerColor;
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
