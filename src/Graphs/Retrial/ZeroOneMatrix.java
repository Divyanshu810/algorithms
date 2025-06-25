package Graphs.Retrial;

import java.util.LinkedList;
import java.util.Queue;

class Trip {
    int r;
    int c;
    int d;

    public Trip (int r, int c, int d) {
        this.r = r;
        this.c = c;
        this.d = d;
    }
}

public class ZeroOneMatrix {

    public static void main(String[] args) {

        int[][] mat = {{0,1,1,0},{1,1,0,0},{0,0,1,1}};
        int[][] ans = new int[3][4];
        ans = distance(mat);

        for(int i = 0; i<3; i++) {
            for(int j = 0; j<4; j++) {
                System.out.print(ans[i][j]);
            }
            System.out.println();
        }

    }
    private static int[][] distance(int[][] ma) {
        int n = ma.length;
        int m = ma[0].length;
        int[][] ans = new int[n][m];
        int[][] vi = new int[n][m];
        Queue<Trip> q = new LinkedList<>();

        for(int i = 0; i<n; i++) {
            for(int j = 0; j<m; j++) {
                if(ma[i][j] == 1) {
                    ans[i][j] = 0;
                    q.add(new Trip(i,j,0));
                    vi[i][j] = 1;
                }
            }
        }
        int[] dr = {-1, 0, 1, 0};
        int[] dc = {0, 1, 0, -1};

        while(!q.isEmpty()) {
            Trip t = q.poll();
            int r = t.r, c = t.c, d = t.d;
            for(int k = 0; k<4; k++) {
                int mr = r + dr[k];
                int mc = c + dc[k];

                if(mr>=0 && mr<n && mc >= 0 && mc < m && vi[mr][mc] !=1) {
                    ans[mr][mc] = d+1;
                    vi[mr][mc] = 1;
                }
            }

        }

        return ans;
    }


}
