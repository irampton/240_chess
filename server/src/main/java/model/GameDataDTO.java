package model;

public class GameDataDTO {
    private int gameID;
    private String whiteUsername;
    private String blackUsername;
    private String gameName;

    // Constructor to map from GameData
    public GameDataDTO(GameData gameData) {
        this.gameID = gameData.getGameID();
        this.whiteUsername = gameData.getWhiteUsername();
        this.blackUsername = gameData.getBlackUsername();
        this.gameName = gameData.getGameName();
    }

    // Getters and setters
    public int getGameID() {
        return gameID;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public String getWhiteUsername() {
        return whiteUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }

    public String getBlackUsername() {
        return blackUsername;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }
}
