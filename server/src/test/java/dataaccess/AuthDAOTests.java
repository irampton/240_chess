package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

public class AuthDAOTests {
    private AuthDAO authDAO;

    @BeforeEach
    public void setUp() {
        authDAO = new AuthDAO();
        authDAO.clear();
    }

    // Positive Test Case - clear()
    @Test
    @Order(1)
    @DisplayName("Clear empties table and returns without errors")
    public void testClearAllSuccess() {
        String username = "Test";

        AuthData authData = null;
        try {
            authData = authDAO.createAuth(username);
        } catch (Exception e) {
            e.printStackTrace();
        }

        authDAO.clear();

        try {
            Assertions.assertNull(authDAO.getAuth(authData.getAuthToken()));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("DB error");
        }
    }

    // Positive test case - createAuth()
    @Test
    @Order(2)
    @DisplayName("createAuth returns an auth token")
    public void createAuthSuccess() {
        String username = "TestUser";

        AuthData authData = null;
        try {
            authData = authDAO.createAuth(username);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Auth creation failed");
        }

        Assertions.assertNotNull(authData);
        Assertions.assertNotNull(authData.getAuthToken());
        Assertions.assertEquals(username, authData.getUsername());
    }

    // Negative test case - createAuth()
    @Test
    @Order(3)
    @DisplayName("getAuth negative test case")
    public void createAuthFailure() {
        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.createAuth(null);
        });
    }

    // Positive test case - getAuth()
    @Test
    @Order(4)
    @DisplayName("Get auth returns AuthData")
    public void getAuthSuccess() {
        String username = "Testname";

        AuthData authData = null;
        try {
            authData = authDAO.createAuth(username);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Auth creation failed");
        }

        AuthData retrievedAuthData = null;
        try {
            retrievedAuthData = authDAO.getAuth(authData.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Auth retrieval failed");
        }

        Assertions.assertNotNull(retrievedAuthData);
        Assertions.assertEquals(authData.getAuthToken(), retrievedAuthData.getAuthToken());
        Assertions.assertEquals(authData.getUsername(), retrievedAuthData.getUsername());
    }

    // Negative test case - getAuth()
    @Test
    @Order(5)
    @DisplayName("Get auth fails with bad token")
    public void getAuthFails() {
        String invalidToken = "invalid_token";

        AuthData authData = null;
        try {
            authData = authDAO.getAuth(invalidToken);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNull(authData);
    }

    // Positive test case - createAuth()
    @Test
    @Order(6)
    @DisplayName("deleteAuth deletes auth")
    public void deleteAuthSuccess() {
        String username = "TestUser";

        AuthData authData = null;
        try {
            authData = authDAO.createAuth(username);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Auth creation failed");
        }

        try {
            authDAO.deleteAuth(authData);
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("Auth deletion failed");
        }

        AuthData deletedAuthData = null;
        try {
            deletedAuthData = authDAO.getAuth(authData.getAuthToken());
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assertions.assertNull(deletedAuthData);
    }

    // Negative test case - createAuth()
    @Test
    @Order(7)
    @DisplayName("deleteAuth negative test case")
    public void deleteAuthFailure() {
        AuthData invalidAuthData = new AuthData(null, "NonExistentUser");

        Assertions.assertThrows(DataAccessException.class, () -> {
            authDAO.deleteAuth(invalidAuthData);
        });
    }

}
