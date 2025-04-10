package server;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.ChessGameDeserializer;
import org.eclipse.jetty.websocket.api.annotations.*;
import org.eclipse.jetty.websocket.api.*;
import spark.Spark;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;

@WebSocket
public class WSServer {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

    public WSServer() {
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws Exception {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        switch (command.getCommandType()){
            case CONNECT:
                System.out.println("Client connected");
                break;
            case MAKE_MOVE:
                break;
            case LEAVE:
                break;
            case RESIGN:
                break;
            default:
                session.getRemote().sendString(gson.toJson(new ErrorMessage("Invalid Command")));
        }
    }

    @OnWebSocketError
    public void onError(Session session, Throwable error) {
        // Log or handle the error as needed.
        System.err.println("WebSocket Error: " + error.getMessage());
        error.printStackTrace();
    }
}
