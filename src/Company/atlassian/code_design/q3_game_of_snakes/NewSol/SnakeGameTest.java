package Company.atlassian.code_design.q3_game_of_snakes.NewSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SnakeGameTest {

    @Nested
    @DisplayName("Position Tests")
    class PositionTests {

        @Test
        @DisplayName("Should create position with correct coordinates")
        void testPositionCreation() {
            Position pos = new Position(5, 10);
            assertEquals(5, pos.getX());
            assertEquals(10, pos.getY());
        }

        @Test
        @DisplayName("Should move position in correct direction")
        void testPositionMove() {
            Position pos = new Position(5, 5);

            assertEquals(new Position(5, 4), pos.move(Direction.UP));
            assertEquals(new Position(5, 6), pos.move(Direction.DOWN));
            assertEquals(new Position(4, 5), pos.move(Direction.LEFT));
            assertEquals(new Position(6, 5), pos.move(Direction.RIGHT));
        }

        @Test
        @DisplayName("Should check equality correctly")
        void testPositionEquality() {
            Position pos1 = new Position(3, 4);
            Position pos2 = new Position(3, 4);
            Position pos3 = new Position(4, 3);

            assertEquals(pos1, pos2);
            assertNotEquals(pos1, pos3);
        }
    }

    @Nested
    @DisplayName("Snake Tests")
    class SnakeTests {

        private Snake snake;

        @BeforeEach
        void setUp() {
            snake = new Snake(new Position(5, 5), 3);
        }

        @Test
        @DisplayName("Should initialize with correct size")
        void testInitialSize() {
            assertEquals(3, snake.getSize());
        }

        @Test
        @DisplayName("Should have correct initial body positions")
        void testInitialBody() {
            assertEquals(new Position(5, 5), snake.getBody().get(0));
            assertEquals(new Position(4, 5), snake.getBody().get(1));
            assertEquals(new Position(3, 5), snake.getBody().get(2));
        }

        @Test
        @DisplayName("Should move correctly without growing")
        void testMoveWithoutGrow() {
            snake.move(Direction.RIGHT);

            assertEquals(3, snake.getSize());
            assertEquals(new Position(6, 5), snake.getHead());
        }

        @Test
        @DisplayName("Should grow when flagged")
        void testGrow() {
            snake.grow();
            snake.move(Direction.RIGHT);

            assertEquals(4, snake.getSize());
        }

        @Test
        @DisplayName("Should wrap around correctly")
        void testMoveWithWrap() {
            Snake snake = new Snake(new Position(0, 5), 3);
            snake.moveWithWrap(Direction.LEFT, 10, 10);

            assertEquals(new Position(9, 5), snake.getHead());
        }

        @Test
        @DisplayName("Should detect self collision")
        void testSelfCollision() {
            assertFalse(snake.hasCollisionWithSelf());

            snake.grow();
            snake.move(Direction.RIGHT);
            snake.grow();
            snake.move(Direction.RIGHT);
            snake.grow();
            snake.move(Direction.DOWN);
            snake.move(Direction.LEFT);
            snake.move(Direction.LEFT);
            snake.move(Direction.UP);

            assertTrue(snake.hasCollisionWithSelf());
        }
    }

    @Nested
    @DisplayName("Basic SnakeGame Tests")
    class BasicGameTests {

        private SnakeGameImpl game;

        @BeforeEach
        void setUp() {
            game = new SnakeGameImpl(20, 20);
        }

        @Test
        @DisplayName("Should not be game over initially")
        void testInitialState() {
            assertFalse(game.isGameOver());
            assertEquals(0, game.getMoveCount());
        }

        @Test
        @DisplayName("Should start with snake of size 3")
        void testInitialSnakeSize() {
            assertEquals(3, game.getSnake().getSize());
        }

        @Test
        @DisplayName("Should increment move count on each move")
        void testMoveCount() {
            game.moveSnake(Direction.RIGHT);
            assertEquals(1, game.getMoveCount());

            game.moveSnake(Direction.RIGHT);
            assertEquals(2, game.getMoveCount());
        }

        @Test
        @DisplayName("Should grow snake every 5 moves")
        void testGrowEvery5Moves() {
            assertEquals(3, game.getSnake().getSize());

            for (int i = 0; i < 5; i++) {
                game.moveSnake(Direction.RIGHT);
            }
            assertEquals(4, game.getSnake().getSize());

            for (int i = 0; i < 5; i++) {
                game.moveSnake(Direction.RIGHT);
            }
            assertEquals(5, game.getSnake().getSize());
        }

        @Test
        @DisplayName("Should end game when snake hits wall")
        void testHitWall() {
            for (int i = 0; i < 20; i++) {
                game.moveSnake(Direction.RIGHT);
                if (game.isGameOver()) break;
            }

            assertTrue(game.isGameOver());
        }

        @Test
        @DisplayName("Should end game when snake hits itself")
        void testHitSelf() {
            for (int i = 0; i < 15; i++) {
                game.moveSnake(Direction.RIGHT);
            }

            game.moveSnake(Direction.DOWN);
            game.moveSnake(Direction.LEFT);
            game.moveSnake(Direction.UP);

            assertTrue(game.isGameOver());
        }

        @Test
        @DisplayName("Should not move after game over")
        void testNoMoveAfterGameOver() {
            for (int i = 0; i < 20; i++) {
                game.moveSnake(Direction.RIGHT);
            }

            assertTrue(game.isGameOver());
            int moveCountAtGameOver = game.getMoveCount();

            game.moveSnake(Direction.DOWN);

            assertEquals(moveCountAtGameOver, game.getMoveCount());
        }
    }

    @Nested
    @DisplayName("Wrap Around Tests")
    class WrapAroundTests {

        private SnakeGameImpl game;

        @BeforeEach
        void setUp() {
            game = new SnakeGameImpl(10, 10, true);  // Wrap around enabled
        }

        @Test
        @DisplayName("Should wrap around when hitting right wall")
        void testWrapRight() {
            // Move snake to right edge
            for (int i = 0; i < 10; i++) {
                game.moveSnake(Direction.RIGHT);
            }

            assertFalse(game.isGameOver());
            assertEquals(0, game.getSnake().getHead().getX());
        }

        @Test
        @DisplayName("Should wrap around when hitting left wall")
        void testWrapLeft() {
            // Move snake to left edge
            for (int i = 0; i < 10; i++) {
                game.moveSnake(Direction.LEFT);
            }

            assertFalse(game.isGameOver());
            assertEquals(9, game.getSnake().getHead().getX());
        }

        @Test
        @DisplayName("Should wrap around when hitting top wall")
        void testWrapTop() {
            for (int i = 0; i < 10; i++) {
                game.moveSnake(Direction.UP);
            }

            assertFalse(game.isGameOver());
            assertEquals(9, game.getSnake().getHead().getY());
        }

        @Test
        @DisplayName("Should wrap around when hitting bottom wall")
        void testWrapBottom() {
            for (int i = 0; i < 10; i++) {
                game.moveSnake(Direction.DOWN);
            }

            assertFalse(game.isGameOver());
            assertEquals(0, game.getSnake().getHead().getY());
        }

        @Test
        @DisplayName("Should still detect self collision with wrap around")
        void testSelfCollisionWithWrap() {
            // Grow snake and make it collide
            for (int i = 0; i < 20; i++) {
                game.moveSnake(Direction.RIGHT);
            }

            game.moveSnake(Direction.DOWN);
            game.moveSnake(Direction.LEFT);
            game.moveSnake(Direction.UP);

            assertTrue(game.isGameOver());
        }
    }

    @Nested
    @DisplayName("Food Based Growth Tests")
    class FoodGrowthTests {

        private SnakeGameImpl game;

        @BeforeEach
        void setUp() {
            game = new SnakeGameImpl(20, 20, new FoodBasedGrowthStrategy(), false, true);
        }

        @Test
        @DisplayName("Should have food on board")
        void testFoodExists() {
            assertNotNull(game.getFood());
            assertNotNull(game.getFood().getPosition());
        }

        @Test
        @DisplayName("Food should not spawn on snake")
        void testFoodNotOnSnake() {
            Position foodPos = game.getFood().getPosition();
            assertFalse(game.getSnake().containsPosition(foodPos));
        }

        @Test
        @DisplayName("Should not grow without eating food")
        void testNoGrowWithoutFood() {
            int initialSize = game.getSnake().getSize();

            // Move away from food
            game.moveSnake(Direction.UP);
            game.moveSnake(Direction.UP);
            game.moveSnake(Direction.UP);
            game.moveSnake(Direction.UP);
            game.moveSnake(Direction.UP);

            assertEquals(initialSize, game.getSnake().getSize());
        }
    }

    @Nested
    @DisplayName("Growth Strategy Tests")
    class GrowthStrategyTests {

        @Test
        @DisplayName("IntervalGrowthStrategy should grow at correct intervals")
        void testIntervalGrowth() {
            GrowthStrategy strategy = new IntervalGrowthStrategy(3);

            assertFalse(strategy.shouldGrow(1, false));
            assertFalse(strategy.shouldGrow(2, false));
            assertTrue(strategy.shouldGrow(3, false));
            assertFalse(strategy.shouldGrow(4, false));
            assertFalse(strategy.shouldGrow(5, false));
            assertTrue(strategy.shouldGrow(6, false));
        }

        @Test
        @DisplayName("FoodBasedGrowthStrategy should grow only when food eaten")
        void testFoodGrowth() {
            GrowthStrategy strategy = new FoodBasedGrowthStrategy();

            assertFalse(strategy.shouldGrow(1, false));
            assertFalse(strategy.shouldGrow(5, false));
            assertFalse(strategy.shouldGrow(10, false));

            assertTrue(strategy.shouldGrow(1, true));
            assertTrue(strategy.shouldGrow(5, true));
        }

        @Test
        @DisplayName("Custom interval should work correctly")
        void testCustomInterval() {
            SnakeGameImpl game = new SnakeGameImpl(20, 20,
                    new IntervalGrowthStrategy(2), false, false);

            assertEquals(3, game.getSnake().getSize());

            game.moveSnake(Direction.RIGHT);  // Move 1
            assertEquals(3, game.getSnake().getSize());

            game.moveSnake(Direction.RIGHT);  // Move 2 - grow!
            assertEquals(4, game.getSnake().getSize());

            game.moveSnake(Direction.RIGHT);  // Move 3
            assertEquals(4, game.getSnake().getSize());

            game.moveSnake(Direction.RIGHT);  // Move 4 - grow!
            assertEquals(5, game.getSnake().getSize());
        }
    }

    @Nested
    @DisplayName("Direction Tests")
    class DirectionTests {

        @Test
        @DisplayName("Should have correct dx and dy values")
        void testDirectionValues() {
            assertEquals(0, Direction.UP.getDx());
            assertEquals(-1, Direction.UP.getDy());

            assertEquals(0, Direction.DOWN.getDx());
            assertEquals(1, Direction.DOWN.getDy());

            assertEquals(-1, Direction.LEFT.getDx());
            assertEquals(0, Direction.LEFT.getDy());

            assertEquals(1, Direction.RIGHT.getDx());
            assertEquals(0, Direction.RIGHT.getDy());
        }
    }
}