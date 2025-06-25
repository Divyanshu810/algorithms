package Graphs;

import java.util.LinkedList;
import java.util.Queue;

class Pair {
    int ro;
    int co;
    int ti;
    public Pair(int ro, int co, int ti){
        this.ro = ro;
        this.co = co;
        this.ti = ti;
    }
}

public class RottenOranges {
    // https://www.geeksforgeeks.org/problems/rotten-oranges2536/1?utm_source=youtube&utm_medium=collab_striver_ytdescription&utm_campaign=rotten_oranges

    public static void main(String[] args) {
        int[][] oranges = {{0,1,2}, {0,1,2}, {2,1,1}};
        int[][] orang = {{2,2,0,1}};
        int[][] orange1 = {{2,1,1}, {1,1,0}, {0,1,1}};
        int[][] orange2 = {{2,1,1}, {0,1,1}, {1,0,1}};
        int[][] or = {{1,2}};
        System.out.println(rottingOranges(oranges));

        System.out.println(rottingOranges(orang));
        System.out.println(rottingOranges(orange1));
        System.out.println(rottingOranges(or));
    }
    private static int rottingOranges(int[][] o){
        Queue<Pair> q = new LinkedList<>();
        int n = o.length;
        int m = o[0].length;
        int[][] vis = new int[n][m];
        int cnt = 0;

        for(int i =0; i<n; i++){
            for(int j = 0; j< m; j++){
                if(o[i][j] == 2){
                    vis[i][j] = 2;
                    q.add(new Pair(i,j,0));
                }
                else
                    vis[i][j] = 0;
                if(o[i][j] == 1)
                    cnt++;
            }
        }

        int tm = 0, cntF = 0;
        int[] delR = {-1, 0, 1, 0};
        int[] delC = {0, 1, 0, -1};

        while(!q.isEmpty()){
            Pair p = q.poll();
            int r = p.ro, c = p.co, t = p.ti;
            tm = Math.max(tm, t);
            for(int i = 0; i<4; i++){
                int mr = r + delR[i];
                int mc = c + delC[i];
                if(mr < n && mr >=0 && mc <m && mc >=0 && o[mr][mc] == 1 && vis[mr][mc] == 0){
                    q.add(new Pair(mr, mc, t+1));
                    vis[mr][mc] = 1;
                    cntF++;
                }
            }
        }
        if(cnt != cntF)return -1;
        return tm;
    }


}
