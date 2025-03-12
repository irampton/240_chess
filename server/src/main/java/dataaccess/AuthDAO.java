package dataaccess;

import model.AuthData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthDAO {
    // Clears all games from the database
    // returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement("DELETE FROM auth_data")) {
                    preparedStatement.executeUpdate();
                }
            }
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    // Create AuthData and add to the list
    public AuthData createAuth(String username) throws DataAccessException {
        AuthData authData = new AuthData(UUID.randomUUID().toString(), username);

        try (var conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO auth_data (authToken, username) VALUES (?, ?)";
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authData.getAuthToken());
                preparedStatement.setString(2, authData.getUsername());

                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating user.");
        }

        return authData;
    }

    // Get AuthData based on token
    public AuthData getAuth(String token) {
        String query = "SELECT authToken, username FROM auth_data WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, token);
                try (var rs = preparedStatement.executeQuery()) {

                    if (rs.next()) {
                        return new AuthData(
                                rs.getString("authToken"),
                                rs.getString("username")
                        );
                    }
                }
            }
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null;
    }

    // Delete AuthData from the list
    public void deleteAuth(AuthData authData) {
        String query = "DELETE FROM auth_data WHERE authToken = ?";
        try (var conn = DatabaseManager.getConnection()) {
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, authData.getAuthToken());
                preparedStatement.executeUpdate();
            }
        } catch (SQLException | DataAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
