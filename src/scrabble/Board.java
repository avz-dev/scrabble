package scrabble;

import java.util.*;

public class Board {
    Square[][] boardSpaces;
    Stack<Tile> tileBag = new Stack<>();
    // Can be overridden to be modular
    int[] tileFrequency = {9, 2, 2, 4, 12, 2, 3, 2, 9, 1, 1, 4, 2, 6, 8, 2, 1, 6, 4, 6, 4, 2, 2, 1, 2, 1, 2};
    int[] tilePoints = {1, 3, 3, 2, 1, 4, 2, 4, 1, 8, 5, 1, 3, 1, 1, 3, 10, 1, 1, 1, 1, 4, 4, 8, 4, 10, 0};
    private final int RACK_SIZE = 7;
    private final int ABC_MIN = 97;
    private int boardSize;

    // Reads in and generates board from user input
    public void readBoard() {
        Scanner scanner = new Scanner(System.in);
        boardSize = scanner.nextInt();
        int letter, index;
        boardSpaces = new Square[boardSize][boardSize];
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                String input = scanner.next();
                if (input.length() == 1) {
                    letter = input.charAt(0);
                    index = letter - ABC_MIN;
                    if (letter < ABC_MIN) {
                        boardSpaces[i][j] = new Square(1, new Tile((char)letter, 0));
                    } else {
                        boardSpaces[i][j] = new Square(1, new Tile((char)letter, tilePoints[index]));
                    }
                } else if (input.charAt(0) == '.') {
                    if (input.charAt(1) == '.') {
                        boardSpaces[i][j] = new Square(1, null);
                    } else {
                        boardSpaces[i][j] = new Square(input.charAt(1) - 48, null, false);
                    }
                } else {
                    boardSpaces[i][j] = new Square(input.charAt(0) - 48, null, true);
                }
            }
        }
    }

    public void printBoard() {
        Tile tile;
        Square square;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                square = boardSpaces[i][j];
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
    public void printBoard1() {
        Tile tile;
        Square square;
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                square = boardSpaces[i][j];
                tile = square.getTile();
                if (tile == null) {
                    if (square.getMultiplier() == 1) {
                        System.out.print(".");
                    } else if (square.isWordMultiplier()) {
                        System.out.print(".");
                    } else {
                        System.out.print("." );
                    }
                } else {
                    System.out.print(tile.getPoints());
                }
                System.out.print(" ");
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

    public int getRACK_SIZE() { return RACK_SIZE; }
}
