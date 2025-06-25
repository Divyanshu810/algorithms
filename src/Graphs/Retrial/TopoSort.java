package Graphs.Retrial;

import java.util.ArrayList;
import java.util.Stack;

public class TopoSort {

    public static void main(String[] args) {
        int V = 6;
        ArrayList<ArrayList<Integer>> adj = new ArrayList<>();
        for (int i = 0; i < V; i++) {
            adj.add(new ArrayList<>());
        }
        adj.get(2).add(3);
        adj.get(3).add(1);
        adj.get(4).add(0);
        adj.get(4).add(1);
        adj.get(5).add(0);
        adj.get(5).add(2);

        int[] ans = topoSort(V, adj);
        for (int node : ans) {
            System.out.print(node + " ");
        }
        System.out.println("");
    }

    private static int[] topoSort(int n, ArrayList<ArrayList<Integer>> adj) {
        boolean[] v = new boolean[n];
        Stack<Integer> st = new Stack<>();

        for(int i = 0; i<n; i++) {
            if(!v[i]){
                dfs(i, adj, v, st);
            }
        }
        int[] ans = new int[n];
        int i =0;
        while(!st.isEmpty()) {
            ans[i++]  = st.pop();
        }
        return ans;
    }

    private static void dfs(int n, ArrayList<ArrayList<Integer>> adj, boolean[] v, Stack<Integer> st) {
        v[n] = true;

        for(int i : adj.get(n)) {
            if(!v[i]) {
                dfs(i, adj, v, st);
            }
        }
        st.add(n);
    }


}
