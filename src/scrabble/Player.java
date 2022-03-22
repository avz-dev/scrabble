package scrabble;

import java.util.Collections;
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
        LinkedList<Square> wordSquares = new LinkedList<>();
        String word = "";
        Tile tile;
        boolean connects = false;
        char direction, cont;
        int tileIndex, row, col, boardIndex, startCol;
        int score = 0, wordMultiplier = 1;
        Square square;
        printRack();

        // TODO: Investigate wonky point counts
        // Prompt user for starting square
        do {
            System.out.println("Select starting square:");
            row = scnr.nextInt();
            col = scnr.nextInt();
            if (board.isEmpty()) {
                if (row + board.getRACK_SIZE() >= board.getCenter()
                    || col + board.getRACK_SIZE() >= board.getCenter()) {
                    System.out.println("First move must pass through center of board.");
                    continue;
                } else {
                    connects = true;
                }
            }
        } while (row > board.getBoardSize() || col > board.getBoardSize() || row < 0 || col < 0
                || board.getBoardTile(row,col) != null);

        square = board.getBoardSpace(row, col);
        startCol = col;

        // Prompt user for word direction
        System.out.println("Across or Down (a/d):");
        do {
            direction = scnr.next().charAt(0);
        } while (direction != 'a' && direction != 'd');

        ///////
        if (direction == 'd') {
            board.transposeBoard();
            int temp = row;
            row = col;
            col = temp;
        }
        boardIndex = col;
        int wordLength = word.length();
        word = something(row, col, startCol, word);
        if (word.length() != wordLength) connects = true;

        do {
            int crossScore = 0;
            // Prompt user to select a tile
            printRack();
            System.out.println("Select a tile:");
            do {
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

            board.placeTile(tile, row, col);
            word += tile.getLetter();
            score += (tile.getPoints()*square.multiplyLetter());
            wordSquares.add(board.getBoardSpace(row,col));
            wordTiles.add(tile);
            rack.remove(tile);
            wordMultiplier *= square.multiplyWords();

            crossScore += crossCheck(row,col,square);
            if (crossScore == 0) {
                break; // Do I want this??
            } else if (crossScore != -1) {
                score += crossScore;
                connects = true;
            }

            col++;

            if (col >= board.getBoardSize()) break;
            square = board.getBoardSpace(row,col);

            while (!square.isEmpty()) {
                word += square.getLetter();
                score += (square.getPoints()*square.multiplyLetter());
                col++;
                square = board.getBoardSpace(row,col);
                connects = true;
            }

            System.out.println("Word: "+word);

            System.out.println("Continue? (y/n)");
            cont = scnr.next().charAt(0);
        } while (cont == 'y');

        if ((word.length() + boardIndex) > board.getBoardSize()) {
            System.out.println(word+" does not fit on board");
            undoTurn(wordTiles, wordSquares);
        } else if (!trie.traverseTrie(word) && word.length() != 1) {
            System.out.println(word + " is not a word");
            undoTurn(wordTiles, wordSquares);
        } else if (!connects) {
            System.out.println("Word must connect to tiles on board.");
            undoTurn(wordTiles, wordSquares);
        } else if (word != "") {
            score*=wordMultiplier;
            fillRack();
            System.out.println("total word score: "+score);
        }
    }

    public String something(int i, int j, int startCol, String word) {
        while (j != 0 && board.getBoardTile(i, j-1) != null) j--;
        while (j != startCol) {
            word += board.getBoardSpace(i, j).getTile().getLetter();
            j++;
        }
        return word;
    }

    private int crossCheck(int i, int j, Square square) {
        int result1 = crossCheckHelper(i,j,square);
        board.transposeBoard();
        int result2 = crossCheckHelper(j,i,square);
        board.transposeBoard();

        if (result1 < 0 || result2 < 0) return -1;
        else if (result1 == 0 || result2 == 0) return 0;
        else return result1+result2;
    }

    private int crossCheckHelper(int i, int j, Square square) {
        Square crossSquare;
        String word = "";
        int score = 0;
        int wordMultiplier = square.multiplyWords();

        do {
            i--;
            if (i < 0) break;
            crossSquare = board.getBoardSpace(i, j);
        } while (!crossSquare.isEmpty());
        i++;

        crossSquare = board.getBoardSpace(i, j);

        do {
            if (crossSquare == square) {
                score += crossSquare.getPoints()*crossSquare.multiplyLetter();
            } else {
                score += crossSquare.getPoints();
            }
            word += crossSquare.getLetter();
            i++;
            if (i >= board.getBoardSize()) break;
            crossSquare = board.getBoardSpace(i,j);
        } while (!crossSquare.isEmpty());

        System.out.println(word+" "+word.length());
        if (word.length() == 1) {
            return -1;
        } else if (trie.traverseTrie(word)) {
            return score*wordMultiplier;
        } else {
            System.out.println(word+" is not a valid word.");
            return 0;
        }
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
            rack.add(board.drawTile()); // TODO: make sure line doesn't affect code
        }
    }

    public int sumPoints(LinkedList<Tile> word) {
        int pointSum = 0;
        for (Tile tile : word) {
            pointSum += tile.getPoints();
        }
        return pointSum;
    }

    public void shuffleRack() {
        Collections.shuffle(rack);
    }
    public void printRack() {
        for (Tile tile : rack) {
            System.out.print(tile.getLetter()+"  ");
        }
        System.out.println();
    }
}
