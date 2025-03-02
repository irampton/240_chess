package dataaccess;

import model.AuthData;

import java.util.ArrayList;
import java.util.List;

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

    public void createAuth() {

    }

    public void getAuth() {

    }

    public void deleteAuth() {

    }
}
