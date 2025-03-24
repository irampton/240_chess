package ui;

import chess.ChessBoard;
import chess.ChessGame;
import model.*;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

enum State {
    LOGGED_OUT,
    LOGGED_IN,
    IN_GAME
}

public class GameState {
    private State currentState;
    private final Scanner scanner = new Scanner(System.in);
    private ServerFacade serverFacade;
    private final DrawChessBoard boardDrawer = new DrawChessBoard();

    public GameState() {
        currentState = State.LOGGED_OUT;
        serverFacade = new ServerFacade(null, 0);
    }

    public void getCommand() {
        System.out.print(SET_TEXT_COLOR_BLACK);
        switch (currentState) {
            case LOGGED_OUT:
                System.out.print("[LOGGED_OUT] >>> ");
                break;
            case LOGGED_IN:
                System.out.print("[LOGGED_IN] >>> ");
                break;
            case IN_GAME:
                System.out.print("[IN_GAME] >>> ");
                break;
        }
        String line = scanner.nextLine();
        var command = line.split(" ");
        switch (currentState) {
            case LOGGED_OUT:
                switch (command[0].toLowerCase()) {
                    case "help":
                        // help
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Help\t\t\t\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- List all available commands\n");
                        // quit
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Quit\t\t\t\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Quit the chess client\n");
                        // login
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Login\t\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<USERNAME> <PASSWORD>\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Login to the server\n");
                        // register
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Register\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<USERNAME> <PASSWORD> <EMAIL>\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Create an account\n");
                        break;
                    case "quit":
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    case "register":
                        try {
                            if (command.length != 4) {
                                throw new IllegalArgumentException("Invalid number of arguments. Expected 4 arguments.");
                            }
                            UserData newUser = new UserData(command[1], command[2], command[3]);
                            serverFacade.register(newUser);
                            System.out.print(SET_TEXT_COLOR_CYAN);
                            System.out.print("Logged in as: ");
                            System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                            System.out.print(command[1]);
                            System.out.println();
                            currentState = State.LOGGED_IN;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "login":
                        try {
                            if (command.length != 3) {
                                throw new IllegalArgumentException("Invalid number of arguments. Expected 3 arguments.");
                            }
                            LoginRequest loginInfo = new LoginRequest(command[1], command[2]);
                            serverFacade.login(loginInfo);
                            System.out.print(SET_TEXT_COLOR_CYAN);
                            System.out.print("Logged in as: ");
                            System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                            System.out.print(command[1]);
                            System.out.println();
                            currentState = State.LOGGED_IN;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "clear":
                        try {
                            if (serverFacade.clearDatabase()) {
                                System.out.println("Database cleared");
                            } else {
                                System.out.println("Failed to clear database");
                            }
                        } catch (Exception e) {
                            System.out.println("Error clearing database");
                        }
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
            case LOGGED_IN:
                switch (command[0].toLowerCase()) {
                    case "help":
                        // help
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Help\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- List all available commands\n");
                        // quit
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Quit\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Quit the chess client\n");
                        // list
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("List\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- List all available games\n");
                        // create
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Create\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<NAME>\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Create a new game\n");
                        // join
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Join\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<ID> [WHITE|BLACK]\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Join a game\n");
                        // observe
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Observe\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<ID>\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Observe a game\n");
                        break;
                    case "logout":
                        try {
                            serverFacade.logout();
                            System.out.print(SET_TEXT_COLOR_CYAN);
                            System.out.print("Logged out");
                            System.out.println();
                            currentState = State.LOGGED_OUT;
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "create":
                        try {
                            if (command.length != 2) {
                                throw new IllegalArgumentException("Invalid number of arguments. Expected 2 arguments.");
                            }
                            serverFacade.createGame(new CreateGameRequest(command[1]));
                            System.out.print(SET_TEXT_COLOR_CYAN);
                            System.out.print("Created game \"");
                            System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                            System.out.print(command[1]);
                            System.out.print(SET_TEXT_COLOR_CYAN);
                            System.out.print("\"");
                            System.out.println();
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "list":
                        try {
                            List<GameData> gameList = serverFacade.listGames();
                            int index = 1;
                            for (GameData game : gameList) {
                                System.out.print(SET_TEXT_COLOR_CYAN);
                                System.out.print(index + "\t");
                                System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                                System.out.println(game.getGameName() + "\t");
                                index++;
                            }
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }
                        break;
                    case "join":
                        currentState = State.IN_GAME;
                        break;
                    case "observe":
                        currentState = State.IN_GAME;
                        ChessBoard board = new ChessBoard();
                        board.resetBoard();
                        boardDrawer.drawBoard(board, ChessGame.TeamColor.WHITE);
                        break;
                    case "quit":
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
            case IN_GAME:
                switch (command[0].toLowerCase()) {
                    case "leave":
                        System.out.println("Leaving game");
                        currentState = State.LOGGED_IN;
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
        }
    }
}
