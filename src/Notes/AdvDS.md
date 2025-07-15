# Advanced Data Structures for Interviews

## Table of Contents
1. [Segment Trees](#segment-trees)
2. [Binary Indexed Trees (Fenwick Trees)](#binary-indexed-trees-fenwick-trees)
3. [Prefix Sum Arrays](#prefix-sum-arrays)
4. [Other Advanced Data Structures](#other-advanced-data-structures)

---

## Segment Trees

### Overview
A segment tree is a tree data structure used for storing information about intervals/segments. It allows for efficient range queries and range updates on arrays.

### Time Complexity
- Build: O(n)
- Query: O(log n)
- Update: O(log n)
- Space: O(n)

### Use Cases
1. **Range Sum Queries**: Find sum of elements in range [l, r]
2. **Range Minimum/Maximum Queries**: Find min/max in range [l, r]
3. **Range Updates**: Update all elements in a range
4. **Lazy Propagation**: Efficient range updates with deferred computation

### Common Interview Problems
- Range Sum Query - Mutable (LeetCode 307)
- Range Sum Query 2D - Mutable (LeetCode 308)
- Count of Smaller Numbers After Self (LeetCode 315)
- Reverse Pairs (LeetCode 493)

### Implementation Example (Range Sum)
```java
class SegmentTree {
    private int[] tree;
    private int n;
    
    public SegmentTree(int[] nums) {
        n = nums.length;
        tree = new int[4 * n];
        build(nums, 0, 0, n - 1);
    }
    
    private void build(int[] nums, int node, int start, int end) {
        if (start == end) {
            tree[node] = nums[start];
        } else {
            int mid = (start + end) / 2;
            build(nums, 2 * node + 1, start, mid);
            build(nums, 2 * node + 2, mid + 1, end);
            tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
        }
    }
    
    public void update(int idx, int val) {
        update(0, 0, n - 1, idx, val);
    }
    
    private void update(int node, int start, int end, int idx, int val) {
        if (start == end) {
            tree[node] = val;
        } else {
            int mid = (start + end) / 2;
            if (idx <= mid) {
                update(2 * node + 1, start, mid, idx, val);
            } else {
                update(2 * node + 2, mid + 1, end, idx, val);
            }
            tree[node] = tree[2 * node + 1] + tree[2 * node + 2];
        }
    }
    
    public int query(int l, int r) {
        return query(0, 0, n - 1, l, r);
    }
    
    private int query(int node, int start, int end, int l, int r) {
        if (r < start || end < l) return 0;
        if (l <= start && end <= r) return tree[node];
        
        int mid = (start + end) / 2;
        return query(2 * node + 1, start, mid, l, r) +
               query(2 * node + 2, mid + 1, end, l, r);
    }
}
```

### Visualization

#### Basic Segment Tree Structure
```
Array: [1, 3, 5, 7, 9, 11]
Indices: 0  1  2  3  4  5

Segment Tree (Sum):
                   36 [0,5]
                 /          \
           9 [0,2]            27 [3,5]
          /        \         /         \
     4 [0,1]     5 [2,2]  16 [3,4]   11 [5,5]
    /      \              /      \
1 [0,0]  3 [1,1]     7 [3,3]  9 [4,4]
```

#### Query Example: sum(1, 4)
```
Query Range: [1, 4] (highlighted with *)

                   36 [0,5]
                 /          \
           9*[0,2]           27*[3,5]
          /        \         /         \
     4*[0,1]     5*[2,2]  16*[3,4]   11 [5,5]
    /      \              /      \
1 [0,0]  3*[1,1]     7*[3,3]  9*[4,4]

Path taken: 3 + 5 + 7 + 9 = 24
```

#### Update Example: update(2, 10)
```
Before: Array = [1, 3, 5, 7, 9, 11]
After:  Array = [1, 3, 10, 7, 9, 11]

Updated nodes (marked with *):
                   41*[0,5]  (+5)
                 /          \
           14*[0,2]          27 [3,5]
          /        \         /         \
     4 [0,1]    10*[2,2]  16 [3,4]   11 [5,5]
    /      \              /      \
1 [0,0]  3 [1,1]     7 [3,3]  9 [4,4]
```

### Alternative: Lazy Propagation
For range updates, use lazy propagation to defer updates until necessary:
```java
class LazySegmentTree {
    private int[] tree, lazy;
    private int n;
    
    // Implementation with lazy propagation for range updates
    // Reduces update complexity from O(n log n) to O(log n)
}
```

---

## Binary Indexed Trees (Fenwick Trees)

### Overview
A Binary Indexed Tree (BIT) or Fenwick Tree is a data structure that efficiently supports prefix sum queries and point updates on an array.

### Time Complexity
- Build: O(n)
- Prefix Sum Query: O(log n)
- Point Update: O(log n)
- Space: O(n)

### Use Cases
1. **Prefix Sum Queries**: Calculate sum from index 0 to i
2. **Range Sum Queries**: Calculate sum from index l to r
3. **Frequency Counting**: Count inversions, smaller elements
4. **Coordinate Compression**: Handle large value ranges

### Common Interview Problems
- Range Sum Query - Mutable (LeetCode 307)
- Count of Smaller Numbers After Self (LeetCode 315)
- Reverse Pairs (LeetCode 493)
- Count of Range Sum (LeetCode 327)

### Implementation Example
```java
class BinaryIndexedTree {
    private int[] tree;
    private int n;
    
    public BinaryIndexedTree(int size) {
        n = size;
        tree = new int[n + 1];
    }
    
    public void update(int idx, int delta) {
        for (int i = idx; i <= n; i += i & (-i)) {
            tree[i] += delta;
        }
    }
    
    public int query(int idx) {
        int sum = 0;
        for (int i = idx; i > 0; i -= i & (-i)) {
            sum += tree[i];
        }
        return sum;
    }
    
    public int rangeQuery(int left, int right) {
        return query(right) - query(left - 1);
    }
}
```

### Visualization

#### Binary Indexed Tree Structure
```
Array:    [1, 3, 5, 7, 9, 11]
Indices:   1  2  3  4  5  6    (1-indexed)

BIT Array: [0, 1, 4, 5, 16, 9, 25]
Indices:   0  1  2  3  4   5  6

Tree Structure (shows responsibility ranges):
Index:  1   2   3   4   5   6
Value:  1   4   5  16   9  25
Range: [1] [1,2] [3] [1,4] [5] [5,6]

Visual representation:
        4[1,4]              6[5,6]
       /      \            /      \
    2[1,2]    4[4]      6[6]      
   /    \     |        |         
1[1]   2[2]  4[4]     6[6]       
```

#### BIT Operations Visualization

**Query(5) - Find prefix sum from 1 to 5:**
```
Start at index 5
5 in binary: 101
5 & (-5) = 1, so add BIT[5] = 9

Move to 5 - 1 = 4
4 in binary: 100  
4 & (-4) = 4, so add BIT[4] = 16

Move to 4 - 4 = 0 (stop)
Result: 9 + 16 = 25 (sum of elements 1,2,3,4,5)
```

**Update(3, +2) - Add 2 to index 3:**
```
Start at index 3
3 in binary: 011
3 & (-3) = 1, so update BIT[3] += 2

Move to 3 + 1 = 4
4 in binary: 100
4 & (-4) = 4, so update BIT[4] += 2

Move to 4 + 4 = 8 (out of bounds, stop)
```

#### Bit Manipulation Insight
```
Index (i)  | Binary | i & (-i) | Next Index | Responsibility
-----------|--------|----------|------------|---------------
1          | 001    | 001      | 2          | [1]
2          | 010    | 010      | 4          | [1,2]  
3          | 011    | 001      | 4          | [3]
4          | 100    | 100      | 8          | [1,4]
5          | 101    | 001      | 6          | [5]
6          | 110    | 010      | 8          | [5,6]
```

### Key Insight: Bit Manipulation
- `i & (-i)` gives the lowest set bit of i
- Used to navigate the tree structure efficiently
- For updates: move up by adding `i & (-i)`
- For queries: move down by subtracting `i & (-i)`

### When to Use BIT vs Segment Tree
- **Use BIT when**: Simple range sum queries, point updates only
- **Use Segment Tree when**: Complex range operations, range updates, min/max queries

---

## Prefix Sum Arrays

### Overview
Prefix sum is a preprocessing technique where we calculate cumulative sums to answer range sum queries in O(1) time.

### Time Complexity
- Build: O(n)
- Range Query: O(1)
- Update: O(n) (requires rebuilding)
- Space: O(n)

### Types of Prefix Sums

#### 1. Basic Prefix Sum
```java
class PrefixSum {
    private int[] prefixSum;
    
    public PrefixSum(int[] nums) {
        prefixSum = new int[nums.length + 1];
        for (int i = 0; i < nums.length; i++) {
            prefixSum[i + 1] = prefixSum[i] + nums[i];
        }
    }
    
    public int rangeSum(int left, int right) {
        return prefixSum[right + 1] - prefixSum[left];
    }
}
```

#### 2. 2D Prefix Sum
```java
class Matrix2DPrefixSum {
    private int[][] prefixSum;
    
    public Matrix2DPrefixSum(int[][] matrix) {
        int m = matrix.length, n = matrix[0].length;
        prefixSum = new int[m + 1][n + 1];
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                prefixSum[i][j] = matrix[i-1][j-1] + 
                                 prefixSum[i-1][j] + 
                                 prefixSum[i][j-1] - 
                                 prefixSum[i-1][j-1];
            }
        }
    }
    
    public int sumRegion(int row1, int col1, int row2, int col2) {
        return prefixSum[row2+1][col2+1] - 
               prefixSum[row1][col2+1] - 
               prefixSum[row2+1][col1] + 
               prefixSum[row1][col1];
    }
}
```

### Visualization

#### 1D Prefix Sum Array
```
Original Array: [2, 1, 3, 6, 5, 4]
Indices:         0  1  2  3  4  5

Prefix Sum:     [0, 2, 3, 6, 12, 17, 21]
Indices:         0  1  2  3   4   5   6

Visual representation:
Index:   0   1   2   3   4   5   6
Value:   0   2   3   6  12  17  21
         ^   ^   ^   ^   ^   ^   ^
         0  0+2 2+1 3+3 6+6 12+5 17+4

Range Sum Query [1,4]:
sum = prefixSum[5] - prefixSum[1] = 17 - 2 = 15
(Elements: 1 + 3 + 6 + 5 = 15)
```

#### 2D Prefix Sum Matrix
```
Original Matrix:
1  2  3
4  5  6
7  8  9

Prefix Sum Matrix (with padding):
0  0  0  0
0  1  3  6
0  5 12 21
0 12 27 45

Building process:
prefixSum[i][j] = matrix[i-1][j-1] + 
                  prefixSum[i-1][j] + 
                  prefixSum[i][j-1] - 
                  prefixSum[i-1][j-1]

Example: prefixSum[2][2] = 5 + 3 + 5 - 1 = 12

Range Sum Query for submatrix (0,1) to (1,2):
Area: |2 3|
      |5 6|

Sum = prefixSum[2][3] - prefixSum[0][3] - prefixSum[2][1] + prefixSum[0][1]
    = 21 - 0 - 5 + 0 = 16
```

#### Prefix Sum with HashMap (Subarray Sum = K)
```
Array: [1, 2, 3, 4, 5], K = 5
Step-by-step visualization:

i=0: num=1, prefixSum=1
     HashMap: {0:1, 1:1}
     Looking for: 1-5 = -4 (not found)

i=1: num=2, prefixSum=3  
     HashMap: {0:1, 1:1, 3:1}
     Looking for: 3-5 = -2 (not found)

i=2: num=3, prefixSum=6
     HashMap: {0:1, 1:1, 3:1, 6:1}
     Looking for: 6-5 = 1 (found! count++)
     Subarray [2,3] sums to 5

i=3: num=4, prefixSum=10
     HashMap: {0:1, 1:1, 3:1, 6:1, 10:1}
     Looking for: 10-5 = 5 (not found)

i=4: num=5, prefixSum=15
     HashMap: {0:1, 1:1, 3:1, 6:1, 10:1, 15:1}
     Looking for: 15-5 = 10 (found! count++)
     Subarray [4] sums to 5

Result: 2 subarrays with sum = 5
```

#### Difference Array Visualization
```
Original Array: [1, 3, 5, 7, 9]

Difference Array: [1, 2, 2, 2, 2]
How: diff[0] = arr[0] = 1
     diff[i] = arr[i] - arr[i-1] for i > 0

Range Update: Add 3 to range [1,3]
- diff[1] += 3  → [1, 5, 2, 2, 2]
- diff[4] -= 3  → [1, 5, 2, 2, -1]

Reconstruct Array:
result[0] = diff[0] = 1
result[1] = result[0] + diff[1] = 1 + 5 = 6  
result[2] = result[1] + diff[2] = 6 + 2 = 8
result[3] = result[2] + diff[3] = 8 + 2 = 10
result[4] = result[3] + diff[4] = 10 + (-1) = 9

Final Array: [1, 6, 8, 10, 9]
Original:    [1, 3, 5,  7, 9]
Difference:  [0, 3, 3,  3, 0] ✓
```

### Use Cases
1. **Subarray Sum Problems**: Find subarrays with target sum
2. **Range Queries**: Fast range sum calculations
3. **Difference Arrays**: For range updates
4. **Sliding Window**: Optimize certain sliding window problems

### Common Interview Problems
- Subarray Sum Equals K (LeetCode 560)
- Range Sum Query - Immutable (LeetCode 303)
- Range Sum Query 2D - Immutable (LeetCode 304)
- Continuous Subarray Sum (LeetCode 523)

### Advanced Techniques

#### Prefix Sum with HashMap
```java
// For subarray sum problems
Map<Integer, Integer> prefixSumCount = new HashMap<>();
prefixSumCount.put(0, 1); // Handle subarrays starting from index 0

int prefixSum = 0, count = 0;
for (int num : nums) {
    prefixSum += num;
    count += prefixSumCount.getOrDefault(prefixSum - target, 0);
    prefixSumCount.put(prefixSum, prefixSumCount.getOrDefault(prefixSum, 0) + 1);
}
```

#### Difference Array
```java
// For multiple range updates
class DifferenceArray {
    private int[] diff;
    
    public DifferenceArray(int[] nums) {
        diff = new int[nums.length];
        diff[0] = nums[0];
        for (int i = 1; i < nums.length; i++) {
            diff[i] = nums[i] - nums[i-1];
        }
    }
    
    public void rangeUpdate(int left, int right, int val) {
        diff[left] += val;
        if (right + 1 < diff.length) {
            diff[right + 1] -= val;
        }
    }
    
    public int[] getResult() {
        int[] result = new int[diff.length];
        result[0] = diff[0];
        for (int i = 1; i < diff.length; i++) {
            result[i] = result[i-1] + diff[i];
        }
        return result;
    }
}
```

---

## Other Advanced Data Structures

### 1. Trie (Prefix Tree)

#### Use Cases
- Autocomplete systems
- IP routing tables
- Word search problems
- XOR maximum problems

#### Time Complexity
- Insert/Search: O(m) where m is string length
- Space: O(ALPHABET_SIZE * N * M)

#### Implementation
```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isEndOfWord = false;
}

class Trie {
    private TrieNode root;
    
    public Trie() {
        root = new TrieNode();
    }
    
    public void insert(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }
            current = current.children[index];
        }
        current.isEndOfWord = true;
    }
    
    public boolean search(String word) {
        TrieNode node = searchNode(word);
        return node != null && node.isEndOfWord;
    }
    
    private TrieNode searchNode(String word) {
        TrieNode current = root;
        for (char c : word.toCharArray()) {
            int index = c - 'a';
            if (current.children[index] == null) {
                return null;
            }
            current = current.children[index];
        }
        return current;
    }
}
```

#### Visualization

##### Trie Structure Example
```
Words to insert: ["cat", "car", "card", "care", "careful", "cars", "dog", "dogs"]

Trie Structure:
                root
              /      \
           c(0)       d(3)
            |          |
           a(0)       o(3)
            |          |
           t(19) ---- r(17)      g(6)
            |          |          |
           *(end)     d(3)       *(end)
                       |          |
                      *(end)     s(18)
                       |          |
                                 *(end)

Numbers in parentheses represent array indices (a=0, b=1, ..., z=25)
* indicates isEndOfWord = true
```

##### Step-by-step Insertion of "CAR"
```
Step 1: Insert 'c'
root -> children[2] = new TrieNode()

Step 2: Insert 'a' 
root -> children[2] -> children[0] = new TrieNode()

Step 3: Insert 'r'
root -> children[2] -> children[0] -> children[17] = new TrieNode()
Set isEndOfWord = true

Final path: root -c-> node -a-> node -r-> node(END)
```

##### Detailed Tree Visualization
```
Insert words: ["cat", "car", "dogs"]

                    root
                 /        \
             c[2]            d[3]
              |               |
             a[0]            o[14]
              |               |
          t[19]   r[17]      g[6]
           |       |          |
         (END)   (END)       s[18]
                              |
                            (END)

Legend:
- [n] = array index in children array
- (END) = isEndOfWord = true
- Each level represents one character
```

##### Search Operation Example
```
Search for "car":
1. Start at root
2. Go to children[2] (for 'c') ✓
3. Go to children[0] (for 'a') ✓  
4. Go to children[17] (for 'r') ✓
5. Check isEndOfWord = true ✓
Result: FOUND

Search for "ca":
1. Start at root
2. Go to children[2] (for 'c') ✓
3. Go to children[0] (for 'a') ✓
4. Check isEndOfWord = false ✗
Result: NOT FOUND (prefix exists but not a complete word)
```

##### Binary Trie (for XOR problems)
```
Numbers: [3, 10, 5, 25]
Binary representations:
3  = 00011
10 = 01010  
5  = 00101
25 = 11001

Binary Trie:
           root
          /    \
        0       1
       /|\     /|\
      0 1     1  0
     /| |\   /|  |\
    0 1 0 1 0 1  0 1
   
Each path from root to leaf represents one number
Used for maximum XOR pair problems
```

### 2. Union-Find (Disjoint Set Union)

#### Use Cases
- Connected components
- Cycle detection
- Minimum spanning tree (Kruskal's algorithm)
- Dynamic connectivity

#### Time Complexity
- Find/Union: O(�(n)) H O(1) with path compression and union by rank
- Space: O(n)

#### Implementation
```java
class UnionFind {
    private int[] parent, rank;
    private int components;
    
    public UnionFind(int n) {
        parent = new int[n];
        rank = new int[n];
        components = n;
        for (int i = 0; i < n; i++) {
            parent[i] = i;
        }
    }
    
    public int find(int x) {
        if (parent[x] != x) {
            parent[x] = find(parent[x]); // Path compression
        }
        return parent[x];
    }
    
    public boolean union(int x, int y) {
        int rootX = find(x);
        int rootY = find(y);
        
        if (rootX == rootY) return false;
        
        // Union by rank
        if (rank[rootX] < rank[rootY]) {
            parent[rootX] = rootY;
        } else if (rank[rootX] > rank[rootY]) {
            parent[rootY] = rootX;
        } else {
            parent[rootY] = rootX;
            rank[rootX]++;
        }
        
        components--;
        return true;
    }
    
    public boolean connected(int x, int y) {
        return find(x) == find(y);
    }
    
    public int getComponents() {
        return components;
    }
}
```

#### Visualization

##### Initial State
```
Elements: 0, 1, 2, 3, 4, 5
Initial Union-Find structure (each element is its own set):

parent: [0, 1, 2, 3, 4, 5]
rank:   [0, 0, 0, 0, 0, 0]

Visual representation:
0    1    2    3    4    5
↓    ↓    ↓    ↓    ↓    ↓
0    1    2    3    4    5
(Each element points to itself)
```

##### Union Operations Step-by-Step
```
Operation 1: union(0, 1)
- find(0) = 0, find(1) = 1
- rank[0] = rank[1] = 0, so make 1 point to 0
- rank[0]++

parent: [0, 0, 2, 3, 4, 5]
rank:   [1, 0, 0, 0, 0, 0]

Visual:
0    2    3    4    5
↓    ↓    ↓    ↓    ↓
0    2    3    4    5
↑
1

Operation 2: union(2, 3)
parent: [0, 0, 2, 2, 4, 5]
rank:   [1, 0, 1, 0, 0, 0]

Visual:
0    2    4    5
↓    ↓    ↓    ↓
0    2    4    5
↑    ↑
1    3

Operation 3: union(0, 2) - Union by rank
- find(0) = 0, find(2) = 2
- rank[0] = rank[2] = 1, so make 2 point to 0
- rank[0]++

parent: [0, 0, 0, 2, 4, 5]
rank:   [2, 0, 1, 0, 0, 0]

Visual:
    0      4    5
   /|\     ↓    ↓
  1 2  ?   4    5
    ↓
    3
```

##### Path Compression Example
```
Before path compression:
    0
   /|\
  1 2  
    ↓
    3

After find(3) with path compression:
    0
   /|\
  1 2 3

parent array changes from [0, 0, 0, 2, 4, 5] to [0, 0, 0, 0, 4, 5]
Now 3 directly points to root 0
```

##### Connected Components Visualization
```
After operations: union(0,1), union(2,3), union(0,2), union(4,5)

Final structure:
Component 1: {0, 1, 2, 3}
    0
   /|\
  1 2 3

Component 2: {4, 5}
    4
    ↓
    5

parent: [0, 0, 0, 0, 4, 4]
rank:   [2, 0, 1, 0, 1, 0]
components = 2
```

##### Union-Find in Graph Context
```
Graph edges: [(0,1), (1,2), (3,4)]
Vertices: 0, 1, 2, 3, 4

Processing edges:
1. union(0, 1): Components = {0,1}, {2}, {3}, {4}
2. union(1, 2): Components = {0,1,2}, {3}, {4}  
3. union(3, 4): Components = {0,1,2}, {3,4}

Final connected components:
Group 1: 0 - 1 - 2
Group 2: 3 - 4

Can answer queries like:
- connected(0, 2)? → Yes (same component)
- connected(0, 3)? → No (different components)
```

##### Time Complexity Analysis
```
Without optimizations:
Operation    | Time Complexity
-------------|----------------
find(x)      | O(n)
union(x,y)   | O(n)

With Union by Rank only:
Operation    | Time Complexity
-------------|----------------
find(x)      | O(log n)
union(x,y)   | O(log n)

With Path Compression + Union by Rank:
Operation    | Time Complexity
-------------|----------------
find(x)      | O(α(n)) ≈ O(1)
union(x,y)   | O(α(n)) ≈ O(1)

α(n) is the inverse Ackermann function, practically constant
```

### 3. Heavy-Light Decomposition

#### Use Cases
- Tree path queries
- Tree path updates
- LCA with path queries

#### Time Complexity
- Preprocessing: O(n)
- Query/Update: O(log�n)

### 4. Persistent Data Structures

#### Use Cases
- Version control systems
- Functional programming
- Time-travel debugging
- Maintaining multiple versions

### 5. Sqrt Decomposition (Mo's Algorithm)

#### Use Cases
- Offline range queries
- Problems where updates are expensive but queries can be reordered

#### Time Complexity
- O((n + q)n) where q is number of queries

#### Visualization
```
Array: [1, 3, 5, 7, 9, 11, 13, 15, 17]
Block size = √9 = 3

Decomposition:
Block 0: [1, 3, 5]     indices [0, 1, 2]
Block 1: [7, 9, 11]    indices [3, 4, 5] 
Block 2: [13, 15, 17]  indices [6, 7, 8]

For range query [1, 6]:
- Block 0: partial (index 1, 2) → sum = 3 + 5 = 8
- Block 1: complete → sum = 7 + 9 + 11 = 27  
- Block 2: partial (index 6) → sum = 13
Total: 8 + 27 + 13 = 48

Mo's Algorithm Query Ordering:
Sort queries by (left/√n, right) to minimize pointer movements
```

### 6. Treap (Randomized Binary Search Tree)

#### Use Cases
- When you need both BST operations and array-like operations
- Implicit treaps for sequence operations

#### Visualization
```
Treap combines BST property + Heap property
Each node has (key, priority)

Example treap:
       (15, 85)
      /        \
   (10, 60)   (20, 40)
   /    \        \
(5, 30) (12, 70) (25, 35)

BST property: left.key < node.key < right.key
Heap property: parent.priority > child.priority
```

### 7. Skip List

#### Use Cases
- Alternative to balanced trees
- Concurrent data structures
- Database indexing

#### Visualization
```
Skip List with values [3, 6, 7, 9, 12, 17, 19, 21, 25, 26]

Level 3: head -----------------------> null
Level 2: head ---------> 9 ----------> 21 -> null  
Level 1: head -> 6 ----> 9 -> 17 ----> 21 -> 25 -> null
Level 0: head -> 3 -> 6 -> 7 -> 9 -> 12 -> 17 -> 19 -> 21 -> 25 -> 26 -> null

Search path for 17:
Start at head, level 3 → down to level 2 → follow to 9
At 9, level 2 → down to level 1 → follow to 17 ✓

Expected time: O(log n)
```

---

## Interview Tips

### When to Use Each Data Structure

1. **Use Segment Tree when**:
   - Need range queries AND range updates
   - Complex aggregation functions (min, max, gcd, etc.)
   - Need lazy propagation

2. **Use Binary Indexed Tree when**:
   - Only need prefix sums or simple range sums
   - Point updates only
   - Memory is a concern (more space-efficient than segment tree)

3. **Use Prefix Sum when**:
   - Array is static (no updates)
   - Need O(1) range sum queries
   - Working with 2D matrices

4. **Use Trie when**:
   - String prefix operations
   - Dictionary/autocomplete problems
   - Bit manipulation problems (binary trie)

5. **Use Union-Find when**:
   - Dynamic connectivity problems
   - Grouping/clustering problems
   - Detecting cycles in undirected graphs

### Common Patterns to Recognize

1. **Range Query Problems** � Consider Segment Tree or BIT
2. **Prefix/Suffix Problems** � Consider Prefix Sum
3. **String Matching/Prefix** � Consider Trie
4. **Connected Components** � Consider Union-Find
5. **Coordinate Compression** � Use with BIT for large ranges
6. **Offline Queries** � Consider Mo's Algorithm or persistent data structures

### Implementation Tips

1. **Always consider the constraints**: Choose simpler solutions for smaller inputs
2. **Practice the basic implementations**: Know how to code them from scratch
3. **Understand the trade-offs**: Time vs Space, Query vs Update complexity
4. **Test edge cases**: Empty arrays, single elements, maximum constraints
5. **Optimize when needed**: Lazy propagation, path compression, etc.