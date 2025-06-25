package Graphs;

import java.util.*;

public class BipartiteGraphBFS {

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

        System.out.println(isBPCheck(3, 0, adjw));
        System.out.println(isBPCheck(4, 0, adj));

    }

    private static boolean isBPCheck(int V, int s, List<List<Integer>> adj){
        int[] col = new int[V];

        for(int i = 0; i< V; i++) col[i] = -1;

        for(int i = 0; i< V; i++){
            if(col[i] == -1 ){
                if(!bfs(i, V, adj, col))
                    return false;
            }
        }
        return true;
    }

    private static boolean bfs(int n, int V, List<List<Integer>> adj, int[] col){
        Queue<Integer> q = new LinkedList<>();
        q.add(n);
        col[n] = 1;

        while(!q.isEmpty()){
            int no = q.poll();

            for(int i : adj.get(no)){
                if(col[i] == -1 ){
                    col[i] = 1- col[no];
                    q.add(i);
                } else if(col[i] == col[no])
                    return false;
            }
        }
        return true;
    }

}
