package Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Stack;

class Pair {
    int n;
    int w;

    public Pair (int n, int w) {
        this.n = n;
        this.w = w;
    }
}
//10 24
//0 2 6
//0 3 7
//0 4 9
//0 6 8
//0 7 6
//1 2 6
//1 3 7
//1 5 10
//1 6 1
//1 7 4
//2 3 3
//2 6 10
//2 8 8
//2 9 10
//3 5 3
//3 6 10
//3 7 5
//5 6 9
//5 7 7
//6 7 7
//6 8 8
//6 9 8
//7 9 1
//8 9 6
public class ShortestPathDAG {
    public static void main(String[] args) {
        int[][] ed = {{0,2,6}, {0,3,7}, {0,4,9}, {0,6,8}, {0,7,6}, {1,2,6}, {1,3,7}, {1,5,10}, {1,6,1}, {1,7,4}, {2,3,3},
                {2,6,10}, {2,8,8}, {2,9,10}, {3,5,3}, {3,6,10}, {3,7,5}, {5,6,9}, {5,7,7}, {6,7,7}, {6,8,8}, {6,9,8}, {7,9,1}, {8,9,6}};
        shortestPaths(ed, 10, 24);
    }

    private static void topo(int n, int[] v, ArrayList<ArrayList<Pair>> adj, Stack<Integer> s) {
        v[n] = 1;

        for(Pair p : adj.get(n)) {
            int a = p.n;
            if(v[a] == 0)
                topo(a, v, adj, s);
        }
        s.add(n);
    }
    private static void shortestPaths(int[][] ed, int V, int E) {
        ArrayList<ArrayList<Pair>> adj = new ArrayList<>();
        for(int i = 0; i<V; i++) {
            adj.add(new ArrayList<Pair>());
        }
        int[] d = new int[V];
        Arrays.fill(d, (int) 1e9);

        for(int i = 0; i<E; i++) {
            int u = ed[i][0], v = ed[i][1], w = ed[i][2];
            adj.get(u).add(new Pair(v,w));
        }
        int[] v = new int[V];
        Stack<Integer> s = new Stack<>();
        for(int i = 0; i<V; i++) {
            if(v[i] == 0)
                topo(i, v, adj, s);
        }

        d[0] = 0;
        while(!s.empty()) {
            int z = s.pop();

            for(Pair p : adj.get(z)) {
                int k = p.n;
                int w = p.w;
                if(d[z] + w < d[k])
                    d[k] = d[z] + w;
            }
        }
        for(int i = 0; i<V; i++) {
            if(d[i] == (int)1e9)
                System.out.println(-1);
            else
                System.out.println(d[i]);
        }

    }
}
