package server;

import chess.ChessGame;
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

    private final Gson gson = new GsonBuilder().registerTypeAdapter(ChessGame.class, new ChessGameDeserializer()).create();

    private static final Map<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();
    private static final Map<Session, Integer> sessionToGameMap = new ConcurrentHashMap<>();

    public WSServer() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        AuthData auth;
        try {
            switch (command.getCommandType()) {
                case CONNECT:
                    auth = checkAuth(command.getAuthToken());
                    ConnectCommand connectCommand = gson.fromJson(message, ConnectCommand.class);
                    GameData game;
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
                    System.out.print("User: " + auth.getUsername() + " connected to game " + game.getGameName() + " as ");
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

                    gameSessions.computeIfAbsent(connectCommand.getGameID(), k -> new CopyOnWriteArraySet<>()).add(session);
                    sessionToGameMap.put(session, connectCommand.getGameID());

                    session.getRemote().sendString(gson.toJson(new LoadGameMessage(game.getGame())));

                    broadcastToGame(connectCommand.getGameID(), gson.toJson(new NotificationMessage(joinMessage)));
                    break;
                case MAKE_MOVE:
                    break;
                case LEAVE:
                    auth = checkAuth(command.getAuthToken());
                    LeaveCommand leaveMsg = gson.fromJson(message, LeaveCommand.class);
                    broadcastToGame(leaveMsg.getGameID(), gson.toJson(new NotificationMessage(auth.getUsername() + " has left the game")));
                    removeSession(session);
                    break;
                case RESIGN:
                    break;
                default:
                    throw new Exception("Invalid Command");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
}
