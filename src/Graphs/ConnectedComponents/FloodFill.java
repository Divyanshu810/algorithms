package Graphs.ConnectedComponents;

public class FloodFill {

    public static void main(String[] args) {
        int[][] img = { {1,1,1},
                {1,1,0},
                {1,0,1}
        };
        int[][] ims = {{0,0,0}, {0,0,0}};
        floodFill(ims, 0,0,2);
//        floodFill(img, 1,1, 3);
    }

    private static void floodFill(int[][] img, int sr, int sc, int tc){
        int[][] vis = img;
        int[] delR = {-1, 0, 1, 0};
        int[] delC = {0, 1, 0, -1};
        int ic = vis[sr][sc];
        dfs(img, vis, sr, sc, ic, tc, delR, delC);
        for(int i = 0; i< img.length; i++){
            for(int j = 0; j< img[0].length; j++){
                System.out.print(vis[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void dfs(int[][] img, int[][] vis, int ro, int co, int ic, int tc, int[] delR, int[] delC){
        vis[ro][co] = tc;
        int n = img.length;
        int m = img[0].length;
        for(int z = 0; z <4; z++){
            int mR = ro + delR[z];
            int mC = co + delC[z];
            if(mR <n && mR >=0 && mC < m && mC >=0 && vis[mR][mC] != tc && img[mR][mC] == ic){
                dfs(img, vis, mR, mC, ic, tc, delR, delC);
            }
        }
    }

}
