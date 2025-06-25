package recursion;

import java.util.ArrayList;
import java.util.List;

public class RatMaze {

    public static void main(String[] args) {
        int n = 4;
        int[][] a = {{1,0,0,0},{1,1,0,1},{1,1,0,0},{0,1,1,1}};
        combinations(a,n);
    }

    private static void combinations(int[][] maze, int n){
        List<String> ans = new ArrayList<>();
        String path = "";
        int[] di = {1,0,0,-1};
        int[] dj = {0,-1,1,0};
        int[][] vis = new int[n][n];
        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                vis[i][j] = 0;
            }
        }
        mazeEscape(0,0,ans, path, di, dj, vis, maze, n);
        for(String s : ans){
            System.out.println(s);
        }

    }
    private static void mazeEscape(int i, int j, List<String> ans, String path, int[] di, int[] dj, int[][] vis, int[][] maze, int n){
        if(i == n-1 && j == n-1){
            ans.add(path);
            return;
        }

        String dir = "DLRU";
        for(int ind = 0; ind < 4; ind++){
            int nexti = i+ di[ind];
            int nextj = j + dj[ind];
            if(nexti >=0 && nextj >= 0 && nexti <n && nextj < n && maze[nexti][nextj] == 1 && vis[nexti][nextj] == 0){
                vis[i][j] = 1;
                mazeEscape(nexti, nextj, ans, path + dir.charAt(ind), di, dj, vis, maze, n);
                vis[i][j] = 0;
            }
        }


    }
}
