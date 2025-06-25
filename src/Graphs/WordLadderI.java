package Graphs;

import java.util.*;

class Pairzz {
    String w;
    int st;

    Pairzz(String w, int st) {
        this.w = w;
        this.st = st;
    }
}

public class WordLadderI {

    public static void main(String[] args) {
        String[] List = {"des","der","dfr","dgt","dfs"};
        System.out.println(wordLadder("der", "dfs", List));

    }


    private static int wordLadder(String startWord, String targetWord, String[] list) {
        Queue<Pairzz> q = new LinkedList<>();
        Set<String> st = new HashSet<>(Arrays.asList(list));

        q.add(new Pairzz(startWord, 1));
        st.remove(startWord);

        while(!q.isEmpty()) {
            Pairzz p = q.poll();
            String w = p.w;
            int ste = p.st;
            if(w.equals(targetWord)) return ste;
            for(int i = 0; i<w.length(); i++) {
                char[] charArray = w.toCharArray();
                for(char ch = 'a'; ch<='z'; ch++) {
                    charArray[i] = ch;
                    String word = new String(charArray);
                    if(st.contains(word)) {
                        st.remove(word);
                        q.add(new Pairzz(word, ste +1));
                    }
                }
            }
        }
        return 0;
    }
}
