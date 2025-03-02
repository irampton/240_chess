package server;

import com.google.gson.Gson;
import handlers.ClearHandler;
import spark.*;

public class Server {



    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        var serializer = new Gson();
        ClearHandler clearHandler = new ClearHandler();
        // Register your endpoints and handle exceptions here.

        Spark.delete("/db",(req,res) -> (clearHandler.handleRequest(req,res)));
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
