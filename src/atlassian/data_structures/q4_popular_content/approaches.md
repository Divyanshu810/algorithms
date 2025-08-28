# Popular Content Tracking - Approach Analysis

## Problem Summary
Track content popularity with increasePopularity/decreasePopularity operations and efficiently return the most popular content ID at any time. Return -1 if no content has popularity > 0.

## Approach 1: HashMap + Max Heap with Lazy Deletion
**Core Idea**: Use HashMap for O(1) popularity lookups and max heap to track the most popular content with lazy deletion of stale entries.

**Algorithm**:
1. HashMap<Integer, Integer> for contentId -> popularity mapping
2. PriorityQueue<Integer> (max heap) ordered by popularity
3. On increase: Update HashMap, add to heap
4. On decrease: Update HashMap, mark for lazy deletion
5. getMostPopular(): Peek heap, clean stale entries lazily

**Pros**:
- Fast popularity updates O(1) for HashMap operations
- Efficient max tracking with heap
- Handles frequent updates well
- Simple increase/decrease logic

**Cons**:
- Heap can accumulate stale entries
- Lazy deletion adds complexity to getMostPopular()
- Memory overhead for duplicate entries
- Worst case O(n) for getMostPopular() if many stale entries

**Time Complexity**: O(1) average for updates, O(log n) worst case for getMostPopular()
**Space Complexity**: O(n) with potential for extra heap entries

## Approach 2: HashMap + TreeMap for Count Tracking
**Core Idea**: Use HashMap for content->popularity and TreeMap for popularity->content sets to efficiently find maximum.

**Algorithm**:
1. HashMap<Integer, Integer> for contentId -> popularity
2. TreeMap<Integer, Set<Integer>> for popularity -> set of contentIds
3. On update: Move contentId between popularity buckets
4. getMostPopular(): Return any contentId from highest popularity bucket

**Pros**:
- Always accurate max popularity
- No stale entries or cleanup needed
- Efficient range queries possible
- Clean separation of concerns

**Cons**:
- More complex update logic (moving between buckets)
- TreeMap operations are O(log k) where k = unique popularity values
- Extra memory for TreeMap structure
- Set operations add overhead

**Time Complexity**: O(log k) for updates and getMostPopular()
**Space Complexity**: O(n + k) where k is unique popularity counts

## Approach 3: HashMap + Manual Max Tracking with Recomputation
**Core Idea**: Use HashMap for popularity storage and manually track max, recomputing when necessary.

**Algorithm**:
1. HashMap<Integer, Integer> for contentId -> popularity
2. Track maxPopularity and mostPopularContent variables
3. On increase: Update HashMap, check if new max
4. On decrease: Update HashMap, recompute max if current max was affected
5. getMostPopular(): Return cached most popular content

**Pros**:
- Simple and straightforward
- Minimal memory overhead
- O(1) getMostPopular() when max is cached
- Easy to understand and debug

**Cons**:
- O(n) recomputation when max content popularity decreases
- Poor performance for workloads with frequent decreases to max content
- Manual max tracking prone to bugs
- Not optimal for mixed increase/decrease patterns

**Time Complexity**: O(1) for most operations, O(n) worst case when recomputing max
**Space Complexity**: O(n)

## Recommended Approach: **Approach 2 (HashMap + TreeMap)**

**Rationale**:
- Provides consistent O(log k) performance for all operations
- No stale entries or cleanup complexity
- Scales well with varying popularity distributions
- Clean implementation that's easy to extend
- Handles the requirement to return -1 naturally (check if TreeMap is empty or max key ≤ 0)

**Trade-offs**:
- Slightly higher space complexity acceptable for consistency
- TreeMap operations are well-optimized in Java
- k (unique popularity values) typically much smaller than n (total content)

## Implementation Considerations

**For returning -1 when no content has popularity > 0**:
- Check if TreeMap is empty or highest key ≤ 0
- Remove contentIds when popularity drops to 0 or below

**For handling popularity going negative**:
- Either clamp at 0 or allow negative values based on requirements
- If allowing negative, ensure getMostPopular() only considers positive

**For performance optimization**:
- Use LinkedHashSet in TreeMap values for deterministic ordering
- Consider cleanup of empty popularity buckets

**For thread safety**:
- Add ReadWriteLock around all operations
- Or use ConcurrentHashMap with synchronized TreeMap operations