package boardgame;

public class Board {
    private int rows;
    private int collumns;
    private Piece piece[][];

    public Board(int rows, int collumns) {
        this.rows = rows;
        this.collumns = collumns;
        piece = new Piece[rows][collumns];
    }

    public int getRows() {
        return rows;
    }
    public void setRows(int rows) {
        this.rows = rows;
    }
    public int getCollumns() {
        return collumns;
    }
    public void setCollumns(int collumns) {
        this.collumns = collumns;
    }

    public Piece piece(int row, int collum){
        return piece[row][collum];
    }
    public Piece piece(Position position){
        return piece[position.getRow()][position.getCollum()];

    }

}