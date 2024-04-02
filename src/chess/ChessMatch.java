package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.*;
import exceptions.ChessException;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {
    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;
    private boolean checkMate;
    private ChessPiece enPassantVulnerable;
    private ChessPiece promoted;

    private List<ChessPiece> piecesOnTheBoard = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<Piece>();

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
        if (testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }
        ChessPiece movedPiece = (ChessPiece) board.piece(target);

        //special move promotion
        promoted = null;
        if (movedPiece instanceof Pawn){
            if (movedPiece.getColor() == Color.WHITE && target.getRow() == 0 || movedPiece.getColor() == Color.BLACK && target.getRow() == 7){
                promoted = (ChessPiece) board.piece(target);
                promoted = replcePromotedPiece("Q");
            }
        }
        check = (testCheck(opponent(currentPlayer))) ? true : false;
        if (testCheckMate(opponent(currentPlayer))){
            checkMate = true;
        }else {
            nextTurn();
        }

        //special move enPassant
        if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)){
            enPassantVulnerable = movedPiece;
        } else {
            enPassantVulnerable = null;
        }
        return (ChessPiece) capturedPiece;
    }
    public ChessPiece replcePromotedPiece(String type){
       if (promoted == null){
           throw new IllegalStateException("There is no piece to be promoted");
       }
       if (!type.equals("B") && !type.equals("N") && !type.equals("R") & !type.equals("Q")){
           throw new InvalidParameterException("Invalid type for promotion");
       }
       Position pos = promoted.getChessPosition().toPosition();
       Piece p = board.removePiece(pos);
       piecesOnTheBoard.remove(p);
       ChessPiece newPiece = newPiece(type, promoted.getColor());
       board.placePiece(newPiece, pos);
       piecesOnTheBoard.add(newPiece);
       return newPiece;
    }
    private ChessPiece newPiece(String type, Color color){
        if (type.equals("B")) return new Bishop(board, color);
        if (type.equals("N")) return new Knight(board, color);
        if (type.equals("R")) return new Rook(board, color);
        if (type.equals("Q")) return new Queen(board, color);
    }

    private Piece makeMove(Position source, Position target){
        ChessPiece p = (ChessPiece) board.removePiece(source);
        p.increaseMoveCount();
        Piece capturedPiece = board.removePiece(target);
        board.placePiece(p, target);
        if (capturedPiece != null){
            piecesOnTheBoard.remove(capturedPiece);
            capturedPieces.add((ChessPiece) capturedPiece);
        }

        //specialmove castlgin kingside rook
        if (p instanceof King && target.getCollum() == source.getCollum() + 2){
            Position specialSource = new Position(source.getRow(), source.getCollum() + 3);
            Position specialTarget = new Position(source.getRow(), source.getCollum() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(specialSource);
            board.placePiece(rook, specialTarget);
            rook.increaseMoveCount();
        }
        //specialmove castlgin queenside rook
        if (p instanceof King && target.getCollum() == source.getCollum() + 2){
            Position specialSource = new Position(source.getRow(), source.getCollum() - 4);
            Position specialTarget = new Position(source.getRow(), source.getCollum() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(specialSource);
            board.placePiece(rook, specialTarget);
            rook.increaseMoveCount();

            //specialmove en passnt
            if (p instanceof Pawn){
                if (source.getCollum() != target.getCollum() && capturedPiece == null){
                    Position pawnPosition;
                    if (p.getColor() == Color.WHITE){
                        pawnPosition = new Position(target.getRow() + 1, target.getCollum());
                    } else {
                        pawnPosition = new Position(target.getRow() - 1, target.getCollum());
                    }
                    capturedPiece = board.removePiece(pawnPosition);
                    capturedPieces.add(capturedPiece);
                    piecesOnTheBoard.remove(capturedPiece);
                }
            }
        }
        return capturedPiece;
    }
    private void undoMove(Position source, Position target, Piece capturedPiece){
        ChessPiece p = (ChessPiece) board.removePiece(target);
        p.decreaseMoveCount();
        board.placePiece(p, source);
        if (capturedPiece != null){
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBoard.add((ChessPiece) capturedPiece);
        }
        //specialmove castlgin kingside rook
        if (p instanceof King && target.getCollum() == source.getCollum() + 2){
            Position specialSource = new Position(source.getRow(), source.getCollum() + 3);
            Position specialTarget = new Position(source.getRow(), source.getCollum() + 1);
            ChessPiece rook = (ChessPiece) board.removePiece(specialTarget);
            board.placePiece(rook, specialSource);
            rook.increaseMoveCount();
        }
        //specialmove castlgin queenside rook
        if (p instanceof King && target.getCollum() == source.getCollum() + 2){
            Position specialSource = new Position(source.getRow(), source.getCollum() - 4);
            Position specialTarget = new Position(source.getRow(), source.getCollum() - 1);
            ChessPiece rook = (ChessPiece) board.removePiece(specialTarget);
            board.placePiece(rook, specialSource);
            rook.decreaseMoveCount();
        }
        //specialmove en passnt
        if (p instanceof Pawn){
            if (source.getCollum() != target.getCollum() && capturedPiece == enPassantVulnerable){
                ChessPiece pawn = (ChessPiece) board.removePiece(target);
                Position pawnPosition;
                if (p.getColor() == Color.WHITE){
                    pawnPosition = new Position(3, target.getCollum());
                } else {
                    pawnPosition = new Position(4, target.getCollum());
                }
                board.placePiece(pawn, pawnPosition);
                capturedPiece = board.removePiece(pawnPosition);
                capturedPieces.add(capturedPiece);
                piecesOnTheBoard.remove(capturedPiece);
            }
        }
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
    private Color opponent(Color color){
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }
    private ChessPiece king(Color color){
        List<Piece> pieceList = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
        for (Piece p : pieceList){
            if (p instanceof King){
                return (ChessPiece) p;
            }
        }
        throw new IllegalStateException("There is no color "+color+"king on the board");
    }
    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPosition();
        List<Piece> opponentPieceList = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieceList){
            boolean[][] mat = p.possibleMoves();
            if (mat[kingPosition.getRow()][kingPosition.getCollum()]){
                return true;
            }
        }
        return false;
    }
    private boolean testCheckMate(Color color){
        if (!testCheck(color)){
            return false;
        }
        List<Piece> pieceList = piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : pieceList){
            boolean[][] mat = p.possibleMoves();
            for (int i = 0; i < board.getRows(); i++){
                for (int j = 0; i < board.getCollumns(); j++){
                    if (mat[i][j]){
                        Position source = ((ChessPiece)p).getChessPosition().toPosition();
                        Position target = new Position(i, j);
                        Piece capturePiece = makeMove(source, target);
                        boolean testCheck = testCheck(color);
                        undoMove(source, target, capturePiece);
                        if (!testCheck){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
    private void initialSetup(){
        placeNewPiece('a', 1, new Rook(board, Color.WHITE));
        placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
    }
    public int getTurn(){
        return turn;
    }
    public Color getCurrentPlayer(){
        return currentPlayer;
    }
    public boolean getCheck(){
        return check;
    }
    public boolean getCheckMate(){
        return checkMate;
    }
    public ChessPiece getEnPassantVulnerable(){
        return enPassantVulnerable;
    }
    public ChessPiece getPromoted(){
        return promoted;
    }
}
