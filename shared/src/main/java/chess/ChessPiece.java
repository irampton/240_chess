package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private PieceType type;
    private ChessGame.TeamColor teamColor;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.type = type;
        this.teamColor = pieceColor;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return this.teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return this.type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();

        switch (this.getPieceType()) {
            case KING:
                for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
                    for (int colOffset = -1; colOffset <= 1; colOffset++) {
                        if (rowOffset == 0 && colOffset == 0){
                            continue;
                        }

                        int newRow = myPosition.getRow() + rowOffset;
                        int newCol = myPosition.getColumn() + colOffset;
                        if (isValidPosition(newRow, newCol)) {
                            ChessPosition newPosition = new ChessPosition(newRow, newCol);
                            ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                            if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                                validMoves.add(new ChessMove(myPosition, newPosition, null));
                            }
                        }
                    }
                }

                break;

            case QUEEN:
                // Moves like a rook or a bishop, can combine code
                addRookMoves(board, myPosition, validMoves);
                addBishopMoves(board, myPosition, validMoves);
                break;

            case ROOK:
                // Rook moves vertically or horizontally
                addRookMoves(board, myPosition, validMoves);
                break;

            case BISHOP:
                // Bishop moves diagonally
                addBishopMoves(board, myPosition, validMoves);
                break;

            case KNIGHT:
                int[][] knightMoves = {
                        {-2, -1}, {-2, 1}, {2, -1}, {2, 1},
                        {-1, -2}, {-1, 2}, {1, -2}, {1, 2}
                };
                for (int[] move : knightMoves) {
                    int newRow = myPosition.getRow() + move[0];
                    int newCol = myPosition.getColumn() + move[1];
                    if (isValidPosition(newRow, newCol)) {
                        ChessPosition newPosition = new ChessPosition(newRow, newCol);
                        ChessPiece pieceAtNewPosition = board.getPiece(newPosition);
                        if (pieceAtNewPosition == null || pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                            validMoves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
                break;

            case PAWN:
                break;

            default:
                throw new UnsupportedOperationException("Unsupported piece type: " + this.getPieceType());
        }

        return validMoves;
    }

    // Helper method to check if a position is valid on the chessboard
    private boolean isValidPosition(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }


    private void addRookMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] directions = {
                {-1, 0}, // up
                {1, 0},  // down
                {0, -1}, // left
                {0, 1}   // right
        };
        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += rowChange;
                col += colChange;

                if (!isValidPosition(row, col)) break;

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null)); // Capture move
                    break;
                } else {
                    break; // Blocked by friendly piece
                }
            }
        }
    }

    private void addBishopMoves(ChessBoard board, ChessPosition myPosition, Collection<ChessMove> validMoves) {
        int[][] directions = {
                {-1, -1}, // top-left
                {-1, 1},  // top-right
                {1, -1},  // bottom-left
                {1, 1}    // bottom-right
        };
        for (int[] direction : directions) {
            int rowChange = direction[0];
            int colChange = direction[1];
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += rowChange;
                col += colChange;

                if (!isValidPosition(row, col)) break;

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece pieceAtNewPosition = board.getPiece(newPosition);

                if (pieceAtNewPosition == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                } else if (pieceAtNewPosition.getTeamColor() != this.getTeamColor()) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null)); // Capture move
                    break;
                } else {
                    break; // Blocked by friendly piece
                }
            }
        }
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return type == that.type && teamColor == that.teamColor;
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, teamColor);
    }
}
