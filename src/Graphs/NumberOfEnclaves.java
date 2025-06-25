package Graphs;

import java.util.LinkedList;
import java.util.Queue;

//https://www.geeksforgeeks.org/problems/number-of-enclaves/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=number-of-enclaves

class Pairs {
    int i;
    int j;
    public Pairs(int i, int j){
        this.i = i;
        this.j = j;
    }
}

public class NumberOfEnclaves {

    public static void main(String[] args) {
        int[][] g = {{0, 0, 0, 0},
                {1, 0, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 0}};
        System.out.println(enclaves(g));

        int[][] h = {{0, 0, 0, 1},
                {0, 1, 1, 0},
                {0, 1, 1, 0},
                {0, 0, 0, 1},
                {0, 1, 1, 0}};
        System.out.println(enclaves(h));
    }

    private static int enclaves(int[][] g){
        int n = g.length, m = g[0].length;
        boolean[][] vis = new boolean[n][m];

        Queue<Pairs>  q = new LinkedList<>();
        for(int i = 0; i<n ; i++){
            if(g[i][0] == 1){
                vis[i][0] = true;
                q.add(new Pairs(i, 0));
            }
            if(g[i][m-1] == 1){
                vis[i][m-1] = true;
                q.add(new Pairs(i, m-1));
            }
        }
        for(int j = 0; j<m; j++){
            if(g[0][j] == 1){
                vis[0][j] = true;
                q.add(new Pairs(0, j));
            }
            if(g[n-1][j] == 1){
                vis[n-1][j] = true;
                q.add(new Pairs(n-1, j));
            }
        }

        int[] delR = {-1, 0, 1, 0};
        int[] delC = {0, 1, 0, -1};

        while(!q.isEmpty()){
            Pairs p = q.poll();
            int r = p.i;
            int c = p.j;

            for(int i = 0; i< 4; i++){
                int mr = r + delR[i];
                int mc = c + delC[i];
                if(mr >= 0 && mr < n && mc >=0 && mc < m && !vis[mr][mc] && g[mr][mc] == 1){
                    vis[mr][mc] = true;
                    q.add(new Pairs(mr, mc));
                }
            }
        }
        int cnt = 0;
        for(int i = 0; i< n; i ++){
            for(int j = 0; j< m; j++){
                if(g[i][j] == 1 && !vis[i][j])
                    cnt++;
            }
        }
        return cnt;
    }
}
