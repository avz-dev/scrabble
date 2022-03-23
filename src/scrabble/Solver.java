package scrabble;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Solver {
    private LinkedList<Tile> rack = new LinkedList<>();
    private Board board;
    private Trie trie;
    private int bestScore, bestRow, bestCol;
    private String bestWord;
    private boolean wasTransposed;

    public Solver(Board board, Trie trie) {
        this.board = board;
        this.trie = trie;
        fillRack();
    }

    public Solver(LinkedList<Tile> rack, Board board, Trie trie) {
        this.rack = rack;
        this.board = board;
        this.trie = trie;
    }

    // Finds highest-scoring words for both down and across words
    public void solve() {
        // resets best values each time it is called
        bestScore = bestRow = bestCol = 0;
        bestWord = "";

        board.findAnchors();
        goSolve();
        board.transposeBoard();
        goSolve();
        board.transposeBoard();
        playWord();
        fillRack();
    }

    // Scans board for squares with possible word plays, looks for words on valid squares
    private void goSolve() {
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                if (board.isSquareEmpty(i,j)) {
                    if (inProximity(i,j)) {
                        startSolve("", rack, board.getBoardSpace(i,j));
                    }
                }
            }
        }
    }

    // Checks for existing "left parts" before attempting to build words
    private void startSolve(String word, LinkedList<Tile> tiles, Square square) {
        Square temp = square;
        int score = 0;
        TrieNode node;
        while (board.getLeftSquare(temp) != null && !board.getLeftSquare(temp).isEmpty()) {
            temp = board.getLeftSquare(temp);
            score += temp.getPoints();
            word = temp.getLetter() + word;
        }
        node = trie.findNode(word);
        recursiveSolve(word, node, tiles, score, square, 1, 0,false);
    }

    // Tries all possible letter combinations seeking the highest-scoring word (recursive backtracking)
    public void recursiveSolve(String word, TrieNode node, LinkedList<Tile> tiles, int score,
                               Square square, int wordMultiplier, int crossScore, boolean connects) {
        // Picks up any letters already on board
        while (!square.isEmpty() && node != null) {
            word += square.getLetter();
            node = node.getNode(square.getLetter());
            score += square.getPoints();
            if (!board.isRightNull(square)) square = board.getRightSquare(square);
            else break;
        }

        if (square == board.getBoardSpace(2,2) && word.equals("")) {
            System.out.println();
        }

        // If a word connects to the board and is valid, then the word is tested against current best word
        if (connects && node != null && (tiles.isEmpty() || node.isTerminal())) {
            testWord(node, score*wordMultiplier,tiles,word,board.getLeftSquare(square), crossScore);
        }

        // if the word built so far is valid and the current square is empty, then proceed
        if (node != null && square.isEmpty()) {
            for (Character letter : node.getNodes()) {
                for (Tile tile : tiles) {
                    if (tile.isBlank()) tile.setLetter((char) ((int) letter - 32)); // assign blanks
                    else if (tile.getLetter() != letter) continue;
                    square.setTile(tile);
                    // check for intersecting word validity
                    int crossCheckScore = 0;
                    if (square.isAnchor()) {
                        crossCheckScore = crossCheck(square);
                        if (crossCheckScore != -1) connects = true;
                    }
                    // if there was a valid intersection or none at all, then proceed
                    if (crossCheckScore != -1) {
                        // if playing the current tile results in a valid word, then test it
                        if (node.getNode(letter).isTerminal() && connects &&
                            (board.isRightEmpty(square) || board.isRightNull(square))) {
                            testWord(node.getNode(letter), (score+ square.getPoints())*wordMultiplier,
                                    removeFromRack(tiles,tile), word+tile.getLowercaseLetter(),
                                    square, crossScore+crossCheckScore);
                        }
                        // if there's more space on the board, then keep on going
                        if (!board.isRightNull(square)) {
                            recursiveSolve(word + letter,
                                    node.getNode(letter),
                                    removeFromRack(tiles, tile),
                                    (score + square.getPoints()),
                                    board.getRightSquare(square),
                                    wordMultiplier * square.multiplyWords(),
                                    crossScore+crossCheckScore, connects);
                        }
                    }
                    square.setTile(null); // square reset
                    if (tile.isBlank()) tile.setLetter('_'); // blank tiles reset
                }
            }
        }
    }

    private void testWord(TrieNode node, int score, LinkedList<Tile> tiles, String word, Square square,
                         int crossScore) {
        score += crossScore;
        if (tiles.isEmpty()) {
            score += board.getBONUS();
        }
        if (node.isTerminal() && bestScore <= score) {
            bestScore = score;
            bestWord = word;
            bestRow = square.getRow();
            bestCol = square.getCol();
            wasTransposed = board.isTransposed();
        }
    }

    // checks for intersecting words and verifies them
    private int crossCheck(Square square) {
        int score = 0;
        int wordMultiplier = square.multiplyWords();
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
            return score*wordMultiplier;
        } else {
            return -1;
        }
    }

    // makes a copy of the rack, removes a given tile, and returns the copy
    private LinkedList<Tile> removeFromRack(LinkedList<Tile> temp, Tile tile) {
        LinkedList<Tile> clone = (LinkedList<Tile>) temp.clone();
        clone.remove(tile);
        return clone;
    }

    public void fillRack() {
        for (int i = rack.size(); i < board.getRACK_SIZE(); i++) {
            rack.add(board.drawTile());
        }
    }


    // Determines whether it's possible for a word to connect to a played tile
    private boolean inProximity(int row, int col) {
        for (col = col; col <= board.getRACK_SIZE() + col; col++) {
            if (col >= board.getBoardSize()) break;
            if (board.isSquareAnchor(row, col)) return true;
        }
        return false;
    }

    // Plays the determined best word, removing tiles from the rack and clearing square multipliers
    private void playWord() {
        Square square;
        sortRack();
        int letter = 0;
        String word = "";
        bestCol = bestCol -bestWord.length()+1;

        // if the board was transposed when the best word was found, the board is transposed
        if (wasTransposed) board.transposeBoard();

        for (int i = bestCol; i < bestCol +bestWord.length(); i++) {
            square = board.getBoardSpace(bestRow, i);
            while (!square.isEmpty()) {
                word += square.getLetter();
                square = board.getRightSquare(square);
                letter++;
                i++;
            }
            for (Tile tile : rack) {
                if (tile.getLetter() == '_') tile.setLetter((char)((int)bestWord.charAt(letter)-32));
                if (tile.getLowercaseLetter() == bestWord.charAt(letter)) {
                    word += tile.getLetter();
                    square.setTile(tile);
                    square.clearMultipliers();
                    rack.remove(tile);
                    letter++;
                    break;
                }
            }
        }
        if (wasTransposed) board.transposeBoard();
        bestWord = word;
    }

    // Sorts rack so blank tiles are always at the end
    public void sortRack() {
        Collections.sort(rack, new Comparator<Tile>() {
            @Override
            public int compare(Tile o1, Tile o2) {
                return o2.getLetter() - o1.getLetter();
            }
        });
    }

    // returns current best score
    public int getBestScore() { return bestScore; }

    // returns current best word
    public String getBestWord() { return bestWord; }
}
