/*  Andrew Valdez
    Handles all GUI elements and feeds player input into Player.
    Calls on solver to play its turn. */
package scrabble;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;


public class Display {
    private final Board BOARD = new Board();
    private final Trie TRIE = new Trie();
    private Player player = new Player();
    private Solver solver;
    private Label[] tray;
    private Label[][] spaces;
    private char direction;
    private int tileIndex;
    private Square square = null;
    private Word word;
    private final String[] SUBSCRIPTS = {"₀","₁","₂","₃","₄","₅","","","₈","","₁₀"}; // for point representation

    @FXML // text field to enter desired blank tile letter
    private TextField textField;

    @FXML // buttons with various functionality
    private Button acrossButton, downButton, shuffleButton, resetButton, playButton;

    @FXML // hold board and tray tiles/spaces
    private GridPane boardSpace, traySpace;

    @FXML // represent tiles in tray, scores, and instructions for user
    private Label tile0, tile1, tile2, tile3, tile4, tile5, tile6, instructions, playerScore, computerScore;

    @FXML // represent board spaces
    private Label square0, square1, square2, square3, square4, square5, square6, square7,
            square8, square9, square10, square11, square12, square13, square14, square15,
            square16, square17, square18, square19, square20, square21, square22, square23,
            square24, square25, square26, square27, square28, square29, square30, square31,
            square32, square33, square34, square35, square36, square37, square38, square39,
            square40,square41, square42, square43, square44, square45, square46, square47,
            square48, square49, square50,square51, square52, square53, square54, square55,
            square56, square57, square58, square59, square60, square61, square62, square63,
            square64, square65, square66, square67, square68, square69, square70, square71,
            square72, square73, square74, square75, square76, square77, square78, square79,
            square80,square81, square82, square83, square84, square85, square86, square87,
            square88, square89, square90, square91, square92, square93, square94, square95,
            square96, square97, square98, square99, square100, square101, square102, square103,
            square104, square105, square106, square107, square108, square109, square110, square111,
            square112, square113, square114, square115, square116, square117, square118, square119,
            square120, square121, square122, square123, square124, square125, square126, square127,
            square128, square129, square130, square131, square132, square133, square134, square135,
            square136, square137, square138, square139, square140,square141, square142, square143,
            square144, square145, square146, square147, square148, square149, square150,square151,
            square152, square153, square154, square155, square156, square157, square158, square159,
            square160, square161, square162, square163, square164, square165, square166, square167,
            square168, square169, square170, square171, square172, square173, square174, square175,
            square176, square177, square178, square179, square180,square181, square182, square183,
            square184, square185, square186, square187, square188, square189, square190, square191,
            square192, square193, square194, square195, square196, square197, square198, square199,
            square200, square201, square202, square203, square204, square205, square206, square207,
            square208, square209, square210, square211, square212, square213, square214, square215,
            square216, square217, square218, square219, square220, square221, square222, square223,
            square224;

    @FXML // initializes GUI elements, board/trie and computer/player objects, and starts game
    void initialize() throws IOException {
        Label[] tray = {tile0,tile1,tile2,tile3,tile4,tile5,tile6};
        Label[][] spaces = {{square0, square1, square2, square3, square4, square5, square6, square7,
                              square8, square9, square10, square11, square12, square13, square14},
                            {square15, square16, square17, square18, square19, square20, square21,
                              square22, square23, square24, square25, square26, square27, square28, square29},
                            {square30, square31, square32, square33, square34, square35, square36,
                            square37, square38, square39, square40,square41, square42, square43, square44},
                            {square45, square46, square47, square48, square49, square50,square51,
                            square52, square53, square54, square55, square56, square57, square58, square59},
                            {square60, square61, square62, square63, square64, square65, square66,
                            square67, square68, square69, square70, square71, square72, square73, square74},
                            {square75, square76, square77, square78, square79, square80,square81,
                            square82, square83, square84, square85, square86, square87, square88, square89},
                            {square90, square91, square92, square93, square94, square95, square96,
                            square97, square98, square99, square100, square101, square102, square103, square104},
                            {square105, square106, square107, square108, square109, square110, square111,
                            square112, square113, square114, square115, square116, square117, square118, square119},
                            {square120, square121, square122, square123, square124, square125, square126,
                            square127, square128, square129, square130, square131, square132, square133, square134},
                            {square135, square136, square137, square138, square139, square140,square141,
                            square142, square143, square144, square145, square146, square147, square148, square149},
                            {square150,square151, square152, square153, square154, square155, square156,
                            square157, square158, square159, square160, square161, square162, square163, square164},
                            {square165, square166, square167, square168, square169, square170, square171,
                            square172, square173, square174, square175, square176, square177, square178, square179},
                            {square180,square181, square182, square183, square184, square185, square186,
                            square187, square188, square189, square190, square191, square192, square193, square194},
                            {square195, square196, square197, square198, square199, square200, square201,
                            square202, square203, square204, square205, square206, square207, square208, square209},
                            {square210, square211, square212, square213, square214, square215, square216,
                            square217, square218, square219, square220, square221, square222, square223, square224}};
        this.tray = tray;
        this.spaces = spaces;
        buildTrieAndBoard();

        Player player = new Player(BOARD, TRIE);
        Solver solver = new Solver(BOARD, TRIE);
        this.player = player;
        this.solver = solver;

        promptTurn();
    }

    // Reads in dictionary, creates trie structure, AND creates standard scrabble board
    private void buildTrieAndBoard() throws IOException {
        // Reads in dictionary scanner new file (args[0])
        InputStream dictionary = Display.class.getResourceAsStream("/sowpods.txt");
//        File dictionary = new File("src/scrabble/resources/sowpods.txt");
        Scanner scanner = new Scanner(dictionary);
        while (scanner.hasNext()) {
            String word = scanner.next();
            TRIE.buildTrie(word);
        }
        BOARD.createBoard();
    }

    // Starts player turn, locks off unnecessary buttons
    private void promptTurn() {
        instructions.setText("Select starting square or press play to end turn");
        boardSpace.setMouseTransparent(false);
        boardSpace.setVisible(true);
        traySpace.setMouseTransparent(true);
        acrossButton.setVisible(false);
        downButton.setVisible(false);
        resetButton.setDisable(true);
        playButton.setVisible(true);
        textField.setVisible(false);
        enableBoardSpaces();
        updateAll();
        BOARD.findAnchors();
    }

    @FXML // Allows selection of starting square
    private void selectSquare(MouseEvent event) {
        Label label = (Label) event.getSource();
        outerLoop:
        for (int i = 0; i < BOARD.getBoardSize(); i++) {
            for (int j = 0; j < BOARD.getBoardSize(); j++) {
                if (label == spaces[i][j]) {
                    square = BOARD.getBoardSpace(i,j);
                    break outerLoop;
                }
            }
        }
        boardSpace.setMouseTransparent(true);
        acrossButton.setVisible(true);
        downButton.setVisible(true);
        playButton.setVisible(false);
        instructions.setText("Select a direction");
    }

    @FXML // Allows selection of direction and sets variable accordingly
    private void selectDirection(MouseEvent event) {
        Button button = (Button) event.getSource();
        Word word;
        if (button.getText().equals("Across")) {
            direction = 'a';
        } else direction = 'd';
        acrossButton.setVisible(false);
        downButton.setVisible(false);
        traySpace.setMouseTransparent(false);
        instructions.setText("Select a tile");
        word = player.findPartialWord(square,0,"",direction);
        this.word = word;
    }

    @FXML // Allows selection of tile from tray
    private void selectTile(MouseEvent event) {
        Label label = (Label) event.getSource();
        if (word.getSquare() == null) {
            traySpace.setMouseTransparent(true);
            return;
        }
        for (int i = 0; i < BOARD.getBoardSize(); i++) {
            if (label == tray[i]) {
                tileIndex = i;
                if (player.getTrayTileLetter(i) == '_') {
                    instructions.setText("Enter a letter");
                    textField.setVisible(true);
                    traySpace.setMouseTransparent(true);
                    shuffleButton.setMouseTransparent(true);
                } else word = player.playTile(word, i, direction);
                break;
            }
        }
        updateAll();
        playButton.setVisible(true);
        resetButton.setDisable(false);
    }

    @FXML // Allows user to input desired letter in text field and sets blank
    private void setBlankLetter(ActionEvent event) {
        char letter = textField.getText().toUpperCase().charAt(0);
        if (letter > 64 && letter < 91) {
            player.setBlank(letter, tileIndex);
            word = player.playTile(word, tileIndex, direction);
            updateAll();
            textField.setVisible(false);
            textField.clear();
            traySpace.setMouseTransparent(false);
            shuffleButton.setMouseTransparent(false);
            instructions.setText("Select a tile");
        }
    }

    @FXML // Allows player to end turn or attempt to play a word
    private void playWord(MouseEvent event) {
        int result;
        if (word == null || word.isEmpty()) result = 4;
        else result = player.playWord(word);

        switch (result) {
            case 1 -> instructions.setText("Word must connect to existing board tiles");
            case 2 -> instructions.setText("Invalid word");
            case 3 -> {
                word.resetWord();
                solver.solve();
                promptTurn();
            }
            case 4 -> {
                if (solver.hasNoMoves()) {
                    endGame();
                    return;
                }
                instructions.setText("Player ended turn");
                solver.solve();
                promptTurn();
                if (solver.hasNoMoves()) {
                    instructions.setText("Computer had no moves, your turn.");
                }
            }
        }
        updateAll();
    }

    @FXML // Resets entire player turn
    private void resetTurn(MouseEvent event) {
        player.undoTurn();
        promptTurn();
        word.resetWord();
        tileIndex = 0;
    }

    @FXML // Shuffles the tiles in the tray
    private void shuffle(MouseEvent event) {
        player.shuffleTray();
        updateTray();
    }

    // Enables board spaces to be selected
    private void enableBoardSpaces() {
        for (int i = 0; i < BOARD.getBoardSize(); i++) {
            for (int j = 0; j < BOARD.getBoardSize(); j++) {
                spaces[i][j].setMouseTransparent(false);
            }
        }
    }

    // Updates visuals for the board, tray, & scores
    private void updateAll() {
        updateBoard();
        updateTray();
        playerScore.setText("Player: "+player.getScore());
        computerScore.setText("Computer: "+solver.getScore());
    }

    // Updates appearance of tray
    private void updateTray() {
        for (Label label : tray) {
            label.setText("");
            label.setStyle("");
            label.setMouseTransparent(true);
        }
        for (int i = 0; i < player.getTraySize(); i++) {
            tray[i].setText(player.getTrayTileLetter(i)+" "+ SUBSCRIPTS[player.getTrayTilePoints(i)]);
            tray[i].setStyle("-fx-background-color:tan;");
            tray[i].setMouseTransparent(false);
        }
    }

    // Update board visuals
    private void updateBoard() {
        Square square;
        BOARD.findAnchors();
        if (BOARD.isTransposed()) BOARD.transposeBoard();
        for (int i = 0; i < BOARD.getBoardSize(); i++) {
            for (int j = 0; j < BOARD.getBoardSize(); j++) {
                square = BOARD.getBoardSpace(i, j);
                if (square.isEmpty()) {
                    spaces[i][j].setMouseTransparent(false);
                    if (square.getMultiplier() == 1) {
                        spaces[i][j].setText("");
                        spaces[i][j].setStyle("-fx-background-color:#ebddc5;");
                    } else if (square.isWordMultiplier()) {
                        if (square.getMultiplier() == 2) {
                            if (i == BOARD.getCenter() && j == BOARD.getCenter()) {
                                spaces[i][j].setText("★");
                            } else spaces[i][j].setText("dw");
                            spaces[i][j].setStyle("-fx-background-color:#ebafa9;");
                        } else {
                            spaces[i][j].setText("tw");
                            spaces[i][j].setStyle("-fx-background-color:#c9694f;");
                        }
                    } else {
                        if (square.getMultiplier() == 2) {
                            spaces[i][j].setText("dl");
                            spaces[i][j].setStyle("-fx-background-color:#C8C8A9;");
                        } else {
                            spaces[i][j].setText("tl");
                            spaces[i][j].setStyle("-fx-background-color:#83AF9B;");
                        }
                    }
                } else {
                    spaces[i][j].setText(square.getLetter() + " " + SUBSCRIPTS[square.getRawPoints()]);
                    spaces[i][j].setStyle("-fx-background-color:tan;");
                    spaces[i][j].setMouseTransparent(true);
                }
            }
        }
    }

    // ends the game, locks all buttons and declares winner
    private void endGame() {
        boardSpace.setMouseTransparent(true);
        traySpace.setMouseTransparent(true);
        playButton.setDisable(true);
        shuffleButton.setDisable(true);
        resetButton.setDisable(true);
        if (solver.getScore() > player.getScore()) instructions.setText("Computer wins!");
        else if (solver.getScore() == player.getScore()) instructions.setText("It's a draw!");
        else instructions.setText("You win!");
    }
}