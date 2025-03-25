package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import ui.ServerFacade;

import java.util.List;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;
    private static int port;

    @BeforeAll
    public static void init() {
        server = new Server();
        port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @BeforeEach
    public void setUp() {
        serverFacade = new ServerFacade(null, port);
        try {
            serverFacade.clearDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    // clearDatabase
    @Test
    public void clearTest() {
        try {
            serverFacade.clearDatabase();
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Positive Test Case: register
    @Test
    public void registerTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Negative Test Case: register
    @Test
    public void registerFailTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            serverFacade.register(registerRequest);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Username already taken", e.getMessage());
        }
    }

    // Positive Test Case: login
    @Test
    public void loginTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword());
            serverFacade.login(loginRequest);
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Negative Test Case: login
    @Test
    public void loginFailTest() {
        try {
            LoginRequest loginRequest = new LoginRequest("newUsername", "password");
            serverFacade.login(loginRequest);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Unauthorized", e.getMessage());
        }
    }

    @Test
    // Positive Test Case: logout
    public void logoutTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword());
            serverFacade.login(loginRequest);
            serverFacade.logout();
            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    @Test
    // Negative Test Case: logout
    public void logoutFailTest() {
        try {
            serverFacade.logout();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Not logged in", e.getMessage());
        }
    }

    // Positive Test Case: createGame
    @Test
    public void createGameTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword());
            serverFacade.login(loginRequest);

            CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
            serverFacade.createGame(createGameRequest);

            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Negative Test Case: createGame
    @Test
    public void createGameFailTest() {
        try {
            // Try to create a game without being logged in (Should fail)
            CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
            serverFacade.createGame(createGameRequest);
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Not logged in", e.getMessage());
        }
    }

    // Positive Test Case: listGames
    @Test
    public void listGamesTest() {
        try {
            UserData registerRequest = new UserData("newUsername", "password", "email@example.com");
            serverFacade.register(registerRequest);
            LoginRequest loginRequest = new LoginRequest(registerRequest.getUsername(), registerRequest.getPassword());
            serverFacade.login(loginRequest);

            CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
            serverFacade.createGame(createGameRequest);

            List<GameData> games = serverFacade.listGames();
            Assertions.assertFalse(games.isEmpty());
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Negative Test Case: listGames
    @Test
    public void listGamesFailTest() {
        try {
            serverFacade.listGames();
            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Not logged in", e.getMessage());
        }
    }

    // Positive Test Case: joinGame
    @Test
    public void joinGameTest() {
        try {
            UserData registerRequest1 = new UserData("player1", "password1", "email1@example.com");
            serverFacade.register(registerRequest1);

            CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
            serverFacade.createGame(createGameRequest);

            List<GameData> games = serverFacade.listGames();
            int gameID = games.getFirst().getGameID();

            GameJoinRequest joinRequest = new GameJoinRequest("WHITE", gameID);
            serverFacade.joinGame(joinRequest);

            Assertions.assertTrue(true);
        } catch (Exception e) {
            Assertions.fail();
        }
    }

    // Negative Test Case: joinGame
    @Test
    public void joinGameFailTest() {
        try {
            UserData registerRequest1 = new UserData("player1", "password1", "email1@example.com");
            serverFacade.register(registerRequest1);

            CreateGameRequest createGameRequest = new CreateGameRequest("New Game");
            serverFacade.createGame(createGameRequest);

            List<GameData> games = serverFacade.listGames();
            int gameID = games.getFirst().getGameID();

            GameJoinRequest joinRequest = new GameJoinRequest("WHITE", gameID);
            serverFacade.joinGame(joinRequest);
            serverFacade.joinGame(joinRequest);

            Assertions.fail();
        } catch (Exception e) {
            Assertions.assertEquals("Color already taken", e.getMessage());
        }
    }
}
