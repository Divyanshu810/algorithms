package practice.atlassian.karat;

import java.util.ArrayList;
import java.util.List;

public class WordSearch {
    public List<int[]> exist(char[][] board, String word) {
        int[][] visited = new int[board.length][board[0].length];
        List<int[]> result=new ArrayList<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (word.charAt(0) == board[i][j]) {
                    visited[i][j] = 1;
                    int[] temp=new int[2];
                    temp[0]=i;temp[1]=j;
                    result.add(temp);
                    if (recursion(1, i, j, board, visited, word, result)) {
                        return result;
                    }
                    result.remove(result.size()-1);
                    visited[i][j] = 0;
                }
            }
        }
        return null;
    }

    boolean recursion(int ind, int row, int col, char[][] board, int[][] visited, String word,List<int[]> result) {
        if (ind == word.length()) {
            return true;
        }
        int[] drow = {1, 0, -1, 0};
        int[] dcol = {0, 1, 0, -1};
        for (int i = 0; i < 4; i++) {
            if (row + drow[i] >= 0 && row + drow[i] < board.length && col + dcol[i] >= 0 && col + dcol[i] < board[0].length && visited[row + drow[i]][col + dcol[i]] == 0 && word.charAt(ind) == board[row + drow[i]][col + dcol[i]]) {
                visited[row + drow[i]][col + dcol[i]] = 1;
                int[] temp=new int[2];
                temp[0]=row + drow[i];temp[1]=col + dcol[i];
                result.add(temp);
                if (recursion(ind + 1, row + drow[i], col + dcol[i], board, visited, word, result)) {
                    return true;
                }
                result.remove(result.size()-1);
                visited[row + drow[i]][col + dcol[i]] = 0;
            }
        }
        return false;
    }

}
