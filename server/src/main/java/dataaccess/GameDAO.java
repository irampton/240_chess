package dataaccess;

import chess.ChessGame;
import com.google.gson.GsonBuilder;
import model.ChessGameDeserializer;
import model.GameData;
import model.UserData;

import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class GameDAO {
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

    // Clears all games from the database
    // returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement("DELETE FROM game_data")) {
                    preparedStatement.executeUpdate();
                }
            }
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    public GameData createGame(String gameName) {
        GameData game = new GameData(
                0,          // Temp id
                null,
                null, gameName,
                new ChessGame());

        try (var conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO game_data (gameName, whiteUsername, blackUsername, game) VALUES (?, ?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
                preparedStatement.setString(1, gameName);
                preparedStatement.setString(2, null); // White username (initially null)
                preparedStatement.setString(3, null); // Black username (initially null)
                String gameJson = gson.toJson(game.getGame());
                preparedStatement.setString(4, gameJson);

                int affectedRows = preparedStatement.executeUpdate();
                if (affectedRows > 0) {
                    try (var generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            // Set the auto-generated game ID
                            game.setGameID(generatedKeys.getInt(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return game;
    }

    public GameData getGame(int gameId) throws DataAccessException {
        GameData game = null;
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM game_data WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setInt(1, gameId);
                try (var resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String gameJson = resultSet.getString("game");
                        ChessGame chessGame = gson.fromJson(gameJson, ChessGame.class);

                        game = new GameData(
                                resultSet.getInt("gameID"),
                                resultSet.getString("whiteUsername"),
                                resultSet.getString("blackUsername"),
                                resultSet.getString("gameName"),
                                chessGame
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Error getting game.");
        }
        return game;
    }

    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> games = new ArrayList<>();
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT * FROM game_data";
            try (var preparedStatement = conn.prepareStatement(query);
                 var resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    GameData game = new GameData(
                            resultSet.getInt("gameID"),
                            resultSet.getString("whiteUsername"),
                            resultSet.getString("blackUsername"),
                            resultSet.getString("gameName"),
                            null
                    );
                    games.add(game);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("Error listing games.");
        }
        return games;
    }

    public void updateGame(GameData game) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String query = "UPDATE game_data SET gameName = ?, whiteUsername = ?, blackUsername = ?, game = ? WHERE gameID = ?";
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, game.getGameName());
                preparedStatement.setString(2, game.getWhiteUsername());
                preparedStatement.setString(3, game.getBlackUsername());
                String gameJson = gson.toJson(game.getGame());
                preparedStatement.setString(4, gameJson);
                preparedStatement.setInt(5, game.getGameID());

                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("error updating game");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DataAccessException("error updating game");
        }
    }
}
