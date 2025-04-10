package service;

import model.*;
import org.junit.jupiter.api.*;
import servermodel.CreateGameResponse;
import servermodel.ErrorResponse;
import servermodel.GameDataDTO;

import java.util.List;
import java.util.Map;

public class GameServiceTest {
    private GameService gameService;
    private UserService userService;
    private DatabaseService databaseService;

    private String authToken;

    @BeforeEach
    public void setUp() {
        databaseService = new DatabaseService();
        databaseService.clearAll();
        gameService = new GameService();
        userService = new UserService();
        authToken = ((AuthData) userService.register(
                new UserData(
                        "username",
                        "password",
                        "email")
        )).getAuthToken();
    }

    // Positive Test Case: All DAO clear methods succeed
    @Test
    @Order(1)
    @DisplayName("Create creates a new game and returns the ID")
    public void createGame() {
        int gameID = ((CreateGameResponse) gameService
                .create(authToken, "gameName")
        ).getGameID();


        // Assert that the gameID is a number (positive integer)
        Assertions.assertTrue(gameID > 0);
    }

    // Negative Test Case: Bad auth returns a 403
    @Test
    @Order(2)
    @DisplayName("Create Game Bad Auth")
    public void createGameBadAuth() {
        ErrorResponse response = (ErrorResponse) gameService.create(null, "gameName");

        Assertions.assertEquals(401, response.getStatusCode());
        Assertions.assertEquals("Error: unauthorized", response.getError());
    }

    // Positive Test Case: Join Game with White
    @Test
    @Order(5)
    @DisplayName("Join Game as White player")
    public void joinGameWhite() {
        int gameID = ((CreateGameResponse) gameService
                .create(authToken, "gameName")
        ).getGameID();

        GameData response = (GameData) gameService.join(authToken, new GameJoinRequest("WHITE", gameID));

        Assertions.assertEquals("username", response.getWhiteUsername());
    }

    // Negative Test Case: Join Game White Username Taken
    @Test
    @Order(6)
    @DisplayName("Join Game as White when username is taken")
    public void joinGameWhiteUsernameTaken() {
        int gameID = ((CreateGameResponse) gameService
                .create(authToken, "gameName")
        ).getGameID();

        gameService.join(authToken, new GameJoinRequest("WHITE", gameID));
        ErrorResponse response = (ErrorResponse) gameService.join(authToken, new GameJoinRequest("WHITE", gameID));

        Assertions.assertEquals(403, response.getStatusCode());
        Assertions.assertEquals("Error: white username taken", response.getError());
    }

    // Positive Test Case: List all games with a valid auth token
    @Test
    @Order(3)
    @DisplayName("List Games with Valid Auth Token")
    public void listGamesWithValidAuth() {
        // Create a game to ensure there is at least one game to list
        gameService.create(authToken, "gameName");

        // Call the list method
        Map<String, List<GameDataDTO>> response = (Map<String, List<GameDataDTO>>) gameService.list(authToken);

        // Ensure that the response contains a list of games under the "games" key
        Assertions.assertNotNull(response);
        Assertions.assertTrue(response.containsKey("games"));
        Assertions.assertFalse(response.get("games").isEmpty());
    }

    // Negative Test Case: List all games with an invalid auth token
    @Test
    @Order(4)
    @DisplayName("List Games with Invalid Auth Token")
    public void listGamesWithInvalidAuth() {
        // Call the list method with an invalid auth token (e.g., null)
        ErrorResponse response = (ErrorResponse) gameService.list(null);

        // Ensure that the response is an error with status 401
        Assertions.assertEquals(401, response.getStatusCode());
        Assertions.assertEquals("Error: unauthorized", response.getError());
    }

}