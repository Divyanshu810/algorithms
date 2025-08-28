package practice.atlassian.code_design.q3_game_of_snakes;
import java.util.*;

enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);
    
    private final int deltaX;
    private final int deltaY;
    
    Direction(int deltaX, int deltaY) {
        this.deltaX = deltaX;
        this.deltaY = deltaY;
    }
    
    public int getDeltaX() { return deltaX; }
    public int getDeltaY() { return deltaY; }
    
    public Direction opposite() {
        switch (this) {
            case UP: return DOWN;
            case DOWN: return UP;
            case LEFT: return RIGHT;
            case RIGHT: return LEFT;
            default: return this;
        }
    }
}

class Position {
    private int x;
    private int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    public int getX() { return x; }
    public int getY() { return y; }
    
    public Position move(Direction direction) {
        return new Position(x + direction.getDeltaX(), y + direction.getDeltaY());
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Position position = (Position) obj;
        return x == position.x && y == position.y;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
    
    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}

interface SnakeGame {
    void moveSnake(Direction direction);
    boolean isGameOver();
}

public class Solution {
    
    // Approach 1: Time-based Growth Snake Game
    public static class TimeBasedSnakeGame implements SnakeGame {
        private Deque<Position> snake;
        private Set<Position> occupied;
        private Direction currentDirection;
        private int boardWidth;
        private int boardHeight;
        private int moves;
        private boolean gameOver;
        private int initialSize;
        private int growthInterval;
        
        public TimeBasedSnakeGame(int boardWidth, int boardHeight) {
            this(boardWidth, boardHeight, 3, 5);
        }
        
        public TimeBasedSnakeGame(int boardWidth, int boardHeight, int initialSize, int growthInterval) {
            this.boardWidth = boardWidth;
            this.boardHeight = boardHeight;
            this.initialSize = initialSize;
            this.growthInterval = growthInterval;
            this.snake = new ArrayDeque<>();
            this.occupied = new HashSet<>();
            this.moves = 0;
            this.gameOver = false;
            this.currentDirection = Direction.RIGHT;
            
            initializeSnake();
        }
        
        private void initializeSnake() {
            // Start snake in the middle of the board, growing to the right
            int centerX = boardWidth / 2;
            int centerY = boardHeight / 2;
            
            for (int i = 0; i < initialSize; i++) {
                Position pos = new Position(centerX - i, centerY);
                snake.addLast(pos);
                occupied.add(pos);
            }
        }
        
        @Override
        public void moveSnake(Direction direction) {
            if (gameOver) {
                return;
            }
            
            // Prevent immediate reverse direction (snake can't move backwards into itself)
            if (direction == currentDirection.opposite() && snake.size() > 1) {
                direction = currentDirection;
            }
            
            currentDirection = direction;
            Position head = snake.peekFirst();
            Position newHead = head.move(direction);
            
            // Wrap around the board if needed
            newHead = wrapAroundIfNeeded(newHead);
            
            // Check self collision
            if (occupied.contains(newHead)) {
                gameOver = true;
                return;
            }
            
            // Add new head
            snake.addFirst(newHead);
            occupied.add(newHead);
            
            // Check if snake should grow
            moves++;
            boolean shouldGrow = (moves % growthInterval == 0);
            
            if (!shouldGrow) {
                // Remove tail if not growing
                Position tail = snake.removeLast();
                occupied.remove(tail);
            }
        }
        
        @Override
        public boolean isGameOver() {
            return gameOver;
        }
        
        private boolean isOutOfBounds(Position pos) {
            return pos.getX() < 0 || pos.getX() >= boardWidth || 
                   pos.getY() < 0 || pos.getY() >= boardHeight;
        }
        
        private Position wrapAroundIfNeeded(Position pos) {
            int x = pos.getX();
            int y = pos.getY();
            
            // Wrap around X axis
            if (x < 0) {
                x = boardWidth - 1;
            } else if (x >= boardWidth) {
                x = 0;
            }
            
            // Wrap around Y axis
            if (y < 0) {
                y = boardHeight - 1;
            } else if (y >= boardHeight) {
                y = 0;
            }
            
            return new Position(x, y);
        }
        
        public List<Position> getSnakeBody() {
            return new ArrayList<>(snake);
        }
        
        public int getSnakeLength() {
            return snake.size();
        }
        
        public int getMoves() {
            return moves;
        }
        
        public Direction getCurrentDirection() {
            return currentDirection;
        }
        
        public void resetGame() {
            snake.clear();
            occupied.clear();
            moves = 0;
            gameOver = false;
            currentDirection = Direction.RIGHT;
            initializeSnake();
        }
    }
    
    // Approach 2: Food-based Growth Snake Game
    public static class FoodBasedSnakeGame implements SnakeGame {
        private Deque<Position> snake;
        private Set<Position> occupied;
        private Position food;
        private Direction currentDirection;
        private int boardWidth;
        private int boardHeight;
        private boolean gameOver;
        private int score;
        private Random random;
        
        public FoodBasedSnakeGame(int boardWidth, int boardHeight) {
            this.boardWidth = boardWidth;
            this.boardHeight = boardHeight;
            this.snake = new ArrayDeque<>();
            this.occupied = new HashSet<>();
            this.gameOver = false;
            this.score = 0;
            this.currentDirection = Direction.RIGHT;
            this.random = new Random();
            
            initializeSnake();
            generateFood();
        }
        
        private void initializeSnake() {
            int centerX = boardWidth / 2;
            int centerY = boardHeight / 2;
            
            for (int i = 0; i < 3; i++) {
                Position pos = new Position(centerX - i, centerY);
                snake.addLast(pos);
                occupied.add(pos);
            }
        }
        
        private void generateFood() {
            List<Position> availablePositions = new ArrayList<>();
            
            for (int x = 0; x < boardWidth; x++) {
                for (int y = 0; y < boardHeight; y++) {
                    Position pos = new Position(x, y);
                    if (!occupied.contains(pos)) {
                        availablePositions.add(pos);
                    }
                }
            }
            
            if (!availablePositions.isEmpty()) {
                food = availablePositions.get(random.nextInt(availablePositions.size()));
            } else {
                food = null; // Board is full, game should end
            }
        }
        
        @Override
        public void moveSnake(Direction direction) {
            if (gameOver) {
                return;
            }
            
            // Prevent immediate reverse direction
            if (direction == currentDirection.opposite() && snake.size() > 1) {
                direction = currentDirection;
            }
            
            currentDirection = direction;
            Position head = snake.peekFirst();
            Position newHead = head.move(direction);
            
            // Wrap around the board if needed
            newHead = wrapAroundIfNeeded(newHead);
            
            // Check self collision
            if (occupied.contains(newHead)) {
                gameOver = true;
                return;
            }
            
            // Add new head
            snake.addFirst(newHead);
            occupied.add(newHead);
            
            // Check if food is eaten
            boolean ateFood = newHead.equals(food);
            
            if (ateFood) {
                score++;
                generateFood();
                // Snake grows (don't remove tail)
            } else {
                // Remove tail if no food eaten
                Position tail = snake.removeLast();
                occupied.remove(tail);
            }
            
            // Check if board is full (win condition)
            if (food == null) {
                gameOver = true;
            }
        }
        
        @Override
        public boolean isGameOver() {
            return gameOver;
        }
        
        private boolean isOutOfBounds(Position pos) {
            return pos.getX() < 0 || pos.getX() >= boardWidth || 
                   pos.getY() < 0 || pos.getY() >= boardHeight;
        }
        
        private Position wrapAroundIfNeeded(Position pos) {
            int x = pos.getX();
            int y = pos.getY();
            
            // Wrap around X axis
            if (x < 0) {
                x = boardWidth - 1;
            } else if (x >= boardWidth) {
                x = 0;
            }
            
            // Wrap around Y axis
            if (y < 0) {
                y = boardHeight - 1;
            } else if (y >= boardHeight) {
                y = 0;
            }
            
            return new Position(x, y);
        }
        
        public List<Position> getSnakeBody() {
            return new ArrayList<>(snake);
        }
        
        public Position getFood() {
            return food;
        }
        
        public int getScore() {
            return score;
        }
        
        public int getSnakeLength() {
            return snake.size();
        }
        
        public boolean hasWon() {
            return gameOver && food == null;
        }
        
        public void resetGame() {
            snake.clear();
            occupied.clear();
            gameOver = false;
            score = 0;
            currentDirection = Direction.RIGHT;
            initializeSnake();
            generateFood();
        }
    }
    
    // Approach 3: Advanced Snake Game with Multiple Game Modes
    public static class AdvancedSnakeGame implements SnakeGame {
        public enum GameMode {
            TIME_BASED,
            FOOD_BASED,
            MIXED
        }
        
        private TimeBasedSnakeGame timeBasedGame;
        private FoodBasedSnakeGame foodBasedGame;
        private GameMode currentMode;
        
        public AdvancedSnakeGame(int boardWidth, int boardHeight, GameMode mode) {
            this.currentMode = mode;
            
            switch (mode) {
                case TIME_BASED:
                    timeBasedGame = new TimeBasedSnakeGame(boardWidth, boardHeight);
                    break;
                case FOOD_BASED:
                    foodBasedGame = new FoodBasedSnakeGame(boardWidth, boardHeight);
                    break;
                case MIXED:
                    // Could implement a hybrid mode
                    timeBasedGame = new TimeBasedSnakeGame(boardWidth, boardHeight);
                    break;
            }
        }
        
        @Override
        public void moveSnake(Direction direction) {
            switch (currentMode) {
                case TIME_BASED:
                case MIXED:
                    timeBasedGame.moveSnake(direction);
                    break;
                case FOOD_BASED:
                    foodBasedGame.moveSnake(direction);
                    break;
            }
        }
        
        @Override
        public boolean isGameOver() {
            switch (currentMode) {
                case TIME_BASED:
                case MIXED:
                    return timeBasedGame.isGameOver();
                case FOOD_BASED:
                    return foodBasedGame.isGameOver();
                default:
                    return true;
            }
        }
        
        public List<Position> getSnakeBody() {
            switch (currentMode) {
                case TIME_BASED:
                case MIXED:
                    return timeBasedGame.getSnakeBody();
                case FOOD_BASED:
                    return foodBasedGame.getSnakeBody();
                default:
                    return new ArrayList<>();
            }
        }
        
        public GameMode getCurrentMode() {
            return currentMode;
        }
    }
    
    // Game Simulator for testing
    public static class GameSimulator {
        public static void simulateGame(SnakeGame game, Direction[] moves) {
            System.out.println("Starting game simulation...");
            
            for (int i = 0; i < moves.length && !game.isGameOver(); i++) {
                System.out.println("Move " + (i + 1) + ": " + moves[i]);
                game.moveSnake(moves[i]);
                
                if (game instanceof TimeBasedSnakeGame) {
                    TimeBasedSnakeGame tbGame = (TimeBasedSnakeGame) game;
                    System.out.println("Snake length: " + tbGame.getSnakeLength() + 
                                     ", Total moves: " + tbGame.getMoves());
                } else if (game instanceof FoodBasedSnakeGame) {
                    FoodBasedSnakeGame fbGame = (FoodBasedSnakeGame) game;
                    System.out.println("Snake length: " + fbGame.getSnakeLength() + 
                                     ", Score: " + fbGame.getScore());
                }
            }
            
            System.out.println("Game Over: " + game.isGameOver());
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Testing Time-based Snake Game ===");
        testTimeBasedGame();
        
        System.out.println("\n=== Testing Food-based Snake Game ===");
        testFoodBasedGame();
        
        System.out.println("\n=== Testing Game Simulation ===");
        testGameSimulation();
        
        System.out.println("\n=== Testing Wrap-Around Functionality ===");
//         testWrapAroundFunctionality();
    }
    
    private static void testTimeBasedGame() {
        TimeBasedSnakeGame game = new TimeBasedSnakeGame(10, 10);
        
        System.out.println("Initial snake length: " + game.getSnakeLength());
        System.out.println("Initial snake body: " + game.getSnakeBody());
        
        // Make some moves
        for (int i = 0; i < 6; i++) {
            game.moveSnake(Direction.RIGHT);
            System.out.println("After move " + (i + 1) + ": length=" + 
                             game.getSnakeLength() + ", moves=" + game.getMoves());
        }
        
        System.out.println("Game over: " + game.isGameOver());
    }
    
    private static void testFoodBasedGame() {
        FoodBasedSnakeGame game = new FoodBasedSnakeGame(5, 5);
        
        System.out.println("Initial state:");
        System.out.println("Snake: " + game.getSnakeBody());
        System.out.println("Food: " + game.getFood());
        System.out.println("Score: " + game.getScore());
        
        // Try to reach food
        for (int i = 0; i < 10 && !game.isGameOver(); i++) {
            Direction dir = (i % 2 == 0) ? Direction.DOWN : Direction.UP;
            game.moveSnake(dir);
            System.out.println("Move " + (i + 1) + ": Score=" + game.getScore() + 
                             ", Length=" + game.getSnakeLength());
        }
    }
    
    private static void testGameSimulation() {
        TimeBasedSnakeGame game = new TimeBasedSnakeGame(8, 8);
        
        Direction[] moves = {
            Direction.RIGHT, Direction.RIGHT, Direction.DOWN,
            Direction.DOWN, Direction.LEFT, Direction.LEFT,
            Direction.UP, Direction.UP, Direction.RIGHT
        };
        
        GameSimulator.simulateGame(game, moves);
    }
}