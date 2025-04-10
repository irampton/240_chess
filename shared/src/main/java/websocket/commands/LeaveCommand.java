package websocket.commands;

public class LeaveCommand extends UserGameCommand {
    public LeaveCommand(String authToken, Integer gameID, CommandType type) {
        super(UserGameCommand.CommandType.CONNECT, authToken, gameID);
    }

}
