package websockets;

import chess.ChessGame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.ChessGameDeserializer;

import javax.websocket.*;
import java.net.URI;
import java.util.Scanner;

import com.google.gson.Gson;
import model.CreateGameRequest;
import websocket.commands.UserGameCommand;
import websocket.messages.*;

import static ui.EscapeSequences.*;


public class WSClient extends Endpoint {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

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
                        // TODO draw chess board
                        System.out.println("Loading game...");
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
                System.out.print("[IN_GAME] >>> ");
            }
        });
    }

    public void send(String msg) throws Exception {
        this.session.getBasicRemote().sendText(msg);
    }

    public void onOpen(Session session, EndpointConfig endpointConfig) {
    }
}
