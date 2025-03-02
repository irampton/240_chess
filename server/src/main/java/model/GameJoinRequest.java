package model;

public class GameJoinRequest {
    private String playerColor;
    private int gameID;

    // Constructor to initialize playerColor and gameID
    public GameJoinRequest(String playerColor, int gameID) {
        this.playerColor = playerColor;
        this.gameID = gameID;
    }

    // Getter for playerColor
    public String getPlayerColor() {
        return playerColor;
    }

    // Setter for playerColor
    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }

    // Getter for gameID
    public int getGameID() {
        return gameID;
    }

    // Setter for gameID
    public void setGameID(int gameID) {
        this.gameID = gameID;
    }
}
