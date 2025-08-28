# Tennis Club Court Assignment - Approach Analysis

## Problem Summary
Assign tennis court bookings to minimize the number of courts needed, with potential maintenance requirements.

## Approach 1: Greedy Scheduling with Priority Queue
**Core Idea**: Use a min-heap to track when each court becomes available and assign bookings to the earliest available court.

**Algorithm**:
1. Sort bookings by start time
2. Use a priority queue (min-heap) to track court end times
3. For each booking:
   - If a court is available (end time ≤ start time), reuse it
   - Otherwise, allocate a new court
4. Update court end time after assignment

**Pros**:
- Optimal solution for minimum courts
- Simple and intuitive
- O(n log n) time complexity
- Easy to extend for maintenance requirements

**Cons**:
- Requires sorting preprocessing
- Priority queue operations add complexity
- May not handle complex constraints easily

**Time Complexity**: O(n log n) for sorting + O(n log k) for heap operations (k = number of courts)
**Space Complexity**: O(k) for the priority queue

## Approach 2: Interval Scheduling with Timeline Sweep
**Core Idea**: Create timeline events for booking starts/ends and sweep through to track concurrent bookings.

**Algorithm**:
1. Create events for each booking start (+1) and end (-1)
2. Sort events by time
3. Sweep through timeline tracking concurrent bookings
4. Maximum concurrent bookings = minimum courts needed
5. Assign courts by processing bookings in start time order

**Pros**:
- Clear separation of minimum calculation and assignment
- Easy to understand maximum concurrent bookings
- Good for analysis and reporting
- Handles overlapping bookings naturally

**Cons**:
- Two-pass algorithm (calculate then assign)
- More complex event processing
- Less efficient for large numbers of bookings

**Time Complexity**: O(n log n) for sorting events
**Space Complexity**: O(n) for events list

## Approach 3: Sorted Intervals with Binary Search
**Core Idea**: Maintain sorted list of court end times and use binary search to find earliest available court.

**Algorithm**:
1. Sort bookings by start time
2. Maintain list of court end times (sorted)
3. For each booking:
   - Binary search for earliest court that ends before booking starts
   - If found, update that court's end time
   - Otherwise, add new court
4. Keep list sorted after updates

**Pros**:
- Optimal court utilization
- Efficient binary search for court assignment
- Good for dynamic updates
- Clean separation of concerns

**Cons**:
- Complex to maintain sorted order during updates
- Binary search adds implementation complexity
- Insertion/update operations can be expensive

**Time Complexity**: O(n log n) for sorting + O(n log k) for binary search and updates
**Space Complexity**: O(k) for court end times

## Recommended Approach: **Approach 1 (Greedy Scheduling with Priority Queue)**

**Rationale**:
- Most straightforward to implement correctly
- Naturally handles the core scheduling problem
- Easy to extend for maintenance requirements
- Well-suited for interview time constraints
- Standard approach for interval scheduling problems

**Trade-offs**:
- Priority queue operations are standard and well-understood
- Clean separation between court availability and assignment logic
- Scales well with additional constraints (maintenance, durability)

## Scale-up Considerations

**For maintenance time after each booking**:
- Modify court end time to include maintenance: `endTime = booking.finishTime + maintenanceTime`
- No algorithm change needed

**For maintenance after X bookings**:
- Track usage count per court
- When court reaches durability limit, add maintenance time and reset counter
- Extend court object to include usage tracking

**For conflict checking**:
- Simple overlap detection: `booking1.finishTime > booking2.startTime && booking1.startTime < booking2.finishTime`

**For minimum courts only (simplified)**:
- Use timeline sweep to find maximum concurrent bookings
- Return count without assignment details