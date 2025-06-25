package Graphs.Retrial;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Bfsrt {

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        boolean[] vis = new boolean[5];
        for(int i = 0; i < 5; i++){
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(0).add(4);
        adj.get(4).add(0);
        adj.get(1).add(2);
        adj.get(2).add(1);
        adj.get(1).add(3);
        adj.get(3).add(1);

        ArrayList<Integer> ans = bfs(5, adj);
        for(int i : ans){
            System.out.println(i);
        }
    }

    private static ArrayList<Integer> bfs(int v, ArrayList<ArrayList<Integer>> adj){
        ArrayList<Integer> ans = new ArrayList<>();
        boolean[] vis = new boolean[v];
        Queue<Integer> q = new LinkedList<Integer>();
        q.add(0);
        vis[0] = true;

        while(!q.isEmpty()){
            int i = q.poll();
            ans.add(i);
            for(int k : adj.get(i)){
                if(!vis[k]){
                    vis[k] = true;
                    q.add(k);
                }
            }
        }
        return ans;
    }
}
