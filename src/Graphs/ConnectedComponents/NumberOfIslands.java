package Graphs.ConnectedComponents;

import java.util.LinkedList;
import java.util.Queue;

class Pair {
    int first;
    int second;
    public Pair(int first, int second){
        this.first = first;
        this.second = second;
    }
}

public class NumberOfIslands {
    public static void main(String[] args) {
        char[][] M = {
                { '1', '1', '0', '0', '0' },
                { '0', '1', '0', '0', '1' },
                { '1', '0', '0', '1', '1' },
                { '0', '0', '0', '0', '0' },
                { '1', '0', '1', '1', '0' }
        };
        char[][] K = {
                { '1', '1', '0', '0', '0' },
                { '1', '1', '0', '0', '0' },
                { '0', '0', '1', '0', '0' },
                { '0', '0', '0', '1', '1' }
        };

        char[][] N = {{'1','1','0','0','0'},{'1','1','0','0','0'},{'0','0','1','0','0'},{'0','0','0','1','1'}};

        connectedComp(M);
    }
    private static void connectedComp(char[][] is){
        int n = is.length;
        int m = is[0].length;
        int[][] vis = new int[n][m];
        int cnt = 0;
        for(int i = 0; i< n ; i++){
            for(int j = 0; j< m; j++){
                if(vis[i][j] == 0 && is[i][j] == '1'){
                    cnt++;
                    bfs(is, vis, i,j);

                }
            }
        }
        System.out.println(cnt);
    }

    private static void bfs(char[][] is, int[][] vis, int ro, int co){

        int n = is.length;
        int m = is[0].length;
        Queue<Pair> q = new LinkedList<Pair>();
        q.add(new Pair(ro,co));
        vis[ro][co] = 1;

        while(!q.isEmpty()){
            Pair p = q.poll();
            int row = p.first;
            int col = p.second;
//            int row = q.peek().first;
//            int col = q.peek().second;
//            q.remove();

            for(int delR = -1; delR<=1; delR++){
                for(int delC = -1; delC <=1; delC++){
                    int rowM = row + delR;
                    int colM = col + delC;
                    if( rowM >=0 && rowM<n && colM >=0 && colM < m && is[rowM][colM] == '1' && vis[rowM][colM] == 0 ){
                        vis[rowM][colM] = 1;
                        q.add(new Pair(rowM,colM));
                    }
                }
            }
        }

    }

}
