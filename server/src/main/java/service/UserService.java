package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.*;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Objects;

public class UserService {
    // Instance DAOs
    private AuthDAO authDAO = new AuthDAO();
    private UserDAO userDAO = new UserDAO();

    public Object register(UserData registerRequest) {
        try {
            UserData userResult = userDAO.createUser(registerRequest);
            return authDAO.createAuth(userResult.getUsername());
        } catch (DataAccessException e) {
            if (Objects.equals(e.getMessage(), "Username is already taken.")) {
                return new ErrorResponse("Username is already taken.", 403);
            }
            return new ErrorResponse(e.getMessage(), 400);
        }
    }

    public Object login(LoginRequest loginRequest) {
        UserData user = userDAO.getUser(loginRequest.getUsername());

        if (user == null) {
            return new ErrorResponse("Error: unauthorized", 401);
        } else if (BCrypt.checkpw(loginRequest.getPassword(), user.getPassword())) {
            return authDAO.createAuth(user.getUsername());
        } else {
            return new ErrorResponse("Error: unauthorized", 401);
        }
    }

    public Object logout(String authToken) {
        AuthData authData = authDAO.getAuth(authToken);

        if (authData == null) {
            return new ErrorResponse("Error: unauthorized", 401);
        } else {
            authDAO.deleteAuth(authData);
            return null;
        }
    }
}
