package DP;


import java.util.ArrayList;

public class TotalUniquePathsGrid {

    public static void main(String[] args) {
        System.out.println(rec(2,2));
        System.out.println(rec(3,2));
        System.out.println(rec(1,6));
        System.out.println(memo(2,2));
        System.out.println(memo(3,2));
        System.out.println(memo(1,6));
        System.out.println(tabu(2,2));
        System.out.println(tabu(3,2));
        System.out.println(tabu(1,6));
        System.out.println(sO(2,2));
        System.out.println(sO(3,2));
        System.out.println(sO(1,6));
    }

    private static int rec(int m, int n) {
        return funcR(m-1,n-1);
    }
    private static int funcR(int i, int j) {
        if(i == 0 && j == 0) return 1;
        if(i < 0 || j<0) return 0;

        int up = funcR(i-1,j);
        int left = funcR(i,j-1);
        return up + left;
    }

    private static int memo(int m, int n) {
        int[][] dp = new int[m][n];
        dp[0][0] = 1;
        for(int i = 0; i<m; i++) {
            for(int k = 0; k<n; k++) {
                dp[i][k] = -1;
            }
        }
        return funcM(m-1,n-1,dp);
    }
    private static int funcM(int i, int j, int[][] dp) {
        if( i == 0 && j == 0) return 1;
        if(i<0 || j<0) return 0;
        if(dp[i][j] != -1) return dp[i][j];

        int up = funcM(i-1, j, dp);
        int left = funcM(i,j-1,dp);
        return dp[i][j] = up + left;
    }

    private static int tabu(int m, int n) {
        int[][] dp = new int[m][n];

        for(int i = 0; i<m; i++) {
            for(int j = 0; j<n; j++) {
                if(i ==0 && j == 0) {
                    dp[i][j] = 1;
                    continue;
                }
                int up = 0, left= 0;

                if(i>0) up = dp[i-1][j];
                if(j>0) left = dp[i][j-1];
                dp[i][j] = up+left;
            }
        }
        return dp[m-1][n-1];
    }

    private static int sO(int m, int n) {
        int[] dp = new int[n];

        for(int i = 0; i<m; i++) {
            int[] tmp = new int[n];
            for(int j = 0; j<n; j++) {
                if(i == 0 && j == 0) {
                    tmp[j] = 1;
                    continue;
                }
                int up = 0, left = 0;
                up = dp[j];
                if(j>0) left = tmp[j-1];

                tmp[j] = up + left;
            }
            dp = tmp;
        }
        return dp[n-1];
    }
}
