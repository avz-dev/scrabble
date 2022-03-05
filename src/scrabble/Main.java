package scrabble;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        Trie trie = new Trie();
        Board board = new Board();
        board.fillBag();

        // Reads in dictionary scanner new file (args[0])
        File dictionary = new File("src/scrabble/lexicon.txt");
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            trie.buildTrie(word);
        }

        Player player = new Player(board, trie);

        board.readBoard();
        board.printBoard1();
    }
}
