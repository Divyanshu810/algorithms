package recursion.Retrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NQueens {

    public static void main(String[] args) {
        queens(4);

    }

    private static void queens(int n) {
        int[] leftR = new int[n];
        int[] uD = new int[2*n-1];
        int[] lD = new int[2*n-1];

        char[][] board = new char[n][n];
        for(int i = 0; i<n; i++) {
            for(int j = 0; j<n; j++) {
                board[i][j] = '.';
            }
        }
        List<List<String>> sol = new ArrayList<>();
        combinations(0,leftR, uD, lD, board, sol);
        for(List<String> l : sol) {
            for(String i : l) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static void combinations(int c, int[] lR, int[] uD, int[] lD, char[][] b, List<List<String>> s) {
        if(c == b.length) {
            List<String > l = new ArrayList<>();
            for(int i =0; i< b.length; i++) {
                char[] ar = new char[b.length];
                for(int j = 0; j<b.length; j++) {
                    ar[j]  = b[i][j];
                }
                l.add(Arrays.toString(ar));
            }
            s.add(new ArrayList<>(l));
            return;
        }

        for(int r = 0; r< b.length; r++) {
            if(lR[r] == 0 && uD[b.length -1 + c-r] == 0 && lD[r+c] == 0) {
                lR[r] = 1;
                uD[b.length -1 + c-r] = 1;
                lD[r + c] = 1;
                b[r][c] = 'Q';
                combinations(c+1, lR, uD, lD, b, s);
                lR[r] = 0;
                uD[b.length -1 + c-r] = 0;
                lD[r + c] = 0;
                b[r][c] = '.';
            }
        }
    }
}
