package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;
import exceptions.ChessException;

import java.util.ArrayList;
import java.util.List;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;

    private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
    private List<ChessPiece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        this.board = new Board(8,8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }
    public ChessPiece[][] getPiece(){
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getCollumns()];
        for (int i = 0; i < board.getRows(); i++){
            for (int j = 0; j < board.getCollumns(); j++){
                mat[i][j] = (ChessPiece) board.piece(i, j);
            }
        }
        return mat;
    }
    public boolean[][] possibleMoves(ChessPosition sourcePosition){
        Position position = sourcePosition.toPosition();
        validateSourcePosition(position);
        return board.piece(position).possibleMoves();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition ){
        Position source = sourcePosition.toPosition();
        Position target = targetPosition.toPosition();
        validateSourcePosition(source);
        validateTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);
        nextTurn();
        return (ChessPiece) capturedPiece;
    }
    private Piece makeMove(Position source, Position target){
        Piece p = board.removePiece(source);
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add((ChessPiece) capturedPiece);
        }
        return capturedPiece;
    }
    private void validateSourcePosition(Position position){
        if (!board.thereIsAPiece(position)){
            throw new ChessException("There is no piece on the position");
        }
        if (currentPlayer != ((ChessPiece)board.piece(position)).getColor()){
            throw new ChessException("The chosen piece is invalid");
        }
        if (!board.piece(position).isThereAnyPossibleMoves()){
            throw new ChessException("There is no exists move for the chosen piece");
        }
    }
    private void validateTargetPosition(Position source, Position target){
        if (!board.piece(source).possibleMove(target)){
            throw new ChessException("The piece can't move to target position");
        }
    }
    private void nextTurn(){
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    private void placeNewPiece(char collumn, int row, ChessPiece piece){
        board.placePiece(piece, new ChessPosition(collumn, row).toPosition());
        piecesOnTheBoard.add(piece);
    }
    private void initialSetup(){
        placeNewPiece('b', 6, new Rook(board, Color.WHITE));
        placeNewPiece('e', 8, new King(board, Color.BLACK));
        placeNewPiece('e', 1, new King(board, Color.BLACK));
        placeNewPiece('b', 4, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new King(board, Color.BLACK));
        placeNewPiece('g', 3, new King(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.WHITE));
    }
    public int getTurn(){
        return turn;
    }
    public Color getCurrentPlayer(){
        return currentPlayer;
    }
}
