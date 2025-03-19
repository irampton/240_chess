import chess.*;
import ui.GameState;


public class Main {

    public static void main(String[] args) {
        GameState gameState = new GameState();

        System.out.println("♕ 240 Chess Client - type 'help' to get started.");
        System.out.println();

        while (true) {
            gameState.getCommand();
        }
    }
}