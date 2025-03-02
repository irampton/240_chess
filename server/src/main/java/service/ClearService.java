package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;

public class ClearService {
    // Instance DAOs
    private AuthDAO authDAO = new AuthDAO();
    private GameDAO gameDAO = new GameDAO();
    private UserDAO userDAO = new UserDAO();


    // Clears the database
    // returns 1 if successful, error code if otherwise
    public int clearAll() {
        int sAuth = authDAO.clear();
        int sGame = gameDAO.clear();
        int sUser = userDAO.clear();

        if (sAuth != 1) {
            return sAuth;
        } else if (sGame != 1) {
            return sGame;
        } else if (sUser != 1) {
            return sUser;
        }

        return 1;
    }
}
