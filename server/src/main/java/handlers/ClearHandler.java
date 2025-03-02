package handlers;

import service.ClearService;
import spark.*;
import com.google.gson.Gson;

public class ClearHandler {
    private Gson gson = new Gson();
    private ClearService clearService = new ClearService();

    public Object handleRequest(Request req, Response res) {
        res.status(200); // Manually set response status
        res.type("application/json");

        int errorCode = clearService.clearAll();

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