# OOP Principles and Design Patterns in Game of Snakes Implementation

## OOP Principles

### 1. Encapsulation
- Private instance variables in all classes (snake, occupied, food, etc.)
- Public methods to expose functionality while hiding implementation details
- Accessor methods (getters) to safely access internal state
- Example: The `Position` class encapsulates x and y coordinates with proper getters

### 2. Abstraction
- `SnakeGame` interface defines the core game functionality (moveSnake, isGameOver)
- Implementation details are hidden behind the interface
- Different game implementations provide different abstractions of snake behavior

### 3. Inheritance
- `TimeBasedSnakeGame` and `FoodBasedSnakeGame` both implement the `SnakeGame` interface
- `AdvancedSnakeGame` uses composition to leverage both implementations

### 4. Polymorphism
- `GameSimulator` can work with any implementation of `SnakeGame` interface
- Type checking and casting in the simulator to access specific functionality
- Common interface with different behaviors in each implementation

## Design Patterns

### 1. Strategy Pattern
- `SnakeGame` interface defines a strategy for game behavior
- Different implementations (TimeBasedSnakeGame, FoodBasedSnakeGame) provide different strategies
- Clients can interchangeably use different snake game implementations

### 2. State Pattern
- Game maintains internal state (snake positions, direction, game status)
- State transitions occur based on moves and collisions
- Game behavior changes based on current state (e.g., no moves processed after game over)

### 3. Factory Method (Partial)
- The constructors of each game type serve as factory methods creating initialized games
- Each implementation handles the creation of its required internal objects

### 4. Composite Pattern (Partial)
- `AdvancedSnakeGame` composes other game implementations to create a more complex behavior
- Delegates operations to the appropriate concrete implementation

### 5. Template Method Pattern
- Base structure of the game is similar across implementations
- Specialized behavior like growth logic varies between implementations
- Common template for moving snake with specialized steps

## Additional OOP Concepts

### 1. Immutability
- `Position` objects are immutable - moving creates a new position rather than modifying existing one
- Enhances thread safety and simplifies reasoning about the game state

### 2. Delegation
- `AdvancedSnakeGame` delegates to concrete implementations based on game mode
- Follows "composition over inheritance" principle

### 3. Information Hiding
- Implementation details of snake movement, collision detection, and growth logic are hidden
- Only necessary information is exposed through public methods

### 4. Single Responsibility Principle
- Each class has a specific responsibility:
  - `Position`: Represents and manipulates coordinates
  - `Direction`: Encapsulates movement vectors
  - Game implementations: Handle game logic and state
  - `GameSimulator`: Focuses solely on simulation

### 5. Open/Closed Principle
- The design is open for extension (new game modes) but closed for modification
- New game variants can be added without changing existing code

## Summary
The Game of Snakes implementation demonstrates a well-structured object-oriented design with clear separation of concerns. It effectively uses encapsulation to protect state, polymorphism through interfaces, and appropriate design patterns to create a flexible and maintainable system. The code structure facilitates easy extension with additional features while maintaining a clean and coherent architecture.