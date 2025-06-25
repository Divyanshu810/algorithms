package DP;

import java.util.Arrays;

public class ChocolatePickup3dDP {

    public static void main(String[] args) {

        int[][] g = {{2,3,1,2},
                {3,4,2,2},
                {5,6,3,5}};

        int[][] g2 = {{4,5},
                {3,7},
                {4,2}};
        System.out.println(rec(3,4,g));
        System.out.println(rec(3,2,g2));
        System.out.println(mem(3,4,g));
        System.out.println(mem(3,2,g2));

        System.out.println(tab(3,4,g));
        System.out.println(tab(3,2,g2));

        System.out.println(sO(3,4,g));
        System.out.println(sO(3,2,g2));

    }


    private static int rec(int r, int c, int[][] g) {
       return  recF(0,0,c-1,r,c,g);
    }
    private static int recF(int i, int j1, int j2, int r, int c, int[][] g) {
        if(j1<0 || j1 >= c || j2<0 || j2>=c) return (int)-1e8;

        if(i == r-1) {
            if(j1 == j2) return g[i][j1];
            else
                return g[i][j1] + g[i][j2];
        }
        int maxi = (int)-1e8;
        for(int dj1 = -1; dj1<=1; dj1++) {
            for(int dj2 = -1; dj2<=1; dj2++) {
                int val = 0;
                if(j1 == j2) val += g[i][j1];
                else
                    val += g[i][j1] + g[i][j2];
                val += recF(i+1, j1+dj1, j2+dj2, r, c, g);
                maxi = Math.max(maxi, val);
            }
        }

        return maxi;

    }


    private static int mem(int r, int c, int[][] g) {
        int[][][] dp = new int[r][c][c];

        for(int i = 0; i<r; i++) {
            for(int j =0; j<c; j++) {
                for(int[] k : dp[i])
                    Arrays.fill(k,-1);
            }
        }
        return memoF(0,0,c-1,r,c,g,dp);

    }
    private static int memoF(int i, int j1, int j2, int r, int c, int[][] g, int[][][] dp) {
        if(j1<0 || j1 >= c || j2<0 || j2>=c) return (int)-1e8;

        if(i == r-1) {
            if(j1 == j2) return g[i][j1];
            else
                return g[i][j1] + g[i][j2];
        }
        if(dp[i][j1][j2] != -1) return dp[i][j1][j2];
        int maxi = (int)-1e8;
        for(int dj1 = -1; dj1<=1; dj1++) {
            for(int dj2 = -1; dj2<=1; dj2++) {
                int val = 0;
                if(j1 == j2) val += g[i][j1];
                else
                    val += g[i][j1] + g[i][j2];
                val += memoF(i+1, j1+dj1, j2+dj2, r, c, g,dp);
                maxi = Math.max(maxi, val);
            }
        }

        return dp[i][j1][j2] =  maxi;
    }


    private static int tab(int r, int c, int[][] g) {
        int[][][] dp = new int[r][c][c];

        for(int j1 = 0; j1<c; j1++) {
            for(int j2 = 0; j2<c; j2++) {
                if(j1 == j2) dp[r-1][j1][j2] = g[r-1][j1];
                else
                    dp[r-1][j1][j2] = g[r-1][j1] + g[r-1][j2];
            }
        }

        for(int i = r-2; i>=0; i--) {
            for(int j1 = 0; j1<c; j1++ ) {
                for(int j2 = 0; j2<c; j2++) {
                    int maxi = (int)-1e8;

                    for(int dj1 = -1; dj1<=1; dj1++) {
                        for(int dj2 = -1; dj2<=1; dj2++) {
                            int val = 0;
                            if(j1 == j2) val += g[i][j1];
                            else val += g[i][j1] + g[i][j2];

                            if(j1 + dj1 >=0 && j1 + dj1<c && j2+dj2>=0 && j2 + dj2<c) {
                                val += dp[i+1][j1+dj1][j2+dj2];
                            }
                            maxi = Math.max(maxi,val);
                        }
                    }
                    dp[i][j1][j2] = maxi;
                }
            }
        }
        return dp[0][0][c-1];
    }


    private static int sO(int r, int c, int[][] g) {
        int[][] f = new int[c][c];
        int[][] cu = new int[c][c];

        for(int j1 = 0; j1<c; j1++) {
            for(int j2 = 0; j2<c; j2++) {
                if(j1 == j2) f[j1][j2] = g[r-1][j1];
                else f[j1][j2] = g[r-1][j1] + g[r-1][j2];
            }
        }

        for(int i = r-2; i>=0; i--) {
            for(int j1 = 0; j1<c; j1++) {
                for(int j2 = 0; j2<c; j2++) {
                    int maxi = (int)-1e8;
                    for(int dj1 = -1; dj1<=1; dj1++) {
                        for(int dj2 = -1; dj2<=1; dj2++) {
                            int val = 0;
                            if(j1==j2) val+= g[i][j1];
                            else val+= g[i][j1] + g[i][j2];
                            if(j1+dj1>=0 && j1+dj1<c && j2 + dj2>=0 && j2+dj2<c)
                                val += f[j1+dj1][j2+dj2];
                            maxi = Math.max(maxi,val);
                         }
                    }
                    cu[j1][j2] = maxi;
                }
            }
            for(int a = 0; a<c; a++) {
                for(int b = 0; b<c; b++) {
                    f[a][b] = cu[a][b];
                }
            }
//            f = cu;
        }
        return f[0][c-1];
    }
}
