package Graph;

import java.util.Arrays;
import java.util.PriorityQueue;

class Tuple {
    int wt;
    int ro;
    int co;

    Tuple (int wt, int ro, int co) {
        this.wt = wt;
        this.ro = ro;
        this.co = co;
    }
}

public class PathWithMinimumEffort {

    public static void main(String[] args) {

        int[][] h = {{1,2,2},{3,8,2},{5,3,5}};
        System.out.println(minEffortPath(3,3,h));
        int[][] h2 = {{7,7},{7,7}};
        System.out.println(minEffortPath(2,2,h2));

    }

    private static int minEffortPath(int rows, int cols, int[][] h) {
        PriorityQueue<Tuple> pq = new PriorityQueue<>((x,y)-> x.wt - y.wt);
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};
        int[][] dis = new int[rows][cols];

        for(int i = 0; i<rows; i++) {
            Arrays.fill(dis[i], (int)1e9);
        }

        pq.add(new Tuple(0, 0, 0));
        dis[0][0] = 0;
        while(!pq.isEmpty()) {
            Tuple t = pq.poll();
            int dist = t.wt, ro = t.ro, co = t.co;
            if(ro == rows-1 && co == cols-1)
                return dist;
            for(int i = 0; i<4; i++) {
                int mr = ro + dr[i];
                int mc = co + dc[i];

                if(mr >= 0 && mr <rows && mc >=0 && mc < cols) {
                    int w = Math.max(Math.abs(h[mr][mc] - h[ro][co]), dist);
                    if(w < dis[mr][mc]) {
                        pq.add(new Tuple(w, mr, mc));
                        dis[mr][mc] = w;
                    }
                }
            }
        }
        return 0;

    }

}
