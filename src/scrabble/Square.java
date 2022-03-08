package scrabble;

public class Square {
    private int multiplier;
    private boolean isWordMultiplier;
    private Tile tile;

    public Square(int multiplier, Tile tile) {
        this.multiplier = multiplier;
        this.tile = tile;
    }

    public Square(int multiplier, Tile tile, boolean isWordMultiplier) {
        this.multiplier = multiplier;
        this.tile = tile;
        this.isWordMultiplier = isWordMultiplier;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public int multiplyWords() {
        if (isWordMultiplier) return multiplier;
        else return 1;
    }

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
}
