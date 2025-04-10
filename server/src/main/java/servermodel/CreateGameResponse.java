package servermodel;

public class CreateGameResponse {
    private int gameID;

    public CreateGameResponse(int gameID) {
        this.gameID = gameID;
    }

    public int getGameID() {
        return gameID;
    }
}