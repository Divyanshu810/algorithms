package DP;

public class NinjasTraining {


    public static void main(String[] args) {
        int[][] p1 = {{1,2,5},
                     {3,1,1},
                     {3,3,3}};

        int[][] p2 = {{10,40,70},{20,50,80},{30,60,90}};
        int[][] p3 = {{18,11,19},{4,13,7},{1,8,13}};

        System.out.println(recu(3,p1));
        System.out.println(recu(3,p2));
        System.out.println(recu(3,p3));
        System.out.println(memo(3,p1));
        System.out.println(memo(3,p2));
        System.out.println(memo(3,p3));

        System.out.println(tabu(3,p1));
        System.out.println(tabu(3,p2));
        System.out.println(tabu(3,p3));

        System.out.println(sO(3,p1));
        System.out.println(sO(3,p2));
        System.out.println(sO(3,p3));


    }

    private static int recu(int n, int[][] points) {
        return recF(n-1, 3, points);
    }
    private static int recF(int i, int last, int[][] point) {
        if(i == 0) {
            int maxi =0;
            for(int j =0; j<3; j++) {
                if(j != last) {
                    int p = point[i][j];
                    maxi = Math.max(maxi,p);
                }
            }
            return maxi;
        }

        int maxi = 0;
        for(int j = 0; j<3; j++) {
            if(j != last) {
                int p = point[i][j] + recF(i-1,j,point);
                maxi = Math.max(maxi,p);
            }
        }
        return maxi;
    }

    private static int memo(int n, int[][] p) {
        int[][] dp = new int[n][4];
        return memoF(n-1,3, p, dp);
    }
    private static int memoF(int i, int last, int[][] p, int[][] dp) {
        if(i == 0) {
            int maxi = 0;
            for(int j = 0; j<3; j++) {
                if(j != last) {
                    int pt = p[i][j];
                    maxi = Math.max(maxi, pt);
                }
            }
            return dp[i][last] = maxi;
        }

        int maxi = 0;
        for(int j = 0; j<3; j++) {
            if(j != last) {
                int pt = p[i][j] + memoF(i-1, j, p, dp);
                maxi = Math.max(maxi, pt);
            }
        }
        return dp[i][last] = maxi;
    }

    private static int tabu(int n, int[][] pts) {
        int[][] dp = new int[n][4];
        dp[0][0] = Math.max(pts[0][1], pts[0][2]);
        dp[0][1] = Math.max(pts[0][0], pts[0][2]);
        dp[0][2] = Math.max(pts[0][1], pts[0][0]);
        dp[0][3] = Math.max(pts[0][0],Math.max(pts[0][1], pts[0][2]));

        for(int i = 1; i<n; i++) {
            for(int last = 0; last<4; last++) {
                int maxi = 0;

                for(int j = 0; j<3; j++) {
                    if(j != last) {
                        int pt = pts[i][j] + dp[i-1][j];
                        maxi = Math.max(maxi, pt);
                    }
                }
                dp[i][last] = maxi;
            }
        }
        return dp[n-1][3];

    }

    private static int sO(int n, int[][] pts) {
        int[] dp = new int[4];
        dp[0] = Math.max(pts[0][1], pts[0][2]);
        dp[1] = Math.max(pts[0][0], pts[0][2]);
        dp[2] = Math.max(pts[0][1], pts[0][0]);
        dp[3] = Math.max(pts[0][0],Math.max(pts[0][1], pts[0][2]));

        for(int i = 1; i<n; i++) {
            int[] tmp= new int[4];
            for(int last = 0; last<4; last ++) {
                int maxi = 0;
                for(int j = 0; j<3; j++) {
                    if(j != last) {
                        int pt = pts[i][j] + dp[j];
                        maxi = Math.max(pt, maxi);
                    }
                }
                tmp[last] = maxi;
            }
            dp = tmp;
        }
        return dp[3];
    }


}
