/*  Andrew Valdez
    TrieNode holds letter value, terminal status, and successive nodes. */
package scrabble;

import java.util.HashMap;
import java.util.Set;

public class TrieNode {
    private boolean terminalNode;
    private char letter;
    private HashMap<Character, TrieNode> childNodes = new HashMap<>();

    public TrieNode() { }

    public TrieNode(char letter, boolean terminalNode) {
        this.letter = letter;
        this.terminalNode = terminalNode;
    }

    // gets key set of child nodes
    public Set<Character> getNodes() { return childNodes.keySet(); }

    // inserts a new child node
    public void insertNode(TrieNode child) {
        childNodes.put(child.letter, child);
    }

    // gets node associated with a given key
    public TrieNode getNode(char letter) {
        return childNodes.get(letter);
    }

    // returns whether node is terminal
    public boolean isTerminal() { return terminalNode; }
}
