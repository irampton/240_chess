package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class GameDAOTests {
    private GameDAO gameDAO;

    @BeforeEach
    public void setUp() {
        gameDAO = new GameDAO();
        gameDAO.clear();
    }

    // Positive Test Case - clear()
    @Test
    @Order(1)
    @DisplayName("Clear empties table and returns without errors")
    public void testClearAllSuccess() {
        String gameName = "Test";

        GameData data = null;
        try {
            data = gameDAO.createGame(gameName);
        } catch (Exception e) {
            e.printStackTrace();
        }

        gameDAO.clear();

        try {
            Assertions.assertNotNull(data);
            Assertions.assertNull(gameDAO.getGame(data.getGameID()));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("DB error");
        }
    }

    // Positive test case - createGame()
    @Test
    @Order(2)
    @DisplayName("createGame creates a game")
    public void createGameSuccess() {
        String gameName = "TestGame";

        GameData game = null;
        try {
            game = gameDAO.createGame(gameName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to create game");
        }

        Assertions.assertNotNull(game);
        Assertions.assertEquals(gameName, game.getGameName());
        Assertions.assertTrue(game.getGameID() > 0);
    }

    // Negative test case - createGame()
    @Test
    @Order(3)
    @DisplayName("createGame needs a username")
    public void createGameFailure() {
        Assertions.assertNull(gameDAO.createGame(null));
    }

    // Positive test case - getGame()
    @Test
    @Order(4)
    @DisplayName("getGame returns valid GameData")
    public void getGameSuccess() {
        String gameName = "TestGame";

        GameData game = null;
        try {
            game = gameDAO.createGame(gameName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Game creation failed");
        }

        GameData retrievedGame = null;
        try {
            retrievedGame = gameDAO.getGame(game.getGameID());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to get game");
        }

        Assertions.assertNotNull(retrievedGame);
        Assertions.assertEquals(game.getGameID(), retrievedGame.getGameID());
        Assertions.assertEquals(game.getGameName(), retrievedGame.getGameName());
    }

    // Negative test case - getAuth()
    @Test
    @Order(5)
    @DisplayName("getGame fails with bad id")
    public void getGameFails() {
        int invalidGameID = 99999;

        GameData game = null;
        try {
            game = gameDAO.getGame(invalidGameID);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNull(game);
    }

    // Positive test case - getAllGames()
    @Test
    @Order(6)
    @DisplayName("getAllGames deletes auth")
    public void getAllGamesSuccess() {
        String gameName1 = "Game1";
        String gameName2 = "Game2";

        try {
            gameDAO.createGame(gameName1);
            gameDAO.createGame(gameName2);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Game creation failed");
        }

        List<GameData> games = null;
        try {
            games = gameDAO.getAllGames();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to get all games");
        }

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.size() > 1);
    }

    // Negative test case - getAllGames()
    @Test
    @Order(7)
    @DisplayName("getAllGames negative test case")
    public void getAllGamesFailure() {
        List<GameData> games = null;
        try {
            games = gameDAO.getAllGames();
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to get all games");
        }

        Assertions.assertNotNull(games);
        Assertions.assertTrue(games.isEmpty());
    }

    // Positive test case - updateGame()
    @Test
    @Order(8)
    @DisplayName("updateGame updates players")
    public void updateGameSuccess() {
        String gameName = "TestGame";
        String whiteUsername = "WhitePlayer";
        String blackUsername = "BlackPlayer";

        GameData game = null;
        try {
            game = gameDAO.createGame(gameName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Game creation failed");
        }

        game.setWhiteUsername(whiteUsername);
        game.setBlackUsername(blackUsername);

        try {
            gameDAO.updateGame(game);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to update game");
        }

        GameData updatedGame = null;
        try {
            updatedGame = gameDAO.getGame(game.getGameID());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to retrieve updated game");
        }

        Assertions.assertEquals(whiteUsername, updatedGame.getWhiteUsername());
        Assertions.assertEquals(blackUsername, updatedGame.getBlackUsername());
    }

    // Positive test case - updateGame() - gameData
    @Test
    @Order(9)
    @DisplayName("updateGame updates gameData")
    public void updateGameGameDataSuccess() {
        String gameName = "TestGame";

        GameData game = null;
        try {
            game = gameDAO.createGame(gameName);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Game creation failed");
        }

        ChessGame chessGame = new ChessGame();
        game.setGame(chessGame);

        try {
            gameDAO.updateGame(game);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to update game data");
        }

        // Verify the update
        Collection<ChessMove> moves = chessGame.validMoves(new ChessPosition(1,2));
        ChessMove selectedMove = moves.stream().findFirst().orElse(null);
        try {
            chessGame.makeMove(selectedMove);
        } catch (Exception e) {
            e.printStackTrace();
        }
        game.setGame(chessGame);

        try {
            gameDAO.updateGame(game);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to update game");
        }

        GameData updatedGame = null;
        try {
            updatedGame = gameDAO.getGame(game.getGameID());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Failed to retrieve updated game");
        }

        Assertions.assertNotNull(updatedGame);
        Assertions.assertEquals(chessGame, updatedGame.getGame()); // Ensures the game data was updated
    }

    // Negative test case - updateGame()
    @Test
    @Order(10)
    @DisplayName("updateGame negative test case")
    public void updateGameFailure() {
        GameData game = new GameData(99999, null, null, "NonExistentGame", null); // Non-existent game ID

        Assertions.assertThrows(DataAccessException.class, () -> {
            gameDAO.updateGame(game);
        });
    }

}
