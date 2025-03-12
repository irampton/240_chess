package model;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import com.google.gson.*;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ChessGameDeserializer implements JsonDeserializer<ChessGame> {
    @Override
    public ChessGame deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        ChessGame game = new ChessGame();

        // Deserialize the chessBoard
        JsonObject chessBoardObject = jsonObject.getAsJsonObject("chessBoard");
        JsonObject boardObject = chessBoardObject.getAsJsonObject("board");

        // Create a map to store the parsed board entries
        Map<ChessPosition, ChessPiece> board = new HashMap<>();

        // Iterate over the entries in the "board" object and clean up the coordinate keys
        for (Map.Entry<String, JsonElement> entry : boardObject.entrySet()) {
            String key = entry.getKey();

            // Clean up the key to create a ChessPosition object
            // Remove the curly braces and split by comma (e.g., "{8,7}" => "8,7")
            String cleanedKey = key.replace("{", "").replace("}", "");
            String[] coordinates = cleanedKey.split(",");
            int row = Integer.parseInt(coordinates[0].trim());
            int column = Integer.parseInt(coordinates[1].trim());
            ChessPosition position = new ChessPosition(row, column);

            // Deserialize the chess piece data
            ChessPiece piece = context.deserialize(entry.getValue(), ChessPiece.class);

            // Add to the board map
            board.put(position, piece);
        }

        ChessBoard newBoard = new ChessBoard();
        newBoard.setBoard(board);
        game.setBoard(newBoard);

        game.setTeamTurn(Objects.equals(jsonObject.get("currentTeam").getAsString(), "WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK);

        return game;
    }
}
