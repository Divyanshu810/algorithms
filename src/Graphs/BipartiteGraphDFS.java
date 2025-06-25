package Graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BipartiteGraphDFS {

    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(2,3));
        adj.add(Arrays.asList(3));
        adj.add(Arrays.asList(0,3));
        adj.add(Arrays.asList(0,1,2));
//        adj.add(Arrays.asList(1,3));
        List<List<Integer>> adjw = new ArrayList<>();
        adjw.add(Arrays.asList(1));
        adjw.add(Arrays.asList(0,2));
        adjw.add(Arrays.asList(1));

        System.out.println(checkBipartite(3, 0, adjw));
        System.out.println(checkBipartite(4, 0, adj));
    }

    private static boolean checkBipartite(int V, int s, List<List<Integer>> adj){
        int[] col = new int[V];
        col[s] = 1;
        for(int i = 0; i< V; i++){
            col[i] = -1;
        }
        for(int i = 0; i< V; i++){
            if(col[i] == -1){
                if(!dfs(s, V, adj, col, 0))
                    return false;
            }
        }
        return true;
    }

    private static boolean dfs(int s, int V, List<List<Integer>> adj, int[] col, int co){
        col[s] = co;
        for(int i : adj.get(s)){
            if(col[i] == -1){
                if(!dfs(i, V, adj, col, 1 - co))
                    return false;
            } else if(col[i] == co)
                return false;
        }
        return true;
    }
}
