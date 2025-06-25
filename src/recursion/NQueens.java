package recursion;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class NQueens {

    public static void main(String[] args) {
        int n = 7;
        Nqueens(n);

    }

    private static void Nqueens(int n){
        List<List<String>> res = new ArrayList<>();
        char[][] board = new char[n][n];
        for(int i = 0; i<n; i++){
            for(int j = 0; j<n; j++){
                board[i][j] = '.';
            }
        }
        int[] leftRow = new int[n];
        int[] lowerD = new int[2*n-1];
        int[] upperD = new int[2*n-1];
        combinations(0, res, board, leftRow, lowerD, upperD);
        System.out.println(res.size());
        for(List<String > l : res){
            for(String k : l){
                System.out.println(k);
            }
            System.out.println();
        }

    }

    private static void combinations(int c, List<List<String>> ans, char[][] board, int[] leftRow, int[] lowerD, int[] upperD){
        if(c == board.length){
            ans.add(construct(board));
            return;
        }

        for(int r = 0; r< board.length; r++){
            if(leftRow[r] == 0 && lowerD[c + r] == 0 && upperD[board.length -1 + c -r] == 0){
                leftRow[r] = 1;
                lowerD[c + r] = 1;
                upperD[board.length -1 + c -r] = 1;
                board[r][c] = 'Q';
                combinations(c+1, ans, board, leftRow,lowerD, upperD);
                leftRow[r] = 0;
                lowerD[c + r] = 0;
                upperD[board.length -1 + c -r] = 0;
                board[r][c] = '.';
            }
        }

    }

    private static List<String> construct(char[][] board){
        List<String> b = new LinkedList<>();
        for(int k = 0; k<board.length; k++ ){
            b.add(new String(board[k]));
        }
        return b;
    }
}
