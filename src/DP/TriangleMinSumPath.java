package DP;

import java.util.Arrays;

public class TriangleMinSumPath {

    public static void main(String[] args) {
        int[][] t1 = {{1},{2,3},{3,6,7},{8,9,6,10}};//[[1], [2,3], [3,6,7], [8,9,6,1]]
        int[][] t2 = {{2},{3,4},{6,5,7},{4,1,8,3}};
        int[][] t3 = {{5},{-1,3},{22,1,-9}};

        System.out.println(rec(t1,4));
        System.out.println(rec(t2,4));
        System.out.println(rec(t3,3));

        System.out.println(memo(t1,4));
        System.out.println(memo(t2,4));
        System.out.println(memo(t3,3));

        System.out.println(tabu(t1,4));
        System.out.println(tabu(t2,4));
        System.out.println(tabu(t3,3));

        System.out.println(sO(t1,4));
        System.out.println(sO(t2,4));
        System.out.println(sO(t3,3));
    }

    private static int rec(int[][] t, int n) {
        return recF( 0,  0,  t,  n);
    }
    private static int recF(int i, int j, int[][] t, int n) {
        if(i == n-1) return t[i][j];

        int d = t[i][j] + recF(i+1,j,t,n);
        int di = t[i][j] + recF(i+1,j+1,t,n);

        return Math.min(d,di);
    }

    private static int memo(int[][] t, int n) {
        int[][] dp = new int[n][n];
        for(int[] l : dp) {
            Arrays.fill(l,-1);
        }
        return memoF(0,0,t,n,dp);
    }
    private static int memoF(int i, int j, int[][] t, int n, int[][] dp) {
        if(i == n-1) return dp[i][j] = t[i][j];
        if(dp[i][j] != -1) return dp[i][j];

        int d = t[i][j] + memoF(i+1,j,t, n, dp);
        int di = t[i][j] + memoF(i+1,j+1,t,n,dp);

        return dp[i][j] = Math.min(d,di);
    }

    private static int tabu(int[][] t, int n) {
        int[][] dp = new int[n][n];

        for(int i = 0; i<n; i++) {
            dp[n-1][i] = t[n-1][i];
        }

        for(int i = n-2; i>=0; i--) {
            for(int j = i; j>=0; j--) {

                int d = t[i][j] + dp[i+1][j];
                int di = t[i][j] + dp[i+1][j+1];

                dp[i][j] = Math.min(d,di);
            }
        }
        return dp[0][0];
    }

    private static int sO(int[][] t, int n) {
        int[] dp = new int[n];
        for(int i = 0; i<n; i++) {
            dp[i] = t[n-1][i];
        }

        for(int i = n-2; i>=0; i--) {
            int[] cur = new int[n];
            for(int j = i; j>=0; j--) {
                int d = t[i][j] + dp[j];
                int di = t[i][j] + dp[j+1];

                cur[j] = Math.min(d,di);
            }
            dp = cur;
        }
        return dp[0];
    }

}
