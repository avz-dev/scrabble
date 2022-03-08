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
        File dictionary = new File(args[0]);
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            trie.buildTrie(word);
        }

        Player player = new Player(board, trie);
        board.createBoard();
        // TODO: change while to check for win condition
        while (true) {
            board.printSimpleBoard();
            player.playTurn();
        }

    }
}
