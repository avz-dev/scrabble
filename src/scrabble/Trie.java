package scrabble;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    // Adds word to trie without duplicating nodes
    public TrieNode buildTrie(String word) {
        TrieNode newNode;
        TrieNode current = root;
        boolean isTerminal;
        char letter;
        word = word.toLowerCase();
        for (int i = 0; i < word.length(); i++) {
            letter = word.charAt(i);
            isTerminal = i == (word.length() - 1);
            if (current.getNode(letter) == null) {
                newNode = new TrieNode(letter, isTerminal);
                current.insertNode(newNode);
            } else {
                newNode = current.getNode(letter);
            }
            current = newNode;
        }
        return root;
    }

    // takes a partial or complete word and finds the last node
    public TrieNode findNode(String word) {
        TrieNode current = root;
        if (word.equals("")) return root;
        for (int i = 0; i < word.length(); i++) current = current.getNode(word.toLowerCase().charAt(i));
        return current;
    }

    // Returns trie root node
    public TrieNode getRoot() { return root; }

    // takes a partial or complete node and determines if it's a legal word
    public boolean traverseTrie (String word) {
        TrieNode current = root;
        int i;
        if (word == null) return false;
        for (i = 0; i < word.length(); i++) {
            current = current.getNode(word.charAt(i));
            if (current == null) break;
        }
        return current != null && current.isTerminal();
    }
}
