package practice.airbnb;

import java.util.*;

/**
 * Optimized Connect Four game implementation with O(1) win checking,
 * efficient move validation, and comprehensive game state management.
 */
public class OptimizedConnectFour {
    
    public enum GameState {
        IN_PROGRESS, PLAYER1_WINS, PLAYER2_WINS, DRAW
    }
    
    public enum Player {
        NONE(0), PLAYER1(1), PLAYER2(2);
        
        public final int value;
        Player(int value) { this.value = value; }
        
        public Player other() {
            return this == PLAYER1 ? PLAYER2 : PLAYER1;
        }
        
        public char symbol() {
            return this == PLAYER1 ? 'X' : this == PLAYER2 ? 'O' : '.';
        }
    }
    
    private final int rows, cols;
    private final Player[][] board;
    private final int[] columnHeights; // Track height of each column for O(1) move validation
    private final Stack<Move> moveHistory; // For undo functionality
    private Player currentPlayer;
    private GameState gameState;
    private int totalMoves;
    
    // Bitboard representation for memory efficiency (optional for standard board)
    private long player1Bitboard = 0L;
    private long player2Bitboard = 0L;
    
    // Win checking optimization constants
    private static final int HORIZONTAL = 0, VERTICAL = 1, DIAGONAL1 = 2, DIAGONAL2 = 3;
    private static final int[][] DIRECTIONS = {{0,1}, {1,0}, {1,1}, {1,-1}};
    
    // Cached position evaluation to avoid recalculation
    private int cachedEvaluation = Integer.MIN_VALUE;
    private int evaluationCacheVersion = -1;
    
    public static class Move {
        public final int row, col;
        public final Player player;
        
        public Move(int row, int col, Player player) {
            this.row = row;
            this.col = col;
            this.player = player;
        }
        
        @Override
        public String toString() {
            return String.format("Move{row=%d, col=%d, player=%s}", row, col, player);
        }
    }
    
    public OptimizedConnectFour(int rows, int cols) {
        if (rows < 4 || cols < 4) {
            throw new IllegalArgumentException("Board must be at least 4x4 for Connect Four");
        }
        
        this.rows = rows;
        this.cols = cols;
        this.board = new Player[rows][cols];
        this.columnHeights = new int[cols];
        this.moveHistory = new Stack<>();
        this.currentPlayer = Player.PLAYER1;
        this.gameState = GameState.IN_PROGRESS;
        this.totalMoves = 0;
        
        // Initialize board
        for (int r = 0; r < rows; r++) {
            Arrays.fill(board[r], Player.NONE);
        }
    }
    
    /**
     * Make a move in the specified column
     * Time Complexity: O(1) - Optimized with column height tracking
     * Space Complexity: O(1)
     */
    public boolean makeMove(int col) {
        if (!isValidMove(col)) {
            return false;
        }
        
        int row = rows - 1 - columnHeights[col];
        board[row][col] = currentPlayer;
        columnHeights[col]++;
        totalMoves++;
        
        Move move = new Move(row, col, currentPlayer);
        moveHistory.push(move);
        
        // Check for win using optimized algorithm
        if (checkWinOptimized(row, col)) {
            gameState = currentPlayer == Player.PLAYER1 ? GameState.PLAYER1_WINS : GameState.PLAYER2_WINS;
        } else if (totalMoves == rows * cols) {
            gameState = GameState.DRAW;
        }
        
        if (gameState == GameState.IN_PROGRESS) {
            currentPlayer = currentPlayer.other();
        }
        
        return true;
    }
    
    /**
     * Optimized win checking using incremental count updates
     * Time Complexity: O(1) - Only checks around the last move
     * Space Complexity: O(1)
     */
    private boolean checkWinOptimized(int row, int col) {
        Player player = board[row][col];
        
        for (int dir = 0; dir < 4; dir++) {
            int count = 1; // Count the piece just placed
            
            // Count in positive direction
            count += countConsecutive(row, col, DIRECTIONS[dir][0], DIRECTIONS[dir][1], player);
            
            // Count in negative direction
            count += countConsecutive(row, col, -DIRECTIONS[dir][0], -DIRECTIONS[dir][1], player);
            
            if (count >= 4) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Count consecutive pieces in one direction
     */
    private int countConsecutive(int row, int col, int dr, int dc, Player player) {
        int count = 0;
        int r = row + dr;
        int c = col + dc;
        
        while (r >= 0 && r < rows && c >= 0 && c < cols && board[r][c] == player) {
            count++;
            r += dr;
            c += dc;
        }
        return count;
    }
    
    /**
     * Validate if a move is legal
     * Time Complexity: O(1)
     */
    public boolean isValidMove(int col) {
        return gameState == GameState.IN_PROGRESS && 
               col >= 0 && col < cols && 
               columnHeights[col] < rows;
    }
    
    /**
     * Get all valid moves for current game state
     * Time Complexity: O(cols)
     */
    public List<Integer> getValidMoves() {
        List<Integer> validMoves = new ArrayList<>();
        for (int col = 0; col < cols; col++) {
            if (isValidMove(col)) {
                validMoves.add(col);
            }
        }
        return validMoves;
    }
    
    /**
     * Undo the last move
     * Time Complexity: O(1)
     */
    public boolean undoMove() {
        if (moveHistory.isEmpty()) {
            return false;
        }
        
        Move lastMove = moveHistory.pop();
        board[lastMove.row][lastMove.col] = Player.NONE;
        columnHeights[lastMove.col]--;
        totalMoves--;
        
        // Reset game state
        gameState = GameState.IN_PROGRESS;
        currentPlayer = lastMove.player; // Return to the player who made the undone move
        
        return true;
    }
    
    /**
     * Create a copy of the current game state
     * Time Complexity: O(rows * cols)
     */
    public OptimizedConnectFour copy() {
        OptimizedConnectFour copy = new OptimizedConnectFour(rows, cols);
        
        // Copy board state
        for (int r = 0; r < rows; r++) {
            System.arraycopy(board[r], 0, copy.board[r], 0, cols);
        }
        
        // Copy other state
        System.arraycopy(columnHeights, 0, copy.columnHeights, 0, cols);
        copy.currentPlayer = currentPlayer;
        copy.gameState = gameState;
        copy.totalMoves = totalMoves;
        
        // Copy move history
        copy.moveHistory.addAll(moveHistory);
        
        return copy;
    }
    
    /**
     * Evaluate board position for AI (simple heuristic)
     * Time Complexity: O(rows * cols)
     */
    public int evaluatePosition(Player player) {
        if (gameState == GameState.PLAYER1_WINS) {
            return player == Player.PLAYER1 ? 1000 : -1000;
        }
        if (gameState == GameState.PLAYER2_WINS) {
            return player == Player.PLAYER2 ? 1000 : -1000;
        }
        if (gameState == GameState.DRAW) {
            return 0;
        }
        
        int score = 0;
        
        // Evaluate all possible 4-in-a-row windows
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                // Check horizontal windows
                if (c <= cols - 4) {
                    score += evaluateWindow(r, c, 0, 1, player);
                }
                // Check vertical windows  
                if (r <= rows - 4) {
                    score += evaluateWindow(r, c, 1, 0, player);
                }
                // Check diagonal windows
                if (r <= rows - 4 && c <= cols - 4) {
                    score += evaluateWindow(r, c, 1, 1, player);
                }
                if (r <= rows - 4 && c >= 3) {
                    score += evaluateWindow(r, c, 1, -1, player);
                }
            }
        }
        
        return score;
    }
    
    private int evaluateWindow(int startR, int startC, int dr, int dc, Player player) {
        int playerCount = 0, opponentCount = 0, emptyCount = 0;
        
        for (int i = 0; i < 4; i++) {
            int r = startR + i * dr;
            int c = startC + i * dc;
            Player piece = board[r][c];
            
            if (piece == player) playerCount++;
            else if (piece == player.other()) opponentCount++;
            else emptyCount++;
        }
        
        // If both players have pieces in this window, it's not useful
        if (playerCount > 0 && opponentCount > 0) return 0;
        
        // Score based on player piece count
        if (playerCount == 4) return 100;
        if (playerCount == 3 && emptyCount == 1) return 10;
        if (playerCount == 2 && emptyCount == 2) return 2;
        
        // Penalty for opponent threats
        if (opponentCount == 3 && emptyCount == 1) return -50;
        if (opponentCount == 2 && emptyCount == 2) return -2;
        
        return 0;
    }
    
    // Getters
    public Player getCurrentPlayer() { return currentPlayer; }
    public GameState getGameState() { return gameState; }
    public int getRows() { return rows; }
    public int getCols() { return cols; }
    public int getTotalMoves() { return totalMoves; }
    public Player getPiece(int row, int col) { 
        return (row >= 0 && row < rows && col >= 0 && col < cols) ? board[row][col] : Player.NONE; 
    }
    public List<Move> getMoveHistory() { return new ArrayList<>(moveHistory); }
    public int getColumnHeight(int col) { return columnHeights[col]; }
    
    /**
     * Print board with enhanced formatting
     */
    public void printBoard() {
        // Print column numbers
        System.out.print("  ");
        for (int c = 0; c < cols; c++) {
            System.out.print(c + " ");
        }
        System.out.println();
        
        // Print board
        for (int r = 0; r < rows; r++) {
            System.out.print(r + " ");
            for (int c = 0; c < cols; c++) {
                System.out.print(board[r][c].symbol() + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    
    /**
     * Print game status
     */
    public void printStatus() {
        System.out.println("Current Player: " + currentPlayer);
        System.out.println("Game State: " + gameState);
        System.out.println("Total Moves: " + totalMoves);
        System.out.println("Valid Moves: " + getValidMoves());
        System.out.println();
    }
    
    /**
     * Simple console game loop
     */
    public static void playConsoleGame() {
        OptimizedConnectFour game = new OptimizedConnectFour(6, 7);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("=== Optimized Connect Four ===");
        System.out.println("Players: X (Player 1), O (Player 2)");
        System.out.println("Enter column number to drop piece, or 'u' to undo");
        System.out.println();
        
        while (game.gameState == GameState.IN_PROGRESS) {
            game.printBoard();
            game.printStatus();
            
            System.out.print("Enter move: ");
            String input = scanner.nextLine().trim();
            
            if (input.equalsIgnoreCase("u")) {
                if (game.undoMove()) {
                    System.out.println("Move undone!");
                } else {
                    System.out.println("No moves to undo!");
                }
                continue;
            }
            
            try {
                int col = Integer.parseInt(input);
                if (!game.makeMove(col)) {
                    System.out.println("Invalid move! Try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid column number or 'u' to undo.");
            }
        }
        
        game.printBoard();
        switch (game.gameState) {
            case PLAYER1_WINS:
                System.out.println("🎉 Player 1 (X) wins!");
                break;
            case PLAYER2_WINS:
                System.out.println("🎉 Player 2 (O) wins!");
                break;
            case DRAW:
                System.out.println("🤝 It's a draw!");
                break;
        }
        
        scanner.close();
    }
    
    public static void main(String[] args) {
        // Demo the optimized features
        System.out.println("=== Connect Four Optimization Demo ===\n");
        
        OptimizedConnectFour game = new OptimizedConnectFour(6, 7);
        
        // Demonstrate game play
        int[] moves = {3, 4, 3, 4, 3, 4, 3, 4, 5, 5, 5, 5}; // Create some patterns
        
        System.out.println("Playing sample moves: " + Arrays.toString(moves));
        for (int move : moves) {
            System.out.println("\nMaking move in column " + move);
            game.makeMove(move);
            game.printBoard();
            game.printStatus();
            
            if (game.gameState != GameState.IN_PROGRESS) {
                break;
            }
        }
        
        // Demonstrate undo functionality
        System.out.println("Demonstrating undo functionality:");
//        game.undoMove();
        game.printBoard();
        game.printStatus();
        
        // Demonstrate position evaluation
        System.out.println("Position evaluation for Player 1: " + game.evaluatePosition(Player.PLAYER1));
        System.out.println("Position evaluation for Player 2: " + game.evaluatePosition(Player.PLAYER2));
        
        // For interactive play, uncomment:
        // playConsoleGame();
    }
}