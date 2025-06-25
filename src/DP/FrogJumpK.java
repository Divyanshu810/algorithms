package DP;

import java.util.Arrays;

public class FrogJumpK {

    public static void main(String[] args) {
        int n = 5, k = 3;
        int[] h = {10,30,40,50,20};
        System.out.println(frJKR(n-1,k,h));
        System.out.println(frJKM(n-1,k,h));
        System.out.println(frJKT(n-1,k,h));

        int m = 10, k1 = 4;
        int[] h1 = {40,10,20,70,80,10,20,70,80,60};
        System.out.println(frJKR(m-1,k1,h1));
        System.out.println(frJKM(m-1,k1,h1));
        System.out.println(frJKT(m-1,k1,h1));
    }

    private static int frJKR(int n, int k, int[] h) {
        if(n ==0) return 0;
        int minE = Integer.MAX_VALUE;
        for(int j = 1; j<=k; j++) {
            if(n-j>=0) {
                int jE = frJKR(n-j,k,h) + Math.abs(h[n] - h[n-j]);
                minE = Math.min(minE, jE);
            }
        }
        return minE;
    }

    private static int frJKM(int n, int k, int[] h) {
        int[] dp = new int[n+1];
        Arrays.fill(dp,-1);
        dp[0] = 0;
        frJKMR(n,k,h,dp);
        return dp[n];
    }
    private static int frJKMR(int n, int k, int[] h, int[] dp) {
        if(n == 0) return 0;
        if(dp[n] != -1) return dp[n];
        int minE = Integer.MAX_VALUE;
        for(int j = 1; j<=k; j++) {
            if(n-j>=0) {
                int jE = frJKMR(n-j,k,h,dp) + Math.abs(h[n] - h[n-j]);
                minE = Math.min(minE, jE);
            }
        }
        return dp[n] = minE;
    }

    //todo
    private static int frJKT(int n, int k, int[] h) {
        int[] dp = new int[n];
        dp[0] = 0;
        for(int i = 1; i<n; i++) {
            int minE = Integer.MAX_VALUE;
            for(int j =1; j<=k; j++) {
                if(i-j >=0) {
                    int jE = dp[i - j] + Math.abs(h[i] - h[i-j]);
                    minE = Math.min(minE, jE);
                }
            }
            dp[i] = minE;
        }
        return dp[n-1];
    }
}
