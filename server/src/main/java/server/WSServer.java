package server;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.ChessGameDeserializer;
import model.GameData;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;

@WebSocket
public class WSServer {
    private AuthDAO authDAO = new AuthDAO();
    private UserDAO userDAO = new UserDAO();
    private GameDAO gameDAO = new GameDAO();

    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

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
                    } catch (Exception e){
                        throw new Exception("Invalid game");
                    }
                    System.out.print("User: " + auth.getUsername() + " connected to game " + game.getGameName() + " as ");
                    switch (connectCommand.getType()) {
                        case BLACK:
                            System.out.print("black");
                            break;
                        case WHITE:
                            System.out.print("white");
                            break;
                        case OBSERVER:
                            System.out.print("an observer");
                            break;
                    }
                    System.out.print("\n");
                    session.getRemote().sendString(gson.toJson(new LoadGameMessage(game.getGame())));
                    break;
                case MAKE_MOVE:
                    break;
                case LEAVE:
                    break;
                case RESIGN:
                    break;
                default:
                    throw new Exception("Invalid Command");
            }
        } catch (Exception e) {
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
}
