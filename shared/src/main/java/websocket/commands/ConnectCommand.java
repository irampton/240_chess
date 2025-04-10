package websocket.commands;

public class ConnectCommand extends UserGameCommand {
    public enum CommandType {
        WHITE,
        BLACK,
        OBSERVER
    }

    private final CommandType type;

    public ConnectCommand(String authToken, Integer gameID, CommandType type) {
        super(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        this.type = type;
    }

    public CommandType getType() {
        return type;
    }

}
