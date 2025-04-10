package websocket.messages;

public class NotificationMessage extends ServerMessage {
    private final String notification;

    NotificationMessage(String notification) {
        super(ServerMessageType.NOTIFICATION);
        this.notification = notification;
    }

    public String getNotification() {
        return notification;
    }
}
