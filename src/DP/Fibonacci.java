package DP;

import java.util.Arrays;

public class Fibonacci {

    public static void main(String[] args) {
//        System.out.println(fibo(100000000));
        int n = 10000;
        int[] dp = new int[n+1];
        Arrays.fill(dp,-1);
        dp[0] = 0; dp[1] = 1;
        System.out.println(fiboM(n,dp));

        System.out.println(fiboT(n));
        System.out.println(fiboSO(n));
    }
    private static int fibo(int n) {
        if(n == 0) return 0;
        if(n == 1) return 1;

        return fibo(n-1) + fibo(n-2);
    }

    private static int fiboM(int n, int[] dp) {
        if(n == 0) return 0;
        if(n == 1) return 1;
        if(dp[n] != -1) return dp[n];

        return dp[n] =  fiboM(n-1,dp) + fiboM(n-2,dp);
    }

    private static int fiboT(int n) {
        int[] dp = new int[n+1];

        dp[0] = 0; dp[1] = 1;

        for(int i = 2; i<=n ; i++) {
            dp[i] = dp[i-1] + dp[i-2];
        }
        return dp[n];
    }

    private static int fiboSO(int n) {
        int prev = 0, prev2 = 1;
        for(int i = 1; i<=n; i++) {
            int curI = prev + prev2;
            prev2 = prev;
            prev = curI;
        }
        return prev;
    }

}
