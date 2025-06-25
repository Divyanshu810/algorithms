package recursion.Retrial;

public class Sudoku {

    public static void main(String[] args) {
        char[][] board = {
                {'9', '5', '7', '.', '1', '3', '.', '8', '4'},
                {'4', '8', '3', '.', '5', '7', '1', '.', '6'},
                {'.', '1', '2', '.', '4', '9', '5', '3', '7'},
                {'1', '7', '.', '3', '.', '4', '9', '.', '2'},
                {'5', '.', '4', '9', '7', '.', '3', '6', '.'},
                {'3', '.', '9', '5', '.', '8', '7', '.', '1'},
                {'8', '4', '5', '7', '9', '.', '6', '1', '3'},
                {'.', '9', '1', '.', '3', '6', '.', '7', '5'},
                {'7', '.', '6', '1', '8', '5', '4', '.', '9'}
        };
        solve(board);
        for(int i = 0; i< 9; i++) {
            for(int j = 0; j< 9;j ++) {
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }

    private static void solve(char[][] b) {
        solver(b);
    }
    private static boolean solver(char[][] b) {
        for(int i =0; i< b.length; i++) {
            for(int j = 0; j<b[0].length; j++) {
                if(b[i][j] == '.') {
                    for(char c = '1'; c<= '9'; c++) {
                        if(isValid(i, j, c, b)) {
                            b[i][j] = c;
                            if(solver(b))
                                return true;
                            else
                                b[i][j]  = '.';
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValid(int i, int j, char c, char[][] b) {
        for(int k = 0; k<9; k++) {
            if(b[k][j] == c)
                return false;
            if(b[i][k] == c)
                return false;
            if(b[3*(i/3) + k/3][3*(j/3) + k%3] == c)
                return false;
        }
        return true;
    }


}
