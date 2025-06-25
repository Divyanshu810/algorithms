package Graphs.Retrial;

import java.util.ArrayList;

public class DFSrT {
    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        boolean[] vis = new boolean[5];
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

//        dfs(5,adj);
        dfs(5, adj);
    }

    private static void dfs(int v, ArrayList<ArrayList<Integer>> adj){
        boolean[] vis = new boolean[v];
        ArrayList<Integer> ans = new ArrayList<>();
        dfsTr(0, vis, adj, ans);
        for(int i : ans){
            System.out.println(i);
        }
    }

    private static void dfsTr(int n, boolean[] vis, ArrayList<ArrayList<Integer>> adj, ArrayList<Integer> ans){
        vis[n] = true;
        ans.add(n);

        for(int i : adj.get(n)){
            if(!vis[i])
                dfsTr(i, vis, adj, ans);
        }
    }
}
