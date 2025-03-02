package handlers;

import model.ErrorResponse;
import model.ResponseObject;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
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

    /*public Object login(Request req, Response res) {

    }

    public Object logout(Request req, Response res) {

    }*/
}
