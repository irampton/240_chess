package passoff.chess;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ChessBoardTests {

    @Test
    @DisplayName("Add and Get Piece")
    public void getAddPiece() {
        ChessPosition position = new ChessPosition(4, 4);
        ChessPiece piece = new ChessPiece(ChessGame.TeamColor.BLACK, ChessPiece.PieceType.BISHOP);

        var board = new ChessBoard();
        board.addPiece(position, piece);

        ChessPiece foundPiece = board.getPiece(position);

        Assertions.assertEquals(piece.getPieceType(), foundPiece.getPieceType(),
                "ChessPiece returned by getPiece had the wrong piece type");
        Assertions.assertEquals(piece.getTeamColor(), foundPiece.getTeamColor(),
                "ChessPiece returned by getPiece had the wrong team color");
    }


    @Test
    @DisplayName("Reset Board")
    public void defaultGameBoard() {
        var expectedBoard = TestUtilities.defaultBoard();

        // Log piece type and team at (0,0) and (8,8)
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position00 = new ChessPosition(i, j);
                ChessPiece piece00 = expectedBoard.getPiece(position00);
                if (piece00 != null) {
                    System.out.println("Piece at (" + i + "," + j + "): " + piece00.getPieceType() + " of team " + piece00.getTeamColor());
                }
            }
        }

        System.out.println("\nHEY!!!\n");

        var actualBoard = new ChessBoard();
        actualBoard.resetBoard();

        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPiece piece00 = actualBoard.getPiece(new ChessPosition(i, j));
                if (piece00 != null) {
                    System.out.println("Piece at (" + i + "," + j + "): " + piece00.getPieceType() + " of team " + piece00.getTeamColor());
                }
            }
        }

        Assertions.assertEquals(expectedBoard, actualBoard);
    }


    @Test
    @DisplayName("Piece Move on All Pieces")
    public void pieceMoveAllPieces() {
        var board = new ChessBoard();
        board.resetBoard();
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition position = new ChessPosition(i, j);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    Assertions.assertDoesNotThrow(() -> piece.pieceMoves(board, position));
                }
            }
        }
    }

}