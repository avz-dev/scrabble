package scrabble;

public class Square {
    private int multiplier;
    private boolean isWordMultiplier;
    private Tile tile;
    private int row;
    private int col;
    private boolean isAnchor;

    public Square(int multiplier, Tile tile, boolean isWordMultiplier, int row, int col) {
        this.multiplier = multiplier;
        this.tile = tile;
        this.isWordMultiplier = isWordMultiplier;
        this.row = row;
        this.col = col;
    }

    public boolean isEmpty() { return tile == null; }

    public Tile getTile() { return tile; }

    public char getLetter() { return tile.getLetter(); }

    public char getLowercaseLetter() {
        return tile.getLowercaseLetter();
    }

    public int getPoints() {
        if (!isWordMultiplier) return tile.getPoints()*multiplyLetter();
        return tile.getPoints();
    }

    public int getRawPoints() {
        return tile.getPoints();
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    // applies multiplier if square is a word multiplier, otherwise return identity
    public int multiplyWords() {
        if (isWordMultiplier) return multiplier;
        else return 1;
    }

    // returns identity if square is word multiplier, otherwise returns multiplier
    public int multiplyLetter() {
        if (isWordMultiplier) return 1;
        else return multiplier;
    }

    public int getMultiplier() {
        return multiplier;
    }

    public boolean isWordMultiplier() {
        return isWordMultiplier;
    }

    public int getRow() { return row; }

    public int getCol() { return col; }

    public void transposeSquare() {
        int temp = row;
        row = col;
        col = temp;
    }

    public void clearMultipliers() {
        multiplier = 1;
    }

    public void setAnchor(boolean anchor) { isAnchor = anchor; }

    public boolean isAnchor() { return isAnchor; }

    @Override
    public String toString() {
        return row+","+col;
    }
}
