package scrabble;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class SolverMain {
// args for test: src/scrabble/twl06.txt src/scrabble/resources/test_solver.txt
    public static void main(String[] args) throws IOException {
        Trie trie = new Trie();
        Board board = new Board();
        LinkedList<Tile> rack;


        // Reads in dictionary scanner new file (args[0])
        File dictionary = new File(args[0]);
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            trie.buildTrie(word);
        }
        File file = new File(args[1]);
        rack = board.readBoard(file);

        Solver solver = new Solver(rack, board, trie);

        for (Tile tile : rack) {
            System.out.print(tile.printLetter()+" ");
        }
        System.out.println();
        board.findAnchors();
        board.transposeBoard();
        board.printSimpleBoard();
        board.transposeBoard();
//        solver.findWord("",trie.getRoot(),rack,0);
        solver.solve();


    }
}
