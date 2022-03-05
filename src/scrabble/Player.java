package scrabble;

import java.util.LinkedList;
import java.util.Scanner;

public class Player {
    LinkedList<Tile> rack = new LinkedList<>();
    Trie trie;
    Scanner scnr = new Scanner(System.in);
    Board board;

    public Player(Board board, Trie trie) {
        this.board = board;
        this.trie = trie;
        fillRack();
    }

    public void playTurn() {
        LinkedList<Tile> wordTiles = new LinkedList<>();
        String word;
        char letter;
        boolean tileInRack = false;

        do {
            printRack(rack);
            word = scnr.next();

            if (word.length() > rack.size()) {
                word = null;
            } else {
                for (int i = 0; i < word.length(); i++) {
                    letter = word.charAt(i);
                    for (Tile tile : rack) {
                        if (tile.getLetter() == letter) {
                            wordTiles.add(tile);
                            rack.remove(tile);
                            tileInRack = true;
                            break;
                        }
                    }
                    if (!tileInRack) {
                        word = null;
                        rack.addAll(wordTiles);
                        break;
                    }
                }
            }
        } while (word == null);

        if (!trie.traverseTrie(word)) {
            rack.addAll(wordTiles);
        } else {
            System.out.println(sumPoints(wordTiles));
        }
        System.out.println(trie.traverseTrie(word));
    }

    public void fillRack() {
        for (int i = rack.size(); i < board.getRACK_SIZE(); i++) {
            rack.add(board.tileBag.pop());
        }
    }

    public int sumPoints(LinkedList<Tile> word) {
        int pointSum = 0;
        for (Tile tile : word) {
            pointSum += tile.getPoints();
        }
        return pointSum;
    }

    public void printRack(LinkedList<Tile> rack) {
        for (Tile tile : rack) {
            System.out.print(tile.getLetter()+"  ");
        }
        System.out.println();
    }
}
