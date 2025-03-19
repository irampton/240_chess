package ui;

import java.util.Scanner;

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
                        System.out.print("this is a list of commands: \n");
                        break;
                    case "quit":
                        System.out.println("Goodbye!");
                        System.exit(0);
                        break;
                    case "register":
                        break;
                    case "login":
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
            case LOGGED_IN:
                switch (command[0].toLowerCase()) {
                    default:
                        System.out.println("Invalid command");
                }
                break;
            case IN_GAME:
                switch (command[0].toLowerCase()) {
                    case "leave":
                        break;
                    default:
                        System.out.println("Invalid command");
                }
                break;
        }
    }
}
