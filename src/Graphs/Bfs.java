package Graphs;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Bfs {

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
        Queue<Integer> q =new LinkedList<>();
        q.add(0);
        boolean[] vis = new boolean[v];
        vis[0] = true;
        ArrayList<Integer> bfs = new ArrayList<>();

        while(!q.isEmpty()){
            int a = q.poll();
            bfs.add(a);
            for(int i : adj.get(a) ){
                if(!vis[i]){
                    vis[i] = true;
                    q.add(i);
                }
            }

        }
        return bfs;

    }
}
