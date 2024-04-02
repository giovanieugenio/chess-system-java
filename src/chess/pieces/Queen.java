package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {
    public Queen(Board board, Color color) {
        super(board, color);
    }
    @Override
    public String toString() {
        return " Q ";
    }
    @Override
    public boolean[][] possibleMoves() {
        boolean[][] mat = new boolean[getBoard().getRows()][getBoard().getCollumns()];
        Position p = new Position(0,0);
        p.setValues(position.getRow() - 1, position.getCollum());
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setRow(p.getRow() - 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        // move left
        p.setValues(position.getRow() - 1, position.getCollum() - 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setCollum(p.getCollum() - 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //move right
        p.setValues(position.getRow() - 1, position.getCollum() + 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setCollum(p.getCollum() + 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //move below
        p.setValues(position.getRow() + 1, position.getCollum());
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setRow(p.getRow() + 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //NW
        p.setValues(position.getRow() - 1, position.getCollum() - 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setValues(p.getRow() - 1, p.getCollum() - 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //NE
        p.setValues(position.getRow() - 1, position.getCollum() + 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setValues(p.getRow() - 1, p.getCollum() + 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //SE
        p.setValues(position.getRow() + 1, position.getCollum() + 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setValues(p.getRow() + 1, p.getCollum() + 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        //SW
        p.setValues(position.getRow() + 1, position.getCollum() - 1);
        while (getBoard().positionExists(p) && !getBoard().thereIsAPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
            p.setValues(p.getRow() + 1, p.getCollum() - 1);
        }
        if (getBoard().positionExists(p) && isThereOpponentPiece(p)){
            mat[p.getRow()][p.getCollum()] = true;
        }
        return mat;
    }
}
