package ui;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.List;

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

    public void clearDatabase() {

    }

    public AuthData register(UserData user) {

    }

    public AuthData login(UserData user) {

    }

    public void logout() {

    }

    public List<GameData> listGames() {

    }

    public int createGame(String name) {

    }

    public void joinGame(String playerColor, int gameID) {

    }

}
