package handlers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.*;
import servermodel.ErrorResponse;
import servermodel.ResponseObject;
import service.GameService;
import spark.Request;
import spark.Response;

import java.util.Objects;

public class GameHandler {
    private Gson gson = new Gson();
    private GameService gameService = new GameService();

    public Object create(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        String gameName = null;
        try {
            // Get the body of the request and parse it as a JSON object
            String requestBody = req.body();
            JsonObject jsonBody = JsonParser.parseString(requestBody).getAsJsonObject();

            // Get the value of "gameName" from the JSON object
            if (jsonBody.has("gameName")) {
                gameName = jsonBody.get("gameName").getAsString();
            } else {
                res.status(400);
                return gson.toJson(new ResponseObject("Error: bad request"));
            }
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(new ResponseObject("Error: bad request"));
        }

        Object response = gameService.create(authToken, gameName);

        if (response instanceof ErrorResponse && ((ErrorResponse) response).getStatusCode() == 401) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        res.status(200);
        return gson.toJson(response);
    }

    public Object list(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        Object response = gameService.list(authToken);

        if (response instanceof ErrorResponse && ((ErrorResponse) response).getStatusCode() == 401) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        res.status(200);
        return gson.toJson(response);
    }

    public Object join(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        GameJoinRequest gameJoinRequest;
        try {
            gameJoinRequest = gson.fromJson(req.body(), GameJoinRequest.class);
        } catch (Throwable e) {
            res.status(400);
            return gson.toJson(new ResponseObject("Error: bad request"));
        }

        if (!(Objects.equals(gameJoinRequest.getPlayerColor(), "WHITE") || Objects.equals(gameJoinRequest.getPlayerColor(), "BLACK"))) {
            res.status(400);
            return gson.toJson(new ResponseObject("Error: bad request"));
        }

        Object response = gameService.join(authToken, gameJoinRequest);

        if (response instanceof ErrorResponse) {
            if (((ErrorResponse) response).getStatusCode() == 400) {
                res.status(400);
                return gson.toJson(new ResponseObject("Error: bad request"));
            }

            if (((ErrorResponse) response).getStatusCode() == 401) {
                res.status(401);
                return gson.toJson(new ResponseObject("Error: unauthorized"));
            }

            if (((ErrorResponse) response).getStatusCode() == 403) {
                res.status(403);
                return gson.toJson(new ResponseObject("Error: already taken"));
            }
        }

        res.status(200);
        return gson.toJson(response);
    }
}
