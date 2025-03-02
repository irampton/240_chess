package model;

public class RegisterResult {
    private String username;
    private String authToken;

    public RegisterResult(String username, String authToken) {
        this.username = username;
        this.authToken = authToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "username='" + username + '\'' +
                ", authToken='" + authToken + '\'' +
                '}';
    }

    // Optionally, you can also implement hashCode and equals if needed for better object comparison
}
