package scrabble;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Board {
    private Square[][] boardSpace;
    Stack<Tile> tileBag = new Stack<>();
    // Can be overridden to be modular
    int[] tileFrequency = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2};
    int[] tilePoints = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0};
    private final int RACK_SIZE = 7;
    private final int ABC_MIN = 97;
    private int boardSize, center;
    private boolean isEmpty = true;

    // Reads in and generates board from user input
    public void readBoard(File file) throws IOException {
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
                    index = letter - ABC_MIN;
                    if (letter < ABC_MIN) {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, 0));
                    } else {
                        boardSpace[i][j] = new Square(1, new Tile((char)letter, tilePoints[index]));
                    }
                } else if (input.charAt(0) == '.') {
                    if (input.charAt(1) == '.') {
                        boardSpace[i][j] = new Square(1, null);
                    } else {
                        boardSpace[i][j] = new Square(input.charAt(1) - 48, null, false);
                    }
                } else {
                    boardSpace[i][j] = new Square(input.charAt(0) - 48, null, true);
                }
            }
        }
    }

    // Creates standard scrabble board
    public void createBoard() throws IOException {
//        readBoard(new File("src/scrabble/standard-board.txt"));
        readBoard(new File("src/scrabble/test.txt"));
    }

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
                    System.out.print(tile.getLetter());
                }
                System.out.print(" ");
            }
            System.out.println();
        }
    }

    public void printSimpleBoard() {
        Tile tile;
        Square square;
        System.out.println("   0  1  2  3  4  5  6  7  8  9 10 11 12 13 14");
        for (int i = 0; i < boardSize; i++) {
            System.out.print(i+" ");
            if (i < 10) System.out.print(" ");
            for (int j = 0; j < boardSize; j++) {
                square = boardSpace[i][j];
                tile = square.getTile();
                if (tile == null) {
                    System.out.print(".");
                } else {
                    System.out.print(tile.printLetter());
                }
                System.out.print("  ");
            }
            System.out.println();
        }
    }

    // Fills bag with standard scrabble tile quantities and point values
    public void fillBag() {
        int freq = 0, pts = 0;
        // Create tiles a-z
        for (char l = 'a'; l <= 'z'; l++) {
            for(int i = 0; i < tileFrequency[freq]; i++) {
                tileBag.add(new Tile(l, tilePoints[pts]));
            }
            pts++;
            freq++;
        }
        // Create blank tiles
        for (int i = 0; i < tileFrequency[freq]; i++) {
            tileBag.add(new Tile('_', tilePoints[pts]));
        }
        Collections.shuffle(tileBag);
    }

    public void placeTile(Tile tile, int row, int col) {
        boardSpace[row][col].setTile(tile);
    }

    public int getRACK_SIZE() { return RACK_SIZE; }

    public int getBoardSize() { return boardSize; }

    public Square getBoardSpace(int i, int j) { return boardSpace[i][j]; }

    public boolean isEmpty() { return isEmpty; }

    public int getCenter() { return center; }
}
