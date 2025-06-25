package Graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AlienDict {
    public static void main(String[] args) {

        String[] d = {"baa","abcd","abca","cab","cad"};
        String[] d2 = {"caa","aaa","aab"};
        String[] d3 = {"dhhid", "dahi", "cedg", "fg", "gdah", "i", "gbdei", "hbgf", "e", "ddde"};

        alienDi(d, 4);
        alienDi(d2, 3);
        alienDi(d3, 9);

    }

    private static void alienDi(String[] d, int k) {
        List<List<Integer>> adj = new ArrayList<>();
        for(int i = 0; i< k; i++) {
            adj.add(new ArrayList<>());
        }

        for(int i = 0; i<d.length-1; i++) {
            String s1 = d[i];
            String s2 = d[i+1];
            int l = Math.min(s1.length(), s2.length());

            for(int p = 0; p<l; p++) {
                if(s1.charAt(p) != s2.charAt(p)) {
                    adj.get(s1.charAt(p) - 'a').add(s2.charAt(p) - 'a');
                    break;
                }
            }
        }

        List<Integer> l = topoSort(k, adj);

        String s = "";

        for(int f : l) {
            s += (char)(f + (int)'a');
        }
        System.out.println(s);
    }

    private static List<Integer> topoSort(int v, List<List<Integer>> adj) {
        int[] ind = new int[v];
        Queue<Integer> q = new LinkedList<>();

        for(int i = 0; i< adj.size(); i++) {
            for(int k : adj.get(i)) {
                ind[k]++;
            }
        }
        for(int i = 0; i<v; i++) {
            if(ind[i] == 0)
                q.add(i);
        }

        List<Integer> ans = new ArrayList<>();

        while(!q.isEmpty()) {
            int n = q.poll();
            ans.add(n);
            for(int z: adj.get(n)) {
                ind[z]--;
                if(ind[z] == 0)
                    q.add(z);
            }
        }
        return ans;
    }
}
