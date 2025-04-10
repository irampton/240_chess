package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.*;
import websockets.WSClient;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import websocket.commands.*;
import websocket.commands.ConnectCommand.CommandType.*;

import static chess.ChessGame.TeamColor.WHITE;

public class ServerFacade {
    // gson
    private Gson gson = new GsonBuilder()
            .registerTypeAdapter(ChessGame.class, new ChessGameDeserializer())
            .create();

    private WSClient wsClient;

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
        try {
            wsClient = new WSClient();
        } catch (Exception e) {
            //fail silently
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
            getAuthToken(http);
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
        //System.out.println("Server returned HTTP code " + status);
        if (status >= 200 && status < 300) {
            getAuthToken(http);
        } else {
            switch (status) {
                case 500:
                    throw new Exception("Internal Server Error");
                case 401:
                    throw new Exception("Unauthorized");
            }
            throw new Exception("Error logging in");
        }
    }

    private void getAuthToken(HttpURLConnection http) throws IOException {
        try (InputStream in = http.getInputStream()) {
            AuthData auth = new Gson().fromJson(new InputStreamReader(in), AuthData.class);
            this.authToken = auth.getAuthToken();
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

    public List<GameData> listGames() throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("GET");

        // Set the Authorization header if we have a token
        if (this.authToken == null) {
            throw new Exception("Not logged in");
        }
        http.setRequestProperty("Authorization", this.authToken);

        http.connect();

        var status = http.getResponseCode();
        if (status >= 200 && status < 300) {
            try (InputStream respBody = http.getInputStream()) {
                InputStreamReader inputStreamReader = new InputStreamReader(respBody);

                // Deserialize the JSON into a Map
                Map<String, Object> responseMap = new Gson().fromJson(inputStreamReader, Map.class);

                // Get the 'games' array from the Map
                List<Map<String, Object>> gameDataList = (List<Map<String, Object>>) responseMap.get("games");

                List<GameData> games = new ArrayList<>();
                for (Map<String, Object> gameData : gameDataList) {
                    int gameID = ((Double) gameData.get("gameID")).intValue();
                    String gameName = (String) gameData.get("gameName");
                    String whiteUsername = (String) gameData.get("whiteUsername");
                    String blackUsername = (String) gameData.get("blackUsername");
                    games.add(new GameData(gameID, whiteUsername, blackUsername, gameName, null));
                }

                return games;
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

    public void createGame(CreateGameRequest newGame) throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("POST");

        // Set the Authorization header if we have a token
        if (this.authToken == null) {
            throw new Exception("Not logged in");
        }
        http.setRequestProperty("Authorization", this.authToken);

        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(newGame);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status < 200 || status > 300) {
            //System.out.println("Server returned HTTP code " + status);
            switch (status) {
                case 400:
                    throw new Exception("Bad Request");
                case 401:
                    throw new Exception("Unauthorized");
                case 500:
                    throw new Exception("Internal Server Error");
            }
            throw new Exception("Error creating game");
        }
    }

    public void joinGame(GameJoinRequest joinRequest) throws Exception {
        URI uri = new URI(this.serverUrl + ":" + this.port + "/game");
        HttpURLConnection http = (HttpURLConnection) uri.toURL().openConnection();
        http.setRequestMethod("PUT");

        // Set the Authorization header if we have a token
        if (this.authToken == null) {
            throw new Exception("Not logged in");
        }
        http.setRequestProperty("Authorization", this.authToken);

        http.setDoOutput(true);
        http.addRequestProperty("Content-Type", "application/json");

        try (var outputStream = http.getOutputStream()) {
            var jsonBody = new Gson().toJson(joinRequest);
            outputStream.write(jsonBody.getBytes());
        }

        http.connect();

        // Handle bad HTTP status
        var status = http.getResponseCode();
        if (status < 200 || status > 300) {
            //System.out.println("Server returned HTTP code " + status);
            switch (status) {
                case 400:
                    throw new Exception("Bad Request");
                case 401:
                    throw new Exception("Unauthorized");
                case 403:
                    throw new Exception("Color already taken");
                case 500:
                    throw new Exception("Internal Server Error");
            }
            throw new Exception("Error creating game");
        } else {
            // We've joined the game, now let's play!
            joinGameWS(joinRequest);
        }
    }

    public void joinGameWS(GameJoinRequest joinRequest) throws Exception {
        wsClient.send(gson.toJson(new ConnectCommand(authToken, joinRequest.getGameID(), joinRequest.getPlayerColor().equalsIgnoreCase("white") ? ConnectCommand.CommandType.WHITE : ConnectCommand.CommandType.BLACK)));
        wsClient.setRole(joinRequest.getPlayerColor().equalsIgnoreCase("white") ? ConnectCommand.CommandType.WHITE : ConnectCommand.CommandType.BLACK);
        wsClient.showNextOutput();
    }

    public void observeGame(int gameID) throws Exception {
        wsClient.send(gson.toJson(new ConnectCommand(authToken, gameID, ConnectCommand.CommandType.OBSERVER)));
        wsClient.setRole(ConnectCommand.CommandType.OBSERVER);
        wsClient.showNextOutput();
    }

    public void leaveGame(int gameID) throws Exception {
        wsClient.send(gson.toJson(new LeaveCommand(authToken, gameID)));
        wsClient.showLoggedInMessages();
    }

    public void redrawBoard() {
        wsClient.drawChessBoard();
    }

    public void drawHighlightedChessboard(ChessPosition startPosition) {
        wsClient.drawHighlightedChessBoard(startPosition);
    }

    public void makeMove(ChessMove move, int gameID) throws Exception {
        wsClient.send(gson.toJson(new MakeMoveCommand(authToken, gameID, move), MakeMoveCommand.class));
        wsClient.showNextOutput();
    }

    public void resignGame(int gameID) throws Exception {
        wsClient.send(gson.toJson(new ResignCommand(authToken, gameID), ResignCommand.class));
    }
}
