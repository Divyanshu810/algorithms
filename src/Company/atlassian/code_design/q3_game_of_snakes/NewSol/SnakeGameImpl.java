package Company.atlassian.code_design.q3_game_of_snakes.NewSol;


import java.util.Random;

import java.util.*;

import java.util.Objects;

/*
┌─────────────────────────────────────────────────────────────────┐
│                    <<enum>>                                      │
│                   Direction                                      │
├─────────────────────────────────────────────────────────────────┤
│ UP, DOWN, LEFT, RIGHT                                           │
│ + getDx(): int                                                  │
│ + getDy(): int                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    Position                                      │
├─────────────────────────────────────────────────────────────────┤
│ - x: int                                                        │
│ - y: int                                                        │
├─────────────────────────────────────────────────────────────────┤
│ + getX(): int                                                   │
│ + getY(): int                                                   │
│ + equals(), hashCode()                                          │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     Snake                                        │
├─────────────────────────────────────────────────────────────────┤
│ - body: LinkedList<Position>                                    │
├─────────────────────────────────────────────────────────────────┤
│ + move(direction: Direction): void                              │
│ + grow(): void                                                  │
│ + getHead(): Position                                           │
│ + hasCollisionWithSelf(): boolean                               │
│ + getBody(): List<Position>                                     │
│ + getSize(): int                                                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                <<interface>>                                     │
│                  SnakeGame                                       │
├─────────────────────────────────────────────────────────────────┤
│ + moveSnake(direction: Direction): void                         │
│ + isGameOver(): boolean                                         │
└─────────────────────────────────────────────────────────────────┘
                        ▲
                        │ implements
┌─────────────────────────────────────────────────────────────────┐
│                  SnakeGameImpl                                   │
├─────────────────────────────────────────────────────────────────┤
│ - width: int                                                    │
│ - height: int                                                   │
│ - snake: Snake                                                  │
│ - moveCount: int                                                │
│ - gameOver: boolean                                             │
│ - INITIAL_SIZE: int = 3                                         │
│ - GROW_INTERVAL: int = 5                                        │
├─────────────────────────────────────────────────────────────────┤
│ + moveSnake(direction: Direction): void                         │
│ + isGameOver(): boolean                                         │
│ + getSnake(): Snake                                             │
│ + getMoveCount(): int                                           │
│ - isOutOfBounds(position: Position): boolean                    │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                <<interface>>                                     │
│               GrowthStrategy                                     │
├─────────────────────────────────────────────────────────────────┤
│ + shouldGrow(moveCount: int, ateFood: boolean): boolean         │
└─────────────────────────────────────────────────────────────────┘
          ▲                              ▲
          │                              │
┌─────────────────────┐      ┌─────────────────────┐
│ IntervalGrowth      │      │ FoodBasedGrowth     │
├─────────────────────┤      ├─────────────────────┤
│ - interval: int     │      │                     │
├─────────────────────┤      ├─────────────────────┤
│ + shouldGrow()      │      │ + shouldGrow()      │
└─────────────────────┘      └─────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       Food                                       │
├─────────────────────────────────────────────────────────────────┤
│ - position: Position                                            │
├─────────────────────────────────────────────────────────────────┤
│ + getPosition(): Position                                       │
│ + respawn(width, height, snake): void                           │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                  SnakeGameImpl                                   │
├─────────────────────────────────────────────────────────────────┤
│ - snake: Snake                                                  │
│ - food: Food                                                    │
│ - growthStrategy: GrowthStrategy                                │
│ - wrapAround: boolean                                           │
├─────────────────────────────────────────────────────────────────┤
│ + moveSnake(direction): void                                    │
│ + isGameOver(): boolean                                         │
│ - handleWrapAround(position): Position                          │
└─────────────────────────────────────────────────────────────────┘
 */

enum Direction {
    UP(0, -1),
    DOWN(0, 1),
    LEFT(-1, 0),
    RIGHT(1, 0);

    private final int dx;
    private final int dy;

    Direction(int dx, int dy) {
        this.dx = dx;
        this.dy = dy;
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }
}

class Position {
    private final int x;
    private final int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Position move(Direction direction) {
        return new Position(x + direction.getDx(), y + direction.getDy());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return x == position.x && y == position.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}

class Snake {
    private final LinkedList<Position> body;
    private boolean shouldGrow;

    public Snake(Position startPosition, int initialSize) {
        body = new LinkedList<>();

        for (int i = 0; i < initialSize; i++) {
            body.add(new Position(startPosition.getX() - i, startPosition.getY()));
        }

        shouldGrow = false;
    }

    public void move(Direction direction) {
        Position newHead = body.getFirst().move(direction);
        body.addFirst(newHead);

        if (shouldGrow) {
            shouldGrow = false;
        } else {
            body.removeLast();
        }
    }

    // Scale-Up: Wrap around support
    public void moveWithWrap(Direction direction, int width, int height) {
        Position newHead = body.getFirst().move(direction);

        // Wrap around logic
        int newX = (newHead.getX() % width + width) % width;
        int newY = (newHead.getY() % height + height) % height;
        newHead = new Position(newX, newY);

        body.addFirst(newHead);

        if (shouldGrow) {
            shouldGrow = false;
        } else {
            body.removeLast();
        }
    }

    public void grow() {
        shouldGrow = true;
    }

    public Position getHead() {
        return body.getFirst();
    }

    public List<Position> getBody() {
        return Collections.unmodifiableList(body);
    }

    public int getSize() {
        return body.size();
    }

    public boolean hasCollisionWithSelf() {
        Position head = getHead();

        for (int i = 1; i < body.size(); i++) {
            if (head.equals(body.get(i))) {
                return true;
            }
        }
        return false;
    }

    public boolean containsPosition(Position position) {
        return body.contains(position);
    }
}

interface GrowthStrategy {
    boolean shouldGrow(int moveCount, boolean ateFood);
}

class IntervalGrowthStrategy implements GrowthStrategy {

    private final int interval;

    public IntervalGrowthStrategy(int interval) {
        this.interval = interval;
    }

    @Override
    public boolean shouldGrow(int moveCount, boolean ateFood) {
        return moveCount % interval == 0;
    }
}

class FoodBasedGrowthStrategy implements GrowthStrategy {

    @Override
    public boolean shouldGrow(int moveCount, boolean ateFood) {
        return ateFood;
    }
}

class Food {
    private Position position;
    private final Random random;

    public Food(int width, int height, Snake snake) {
        random = new Random();
        respawn(width, height, snake);
    }

    public Position getPosition() {
        return position;
    }

    public void respawn(int width, int height, Snake snake) {
        do {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            position = new Position(x, y);
        } while (snake.containsPosition(position));  // Don't spawn on snake
    }
}

interface SnakeGame {
    void moveSnake(Direction direction);
    boolean isGameOver();
}

public class SnakeGameImpl implements SnakeGame {

    private static final int INITIAL_SIZE = 3;
    private static final int DEFAULT_GROW_INTERVAL = 5;

    private final int width;
    private final int height;
    private final Snake snake;
    private final GrowthStrategy growthStrategy;
    private final boolean wrapAround;

    // Scale-Up: Food
    private final Food food;
    private final boolean foodEnabled;

    private int moveCount;
    private boolean gameOver;

    // Basic constructor (interval growth, no wrap)
    public SnakeGameImpl(int width, int height) {
        this(width, height, new IntervalGrowthStrategy(DEFAULT_GROW_INTERVAL), false, false);
    }

    // Constructor with wrap around option
    public SnakeGameImpl(int width, int height, boolean wrapAround) {
        this(width, height, new IntervalGrowthStrategy(DEFAULT_GROW_INTERVAL), wrapAround, false);
    }

    // Full constructor with all options
    public SnakeGameImpl(int width, int height, GrowthStrategy growthStrategy,
                         boolean wrapAround, boolean foodEnabled) {
        this.width = width;
        this.height = height;
        this.growthStrategy = growthStrategy;
        this.wrapAround = wrapAround;
        this.foodEnabled = foodEnabled;
        this.moveCount = 0;
        this.gameOver = false;

        Position startPosition = new Position(width / 2, height / 2);
        this.snake = new Snake(startPosition, INITIAL_SIZE);

        // Initialize food if enabled
        if (foodEnabled) {
            this.food = new Food(width, height, snake);
        } else {
            this.food = null;
        }
    }

    @Override
    public void moveSnake(Direction direction) {
        if (gameOver) {
            return;
        }

        moveCount++;

        // Check if snake ate food
        boolean ateFood = false;
        if (foodEnabled && food != null) {
            ateFood = snake.getHead().move(direction).equals(food.getPosition());
        }

        // Check if snake should grow
        if (growthStrategy.shouldGrow(moveCount, ateFood)) {
            snake.grow();
        }

        // Move snake (with or without wrap)
        if (wrapAround) {
            snake.moveWithWrap(direction, width, height);
        } else {
            snake.move(direction);

            // Check wall collision only if no wrap around
            if (isOutOfBounds(snake.getHead())) {
                gameOver = true;
                return;
            }
        }

        // Check self collision
        if (snake.hasCollisionWithSelf()) {
            gameOver = true;
            return;
        }

        // Respawn food if eaten
        if (ateFood && food != null) {
            food.respawn(width, height, snake);
        }
    }

    @Override
    public boolean isGameOver() {
        return gameOver;
    }

    private boolean isOutOfBounds(Position position) {
        return position.getX() < 0 || position.getX() >= width ||
                position.getY() < 0 || position.getY() >= height;
    }

    // Getters for testing
    public Snake getSnake() {
        return snake;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public Food getFood() {
        return food;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
