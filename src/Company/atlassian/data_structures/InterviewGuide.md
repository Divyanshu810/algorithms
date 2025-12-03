# Interview Problems - Comprehensive Guide

A detailed explanation of common interview problems with multiple approaches, step-by-step walkthroughs, complexity analysis, and trade-offs.

---

## Table of Contents

1. [Infection Sequences Count](#1-infection-sequences-count)
2. [Closest Common Group for Employees](#2-closest-common-group-for-employees)
3. [Tennis Court Booking](#3-tennis-court-booking)
4. [Commodity Prices - Max Price Tracker](#4-commodity-prices---max-price-tracker)
5. [Popular Content - Most Popular Tracker](#5-popular-content---most-popular-tracker)
6. [Weighted Graph - Shortest Path](#6-weighted-graph---shortest-path)
7. [Job Interval Reporting](#7-job-interval-reporting)
8. [File Collection Reporting](#8-file-collection-reporting)
9. [Common Patterns Summary](#9-common-patterns-summary)

---

## 1. Infection Sequences Count

### Problem Statement
Given n houses in a line and a list of initially infected houses, the virus spreads to adjacent houses daily. Count the number of **distinct infection sequences** possible.

**Key Insight:** Houses infected on the same day can be ordered arbitrarily → answer = product of factorials of daily infection counts.

**Input:** `n=5, infected=[1,3]` (0-indexed)  
**Output:** Number of distinct sequences

### Visual Example
```
Houses:    0   1   2   3   4
Initial:   _   X   _   X   _
           
Day 0: Houses 1,3 infected (given)
Day 1: Houses 0,2,4 can be infected
       - House 0 from House 1
       - House 2 from House 1 OR House 3
       - House 4 from House 3

Segments: [0] [2] [4]
          left  middle  right
```

---

### Approach 1: BFS + Factorial

**Idea:** Multi-source BFS from infected houses, count houses at each level (day), multiply factorials.

**Algorithm:**
```
1. Initialize queue with all initially infected houses (distance = 0)
2. BFS to find distance of each house from nearest infected
3. Group houses by distance (day)
4. Answer = factorial(day1_count) × factorial(day2_count) × ...
```

**Step-by-Step Example:**
```
n=7, infected=[2,5]

Houses:    0   1   2   3   4   5   6
Initial:   _   _   X   _   _   X   _

BFS from sources [2,5]:
  Queue: [(2,0), (5,0)]
  
  Process (2,0): neighbors 1,3 get distance 1
  Process (5,0): neighbors 4,6 get distance 1
  Queue: [(1,1), (3,1), (4,1), (6,1)]
  
  Process (1,1): neighbor 0 gets distance 2
  Process (3,1): neighbor 4 already visited
  Process (4,1): neighbor 3 already visited
  Process (6,1): no new neighbors
  Queue: [(0,2)]

Distance array: [2, 1, 0, 1, 1, 0, 1]

Group by distance (day):
  Day 0: houses 2,5 (initially infected, don't count)
  Day 1: houses 1,3,4,6 → count = 4
  Day 2: house 0 → count = 1

Answer = 4! × 1! = 24 × 1 = 24
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(n) |
| Space | O(n) |

---

### Approach 2: Segment Analysis (Optimal)

**Idea:** Directly analyze segments between infected houses without iterating all houses.

**Three Segment Types:**
```
infected = [2, 5] for n = 8

Houses:  0  1  2  3  4  5  6  7
         [LEFT] X [MID] X [RIGHT]
         
LEFT segment:   houses 0,1 (before first infected)
MIDDLE segment: houses 3,4 (between infected)
RIGHT segment:  houses 6,7 (after last infected)
```

**Key Insight for Middle Segments:**
- Houses can be infected from EITHER side
- This creates more ordering possibilities
- Use combination formula: C(left + right, left)

**Algorithm:**
```
1. Sort infected houses
2. Process LEFT segment: houses before first infected
   - Only one direction, contributes 1 to each day
3. Process MIDDLE segments: between consecutive infected
   - Two directions possible, use combinations
4. Process RIGHT segment: houses after last infected
   - Only one direction, contributes 1 to each day
5. Multiply factorials of daily counts
```

**Step-by-Step (Middle Segment):**
```
Houses 3,4 between infected 2 and 5:

Day 1: Either house 3 (from 2) OR house 4 (from 5)
       2 choices → contributes 2 to day 1 count

Day 2: The remaining house
       1 choice → contributes 1 to day 2 count

If segment has length L:
  Day 1: 2 choices (from either end)
  Day 2: 2 choices
  ...until middle is reached

For segment length 2: C(2,1) = 2 ways to interleave
For segment length 4: C(4,2) = 6 ways to interleave
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(m log m) where m = number of infected |
| Space | O(m) |

---

### Trade-offs

| Approach | Time | Space | Best When |
|----------|------|-------|-----------|
| **BFS + Factorial** | O(n) | O(n) | Small n, simple implementation |
| **Segment Analysis** | O(m log m) | O(m) | Large n, few infected houses |

---

## 2. Closest Common Group for Employees

### Problem Statement
Find the closest (lowest) common parent group for a set of target employees in an organizational hierarchy.

**Variations:**
- Part A: Tree structure (each group has one parent)
- Part B: DAG structure (groups can have multiple parents)
- Part C: Concurrent access with ReadWriteLock
- Part D: Single level (flat structure)

---

### Part A: Tree Structure

**Visual Example:**
```
           Company (depth 0)
          /       \
      Eng (1)    Sales (1)
      /   \         \
   FE(2)  BE(2)   APAC(2)
   /  \     |
Alice Bob  Charlie

Find common group for [Alice, Bob]: Answer = FE (depth 2)
Find common group for [Alice, Charlie]: Answer = Eng (depth 1)
```

**Algorithm:**
```
1. For each employee, collect all ancestor groups with depths
2. Find intersection of all ancestor sets
3. Return group with MINIMUM depth (closest to employees)
```

**Step-by-Step Example:**
```
Target employees: [Alice, Charlie]

Alice's ancestors:
  FE (depth 2), Eng (depth 1), Company (depth 0)

Charlie's ancestors:
  BE (depth 2), Eng (depth 1), Company (depth 0)

Intersection: {Eng, Company}

Depths: Eng=1, Company=0

MINIMUM depth in intersection = Eng (depth 1) ← NOT Company!

Answer: Eng
```

**⚠️ Common Bug:** Using MAX depth instead of MIN depth. We want the group CLOSEST to employees, which has the HIGHEST depth number but we track from root, so it's the MINIMUM in our intersection when we find the deepest common ancestor.

Wait, let me clarify:

```
Depth from ROOT:
  Company: depth 0 (root)
  Eng: depth 1
  FE: depth 2

We want the DEEPEST common ancestor = HIGHEST depth = Eng (depth 1)
NOT Company (depth 0)

So we find MAX depth in the intersection!
```

**Corrected Algorithm:**
```
Return group with MAXIMUM depth (deepest = closest to employees)
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(E × H) where E = employees, H = height |
| Space | O(H) |

---

### Part B: DAG Structure (Multiple Parents)

**Visual Example:**
```
              Company
             /       \
         Eng         Sales
        /   \       /    \
      FE    BE   APAC   EMEA
       \   /  \   /
       Platform  ← shared group (multiple parents!)
          |
        Alice

Alice belongs to Platform
Platform's parents: FE, BE, APAC
```

**Algorithm (BFS Upward):**
```
1. Start from employee's direct groups
2. BFS upward, tracking visit count for each group
3. First group visited by ALL employees = answer
```

**Step-by-Step:**
```
Employees: [Alice, Bob]
Alice in: Platform
Bob in: FE

BFS from Alice (Platform):
  Visit Platform (count: 1)
  Visit FE, BE, APAC (count: 1 each)
  Visit Eng, Sales (count: 1 each)
  Visit Company (count: 1)

BFS from Bob (FE):
  FE already visited → count: 2 ← First with count = numEmployees!
  
Answer: FE
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(E × G) where G = total groups |
| Space | O(G) |

---

### Part C: Concurrent Access

**Use ReadWriteLock:**
```java
ReadWriteLock lock = new ReentrantReadWriteLock();

// Write operations (exclusive access)
void addGroup() {
    lock.writeLock().lock();
    try { /* modify */ }
    finally { lock.writeLock().unlock(); }
}

// Read operations (shared access)
Group getCommonGroup() {
    lock.readLock().lock();
    try { /* read only */ }
    finally { lock.readLock().unlock(); }
}
```

**Behavior:**
| Operation | Concurrent Reads | Concurrent Writes |
|-----------|------------------|-------------------|
| Read | ✅ Allowed | ❌ Blocked |
| Write | ❌ Blocked | ❌ Blocked |

---

### Part D: Single Level (Flat Structure)

**No hierarchy, just group membership.**

```
Groups: {G1: [Alice, Bob], G2: [Bob, Charlie], G3: [Alice]}

Find common for [Alice, Bob]: 
  Alice in: G1, G3
  Bob in: G1, G2
  Intersection: G1
  
Answer: G1
```

**Algorithm:**
```
1. Get groups for first employee
2. Intersect with groups of each subsequent employee
3. Return any remaining group (or null if empty)
```

**Complexity:** O(E × G)

---

### Summary Table

| Part | Structure | Algorithm | Time |
|------|-----------|-----------|------|
| A | Tree | Collect ancestors + intersect | O(E × H) |
| B | DAG | BFS upward + visit count | O(E × G) |
| C | Tree + Concurrent | ReadWriteLock | Same + lock overhead |
| D | Flat | Set intersection | O(E × G) |

---

## 3. Tennis Court Booking

### Problem Statement
Given a list of tennis court bookings with start and finish times, assign each booking to a specific court using minimum number of courts.

**Input:** `[{0,5}, {2,7}, {6,10}, {8,12}]`  
**Output:** 2 courts needed

### Visual Example
```
Timeline:
Court 1: |--B1--|     |--B3--|
Court 2:    |--B2--|     |--B4--|
         0  2     5  6  7  8   10  12
```

---

### Approach 1: Greedy with Min-Heap

**Idea:** Always assign booking to the court that becomes free earliest.

**Data Structures:**
- List of courts
- Min-heap tracking `(availableTime, courtIndex)`

**Algorithm:**
```
1. Sort bookings by start time
2. For each booking:
   a. If earliest court is free (heap.peek().time <= booking.start):
      - Reuse that court
      - Update its available time in heap
   b. Else:
      - Create new court
      - Add to heap
3. Return courts
```

**Step-by-Step Example:**
```
Bookings (sorted): [{0,5}, {2,7}, {6,10}, {8,12}]

Process {0,5}:
  Heap empty → Create Court 1
  Heap: [(5, Court1)]

Process {2,7}:
  Heap.peek() = 5 > 2 → Court 1 not free
  Create Court 2
  Heap: [(5, Court1), (7, Court2)]

Process {6,10}:
  Heap.peek() = 5 <= 6 → Court 1 is free!
  Reuse Court 1
  Heap: [(7, Court2), (10, Court1)]

Process {8,12}:
  Heap.peek() = 7 <= 8 → Court 2 is free!
  Reuse Court 2
  Heap: [(10, Court1), (12, Court2)]

Result: 2 courts
```

**Complexity:**
| Operation | Time |
|-----------|------|
| Sort | O(n log n) |
| Heap operations | O(n log n) |
| **Total** | **O(n log n)** |
| Space | O(n) |

---

### Approach 2: Sweep Line (Count Only)

**Idea:** Track concurrent bookings using events.

**Algorithm:**
```
1. Create events: (time, +1) for start, (time, -1) for end
2. Sort events by time (ends before starts at same time)
3. Sweep through, track max concurrent count
```

**Step-by-Step Example:**
```
Bookings: [{0,5}, {2,7}, {6,10}, {8,12}]

Events: (0,+1), (2,+1), (5,-1), (6,+1), (7,-1), (8,+1), (10,-1), (12,-1)

Sweep:
  t=0:  count = 0+1 = 1, max = 1
  t=2:  count = 1+1 = 2, max = 2  ← peak!
  t=5:  count = 2-1 = 1
  t=6:  count = 1+1 = 2, max = 2
  t=7:  count = 2-1 = 1
  t=8:  count = 1+1 = 2, max = 2
  t=10: count = 2-1 = 1
  t=12: count = 1-1 = 0

Max concurrent = 2 courts needed
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(n log n) |
| Space | O(n) |

---

### Approach 3: Difference Array (No Sorting)

**Idea:** Use array indices as time, avoid sorting.

**Algorithm:**
```
1. Find minTime and maxTime
2. Create diff[] array of size (maxTime - minTime + 1)
3. For each booking: diff[start - minTime]++, diff[end - minTime]--
4. Sweep array to find max
```

**Step-by-Step Example:**
```
Bookings: [{0,5}, {2,7}, {6,10}]
minTime=0, maxTime=10

Build diff array:
  diff[0] += 1, diff[5] -= 1   → [+1, 0, 0, 0, 0, -1, 0, 0, 0, 0, 0]
  diff[2] += 1, diff[7] -= 1   → [+1, 0, +1, 0, 0, -1, 0, -1, 0, 0, 0]
  diff[6] += 1, diff[10] -= 1  → [+1, 0, +1, 0, 0, -1, +1, -1, 0, 0, -1]

Sweep:
  Index: 0   1   2   3   4   5   6   7   8   9   10
  diff: +1   0  +1   0   0  -1  +1  -1   0   0  -1
  count: 1   1   2   2   2   1   2   1   1   1   0
                 ^           ^
               max=2       max=2

Result: 2 courts needed
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O(n + T) where T = time range |
| Space | O(T) |

---

### Trade-offs Comparison

| Approach | Time | Space | Best When |
|----------|------|-------|-----------|
| **Min-Heap** | O(n log n) | O(n) | Need actual assignments |
| **Sweep Line** | O(n log n) | O(n) | Just need count, large time values |
| **Diff Array** | O(n + T) | O(T) | Small time range (T < 10^6) |

---

## 4. Commodity Prices - Max Price Tracker

### Problem Statement
Given a stream of `(timestamp, price)` data points:
- Timestamps can be duplicate → upsert (update if exists)
- Return max commodity price at any time

---

### Approach 1: Brute Force (HashMap)

**Data Structure:** `HashMap<timestamp, price>`

**Operations:**
```
upsert(ts, price): map.put(ts, price)           → O(1)
getMax():          scan all values for max      → O(n)
```

**Trade-off:** Fast writes, slow reads.

---

### Approach 2: Cached Max Variable

**Idea:** Keep a variable tracking current max, only recalculate when necessary.

**Data Structures:**
```java
Map<Long, Double> timestampToPrice;
Double cachedMaxPrice;
```

**Algorithm:**
```
upsert(timestamp, newPrice):
  1. Get oldPrice = map.get(timestamp)
  2. map.put(timestamp, newPrice)
  3. If newPrice > cachedMax:
       cachedMax = newPrice  ← O(1)
  4. Else if oldPrice was cachedMax AND newPrice < cachedMax:
       recalculateMax()      ← O(n) but rare!

getMax():
  return cachedMax           ← O(1) always!
```

**Step-by-Step Example:**
```
upsert(100, 50): map={100:50}, cachedMax=50
upsert(200, 75): map={100:50, 200:75}, cachedMax=75 (new max!)
upsert(150, 60): map={100:50, 200:75, 150:60}, cachedMax=75 (unchanged)
upsert(200, 40): map={100:50, 200:40, 150:60}
                 oldPrice=75 was max, newPrice=40 < max
                 → recalculateMax() → cachedMax=60

getMax() → 60 (O(1))
```

**When is O(n) Recalculation Triggered?**
| Operation | Recalculation? |
|-----------|----------------|
| Insert new max | No |
| Insert non-max | No |
| Update non-max timestamp | No |
| **Update max timestamp to lower value** | **Yes** |
| Remove non-max timestamp | No |
| **Remove max timestamp** | **Yes** |

**Complexity:**
| Operation | Time |
|-----------|------|
| upsert | O(1) average, O(n) worst case |
| getMax | **O(1) always** |
| Space | O(n) |

---

## 5. Popular Content - Most Popular Tracker

### Problem Statement
Stream of `(contentId, action)` where action is `increasePopularity` or `decreasePopularity`.
- Return most popular contentId (highest popularity > 0)
- Return -1 if all popularity ≤ 0

**Key Difference from Commodity Prices:**
- Multiple contentIds can have the SAME popularity
- Return contentId, not the popularity value

---

### Approach 1: Brute Force

```
increase/decrease: O(1)
getMostPopular:    O(n) - scan all entries
```

---

### Approach 2: HashMap + TreeMap

**Idea:** Track contentIds grouped by popularity.

**Data Structures:**
```java
Map<Integer, Integer> contentToPopularity;           // contentId → popularity
TreeMap<Integer, Set<Integer>> popularityToContents; // popularity → set of contentIds
```

**Visual Example:**
```
After: increase(101), increase(102), increase(102), increase(103)

contentToPopularity: {101: 1, 102: 2, 103: 1}
popularityToContents: {1: [101, 103], 2: [102]}
                                      ↑
                                    TreeMap.lastKey() = 2
                                    getMostPopular() = 102
```

**Algorithm:**
```
increase(contentId):
  1. Get oldPopularity
  2. Remove contentId from popularityToContents[oldPopularity]
  3. newPopularity = oldPopularity + 1
  4. Add contentId to popularityToContents[newPopularity]
  5. Update contentToPopularity

getMostPopular():
  1. highest = popularityToContents.lastEntry()
  2. If highest.key <= 0: return -1
  3. Return any contentId from highest.value
```

**Complexity:**
| Operation | Time |
|-----------|------|
| increase/decrease | O(log n) |
| getMostPopular | **O(1)** |

---

### Approach 3: Cached Max (Simpler)

**Data Structures:**
```java
Map<Integer, Integer> contentToPopularity;
int cachedMaxPopularity;
int cachedMostPopularId;
```

**Algorithm:**
```
increase(contentId):
  newPopularity = map.get(contentId) + 1
  map.put(contentId, newPopularity)
  
  If newPopularity > cachedMax:
    cachedMax = newPopularity
    cachedMostPopularId = contentId

decrease(contentId):
  // Update map...
  
  If contentId == cachedMostPopularId:
    recalculateMax()  // O(n) but only when decreasing THE max content

getMostPopular():
  return cachedMaxPopularity <= 0 ? -1 : cachedMostPopularId  // O(1)
```

**Complexity:**
| Operation | Time |
|-----------|------|
| increase | O(1) |
| decrease | O(1) avg, O(n) worst |
| getMostPopular | **O(1)** |

---

### Trade-offs

| Approach | increase | decrease | getMostPopular | Best When |
|----------|----------|----------|----------------|-----------|
| **Brute Force** | O(1) | O(1) | O(n) | Write-heavy |
| **TreeMap** | O(log n) | O(log n) | O(1) | Balanced workload |
| **Cached Max** | O(1) | O(1)/O(n) | O(1) | Read-heavy, rare decrease of max |

---

## 6. Weighted Graph - Shortest Path

### Problem Statement
Network with N labeled nodes and directed edges with weights (time in ms).
- Can packet reach from source to destination?
- If yes, what's the shortest time?

---

### Approach 1: BFS (Reachability Only)

**Use when:** Only need to check if path exists (ignoring weights).

```
Time:  O(V + E)
Space: O(V)
```

---

### Approach 2: Dijkstra's Algorithm

**Idea:** Greedy BFS with priority queue, always expand shortest distance node first.

**Data Structures:**
```java
Map<String, Integer> distance;      // node → shortest distance from source
Map<String, String> parent;         // for path reconstruction
PriorityQueue<NodeDistance> minHeap; // (node, distance)
Set<String> visited;
```

**Algorithm:**
```
1. Initialize all distances to ∞, source to 0
2. Add source to minHeap with distance 0
3. While heap not empty:
   a. Pop node with smallest distance
   b. If already visited, skip
   c. Mark visited
   d. If destination found, reconstruct path and return
   e. For each neighbor:
      - Calculate newDist = currentDist + edgeWeight
      - If newDist < distance[neighbor]:
        - Update distance[neighbor]
        - Update parent[neighbor]
        - Add to heap
4. Return not reachable
```

**Step-by-Step Example:**
```
Graph:
    A --1--> B --6--> D --2--> E
    |        ^        ^
    +--3---> C --4----+

Find shortest path A → E

Initialize:
  distance = {A:0, B:∞, C:∞, D:∞, E:∞}
  heap = [(A, 0)]

Pop (A, 0):
  Visit A
  Neighbors: B (dist=1), C (dist=3)
  Update: distance={A:0, B:1, C:3, D:∞, E:∞}
  heap = [(B,1), (C,3)]

Pop (B, 1):
  Visit B
  Neighbors: D (dist=1+6=7)
  Update: distance={A:0, B:1, C:3, D:7, E:∞}
  heap = [(C,3), (D,7)]

Pop (C, 3):
  Visit C
  Neighbors: B (already visited), D (dist=3+4=7, not better)
  heap = [(D,7)]

Pop (D, 7):
  Visit D
  Neighbors: E (dist=7+2=9)
  Update: distance={A:0, B:1, C:3, D:7, E:9}
  heap = [(E,9)]

Pop (E, 9):
  Destination found!
  Path: A → B → D → E
  Distance: 9
```

**Complexity:**
| Metric | Value |
|--------|-------|
| Time | O((V + E) log V) |
| Space | O(V) |

---

### Scale Up: Add One Bridge

**Problem:** Given potential edges (not yet established), find which ONE edge to add to minimize path.

**Algorithm (Brute Force):**
```
1. Get current shortest path (may be unreachable)
2. For each potential edge:
   a. Temporarily add edge
   b. Run Dijkstra
   c. If better than current best, update
   d. Remove edge
3. Return best result
```

**Complexity:** O(P × (V + E) log V) where P = potential edges

---

## 7. Job Interval Reporting

### Problem Statement
Given CI pipeline intervals `{start, end}`:
- Part 1: Find windows where at least ONE pipeline runs (merge intervals)
- Part 2: Find windows where at least N pipelines run
- Part 3: Find busiest window (maximum concurrent pipelines)

---

### Part 1: Merge Intervals

**Input:** `[{2,5}, {12,15}, {4,8}]`  
**Output:** `[{2,8}, {12,15}]`

**Algorithm:**
```
1. Sort by start time
2. For each interval:
   If overlaps with current (next.start <= current.end):
     Extend current.end = max(current.end, next.end)
   Else:
     Add current to result
     current = next
3. Add last interval
```

**Step-by-Step:**
```
Sorted: [{2,5}, {4,8}, {12,15}]

current = {2,5}

Process {4,8}:
  4 <= 5? Yes, overlaps!
  Extend: current = {2, max(5,8)} = {2,8}

Process {12,15}:
  12 <= 8? No, no overlap
  Add {2,8} to result
  current = {12,15}

Add {12,15} to result

Result: [{2,8}, {12,15}]
```

**Complexity:** O(n log n)

---

### Part 2 & 3: Sweep Line Approaches

#### Approach A: Event-Based (with Sorting)

```
1. Create events: (time, +1) for start, (time, -1) for end
2. Sort by time
3. Sweep and track count
```

#### Approach B: Difference Array (No Sorting)

```
1. Find minTime, maxTime
2. Create diff[maxTime - minTime + 1]
3. For each interval:
     diff[start - minTime] += 1
     diff[end - minTime] -= 1
4. Sweep diff array
```

**Step-by-Step (Finding windows with count >= 2):**
```
Intervals: [{1,5}, {2,6}, {8,10}]
minTime=1, maxTime=10

Build diff (offset by minTime=1):
  Index:  0   1   2   3   4   5   6   7   8   9
  Time:   1   2   3   4   5   6   7   8   9  10
  
  {1,5}: diff[0]++, diff[4]--  → [+1, 0, 0, 0, -1, 0, 0, 0, 0, 0]
  {2,6}: diff[1]++, diff[5]--  → [+1,+1, 0, 0, -1,-1, 0, 0, 0, 0]
  {8,10}: diff[7]++, diff[9]-- → [+1,+1, 0, 0, -1,-1, 0,+1, 0,-1]

Sweep (looking for count >= 2):
  i=0 (t=1): prev=0, count=1       (entered 1, not >= 2)
  i=1 (t=2): prev=1, count=2       (entered 2!) → windowStart=2
  i=2 (t=3): prev=2, count=2       (still >= 2)
  i=3 (t=4): prev=2, count=2       (still >= 2)
  i=4 (t=5): prev=2, count=1       (dropped below 2!) → add {2,5}
  i=5 (t=6): prev=1, count=0
  i=7 (t=8): prev=0, count=1
  i=9 (t=10): prev=1, count=0

Result: [{2,5}]
```

**Why subtract minTime?**
```
Converts actual time → array index (starting from 0)

Without: times 100-110 would need array[111] (wastes 0-99)
With:    times 100-110 only needs array[11]

Formula:
  Time → Index: index = time - minTime
  Index → Time: time = index + minTime
```

---

### Complexity Comparison

| Approach | Time | Space | Best When |
|----------|------|-------|-----------|
| **Sweep Line (Sort)** | O(n log n) | O(n) | Large time values |
| **Difference Array** | O(n + T) | O(T) | Small time range (T < 10^6) |

---

## 8. File Collection Reporting

### Problem Statement
Files have sizes and belong to collection(s). Generate reports:
1. Total size of all files
2. Top N collections by total file size

**Variations:**
- Part A: Single collection per file
- Part B: Multiple collections per file (many-to-many)
- Part C: Thread-safe with ReadWriteLock

---

### Part A: Single Collection Per File

**Data Structures:**
```java
Map<String, File> files;                    // fileId -> File
Map<String, String> fileToCollection;       // fileId -> collectionId
Map<String, Set<String>> collectionToFiles; // collectionId -> set of fileIds
```

**Visual Example:**
```
Files:
  file1 (100 bytes) → photos
  file2 (200 bytes) → photos
  file3 (150 bytes) → videos
  file4 (300 bytes) → videos
  file5 (50 bytes)  → docs

Total size: 100 + 200 + 150 + 300 + 50 = 800 bytes

Collection sizes:
  photos: 300 bytes
  videos: 450 bytes  ← Top 1
  docs: 50 bytes
```

**Algorithm for Top N (Min-Heap):**
```
1. Calculate size for each collection
2. Use min-heap of size N
3. For each collection:
   - Add to heap
   - If heap.size > N: remove smallest
4. Extract and reverse for descending order
```

**Step-by-Step (Top 2):**
```
Collection sizes: {photos: 300, videos: 450, docs: 50}

Process photos (300):
  Heap: [(photos, 300)]

Process videos (450):
  Heap: [(photos, 300), (videos, 450)]

Process docs (50):
  Heap: [(docs, 50), (photos, 300), (videos, 450)]
  Size > 2, remove min (docs, 50)
  Heap: [(photos, 300), (videos, 450)]

Extract & reverse: [(videos, 450), (photos, 300)]
```

**Complexity:**
| Operation | Time |
|-----------|------|
| addFile | O(1) |
| removeFile | O(1) |
| getTotalSize | O(n) |
| getTopNCollections | O(c log c) |

---

### Part B: Multiple Collections Per File

**Key Change:** File can belong to MULTIPLE collections.

**Important:** Don't double-count files in total size!

```
file1 (100 bytes) → photos, favorites
file2 (200 bytes) → photos
file3 (150 bytes) → videos, favorites

Total size = 100 + 200 + 150 = 450 (NOT 100+200 + 150 + 100+150)

Collection sizes (CAN exceed total):
  photos: 300 (file1 + file2)
  favorites: 250 (file1 + file3)
  videos: 150 (file3)
  
Sum of collection sizes: 700 > Total: 450 (due to shared files)
```

**Data Structure Change:**
```java
Map<String, Set<String>> fileToCollections;  // fileId -> SET of collectionIds
```

---

### Part C: Thread-Safe with ReadWriteLock

**Read Operations (shared lock):**
- getTotalSize()
- getTopNCollections()
- getCollectionSize()

**Write Operations (exclusive lock):**
- addFile()
- removeFile()
- addFileToCollection()
- removeFileFromCollection()

**Template:**
```java
private final ReadWriteLock lock = new ReentrantReadWriteLock();
private final Lock readLock = lock.readLock();
private final Lock writeLock = lock.writeLock();

// Read operation
public long getTotalSize() {
    readLock.lock();
    try {
        // read logic
    } finally {
        readLock.unlock();
    }
}

// Write operation
public void addFile(String fileId, long size) {
    writeLock.lock();
    try {
        // write logic
    } finally {
        writeLock.unlock();
    }
}
```

**Behavior:**
| Current | Read Request | Write Request |
|---------|--------------|---------------|
| Idle | ✅ Granted | ✅ Granted |
| Reading | ✅ Granted | ⏳ Waits |
| Writing | ⏳ Waits | ⏳ Waits |

---

### Summary Table

| Part | Feature | getTotalSize | getTopN |
|------|---------|--------------|---------|
| A | Single collection | O(n) | O(c log c) |
| B | Multi collection | O(n) | O(c × f) |
| C | Thread-safe | O(n) + lock | O(c × f) + lock |

---

## 9. Common Patterns Summary

### Pattern 1: Sweep Line / Line Sweep

**Use for:** Interval problems, counting overlaps, finding windows

**Two Variants:**
| Variant | Time | Space | When to Use |
|---------|------|-------|-------------|
| Event-based + Sort | O(n log n) | O(n) | Sparse times, large values |
| Difference Array | O(n + T) | O(T) | Dense times, small range |

---

### Pattern 2: Greedy + Heap

**Use for:** Scheduling, resource allocation, always picking "best" option

**Template:**
```java
PriorityQueue<State> heap = new PriorityQueue<>((a,b) -> a.key - b.key);

for (item : sortedItems) {
    if (!heap.isEmpty() && heap.peek().key <= item.start) {
        // Reuse existing resource
        State reused = heap.poll();
        // Update and re-add
        heap.offer(new State(newKey, reused.id));
    } else {
        // Need new resource
        heap.offer(new State(item.end, newId));
    }
}
```

---

### Pattern 3: Cached Max/Min

**Use for:** Frequent getMax/getMin queries with occasional updates

**Key Insight:**
- Track max in a variable for O(1) reads
- Only recalculate when max is invalidated (rare)

**Template:**
```java
void update(key, value) {
    oldValue = map.get(key);
    map.put(key, value);
    
    if (value > cachedMax) {
        cachedMax = value;  // O(1)
    } else if (oldValue == cachedMax && value < cachedMax) {
        recalculateMax();   // O(n) but rare
    }
}

Value getMax() {
    return cachedMax;  // O(1) always
}
```

---

### Pattern 4: Dijkstra's Algorithm

**Use for:** Shortest path in weighted graphs (non-negative weights)

**Template:**
```java
PriorityQueue<NodeDist> heap = new PriorityQueue<>((a,b) -> a.dist - b.dist);
Map<Node, Integer> distance = new HashMap<>();
Set<Node> visited = new HashSet<>();

heap.offer(new NodeDist(source, 0));
distance.put(source, 0);

while (!heap.isEmpty()) {
    NodeDist curr = heap.poll();
    if (visited.contains(curr.node)) continue;
    visited.add(curr.node);
    
    if (curr.node == destination) return curr.dist;
    
    for (Edge edge : neighbors(curr.node)) {
        int newDist = curr.dist + edge.weight;
        if (newDist < distance.getOrDefault(edge.dest, INF)) {
            distance.put(edge.dest, newDist);
            heap.offer(new NodeDist(edge.dest, newDist));
        }
    }
}
```

---

### Pattern 5: Bucket/Grouping with TreeMap

**Use for:** Tracking items grouped by a sortable key, need min/max group

**Template:**
```java
Map<Item, Key> itemToKey;
TreeMap<Key, Set<Item>> keyToItems;

void update(item, newKey) {
    // Remove from old bucket
    Key oldKey = itemToKey.get(item);
    keyToItems.get(oldKey).remove(item);
    if (keyToItems.get(oldKey).isEmpty()) {
        keyToItems.remove(oldKey);
    }
    
    // Add to new bucket
    itemToKey.put(item, newKey);
    keyToItems.computeIfAbsent(newKey, k -> new HashSet<>()).add(item);
}

Key getMaxKey() {
    return keyToItems.lastKey();  // O(log n)
}
```

---

### Pattern 6: Multi-Source BFS

**Use for:** Finding distances from multiple starting points, infection spread, nearest source

**Template:**
```java
Queue<int[]> queue = new LinkedList<>();
int[] distance = new int[n];
Arrays.fill(distance, -1);

// Add ALL sources to queue initially
for (int source : sources) {
    queue.offer(new int[]{source, 0});
    distance[source] = 0;
}

while (!queue.isEmpty()) {
    int[] curr = queue.poll();
    int node = curr[0], dist = curr[1];
    
    for (int neighbor : getNeighbors(node)) {
        if (distance[neighbor] == -1) {
            distance[neighbor] = dist + 1;
            queue.offer(new int[]{neighbor, dist + 1});
        }
    }
}
```

---

### Pattern 7: Ancestor Collection (LCA / Common Parent)

**Use for:** Finding common ancestors, lowest common ancestor in trees/DAGs

**Template (Tree):**
```java
// Collect ancestors with depths for each target
Map<Group, Integer> getAncestors(Employee emp) {
    Map<Group, Integer> ancestors = new HashMap<>();
    Group current = emp.getGroup();
    int depth = 0;
    
    while (current != null) {
        ancestors.put(current, depth);
        current = current.getParent();
        depth++;
    }
    return ancestors;
}

// Find intersection, return one with MAX depth (closest to employees)
Group findClosest(List<Employee> employees) {
    Map<Group, Integer> common = getAncestors(employees.get(0));
    
    for (int i = 1; i < employees.size(); i++) {
        Map<Group, Integer> ancestors = getAncestors(employees.get(i));
        common.keySet().retainAll(ancestors.keySet());
    }
    
    return common.entrySet().stream()
        .max(Map.Entry.comparingByValue())
        .map(Map.Entry::getKey)
        .orElse(null);
}
```

**Template (DAG - BFS upward):**
```java
Group findClosestDAG(List<Employee> employees) {
    Map<Group, Integer> visitCount = new HashMap<>();
    
    for (Employee emp : employees) {
        Set<Group> visited = new HashSet<>();
        Queue<Group> queue = new LinkedList<>(emp.getGroups());
        
        while (!queue.isEmpty()) {
            Group g = queue.poll();
            if (visited.contains(g)) continue;
            visited.add(g);
            
            int count = visitCount.merge(g, 1, Integer::sum);
            if (count == employees.size()) {
                return g;  // First group visited by ALL
            }
            
            queue.addAll(g.getParents());
        }
    }
    return null;
}
```

---

### Pattern 8: Top N Using Min-Heap

**Use for:** Finding top N elements by some metric

**Key Insight:** Use MIN-heap of size N, not MAX-heap!
- Min-heap keeps smallest at top
- When size > N, remove smallest (not good enough)
- Remaining N elements are the largest

**Template:**
```java
PriorityQueue<Item> minHeap = new PriorityQueue<>(
    (a, b) -> Long.compare(a.value, b.value)  // MIN heap
);

for (Item item : items) {
    minHeap.offer(item);
    if (minHeap.size() > n) {
        minHeap.poll();  // Remove smallest
    }
}

// Extract in descending order
List<Item> result = new ArrayList<>();
while (!minHeap.isEmpty()) {
    result.add(minHeap.poll());
}
Collections.reverse(result);  // Largest first
```

**Why Min-Heap instead of Max-Heap?**
```
Finding Top 2 from [5, 3, 8, 1, 9]

Min-Heap approach (correct):
  Add 5: [5]
  Add 3: [3, 5]
  Add 8: [3, 5, 8] → size > 2, remove 3 → [5, 8]
  Add 1: [1, 5, 8] → size > 2, remove 1 → [5, 8]
  Add 9: [5, 8, 9] → size > 2, remove 5 → [8, 9] ✓

Max-Heap would keep largest at top, making it hard to evict.
```

**Complexity:** O(n log N) where N = desired top count

---

### Pattern 9: ReadWriteLock for Concurrent Access

**Use for:** Thread-safe data structures with more reads than writes

**Template:**
```java
private final ReadWriteLock lock = new ReentrantReadWriteLock();
private final Lock readLock = lock.readLock();
private final Lock writeLock = lock.writeLock();

// Multiple readers allowed simultaneously
public Data read() {
    readLock.lock();
    try {
        return data;
    } finally {
        readLock.unlock();
    }
}

// Only one writer at a time, blocks all readers
public void write(Data newData) {
    writeLock.lock();
    try {
        data = newData;
    } finally {
        writeLock.unlock();
    }
}
```

**When to Use What:**
| Scenario | Lock Type |
|----------|-----------|
| Read-heavy workload | ReadWriteLock |
| Write-heavy workload | synchronized or ReentrantLock |
| Simple single-value | AtomicReference/AtomicLong |
| High contention | ConcurrentHashMap |

---

## Quick Reference Card

| Problem Type | Pattern | Time |
|--------------|---------|------|
| Count overlapping intervals | Sweep Line | O(n log n) or O(n+T) |
| Merge intervals | Sort + merge | O(n log n) |
| Min resources for scheduling | Greedy + Heap | O(n log n) |
| Track max with updates | Cached variable | O(1) read, O(1)/O(n) write |
| Shortest path (weighted) | Dijkstra | O((V+E) log V) |
| Group items by key | TreeMap + Sets | O(log n) per op |
| Check reachability | BFS/DFS | O(V + E) |
| Distance from multiple sources | Multi-Source BFS | O(V + E) |
| Common ancestor (Tree) | Collect + Intersect | O(E × H) |
| Common ancestor (DAG) | BFS upward + count | O(E × G) |
| Infection/spread counting | BFS + Factorial | O(n) |
| Segment analysis | Math (combinations) | O(m log m) |
| Top N by aggregate | Min-Heap of size N | O(n log N) |
| Thread-safe reads/writes | ReadWriteLock | +lock overhead |
| Many-to-many relationships | Bidirectional maps | O(1) lookup |