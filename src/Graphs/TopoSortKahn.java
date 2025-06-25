package Graphs;

import java.util.*;

public class TopoSortKahn {

    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(1));
        adj.add(Arrays.asList(2));
        adj.add(Arrays.asList());
        adj.add(Arrays.asList(1,2));
        topoSortKahn(adj);
    }

    private static void topoSortKahn(List<List<Integer>> adj) {
        Queue<Integer> q = new LinkedList<>();
        int[] ind = new int[adj.size()];

        for(int i = 0; i<adj.size(); i++) {
            for(int j : adj.get(i)) {
                ind[j]++;
            }
        }
        for(int i = 0; i<adj.size(); i++) {
            if(ind[i] == 0)
                q.add(i);
        }

        int[] ans = new int[adj.size()];
        int i = 0;
        while(!q.isEmpty()) {
            int n = q.poll();
            ans[i++] = n;

            for(int j : adj.get(n)) {
                ind[j]--;
                if(ind[j] == 0)
                    q.add(j);
            }
        }

        for(int z = 0; z<i; z++) {
            System.out.println(ans[z]);
        }

    }
}
