package Graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

public class ShortestDistBinaryMaze {

    public static void main(String[] args) {
      int[][] g =   {{1, 1, 1, 1},
            {1, 1, 0, 1},
            {1, 1, 1, 1},
            {1, 1, 0, 0},
            {1, 0, 0, 1}};
      int[] s = {0,1};
      int[] d = {2,2};

        System.out.println(shortestPath(g, s, d));

        int[][] f = {{1, 1, 1, 1, 1},
                {1, 1, 1, 1, 1},
                {1, 1, 1, 1, 0},
                {1, 0, 1, 0, 1}};
        int[] s1 = {0,0};
        int[] d1 = {3,4};
        System.out.println(shortestPath(f,s1,d1));
    }

    private static int shortestPath(int[][] g, int[] s, int[] d) {
        int[][] dist = new int[g.length][g[0].length];
        for(int i = 0; i<g.length; i++) {
            Arrays.fill(dist[i], (int)1e9);
        }
        dist[s[0]][s[1]] = 0;
        Queue<Tuple> pq = new LinkedList<>();
        pq.add(new Tuple(0, s[0], s[1]));

        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};

        while(!pq.isEmpty()) {
            Tuple t = pq.poll();

            int wt = t.wt, ro = t.ro, co = t.co;

            for(int i = 0; i<4; i++) {
                int mr = ro + dr[i];
                int mc = co + dc[i];

                if(mr >=0 && mr < g.length && mc >=0 && mc <g[0].length && g[mr][mc] == 1) {
                    int dis= wt + 1;
                    if(dis < dist[mr][mc]) {
                        dist[mr][mc] = dis;
                        if(mr == d[0] && mc == d[1])
                            return dis;
                        pq.add(new Tuple(dis, mr, mc));
                    }
                }
            }
        }
        return -1;


    }
}
