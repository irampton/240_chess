package model;

public class AuthData {
    private String authToken;
    private String username;

    // Constructor
    public AuthData(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    // Getters and Setters
    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        return "AuthData [authToken=" + authToken + ", username=" + username + "]";
    }
}