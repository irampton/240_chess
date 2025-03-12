package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

public class UserDAOTests {
    private UserDAO userDAO;

    @BeforeEach
    public void setUp() {
        userDAO = new UserDAO();
        userDAO.clear();
    }

    // Positive Test Case - clear()
    @Test
    @Order(1)
    @DisplayName("Clear empties table and returns without errors")
    public void testClearAllSuccess() {
        String username = "Test";

        try {
            userDAO.createUser(new UserData(
                    username,
                    "foo",
                    "foo"
            ));
        } catch (Exception e) {
            e.printStackTrace();
        }

        userDAO.clear();

        Assertions.assertNull(userDAO.getUser(username));
    }

    // Positive test case - createUser()
    @Test
    @Order(2)
    @DisplayName("Add users returns without errors")
    public void addUsersAllSuccess() {
        String username = "Test";

        UserData user = null;
        try {
            user = userDAO.createUser(new UserData(
                    username,
                    "foo",
                    "foo"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail();
        }

        Assertions.assertEquals(username, user.getUsername());
    }

    // Negative test case - createUser()
    @Test
    @Order(3)
    @DisplayName("Add users fails with duplicate username")
    public void addUsersDuplicateFails() {
        String username = "Test";

        try {
            userDAO.createUser(new UserData(
                    username,
                    "foo",
                    "foo"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("First user creation failed");
        }

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.createUser(new UserData(
                    username,
                    "bar",
                    "bar"
            ));
        });
    }

    // Positive test case - createUser()
    @Test
    @Order(4)
    @DisplayName("Get user returns user")
    public void getUserSuccess() {
        String username = "Test";

        UserData createdUser = null;
        try {
            createdUser = userDAO.createUser(new UserData(
                    username,
                    "foo",
                    "foo"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            Assertions.fail("User creation failed");
        }

        UserData retrievedUser = userDAO.getUser(username);

        Assertions.assertNotNull(retrievedUser);
        Assertions.assertEquals(createdUser.getUsername(), retrievedUser.getUsername());
        Assertions.assertEquals(createdUser.getEmail(), retrievedUser.getEmail());
    }

    // Negative test case - createUser()
    @Test
    @Order(5)
    @DisplayName("Get user fails with non-existent user")
    public void getUserFails() {
        String nonExistentUsername = "NonExistentUser";

        UserData user = userDAO.getUser(nonExistentUsername);

        Assertions.assertNull(user);
    }
}
