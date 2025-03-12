package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor currentTeam;
    private ChessBoard chessBoard = new ChessBoard();

    public ChessGame() {
        currentTeam = TeamColor.WHITE;
        chessBoard.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return currentTeam;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        currentTeam = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece piece = chessBoard.getPiece(startPosition);
        Collection<ChessMove> allMoves = piece.pieceMoves(chessBoard, startPosition);
        Collection<ChessMove> finalMoves = new ArrayList<>();

        allMoves.forEach(move -> {
            if (!moveCausesCheck(chessBoard, move)) {
                finalMoves.add(move);
            }
        });

        return finalMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to preform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece piece = chessBoard.getPiece(move.getStartPosition());
        ChessPiece endPiece = chessBoard.getPiece(move.getEndPosition());

        if (endPiece != null) {
            if (endPiece.getTeamColor() == piece.getTeamColor()) {
                throw new InvalidMoveException("Can't capture own piece");
            }
        }
        if (piece == null) {
            throw new InvalidMoveException("No existing piece found");
        } else {
            if (piece.getTeamColor() != currentTeam) {
                throw new InvalidMoveException("It's not your turn");
            }
            Collection<ChessMove> validMoves = validMoves(move.getStartPosition());
            if (!validMoves.contains(move)) {
                throw new InvalidMoveException("Move is not legal");
            }
        }


        chessBoard.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() != null) {
            chessBoard.addPiece(move.getEndPosition(), new ChessPiece(piece.teamColor, move.getPromotionPiece()));
        } else {
            chessBoard.addPiece(move.getEndPosition(), piece);
        }

        if (isInCheck(piece.getTeamColor())) {
            throw new InvalidMoveException("Can't move into check");
        }

        currentTeam = currentTeam == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        return boardInCheck(chessBoard, teamColor);
    }

    private boolean boardInCheck(ChessBoard board, TeamColor teamColor) {
        Collection<ChessMove> allEnemyMoves = new ArrayList<>();
        ChessPosition kingPos = null;
        for (int row = 8; row >= 1; row--) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(position);
                if (piece != null) {
                    if (piece.getTeamColor() != teamColor) {
                        allEnemyMoves.addAll(piece.pieceMoves(board, position));
                    } else if (piece.getPieceType() == ChessPiece.PieceType.KING) {
                        kingPos = position;
                    }
                }
            }
        }

        AtomicBoolean check = new AtomicBoolean(false);
        ChessPosition finalKingPos = kingPos;
        allEnemyMoves.forEach(move -> {
            if (move.getEndPosition().equals(finalKingPos)) {
                check.set(true);
            }
        });

        return check.get();
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        for (int row = 8; row >= 1; row--) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor) {
                    if (!allMovesInCheck(piece, position)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (boardInCheck(chessBoard, teamColor)) {
            return false;
        }

        for (int row = 8; row >= 1; row--) {
            for (int column = 1; column <= 8; column++) {
                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = chessBoard.getPiece(position);
                if (piece != null && piece.getTeamColor() == teamColor && !validMoves(position).isEmpty()) {
                    //todo - probs validate for check and such
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        chessBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return chessBoard;
    }

    private boolean moveCausesCheck(ChessBoard board, ChessMove move) {
        ChessPiece originalStartPiece = board.getPiece(move.getStartPosition());
        ChessPiece originalEndPiece = board.getPiece(move.getEndPosition());

        ChessPiece piece = board.getPiece(move.getStartPosition());

        board.addPiece(move.getStartPosition(), null);
        board.addPiece(move.getEndPosition(), piece);

        boolean boardInCheck = boardInCheck(board, piece.getTeamColor());

        board.addPiece(move.getStartPosition(), originalStartPiece);
        board.addPiece(move.getEndPosition(), originalEndPiece);

        return boardInCheck;
    }

    private boolean allMovesInCheck(ChessPiece piece, ChessPosition position) {
        Collection<ChessMove> moves = piece.pieceMoves(chessBoard, position);
        AtomicBoolean allMovesInCheckBool = new AtomicBoolean(true);
        moves.forEach(move -> {
            if (!moveCausesCheck(chessBoard, move)) {
                allMovesInCheckBool.set(false);
            }
        });

        return allMovesInCheckBool.get();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return currentTeam == chessGame.currentTeam && Objects.equals(chessBoard, chessGame.chessBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(currentTeam, chessBoard);
    }

    @Override
    public String toString() {
        return "Current Team: " + currentTeam + "\n" + chessBoard.toString();
    }
}
