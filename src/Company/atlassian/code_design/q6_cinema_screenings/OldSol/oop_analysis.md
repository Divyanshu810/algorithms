# OOP Principles and Design Patterns in Cinema Screenings Implementation

## OOP Principles

### 1. Encapsulation
- Private instance variables in all classes (title, startTime, endTime, etc.)
- Public methods to access functionality while hiding implementation details
- Accessor methods (getters) to safely access internal state
- Protected fields in `Cinema` class for controlled access by subclasses

### 2. Inheritance
- `ScreeningWithRevenue` extends `Screening` to add revenue tracking functionality
- `CinemaWithRevenue` extends `Cinema` to enhance with revenue management
- Child classes extend parent functionality while preserving existing behavior

### 3. Abstraction
- Each class models a specific concept:
  - `Screening` abstracts a movie showing with time slots
  - `Cinema` abstracts a movie theater with scheduling capabilities
  - `TimeSlot` abstracts a period of available time

### 4. Polymorphism
- `printSchedule` method is overridden in `CinemaWithRevenue` to display revenue information
- Method overloading for constructors with different parameter combinations
- Runtime polymorphism when handling different types of screenings

## Design Patterns

### 1. Template Method Pattern
- The `findAvailableTimeSlots` method in both `Cinema` and `MultiRoomCinema` follow the same template
- The core algorithm is the same, but MultiRoomCinema operates on a specific room's schedule
- The invariant parts are kept in the algorithm while varying parts are specialized

### 2. Strategy Pattern (Implicit)
- Different scheduling strategies:
  - Basic scheduling (first available slot)
  - Optimization based on room efficiency in `addScreeningOptimized`
  - Revenue-based optimization in `addToFullSchedule`

### 3. Decorator Pattern (Partial)
- `ScreeningWithRevenue` decorates the base `Screening` with additional revenue information
- Adds behavior without affecting the core functionality

### 4. Factory Method (Partial)
- Creation of different screening types is encapsulated in methods
- `scheduleAtBestSlot` manages the creation and initialization of scheduled screenings

## Additional OOP Concepts

### 1. Composition
- `Cinema` is composed of `Screening` objects
- `MultiRoomCinema` is composed of multiple screening lists

### 2. Method Overriding
- `printSchedule` is overridden in `CinemaWithRevenue` to add revenue information
- Extends functionality while preserving the method signature

### 3. Defensive Copying
- `getScreenings` returns a new ArrayList to prevent direct modification of the internal list

### 4. Single Responsibility Principle
- Each class has a well-defined responsibility:
  - `Screening`: Manage movie details and scheduling
  - `TimeSlot`: Represent time intervals
  - `Cinema`: Handle basic scheduling logic
  - `CinemaWithRevenue`: Handle revenue optimization
  - `MultiRoomCinema`: Manage multiple screening rooms

### 5. Open/Closed Principle
- The design is open for extension (adding new cinema types) but closed for modification
- New features (revenue tracking, multiple rooms) added without changing existing functionality

### 6. Liskov Substitution Principle
- `ScreeningWithRevenue` can be used anywhere a `Screening` is expected
- `CinemaWithRevenue` extends `Cinema` without breaking its core behavior

## Advanced OOP Features

### 1. Type Checking and Casting
- Runtime type checking with `instanceof` to safely handle different screening types
- Type casting to access extended functionality when needed

### 2. Algorithm Optimization
- Screenings are sorted by start time for efficient time slot finding
- Methods like `findLeastProfitableScreening` efficiently find optimal choices

### 3. Parameter Validation
- Input validation in `addScreening` method for room numbers
- Defensive programming to prevent invalid operations

## Summary
The Cinema Screenings implementation demonstrates a well-structured object-oriented design with clear separation of concerns. It effectively uses inheritance and composition to build a flexible, extensible system. The code follows SOLID principles, particularly the Single Responsibility and Open/Closed principles. The design elegantly handles increasingly complex requirements through specialized classes while maintaining a clean architecture.