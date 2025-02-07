package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    ChessGame.TeamColor teamColor;
    PieceType pieceType;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        teamColor = pieceColor;
        pieceType = type;
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
        return teamColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return pieceType;
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

        int[][] rockDirections = {
                {1, 0}, {0, 1}, {-1, 0}, {0, -1}
        };
        int[][] bishopDirections = {
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1}
        };

        switch (this.pieceType) {
            case KING:
                int[][] kingMoves = {
                        {-1, 1}, {1, 0}, {1, 1},
                        {0, 1}, {0, -1},
                        {-1, -1}, {-1, 0}, {1, -1},
                };
                addSimpleMoves(board, myPosition, kingMoves, validMoves);
                break;
            case QUEEN:
                addSlideMoves(board, myPosition, bishopDirections, validMoves);
            case ROOK:
                addSlideMoves(board, myPosition, rockDirections, validMoves);
                break;
            case BISHOP:
                addSlideMoves(board, myPosition, bishopDirections, validMoves);
                break;
            case KNIGHT:
                int[][] knightMoves = {
                        {2, 1}, {1, 2}, {-2, 1}, {-1, 2},
                        {2, -1}, {1, -2}, {-2, -1}, {-1, -2}
                };
                addSimpleMoves(board, myPosition, knightMoves, validMoves);
                break;
            case PAWN:
                addPawnMoves(board, myPosition,
                        teamColor == ChessGame.TeamColor.WHITE ? 1 : -1,
                        teamColor == ChessGame.TeamColor.WHITE ? (myPosition.getRow() == 2) : (myPosition.getRow() == 7),
                        validMoves);
                break;
        }

        return validMoves;
    }

    private void addPawnMoves(ChessBoard board, ChessPosition myPosition, int direction, boolean startingPos, Collection<ChessMove> validMoves) {
        ChessPosition newPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn());
        if (insideBoard(newPosition) && board.getPiece(newPosition) == null) {
            addPawnMove(myPosition, newPosition, validMoves);
            if (startingPos) {
                newPosition = new ChessPosition(myPosition.getRow() + 2 * direction, myPosition.getColumn());
                if (insideBoard(newPosition) && board.getPiece(newPosition) == null) {
                    validMoves.add(new ChessMove(myPosition, newPosition, null));
                }
            }
        }


        newPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() + 1);
        if (insideBoard(newPosition) && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != teamColor) {
            addPawnMove(myPosition, newPosition, validMoves);
        }
        newPosition = new ChessPosition(myPosition.getRow() + direction, myPosition.getColumn() - 1);
        if (insideBoard(newPosition) && board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != teamColor) {
            addPawnMove(myPosition, newPosition, validMoves);
        }

    }

    private void addPawnMove(ChessPosition myPosition, ChessPosition newPosition, Collection<ChessMove> validMoves) {
        if (teamColor == ChessGame.TeamColor.WHITE ? newPosition.getRow() == 8 : newPosition.getRow() == 1) {
            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
            validMoves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        } else {
            validMoves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

    private void addSimpleMoves(ChessBoard board, ChessPosition myPosition, int[][] moves, Collection<ChessMove> validMoves) {
        for (int[] move : moves) {
            ChessPosition newPosition = new ChessPosition(myPosition.getRow() + move[0], myPosition.getColumn() + move[1]);
            if (insideBoard(newPosition) && spaceFree(board, newPosition)) {
                validMoves.add(
                        new ChessMove(myPosition, newPosition, null)
                );
            }
        }
    }

    private void addSlideMoves(ChessBoard board, ChessPosition myPosition, int[][] directions, Collection<ChessMove> validMoves) {
        //java.io.PrintStream out = System.out;
        for (int[] direction : directions) {
            boolean valid;
            int count = 1;
            //out.println(direction[0] + " " + direction[1]);
            do {
                valid = false;

                ChessPosition newPosition = new ChessPosition(
                        myPosition.getRow() + direction[0] * count,
                        myPosition.getColumn() + direction[1] * count);

                //out.println(newPosition.toString());

                if (insideBoard(newPosition) && spaceFree(board, newPosition)) {
                    validMoves.add(
                            new ChessMove(myPosition, newPosition, null)
                    );
                    valid = board.getPiece(newPosition) == null;
                }

                count++;
            } while (valid);
        }
    }

    private boolean insideBoard(ChessPosition myPosition) {
        int row = myPosition.getRow();
        int column = myPosition.getColumn();
        return row >= 1 && row <= 8 && column >= 1 && column <= 8;
    }

    private boolean spaceFree(ChessBoard board, ChessPosition myPosition) {
        return board.getPiece(myPosition) == null || board.getPiece(myPosition).getTeamColor() != teamColor;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return teamColor == that.teamColor && pieceType == that.pieceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamColor, pieceType);
    }
}
