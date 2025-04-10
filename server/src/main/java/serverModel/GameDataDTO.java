package serverModel;

import model.GameData;

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

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    public void setGameID(int gameID) {
        this.gameID = gameID;
    }

    public void setBlackUsername(String blackUsername) {
        this.blackUsername = blackUsername;
    }

    public void setWhiteUsername(String whiteUsername) {
        this.whiteUsername = whiteUsername;
    }
}
