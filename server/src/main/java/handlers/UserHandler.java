package handlers;

import server.model.ErrorResponse;
import model.LoginRequest;
import server.model.ResponseObject;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import com.google.gson.Gson;

public class UserHandler {
    private Gson gson = new Gson();
    private UserService userService = new UserService();


    public Object register(Request req, Response res) {
        UserData userData;
        try {
            userData = gson.fromJson(req.body(), UserData.class);
        } catch (Throwable e) {
            res.status(400);
            return gson.toJson(new ResponseObject("Error: bad request"));
        }

        // Validate the user data (e.g., check if username, password, and email are not empty)
        if (userData.getUsername() == null || userData.getUsername().isEmpty() ||
                userData.getPassword() == null || userData.getPassword().isEmpty() ||
                userData.getEmail() == null || userData.getEmail().isEmpty()) {
            res.status(400);
            return gson.toJson(new ResponseObject("Error: bad request"));
        }

        Object response = userService.register(userData);

        if(response == null) {
            res.status(500);
            return gson.toJson(new ResponseObject("Error: An unknown error occurred on the server"));
        }

        if(response instanceof ErrorResponse && ((ErrorResponse) response).getStatusCode() == 403) {
            res.status(403);
            return gson.toJson(new ResponseObject("Error: already taken"));
        }

        res.status(200);
        return gson.toJson(response);
    }

    public Object login(Request req, Response res) {
        LoginRequest loginRequest;
        try {
            loginRequest = gson.fromJson(req.body(), LoginRequest.class);
        } catch (Throwable e) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        // Validate the login request (ensure username and password are not empty)
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isEmpty() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        Object response = userService.login(loginRequest);

        if (response instanceof ErrorResponse && ((ErrorResponse) response).getStatusCode() == 401) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        res.status(200);
        return gson.toJson(response);
    }

    public Object logout(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        Object response = userService.logout(authToken);

        if (response instanceof ErrorResponse && ((ErrorResponse) response).getStatusCode() == 401) {
            res.status(401);
            return gson.toJson(new ResponseObject("Error: unauthorized"));
        }

        res.status(200);
        return gson.toJson(new ResponseObject(null));
    }
}
