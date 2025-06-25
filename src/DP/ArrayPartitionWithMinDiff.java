package DP;

public class ArrayPartitionWithMinDiff {


    public static void main(String[] args) {
        int n = 4;
        int[] a = {1,2,3,4};

        int m = 3;
        int[] b = {8,6,5};
        System.out.println(func(n,a));
        System.out.println(func(m,b));

    }

    private static int func(int n, int[] a) {
        int ts = 0;
        for(int i = 0; i<n; i++) ts += a[i];

        boolean[][] dp = new boolean[n][ts+1];

        for(int i = 0; i<n; i++) {
            dp[i][0] = true;
        }
        if(a[0]<= ts) dp[0][a[0]] =true;

        for(int i = 1; i<n; i++) {
            for(int t = 1; t<=ts; t++) {
                boolean nt = dp[i-1][t];
                boolean ta = false;
                if(t>=a[i]) ta = dp[i-1][t-a[i]];
                dp[i][t] = ta||nt;
            }
        }

        int mini = (int)1e8;

        for(int i = 0; i<=ts/2; i++) {
            if(dp[n-1][i]) {
                mini = Math.min(mini, Math.abs((ts-i)-i));
            }
        }
        return mini;
    }
}
