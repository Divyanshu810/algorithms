package Graphs;

public class FloydWarshall {

    public static void main(String[] args) {
        int[][] m = {{0,1,43},{1,0,6},{-1,-1,0}};
        floydWarshall(m);

        for(int i = 0; i< 3; i++) {
            for(int j = 0; j< 3; j++) {
                System.out.print(m[i][j]);
            }
            System.out.println();
        }
    }

    private static void floydWarshall(int[][] m) {
        int n = m.length;
        for(int i = 0; i< n; i++) {
            for(int j = 0; j<n; j++) {
                if(m[i][j] == -1)
                    m[i][j] = (int)1e9;
                if(i == j)
                    m[i][j] = 0;
            }
        }

        for(int k = 0; k<n; k++) {
            for(int i = 0; i< n; i++) {
                for(int j = 0; j<n; j++) {
                    m[i][j] = Math.min(m[i][j], m[i][k] + m[k][j]);
                }
            }
        }

        for(int i = 0; i< n; i++) {
            for(int j = 0; j<n; j++) {
                if(m[i][j] == 1e9)
                    m[i][j] = -1;
            }
        }
    }
}
