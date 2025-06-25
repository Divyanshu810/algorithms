package Graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DirectedCyclicGraph {

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

        System.out.println(checkDirectedCyclic(3, adjw));
        System.out.println(checkDirectedCyclic(4, adj));

    }

    private static boolean checkDirectedCyclic(int V, List<List<Integer>> ad){
        boolean[] vis = new boolean[V];
        boolean[] pv = new boolean[V];

        for(int i = 0; i<V; i++){
            if(!vis[i]){
                if(dfs(i, V, ad, vis, pv))
                    return true;
            }
        }
        return false;
    }

    private static boolean dfs(int n, int V, List<List<Integer>> ad, boolean[] vis, boolean[] pv){
        vis[n] = true;
        pv[n]  = true;

        for(int i : ad.get(n)){
            if(!vis[i]){
                if(!dfs(i, V, ad, vis, pv))
                    return false;
            } else if(pv[i])
                return true;
        }
        pv[n] = false;
        return false;
    }

}
