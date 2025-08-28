# Cinema Screenings - Approaches

## Problem Statement
Let's pretend we are in charge of a cinema. We want to figure out whether a new movie can be added to the existing schedule without removing any of the current movies.

### Notes:
- The cinema opens at 10:00 (600 minutes after midnight)
- The last possible end time for a movie is 23:00 (1380 minutes after midnight)
- Movie durations include setting up the room before the movie begins and cleaning it up afterward
- Movie start times are expressed in minutes starting from midnight

### Scale Ups:
1. Add expected revenue per screening to each movie
2. Insert a new screening into a full schedule (decide which existing screening to remove)
3. Multiple Rooms: Cinema has more than one room (maximize earnings)

### Scale Downs:
- Given a schedule, simply print out movie start and end times

## Approach 1: Interval-Based Greedy Approach

### Description
Model each movie screening as an interval (start time, end time) and use interval scheduling algorithms to determine if a new movie can fit into the schedule.

#### Implementation Details
- Represent each movie as an interval with start and end times
- Sort existing movie screenings by start times
- Check for overlaps by iterating through sorted screenings
- Identify time gaps (if any) where a new movie could be scheduled
- Check if any gap is large enough to accommodate the new movie

### Pros
- Simple to implement and understand
- Efficient for the basic requirement (O(n log n) due to sorting)
- Easy to extend for the scale-down requirement (just print the intervals)
- Works well for small schedules

### Cons
- Does not inherently account for revenue optimization
- May require significant modification for multiple rooms
- For the "full schedule" scenario, requires additional logic to decide which screening to remove

## Approach 2: OOP with Time Slot Management

### Description
Use an object-oriented approach with dedicated classes for movies, screenings, and the cinema to manage the schedule more comprehensively.

#### Implementation Details
- Create a `Movie` class with properties like title, duration, and revenue
- Create a `Screening` class to represent a scheduled showing with start/end times
- Create a `Cinema` class to manage the schedule and provide operations
- Implement methods to add/remove screenings and check for availability
- For scale-ups, add revenue tracking and optimization logic

### Pros
- Better organized and more maintainable code
- More extensible for future requirements and scale-ups
- Clearer separation of concerns
- Easier to implement complex scheduling logic
- More representative of a real-world cinema management system

### Cons
- More complex initial implementation
- Slightly higher overhead for simple operations
- May use more memory due to object structure

## Approach 3: Time Slot Optimization with Dynamic Programming

### Description
Use dynamic programming to optimize revenue when deciding which screenings to include or remove from the schedule.

#### Implementation Details
- Represent the day as discrete time slots (e.g., 5-minute increments)
- For each potential screening, calculate the revenue per minute
- Use dynamic programming to find the maximum revenue schedule
- For the scale-up with multiple rooms, apply the algorithm for each room
- Implement room assignment optimization for the multi-room scenario

### Pros
- Optimal revenue generation for the cinema
- Handles complex constraints efficiently
- Excellent for the scale-up requiring revenue maximization
- Can elegantly handle the "full schedule" problem by finding the optimal screening to remove

### Cons
- Most complex implementation
- Higher computational complexity
- May be overkill for the base requirement
- More difficult to understand and maintain

## Chosen Approach: OOP with Time Slot Management

The Object-Oriented approach (Approach 2) provides the best balance between:
- Code organization and maintainability
- Extensibility for all scale-up requirements
- Reasonable performance characteristics
- Clear modeling of the problem domain

This approach allows us to:
1. Easily check if a new movie can fit in the existing schedule
2. Handle revenue optimization for scale-up 1
3. Determine which screening to remove for scale-up 2
4. Extend to multiple rooms for scale-up 3

The interval-based approach is simpler but less extensible, while the dynamic programming approach is more complex than needed for most scenarios. The OOP approach strikes the right balance of clarity, extensibility, and performance.