package DP;

import java.util.Arrays;

public class FrogJump {
    //https://www.naukri.com/code360/problems/frog-jump_3621012?source=youtube&campaign=striver_dp_videos&utm_source=youtube&utm_medium=affiliate&utm_campaign=striver_dp_videos&leftPanelTabValue=PROBLEM

    public static void main(String[] args) {
        int n = 4;
        int[] h = {10,20,30,10};
        System.out.println(frJRec(n-1,h));
        System.out.println(frJM(n-1,h));
        System.out.println(frJT(n-1,h));
        System.out.println(frJSO(n-1,h));
        System.out.println(frJR(n-1,h));

        int m = 8;
        int[] h2 = {7,4,4,2,6,6,3,4};
        System.out.println(frJRec(m-1,h2));
        System.out.println(frJM(m-1,h2));
        System.out.println(frJT(m-1,h2));
        System.out.println(frJSO(m-1,h2));
        System.out.println(frJR(m-1,h2));
    }

    private static int frJR(int n, int[] h) {
        if(n == 0) return 0;
        if(n == 1) return Math.abs(h[n] - h[n-1]);
        int l = Math.abs(h[n] - h[n-1]) + frJR(n-1, h);
        int r = Math.abs(h[n] - h[n-2])  + frJR(n-2,h);

        return Math.min(l,r);
    }

    private static int frJRec(int n, int[] h) {
        if(n == 0) return 0;

        int l = frJRec(n-1, h) + Math.abs(h[n] - h[n-1]);
        int r = Integer.MAX_VALUE;
        if(n >1)
            r = frJRec(n-2,h) + Math.abs(h[n] - h[n-2]);
        return Math.min(l,r);
    }

    private static int frJM(int n, int[] h) {
        int[] dp = new int[n+1];
        Arrays.fill(dp,-1);
        dp[0] = 0;
        frJMR(n,h,dp);
        return dp[n];
    }
    private static int frJMR(int n, int[] h, int[] dp) {
        if(dp[n] != -1) return dp[n];

        int l = frJMR(n-1,h,dp) + Math.abs(h[n] - h[n-1]);
        int r = Integer.MAX_VALUE;
        if(n >1)
            r= frJMR(n-2, h, dp) + Math.abs(h[n] - h[n-2]);

        return dp[n] = Math.min(l,r);
    }

    private static int frJT(int n, int[] h) {
        int[] dp = new int[n+1];
        dp[0] = 0;

        for(int i = 1; i<=n; i++) {
            int l = dp[i-1] + Math.abs(h[i] - h[i-1]);
            int r = Integer.MAX_VALUE;
            if(i>1)
                r = dp[i-2] + Math.abs(h[i] - h[i-2]);
            dp[i] = Math.min(l,r);
        }
        return dp[n];
    }

    private static int frJSO(int n, int[] h) {
        int prev = 0, prev2 = 0;

        for(int i = 1; i<=n; i++) {
            int l = prev + Math.abs(h[i] - h[i-1]);
            int r = Integer.MAX_VALUE;
            if(i > 1)
                r = prev2 + Math.abs(h[i]-h[i-2]);
            int curI = Math.min(l,r);
            prev2 = prev;
            prev = curI;
        }
        return prev;
    }



}
