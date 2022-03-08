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
        int tileIndex, row, col, boardIndex, startRow, startCol;
        int score = 0, crossScore = 0, wordMultiplier = 1;
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
                || board.getBoardSpace(row,col).getTile() != null);

        square = board.getBoardSpace(row, col);
        startRow = row;
        startCol = col;

        // Prompt user for word direction
        System.out.println("Across or Down (a/d):");
        do {
            direction = scnr.next().charAt(0);
        } while (direction != 'a' && direction != 'd');

        if (direction == 'a') {
            while (col != 0 && board.getBoardSpace(row, col - 1).getTile() != null) col--;
            boardIndex = col;
            while (col != startCol) {
                word += board.getBoardSpace(row, col).getTile().getLetter();
                col++;
                connects = true;
            }
        } else {
            while (row != 0 && board.getBoardSpace(row-1,col).getTile() != null) row--;
            boardIndex = row;
            while (row != startRow) {
                word += board.getBoardSpace(row, col).getTile().getLetter();
                row++;
                connects = true;
            }
        }

        do {
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

//            board.printSimpleBoard();

            crossScore += crossCheck(direction,row,col, square, tile);
            if (crossScore == 0) {
                break; // Do I want this??
            } else if (crossScore != -1) {
                score += crossScore;
                connects = true;
            }
            crossScore = 0;

            if (direction == 'a') col++;
            else row++;

            if (row >= board.getBoardSize() || col >= board.getBoardSize()) break;
            square = board.getBoardSpace(row,col);

            while (square.getTile() != null) {
                word += square.getTile().getLetter();
                score += (square.getTile().getPoints()*square.multiplyLetter());
                if (direction == 'a') col++;
                else row++;
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
        } else if (!trie.traverseTrie(word)) {
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

    public int crossCheck(char dir, int row, int col, Square square, Tile tile) {
        Square crossSquare = square;
        Tile currentTile;
        String word = "";
        int score = 0;
        int wordMultiplier = crossSquare.multiplyWords();

        if (dir == 'a') {
            do {
                row--;
                if (row < 0) break;
                crossSquare = board.getBoardSpace(row, col);
            } while (crossSquare.getTile() != null);
            row++;
        } else {
            do {
                col--;
                if (col < 0) break;
                crossSquare = board.getBoardSpace(row, col);
            } while (crossSquare.getTile() != null);
            col++;
        }
        crossSquare = board.getBoardSpace(row, col);

        do {
            if (crossSquare == square) {
                currentTile = tile;
                score += currentTile.getPoints()*crossSquare.multiplyLetter();
            } else {
                currentTile = crossSquare.getTile();
                score += currentTile.getPoints();
            }
            word += currentTile.getLetter();
            if (dir == 'a') row++;
            else col++;
            if (col >= board.getBoardSize() || row >= board.getBoardSize()) break;
            crossSquare = board.getBoardSpace(row,col);
            currentTile = crossSquare.getTile();
        } while (currentTile != null);

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
            rack.add(board.tileBag.pop());
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
