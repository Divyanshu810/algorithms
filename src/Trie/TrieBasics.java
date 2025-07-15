package Trie;

import java.util.Arrays;
import java.util.List;

public class TrieBasics {

}

class Node {
    Node[] children = new Node[27];
    boolean fl;
    Node() {
    }
}
class MagicDictionaryTrie {
    Node root = new Node();

    private void insert(String w) {
        Node n = root;
        for(int i = 0; i<w.length(); i++) {
            if(n.children[w.charAt(i)-'a'] == null) {
                n.children[w.charAt(i) - 'a'] = new Node();
            }
            n = n.children[w.charAt(i)-'a'];
        }
        n.fl = true;
    }
    public boolean findP(String pref) {
        return false;
    }

    public boolean found(String w) {
        Node n = root;
        for(int i = 0; i<w.length(); i++) {
            if(n.children[w.charAt(i)-'a'] == null) return false;
            n = n.children[w.charAt(i)-'a'];
        }
        return n.fl;
    }

    public boolean search(String s) {
        char[] ar = s.toCharArray();
        for(int i = 0; i<s.length(); i++) {
            char c = ar[i];
            for(char ch ='a'; ch<='z'; ch++) {
                if(ch == c) continue;
                ar[i] = ch;
                String tmp = new String(ar);
                if(found(tmp)) return true;
            }
            ar[i] = c;
        }
        return false;
    }

    public void buildDict(List<String> d) {
        for(String s : d) {
            insert(s);
        }
    }

    public static void main(String[] args) {
        List<String> dict = Arrays.asList("hello", "leetcode");
        MagicDictionaryTrie obj = new MagicDictionaryTrie();
        obj.buildDict(dict);
        System.out.println(obj.search("hello"));
        System.out.println(obj.search("hellp"));
        System.out.println(obj.search("leetcode"));
        System.out.println(obj.search("leetcoded"));

        StringBuilder sb = new StringBuilder();
//        sb.to
    }
}
