package Graphs.Retrial;

import java.util.LinkedList;
import java.util.Queue;

class Pair {
    int f;
    int s;
    public Pair(int f, int s) {
        this.f = f;
        this.s = s;
    }
}

public class NumberOfIslands {

    public static void main(String[] args) {

        char[][] g = {{'0','1'},{'1','0'},{'1','1'},{'1','0'}};
        char[][] h = {{'0','1','1','1','0','0','0'},{'0','0','1','1','0','1','0'}};
        System.out.println(numberOfIslandsDFS(g));
        System.out.println(numberOfIslandsDFS(h));

        System.out.println(numberOfIslandsBFS(g));
        System.out.println(numberOfIslandsBFS(h));
    }

    private static int numberOfIslandsDFS(char[][] g) {
        int n = g.length, m = g[0].length;
        boolean[][] vis = new boolean[n][m];
        int cnt = 0;

        for(int i = 0; i<n; i++) {
            for(int j = 0; j<m; j++) {
                if(!vis[i][j] && g[i][j] == '1') {
                    dfs(i,j,g,vis);
                    cnt++;
                }
            }
        }
        return cnt;
    }

    private static int numberOfIslandsBFS(char[][] g) {
        int n = g.length, m = g[0].length;
        boolean[][] vis = new boolean[n][m];
        Queue<Pair> q = new LinkedList<>();
        int cnt = 0;

        for(int i = 0; i<n; i++) {
            for(int j = 0; j<m; j++) {
                if(!vis[i][j] && g[i][j] == '1') {
                    bfs(i,j,g,vis,q);
                    cnt++;
                }
            }
        }
        return cnt;
    }


    private static void dfs(int i, int j, char[][] g, boolean[][] vis) {
        vis[i][j] = true;
        int n = g.length, m = g[0].length;

        for(int dr = -1; dr<=1; dr++) {
            for(int dc = -1; dc<=1; dc++) {
                int mr = i+dr;
                int mc = j+dc;
                if(mr>=0 && mr<n && mc>=0 && mc <m && g[mr][mc] == '1' && !vis[mr][mc]) {
                    dfs(mr, mc, g, vis);
                }
            }
        }
    }

    private static void bfs(int i, int j, char[][] g, boolean[][] v, Queue<Pair> q) {
        v[i][j] = true;
        q.add(new Pair(i,j));
        int n = g.length, m = g[0].length;

        while(!q.isEmpty()) {
            Pair p = q.poll();
            int r = p.f, c = p.s;
            v[r][c] = true;
            for(int dr = -1; dr<=1; dr++) {
                for(int dc = -1; dc<=1; dc++) {
                    int mr = dr + r;
                    int mc = dc + c;

                    if(mr >=0 && mr < n && mc>=0 && mc<m && g[mr][mc] == '1' && !v[mr][mc]) {
                        q.add(new Pair(mr,mc));
                    }
                }
            }
        }
    }
}