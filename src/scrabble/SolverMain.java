package scrabble;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.Scanner;

public class SolverMain {
// args for test: src/scrabble/twl06.txt src/scrabble/resources/test_solver.txt
    public static void main(String[] args) throws IOException {
        Trie trie = new Trie();

        // Reads in dictionary scanner new file (args[0])
        File dictionary = new File(args[0]);
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            trie.buildTrie(word);
        }

        // Reads in board and tile rack from input file
        File file = new File(args[1]);
        Scanner reader = new Scanner(file);
        PrintWriter writer = new PrintWriter(args[2], "UTF-8");
        while (reader.hasNext()) {
            Board board = new Board();
            LinkedList<Tile> rack;
            int boardSize = reader.nextInt();
            String[][] boardArray = new String[boardSize][boardSize];
            for (int i = 0; i < boardSize; i++) {
                for (int j = 0; j < boardSize; j++) {
                    boardArray[i][j] = reader.next();
                }
            }
            String tileString = reader.next();
            board.readSolverBoard(boardArray,boardSize);
            rack = board.readRack(tileString);
            writer.println("Input Board:");
            board.writeBoard(writer);
            writer.println("Tray: "+tileString);
            Solver solver = new Solver(rack, board, trie);
            solver.solve();
            writer.println("Solution "+solver.bestWord+" has "+ solver.maxScore+" points");
            writer.println("Solution Board:");
            board.writeBoard(writer);
            writer.println();
        }
        writer.close();
    }
}
