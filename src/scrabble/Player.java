/*  Andrew Valdez
    Player plays valid words, checks word validity, modifies tray, resets turn.
    Word keeps track of overall score and current word score. */
package scrabble;

import java.util.Collections;
import java.util.LinkedList;

public class Player {
    private LinkedList<Tile> tray = new LinkedList<>();
    private LinkedList<Tile> wordTiles = new LinkedList<>();
    private LinkedList<Square> wordSquares = new LinkedList<>();
    private Trie trie;
    private Board board;
    private int score = 0;

    public Player() {

    }

    public Player(Board board, Trie trie) {
        this.board = board;
        this.trie = trie;
        fillTray();
    }

    // finds "left parts" of word before building onto it
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

    // attempts to play word and return int values according to success:
    // 1: word is not anchored to existing tiles (or center tile for first turn)
    // 2: word or cross word is invalid (also default)
    // 3: the word is legal and played
    public int playWord(Word word) {
        boolean legalWord = trie.traverseTrie(word.getWord());
        if (!word.doesConnect()) {
            return 1;
        } else if (!word.isLegalWord()) {
            return 2;
        } else if (legalWord) {
            score += word.getScoreTotal();
            fillTray();
            wordTiles.clear();
            wordSquares.clear();
            board.setIsEmpty(false);
            return 3;
        } else {
            return 2;
        }
    }

    // places a tile, updating word object with necessary data
    public Word playTile(Word word, int tileIndex, char direction) {
        Tile tile = tray.get(tileIndex);
        Square square = word.getSquare();

        if (direction == 'd') board.transposeBoard();
        square.setTile(tile);
        wordSquares.add(square);
        word.updateWord(tile.getLetter(),tile.getPoints()*square.multiplyLetter(),
                        board.getRightSquare(square), square.multiplyWords());
        wordTiles.add(tile);
        tray.remove(tile);

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

    // Return tiles to tray
    public void undoTurn() {
        for (Tile tile : wordTiles) {
            if (tile.isBlank()) tile.setLetter('_');
        }
        tray.addAll(wordTiles);
        for (Square space : wordSquares) {
            space.setTile(null);
        }
        wordTiles.clear();
        wordSquares.clear();
    }

    // Shuffle tiles in the tray
    public void shuffleTray() { Collections.shuffle(tray); }

    public void fillTray() {
        for (int i = tray.size(); i < board.getTRAY_SIZE(); i++) {
            tray.add(board.drawTile());
        }
    }

    public void setBlank(char letter, int index) {
        if (tray.get(index).isBlank()) tray.get(index).setLetter(letter);
    }

    public int getTrayTilePoints(int i)  { return tray.get(i).getPoints(); }

    public char getTrayTileLetter(int i) {
        return tray.get(i).getLetter();
    }

    public int getTraySize() { return tray.size(); }

    public int getScore() { return score;}
}

// used for building word and collecting word data (scores, validity, etc)
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

    public boolean isEmpty() { return word.equals(""); }
}
