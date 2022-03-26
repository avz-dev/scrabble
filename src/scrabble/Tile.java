/*  Andrew Valdez
    Tile holds tile data such as letter, points, and blank status. */
package scrabble;

public class Tile {
    private char letter;
    private int points;
    private boolean blank;

    public Tile(char letter, int points) {
        this.letter = letter;
        this.points = points;
        if (letter < 97) blank = true;
    }

    public char getLetter() { return letter; }

    public char getLowercaseLetter() {
        if (blank && letter != '_') return (char)((int) letter + 32);
        else return letter;
    }

    public int getPoints() { return points; }

    public boolean isBlank() { return blank; }

    @Override
    public String toString() { return ""+letter; }

    public void setLetter(char letter) { this.letter = letter; }
}
