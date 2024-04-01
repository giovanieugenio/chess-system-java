package boardgame;

import exceptions.BoardException;

public class Board {
    private int rows;
    private int collumns;
    private Piece piece[][];

    public Board(int rows, int collumns) {
        if (rows < 1 || collumns < 1){
            throw new BoardException("Error create board! ");
        }
        this.rows = rows;
        this.collumns = collumns;
        piece = new Piece[rows][collumns];
    }

    public int getRows() {
        return rows;
    }
    public int getCollumns() {
        return collumns;
    }
    public Piece piece(int row, int collum){
        if (!positionExists(row, collum)){
            throw new BoardException("Position not found");
        }
        return piece[row][collum];
    }
    public Piece piece(Position position){
        if (!positionExists(position)){
            throw new BoardException("Position not found");
        }
        return piece[position.getRow()][position.getCollum()];
    }
    public void placePiece(Piece pieces, Position position){
        if (thereIsAPiece(position)) {
            throw new BoardException("There is already a on position");
        }
        piece[position.getRow()][position.getCollum()] = pieces;
        pieces.position = position;
    }
    public Piece removePiece(Position position){
        if (!positionExists(position)){
            throw new BoardException("Position not found");
        }
        if (piece(position) == null){
            return null;
        }
        Piece aux = piece(position);
        aux.position = null;
        piece[position.getRow()][position.getCollum()] = null;
        return aux;
    }
    
    public boolean positionExists(int row, int collum){
        return row >= 0 && row < getRows() && collum >= 0 && collum < getCollumns();
    }
    public boolean positionExists(Position position){
        return positionExists(position.getRow(), position.getCollum());
    }
    public boolean thereIsAPiece(Position position){
        if (!positionExists(position)){
            throw new BoardException("Position not found");
        }
        return piece(position) != null;
    }
}