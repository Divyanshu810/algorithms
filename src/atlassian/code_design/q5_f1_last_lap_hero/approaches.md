# F1 Last Lap Hero - Approaches

## Problem Statement
Design and implement a program that accepts lap times for Formula 1 race drivers. Return the "Last Lap Hero" - the driver who had the biggest improvement on their last lap compared to their average lap time.

### Scale Ups
1. Implement a system to handle Pit Stop Laps where a pit stop causes a longer lap time. Pit Stop Laps should be excluded from the average lap time calculation, but are a valid last lap.
   Calculate the "Last Lap Hero" for both scenarios: including and excluding pit stops.
2. Introduce telemetry to track and report every time the "Last Lap Hero" changes during the race.

### Scale Down
3. Only return which driver had the fastest lap.
4. Accept lap times for only one driver. Stats to return:
   - The fastest lap time
   - The average lap time

## Approach 1: Simple Map-Based Approach

### Description
Use a map to store lap times for each driver, calculating averages and improvements on-demand.

#### Implementation Details
- Use a `Map<String, List<Double>>` to store driver names and their lap times.
- When querying for the "Last Lap Hero", calculate the average (excluding the last lap) for each driver and compare with their last lap.
- Return the driver with the biggest improvement.

### Pros
- Simple to implement and understand
- Minimal data structures and complexity
- Easy to add new lap times
- Works well for the base requirements

### Cons
- Recalculates averages each time we query for the "Last Lap Hero"
- Not optimized for the scale-up requirements, especially telemetry reporting
- No special handling for pit stops without additional logic
- Not extensible for future requirements without significant changes

## Approach 2: Object-Oriented Approach with Driver and Race Management

### Description
Create a structured OO design with classes to represent drivers, lap times, and race statistics.

#### Implementation Details
- Create a `Driver` class to encapsulate driver data, lap times, and statistics
- Create a `Race` class to manage the collection of drivers and provide race-wide statistics
- Implement methods to add lap times, mark pit stops, and calculate improvements
- Track and update statistics incrementally as new lap times are added
- Add observer pattern for telemetry reporting

### Pros
- Better organization and separation of concerns
- More maintainable and extensible for future requirements
- Easier to implement the pit stop handling and telemetry reporting
- Each driver maintains their own state, making it easier to track individual statistics
- More aligned with OOP principles and practices

### Cons
- More complex initial implementation
- Slightly higher memory usage due to object overhead
- Requires careful design to ensure efficient operations

## Approach 3: Real-Time Statistics Tracking with Events

### Description
A more advanced approach focused on real-time statistics and event-based updates.

#### Implementation Details
- Maintain running statistics (sums, counts, min/max) for each driver to avoid recalculation
- Implement an event system to notify subscribers when the "Last Lap Hero" changes
- Use a priority queue or sorted structure to efficiently track drivers by improvement
- Pre-compute and update statistics incrementally as new lap times are added

### Pros
- Highest performance for the telemetry requirement
- Most efficient for large datasets and real-time updates
- Minimal recalculation needed when adding new lap times
- Scales well with increasing number of drivers and laps
- Perfect for the telemetry reporting requirement

### Cons
- Most complex implementation
- Higher initial development time
- May be over-engineered for the base requirements
- Requires careful handling of edge cases and statistics updates

## Chosen Approach: Object-Oriented Approach with Driver and Race Management

The Object-Oriented approach (Approach 2) provides the best balance between:
- Code organization and maintainability
- Extensibility for the scale-up requirements
- Reasonable performance characteristics
- Alignment with software engineering best practices

This approach allows for clean implementation of all the requirements, including pit stop handling and telemetry reporting, while maintaining a clear and maintainable codebase. It strikes a good balance between simplicity and extensibility.