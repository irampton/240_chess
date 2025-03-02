package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class DatabaseServiceTest {
    private DatabaseService databaseService;

    private AuthDAO authDAO;
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        databaseService = new DatabaseService();

        authDAO = new AuthDAO();
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
    }

    // Positive Test Case: All DAO clear methods succeed
    @Test
    @Order(1)
    @DisplayName("Clear empties database and returns without errors")
    public void testClearAllSuccess() {
        String authToken = "";
        int gameId = 0;

        try {
            authToken = authDAO.createAuth("username").getAuthToken();
            gameId = gameDAO.createGame("game1").getGameID();
            userDAO.createUser(new UserData("username", "password", "email"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        int result = databaseService.clearAll();

        Assertions.assertEquals(1, result);
        Assertions.assertNull(authDAO.getAuth(authToken));
        Assertions.assertNull(gameDAO.getGame(gameId));
        Assertions.assertNull(userDAO.getUser("username"));

    }
}
