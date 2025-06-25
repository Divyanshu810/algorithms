package Graphs.DSU;

import java.util.ArrayList;
import java.util.List;

public class NumberOfIslandsII {

    public static void main(String[] args) {
        List<Integer> ans = new ArrayList<>();
        int[][] op = {{1,1},{0,1},{3,3},{3,4}};
        ans = sol(4,5,op);

        for(int i : ans) {
            System.out.println(i);
        }

        int[][] op1 = {{0,0},{1,1},{2,2},{3,3}};
        ans = sol(4,5,op1);

        for(int i : ans) {
            System.out.println(i);
        }

    }

    private static List<Integer> sol(int r, int c, int[][] op) {
        DisjointSet ds = new DisjointSet(r*c);
        int[][] vis = new int[r][c];
        List<Integer> ans = new ArrayList<>();
        int cnt = 0;
        for(int i = 0; i< op.length; i++) {
            int ro = op[i][0], co = op[i][1];
            if(vis[ro][co] == 1) {
                ans.add(cnt);
            } else  {
                cnt++;
                vis[ro][co] = 1;

                int[] dr = {-1, 0, 1, 0};
                int[] dc = {0, 1, 0, -1};
                for(int k = 0; k<4; k++) {
                    int mr = ro + dr[k], mc = co + dc[k];
                    if(mr >=0 && mr < r && mc>=0 && mc <c) {
                        if(vis[mr][mc] == 1) {
                            int node = ro*c + co, adNode = mr*c + mc;
                            if(ds.findUPar(node) != ds.findUPar(adNode)) {
                                cnt--;
                                ds.unionBySize(node,adNode);
                            }
                        }
                    }
                }
                ans.add(cnt);
            }
        }
        return ans;
    }
}
