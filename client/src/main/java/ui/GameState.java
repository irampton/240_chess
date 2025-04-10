package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import model.*;

import java.util.List;
import java.util.Scanner;

import static ui.EscapeSequences.*;

public class GameState {
    enum State {
        LOGGED_OUT,
        LOGGED_IN,
        IN_GAME
    }

    private State currentState;
    private final Scanner scanner = new Scanner(System.in);
    private ServerFacade serverFacade;
    private final DrawChessBoard boardDrawer = new DrawChessBoard();
    private List<GameData> gameList;
    private Integer gameID;
    private Boolean suppressNextOutput = false;
    private Boolean printInGame = false;
    private String username;

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
                if (!suppressNextOutput) {
                    System.out.print("[LOGGED_IN] >>> ");
                } else {
                    suppressNextOutput = false;
                }
                break;
            case IN_GAME:
                if (printInGame) {
                    System.out.print("[IN_GAME] >>> ");
                    printInGame = false;
                }
                break;
        }
        String line = scanner.nextLine();
        var command = line.split(" ");
        switch (currentState) {
            case LOGGED_OUT:
                loggedOutCommands(command);
                break;
            case LOGGED_IN:
                loggedInCommands(command);
                break;
            case IN_GAME:
                inGameCommands(command);
                break;
        }
    }

    private void loggedOutCommands(String[] command) {
        try {
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
                    break;
                case "login":
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
                    username = command[1];
                    break;
                case "clear":
                    try {
                        if (serverFacade.clearDatabase()) {
                            System.out.println("Database cleared");
                        } else {
                            System.out.println("Failed to clear database");
                        }
                    } catch (Exception e) {
                        throw new Exception("Error clearing database");
                    }
                    break;
                default:
                    throw new Exception("Invalid command");
            }
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println(e.getMessage());
            System.out.println(RESET_TEXT_COLOR);
        }
        return;
    }

    private void loggedInCommands(String[] command) {
        try {
            switch (command[0].toLowerCase()) {
                case "help":
                    printLoggedInHelp();
                    break;
                case "logout":
                    serverFacade.logout();
                    System.out.print(SET_TEXT_COLOR_CYAN);
                    System.out.print("Logged out");
                    System.out.println();
                    currentState = State.LOGGED_OUT;
                    break;
                case "create":
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
                    break;
                case "list":
                    gameList = serverFacade.listGames();
                    printGameList();
                    break;
                case "join":
                    joinGameCommand(command);
                    currentState = State.IN_GAME;
                    break;
                case "observe":
                    if (command.length != 2) {
                        throw new IllegalArgumentException("Invalid number of arguments. Expected 2 arguments.");
                    }

                    int gameNumber;
                    try {
                        gameNumber = Integer.parseInt(command[1]);
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Invalid game number");
                    }

                    if (!(gameNumber >= 1 && gameNumber <= gameList.size())) {
                        throw new IllegalArgumentException("Invalid game ID");
                    }
                    GameData game = gameList.get(gameNumber - 1);
                    gameID = game.getGameID();

                    try {
                        serverFacade.observeGame(gameID);
                        currentState = State.IN_GAME;
                    } catch (Exception e) {
                        throw new IllegalArgumentException("Server Error. Please try again.");
                    }
                    break;
                case "quit":
                    System.out.println("Goodbye!");
                    System.exit(0);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command");
            }
        } catch (Exception e) {
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println(e.getMessage());
            System.out.println(RESET_TEXT_COLOR);
        }
    }

    private static void printLoggedInHelp() {
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
    }

    private void printGameList() {
        System.out.print(RESET_TEXT_COLOR);
        System.out.println("ID Game Name           White Player        Black Player");
        int index = 1;
        for (GameData game : gameList) {
            String whiteUsername = (game.getWhiteUsername() != null) ? game.getWhiteUsername() : "---";
            String blackUsername = (game.getBlackUsername() != null) ? game.getBlackUsername() : "---";
            System.out.print(SET_TEXT_COLOR_CYAN);
            System.out.print(String.format("%-3d", index));
            System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
            System.out.print(String.format("%-20s", game.getGameName()));
            System.out.print(SET_TEXT_COLOR_PURPLE);
            System.out.print(String.format("%-20s", whiteUsername));
            System.out.print(String.format("%-20s", blackUsername));
            System.out.println();
            index++;
        }
    }

    private void joinGameCommand(String[] command) throws Exception {
        if (gameList == null) {
            throw new IllegalArgumentException("Please list games before joining any");
        }
        if (command.length != 3) {
            throw new IllegalArgumentException("Invalid number of arguments. Expected 3 arguments.");
        }

        int gameNumber = Integer.parseInt(command[1]);

        // Check arguments
        if (!(gameNumber >= 1 && gameNumber <= gameList.size())) {
            throw new IllegalArgumentException("Invalid game ID");
        }
        if (!(command[2].equalsIgnoreCase("WHITE") || command[2].equalsIgnoreCase("BLACK"))) {
            throw new IllegalArgumentException("Invalid color");
        }

        GameData game = gameList.get(gameNumber - 1);
        gameID = game.getGameID();

        ChessGame.TeamColor teamColor = command[2].equalsIgnoreCase("WHITE")
                ? ChessGame.TeamColor.WHITE
                : ChessGame.TeamColor.BLACK;
        if (
                (teamColor == ChessGame.TeamColor.WHITE && game.getWhiteUsername() != null
                        && game.getWhiteUsername().equalsIgnoreCase(username))
                        || (teamColor == ChessGame.TeamColor.BLACK && game.getBlackUsername() != null
                        && game.getBlackUsername().equalsIgnoreCase(username))
        ) {
            // User is already in game, let them join
            serverFacade.joinGameWS(new GameJoinRequest(command[2].toUpperCase(), gameID));
        } else if (
                (teamColor == ChessGame.TeamColor.WHITE
                        && game.getWhiteUsername() != null)
                        || (teamColor == ChessGame.TeamColor.BLACK
                        && game.getBlackUsername() != null)
        ) {
            throw new IllegalArgumentException("Color already taken");
        } else {
            serverFacade.joinGame(new GameJoinRequest(command[2].toUpperCase(), gameID));
        }
    }

    private void inGameCommands(String[] command) {
        try {
            switch (command[0].toLowerCase()) {
                case "help":
                    printInGameHelp();
                    printInGame = true;
                    break;
                case "redraw":
                    serverFacade.redrawBoard();
                    printInGame = true;
                    break;
                case "highlight":
                    if (command.length != 2) {
                        throw new IllegalArgumentException("Invalid number of arguments. Expected 2 arguments.");
                    }

                    ChessPosition startPosition = parseChessPosition(command[1]);
                    serverFacade.drawHighlightedChessboard(startPosition);
                    printInGame = true;
                    break;
                case "move":
                    if (command.length != 3) {
                        throw new IllegalArgumentException("Invalid number of arguments. Expected 3 arguments.");
                    }
                    ChessPosition moveStart = parseChessPosition(command[1]);
                    ChessPosition moveEnd = parseChessPosition(command[2]);

                    serverFacade.makeMove(new ChessMove(moveStart, moveEnd, null), gameID);
                    break;
                case "leave":
                    serverFacade.leaveGame(gameID);
                    currentState = State.LOGGED_IN;
                    suppressNextOutput = true;
                    break;
                case "resign":
                    break;
                default:
                    throw new IllegalArgumentException("Invalid command");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.print(SET_TEXT_COLOR_RED);
            System.out.println(e.getMessage());
            System.out.println(RESET_TEXT_COLOR);
            printInGame = true;
        }
    }

    private static void printInGameHelp() {
        // Help
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Help\t\t\t\t\t\t\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- List all available commands\n");
        // Redraw
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Redraw\t\t\t\t\t\t\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- Redraw the chessboard\n");
        // Highlight
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Highlight\t");
        System.out.print(SET_TEXT_COLOR_CYAN);
        System.out.print("<LOCATION>\t\t\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- Show legal moves\n");
        // Move
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Move\t\t");
        System.out.print(SET_TEXT_COLOR_CYAN);
        System.out.print("<LOCATION> <LOCATION>\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- Make a move\n");
        // Leave
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Leave\t\t\t\t\t\t\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- Leave the game\n");
        // Resign
        System.out.print(SET_TEXT_COLOR_BLUE);
        System.out.print("Resign\t\t\t\t\t\t\t\t");
        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
        System.out.print("- Resign from the game\n");
    }

    public static ChessPosition parseChessPosition(String pos) throws Exception {
        // Check that the input is not null and exactly one letter and 1 number
        if (pos == null || pos.length() != 2) {
            throw new IllegalArgumentException(
                    "Invalid input: must be a valid chess position."
            );
        }
        char colChar = Character.toLowerCase(pos.charAt(0));
        if (colChar < 'a' || colChar > 'h') {
            throw new IllegalArgumentException(
                    "Invalid column: '" + pos.charAt(0) + "'. Must be between 'a' and 'h'."
            );
        }
        char rowChar = pos.charAt(1);
        if (rowChar < '1' || rowChar > '8') {
            throw new IllegalArgumentException(
                    "Invalid row: '" + rowChar + "'. Must be between '1' and '8'."
            );
        }

        // Get row and col
        int col = colChar - 'a' + 1;
        int row = rowChar - '0';

        return new ChessPosition(row, col);
    }

}
