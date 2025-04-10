package websocket.messages;

public class ErrorMessage extends ServerMessage {
    private final String error;

    ErrorMessage(String error) {
        super(ServerMessageType.ERROR);
        this.error = error;
    }

    String getError() {
        return error;
    }
}
