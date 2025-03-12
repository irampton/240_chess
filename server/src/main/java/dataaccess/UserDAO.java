package dataaccess;

import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    // Static list to store UserData in memory
    private static List<UserData> userDataList = new ArrayList<>();

    // Clears all users from the memory (database)
    // Returns 1 if successful, error code if otherwise
    public int clear() {
        try {
            try (var conn = DatabaseManager.getConnection()) {
                try (var preparedStatement = conn.prepareStatement("DELETE FROM users")) {
                    preparedStatement.executeUpdate();
                }
            }
            return 1; // Success
        } catch (Exception e) {
            e.printStackTrace();
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    // Adds a user and returns its UserData object
    public UserData createUser(UserData user) throws DataAccessException {
        // Check if a user with the same username already exists
        if (getUser(user.getUsername()) != null) {
            throw new DataAccessException("Username is already taken.");
        }

        try (var conn = DatabaseManager.getConnection()) {
            String query = "INSERT INTO users (username, email, password_hash) VALUES (?, ?, ?)";
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getEmail());
                String hashedPassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt());
                preparedStatement.setString(3, hashedPassword);

                preparedStatement.executeUpdate();
            }
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException("Error creating user.");
        }
    }

    // Gets and returns a user by username
    public UserData getUser(String username) {
        try (var conn = DatabaseManager.getConnection()) {
            String query = "SELECT id, username, email, password_hash FROM users WHERE username = ?";
            try (var preparedStatement = conn.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                try (var rs = preparedStatement.executeQuery()) {
                    if (rs.next()) {
                        return new UserData(
                                rs.getString("username"),
                                rs.getString("password_hash"),
                                rs.getString("email")
                        );
                    }
                }
            }
        } catch (DataAccessException | SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        return null; // User not found
    }
}