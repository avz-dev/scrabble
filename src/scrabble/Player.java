package scrabble;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Scanner;

public class Player {
    private LinkedList<Tile> rack = new LinkedList<>();
    private LinkedList<Tile> wordTiles = new LinkedList<>();
    private LinkedList<Square> wordSquares = new LinkedList<>();
    private Trie trie;
    private Scanner scnr = new Scanner(System.in);
    private Board board;
    private int score = 0;

    public Player() {

    }

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

        findPartialWord(square,score,word,direction);

        if (board.isTransposed()) board.transposeBoard();
    }

    public Word findPartialWord(Square square, int score, String word, char direction) {
        Square initial = square;
        if (direction == 'd') board.transposeBoard();
        while (board.getLeftSquare(square) != null && !board.getLeftSquare(square).isEmpty()) {
            square = board.getLeftSquare(square);
            score += square.getPoints();
            word = square.getLetter() + word;
        }
        if (board.isTransposed()) board.transposeBoard();
        return new Word(initial,score,word);
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
            undoTurn();
        } else if (!trie.traverseTrie(word) || !legalWord) {
            System.out.println("Invalid word");
            undoTurn();
        } else if (!connects) {
            System.out.println("Word must connect to tiles on board.");
            undoTurn();
        } else {
            score*=wordMultiplier;
            fillRack();
            wordTiles.clear();
            wordSquares.clear();
            board.setIsEmpty(false);
            System.out.println("total word score: "+score);
        }
    }

    public int playWord(Word word) {
        boolean legalWord = trie.traverseTrie(word.getWord());
        if (!word.doesConnect()) {
            return 1;
        } else if (!word.isLegalWord()) {
            return 2;
        } else if (legalWord) {
            score += word.getScoreTotal();
            fillRack();
            wordTiles.clear();
            wordSquares.clear();
            board.setIsEmpty(false);
            return 3;
        } else {
            return 2;
        }
    }

    public Word playTile(Word word, int tileIndex, char direction) {
        Tile tile = rack.get(tileIndex);
        Square square = word.getSquare();

        if (direction == 'd') board.transposeBoard();
        square.setTile(tile);
        wordSquares.add(square);
        word.updateWord(tile.getLetter(),tile.getPoints()*square.multiplyLetter(),
                        board.getRightSquare(square), square.multiplyWords());
        wordTiles.add(tile);
        rack.remove(tile);

        if (square.isAnchor()) {
            int crossScore = crossCheck(square);
            if (crossScore != -1) {
                word.updateCrossScore(crossScore);
            }
            else word.setLegalWord(false);
            word.setConnects(true);
        }

        word.setSquare(board.getRightSquare(square));
        square = word.getSquare();

        // Add existing board tiles to word
        while (square != null && !square.isEmpty()) {
            word.updateWord(square.getLetter(),square.getPoints(),board.getRightSquare(square), square.multiplyWords());
            word.setConnects(true);
            square = word.getSquare();
        }
        if (board.isTransposed()) board.transposeBoard();
        return word;
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

    public void setBlank(char letter, int index) {
        if (rack.get(index).isBlank()) rack.get(index).setLetter(letter);
    }

    // Determines whether it's possible for a word to connect to a played tile
    private boolean inProximity(int row, int col) {
        for (int j = col; col <= board.getRACK_SIZE() + col; j++) {
            if (j >= board.getBoardSize()) break;
            if (board.isSquareAnchor(row, j)) return true;
        }
        return false;
    }

    public void undoTurn() {
        for (Tile tile : wordTiles) {
            if (tile.isBlank()) tile.setLetter('_');
        }
        rack.addAll(wordTiles);
        for (Square space : wordSquares) {
            space.setTile(null);
        }
        wordTiles.clear();
        wordSquares.clear();
    }

    public void undoMove() {
        if (wordTiles.getLast().isBlank()) wordTiles.getLast().setLetter('_');
        rack.add(wordTiles.getLast());
        wordTiles.remove(wordTiles.getLast());
        wordSquares.getLast().setTile(null);
        wordSquares.remove(wordSquares.getLast());
    }

    public void fillRack() {
        for (int i = rack.size(); i < board.getRACK_SIZE(); i++) {
            rack.add(board.drawTile());
        }
    }

    public int getTilePoints(int i)  { return rack.get(i).getPoints(); }

    public char getTrayTile(int i) {
        return rack.get(i).getLetter();
    }

    public int sumPoints(LinkedList<Tile> word) {
        int pointSum = 0;
        for (Tile tile : word) {
            pointSum += tile.getPoints();
        }
        return pointSum;
    }

    public int getScore() { return score;}
    public int getTraySize() { return rack.size(); }
    public void shuffleRack() { Collections.shuffle(rack); }

    public void printRack() {
        for (Tile tile : rack) {
            System.out.print(tile.getLetter()+"  ");
        }
        System.out.println();
    }
}

class Word {
    private Square square;
    private int score, crossScore;
    private int wordMultiplier = 1;
    private String word;
    private boolean connects = false;
    private boolean legalWord = true;

    public Word(Square square, int score,String word) {
        this.square = square;
        this.score = score;
        this.word = word;
    }

    public int getScoreTotal() {
        return crossScore + (score * wordMultiplier);
    }

    public int getScore() { return score; }

    public String getWord() { return word.toLowerCase(); }

    public Square getSquare() { return square; }

    public void updateWord(char letter, int score, Square square, int wordMultiplier) {
        this.word += letter;
        this.score += score;
        this.square = square;
        this.wordMultiplier *= wordMultiplier;
    }

    public void resetWord() {
        word = "";
        score = crossScore = 0;
        wordMultiplier = 1;
        square = null;
        connects = false;
        legalWord = true;
    }

    public void updateCrossScore(int crossScore) {
        this.crossScore += crossScore;
    }

    public void setConnects(boolean connects) {
        this.connects = connects;
    }

    public void setLegalWord(boolean legalWord) {
        this.legalWord = legalWord;
    }

    public void setSquare(Square square) { this.square = square; }

    public boolean doesConnect() { return connects; }

    public boolean isLegalWord() { return legalWord; }
}
