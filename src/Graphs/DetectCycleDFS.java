package Graphs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


//https://www.geeksforgeeks.org/problems/detect-cycle-in-an-undirected-graph/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=detect-cycle-in-an-undirected-graph


public class DetectCycleDFS {
    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(1));
        adj.add(Arrays.asList(0,4));
        adj.add(Arrays.asList(3));
        adj.add(Arrays.asList(2,4));
        adj.add(Arrays.asList(1,3));

        System.out.println(detectCycle(5, adj));
    }

    private static boolean detectCycle(int V, List<List<Integer>> adj){
        boolean[] vis = new boolean[V];

        for(int i = 0; i< V; i++){
            if(!vis[i]){
                if(dfs(i, -1, adj, vis))
                    return true;
            }
        }
        return false;
    }

    private static boolean dfs(int n, int p, List<List<Integer>> adj, boolean[] vis){
        vis[n] = true;
        for(int adjN : adj.get(n)){
            if(!vis[adjN]){
                if(dfs(adjN, n, adj, vis))
                    return true;
            } else if (adjN != p) {
                return true;
            }
        }
        return false;
    }
}
