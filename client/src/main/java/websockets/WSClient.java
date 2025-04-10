package websockets;

import chess.ChessBoard;
import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.ChessGameDeserializer;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

import com.google.gson.Gson;
import model.CreateGameRequest;
import ui.DrawChessBoard;
import websocket.commands.ConnectCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import static ui.EscapeSequences.*;


public class WSClient extends Endpoint {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();
    private Boolean suppressNextOutput = false;
    private ConnectCommand.CommandType role;
    private final DrawChessBoard boardDrawer = new DrawChessBoard();


    public static void main(String[] args) throws Exception {
        var ws = new WSClient();
        Scanner scanner = new Scanner(System.in);
    }

    public Session session;

    public WSClient() throws Exception {
        URI uri = new URI("ws://localhost:8080/ws");
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            public void onMessage(String message) {
                ServerMessage msg = gson.fromJson(message, ServerMessage.class);
                System.out.println();
                switch (msg.getServerMessageType()) {
                    case LOAD_GAME:
                        LoadGameMessage gameMessage = gson.fromJson(message, LoadGameMessage.class);
                        switch (role) {
                            case WHITE:
                            case OBSERVER:
                                boardDrawer.drawBoard(gameMessage.getGame().getBoard(), ChessGame.TeamColor.WHITE);
                                break;
                            case BLACK:
                                boardDrawer.drawBoard(gameMessage.getGame().getBoard(), ChessGame.TeamColor.BLACK);
                                break;
                        }
                        break;
                    case ERROR:
                        ErrorMessage err = gson.fromJson(message, ErrorMessage.class);
                        System.out.print(SET_TEXT_COLOR_RED);
                        System.out.println(err.getError());
                        System.out.println(RESET_TEXT_COLOR);
                        break;
                    case NOTIFICATION:
                        NotificationMessage notificationMessage = gson.fromJson(message, NotificationMessage.class);
                        System.out.print(SET_TEXT_COLOR_BLACK);
                        System.out.println(notificationMessage.getNotification());
                        System.out.println(RESET_TEXT_COLOR);
                        break;
                    default:
                        System.out.print(SET_TEXT_COLOR_RED);
                        System.out.println("Unknown message from server");
                        System.out.println(RESET_TEXT_COLOR);
                }
                if (!suppressNextOutput) {
                    System.out.print("[IN_GAME] >>> ");
                } else {
                    System.out.print("[LOGGED_IN] >>> ");
                    suppressNextOutput = false;
                }
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }

    public void suppressNextOutput() {
        suppressNextOutput = true;
    }

    public void setRole(ConnectCommand.CommandType role) {
        this.role = role;
    }
}
