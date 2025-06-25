package Graphs;

import java.util.*;

class PairI{
    int i;
    int j;
    public PairI(int k, int l){
        this.i = k;
        this.j = l;
    }
}

public class DetectCycleBFS {

    public static void main(String[] args) {

        List<List<Integer>> adj = new ArrayList<>();
        //[[1], [0, 2, 4], [1, 3], [2, 4], [1, 3]]
        adj.add(Arrays.asList(1));
        adj.add(Arrays.asList(0,2,4));
        adj.add(Arrays.asList(1,3));
        adj.add(Arrays.asList(2,4));
        adj.add(Arrays.asList(1,3));
        System.out.println(detectCycle(5, adj));

    }

    private static boolean detectCycle(int V, List<List<Integer>> adj){
        boolean[] vis = new boolean[V];
        for(int i = 0; i< V; i++){
            if(!vis[i]){
                if(bfs(i, adj, vis))
                    return true;
            }
        }
        return false;
    }

    private static boolean bfs(int n, List<List<Integer>> adj, boolean[] vis){
        Queue<PairI> q = new LinkedList<>();
        q.add(new PairI(n,-1));
        vis[n] = true;

        while(!q.isEmpty()){
            PairI p = q.poll();
            int node = p.i;
            int parent = p.j;
            for( int i : adj.get(node)){
                if(!vis[i]){
                    vis[i] = true;
                    q.add(new PairI(i,node));
                } else if (parent != i) {
                    return true;
                }
            }
        }
        return false;
    }

}
