package Graphs;

import java.util.*;

class Node{
    int i;
    int j;
    int d;

    public Node(int i, int j, int d){
        this.i = i;
        this.j = j;
        this.d = d;
    }
}

public class ZeroOneMatrix {

    //https://www.geeksforgeeks.org/problems/distance-of-nearest-cell-having-1-1587115620/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=distance-of-nearest-cell-having-1


    public static void main(String[] args) {
        int[][] grid = {{0,1,1,0}, {1,1,0,0}, {0,0,1,1}};
        distances(grid);

    }

    private static void distances(int[][] g){
        int n = g.length;
        int m = g[0].length;
        int[][] vis = new int[n][m];
        int[][] dis = new int[n][m];

        Queue<Node> q = new LinkedList<>();


        for(int i =0; i<n; i++){
            for(int j = 0; j<m; j++){
                if(g[i][j] == 1){
                    q.add(new Node(i,j,0));
                    vis[i][j] = 1;
                }
                else
                    vis[i][j] = 0;
            }
        }

        int[] dR = {-1, 0, 1, 0};
        int[] dC = {0, 1, 0, -1};

        while(!q.isEmpty()){
            Node z = q.poll();
            int ro = z.i;
            int co = z.j;
            int di = z.d;
            dis[ro][co]  = di;

            for(int k = 0; k<4; k++){
                int delR = ro + dR[k];
                int delC = co + dC[k];

                if(delR >=0 && delR < n && delC >=0 && delC < m && vis[delR][delC] == 0){
                    vis[delR][delC] = 1;
                    q.add(new Node(delR, delC, di +1));
                }
            }
        }

        for(int i = 0; i< n; i++){
            for( int j = 0; j<m; j++){
                System.out.print(dis[i][j] + " ");
            }
            System.out.println();
        }

//        return dis;
    }
}
