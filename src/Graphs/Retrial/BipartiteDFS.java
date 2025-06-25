package Graphs.Retrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BipartiteDFS {

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

        System.out.println(isBipartiteDFS(adj));
        System.out.println(isBipartiteDFS(adjw));

    }

    private static boolean isBipartiteDFS(List<List<Integer>> adj) {
        int[] v = new int[adj.size()];
        for(int i = 0; i<adj.size(); i++)
            v[i] = -1;

        for(int i = 0; i<adj.size(); i++) {
            if(v[i] == -1) {
                if(!dfs(i, v, 1, adj)) return false;
            }
        }
        return true;

    }

    private static boolean dfs(int n, int[] v, int c, List<List<Integer>> adj) {
        v[n] = c;

        for(int k : adj.get(n)) {
            if(v[k] == -1){
                if(!dfs(k, v, 1-c, adj)) return false;
            }
            else if (v[k] == v[n])
                return false;
        }
        return true;
    }
}
