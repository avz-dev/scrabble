/*  Andrew Valdez
    SolverMain reads in dictionary and input file, calls
    Solver to find best word, and produces output file. */
package scrabble;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class SolverMain {
    public static void main(String[] args) throws IOException {
        // Reads in dictionary scanner new file (args[0])
        Trie trie = new Trie();
        File dictionary = new File(args[0]);
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            trie.buildTrie(word);
        }

        // Reads in board and tile tray from input file
        Scanner reader = new Scanner(System.in);

        // Loops through input file and solves each board and tray combo
        while (reader.hasNext()) {
            Board board = new Board();
            LinkedList<Tile> tray;
            int boardSize = reader.nextInt();
            String[][] boardArray = new String[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    boardArray[i][j] = reader.next();
                }
            }
            board.readSolverBoard(boardArray,boardSize);

            // Reads in tray and creates a list of tile objects
            String tileString = reader.next();
            tray = board.readTray(tileString);

            // Writes input board and tray to file
            System.out.println("Input Board:");
            board.printBoard();
            System.out.println("Tray: "+tileString);

            // Creates solver and solves board/tray combo
            Solver solver = new Solver(tray, board, trie);
            solver.solve();

            // Writes solution to file
            System.out.println("Solution "+solver.getBestWord()+" has "+ solver.getBestScore()+" points");
            System.out.println("Solution Board:");
            board.printBoard();
            System.out.println();
        }
    }
}
