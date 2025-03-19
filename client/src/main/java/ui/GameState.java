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
        System.out.print("[LOGGED_OUT] >>> ");
        String line = scanner.nextLine();
        var command = line.split(" ");

        switch (command[0]) {
            case "help":
                System.out.print("this is a list of commands: \n");
                break;
            case "quit":
                System.out.println("Goodbye!");
                System.exit(0);
                break;
            default:
                System.out.println("Invalid command");
        }
    }
}
