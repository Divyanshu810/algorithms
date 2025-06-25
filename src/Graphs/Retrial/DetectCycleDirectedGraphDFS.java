package Graphs.Retrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DetectCycleDirectedGraphDFS {
    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(1));
        adj.add(Arrays.asList(2));
        adj.add(Arrays.asList(3));
        adj.add(Arrays.asList(3));
//        adj.add(Arrays.asList(1,3));
        List<List<Integer>> adjw = new ArrayList<>();
        adjw.add(Arrays.asList(1));
        adjw.add(Arrays.asList(2));
        adjw.add(Arrays.asList());

        System.out.println(isCyclic( adjw));
        System.out.println(isCyclic(adj));

    }

    private static boolean isCyclic(List<List<Integer>> adj) {
        int[] v = new int[adj.size()];
        int[] pv = new int[adj.size()];

        for(int i = 0; i< adj.size(); i++) {
            if(v[i] == 0){
                if(dfs(i, v, pv, adj))
                    return true;
            }
        }
        return false;
    }

    private static boolean dfs(int n, int[] v, int[] pv, List<List<Integer>> adj) {
        v[n] = 1;
        pv[n] = 1;

        for(int k : adj.get(n)) {
            if(v[k] == 0) {
                if(dfs(k, v, pv, adj))
                    return true;
            } else if(pv[k] == 1)
                return true;
        }
        pv[n] = 0;
        return false;
    }
}
