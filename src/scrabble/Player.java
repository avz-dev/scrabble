package scrabble;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Player {
    LinkedList<Tile> rack = new LinkedList<>();
    LinkedList<Tile> wordTiles = new LinkedList<>();
    LinkedList<Square> wordSquares = new LinkedList<>();
    Trie trie;
    Scanner scnr = new Scanner(System.in);
    Board board;

    public Player(Board board, Trie trie) {
        this.board = board;
        this.trie = trie;
        fillRack();
    }

    public void playTurn() {
        String word = "";
        char direction;
        int row, col;
        int score = 0;
        Square square;

        printRack();
        board.findAnchors();

        // Prompt user for starting square
         do {
            System.out.println("Select starting square:");
            row = scnr.nextInt();
            col = scnr.nextInt();
        } while(!inProximity(row,col));

        // Prompt user for word direction
        System.out.println("Across or Down (a/d):");
        do {
            direction = scnr.next().charAt(0);
        } while (direction != 'a' && direction != 'd');

        square = board.getBoardSpace(row, col);

        if (direction == 'd') board.transposeBoard();

        findPartialWord(square,score,word);

        if (board.isTransposed()) board.transposeBoard();
    }

    private void findPartialWord(Square square, int score, String word) {
        TrieNode node;
        Square initial = square;
        int initialIndex = square.getCol();
        while (board.getLeftSquare(square) != null && !board.getLeftSquare(square).isEmpty()) {
            square = board.getLeftSquare(square);
            score += square.getPoints();
            word = square.getLetter() + word;
        }
        buildWord(word, initial, score, initialIndex);
    }

    private void buildWord(String word, Square square, int score,int initialIndex) {
        int tileIndex, crossScore, wordMultiplier;
        crossScore = 0;
        wordMultiplier = 1;
        Tile tile;
        boolean connects = false, legalWord = true;
        char cont;
        // main turn loop
        do {
            // Prompt user to select a tile
            do {
                printRack();
                System.out.println("Select a tile:");
                tileIndex = scnr.nextInt();
            } while (tileIndex < 0 || tileIndex >= rack.size());

            tile = rack.get(tileIndex);

            // Prompt blank letter selection
            if (tile.isBlank()) {
                char letter;
                System.out.println("Select a letter:");
                do {
                    letter = scnr.next().toLowerCase().charAt(0);
                } while (letter < 97 || letter > 122);
                tile.setLetter(letter);
            }

            square.setTile(tile);
            word += tile.getLetter();
            score += (tile.getPoints()*square.multiplyLetter());
            wordSquares.add(square);
            wordTiles.add(tile);
            rack.remove(tile);
            wordMultiplier *= square.multiplyWords();

            if (square.isAnchor()) {
                crossScore += crossCheck(square);
                connects = true;
                if (crossScore != -1) score += crossScore;
                else legalWord = false;
            }

            square = board.getRightSquare(square);
            if (square == null) break;

            // Add existing board tiles to word
            while (square != null && !square.isEmpty()) {
                word += square.getLetter();
                score += square.getPoints();
                square = board.getRightSquare(square);
                connects = true;
            }

            System.out.println("Word: "+word);

            System.out.println("Continue? (y/n)");
            cont = scnr.next().charAt(0);
        } while (cont == 'y');

        if ((word.length() + initialIndex) > board.getBoardSize()) {
            System.out.println(word+" does not fit on board");
            undoTurn(wordTiles, wordSquares);
        } else if (!trie.traverseTrie(word) || !legalWord) {
            System.out.println("Invalid word");
            undoTurn(wordTiles, wordSquares);
        } else if (!connects) {
            System.out.println("Word must connect to tiles on board.");
            undoTurn(wordTiles, wordSquares);
        } else {
            score*=wordMultiplier;
            fillRack();
            wordTiles.clear();
            wordSquares.clear();
            board.setIsEmpty(false);
            System.out.println("total word score: "+score);
        }
    }

    // checks for intersecting words and verifies them
    private int crossCheck(Square square) {
        int score = 0;
        boolean crossesVertically = false;
        String crossWord = "";

        // goes to start of word
        while (board.getTopSquare(square) != null && !board.getTopSquare(square).isEmpty()) {
            square = board.getTopSquare(square);
            crossesVertically = true;
        }
        // goes from start to end of word, collecting score and building word
        while (board.getBottomSquare(square) != null && !board.getBottomSquare(square).isEmpty()) {
            score += square.getPoints();
            crossWord += square.getLowercaseLetter();
            square = board.getBottomSquare(square);
            crossesVertically = true;
        }
        // adds final tile score and letter
        crossWord += square.getLowercaseLetter();
        score += square.getPoints();

        // if no intersection detected, exit
        if (!crossesVertically) return 0;
        // if the word is in the trie, then return the score. Otherwise, return the error indicator
        if (trie.traverseTrie(crossWord)) {
            return score;
        } else {
            return -1;
        }
    }

    // Determines whether it's possible for a word to connect to a played tile
    private boolean inProximity(int row, int col) {
        for (int j = col; col <= board.getRACK_SIZE() + col; j++) {
            if (j >= board.getBoardSize()) break;
            if (board.isSquareAnchor(row, j)) return true;
        }
        return false;
    }

    public void undoTurn(LinkedList<Tile> wordTiles, LinkedList<Square> wordSquares) {
        for (Tile tile : wordTiles) {
            if (tile.isBlank()) tile.setLetter('_');
        }
        rack.addAll(wordTiles);
        for (Square space : wordSquares) {
            space.setTile(null);
        }
    }

    public void fillRack() {
        for (int i = rack.size(); i < board.getRACK_SIZE(); i++) {
            rack.add(board.drawTile());
        }
    }

    public int sumPoints(LinkedList<Tile> word) {
        int pointSum = 0;
        for (Tile tile : word) {
            pointSum += tile.getPoints();
        }
        return pointSum;
    }

    public void shuffleRack() { Collections.shuffle(rack); }

    public void printRack() {
        for (Tile tile : rack) {
            System.out.print(tile.getLetter()+"  ");
        }
        System.out.println();
    }
}
