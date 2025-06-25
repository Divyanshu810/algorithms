package recursion;

import java.util.*;

public class MColouring {

    public static void main(String[] args) {
        int N = 4, M = 3;

        List<Integer>[] G = new ArrayList[N];

        for(int i = 0; i<N; i++){
            G[i] = new ArrayList<>();
        }

        G[0].add(1);
        G[1].add(0);
        G[1].add(2);
        G[2].add(1);
        G[2].add(3);
        G[3].add(2);
        G[3].add(0);
        G[0].add(3);
        G[0].add(2);
        G[2].add(0);
        int[] col = new int[N];
        System.out.println(colour(G, M, col, 0, N));
    }

    private static boolean colour(List<Integer>[] G, int M, int[] col, int n, int N){
        if(n == N)
            return true;
        for(int i = 1; i<= M; i++){
            if(isSafe(col, i, n, G)){
                col[n] = i;
                if(colour(G, M, col, n+1, N))
                    return true;
                col[n] = 0;
            }

        }
        return false;
    }
    private static boolean isSafe(int[] col, int c, int n, List<Integer>[] G){
        for(int g : G[n]){
            if(col[g] == c)return false;
        }
        return true;
    }


}
