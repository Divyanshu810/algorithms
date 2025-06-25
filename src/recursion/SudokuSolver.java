package recursion;

public class SudokuSolver {

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
        for(int i = 0; i< 9; i++){
            for(int j = 0; j<9; j++){
                System.out.print(board[i][j] + " ");
            }
            System.out.println();
        }
    }
    private static boolean solve(char[][] board){
        for(int i = 0; i<9; i++){
            for(int j = 0; j<9; j++){
                if( board[i][j] == '.'){
                    for(char k = '1'; k<= '9'; k++){
                        if(isValid(i,j,k, board)){
                            board[i][j] = k;
                            if(solve(board))
                                return true;
                            else
                                board[i][j] = '.';
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isValid(int row, int col, char k, char[][] board){
        for(int z = 0; z<9; z++){
            if(board[z][col] == k)
                return false;
            if(board[row][z] == k)
                return false;
            if(board[3*(row/3) + z/3][3*(col/3) + z%3] == k)
                return false;
        }
        return true;
    }


}
