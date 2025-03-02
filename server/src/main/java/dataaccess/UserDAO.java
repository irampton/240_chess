package dataaccess;

import model.UserData;

import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Static list to store UserData in memory
    private static List<UserData> userDataList = new ArrayList<>();

    // Clears all users from the memory (database)
    // Returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            userDataList.clear();
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    // Adds a user and returns its UserData object
    public UserData createUser(UserData user) throws DataAccessException {
        // Check if a user with the same username already exists
        for (UserData existingUser : userDataList) {
            if (existingUser.getUsername().equals(user.getUsername())) {
                throw new DataAccessException("Username is already taken.");
            }
        }

        userDataList.add(user);

        return user;
    }

    // Gets and returns a user by username
    public UserData getUser(String username) {
        for (UserData user : userDataList) {
            if (user.getUsername().equals(username)) {
                return user;
            }
        }
        return null;
    }
}