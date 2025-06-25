package DP;

import java.util.Arrays;

public class MinPathSumGrid {

    public static void main(String[] args) {
        int[][] g1 = {{5,9,6},{11,5,2}};
        int[][] g2 = {{1,2,3},{4,5,4},{7,5,9}};

        System.out.println(rec(g1));
        System.out.println(rec(g2));

        System.out.println(memo(g1));
        System.out.println(memo(g2));

        System.out.println(tabu(g1));
        System.out.println(tabu(g2));
    }

    private static int rec(int[][] g) {
        int n = g.length, m= g[0].length;
        return recFunc(n-1,m-1,g);
    }
    private static int recFunc(int i, int j, int[][] g) {
        if(i == 0 && j == 0 ) return g[i][j];
        if(i<0 || j < 0) return ((int)1e4);

        int up = g[i][j] + recFunc(i-1,j,g);
        int left = g[i][j] + recFunc(i,j-1,g);
        return Math.min(up,left);
    }

    private static int memo(int[][] g) {
        int n = g.length, m= g[0].length;
        int[][] dp = new int[n][m];

        for(int[] z : dp) {
            Arrays.fill(z,-1);
        }

        return memoFunc(n-1,m-1,g,dp);
    }

    private static int memoFunc(int i, int j, int[][] g, int[][] dp) {
        if(i ==0 && j == 0) return dp[i][j] = g[0][0];
        if(i <0 || j < 0) return (int)1e9;
        if(dp[i][j] != -1) return dp[i][j];

        int up = g[i][j] + memoFunc(i-1,j,g,dp);
        int left = g[i][j] + memoFunc(i,j-1,g,dp);

        return dp[i][j] = Math.min(up,left);
    }

    private static int tabu(int[][] g) {
        int n = g.length, m = g[0].length;
        int[][] dp = new int[n][m];

        for(int i = 0; i<n;i++) {
            for(int j = 0; j<m; j++) {
                if(i == 0 && j == 0) dp[i][j] = g[i][j];
                else {
                    int up = g[i][j];
                    if(i>0) up += dp[i-1][j];
                    else up += (int)1e9;
                    int left = g[i][j];
                    if(j>0) left += dp[i][j-1];
                    else left += (int)1e9;

                    dp[i][j] = Math.min(up,left);
                }
            }
        }

        return dp[n-1][m-1];
    }
}
