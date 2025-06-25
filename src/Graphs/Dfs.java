package Graphs;

import java.util.ArrayList;

public class Dfs {

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

        ArrayList<Integer> ans = dfs(5,adj);
        dfs(5, adj);
        for(int i : ans)
            System.out.println(i);

    }

    private static ArrayList<Integer> dfs(int v, ArrayList<ArrayList<Integer>> adj ){
        boolean[] vis = new boolean[v+1];
        ArrayList<Integer> ans = new ArrayList<>();
        dfsRec(vis, adj, 0, ans);
        return ans;
    }

    private static void dfsRec(boolean[] vis, ArrayList<ArrayList<Integer>> adj, int n, ArrayList<Integer> ans){
        ans.add(n);
        vis[n] = true;
        for(int i : adj.get(n)){
            if(!vis[i])
                dfsRec(vis,adj,i,ans);
        }
    }
}
