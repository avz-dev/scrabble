package scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("Display.fxml"));
        primaryStage.setTitle("SCRABBLE");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

//    Trie trie = new Trie();
//    Board board = new Board();
//
//    // Reads in dictionary scanner new file (args[0])
//    File dictionary = new File(args[0]);
//    Scanner scanner = new Scanner(dictionary);
//        while (scanner.hasNext()) {
//                String word = scanner.next();
//                trie.buildTrie(word);
//                }
//                board.createBoard();
//
//                Player player = new Player(board, trie);
//                Solver computer = new Solver(board,trie);
//
//                while (true) {
//                board.printSimpleBoard();
//                player.playTurn();
//                board.printSimpleBoard();
//                computer.solve();
//                }