package dataaccess;

import model.GameData;

import java.util.ArrayList;
import java.util.List;

public class GameDAO {
    // Static list to store GameData in memory
    private static List<GameData> gameDataList = new ArrayList<>();

    // Clears all games from the database
    // returns 1 if successful, error code if otherwise
    public int clear(){
        try {
            gameDataList.clear();
            return 1; // Success
        } catch (Exception e) {
            return -1; // Error (Codes will be updated when adding the database)
        }
    }

    public void createGame(){

    }

    public void getGame(){

    }

    public void listGames(){

    }

    public void updateGame(){

    }
}
