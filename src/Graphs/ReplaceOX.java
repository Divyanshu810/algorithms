package Graphs;

public class ReplaceOX {

    //https://www.geeksforgeeks.org/problems/replace-os-with-xs0052/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=replace-os-with-xs

    public static void main(String[] args) {
        char[][] a = {{'X', 'X', 'X', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'O', 'O', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'X', 'O', 'O'}};

        char[][] b = {{'X', 'O', 'X', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'O', 'O', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'X', 'O', 'O'}};

        replace(5, 4, b);
        for(int i = 0; i< 5; i++){
            for(int j = 0; j< 4; j++){
                System.out.print(b[i][j]);
            }
            System.out.println();
        }
    }

    private static char[][] replace(int n, int m, char[][] a){
        boolean[][] vis = new boolean[n][m];
        int[] delR = {-1, 0, 1, 0};
        int[] delC = {0, 1, 0, -1};

        //col
        for(int i = 0; i< n; i++){
            if(!vis[i][0] && a[i][0] == 'O'){
                dfs(vis, i, 0, a, delR, delC);
            }

            if(!vis[i][m-1] && a[i][m-1] == 'O'){
                dfs(vis, i, m-1, a, delR, delC);
            }
        }

        //row
        for(int j = 0; j< m; j++){
            if(!vis[0][j] && a[0][j] == 'O'){
                dfs(vis, 0, j, a, delR, delC);
            }

            if(!vis[n-1][j] && a[n-1][j] == 'O')
                dfs(vis, n-1, j, a, delR, delC);
        }

        for(int i = 0; i< n; i++){
            for(int j = 0; j< m; j++){
                if(a[i][j] == 'O' && !vis[i][j])
                    a[i][j] = 'X';
            }
        }
        return a;
    }

    private static void dfs(boolean[][] vis, int ro, int co, char[][] a, int[] delR, int[] delC){
        vis[ro][co] = true;
        int n = a.length;
        int m = a[0].length;

        for(int i = 0; i < 4; i ++){
            int mr = ro + delR[i];
            int mc = co + delC[i];
            if(mr<n && mc < m && mr >=0 && mc >=0 && !vis[mr][mc] && a[mr][mc] == 'O')
                dfs(vis, mr, mc, a, delR, delC);
        }
    }

}
