//package practice.atlassian.code_design.q3_game_of_snakes;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.DisplayName;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
///**
// * Unit tests for the Snake Game implementations.
// */
//public class SnakeGameTest {
//
//    private Solution.FoodBasedSnakeGame foodBasedGame;
//    private Solution.TimeBasedSnakeGame timeBasedGame;
//    private Solution.AdvancedSnakeGame advancedGame;
//
//    @BeforeEach
//    public void setUp() {
//        // Create fresh game instances for each test
//        foodBasedGame = new Solution.FoodBasedSnakeGame(10, 10);
//        timeBasedGame = new Solution.TimeBasedSnakeGame(10, 10);
//        advancedGame = new Solution.AdvancedSnakeGame(10, 10, Solution.AdvancedSnakeGame.GameMode.FOOD_BASED);
//    }
//
//    @Test
//    @DisplayName("Test basic snake initialization")
//    public void testSnakeInitialization() {
//        // Test food-based game initialization
//        assertFalse(foodBasedGame.isGameOver());
//        assertEquals(1, foodBasedGame.getSnakeLength());
//
//        // Test time-based game initialization
//        assertFalse(timeBasedGame.isGameOver());
//        assertEquals(1, timeBasedGame.getSnakeLength());
//
//        // Test advanced game initialization
//        assertFalse(advancedGame.isGameOver());
//        assertEquals(1, advancedGame.getSnakeLength());
//    }
//
//    @Test
//    @DisplayName("Test snake movement")
//    public void testSnakeMovement() {
//        // Get initial head position
//        Position initialHead = foodBasedGame.getSnakePositions().get(0);
//
//        // Move right
//        foodBasedGame.changeDirection(Direction.RIGHT);
//        foodBasedGame.update();
//
//        // Get new head position
//        Position newHead = foodBasedGame.getSnakePositions().get(0);
//
//        // Should be one position to the right
//        assertEquals(initialHead.getX() + 1, newHead.getX());
//        assertEquals(initialHead.getY(), newHead.getY());
//    }
//
//    @Test
//    @DisplayName("Test snake growth in food-based game")
//    public void testSnakeGrowthInFoodBasedGame() {
//        // Force food to appear at a specific position
//        Position foodPosition = new Position(5, 5);
//        foodBasedGame.placeFood(foodPosition);
//
//        // Navigate snake to food
//        moveSnakeTo(foodBasedGame, foodPosition);
//
//        // After eating food, snake should grow
//        assertEquals(2, foodBasedGame.getSnakeLength());
//    }
//
//    @Test
//    @DisplayName("Test snake growth in time-based game")
//    public void testSnakeGrowthInTimeBasedGame() {
//        int initialLength = timeBasedGame.getSnakeLength();
//
//        // Simulate time passing
//        for (int i = 0; i < timeBasedGame.getGrowthInterval(); i++) {
//            timeBasedGame.update();
//        }
//
//        // After growth interval passes, snake should grow
//        assertEquals(initialLength + 1, timeBasedGame.getSnakeLength());
//    }
//
//    @Test
//    @DisplayName("Test game over when snake hits wall")
//    public void testGameOverWhenSnakeHitsWall() {
//        // Move snake to the wall
//        while (!foodBasedGame.isGameOver()) {
//            foodBasedGame.changeDirection(Direction.RIGHT);
//            foodBasedGame.update();
//
//            // Break if we're getting close to an infinite loop
//            if (foodBasedGame.getSnakePositions().get(0).getX() >= 9) {
//                break;
//            }
//        }
//
//        // One more move should cause game over
//        foodBasedGame.update();
//
//        // Game should be over
//        assertTrue(foodBasedGame.isGameOver());
//    }
//
//    @Test
//    @DisplayName("Test game over when snake hits itself")
//    public void testGameOverWhenSnakeHitsItself() {
//        // First, grow the snake
//        for (int i = 0; i < 5; i++) {
//            // Place food ahead of snake
//            Position head = foodBasedGame.getSnakePositions().get(0);
//            Position foodPosition = new Position(head.getX() + 1, head.getY());
//            foodBasedGame.placeFood(foodPosition);
//
//            // Move to eat food
//            foodBasedGame.changeDirection(Direction.RIGHT);
//            foodBasedGame.update();
//        }
//
//        // Now try to make a U-turn to hit itself
//        foodBasedGame.changeDirection(Direction.DOWN);
//        foodBasedGame.update();
//        foodBasedGame.changeDirection(Direction.LEFT);
//        foodBasedGame.update();
//        foodBasedGame.changeDirection(Direction.UP);
//        foodBasedGame.update();
//
//        // Game should be over
//        assertTrue(foodBasedGame.isGameOver());
//    }
//
//    @Test
//    @DisplayName("Test advanced game mode switching")
//    public void testAdvancedGameModeSwitching() {
//        // Start in food-based mode
//        assertEquals(Solution.AdvancedSnakeGame.GameMode.FOOD_BASED, advancedGame.getCurrentMode());
//
//        // Switch to time-based mode
//        advancedGame.switchMode(Solution.AdvancedSnakeGame.GameMode.TIME_BASED);
//        assertEquals(Solution.AdvancedSnakeGame.GameMode.TIME_BASED, advancedGame.getCurrentMode());
//
//        // Game should still be active
//        assertFalse(advancedGame.isGameOver());
//
//        // Verify the growth mechanism changed
//        int initialLength = advancedGame.getSnakeLength();
//
//        // In time-based mode, snake should grow after a specific time
//        for (int i = 0; i < advancedGame.getGrowthInterval(); i++) {
//            advancedGame.update();
//        }
//
//        // Snake should have grown
//        assertEquals(initialLength + 1, advancedGame.getSnakeLength());
//    }
//
//    @Test
//    @DisplayName("Test game restart")
//    public void testGameRestart() {
//        // First, grow the snake and end the game
//        for (int i = 0; i < 3; i++) {
//            // Place food ahead of snake
//            Position head = foodBasedGame.getSnakePositions().get(0);
//            Position foodPosition = new Position(head.getX() + 1, head.getY());
//            foodBasedGame.placeFood(foodPosition);
//
//            // Move to eat food
//            foodBasedGame.changeDirection(Direction.RIGHT);
//            foodBasedGame.update();
//        }
//
//        // Move to the wall
//        while (!foodBasedGame.isGameOver()) {
//            foodBasedGame.update();
//        }
//
//        // Verify game is over
//        assertTrue(foodBasedGame.isGameOver());
//
//        // Restart game
//        foodBasedGame.restart();
//
//        // Verify game is restarted
//        assertFalse(foodBasedGame.isGameOver());
//        assertEquals(1, foodBasedGame.getSnakeLength());
//    }
//
//    @Test
//    @DisplayName("Test score calculation")
//    public void testScoreCalculation() {
//        // Initially score should be 0
//        assertEquals(0, foodBasedGame.getScore());
//
//        // Grow snake to increase score
//        for (int i = 0; i < 3; i++) {
//            // Place food ahead of snake
//            Position head = foodBasedGame.getSnakePositions().get(0);
//            Position foodPosition = new Position(head.getX() + 1, head.getY());
//            foodBasedGame.placeFood(foodPosition);
//
//            // Move to eat food
//            foodBasedGame.changeDirection(Direction.RIGHT);
//            foodBasedGame.update();
//        }
//
//        // Score should be greater than 0
//        assertTrue(foodBasedGame.getScore() > 0);
//    }
//
//    /**
//     * Helper method to move the snake to a specific position
//     */
//    private void moveSnakeTo(SnakeGame game, Position target) {
//        Position head = game.getSnakePositions().get(0);
//
//        // Move horizontally first
//        while (head.getX() != target.getX()) {
//            if (head.getX() < target.getX()) {
//                game.changeDirection(Direction.RIGHT);
//            } else {
//                game.changeDirection(Direction.LEFT);
//            }
//            game.update();
//            head = game.getSnakePositions().get(0);
//        }
//
//        // Then move vertically
//        while (head.getY() != target.getY()) {
//            if (head.getY() < target.getY()) {
//                game.changeDirection(Direction.DOWN);
//            } else {
//                game.changeDirection(Direction.UP);
//            }
//            game.update();
//            head = game.getSnakePositions().get(0);
//        }
//    }
//}