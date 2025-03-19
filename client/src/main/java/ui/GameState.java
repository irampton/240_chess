package ui;

import java.util.Scanner;

import static ui.EscapeSequences.*;

enum State {
    LOGGED_OUT,
    LOGGED_IN,
    IN_GAME
}

public class GameState {
    private State currentState;
    private Scanner scanner = new Scanner(System.in);

    public GameState() {
        currentState = State.LOGGED_OUT;
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
                        //help
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Help\t\t\t\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- List all available commands\n");
                        //quit
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Quit\t\t\t\t\t\t\t\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Quit the chess client\n");
                        //login
                        System.out.print(SET_TEXT_COLOR_BLUE);
                        System.out.print("Login\t\t");
                        System.out.print(SET_TEXT_COLOR_CYAN);
                        System.out.print("<USERNAME> <PASSWORD>\t\t\t");
                        System.out.print(EscapeSequences.SET_TEXT_COLOR_DARK_GREEN);
                        System.out.print("- Login to the server\n");
                        //register
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
                        break;
                    case "login":
                        currentState = State.LOGGED_IN;
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
            case LOGGED_IN:
                switch (command[0].toLowerCase()) {
                    case "help":
                        break;
                    case "logout":
                        currentState = State.LOGGED_OUT;
                        break;
                    case "create":
                        break;
                    case "list":
                        break;
                    case "play":
                        currentState = State.IN_GAME;
                        break;
                    case "observe":
                        currentState = State.IN_GAME;
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
