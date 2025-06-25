package Graphs.ConnectedComponents;

import java.util.ArrayList;

public class NumberOfProvinces {

    //https://www.geeksforgeeks.org/problems/number-of-provinces/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=number_of_provinces

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for(int i = 0; i < 9; i++){
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(2);
        adj.get(2).add(0);
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(5).add(3);
        adj.get(3).add(5);
        adj.get(6).add(4);
        adj.get(4).add(6);
        adj.get(6).add(7);
        adj.get(7).add(6);

        connectedComponents(adj, 9);

    }

    private static void connectedComponents(ArrayList<ArrayList<Integer>> adj, int V){
        boolean[] vis = new boolean[V+1];
        int cnt = 0;
        for(int i = 0; i< V; i++){
            if(!vis[i]){
                cnt++;
                dfsRec(adj, vis, i);
            }
        }
        System.out.println(cnt);
    }

    private static void dfsRec(ArrayList<ArrayList<Integer>> adj, boolean[] vis, int n){
        vis[n] = true;
        for(int i: adj.get(n)){
            if(!vis[i]){
                dfsRec(adj, vis, i);
            }
        }
    }

}
