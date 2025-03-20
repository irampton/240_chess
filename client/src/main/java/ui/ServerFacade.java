package ui;

import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
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

    /*public AuthData register(UserData user) {

    }

    public AuthData login(UserData user) {

    }*/

    public void logout() {

    }

    /*public List<GameData> listGames() {

    }

    public int createGame(String name) {

    }

    public void joinGame(String playerColor, int gameID) {

    }*/

}
