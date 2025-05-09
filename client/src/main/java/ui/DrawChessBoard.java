package ui;

import chess.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static ui.EscapeSequences.*;

public class DrawChessBoard {

    public void drawBoard(ChessBoard chessBoard, ChessGame.TeamColor teamColor, Collection<ChessPosition> legalMoves) {
        // for each position on the board
        printRowLetters(teamColor);
        for (int row = 1; row <= 8; row++) {
            printColumnNumber(row, teamColor);
            for (int column = 1; column <= 8; column++) {
                ChessPosition pos = new ChessPosition(
                        teamColor == ChessGame.TeamColor.WHITE ? (9 - row) : row,
                        teamColor == ChessGame.TeamColor.BLACK ? (9 - column) : column);
                boolean legalMove = legalMoves.contains(pos);

                // Background color
                if ((row + column) % 2 == 0) {
                    if (legalMove) {
                        System.out.print(SET_BG_COLOR_LIGHT_GREEN);
                    } else {
                        System.out.print(SET_BG_COLOR_LIGHT_BROWN);
                    }
                } else {
                    if (legalMove) {
                        System.out.print(SET_BG_COLOR_DARK_GREEN);
                    } else {
                        System.out.print(SET_BG_COLOR_DARK_BROWN);
                    }
                }

                // Get Piece
                ChessPiece piece = chessBoard.getPiece(pos);
                if (piece == null) {
                    System.out.print(EMPTY);
                    continue;
                }

                // Draw piece
                if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                    switch (piece.getPieceType()) {
                        case KING:
                            System.out.print(WHITE_KING);
                            break;
                        case QUEEN:
                            System.out.print(WHITE_QUEEN);
                            break;
                        case ROOK:
                            System.out.print(WHITE_ROOK);
                            break;
                        case BISHOP:
                            System.out.print(WHITE_BISHOP);
                            break;
                        case KNIGHT:
                            System.out.print(WHITE_KNIGHT);
                            break;
                        case PAWN:
                            System.out.print(WHITE_PAWN);
                            break;
                        default:
                            System.out.print(EMPTY);
                            break;
                    }
                } else {
                    switch (piece.getPieceType()) {
                        case KING:
                            System.out.print(BLACK_KING);
                            break;
                        case QUEEN:
                            System.out.print(BLACK_QUEEN);
                            break;
                        case ROOK:
                            System.out.print(BLACK_ROOK);
                            break;
                        case BISHOP:
                            System.out.print(BLACK_BISHOP);
                            break;
                        case KNIGHT:
                            System.out.print(BLACK_KNIGHT);
                            break;
                        case PAWN:
                            System.out.print(BLACK_PAWN);
                            break;
                        default:
                            System.out.print(EMPTY);
                            break;
                    }
                }
            }
            printColumnNumber(row, teamColor);
            System.out.print(RESET_BG_COLOR);
            System.out.println();
        }
        printRowLetters(teamColor);
    }

    private static void printColumnNumber(int row, ChessGame.TeamColor teamColor) {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_DARK_GREY);
        if (teamColor == ChessGame.TeamColor.WHITE) {
            System.out.print(" " + (9 - row) + " ");
        } else {
            System.out.print(" " + row + " ");
        }

    }

    private static void printRowLetters(ChessGame.TeamColor teamColor) {
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(SET_BG_COLOR_DARK_GREY);
        String letters = "   \u2002\u2004a\u2003\u2002b\u2003\u2002\u200Ac"
                + "\u2003\u2002d\u2003\u2002\u200Ae\u2003\u2002f"
                + "\u2003\u2002\u200Ag\u2003\u2002\u200Ah\u2002\u2004   ";
        if (teamColor == ChessGame.TeamColor.WHITE) {
            System.out.print(letters);
        } else {
            StringBuilder reversed = new StringBuilder(letters);
            reversed.reverse();
            System.out.print(reversed.toString());
        }

        System.out.print(RESET_BG_COLOR);
        System.out.println();
    }

    public void drawHighlightedChessBoard(ChessBoard chessBoard, ChessGame.TeamColor teamColor, ChessPosition startPosition) {
        ChessGame game = new ChessGame();
        game.setBoard(chessBoard);
        game.setTeamTurn(teamColor);

        // Get the legal moves, then grab the end positions
        Collection<ChessMove> chessMoves = game.validMoves(startPosition);
        Collection<ChessPosition> legalMoves = new ArrayList<>();

        for (ChessMove move : chessMoves) {
            legalMoves.add(move.getEndPosition());
        }

        drawBoard(chessBoard, teamColor, legalMoves);
    }
}
