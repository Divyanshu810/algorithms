package Graphs.Retrial;

public class ReplaceOXMatrix {

    public static void main(String[] args) {
        char[][] c = {{'X', 'X', 'X', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'O', 'O', 'X'},
                {'X', 'O', 'X', 'X'},
                {'X', 'X', 'O', 'O'}};
        replace(c);
    }

    private static void replace(char[][] c) {
        int[] dr = {-1,0,1,0};
        int[] dc = {0, 1, 0, -1};
        int n = c.length, m = c[0].length;
        int[][] v = new int[n][m];
        for(int j = 0; j<m; j++) {
            if(c[0][j] == 'O' && v[0][j] == 0)
                dfs(0,j,v,c,dr,dc);
            if(c[n-1][j] == 'O' && v[0][j] == 0)
                dfs(n-1,j,v,c,dr,dc);
        }

        for(int i = 0; i<n; i++) {
            if(c[i][0] == 'O' && v[i][0] == 0)
                dfs(i,0,v,c,dr,dc);
            if(c[i][m-1] == 'O' && v[i][m-1] == 0)
                dfs(i,m-1,v,c,dr,dc);
        }

        for(int i = 0; i<n; i++) {
            for(int j= 0; j<m; j++) {
                if(v[i][j] != 1)
                    c[i][j] = 'X';
            }
        }


        for(int i = 0; i<n; i++) {
            for(int j= 0; j<m; j++) {
                System.out.print(c[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void dfs(int r, int c, int[][] v, char[][] ca, int[] dr, int[] dc) {
        v[r][c] = 1;
        int n = ca.length, m = ca[0].length;
        for(int k = 0; k<4; k++) {
            int mr = dr[k] +r;
            int mc = dc[k] + c;

            if(mr< n && mr >=0 && mc < m && mc>=0 && v[mr][mc] == 0 && ca[mr][mc] == 'O') {
                dfs(mr,mc,v,ca,dr,dc);
            }
        }
    }
}
