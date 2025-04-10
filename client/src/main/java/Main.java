import chess.*;
import ui.GameState;

import static ui.EscapeSequences.RESET_TEXT_COLOR;


public class Main {

    public static void main(String[] args) {
        GameState gameState = new GameState();

        System.out.println("♕ 240 Chess Client - type 'help' to get started.");
        System.out.println();

        while (true) {
            gameState.getCommand();
            System.out.println(RESET_TEXT_COLOR);
        }
    }
}