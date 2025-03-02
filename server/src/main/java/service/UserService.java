package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.ErrorResponse;
import model.RegisterResult;
import model.ResponseObject;
import model.UserData;

import java.util.UUID;

public class UserService {
    // Instance DAOs
    private AuthDAO authDAO = new AuthDAO();
    private UserDAO userDAO = new UserDAO();

    public Object register(UserData registerRequest) {
        try {
            UserData userResult = userDAO.createUser(registerRequest);
            String authToken = UUID.randomUUID().toString();

            return new RegisterResult(userResult.getUsername(), authToken);
        } catch (DataAccessException e) {
            return new ErrorResponse(e.toString(), 400);
        }
    }

    /*public LoginResult login(LoginRequest loginRequest) {

    }
    public void logout(LogoutRequest logoutRequest) {

    }*/
}
