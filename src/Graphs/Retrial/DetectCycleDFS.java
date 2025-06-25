package Graphs.Retrial;

import java.util.ArrayList;

public class DetectCycleDFS {

    public static void main(String[] args) {
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            adj.add(new ArrayList<>());
        }
        adj.get(0).add(1);
        adj.get(1).add(0);
        adj.get(1).add(2);
        adj.get(1).add(4);
        adj.get(2).add(1);
        adj.get(2).add(3);
        adj.get(3).add(2);
        adj.get(3).add(4);
        adj.get(4).add(1);
        adj.get(4).add(3);

        System.out.println(detectCycleDFS(adj));
    }

    private static boolean detectCycleDFS(ArrayList<ArrayList<Integer>> adj) {
        boolean[] v = new boolean[adj.size()];

        for(int i = 0; i<adj.size(); i++) {
            if(!v[i]) {
                if(dfs(i, -1,v, adj)) return true;
            }
        }
        return false;
    }

    private static boolean dfs(int n, int p, boolean[] v, ArrayList<ArrayList<Integer>> adj) {
        v[n] = true;
        for(int i : adj.get(n)) {
            if(!v[i]){
                if(dfs(i,n,v,adj)) return true;
            } else if(i != p)
                return true;
        }
        return false;
    }
}
