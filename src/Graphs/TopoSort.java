package Graphs;

import java.util.*;
import java.util.List;

public class TopoSort {

    public static void main(String[] args) {

        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(0,1));
        adj.add(Arrays.asList(1,2));
        adj.add(Arrays.asList(3,1));
        adj.add(Arrays.asList(3,2));


        List<List<Integer> > adjs = new ArrayList<>(4);
        for (int i = 0; i < 4; i++) {
            adjs.add(new ArrayList<>());
        }

        for (List<Integer> i : adj) {
            adjs.get(i.get(0)).add(i.get(1));
        }
        topoSort(adjs);
    }

    private static void topoSort(List<List<Integer>> adj) {
        int[] v = new int[adj.size()];
//        List<Integer> ans = new List<>();
        Stack<Integer> s = new Stack<>();
        for(int i = 0; i<adj.size(); i++) {
            if(v[i] == 0) {
                dfs(i, adj, s, v);
            }
        }
        while(!s.empty()) {
            System.out.println(s.pop());
        }

        ArrayList<Integer> ad = new ArrayList<>(adj.size());
//        ad.get(0) = 1;

    }

    private static void dfs(int n, List<List<Integer>> adj, Stack<Integer> s, int[] v) {
        v[n] = 1;

        for(int i : adj.get(n)) {
            if(v[i] == 0)
                dfs(i, adj, s, v);
        }
        s.push(n);
    }
}
