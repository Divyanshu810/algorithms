package Graphs;

import java.util.Arrays;

public class BellmanFord {

    public static void main(String[] args) {
        int V = 5;
        int[][] edges = new int[][] {
                {1, 3, 2},
                {4, 3, -1},
                {2, 4, 1},
                {1, 2, 1},
                {0, 1, 5}
        };

        int src = 0;
        int[] ans = bellmanFord(src, V, edges);

        for(int i : ans)
            System.out.println(i);

    }

    private static int[] bellmanFord(int s, int V, int[][] e) {
        int[] d = new int[V];
        Arrays.fill(d, (int)1e8);

        d[s] =0;

        for(int i = 0; i< V; i++) {
            for(int k = 0; k < e.length; k++) {
                int u = e[k][0], v = e[k][1], w = e[k][2];

                if(d[u] != 1e9 && d[u] + w < d[v]) {
                    if(i == V-1)
                        return new int[]{-1};
                    d[v] = d[u] + w;
                }
            }
        }
        return d;
    }
}
