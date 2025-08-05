package atlassian;

import java.util.*;

public class AnagramSubsetMatchTrie {
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word = null;
    }

    private final TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.word = word;
    }

    public String find(List<String> words, String given) {
        for (String word : words) insert(word);
        int[] givenCount = new int[26];
        for (char c : given.toCharArray()) givenCount[c - 'a']++;
        return dfs(root, givenCount);
    }

    private String dfs(TrieNode node, int[] givenCount) {
        if (node.word != null) return node.word;
        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null && givenCount[i] > 0) {
                givenCount[i]--;
                String res = dfs(node.children[i], givenCount);
                givenCount[i]++;
                if (!res.equals("-")) return res;
            }
        }
        return "-";
    }

    public static void main(String[] args) {
        List<String> words = Arrays.asList("baby", "cat", "dada", "dog");
        AnagramSubsetMatchTrie trie = new AnagramSubsetMatchTrie();
        System.out.println(trie.find(words, "ctay")); // Output: cat
        System.out.println(trie.find(words, "dad"));  // Output: -
    }
}