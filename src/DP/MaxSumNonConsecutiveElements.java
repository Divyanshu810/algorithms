package DP;

import java.util.Arrays;

public class MaxSumNonConsecutiveElements {

    public static void main(String[] args) {
        int[] nu = {2,1,4,9};
        System.out.println(mSNCERec(nu));
        System.out.println(mSNCEMem(nu));
        System.out.println(mSNCETab(nu));
        System.out.println(mSNCESO(nu));
        int[] nu1 = {1,2,3,1,3,5,8,1,9};
        System.out.println(mSNCERec(nu1));
        System.out.println(mSNCEMem(nu1));
        System.out.println(mSNCETab(nu1));
        System.out.println(mSNCESO(nu1));
    }

    private static int mSNCERec(int[] nu) {
        int n = nu.length;
        return func(n-1, nu);
    }
    private static int func(int n, int[] nu) {
        if(n == 0) return nu[0];
        if(n < 0) return 0;
        int t = nu[n] + func(n-2, nu);
        int nt = 0 + func(n-1, nu);

        return Math.max(t,nt);
    }

    private static int mSNCEMem(int[] nu) {
        int n = nu.length;
        int[] dp = new int[n+1];
        dp[0] = nu[0];
        Arrays.fill(dp,-1);
        return func1( n-1,  nu, dp);
    }
    private static int func1(int n, int[] nu, int[] dp) {
        if(n == 0) return nu[0];
        if(n<0) return 0;
        if(dp[n] != -1) return dp[n];
        int t = nu[n] + func1(n-2, nu, dp);
        int nt = 0 + func1(n-1, nu, dp);

       return dp[n] = Math.max(t, nt);

    }

    private static int mSNCETab(int[] nu) {
        int n = nu.length;
        int[] dp = new int[n +1];
        dp[0] = nu[0];

        for(int i = 1; i<n; i++) {
            int t = nu[i];
            if(i>1) t+=dp[i-2];
            int nt = 0 + dp[i-1];

            dp[i] = Math.max(t,nt);
        }
        return dp[n-1];
    }

    private static int mSNCESO(int[] nu) {
        int prev = nu[0], prev2 = 0;
        int n = nu.length;
        for(int i = 1; i<n; i++) {
            int t = nu[i] + prev2;
            int nt = 0 + prev;

            int curI = Math.max(t, nt);
            prev2 = prev;
            prev = curI;
        }
        return prev;
    }

}
