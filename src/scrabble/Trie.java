package scrabble;

import java.util.Locale;

public class Trie {
    private TrieNode root;

    public Trie() {
        root = new TrieNode();
    }

    public TrieNode buildTrie(String word) {
        TrieNode newNode;
        TrieNode current = root;
        boolean isTerminal;
        char letter;
        word.toLowerCase(Locale.ROOT);
        for (int i = 0; i < word.length(); i++) {
            letter = word.charAt(i);
            isTerminal = i == (word.length() - 1);
            if (current.childNodes.get(letter) == null) {
                newNode = new TrieNode(letter, isTerminal);
                current.insertNode(newNode);
            } else {
                newNode = current.childNodes.get(letter);
            }
            current = newNode;
        }
        return root;
    }

    public boolean traverseTrie (String word) {
        TrieNode current = root;
        int i;
        if (word == null) return false;
        for (i = 0; i < word.length(); i++) {
            current = current.getNode(word.charAt(i));
            if (current == null) break;
        }
        return i == word.length() && current.terminalNode;
    }
}
