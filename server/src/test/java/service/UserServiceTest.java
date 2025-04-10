package service;

import model.*;
import org.junit.jupiter.api.*;
import serverModel.ErrorResponse;

public class UserServiceTest {
    private UserService userService;
    private DatabaseService databaseService;

    @BeforeEach
    public void setUp() {
        databaseService = new DatabaseService();
        databaseService.clearAll();
        userService = new UserService();
    }

    // Positive Test Case: Register a new user
    @Test
    @Order(1)
    @DisplayName("Register a new user")
    public void registerUser() {
        UserData registerRequest = new UserData("newUsername", "password", "email@example.com");

        AuthData response = (AuthData) userService.register(registerRequest);

        Assertions.assertNotNull(response);
        Assertions.assertNotNull(response.getAuthToken());
    }

    // Negative Test Case: Register with an existing username
    @Test
    @Order(2)
    @DisplayName("Register with an existing username")
    public void registerUserWithExistingUsername() {
        UserData registerRequest = new UserData("existingUsername", "password", "email@example.com");
        userService.register(registerRequest);

        ErrorResponse response = (ErrorResponse) userService.register(registerRequest);

        Assertions.assertEquals(403, response.getStatusCode());
        Assertions.assertEquals("Username is already taken.", response.getError());
    }

    // Positive Test Case: Successful Login
    @Test
    @Order(3)
    @DisplayName("Login with correct credentials")
    public void loginUser() {
        UserData registerRequest = new UserData("validUser", "password", "email@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("validUser", "password");

        Object response = userService.login(loginRequest);

        if (response instanceof ErrorResponse) {
           Assertions.assertEquals(200, ((ErrorResponse) response).getStatusCode());
        } else {
            AuthData auth = (AuthData) response;
            Assertions.assertNotNull(auth);
            Assertions.assertNotNull(auth.getAuthToken());
        }


    }

    // Negative Test Case: Login with incorrect password
    @Test
    @Order(4)
    @DisplayName("Login with incorrect password")
    public void loginUserWithIncorrectPassword() {
        UserData registerRequest = new UserData("validUser", "password", "email@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("validUser", "wrongPassword");

        ErrorResponse response = (ErrorResponse) userService.login(loginRequest);

        Assertions.assertEquals(401, response.getStatusCode());
        Assertions.assertEquals("Error: unauthorized", response.getError());
    }

    // Positive Test Case: Successful Logout
    @Test
    @Order(5)
    @DisplayName("Logout with valid auth token")
    public void logoutUser() {
        // Register and login to get a valid auth token
        UserData registerRequest = new UserData("validUser", "password", "email@example.com");
        userService.register(registerRequest);

        LoginRequest loginRequest = new LoginRequest("validUser", "password");
        AuthData authData = (AuthData) userService.login(loginRequest);

        Object response = userService.logout(authData.getAuthToken());

        Assertions.assertNull(response);
    }

    // Negative Test Case: Logout with invalid auth token
    @Test
    @Order(6)
    @DisplayName("Logout with invalid auth token")
    public void logoutUserWithInvalidAuthToken() {
        ErrorResponse response = (ErrorResponse) userService.logout(null);
        
        Assertions.assertEquals(401, response.getStatusCode());
        Assertions.assertEquals("Error: unauthorized", response.getError());
    }
}
