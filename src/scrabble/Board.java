package scrabble;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;

public class Board {
    private Square[][] boardSpace;
    private Stack<Tile> tileBag = new Stack<>();

    // Standard Scrabble tile frequencies, point values, and rack size
    private final int[] TILE_QTY = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2};
    private final int[] TILE_PTS = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0};
    private final int RACK_SIZE = 7;

    private final int LC_MIN = 97;  // Lowercase minimum

    private int boardSize, center;
    private boolean isEmpty = true;
    private boolean isTransposed = false;

    public Board() { fillBag(); }

    // Reads in and generates board from user input
    public LinkedList<Tile> readBoard(File file) throws IOException {
        Scanner scanner = new Scanner(file);
        boardSize = scanner.nextInt();
        center = boardSize/2;
        int letter, index;
        boardSpace = new Square[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String input = scanner.next();
                if (input.length() == 1) {
                    isEmpty = false;
                    letter = input.charAt(0);
                    index = letter - LC_MIN;
                    if (letter < LC_MIN) {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, 0), false, i, j);
                    } else {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, TILE_PTS[index]), false, i, j);
                    }
                } else if (input.charAt(0) == '.') {
                    if (input.charAt(1) == '.') {
                        boardSpace[i][j] = new Square(1, null, false, i, j);
                    } else {
                        boardSpace[i][j] = new Square(input.charAt(1) - 48, null, false, i, j);
                    }
                } else {
                    boardSpace[i][j] = new Square(input.charAt(0) - 48, null, true, i, j);
                }
            }
        }
        if (scanner.hasNext()) return readRack(scanner.next());
        else return null;
    }

    // takes 2D String array and creates a 2D array of squares
    public void readSolverBoard(String[][] boardArray, int boardSize) {
        this.boardSize = boardSize;
        center = boardSize/2;
        int letter, index;
        boardSpace = new Square[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String square = boardArray[i][j];
                if (square.length() == 1) {
                    isEmpty = false;
                    letter = square.charAt(0);
                    index = letter - LC_MIN;
                    if (letter < LC_MIN) {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, 0), false, i, j);
                    } else {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, TILE_PTS[index]), false, i, j);
                    }
                } else if (square.charAt(0) == '.') {
                    if (square.charAt(1) == '.') {
                        boardSpace[i][j] = new Square(1, null, false, i, j);
                    } else {
                        boardSpace[i][j] = new Square(square.charAt(1) - 48, null, false, i, j);
                    }
                } else {
                    boardSpace[i][j] = new Square(square.charAt(0) - 48, null, true, i, j);
                }
            }
        }
    }

    // Reads rack as string and returns list of appropriate tiles
    public LinkedList<Tile> readRack(String word) {
        LinkedList<Tile> rack = new LinkedList<>();
        for (int i = 0; i < getRACK_SIZE(); i++) {
            if (word.charAt(i) == '*') rack.add(new Tile('_',0)); // Add blank
            else rack.add(new Tile(word.charAt(i), TILE_PTS[word.charAt(i) - LC_MIN]));
        }
        return rack;
    }

    // Creates standard scrabble board
    public void createBoard() throws IOException {
        readBoard(new File("src/scrabble/standard-board.txt"));
    }

    //TODO: Delete, for testing only
    public void printBoard() {
        Tile tile;
        Square square;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                square = boardSpace[i][j];
                tile = square.getTile();
                if (tile == null) {
                    if (square.getMultiplier() == 1) {
                        System.out.print("..");
                    } else if (square.isWordMultiplier()) {
                        System.out.print(square.getMultiplier() + ".");
                    } else {
                        System.out.print("." + square.getMultiplier());
                    }
                } else {
                    System.out.print(" "+tile.getLetter());
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    // Writes board to file
    public void writeBoard(PrintWriter writer) {
        Tile tile;
        Square square;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                square = boardSpace[i][j];
                tile = square.getTile();
                if (tile == null) {
                    if (square.getMultiplier() == 1) {
                        writer.print("..");
                    } else if (square.isWordMultiplier()) {
                        writer.print(square.getMultiplier() + ".");
                    } else {
                        writer.print("." + square.getMultiplier());
                    }
                } else {
                    writer.print(" "+tile.getLetter());
                }
                if (j < boardSize-1) writer.print(" ");
            }
            writer.println();
        }
    }

    //TODO: Delete method, for testing only
    public void printSimpleBoard() {
        Tile tile;
        Square square;
        System.out.print(" ");
        for (int i = 0; i < boardSize; i++) {
            System.out.print("  "+i);
        }
        System.out.println();
        for (int i = 0; i < boardSize; i++) {
            System.out.print(i+" ");
            if (i < 10) System.out.print(" ");
            for (int j = 0; j < boardSize; j++) {
                square = boardSpace[i][j];
                tile = square.getTile();
                if (tile == null) {
                    System.out.print(".");
                } else {
                    System.out.print(tile.getLetter());
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    // Fills bag with standard scrabble tile quantities and point values
    private void fillBag() {
        int freq = 0, pts = 0;
        // Create tiles a-z
        for (char l = 'a'; l <= 'z'; l++) {
            for(int i = 0; i < TILE_QTY[freq]; i++) {
                tileBag.add(new Tile(l, TILE_PTS[pts]));
            }
            pts++;
            freq++;
        }
        // Create blank tiles
        for (int i = 0; i < TILE_QTY[freq]; i++) {
            tileBag.add(new Tile('_', TILE_PTS[pts]));
        }
        Collections.shuffle(tileBag);
    }

    // places a tile on a board square at a specific index
    public void placeTile(Tile tile, int row, int col) { boardSpace[row][col].setTile(tile); }

    // gets random tile from tile bag
    public Tile drawTile() { return tileBag.pop(); }

    // transposes board
    public void transposeBoard() {
        Square[][] transposedBoard = new Square[boardSize][boardSize];
        for(int j = 0; j < boardSize; j++) {
            for(int i = 0; i < boardSize; i++) {
                boardSpace[i][j].transposeSquare();
                transposedBoard[j][i] = boardSpace[i][j];
            }
        }
        boardSpace = transposedBoard;
        isTransposed = !isTransposed;
    }

    // scans board and marks anchor squares
    public void findAnchors() {
        Square square;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                square = boardSpace[i][j];
                if (square.isEmpty() &&
                    ((getRightSquare(square) != null && !getRightSquare(square).isEmpty()) ||
                    (getLeftSquare(square) != null && !getLeftSquare(square).isEmpty()) ||
                    (getTopSquare(square) != null && !getTopSquare(square).isEmpty()) ||
                    (getBottomSquare(square) != null && !getBottomSquare(square).isEmpty()))) {
                    square.setAnchor(true);
                } else {
                    square.setAnchor(false);
                }
            }
        }
    }

    public Square getTopSquare(Square square) {
        if (square.getRow()-1 < 0) return null;
        else return boardSpace[square.getRow()-1][square.getCol()];
    }

    public Square getBottomSquare(Square square) {
        if (square.getRow()+1 >= boardSize) return null;
        else return boardSpace[square.getRow()+1][square.getCol()];
    }

    public Square getRightSquare(Square square) {
        if (square.getCol()+1 >= boardSize) return null;
        else return boardSpace[square.getRow()][square.getCol()+1];
    }

    public Square getLeftSquare(Square square) {
        if (square.getCol()-1 < 0) return null;
        else return boardSpace[square.getRow()][square.getCol()-1];
    }

    public boolean isRightEmpty(Square square) {
        if (getRightSquare(square) == null) return false;
        else return getRightSquare(square).isEmpty();
    }

    public boolean isRightNull(Square square) { return getRightSquare(square) == null; }

    public boolean isTransposed() { return isTransposed; }

    public int getBONUS() { return 50; }

    public int getRACK_SIZE() { return RACK_SIZE; }

    public int getBoardSize() { return boardSize; }

    public boolean isSquareEmpty(int i, int j) { return boardSpace[i][j].isEmpty(); }

    public boolean isSquareAnchor(int i, int j) { return boardSpace[i][j].isAnchor(); }

    public Tile getBoardTile(int i, int j) { return boardSpace[i][j].getTile(); }

    public Square getBoardSpace(int i, int j) { return boardSpace[i][j]; }

    public boolean isEmpty() { return isEmpty; }

    public int getCenter() { return center; }
}
