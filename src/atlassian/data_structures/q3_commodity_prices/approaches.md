# Commodity Prices Stream Processing - Approach Analysis

## Problem Summary
Process a stream of <timestamp, commodityPrice> data points to efficiently return the maximum commodity price at any point in time. Handle out-of-order timestamps, duplicates, and frequent reads/writes.

## Approach 1: TreeMap with Max Tracking
**Core Idea**: Use TreeMap for sorted timestamp storage with a separate variable tracking the current maximum.

**Algorithm**:
1. Use TreeMap<Long, Double> for timestamp->price mapping
2. Maintain maxPrice variable for O(1) reads
3. On upsert: Update TreeMap and recalculate max if needed
4. Handle out-of-order inserts by checking if new price affects global max

**Pros**:
- O(1) read time for getMaxPrice()
- O(log n) write time for upsert
- Handles out-of-order timestamps naturally
- Memory efficient for sparse timestamps

**Cons**:
- Complex max maintenance logic
- Need to recalculate max when removing max entry
- Extra complexity for duplicate timestamp handling

**Time Complexity**: O(1) reads, O(log n) writes
**Space Complexity**: O(n) for unique timestamps

## Approach 2: HashMap + Priority Queue (Max Heap)
**Core Idea**: Use HashMap for fast timestamp lookups and max heap for efficient maximum tracking.

**Algorithm**:
1. HashMap<Long, Double> for timestamp->price storage
2. PriorityQueue<Double> (max heap) for price tracking
3. On upsert: Update HashMap, add new price to heap
4. For duplicates: Remove old price from consideration
5. getMaxPrice() returns heap.peek()

**Pros**:
- O(1) read time for getMaxPrice()
- Fast timestamp-based lookups
- Natural handling of duplicates
- Simple maximum tracking

**Cons**:
- Heap doesn't support efficient arbitrary removal
- May accumulate stale entries in heap
- Potential memory bloat over time
- Complex cleanup needed

**Time Complexity**: O(1) reads, O(log n) writes
**Space Complexity**: O(n) with potential for extra heap entries

## Approach 3: Sorted Map with Lazy Recalculation
**Core Idea**: Use TreeMap and recalculate maximum only when necessary using lazy evaluation.

**Algorithm**:
1. TreeMap<Long, Double> for data storage
2. Cache maximum value with dirty flag
3. On upsert: Mark cache dirty if price could affect max
4. getMaxPrice(): Recalculate max if cache is dirty
5. Use Collections.max() on values when recalculation needed

**Pros**:
- Clean separation of concerns
- Optimal for read-heavy workloads
- No memory overhead for tracking
- Simple duplicate handling

**Cons**:
- Worst-case O(n) read time when cache dirty
- Frequent writes make cache mostly useless
- Less predictable performance
- Not suitable for write-heavy scenarios

**Time Complexity**: O(1) best case, O(n) worst case reads; O(log n) writes
**Space Complexity**: O(n)

## Recommended Approach: **Approach 1 (TreeMap with Max Tracking)**

**Rationale**:
- Best balance of read/write performance for frequent operations
- Guaranteed O(1) read performance as required
- TreeMap provides natural ordering for timestamp-based queries
- Handles all edge cases (out-of-order, duplicates) cleanly
- Predictable performance characteristics

**Trade-offs**:
- Slightly more complex max maintenance logic
- Well-suited for the stated requirement of "frequent reads and writes"
- TreeMap overhead acceptable for the ordering benefits

## Implementation Considerations

**For out-of-order timestamps**:
- TreeMap naturally handles insertion order vs temporal order
- Max tracking logic must consider all timestamps, not just latest

**For duplicate timestamps**:
- Simply overwrite existing entry in TreeMap
- Update max tracking if old max was overwritten

**For memory efficiency**:
- Consider cleanup strategies for very old data
- Implement time-based or size-based eviction if needed

**For O(1) getMaxPrice guarantee**:
- Always maintain current max value
- Use efficient max recalculation when max entry is updated/removed