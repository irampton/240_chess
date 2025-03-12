package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.DataAccessException;
import model.*;

import java.util.*;

public class GameService {
    private AuthDAO authDAO = new AuthDAO();
    private GameDAO gameDAO = new GameDAO();

    // Instance DAOs
    public Object create(String authToken, String gameName) {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return new ErrorResponse("Error checking auth", 500);
        }

        if (authData == null) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        GameData game = gameDAO.createGame(gameName);

        return new CreateGameResponse(game.getGameID());
    }

    public Object list(String authToken) {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return new ErrorResponse("Error checking auth", 500);
        }

        if (authData == null) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        List<GameData> gameList = gameDAO.getAllGames();

        List<GameDataDTO> gameDTOList = new ArrayList<>();
        for (GameData gameData : gameList) {
            gameDTOList.add(new GameDataDTO(gameData));
        }

        // Wrap the gameDTOList in a map with a "games" key
        Map<String, List<GameDataDTO>> response = new HashMap<>();
        response.put("games", gameDTOList);

        return response;
    }

    public Object join(String authToken, GameJoinRequest gameJoinRequest) {
        AuthData authData;
        try {
            authData = authDAO.getAuth(authToken);
        } catch (DataAccessException e) {
            return new ErrorResponse("Error checking auth", 500);
        }

        if (authData == null) {
            return new ErrorResponse("Error: unauthorized", 401);
        }

        GameData game = gameDAO.getGame(gameJoinRequest.getGameID());

        if(game == null) {
            return new ErrorResponse("Error: no game exists", 400);
        }

        if(Objects.equals(gameJoinRequest.getPlayerColor(), "WHITE")){
            if(game.getWhiteUsername() != null){
                return new ErrorResponse("Error: white username taken", 403);
            }

            game.setWhiteUsername(authData.getUsername());
            gameDAO.updateGame(game);

            return game;
        } else{
            if(game.getBlackUsername() != null){
                return new ErrorResponse("Error: black username taken", 403);
            }

            game.setBlackUsername(authData.getUsername());
            gameDAO.updateGame(game);

            return game;
        }

    }
}
