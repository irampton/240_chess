package websocket.messages;

import chess.ChessBoard;

public class LoadGameMessage extends ServerMessage {
    private final ChessBoard board;

    LoadGameMessage(ChessBoard board) {
        super(ServerMessageType.LOAD_GAME);
        this.board = board;
    }

    ChessBoard getBoard() {
        return board;
    }
}
