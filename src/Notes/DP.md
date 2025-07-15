# Dynamic Programming Patterns for Google Interviews

## Table of Contents
1. [Introduction to Dynamic Programming](#introduction-to-dynamic-programming)
2. [When to Use Dynamic Programming](#when-to-use-dynamic-programming)
3. [How to Approach DP Problems](#how-to-approach-dp-problems)
4. [Linear DP Patterns](#linear-dp-patterns)
5. [2D DP Patterns](#2d-dp-patterns)
6. [Tree DP Patterns](#tree-dp-patterns)
7. [State Machine DP](#state-machine-dp)
8. [Knapsack Patterns](#knapsack-patterns)
9. [Subsequence Patterns](#subsequence-patterns)
10. [Overlapping Subproblems Visualization](#overlapping-subproblems-visualization)
11. [Google Interview Examples](#google-interview-examples)

---

## Introduction to Dynamic Programming

Dynamic Programming (DP) is an algorithmic paradigm that solves complex problems by breaking them down into simpler subproblems. It stores the results of subproblems to avoid computing the same results again.

### Key Characteristics
1. **Overlapping Subproblems**: The problem can be broken down into subproblems which are reused several times
2. **Optimal Substructure**: An optimal solution can be constructed from optimal solutions of its subproblems

### DP vs Recursion vs Greedy
```
Recursion: Solves subproblems repeatedly
DP: Solves each subproblem once and stores result
Greedy: Makes locally optimal choice at each step
```

---

## When to Use Dynamic Programming

### Recognition Patterns
1. **Optimization Problems**: Find maximum/minimum, count ways
2. **Decision Problems**: Can we achieve a certain goal?
3. **Counting Problems**: How many ways to do something?

### Key Indicators
- Problem asks for optimal solution (max/min)
- Problem asks for number of ways to do something
- Problem involves making choices at each step
- Future decisions depend on earlier decisions
- Problem has overlapping subproblems

### Problem Categories
```
1. Grid/Path Problems ’ 2D DP
2. String Problems ’ Linear/2D DP  
3. Tree Problems ’ Tree DP
4. Game Theory ’ Minimax DP
5. Resource Allocation ’ Knapsack DP
6. Sequence Problems ’ Linear DP
```

---

## How to Approach DP Problems

### Step-by-Step Framework

#### 1. Identify if it's a DP Problem
- Look for optimization/counting
- Check for overlapping subproblems
- Verify optimal substructure

#### 2. Define the State
- What parameters uniquely identify a subproblem?
- What's the meaning of dp[i] or dp[i][j]?

#### 3. Write the Recurrence Relation
- How does current state relate to previous states?
- What are the base cases?

#### 4. Determine the Order of Computation
- Top-down (Memoization) vs Bottom-up (Tabulation)
- What order to fill the DP table?

#### 5. Optimize Space (if needed)
- Can we reduce space complexity?
- Rolling array technique

### Template Code Structure
```java
// Top-down (Memoization)
public int solve(params) {
    if (base_case) return base_value;
    if (memo[state] != -1) return memo[state];
    
    int result = 0;
    // Try all possible transitions
    for (choice in choices) {
        result = optimizeFunction(result, solve(newParams));
    }
    
    return memo[state] = result;
}

// Bottom-up (Tabulation)
public int solve(params) {
    int[][] dp = new int[n][m];
    
    // Initialize base cases
    // Fill DP table in correct order
    for (int i = 0; i < n; i++) {
        for (int j = 0; j < m; j++) {
            // Apply recurrence relation
            dp[i][j] = optimizeFunction(transitions);
        }
    }
    
    return dp[finalState];
}
```

---

## Linear DP Patterns

### Pattern 1: Single Sequence DP
**State**: `dp[i]` = optimal solution for first i elements
**Transition**: `dp[i] = f(dp[i-1], dp[i-2], ..., arr[i])`

#### Example: House Robber
```java
// Problem: Maximum money without robbing adjacent houses
// dp[i] = max money from houses 0 to i

public int rob(int[] nums) {
    if (nums.length == 0) return 0;
    if (nums.length == 1) return nums[0];
    
    int[] dp = new int[nums.length];
    dp[0] = nums[0];
    dp[1] = Math.max(nums[0], nums[1]);
    
    for (int i = 2; i < nums.length; i++) {
        dp[i] = Math.max(dp[i-1], dp[i-2] + nums[i]);
    }
    
    return dp[nums.length - 1];
}
```

#### Visualization: House Robber
```
Houses: [2, 7, 9, 3, 1]
Indices: 0  1  2  3  4

DP Table:
i=0: dp[0] = 2 (rob house 0)
i=1: dp[1] = max(2, 7) = 7 (rob house 1)
i=2: dp[2] = max(7, 2+9) = 11 (rob houses 0,2)
i=3: dp[3] = max(11, 7+3) = 11 (rob houses 0,2)
i=4: dp[4] = max(11, 11+1) = 12 (rob houses 0,2,4)

Decision tree for house 4:
    rob[4]
   /      \
rob      don't rob
dp[2]+1    dp[3]
11+1=12     11
```

### Pattern 2: Climbing Stairs DP
**State**: `dp[i]` = number of ways to reach step i
**Transition**: `dp[i] = dp[i-1] + dp[i-2]`

#### Example: Coin Change
```java
// Problem: Minimum coins to make amount
// dp[i] = minimum coins to make amount i

public int coinChange(int[] coins, int amount) {
    int[] dp = new int[amount + 1];
    Arrays.fill(dp, amount + 1);
    dp[0] = 0;
    
    for (int i = 1; i <= amount; i++) {
        for (int coin : coins) {
            if (coin <= i) {
                dp[i] = Math.min(dp[i], dp[i - coin] + 1);
            }
        }
    }
    
    return dp[amount] > amount ? -1 : dp[amount];
}
```

### Pattern 3: Maximum Subarray DP (Kadane's Algorithm)
**State**: `dp[i]` = maximum sum ending at position i
**Transition**: `dp[i] = max(nums[i], dp[i-1] + nums[i])`

#### Example: Maximum Subarray Sum
```java
public int maxSubArray(int[] nums) {
    int maxSoFar = nums[0];
    int maxEndingHere = nums[0];
    
    for (int i = 1; i < nums.length; i++) {
        maxEndingHere = Math.max(nums[i], maxEndingHere + nums[i]);
        maxSoFar = Math.max(maxSoFar, maxEndingHere);
    }
    
    return maxSoFar;
}
```

#### Visualization: Maximum Subarray
```
Array: [-2, 1, -3, 4, -1, 2, 1, -5, 4]
       
Step-by-step:
i=0: maxHere=-2, maxSoFar=-2
i=1: maxHere=max(1, -2+1)=1, maxSoFar=1
i=2: maxHere=max(-3, 1-3)=-2, maxSoFar=1
i=3: maxHere=max(4, -2+4)=4, maxSoFar=4
i=4: maxHere=max(-1, 4-1)=3, maxSoFar=4
i=5: maxHere=max(2, 3+2)=5, maxSoFar=5
i=6: maxHere=max(1, 5+1)=6, maxSoFar=6
i=7: maxHere=max(-5, 6-5)=1, maxSoFar=6
i=8: maxHere=max(4, 1+4)=5, maxSoFar=6

Optimal subarray: [4, -1, 2, 1] = 6
```

---

## 2D DP Patterns

### Pattern 1: Grid Path DP
**State**: `dp[i][j]` = optimal solution to reach cell (i,j)
**Transition**: `dp[i][j] = f(dp[i-1][j], dp[i][j-1])`

#### Example: Unique Paths
```java
// Problem: Number of paths from top-left to bottom-right
public int uniquePaths(int m, int n) {
    int[][] dp = new int[m][n];
    
    // Base cases
    for (int i = 0; i < m; i++) dp[i][0] = 1;
    for (int j = 0; j < n; j++) dp[0][j] = 1;
    
    for (int i = 1; i < m; i++) {
        for (int j = 1; j < n; j++) {
            dp[i][j] = dp[i-1][j] + dp[i][j-1];
        }
    }
    
    return dp[m-1][n-1];
}
```

#### Visualization: Unique Paths (3x3 grid)
```
Grid visualization:
S . . F
. . .
. . .

DP Table:
1  1  1
1  2  3
1  3  6

Path calculation for dp[2][2]:
- From dp[1][2] = 3 (paths from above)
- From dp[2][1] = 3 (paths from left)
- Total = 3 + 3 = 6 paths

All possible paths:
RRD, RDR, DRR (where R=right, D=down)
And 3 more paths = 6 total
```

### Pattern 2: String Matching DP
**State**: `dp[i][j]` = optimal solution for first i chars of s1 and first j chars of s2
**Transition**: Based on character match/mismatch

#### Example: Edit Distance
```java
// Problem: Minimum operations to convert word1 to word2
public int minDistance(String word1, String word2) {
    int m = word1.length(), n = word2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    // Base cases
    for (int i = 0; i <= m; i++) dp[i][0] = i;
    for (int j = 0; j <= n; j++) dp[0][j] = j;
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (word1.charAt(i-1) == word2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1];
            } else {
                dp[i][j] = 1 + Math.min(dp[i-1][j-1], 
                    Math.min(dp[i-1][j], dp[i][j-1]));
            }
        }
    }
    
    return dp[m][n];
}
```

#### Visualization: Edit Distance
```
word1 = "horse", word2 = "ros"

DP Table:
    ""  r  o  s
""   0  1  2  3
h    1  1  2  3
o    2  2  1  2
r    3  2  2  2
s    4  3  3  2
e    5  4  4  3

For dp[5][3] (horse ’ ros):
- dp[4][2] + 1 = 3 + 1 = 4 (delete 'e')
- dp[4][3] + 1 = 2 + 1 = 3 (insert 's')
- dp[5][2] + 1 = 4 + 1 = 5 (replace 'e' with 's')
Minimum = 3 operations
```

### Pattern 3: Range DP
**State**: `dp[i][j]` = optimal solution for range [i, j]
**Transition**: Try all possible split points k between i and j

#### Example: Matrix Chain Multiplication
```java
public int matrixChainOrder(int[] p) {
    int n = p.length - 1;
    int[][] dp = new int[n][n];
    
    for (int len = 2; len <= n; len++) {
        for (int i = 0; i < n - len + 1; i++) {
            int j = i + len - 1;
            dp[i][j] = Integer.MAX_VALUE;
            for (int k = i; k < j; k++) {
                int cost = dp[i][k] + dp[k+1][j] + p[i]*p[k+1]*p[j+1];
                dp[i][j] = Math.min(dp[i][j], cost);
            }
        }
    }
    
    return dp[0][n-1];
}
```

---

## Tree DP Patterns

### Pattern 1: Binary Tree DP
**State**: For each node, compute optimal solution for subtree rooted at that node
**Transition**: Combine results from left and right children

#### Example: Binary Tree Maximum Path Sum
```java
private int maxSum = Integer.MIN_VALUE;

public int maxPathSum(TreeNode root) {
    maxGain(root);
    return maxSum;
}

private int maxGain(TreeNode node) {
    if (node == null) return 0;
    
    int leftGain = Math.max(maxGain(node.left), 0);
    int rightGain = Math.max(maxGain(node.right), 0);
    
    int currentMax = node.val + leftGain + rightGain;
    maxSum = Math.max(maxSum, currentMax);
    
    return node.val + Math.max(leftGain, rightGain);
}
```

#### Visualization: Tree DP
```
Tree:
    1
   / \
  2   3
 / \
4   5

For each node, compute:
1. Max gain going through this node
2. Max path sum in subtree

Node 4: gain=4, maxPath=4
Node 5: gain=5, maxPath=5
Node 2: gain=7 (2+max(4,5)), maxPath=11 (2+4+5)
Node 3: gain=3, maxPath=3
Node 1: gain=8 (1+max(7,3)), maxPath=11 (1+7+3)

Global maxPath = 11 (path: 4’2’5)
```

### Pattern 2: Tree with States DP
**State**: For each node, maintain multiple states (robbed/not robbed, etc.)

#### Example: House Robber III
```java
public int rob(TreeNode root) {
    int[] result = robSub(root);
    return Math.max(result[0], result[1]);
}

private int[] robSub(TreeNode root) {
    if (root == null) return new int[2];
    
    int[] left = robSub(root.left);
    int[] right = robSub(root.right);
    
    int[] result = new int[2];
    result[0] = Math.max(left[0], left[1]) + Math.max(right[0], right[1]); // not rob
    result[1] = root.val + left[0] + right[0]; // rob
    
    return result;
}
```

---

## State Machine DP

### Pattern: Multiple States per Position
**State**: `dp[i][state]` = optimal solution at position i in given state
**Transition**: Consider state transitions

#### Example: Buy/Sell Stock with Cooldown
```java
public int maxProfit(int[] prices) {
    if (prices.length <= 1) return 0;
    
    int[] hold = new int[prices.length];
    int[] sold = new int[prices.length];
    int[] rest = new int[prices.length];
    
    hold[0] = -prices[0];
    
    for (int i = 1; i < prices.length; i++) {
        hold[i] = Math.max(hold[i-1], rest[i-1] - prices[i]);
        sold[i] = hold[i-1] + prices[i];
        rest[i] = Math.max(rest[i-1], sold[i-1]);
    }
    
    return Math.max(sold[prices.length-1], rest[prices.length-1]);
}
```

#### Visualization: State Machine
```
States: Hold (have stock), Sold (just sold), Rest (no stock, can buy)

State transitions:
Hold ’ Sold (sell stock)
Sold ’ Rest (cooldown day)
Rest ’ Hold (buy stock) or Rest ’ Rest (stay idle)

For prices = [1,2,3,0,2]:
Day 0: Hold=-1, Sold=0, Rest=0
Day 1: Hold=-1, Sold=1, Rest=0
Day 2: Hold=-1, Sold=2, Rest=1
Day 3: Hold=1, Sold=-1, Rest=2
Day 4: Hold=1, Sold=3, Rest=2

Max profit = 3
```

---

## Knapsack Patterns

### Pattern 1: 0/1 Knapsack
**State**: `dp[i][w]` = maximum value using first i items with weight limit w
**Transition**: Include or exclude current item

#### Example: 0/1 Knapsack
```java
public int knapsack(int[] weights, int[] values, int capacity) {
    int n = weights.length;
    int[][] dp = new int[n + 1][capacity + 1];
    
    for (int i = 1; i <= n; i++) {
        for (int w = 1; w <= capacity; w++) {
            if (weights[i-1] <= w) {
                dp[i][w] = Math.max(
                    dp[i-1][w], 
                    dp[i-1][w - weights[i-1]] + values[i-1]
                );
            } else {
                dp[i][w] = dp[i-1][w];
            }
        }
    }
    
    return dp[n][capacity];
}
```

### Pattern 2: Unbounded Knapsack
**State**: `dp[w]` = maximum value with weight limit w
**Transition**: Try all items that fit

#### Example: Coin Change (Unbounded)
```java
public int change(int amount, int[] coins) {
    int[] dp = new int[amount + 1];
    dp[0] = 1;
    
    for (int coin : coins) {
        for (int i = coin; i <= amount; i++) {
            dp[i] += dp[i - coin];
        }
    }
    
    return dp[amount];
}
```

---

## Subsequence Patterns

### Pattern 1: Longest Common Subsequence (LCS)
**State**: `dp[i][j]` = LCS length for first i chars of s1 and first j chars of s2
**Transition**: Match or skip characters

#### Example: LCS
```java
public int longestCommonSubsequence(String text1, String text2) {
    int m = text1.length(), n = text2.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (text1.charAt(i-1) == text2.charAt(j-1)) {
                dp[i][j] = dp[i-1][j-1] + 1;
            } else {
                dp[i][j] = Math.max(dp[i-1][j], dp[i][j-1]);
            }
        }
    }
    
    return dp[m][n];
}
```

### Pattern 2: Longest Increasing Subsequence (LIS)
**State**: `dp[i]` = length of LIS ending at position i
**Transition**: Extend previous increasing subsequences

#### Example: LIS
```java
public int lengthOfLIS(int[] nums) {
    int[] dp = new int[nums.length];
    Arrays.fill(dp, 1);
    
    for (int i = 1; i < nums.length; i++) {
        for (int j = 0; j < i; j++) {
            if (nums[j] < nums[i]) {
                dp[i] = Math.max(dp[i], dp[j] + 1);
            }
        }
    }
    
    return Arrays.stream(dp).max().orElse(0);
}
```

---

## Overlapping Subproblems Visualization

### Fibonacci Example: Overlapping Subproblems
```
Computing fib(5) recursively:

                 fib(5)
               /        \
           fib(4)        fib(3)
          /      \      /      \
      fib(3)   fib(2) fib(2)  fib(1)
     /    \    /   \   /   \
  fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)
  /   \
fib(1) fib(0)

Subproblems computed multiple times:
- fib(3): 2 times
- fib(2): 3 times  
- fib(1): 5 times
- fib(0): 3 times

Total recursive calls: 15
With memoization: 6 calls (fib(0) to fib(5))
```

### Memoization vs Tabulation
```
Top-down (Memoization):
- Start from original problem
- Recursively solve subproblems
- Store results in memo table
- Good for problems where not all subproblems are needed

Bottom-up (Tabulation):
- Start from base cases
- Build up to original problem
- Fill DP table iteratively
- Good when all subproblems are needed
- Usually more space efficient
```

### Space Optimization Techniques
```
1. Rolling Array:
   Instead of dp[n][m], use dp[2][m] when only previous row needed

2. 1D Array:
   When current state only depends on previous state

3. Variables:
   When only O(1) previous states needed

Example - Fibonacci:
O(n) space: dp[i] = dp[i-1] + dp[i-2]
O(1) space: prev2, prev1, current = prev1 + prev2
```

---

## Google Interview Examples

### Problem 1: Paint House (Google)
```java
// Houses in a row, 3 colors, adjacent houses can't have same color
// Minimize cost
public int minCost(int[][] costs) {
    if (costs.length == 0) return 0;
    
    int n = costs.length;
    int[][] dp = new int[n][3];
    dp[0] = costs[0].clone();
    
    for (int i = 1; i < n; i++) {
        dp[i][0] = costs[i][0] + Math.min(dp[i-1][1], dp[i-1][2]);
        dp[i][1] = costs[i][1] + Math.min(dp[i-1][0], dp[i-1][2]);
        dp[i][2] = costs[i][2] + Math.min(dp[i-1][0], dp[i-1][1]);
    }
    
    return Math.min(dp[n-1][0], Math.min(dp[n-1][1], dp[n-1][2]));
}
```

### Problem 2: Word Break (Google)
```java
// Check if string can be segmented using dictionary words
public boolean wordBreak(String s, List<String> wordDict) {
    Set<String> dict = new HashSet<>(wordDict);
    boolean[] dp = new boolean[s.length() + 1];
    dp[0] = true;
    
    for (int i = 1; i <= s.length(); i++) {
        for (int j = 0; j < i; j++) {
            if (dp[j] && dict.contains(s.substring(j, i))) {
                dp[i] = true;
                break;
            }
        }
    }
    
    return dp[s.length()];
}
```

### Problem 3: Regular Expression Matching (Google)
```java
// Match string with pattern containing '.' and '*'
public boolean isMatch(String s, String p) {
    int m = s.length(), n = p.length();
    boolean[][] dp = new boolean[m + 1][n + 1];
    dp[0][0] = true;
    
    // Handle patterns like a*, a*b*, etc.
    for (int j = 2; j <= n; j += 2) {
        if (p.charAt(j - 1) == '*') {
            dp[0][j] = dp[0][j - 2];
        }
    }
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            char sc = s.charAt(i - 1);
            char pc = p.charAt(j - 1);
            
            if (pc == '*') {
                dp[i][j] = dp[i][j - 2] || 
                    (matches(sc, p.charAt(j - 2)) && dp[i - 1][j]);
            } else {
                dp[i][j] = matches(sc, pc) && dp[i - 1][j - 1];
            }
        }
    }
    
    return dp[m][n];
}

private boolean matches(char s, char p) {
    return p == '.' || s == p;
}
```

### Problem 4: Minimum Window Subsequence (Google)
```java
// Find minimum window in S that contains T as subsequence
public String minWindow(String S, String T) {
    int m = S.length(), n = T.length();
    int[][] dp = new int[m + 1][n + 1];
    
    for (int i = 0; i <= m; i++) {
        dp[i][0] = i;
    }
    
    for (int i = 1; i <= m; i++) {
        for (int j = 1; j <= n; j++) {
            if (S.charAt(i - 1) == T.charAt(j - 1)) {
                dp[i][j] = dp[i - 1][j - 1];
            } else {
                dp[i][j] = dp[i - 1][j];
            }
        }
    }
    
    int start = 0, len = m + 1;
    for (int i = 1; i <= m; i++) {
        if (dp[i][n] != 0) {
            if (i - dp[i][n] + 1 < len) {
                start = dp[i][n] - 1;
                len = i - dp[i][n] + 1;
            }
        }
    }
    
    return len == m + 1 ? "" : S.substring(start, start + len);
}
```

### Common Google DP Interview Tips

#### Pattern Recognition
1. **Optimization**: "Find minimum/maximum..."
2. **Counting**: "How many ways..."
3. **Decision**: "Can we achieve..."
4. **Sequences**: String/array problems
5. **Games**: Two players, optimal strategy

#### Time Complexity Analysis
```
1D DP: O(n) states × O(k) transitions = O(nk)
2D DP: O(n²) states × O(k) transitions = O(n²k)
Tree DP: O(n) nodes × O(k) per node = O(nk)
```

#### Space Optimization Strategies
1. Rolling array when only previous row/column needed
2. Single array when processing in specific order
3. Constant space when only few previous values needed

#### Interview Strategy
1. **Clarify**: Ask about constraints, edge cases
2. **Brute Force**: Start with recursive solution
3. **Optimize**: Add memoization
4. **Iterate**: Convert to bottom-up if asked
5. **Space**: Optimize space complexity if possible