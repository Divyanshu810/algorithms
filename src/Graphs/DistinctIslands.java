package Graphs;

import java.util.ArrayList;
import java.util.HashSet;

class Pairz{
    int i;
    int j;

    public Pairz(int i, int j){
        this.i = i;
        this.j = j;
    }
}

public class DistinctIslands {

    //https://www.geeksforgeeks.org/problems/number-of-distinct-islands/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=number-of-distinct-islands

    public static void main(String[] args) {

        int [][] g = {{1, 1, 0, 0, 0},
                {1, 1, 0, 0, 0},
                {0, 0, 0, 1, 1},
                {0, 0, 0, 1, 1}};
        System.out.println(distinctIslands(g));

        int[][] h = {{1, 1, 0, 1, 1},
                {1, 0, 0, 0, 0},
                {0, 0, 0, 0, 1},
                {1, 1, 0, 1, 1}};
        System.out.println(distinctIslands(h));
    }

    private static int distinctIslands(int[][] g){
        int n = g.length, m = g[0].length;
        boolean[][] vis = new boolean[n][m];
        HashSet<ArrayList<String>> h = new HashSet<>();
        int[] delR = {-1, 0, 1, 0};
        int[] delC = { 0, 1, 0, -1};

        for(int i = 0; i< n; i++){
            for(int j = 0; j< m; j++){
                if(!vis[i][j] && g[i][j] == 1){
                    ArrayList<String> a = new ArrayList<>();
                    dfs(vis, i, j, a, i, j, delR, delC, g);
                    h.add(a);
                }
            }
        }
//        for(ArrayList<String> i : h){
//            for(String p : i){
//                System.out.println(p);
//            }
//        }
        return h.size();
    }

    private static void dfs(boolean[][] vis, int r, int c, ArrayList<String> a, int ir, int ic, int[] delR, int[] delC, int[][] g){
        vis[r][c] = true;
        a.add(toString(r - ir, c - ic));
        int n = g.length, m  = g[0].length;

        for(int i = 0; i < 4; i++){
            int nr = r + delR[i];
            int nc = c + delC[i];

            if(nr < n && nr >=0 && nc < m && nc >=0 && !vis[nr][nc] && g[nr][nc] == 1)
                dfs(vis, nr, nc, a, ir, ic, delR, delC, g);
        }
    }
    private static String toString(int i, int j){
        return Integer.toString(i) + Integer.toString(j);
    }


}
