package dataaccess;

import chess.ChessGame;
import model.GameData;
import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    // Static list to store GameData in memory
    private static List<GameData> gameDataList = new ArrayList<>();

    // Clears all games from the database
    // returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            gameDataList.clear();
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    public GameData createGame(String gameName) {
        GameData game = new GameData(
                gameDataList.size() + 1,
                null,
                null, gameName,
                new ChessGame());
        gameDataList.add(game);
        return game;
    }

    public GameData getGame(int gameId) {
        for (GameData game : gameDataList) {
            if (game.getGameID() == gameId) {
                return game;
            }
        }
        return null;
    }

    public List<GameData> getAllGames() {
        return new ArrayList<>(gameDataList);
    }

    public void updateGame(GameData game) {
        for (GameData existingGame : gameDataList) {
            if (existingGame.getGameID() == game.getGameID()) {
                existingGame.setGameName(game.getGameName());
                existingGame.setWhiteUsername(game.getWhiteUsername());
                existingGame.setBlackUsername(game.getBlackUsername());
            }
        }
    }
}
