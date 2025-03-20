package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.LoginRequest;
import model.UserData;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class ServerFacade {
    // Class-level variables for server port and URL
    private static final int DEFAULT_PORT = 8080;
    private static final String DEFAULT_URL = "http://localhost";

    // Instance variables for configurable port and URL
    private int port = DEFAULT_PORT;
    private String serverUrl = DEFAULT_URL;

    private String authToken = null;

    public ServerFacade(String serverUrl, int port) {
        if (port > 0) {
            this.port = port;
        }
        if (serverUrl != null && !serverUrl.isEmpty()) {
            this.serverUrl = serverUrl;
        }
    }

    public boolean clearDatabase() throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/db");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            return true;
        } else {
            //System.out.println("Server returned HTTP code " + status);
            return false;
        }
    }

    public void register(UserData user) throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/user");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        // Specify that we are going to write out data
        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(user);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            try (InputStream in = http.getInputStream()) {
                AuthData auth = new Gson().fromJson(new InputStreamReader(in), AuthData.class);
                this.authToken = auth.getAuthToken();
            }
        } else {
            //System.out.println("Server returned HTTP code " + status);
            switch (status) {
                case 400:
                    throw new Exception("Bad Request");
                case 403:
                    throw new Exception("Username already taken");
                case 500:
                    throw new Exception("Internal Server Error");
            }
            throw new Exception("Error registering user");
        }
    }

    public void login(LoginRequest loginInfo) throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        // Specify that we are going to write out data
        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(loginInfo);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            try (InputStream in = http.getInputStream()) {
                AuthData auth = new Gson().fromJson(new InputStreamReader(in), AuthData.class);
                this.authToken = auth.getAuthToken();
            }
        } else {
            //System.out.println("Server returned HTTP code " + status);
            switch (status) {
                case 401:
                    throw new Exception("Unauthorized");
                case 500:
                    throw new Exception("Internal Server Error");
            }
            throw new Exception("Error logging in");
        }
    }

    public void logout() throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/session");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("DELETE");

        // Set the Authorization header if we have a token
        if (this.authToken == null) {
            throw new Exception("Not logged in");
        }
        http.setRequestProperty("Authorization", this.authToken);

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            this.authToken = null;
        } else {
            //System.out.println("Server returned HTTP code " + status);
            switch (status) {
                case 401:
                    throw new Exception("Unauthorized");
                case 500:
                    throw new Exception("Internal Server Error");
            }
            throw new Exception("Error logging out");
        }
    }

    /*public List<GameData> listGames() {

    }

    public int createGame(String name) {

    }

    public void joinGame(String playerColor, int gameID) {

    }*/

}
