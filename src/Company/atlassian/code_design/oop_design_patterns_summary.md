# OOP Principles and Design Patterns in Code Design Implementations

This document summarizes the Object-Oriented Programming (OOP) principles and design patterns used across the various code design implementations. These implementations demonstrate strong OOP practices and utilize several classic design patterns to create maintainable, extensible, and well-structured solutions.

## Core OOP Principles Used

### 1. Encapsulation
All implementations demonstrate strong encapsulation by:
- Using private/protected fields to hide implementation details
- Providing public methods for controlled access to functionality
- Using accessor methods (getters) to safely access internal state
- Preventing direct manipulation of internal data structures

**Examples:**
- `Position` class in Game of Snakes encapsulates x and y coordinates
- `Driver` class in F1 Last Lap Hero encapsulates lap time data
- `Screening` class in Cinema Screenings encapsulates movie and time information
- `DeploymentEvent` in Deployment Notification encapsulates event data
- `Rating` class in Customer Satisfaction encapsulates score and customer data
- `PricingPlan` in Cost Explorer encapsulates pricing details
- `RouteResult` in Middleware Router encapsulates routing results and parameters

### 2. Inheritance
Inheritance is used effectively to:
- Extend base functionality in specialized subclasses
- Reuse common code while adding new features
- Create class hierarchies that model the problem domain

**Examples:**
- `ScreeningWithRevenue extends Screening` to add revenue tracking
- `RevertDeploymentEvent extends DeploymentEvent` for specialized behavior
- `CinemaWithRevenue extends Cinema` to add revenue management
- `DriverWithPitStops` extends base driver functionality
- `Router` interface implemented by different router classes
- `DeploymentNotificationServiceWithRevert extends DeploymentNotificationService`
- `ExportFormat` interface implemented by different export formats

### 3. Polymorphism
Polymorphism is applied through:
- Method overriding to specialize behavior in subclasses
- Interface implementations for behavior contracts
- Runtime type checking and casting for specialized handling
- Consistent method signatures across different implementations

**Examples:**
- `printSchedule` overridden in `CinemaWithRevenue`
- `receiveEvent` method overridden in `DeploymentNotificationServiceWithRevert`
- `SnakeGame` interface implemented by different game variants
- `NotificationListener` interface for polymorphic callbacks
- `Router` interface implemented by `SimpleRouter`, `TrieRouter`, `PatternRouter`
- `ExportFormat` interface implemented by `CSVExportFormat`, `JSONExportFormat`, `XMLExportFormat`
- `addRoute` and `callRoute` methods implemented in different router strategies

### 4. Abstraction
Abstraction is achieved by:
- Modeling key concepts as classes with clear responsibilities
- Using interfaces to define contracts
- Hiding complex implementation details
- Focusing on what objects do rather than how they do it

**Examples:**
- `TimeSlot` abstracts a period of available time
- `TelemetryEvent` abstracts event data
- `SnakeGame` interface abstracts common game functionality
- `Notification` abstracts notification content and metadata
- `Router` interface abstracts routing functionality
- `ExportFormat` interface abstracts data export behavior
- `MonthlyCost` in Cost Explorer abstracts cost calculation details

## Design Patterns Applied

### 1. Observer Pattern
Used for event notification systems:
- Subjects maintain lists of observers
- Observers register/unregister with subjects
- Events trigger notifications to all registered observers

**Implementations:**
- **Deployment Notification:** `NotificationListener` for deployment notifications
- **F1 Last Lap Hero:** `TelemetryListener` for race updates
- **Game of Snakes:** Partial implementation in telemetry features
- **Customer Satisfaction:** Partial implementation for notification of changes

### 2. Strategy Pattern
Used to define families of algorithms:
- Different strategies encapsulated in separate classes
- Strategies are interchangeable
- Runtime selection of appropriate strategy

**Implementations:**
- **Game of Snakes:** Different game modes and growth strategies
- **Cinema Screenings:** Different scheduling optimization strategies
- **F1 Last Lap Hero:** Different lap time calculation strategies
- **Middleware Router:** Different routing algorithms (`SimpleRouter`, `TrieRouter`, `PatternRouter`)
- **Customer Satisfaction:** Different export formats for data representation
- **Cost Explorer:** Different calculation strategies based on plan types

### 3. Template Method Pattern
Used to define algorithmic skeletons:
- Base algorithm defined in parent class
- Specific steps implemented or overridden in subclasses
- Consistent structure with specialized behavior

**Implementations:**
- **Cinema Screenings:** Finding available time slots
- **Deployment Notification:** Event processing pipeline
- **F1 Last Lap Hero:** Last lap hero calculation
- **Middleware Router:** Base router functionality with specialized steps
- **Customer Satisfaction:** Service methods with common structure and specialized behavior

### 4. Factory Method Pattern
Used for object creation:
- Creation logic encapsulated in factory methods
- Dynamic instantiation based on parameters
- Consistent creation interface

**Implementations:**
- **Game of Snakes:** Game creation with different modes
- **F1 Last Lap Hero:** Dynamic driver creation with `computeIfAbsent`
- **Cinema Screenings:** Scheduling methods that create and initialize screenings
- **Middleware Router:** `RouterFactory` for creating different router implementations
- **Cost Explorer:** `PricingPlanFactory` for creating plans and products
- **Customer Satisfaction:** Methods to create `Rating` objects

### 5. Decorator Pattern
Used to add features to objects:
- Wraps objects to add functionality
- Maintains same interface as wrapped object
- Allows for flexible feature composition

**Implementations:**
- **Cinema Screenings:** `ScreeningWithRevenue` decorates base `Screening`
- **Customer Satisfaction:** Export formats add functionality to base data

### 6. Composite Pattern
Used to compose objects into tree structures:
- Individual objects and compositions share the same interface
- Clients treat individual objects and compositions uniformly

**Implementations:**
- **Game of Snakes:** `AdvancedSnakeGame` composes different game implementations
- **Cost Explorer:** `YearlyCostReport` composed of `MonthlyCost` objects
- **Middleware Router:** Trie data structure with composed nodes

## SOLID Principles Applied

### 1. Single Responsibility Principle
Each class has a well-defined responsibility:
- Classes focus on single aspects of functionality
- Clear separation of concerns
- High cohesion within classes

### 2. Open/Closed Principle
Designs are open for extension but closed for modification:
- New functionality added via inheritance or composition
- Base classes remain stable
- Extensibility built into the design

### 3. Liskov Substitution Principle
Subclasses can be used in place of their parent classes:
- Subclasses preserve the behavior contracts of parent classes
- Type substitution doesn't break functionality
- Consistent behavior across the inheritance hierarchy

### 4. Interface Segregation Principle
Interfaces are focused and minimal:
- Clients only depend on methods they use
- Small, cohesive interfaces
- No forced implementation of unused methods

### 5. Dependency Inversion Principle
High-level modules depend on abstractions:
- Concrete implementations depend on interfaces
- Reduced coupling between components
- Flexible integration of new implementations

## Advanced OOP Techniques

### 1. Composition Over Inheritance
Used to build complex functionality from simpler components:
- Favoring object composition over class inheritance
- Flexible runtime behavior configuration
- Reduced coupling between classes

### 2. Immutability
Used for thread-safety and simplified reasoning:
- Creating new objects rather than modifying existing ones
- Defensive copying of collections
- Predictable object behavior

### 3. Defensive Programming
Used to ensure robustness:
- Validation of inputs
- Null checks and default values
- Exception handling
- Defensive copying

### 4. Functional Programming Elements
Modern Java features employed:
- Lambda expressions for concise callbacks
- Stream API for declarative processing
- Method references for cleaner code

## Conclusion

The code design implementations demonstrate a strong understanding and application of Object-Oriented Programming principles and design patterns. Each solution uses appropriate patterns for its specific domain, resulting in code that is:

1. **Maintainable** - Easy to understand and modify
2. **Extensible** - Open for adding new features
3. **Reusable** - Components can be reused in different contexts
4. **Testable** - Well-encapsulated code facilitates testing
5. **Flexible** - Adaptable to changing requirements

These implementations serve as excellent examples of how to apply OOP principles and design patterns to solve real-world problems in a structured, maintainable way.