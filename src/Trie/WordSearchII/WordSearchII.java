package Trie.WordSearchII;

import java.util.ArrayList;
import java.util.List;

//Given an m x n board of characters and a list of strings words, return all words on the board.
//Each word must be constructed from letters of sequentially adjacent cells, where adjacent cells are horizontally or
// vertically neighboring. The same letter cell may not be used more than once in a word.
class WordSearchII {
    private  static Node root = new Node();
    public static void main(String[] args) {
        char[][] board = {{'o','a','a','n'},{'e','t','a','e'},{'i','h','k','r'},{'i','f','l','v'}};
        String[] w = {"oath","pea","eat","rain"};
        wordSearch(board, w);

    }
    private static void insert(String[] w) {
        for(String s : w) {
            Node t = root;
            for(int i = 0; i<s.length(); i++) {
                if(t.ch[s.charAt(i)-'a'] == null) {
                    t.ch[s.charAt(i)-'a'] = new Node();
                }
                t = t.ch[s.charAt(i)-'a'];
            }
            t.fl = true;
        }
    }
    private static void dfs(char[][] b, String st, List<String> res, int i, int j, Node r) {
        if(i<0 || j<0 || i>=b.length || j>=b[0].length) return;
        if(b[i][j] == 'F')return;
        if(r.ch[b[i][j]-'a'] == null) return;

        char tm = b[i][j];
        st += tm;
        r = r.ch[b[i][j] - 'a'];
        if( r.fl) {
            r.fl = false;
            res.add(st);
        }
        b[i][j] = 'F';
        dfs(b, st, res, i+1, j, r);
        dfs(b, st, res, i-1, j, r);
        dfs(b, st, res, i, j+1, r);
        dfs(b, st, res, i, j-1, r);
        b[i][j] = tm;
    }
    private static void wordSearch(char[][] b, String[] w) {
        List<String> ans = new ArrayList<>();
        insert(w);
        for(int i = 0; i<b.length; i++) {
            for(int j = 0; j<b[0].length; j++) {
                dfs(b, "", ans, i, j, root);
            }
        }
        for(String s : ans) System.out.println(s);
    }
}
class Node {
    Node[] ch = new Node[26];
    boolean fl;
}


