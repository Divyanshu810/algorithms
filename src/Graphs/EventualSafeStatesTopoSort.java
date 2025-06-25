package Graph;

import java.util.ArrayList;
import java.util.*;

public class EventualSafeStatesTopoSort {

    public static void main(String[] args) {
        List<List<Integer>> adj = new ArrayList<>();
        adj.add(Arrays.asList(1,2));
        adj.add(Arrays.asList(2,3));
        adj.add(Arrays.asList(5));
        adj.add(Arrays.asList(0));
        adj.add(Arrays.asList(5));
        adj.add(Arrays.asList());
        adj.add(Arrays.asList());
        eventualSafeStates(7,adj);

    }

    private static void eventualSafeStates(int V, List<List<Integer>> adj) {
        List<List<Integer>> adjr = new ArrayList<>();
        List<Integer> ans = new ArrayList<>();
        for(int j = 0; j<V; j++) {
            adjr.add(new ArrayList<>());
        }

        int[] ind = new int[V];
        for(int i =0; i< V; i++) {
            for(int k : adj.get(i)) {
                adjr.get(k).add(i);
                ind[i]++;
            }
        }
        Queue<Integer> q = new LinkedList<>();
        for(int i = 0; i<V; i++) {
            if(ind[i] == 0)
                q.add(i);
        }

        while(!q.isEmpty()) {
            int n = q.poll();
            ans.add(n);
            for(int k : adjr.get(n)) {
                ind[k]--;
                if(ind[k] == 0)
                    q.add(k);
            }
        }

        Collections.sort(ans);

        for(int i : ans)
            System.out.println(i);
    }
}
