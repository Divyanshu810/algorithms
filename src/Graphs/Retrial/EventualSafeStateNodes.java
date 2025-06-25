package Graphs.Retrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventualSafeStateNodes {

    //https://www.geeksforgeeks.org/problems/eventual-safe-states/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=eventual-safe-states


    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(1,2));
        adj.add(Arrays.asList(2,3));
        adj.add(Arrays.asList(5));
        adj.add(Arrays.asList(0));
        adj.add(Arrays.asList(5));
        adj.add(Arrays.asList());
        adj.add(Arrays.asList());
        safeStateNodes(adj);
    }

    private static void safeStateNodes(List<List<Integer>> adj) {
        int[] v = new int[adj.size()];
        int[] pv = new int[adj.size()];
        int[] c = new int[adj.size()];

        for(int i = 0; i<adj.size(); i++) {
            if(v[i] == 0)
                dfs(i, v, pv, c, adj);
        }

        List<Integer> ans = new ArrayList<>();
        for(int i = 0; i<adj.size(); i++) {
            if(c[i] == 1)
                System.out.println(i + " ");
        }
    }

    private static boolean dfs(int n, int[] v, int[] pv, int[] c, List<List<Integer>> adj) {
        v[n] = 1;
        pv[n] = 1;
        c[n] = 0;

        for(int k : adj.get(n)) {
            if(v[k] == 0) {
                if(dfs(k, v, pv, c, adj))
                    return true;
            } else if (pv[k] == 1)
                return true;
        }

        c[n] = 1;
        pv[n] = 0;
        return false;
    }
}
