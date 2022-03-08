package scrabble;

public class Tile {
    private char letter;
    private int points;
    boolean blank;

    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        if (letter == '_') blank = true;
    }

    public char getLetter() { return letter; }

    public char printLetter() {
        if (blank) return (char)((int) letter - 32);
        else return letter;
    }

    public int getPoints() { return points; }

    public boolean isBlank() { return blank; }

    public void setLetter(char letter) { this.letter = letter; }
}
