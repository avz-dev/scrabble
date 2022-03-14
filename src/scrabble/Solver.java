package scrabble;

import java.util.LinkedList;

public class Solver {
    LinkedList<Tile> rack;
    Board board;
    Trie trie;
    int maxScore, maxRow, maxCol;
    private final int WORD_MULTIPLIER = 1;
    String bestWord;

    // TODO: Apply word multiplier
    // TODO: Import cross checks
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
        solveHelper();
        board.transposeBoard();
        solveHelper();
        board.transposeBoard();
        printBestWord();
        board.printSimpleBoard();
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
        findWord(word, node, tiles, score, initSquare, WORD_MULTIPLIER);

    }

    public void findWord(String word, TrieNode node, LinkedList<Tile> tiles, int score, Square square, int wordMultiplier) {
        while (board.getRightSquare(square) != null && !square.isEmpty() && node != null) {
            word += square.getLetter();
            node = node.getNode(square.getLetter());
            score += square.getPoints();
            square = board.getRightSquare(square);
        }

        if (node != null) {
            if (node.isTerminal()) testWord(node, score, tiles, word, square);
            for (Tile tile : tiles) {
                if (tile.getLetter() == '_') {
                    for (Character key : node.childNodes.keySet()) {
                        tile.setLetter(key);
                        if (board.getRightSquare(square) != null) {
                            findWord(word + tile.getLetter(),
                                    node.getNode(tile.getLetter()),
                                    removeFromRack(tiles, tile),
                                    score,
                                    board.getRightSquare(square),
                                    wordMultiplier * square.multiplyWords());
                            tile.setLetter('_');
                        }
                    }
                } else if (node.isNextNode(tile.getLetter()) && square.isEmpty()) {
                    square.setTile(tile);
                    if (board.getRightSquare(square) != null) {
                        findWord(word + tile.getLetter(),
                                node.getNode(tile.getLetter()),
                                removeFromRack(tiles, tile),
                                score + (tile.getPoints() * square.multiplyLetter()),
                                board.getRightSquare(square),
                                wordMultiplier * square.multiplyWords());
                    } else {
                        testWord(node.getNode(tile.getLetter()),
                                (score + (tile.getPoints() * square.multiplyLetter()) * square.multiplyWords()),
                                removeFromRack(tiles, tile),
                                (word + tile.getLetter()),
                                square);
                    }
                    square.setTile(null);
                }
            }
        }
    }

    public void testWord(TrieNode node, int score, LinkedList<Tile> tiles, String word, Square square) {
        if (tiles.isEmpty()) {
            score += board.getBONUS();
        }
        if (node.isTerminal() && maxScore < score) {
            board.printSimpleBoard();
            maxScore = score;
            bestWord = word;
            System.out.println(bestWord+" ("+square.getRow()+","+square.getCol()+")");
        }

    }

    public LinkedList<Tile> removeFromRack(LinkedList<Tile> temp, Tile tile) {
        LinkedList<Tile> clone = (LinkedList<Tile>) temp.clone();
        clone.remove(tile);
        return clone;
    }

    public void printRack(LinkedList<Tile> tiles) {
        for (Tile tile : tiles) {
            System.out.print(tile.printLetter()+" ");
        }
        System.out.println();
    }

    public void printWord(String word) {
        System.out.println(word+" is an option.");
    }

    // Determines whether word can connect to a played tile
    public boolean inProximity(int row, int col) {
        for (col = col; col <= board.getRACK_SIZE(); col++) {
            if (col >= board.getBoardSize()) break;
            if (board.isSquareAnchor(row, col)) return true;
        }
        return false;
    }

    public void printBestWord() {
        System.out.println(bestWord+" @ "+maxScore+" pts!");
    }

    public void playWord() {

    }
}
