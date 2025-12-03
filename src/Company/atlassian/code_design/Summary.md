# LLD Interview Questions - Design Walkthroughs

A comprehensive guide for explaining Low-Level Design problems in interviews.

---

## Table of Contents

1. [Middleware Router](#1-middleware-router)
2. [Snake Game](#2-snake-game)
3. [Cost Explorer](#3-cost-explorer)
4. [Customer Satisfaction](#4-customer-satisfaction)
5. [F1 Last Lap Hero](#5-f1-last-lap-hero)
6. [Cinema Screenings](#6-cinema-screenings)
7. [Deployment Notification](#7-deployment-notification)
8. [Rate Limiter](#8-rate-limiter)
9. [File Tracker](#9-file-tracker)
10. [Hit Counter](#10-hit-counter)

---

## 1. Middleware Router

### Problem Statement
Design a routing system that matches URL paths to handlers, supporting exact matches, wildcards (*), and path parameters ({id}).

### Design Walkthrough

**Step 1: Identify the core entities**
- Route: path pattern + handler
- Router: stores routes, matches incoming paths

**Step 2: Choose data structure**
- Simple approach: HashMap for exact matches (O(1))
- Optimal approach: Trie for pattern matching (O(M) where M = segments)

**Step 3: Define matching priority**
```
Exact match > Path parameter > Wildcard
/users/123  >  /users/{id}  >  /users/*
```

**Step 4: Design Trie node structure**
```
TrieNode:
  - children: Map<String, TrieNode>  → exact segment matches
  - paramChild: TrieNode             → for {param}
  - wildcardChild: TrieNode          → for *
  - handler: String                  → endpoint handler
```

### Example Walkthrough

```
Register routes:
  /api/users → "UserHandler"
  /api/users/{id} → "UserDetailHandler"
  /api/* → "CatchAllHandler"

Trie structure:
         [root]
            |
          "api"
            |
    ┌───────┼───────┐
  "users"  {param}   *
    |        |       |
  [end]    [end]   [end]

Match "/api/users/123":
  1. Split: ["api", "users", "123"]
  2. "api" → exact match, go to api node
  3. "users" → exact match, go to users node
  4. "123" → no exact match, check paramChild → found!
  5. Return "UserDetailHandler", params={id: "123"}
```

### Key Points to Mention
- Why Trie over HashMap: Supports patterns, not just exact matches
- Why single paramChild/wildcardChild: They match ANY value, no key needed
- Time complexity: O(M) where M = path segments

---

## 2. Snake Game

### Problem Statement
Design the classic snake game with movement, growth, collision detection, and optional features like wrap-around and food.

### Design Walkthrough

**Step 1: Identify core entities**
- Position: (x, y) coordinates
- Direction: UP, DOWN, LEFT, RIGHT with dx/dy values
- Snake: list of positions (head to tail)
- Game: manages board, snake, food, game state

**Step 2: Choose data structure for snake body**
```
LinkedList<Position>
  - Head: first element
  - Tail: last element
  - Move: add new head, remove tail
  - Grow: add new head, DON'T remove tail
```

**Step 3: Define movement logic**
```
1. Calculate new head position
2. Check collisions (wall, self)
3. Add new head to front
4. If not growing, remove tail
```

**Step 4: Add extensibility**
- GrowthStrategy interface for different growth rules
- Food system with random spawning
- Wrap-around mode for walls

### Example Walkthrough

```
Initial state:
  Board: 5x5
  Snake: [(2,2), (2,1), (2,0)]  ← head at (2,2)
  Direction: RIGHT

Move RIGHT:
  1. New head = (2+1, 2) = (3, 2)
  2. Check collision: (3,2) not wall, not self ✓
  3. Add head: [(3,2), (2,2), (2,1), (2,0)]
  4. Remove tail: [(3,2), (2,2), (2,1)]

  Before:          After:
  . . . . .        . . . . .
  . . . . .        . . . . .
  H → . . .        . H → . .
  ↑ . . . .        . ↑ . . .
  ↑ . . . .        . ↑ . . .

Growth (eating food):
  - Same as move, but skip step 4 (don't remove tail)
  - Snake length increases by 1
```

### Key Points to Mention
- LinkedList allows O(1) add/remove at both ends
- shouldGrow flag defers growth to next move
- Wrap-around: `(newX % width + width) % width`

---

## 3. Cost Explorer

### Problem Statement
Design a subscription billing system that calculates yearly costs with monthly breakdowns for customers with multiple products and plans.

### Design Walkthrough

**Step 1: Identify domain entities**
- Plan: pricing tier (name, monthly price)
- Product: contains multiple plans (e.g., Jira with Free/Standard/Premium)
- Subscription: links customer to product+plan with date range
- Customer: has multiple subscriptions

**Step 2: Define reporting structure**
```
CostReport
  └── MonthlyBill (x12)
        └── BillLineItem (per active subscription)
              - product, plan, amount
```

**Step 3: Core logic - subscription active check**
```java
isActiveInMonth(year, month):
  - Start date <= end of month
  - End date (if set) >= start of month
```

**Step 4: Generate report flow**
```
For each month (1-12):
  For each subscription:
    If active in month:
      Add line item with monthly price
  Sum line items → monthly total
Sum monthly totals → yearly total
```

### Example Walkthrough

```
Customer: John
Subscriptions:
  1. Jira Standard ($10/month) from Mar 1 to Jun 30, 2024
  2. Confluence Basic ($5/month) from Jan 1, ongoing

Generate 2024 Report:

  Month    | Jira | Confluence | Total
  ---------|------|------------|------
  January  |  $0  |    $5      |  $5
  February |  $0  |    $5      |  $5
  March    | $10  |    $5      | $15
  April    | $10  |    $5      | $15
  May      | $10  |    $5      | $15
  June     | $10  |    $5      | $15
  July     |  $0  |    $5      |  $5
  ...      |  $0  |    $5      |  $5
  ---------|------|------------|------
  Yearly   | $40  |   $60      | $100
```

### Key Points to Mention
- Use YearMonth for month-level date comparisons
- Null end date means ongoing subscription
- Each subscription counted independently per month

---

## 4. Customer Satisfaction

### Problem Statement
Design a support agent rating system with sorting, tie-breaking strategies, monthly reports, and export functionality.

### Design Walkthrough

**Step 1: Identify core entities**
- Agent: id, name
- Rating: agentId, score (1-5), timestamp
- AgentStats: calculated metrics (average, count, total)

**Step 2: Design storage**
```
agents: Map<String, Agent>
ratings: List<Rating>  ← store all ratings with timestamps
```

**Step 3: Add extensibility with Strategy Pattern**
```
TieBreakStrategy (Comparator<AgentStats>):
  - BY_TOTAL_RATINGS: more ratings wins
  - BY_NAME: alphabetical
  - BY_ID: agent ID order
```

**Step 4: Add export functionality**
```
Exporter interface:
  - CSVExporter: generates CSV format
  - JSONExporter: generates JSON format
```

### Example Walkthrough

```
Agents: Alice (A1), Bob (A2)

Ratings:
  A1: 5, 4, 5  → avg = 4.67, count = 3
  A2: 5, 5     → avg = 5.0, count = 2

getAgentsSortedByRating():
  1. Calculate stats for each agent
  2. Sort by average (desc): Bob (5.0), Alice (4.67)
  3. Apply tie-breaker if averages equal

Tie scenario (both avg = 4.5):
  - BY_TOTAL_RATINGS: Alice (3) > Bob (2)
  - BY_NAME: Alice < Bob (alphabetical)

Monthly grouping:
  Ratings with timestamps → group by YYYY-MM
  
  2024-01: [A1: avg 4.5, A2: avg 5.0]
  2024-02: [A1: avg 5.0, A2: avg 4.0]

Export CSV:
  Month,AgentId,AgentName,AverageRating,TotalRatings
  2024-1,A2,Bob,5.0,2
  2024-1,A1,Alice,4.5,3
```

### Key Points to Mention
- Stats calculated on-demand from rating list
- Strategy pattern allows flexible sorting
- Integer month key (202401) for simple sorting

---

## 5. F1 Last Lap Hero

### Problem Statement
Track lap times for F1 drivers and find the "Last Lap Hero" - the driver with the biggest improvement on their last lap compared to their average.

### Design Walkthrough

**Step 1: Identify core entities**
- Driver: id, name
- LapTime: driverId, time, isPitStop flag
- DriverStats: calculated metrics (average, last lap, improvement)

**Step 2: Define improvement calculation**
```
Improvement = Average Lap Time - Last Lap Time
  - Positive = faster on last lap (hero!)
  - Negative = slower on last lap
```

**Step 3: Design service structure**
```
drivers: Map<String, Driver>
laps: List<LapTime>  ← all laps in order

Key methods:
  - addLapTime(driverId, time, isPitStop)
  - getLastLapHero() → returns driver with max improvement
```

**Step 4: Handle scale-ups**
- Pit stops: exclude from average but valid as last lap
- Telemetry: track hero changes with a log

### Example Walkthrough

```
Lap times:
  Hamilton: 92, 90, 88 (seconds)
  Verstappen: 91, 89, 85

Calculate stats:
  Hamilton:
    Average = (92 + 90 + 88) / 3 = 90
    Last lap = 88
    Improvement = 90 - 88 = 2 seconds

  Verstappen:
    Average = (91 + 89 + 85) / 3 = 88.33
    Last lap = 85
    Improvement = 88.33 - 85 = 3.33 seconds

Last Lap Hero: Verstappen (bigger improvement)

With Pit Stop (120s):
  Hamilton: 92, 120 (pit), 88
  
  Including pit: avg = 100, improvement = 12
  Excluding pit: avg = 90, improvement = 2
```

### Key Points to Mention
- Need at least 2 laps to calculate improvement
- Pit stop flag allows separate calculations
- Telemetry tracks hero changes in real-time

---

## 6. Cinema Screenings

### Problem Statement
Design a cinema scheduling system to check if a new movie fits, find optimal slots, and manage multiple rooms with revenue optimization.

### Design Walkthrough

**Step 1: Define constraints**
```
- Opening time: 10:00 (600 minutes from midnight)
- Closing time: 23:00 (1380 minutes)
- Back-to-back allowed (end time = next start time)
```

**Step 2: Identify core entities**
- Movie: id, name, duration, revenue
- Screening: movie + start time (end time = start + duration)
- Room: list of screenings, sorted by time
- CinemaService: manages multiple rooms

**Step 3: Core logic - finding available slot**
```
1. Check gap before first screening
2. Check gaps between consecutive screenings
3. Check gap after last screening

For each gap:
  If gap >= movie duration → slot found!
```

**Step 4: Scale-up: Revenue optimization**
```
findBestScreeningToReplace(newMovie):
  For each existing screening:
    If removing it creates enough space:
      Calculate netGain = newRevenue - lostRevenue
      Track best (highest netGain)
```

### Example Walkthrough

```
Room schedule:
  Inception: 10:00 - 12:30 (150 min)
  Avatar: 12:30 - 15:30 (180 min)

Time visualization:
  10:00    12:30    15:30    23:00
    |--------|--------|........|
    Inception  Avatar    FREE

Can fit "NewMovie" (120 min)?
  Gap 1: before Inception = 0 min ✗
  Gap 2: between = 0 min (back-to-back) ✗
  Gap 3: after Avatar = 23:00 - 15:30 = 450 min ✓

  Available slot: 15:30 (930 minutes from midnight)

Revenue replacement:
  Current: Inception ($500) + Avatar ($700) = $1200
  New movie: Blockbuster ($1000, 120 min)
  
  Replace Inception ($500)?
    Net gain = $1000 - $500 = +$500 ✓
  
  Replace Avatar ($700)?
    Net gain = $1000 - $700 = +$300
  
  Best to replace: Inception (highest gain)
```

### Key Points to Mention
- Time in minutes simplifies calculations
- Screenings kept sorted by start time
- Revenue comparison for smart replacements

---

## 7. Deployment Notification

### Problem Statement
Design a notification service that notifies code authors when their changes are deployed successfully for the first time.

### Design Walkthrough

**Step 1: Identify event types**
```
DeploymentStatus:
  - STARTED: deployment begins
  - COMPLETED: deployment succeeds
  - FAILED: deployment fails (retry later)
  - REVERTED: changes rolled back (scale-up)
```

**Step 2: Design storage - KEY INSIGHT**
```
❌ Wrong: Set<String> pendingAuthors (all mixed)
✓ Correct: Map<String, Set<String>> versionAuthors

Why? Multiple deployments can be in progress simultaneously!
Each version has its own set of pending authors.
```

**Step 3: Define event handling**
```
STARTED: Add authors to version's pending set
COMPLETED: Notify version's authors, clear that version
FAILED: Do nothing (authors remain pending for retry)
REVERTED: Remove reverted authors from version
```

### Example Walkthrough

```
Event flow:

1. STARTED v1.0 [Alice, Bob]
   versionAuthors: {"v1.0" → {Alice, Bob}}

2. STARTED v2.0 [Charlie]
   versionAuthors: {
     "v1.0" → {Alice, Bob},
     "v2.0" → {Charlie}
   }

3. COMPLETED v2.0
   → Notify Charlie for v2.0
   → Remove v2.0 from map
   
   versionAuthors: {"v1.0" → {Alice, Bob}}
   pendingNotifications: [Charlie/v2.0]

4. FAILED v1.0
   → Do nothing, Alice & Bob remain pending
   
   versionAuthors: {"v1.0" → {Alice, Bob}}

5. COMPLETED v1.0
   → Notify Alice, Bob for v1.0
   
   versionAuthors: {}
   pendingNotifications: [Charlie/v2.0, Alice/v1.0, Bob/v1.0]

Revert scenario:
  STARTED v3.0 [Dave, Eve]
  REVERTED v3.0 [Dave]  ← Dave's code reverted
  COMPLETED v3.0
  
  → Only Eve notified (Dave was removed)
```

### Key Points to Mention
- Version-specific tracking is crucial
- FAILED keeps authors pending for retry
- REVERTED removes specific authors before notification

---

## 8. Rate Limiter

### Problem Statement
Design an in-memory rate limiter supporting different strategies: fixed-window counter and sliding-window counter.

### Design Walkthrough

**Step 1: Define the interface**
```java
interface RateLimitStrategy {
    boolean allowRequest();
}
```

**Step 2: Strategy 1 - Fixed Window Counter**
```
Divides time into fixed blocks, counts requests per block.

State:
  - windowStart: timestamp when window began
  - requestCount: requests in current window

Logic:
  1. If (now - windowStart >= windowSize): reset window
  2. If (count < max): allow and increment
  3. Else: reject
```

**Step 3: Strategy 2 - Sliding Window (Log-based)**
```
Stores timestamps, removes expired ones.

State:
  - requestTimestamps: list of all request times

Logic:
  1. Remove timestamps older than (now - windowSize)
  2. If (list.size < max): allow and add timestamp
  3. Else: reject
```

**Step 4: Scale-ups**
- Credit-based: unused requests carry over
- Thread-safe: synchronized blocks or AtomicLong

### Example Walkthrough

```
Config: 3 requests per 1000ms

═══ FIXED WINDOW ═══

t=100: count=1 ✓ (0 < 3)
t=200: count=2 ✓ (1 < 3)
t=500: count=3 ✓ (2 < 3)
t=600: count=3 ✗ (3 < 3 is false)
t=1100: NEW WINDOW! count=1 ✓ (reset, 0 < 3)

═══ SLIDING WINDOW ═══

t=100: timestamps=[100], size=1 ✓
t=200: timestamps=[100,200], size=2 ✓
t=500: timestamps=[100,200,500], size=3 ✓
t=600: timestamps=[100,200,500], size=3 ✗
t=1200: 
  - Remove timestamps <= 200 (cutoff = 1200-1000)
  - timestamps=[500], size=1 ✓
  - timestamps=[500,1200]

═══ BURST PROBLEM (Fixed Window) ═══

Window 1 (0-999):    5 requests at t=900-999 ✓
Window 2 (1000+):    5 requests at t=1000-1099 ✓
                     
Result: 10 requests in 200ms! (burst at boundary)

Sliding window prevents this - always looks back exactly 1 second.
```

### Key Points to Mention
- Fixed window: O(1) time/space, but burst at edges
- Sliding window: O(n) time/space, but accurate
- Strategy pattern allows easy algorithm switching

---

## 9. File Tracker

### Problem Statement
Design an in-memory system that tracks files and their membership in collections, with size calculations and ranking queries.

### Design Walkthrough

**Step 1: Identify core entities**
- FileRecord: fileName, fileSize, Set<collections>
- CollectionStats: name, totalSize, fileCount

**Step 2: Design storage with reverse index**
```
files: Map<String, FileRecord>
  - Quick lookup by filename

collectionFiles: Map<String, Set<String>>
  - Quick lookup: "which files are in collection X?"
  - Reverse index for efficient collection queries
```

**Step 3: Key operations**
```
addFile:
  1. Create FileRecord
  2. Add to files map
  3. Add to each collection's set in collectionFiles

updateFile:
  1. Remove from old collections
  2. Update record
  3. Add to new collections

getTotalSize:
  - Sum all file sizes (each file counted ONCE)

getTopCollectionsBySize:
  - Calculate size per collection
  - Sort by total size
```

### Example Walkthrough

```
Add files:
  file1.txt (100 bytes) → [docs, work]
  file2.pdf (200 bytes) → [docs]
  file3.jpg (500 bytes) → []

Storage state:
  files:
    "file1.txt" → FileRecord(100, [docs, work])
    "file2.pdf" → FileRecord(200, [docs])
    "file3.jpg" → FileRecord(500, [])

  collectionFiles:
    "docs" → {file1.txt, file2.pdf}
    "work" → {file1.txt}

Total size: 100 + 200 + 500 = 800 bytes
(Each file counted ONCE, regardless of collections)

Collection sizes:
  docs: file1(100) + file2(200) = 300 bytes
  work: file1(100) = 100 bytes

Update file1.txt to [photos]:
  1. Remove from docs: docs → {file2.pdf}
  2. Remove from work: work → {} (delete empty)
  3. Add to photos: photos → {file1.txt}
```

### Key Points to Mention
- Reverse index enables efficient collection queries
- Files counted once for total, but can be in multiple collections
- Empty collections removed to keep data clean

---

## 10. Hit Counter

### Problem Statement
Design a system to record webpage visits and return counts per page, total counts, and top pages.

### Design Walkthrough

**Step 1: Identify core requirements**
- Record hits for any page
- Get hit count per page
- Get total hits across all pages
- Get top N pages by hits
- (Optional) Get hits in last N seconds

**Step 2: Design storage**
```
pageHits: Map<String, Long>
  - pageId → total hit count
  - O(1) increment and lookup

pageTimestamps: Map<String, List<Long>>
  - pageId → list of timestamps
  - For time-based queries
```

**Step 3: Thread-safe version**
```
pageHits: ConcurrentHashMap<String, AtomicLong>
  - AtomicLong for lock-free increments
  
pageTimestamps: ConcurrentHashMap<String, List<Long>>
  - Collections.synchronizedList for thread-safe list
```

### Example Walkthrough

```
Record visits:
  /home, /home, /about, /home, /products

State after visits:
  pageHits:
    "/home"     → 3
    "/about"    → 1
    "/products" → 1

  pageTimestamps:
    "/home"     → [100, 200, 500]
    "/about"    → [300]
    "/products" → [400]

Queries:
  getHitCount("/home") → 3
  getTotalHits() → 3 + 1 + 1 = 5
  getTopPages(2) → [/home(3), /about(1)] or [/home(3), /products(1)]

Time-based query at t=600:
  getHitsInLastNSeconds("/home", 5) → 3 (all within 5 sec)
  
Later at t=10600:
  cutoff = 10600 - 5000 = 5600
  getHitsInLastNSeconds("/home", 5) → 0 (all timestamps < 5600)

Thread-safe scenario:
  Thread 1: read count=4, check 4<5 ✓
  Thread 2: read count=4, check 4<5 ✓ (RACE!)
  
  With AtomicLong:
  Thread 1: incrementAndGet() → 5
  Thread 2: incrementAndGet() → 6 (correct!)
```

### Key Points to Mention
- HashMap for O(1) hit tracking
- Timestamps enable time-based queries
- AtomicLong for thread-safe counting without locks

---

## Interview Tips

### General Approach

1. **Clarify requirements** (2-3 min)
    - What are the main operations?
    - What are the constraints?
    - Any scale considerations?

2. **Identify entities** (2-3 min)
    - List nouns from the problem
    - Define relationships

3. **Choose data structures** (3-5 min)
    - Explain trade-offs
    - Justify your choices

4. **Write code** (20-25 min)
    - Start with core classes
    - Add main logic
    - Handle edge cases

5. **Test with examples** (5 min)
    - Walk through a scenario
    - Verify correctness

### Common Patterns

| Pattern | Use When | Examples |
|---------|----------|----------|
| Strategy | Multiple algorithms | Rate limiter, Tie-breaking |
| Observer | Event notifications | Deployment, Telemetry |
| Factory | Object creation | Different strategy types |
| Composite | Tree structures | Trie router |

### Data Structure Quick Reference

| Need | Use | Time |
|------|-----|------|
| Fast lookup by key | HashMap | O(1) |
| Ordered data | TreeMap | O(log n) |
| Unique items | HashSet | O(1) |
| FIFO queue | LinkedList | O(1) |
| Pattern matching | Trie | O(m) |
| Top K items | Sort + limit | O(n log n) |

---

## Time Allocation (40-min interview)

```
0-5 min:   Clarify requirements, ask questions
5-10 min:  Identify classes, relationships, data structures
10-35 min: Code implementation
35-40 min: Walk through example, discuss trade-offs
```

Focus on getting a working solution first, then optimize if time permits!