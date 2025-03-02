package handlers;

import model.ResponseObject;
import service.DatabaseService;
import spark.*;
import com.google.gson.Gson;

public class DatabaseHandler {
    private Gson gson = new Gson();
    private DatabaseService databaseService = new DatabaseService();

    public Object clear(Request req, Response res) {
        res.status(200); // Manually set response status
        res.type("application/json");

        int errorCode = databaseService.clearAll();

        ResponseObject responseObject;

        if (errorCode != 1) {
            responseObject = new ResponseObject("Error Code: " + errorCode);
        } else {
            responseObject = new ResponseObject(null);
        }

        // Return the serialized response object as a JSON string
        return gson.toJson(responseObject);
    }
}