package Graphs.Retrial;

import java.util.ArrayList;
import java.util.List;

public class DFS {
    public static void main(String[] args) {
        boolean[] vis = new boolean[5];
        List<Integer> ans = new ArrayList<>();
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(2);
        adj.get(2).add(0);
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(0).add(3);
        adj.get(3).add(0);
        adj.get(2).add(4);
        adj.get(4).add(2);
        dfs(0, ans, adj, vis);

        for(int i : ans) {
            System.out.print(i);
        }
    }

    private static void dfs(int n, List<Integer> ans, ArrayList<ArrayList<Integer>> adj, boolean[] vis) {
        vis[n] = true;
        ans.add(n);

        for( int i: adj.get(n)) {
            if(!vis[i]) {
                dfs(i, ans, adj, vis);
            }
        }
    }
}
