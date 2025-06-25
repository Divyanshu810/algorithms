package Graph;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class ShortestPathInBinaryMaze {

    class Trip {
        int w;
        int i;
        int j;

        Trip(int w, int i, int j) {
            this.w = w;
            this.i = i;
            this.j = j;
        }
    }

    class Solution {

        int shortestPath(int[][] g, int[] s, int[] d) {

            // Your code here

            if(s[0] == d[0] && s[1] == d[1])
                return 0;

            int[][] dist = new int[g.length][g[0].length];
            for(int i = 0;i < g.length; i++) {
                Arrays.fill(dist[i], (int)1e9);
            }
            dist[s[0]][s[1]] = 0;
            int[] delR = {-1, 0, 1, 0};
            int[] delC = {0, 1, 0, -1};

            Queue<Trip> q = new LinkedList<>();

            q.add(new Trip(0, s[0], s[1]));

            while(!q.isEmpty()) {
                Trip t = q.poll();
                int dis = t.w, r = t.i, c = t.j;

                for(int i = 0; i<4; i++) {
                    int mr = r + delR[i], mc = c + delC[i];

                    if(mr >=0 && mr < g.length && mc >=0 && mc < g[0].length
                            && g[mr][mc] == 1 && 1 + dis < dist[mr][mc]) {
                        dist[mr][mc] = 1 +dis;
                        if(mr == d[0] && mc == d[1])
                            return dist[mr][mc];
                        q.add(new Trip(dist[mr][mc], mr, mc));
                    }
                }
            }



            return -1;
        }
    }
}
