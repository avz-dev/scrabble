package scrabble;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

public class Solver {
    LinkedList<Tile> rack;
    Board board;
    Trie trie;
    int maxScore, maxRow, maxCol;
    private final int WORD_MULTIPLIER = 1;
    String bestWord;
    boolean wasBestWordBoardTransposed;

    // TODO: Boy is this solver WACK

    public Solver(LinkedList<Tile> rack, Board board, Trie trie) {
        this.rack = rack;
        this.board = board;
        this.trie = trie;
    }

    public void solve() {
        maxScore = 0;
        bestWord = "";
        maxRow = maxCol = 0;

        board.findAnchors();
        solveHelper();
        board.transposeBoard();
        solveHelper();
        board.transposeBoard();
        playWord();
    }

    public void solveHelper() {
        for (int i = 0; i < board.getBoardSize(); i++) {
            for (int j = 0; j < board.getBoardSize(); j++) {
                if (board.isSquareEmpty(i,j)) {
                    if (inProximity(i,j)) {
                        createWord("", rack, 0, board.getBoardSpace(i,j));
                    }
                }
            }
        }
    }

    public void createWord(String word, LinkedList<Tile> tiles, int score, Square square) {
        Square initSquare = square;
        TrieNode node;
        while (board.getLeftSquare(square) != null && !board.getLeftSquare(square).isEmpty()) {
            square = board.getLeftSquare(square);
            score += square.getPoints();
            word = square.getLetter() + word;
        }
        node = trie.findNode(word);
        findWord(word, node, tiles, score, initSquare, WORD_MULTIPLIER, 0,false);

    }

    public void findWord(String word, TrieNode node, LinkedList<Tile>
                        tiles, int score, Square square, int wordMultiplier, int crossScore, boolean connects) {
        while (!square.isEmpty() && node != null) {
            word += square.getLetter();
            node = node.getNode(square.getLetter());
            score += square.getPoints();
            if (!board.isRightNull(square)) square = board.getRightSquare(square);
            else break;
        }

        if (connects && node != null && (tiles.isEmpty() || node.isTerminal())) {
            testWord(node, score*wordMultiplier,tiles,word,board.getLeftSquare(square), crossScore);
        }

        if (node != null && square != null && square.isEmpty()) {
            for (Character letter : node.childNodes.keySet()) {
                for (Tile tile : tiles) {
                    if (tile.isBlank()) tile.setLetter((char) ((int) letter - 32));
                    else if (tile.getLetter() != letter) continue;
                    square.setTile(tile);
                    int crossCheckScore = 0;
                    if (square.isAnchor()) {
                        crossCheckScore = crossCheck(square);
                        if (crossCheckScore != -1) connects = true;
                    }
                    if (crossCheckScore != -1) {
                        if (node.getNode(letter).isTerminal() && connects &&
                            (board.isRightEmpty(square) || board.isRightNull(square))) {
                            testWord(node.getNode(letter), (score+ square.getPoints())*wordMultiplier,
                                    removeFromRack(tiles,tile), word+tile.getLowercaseLetter(),
                                    square, crossScore+crossCheckScore);
                        }
                        if (!board.isRightNull(square)) {
                            findWord(word + letter,
                                    node.getNode(letter),
                                    removeFromRack(tiles, tile),
                                    (score + square.getPoints()),
                                    board.getRightSquare(square),
                                    wordMultiplier * square.multiplyWords(),
                                    crossScore+crossCheckScore, connects);
                        }
                    }
                    square.setTile(null);
                    if (tile.isBlank()) tile.setLetter('_');
                }
            }
        }
    }

    public void testWord(TrieNode node, int score, LinkedList<Tile> tiles, String word, Square square,
                         int crossScore) {
        score += crossScore;
        if (tiles.isEmpty()) {
            score += board.getBONUS();
        }
        if (node.isTerminal() && maxScore < score) {
            maxScore = score;
            bestWord = word;
            maxRow = square.getRow();
            maxCol = square.getCol();
            wasBestWordBoardTransposed = board.isTransposed();
        }
    }

    public int crossCheck(Square square) {
        int score = 0;
        boolean crossesVertically = false;
        String crossWord = "";

        while (board.getTopSquare(square) != null && !board.getTopSquare(square).isEmpty()) {
            square = board.getTopSquare(square);
            crossesVertically = true;
        }
        while (board.getBottomSquare(square) != null && !board.getBottomSquare(square).isEmpty()) {
            score += square.getPoints();
            crossWord += square.getLowercaseLetter();
            square = board.getBottomSquare(square);
            crossesVertically = true;
        }

        crossWord += square.getLowercaseLetter();
        score += square.getPoints();

        if (!crossesVertically) return 0;
        if (trie.traverseTrie(crossWord)) {
            return score;
        } else {
            return -1;
        }
    }

    public LinkedList<Tile> removeFromRack(LinkedList<Tile> temp, Tile tile) {
        LinkedList<Tile> clone = (LinkedList<Tile>) temp.clone();
        clone.remove(tile);
        return clone;
    }

    public void printRack(LinkedList<Tile> tiles) {
        for (Tile tile : tiles) {
            System.out.print(tile.getLetter()+" ");
        }
        System.out.println();
    }

    public void printWord(String word) {
        System.out.println(word+" is an option.");
    }

    // Determines whether word can connect to a played tile
    public boolean inProximity(int row, int col) {
        for (col = col; col <= board.getRACK_SIZE() + col; col++) {
            if (col >= board.getBoardSize()) break;
            if (board.isSquareAnchor(row, col)) return true;
        }
        return false;
    }

    public void printBestWord() {
        System.out.println(bestWord+" @ "+maxScore+" pts!");
    }

    public void playWord() {
        Square square;
        sortRack();
        int letter = 0;
        String word = "";
        maxCol = maxCol-bestWord.length()+1;
        if (wasBestWordBoardTransposed) {
            board.transposeBoard();
        }
        for (int i = maxCol; i < maxCol+bestWord.length(); i++) {
            square = board.getBoardSpace(maxRow, i);
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
        if (wasBestWordBoardTransposed) {
            board.transposeBoard();
        }
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
}
