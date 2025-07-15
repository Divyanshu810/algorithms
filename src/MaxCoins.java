import java.util.Arrays;

public class MaxCoins {


    public static void main(String[] args) {

        int[][] g = {{0, 3, 1},{2, 0, 0},{1, 2, 0}};
        System.out.println(maxCoins(g));

    }

    private static int maxCoins(int[][] g) {
        int n = g.length, m = g[0].length;
        int[][] dp = new int[n][m];
        for(int[] i : dp) Arrays.fill(i, -1);
        return func(n-1, m-1, g, dp);
    }

    private static int func(int i, int j, int[][] g, int[][] dp) {
        if(i== 0 && j == 0) return g[i][j];
        if(i<0 && j < 0) return 0;
        if(dp[i][j] != -1) return dp[i][j];

        int up = g[i][j] + func(i-1, j, g, dp);
        int left = g[i][j] + func(i, j-1, g, dp);
        return dp[i][j] = Math.max(up, left);
    }
}
