package application;

import chess.ChessMatch;
import chess.ChessPiece;
import chess.ChessPosition;
import exceptions.ChessException;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class ProgramTest {
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        ChessMatch chessMatch = new ChessMatch();

        List<ChessPiece> capturedList = new ArrayList<>();

        while (true){//TEST
            try {
                UI.clearScreen();

                UI.printMatch(chessMatch, capturedList);

                System.out.print("Source: ");
                ChessPosition source = UI.readChessPosition(sc);
                System.out.println();

                System.out.print("Target: ");
                ChessPosition target = UI.readChessPosition(sc);

                boolean[][] possibleMoves = chessMatch.possibleMoves(source);

                UI.clearScreen();
                UI.printBoard(chessMatch.getPiece(), possibleMoves);

                ChessPiece capturedPiece = chessMatch.performChessMove(source, target);

                if (capturedPiece != null){
                    capturedList.add(capturedPiece);
                }

            } catch (ChessException e ){
                System.out.println(e.getMessage());
                sc.nextLine();
            } catch (InputMismatchException e){
                System.out.println(e.getMessage());
                sc.nextLine();
            }
        }
    }
}