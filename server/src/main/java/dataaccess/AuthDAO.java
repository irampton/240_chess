package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthDAO {
    // Static list to store UserData in memory
    private static List<AuthData> authDataList = new ArrayList<>();

    // Clears all games from the database
    // returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            authDataList.clear();
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    // Create AuthData and add to the list
    public AuthData createAuth(String username) {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);
        authDataList.add(authData);

        return authData;
    }

    // Get AuthData based on token
    public AuthData getAuth(String token) {
        for (AuthData auth : authDataList) {
            if (auth.getAuthToken().equals(token)) {
                return auth;
            }
        }
        return null;
    }

    // Delete AuthData from the list
    public void deleteAuth(AuthData authData) {
        authDataList.remove(authData);
    }
}
