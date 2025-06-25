package Graphs;

import java.util.*;

public class WordLadderII {

    public static void main(String[] args) {
        String[] List = {"des","der","dfr","dgt","dfs"};
        wordLadder("der", "dfs", List);

    }

    private static void wordLadder(String startWord, String endWord, String[] wordList) {
        Set<String> set = new HashSet<>(Arrays.asList(wordList));
        ArrayList<ArrayList<String>> ans = new ArrayList<>();

        ArrayList<String> usedOnLevel = new ArrayList<>();
        usedOnLevel.add(startWord);
        Queue<ArrayList<String>> q = new LinkedList<>();
        ArrayList<String> ls = new ArrayList<>();
        ls.add(startWord);
        q.add(ls);
//        q.add(usedOnLevel);

        int level = 0;

        while(!q.isEmpty()) {
            ArrayList<String> vec = q.poll();
            if(vec.size() > level) {
                level++;
                for(String i : vec) {
                    set.remove(i);
                }
//                usedOnLevel.removeAll();
            }
            String word = vec.get(vec.size()-1);
            if(word.equals(endWord)) {
                if(ans.size() == 0)
                    ans.add(vec);
                else if(ans.get(0).size() == vec.size())
                    ans.add(vec);
            }

            for(int i = 0; i<word.length(); i++) {
                for(char c = 'a'; c<='z'; c++) {
                    char[] charArray = word.toCharArray();
                    charArray[i] = c;
                    String st = new String(charArray);

                    if(set.contains(st)){
                        vec.add(st);
                        q.add(new ArrayList<>(vec));
                        usedOnLevel.add(st);
                        vec.remove(vec.size()-1);
                    }
                }
            }
        }

        for(int i = 0; i< ans.size(); i++ ) {
            for(String s : ans.get(i)){
                System.out.print(s + " ");
            }
            System.out.println();
        }

//        ArrayList<Integer> ad = new ArrayList<>(adj.);
    }
}


