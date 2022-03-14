package scrabble;

import java.util.HashMap;

public class TrieNode {
    boolean terminalNode;
    char letter;
    HashMap<Character, TrieNode> childNodes = new HashMap<>();

    // Delete?
    public TrieNode() {

    }

    public TrieNode(char letter, boolean terminalNode) {
        this.letter = letter;
        this.terminalNode = terminalNode;
    }

    public void insertNode(TrieNode child) {
        childNodes.put(child.letter, child);
    }

    public TrieNode getNode(char letter) {
        return childNodes.get(letter);
    }

    public boolean isNextNode(char letter) {
        return childNodes.containsKey(letter);
    }

    public boolean isTerminal() {
        return terminalNode;
    }
}
