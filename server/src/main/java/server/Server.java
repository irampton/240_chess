package server;

import com.google.gson.Gson;
import handlers.DatabaseHandler;
import handlers.GameHandler;
import handlers.UserHandler;
import spark.*;

public class Server {


    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        var serializer = new Gson();
        DatabaseHandler databaseHandler = new DatabaseHandler();
        UserHandler userHandler = new UserHandler();
        GameHandler gameHandler = new GameHandler();

        // Register your endpoints and handle exceptions here.

        // Database
        Spark.delete("/db", databaseHandler::clear);

        // User
        Spark.post("/user", userHandler::register);
        Spark.post("/session", userHandler::login);
        Spark.delete("/session", userHandler::logout);

        // Game
        Spark.get("/game", gameHandler::list);
        Spark.post("/game", gameHandler::create);
        Spark.put("/game", gameHandler::join);

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
