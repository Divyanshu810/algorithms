# Recursion and Backtracking - In-Depth Guide

## Table of Contents
1. [Understanding Recursion](#understanding-recursion)
2. [Recursion Trees - Visual Guide](#recursion-trees---visual-guide)
3. [Recursion Fundamentals](#recursion-fundamentals)
4. [Types of Recursion](#types-of-recursion)
5. [Backtracking Introduction](#backtracking-introduction)
6. [Backtracking Template](#backtracking-template)
7. [Common Patterns](#common-patterns)
8. [Practice Problems](#practice-problems)
9. [Optimization Techniques](#optimization-techniques)

## Understanding Recursion

### What is Recursion?
Recursion is a programming technique where a function calls itself to solve a smaller instance of the same problem. It's based on the mathematical concept of mathematical induction.

### Core Components
1. **Base Case**: The condition that stops the recursion
2. **Recursive Case**: The function calling itself with modified parameters
3. **Progress**: Each recursive call must move toward the base case

### Recursion Call Stack
```
factorial(4)
├── 4 * factorial(3)
    ├── 3 * factorial(2)
        ├── 2 * factorial(1)
            ├── 1 * factorial(0)
                └── return 1  // Base case
            └── return 1 * 1 = 1
        └── return 2 * 1 = 2
    └── return 3 * 2 = 6
└── return 4 * 6 = 24
```

## Recursion Trees - Visual Guide

### Understanding Recursion Trees
Recursion trees are visual representations that help understand:
- **Function call hierarchy**
- **Parameter flow between calls**
- **Base case termination**
- **Return value propagation**
- **Time and space complexity analysis**

### Key Elements in Recursion Trees
1. **Nodes**: Represent function calls with parameters
2. **Edges**: Show parent-child relationships between calls
3. **Leaves**: Base cases that terminate recursion
4. **Depth**: Maximum recursion depth (stack space)
5. **Width**: Number of recursive calls at each level

### Reading Recursion Trees
- **Top-down**: Shows how problems break into subproblems
- **Bottom-up**: Shows how solutions combine from base cases
- **Left-to-right**: Order of recursive calls (usually)

### Tree Analysis Patterns

#### Linear Recursion Tree (Single Branch)
```
f(n) → f(n-1) → f(n-2) → ... → f(1) → f(0)
Depth: O(n)
Total calls: O(n)
```

#### Binary Recursion Tree (Two Branches)
```
        f(n)
       /    \
    f(n-1)  f(n-2)
    /  \    /   \
   ...  ... ... ...
Depth: O(n)
Total calls: O(2^n) worst case
```

#### Divide & Conquer Tree (Balanced)
```
      f(n)
     /    \
  f(n/2)  f(n/2)
   / \     / \
  ... ... ... ...
Depth: O(log n)
Total calls: O(n)
```

## Recursion Fundamentals

### 1. Basic Recursion Pattern
```java
public ReturnType recursiveFunction(parameters) {
    // Base case - stopping condition
    if (baseCondition) {
        return baseValue;
    }
    
    // Recursive case - function calls itself
    return recursiveFunction(modifiedParameters);
}
```

### 2. Simple Examples

#### Factorial
```java
public int factorial(int n) {
    // Base case
    if (n <= 1) {
        return 1;
    }
    
    // Recursive case
    return n * factorial(n - 1);
}
```

#### Fibonacci
```java
public int fibonacci(int n) {
    // Base cases
    if (n <= 1) {
        return n;
    }
    
    // Recursive case
    return fibonacci(n - 1) + fibonacci(n - 2);
}
```

**Recursion Tree for fibonacci(5):**
```
                    fib(5)
                   /      \
               fib(4)      fib(3)
              /     \      /     \
          fib(3)   fib(2) fib(2) fib(1)
         /    \    /   \   /   \    |
     fib(2) fib(1) fib(1) fib(0) fib(1) fib(0)  1
     /   \    |     |     |     |     |
  fib(1) fib(0) 1   1     0     1     0
    |     |
    1     0

Result: fib(5) = 5
Time Complexity: O(2^n) - Exponential due to overlapping subproblems
Space Complexity: O(n) - Maximum depth of recursion tree
```

#### Binary Tree Traversal
```java
public void inorderTraversal(TreeNode root) {
    // Base case
    if (root == null) {
        return;
    }
    
    // Recursive cases
    inorderTraversal(root.left);   // Process left subtree
    System.out.print(root.val + " "); // Process current node
    inorderTraversal(root.right);  // Process right subtree
}
```

## Types of Recursion

### 1. Linear Recursion
Function makes at most one recursive call.
```java
// Example: Finding sum of array
public int arraySum(int[] arr, int index) {
    if (index >= arr.length) return 0;
    return arr[index] + arraySum(arr, index + 1);
}
```

### 2. Binary Recursion
Function makes two recursive calls.
```java
// Example: Fibonacci
public int fibonacci(int n) {
    if (n <= 1) return n;
    return fibonacci(n - 1) + fibonacci(n - 2);
}
```

**Recursion Tree for Binary Recursion (fibonacci(4)):**
```
                fib(4)
               /      \
           fib(3)      fib(2)
          /     \      /     \
      fib(2)   fib(1) fib(1) fib(0)
      /   \      |      |      |
  fib(1) fib(0)  1      1      0
    |      |
    1      0

Each node represents a function call
Total calls: 9 (exponential growth)
```

### 3. Tail Recursion
Recursive call is the last operation in the function.
```java
// Tail recursive factorial
public int factorialTail(int n, int accumulator) {
    if (n <= 1) return accumulator;
    return factorialTail(n - 1, n * accumulator);
}
```

### 4. Mutual Recursion
Functions call each other recursively.
```java
public boolean isEven(int n) {
    if (n == 0) return true;
    return isOdd(n - 1);
}

public boolean isOdd(int n) {
    if (n == 0) return false;
    return isEven(n - 1);
}
```

## Backtracking Introduction

### What is Backtracking?
Backtracking is an algorithmic approach that considers searching every possible combination to solve computational problems. It builds solutions incrementally and abandons ("backtracks") partial solutions that cannot lead to a valid solution.

### Key Characteristics
1. **Incremental Construction**: Build solution step by step
2. **Constraint Checking**: Validate partial solutions
3. **Backtrack**: Undo choices that don't lead to solutions
4. **Exhaustive Search**: Explore all possibilities systematically

### Backtracking vs Brute Force
- **Brute Force**: Generate all possible solutions, then check validity
- **Backtracking**: Check constraints at each step, prune invalid paths early

## Backtracking Template

### Generic Backtracking Template
```java
public void backtrack(state, choices, result) {
    // Base case - solution found
    if (isValidSolution(state)) {
        result.add(new ArrayList<>(state));
        return;
    }
    
    // Try all possible choices
    for (choice : choices) {
        // Check if choice is valid
        if (isValidChoice(state, choice)) {
            // Make choice
            state.add(choice);
            
            // Recursively explore
            backtrack(state, getNextChoices(state), result);
            
            // Backtrack - undo choice
            state.remove(state.size() - 1);
        }
    }
}
```

### Detailed Template with Optimizations
```java
public class BacktrackingSolver {
    private List<List<Integer>> result = new ArrayList<>();
    private List<Integer> path = new ArrayList<>();
    
    public List<List<Integer>> solve(int[] candidates, int target) {
        Arrays.sort(candidates); // Often helpful for pruning
        backtrack(candidates, target, 0);
        return result;
    }
    
    private void backtrack(int[] candidates, int target, int start) {
        // Base case - solution found
        if (target == 0) {
            result.add(new ArrayList<>(path));
            return;
        }
        
        // Pruning - if target becomes negative, no point continuing
        if (target < 0) {
            return;
        }
        
        for (int i = start; i < candidates.length; i++) {
            // Skip duplicates (if sorted array)
            if (i > start && candidates[i] == candidates[i - 1]) {
                continue;
            }
            
            // Make choice
            path.add(candidates[i]);
            
            // Recurse with updated target
            backtrack(candidates, target - candidates[i], i + 1);
            
            // Backtrack
            path.remove(path.size() - 1);
        }
    }
}
```

## Common Patterns

### 1. Permutations Pattern
```java
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    boolean[] used = new boolean[nums.length];
    
    backtrack(nums, path, used, result);
    return result;
}

private void backtrack(int[] nums, List<Integer> path, 
                      boolean[] used, List<List<Integer>> result) {
    // Base case
    if (path.size() == nums.length) {
        result.add(new ArrayList<>(path));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (used[i]) continue;
        
        // Make choice
        path.add(nums[i]);
        used[i] = true;
        
        // Recurse
        backtrack(nums, path, used, result);
        
        // Backtrack
        path.remove(path.size() - 1);
        used[i] = false;
    }
}
```

**Recursion Tree for Permutations of [1,2,3]:**
```
                        permute([1,2,3])
                        path=[], used=[]
                        /       |       \
                  choose 1    choose 2   choose 3
                 path=[1]     path=[2]   path=[3]
                used=[T,F,F] used=[F,T,F] used=[F,F,T]
                   /    \       /    \       /    \
              choose 2  choose 3  choose 1  choose 3  choose 1  choose 2
             path=[1,2] path=[1,3] path=[2,1] path=[2,3] path=[3,1] path=[3,2]
            used=[T,T,F] used=[T,F,T] used=[T,T,F] used=[F,T,T] used=[T,F,T] used=[F,T,T]
                 |         |         |         |         |         |
            choose 3   choose 2   choose 3   choose 1   choose 2   choose 1
           path=[1,2,3] path=[1,3,2] path=[2,1,3] path=[2,3,1] path=[3,1,2] path=[3,2,1]
          used=[T,T,T] used=[T,T,T] used=[T,T,T] used=[T,T,T] used=[T,T,T] used=[T,T,T]
              BASE        BASE        BASE        BASE        BASE        BASE

Final Result: [[1,2,3], [1,3,2], [2,1,3], [2,3,1], [3,1,2], [3,2,1]]
Time Complexity: O(n! × n)
Space Complexity: O(n) - recursion depth
```

### 2. Combinations Pattern
```java
public List<List<Integer>> combine(int n, int k) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    
    backtrack(1, n, k, path, result);
    return result;
}

private void backtrack(int start, int n, int k, 
                      List<Integer> path, List<List<Integer>> result) {
    // Base case
    if (path.size() == k) {
        result.add(new ArrayList<>(path));
        return;
    }
    
    for (int i = start; i <= n; i++) {
        // Make choice
        path.add(i);
        
        // Recurse
        backtrack(i + 1, n, k, path, result);
        
        // Backtrack
        path.remove(path.size() - 1);
    }
}
```

### 3. Subset Pattern
```java
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    List<Integer> path = new ArrayList<>();
    
    backtrack(nums, 0, path, result);
    return result;
}

private void backtrack(int[] nums, int start, 
                      List<Integer> path, List<List<Integer>> result) {
    // Add current subset to result
    result.add(new ArrayList<>(path));
    
    for (int i = start; i < nums.length; i++) {
        // Make choice
        path.add(nums[i]);
        
        // Recurse
        backtrack(nums, i + 1, path, result);
        
        // Backtrack
        path.remove(path.size() - 1);
    }
}
```

**Recursion Tree for Subsets of [1,2,3]:**
```
                    subsets([1,2,3])
                    path=[], start=0
                    ADD [] to result
                    /       |       \
               include 1  include 2  include 3
              path=[1]    path=[2]   path=[3]
              start=1     start=2    start=3
              ADD [1]     ADD [2]    ADD [3]
              /    \         |         BASE
         include 2  include 3  include 3
        path=[1,2]  path=[1,3]  path=[2,3]
        start=2     start=3     start=3
        ADD [1,2]   ADD [1,3]   ADD [2,3]
            |        BASE       BASE
       include 3
      path=[1,2,3]
      start=3
      ADD [1,2,3]
          BASE

Final Result: [[], [1], [2], [3], [1,2], [1,3], [2,3], [1,2,3]]
Time Complexity: O(2^n × n)
Space Complexity: O(n) - recursion depth

Note: Each node adds current path to result, then explores including remaining elements
```

### 4. N-Queens Problem
```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    char[][] board = new char[n][n];
    
    // Initialize board
    for (int i = 0; i < n; i++) {
        Arrays.fill(board[i], '.');
    }
    
    backtrack(board, 0, result);
    return result;
}

private void backtrack(char[][] board, int row, List<List<String>> result) {
    // Base case - all queens placed
    if (row == board.length) {
        result.add(construct(board));
        return;
    }
    
    for (int col = 0; col < board.length; col++) {
        if (isValid(board, row, col)) {
            // Make choice
            board[row][col] = 'Q';
            
            // Recurse
            backtrack(board, row + 1, result);
            
            // Backtrack
            board[row][col] = '.';
        }
    }
}

private boolean isValid(char[][] board, int row, int col) {
    int n = board.length;
    
    // Check column
    for (int i = 0; i < row; i++) {
        if (board[i][col] == 'Q') return false;
    }
    
    // Check diagonal (top-left to bottom-right)
    for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
        if (board[i][j] == 'Q') return false;
    }
    
    // Check diagonal (top-right to bottom-left)
    for (int i = row - 1, j = col + 1; i >= 0 && j < n; i--, j++) {
        if (board[i][j] == 'Q') return false;
    }
    
    return true;
}
```

**Recursion Tree for 4-Queens Problem:**
```
                        4-Queens(row=0)
                        board = [. . . .]
                               [. . . .]
                               [. . . .]
                               [. . . .]
                        /    |    |    \
                   col=0   col=1  col=2  col=3
                 Q. . .   .Q. .  . .Q.  . . .Q
                 . . . .  . . .  . . .  . . . .
                 . . . .  . . .  . . .  . . . .
                 . . . .  . . .  . . .  . . . .
                     |        |      |      |
                4-Queens   4-Queens  4-Queens  4-Queens
                (row=1)    (row=1)   (row=1)   (row=1)
                   |          |        |        |
               Try cols    Try cols  Try cols  Try cols
               2,3 fail    0,3 fail  0,1 fail  0,1,2 fail
                   ↓          ↓        ↓        ↓
               BACKTRACK  BACKTRACK BACKTRACK BACKTRACK

For the successful path (starting with Queen at col=1):
.Q. .     row=0, col=1 ✓
. . .Q    row=1, col=3 ✓ (not attacking (0,1))
Q. . .    row=2, col=0 ✓ (not attacking (0,1) or (1,3))
. .Q.     row=3, col=2 ✓ (not attacking previous queens)

Solution found!

Key insights:
- Each level represents placing a queen in a specific row
- Pruning happens when queens attack each other
- Backtrack immediately when constraint violated
- Time Complexity: O(n!) with heavy pruning
```

### 5. Word Search in Grid
```java
public boolean exist(char[][] board, String word) {
    int m = board.length, n = board[0].length;
    
    for (int i = 0; i < m; i++) {
        for (int j = 0; j < n; j++) {
            if (backtrack(board, word, i, j, 0)) {
                return true;
            }
        }
    }
    return false;
}

private boolean backtrack(char[][] board, String word, int i, int j, int index) {
    // Base case - word found
    if (index == word.length()) {
        return true;
    }
    
    // Boundary checks and character match
    if (i < 0 || i >= board.length || j < 0 || j >= board[0].length || 
        board[i][j] != word.charAt(index)) {
        return false;
    }
    
    // Make choice - mark as visited
    char temp = board[i][j];
    board[i][j] = '#';
    
    // Explore all 4 directions
    boolean found = backtrack(board, word, i + 1, j, index + 1) ||
                   backtrack(board, word, i - 1, j, index + 1) ||
                   backtrack(board, word, i, j + 1, index + 1) ||
                   backtrack(board, word, i, j - 1, index + 1);
    
    // Backtrack - restore original character
    board[i][j] = temp;
    
    return found;
}
```

## Practice Problems

### Beginner Level
1. **Generate Parentheses**: Generate all valid combinations of n pairs of parentheses
2. **Letter Combinations**: Phone number to letter combinations
3. **Palindrome Partitioning**: Partition string into palindromes

### Intermediate Level
1. **Sudoku Solver**: Solve a 9x9 Sudoku puzzle
2. **Word Break II**: Return all possible sentences from word break
3. **Restore IP Addresses**: Generate all valid IP addresses

### Advanced Level
1. **N-Queens II**: Count number of solutions for N-Queens
2. **Expression Add Operators**: Add binary operators to get target
3. **Remove Invalid Parentheses**: Remove minimum parentheses to make valid

## Optimization Techniques

### 1. Pruning
```java
// Example: Early termination in combination sum
if (target < 0) {
    return; // No point continuing if target becomes negative
}

// Skip duplicates
if (i > start && candidates[i] == candidates[i - 1]) {
    continue;
}
```

### 2. Memoization
```java
// Cache results to avoid recomputation
Map<String, List<String>> memo = new HashMap<>();

private List<String> backtrack(String s, Set<String> wordDict) {
    if (memo.containsKey(s)) {
        return memo.get(s);
    }
    
    List<String> result = new ArrayList<>();
    // ... backtracking logic ...
    
    memo.put(s, result);
    return result;
}
```

### 3. State Space Reduction
```java
// Use bit manipulation for visited states
private void backtrack(int[] nums, int used, List<Integer> path) {
    if (Integer.bitCount(used) == nums.length) {
        // All numbers used
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if ((used & (1 << i)) == 0) { // Not used
            path.add(nums[i]);
            backtrack(nums, used | (1 << i), path);
            path.remove(path.size() - 1);
        }
    }
}
```

### 4. Constraint Propagation
```java
// N-Queens with constraint sets
Set<Integer> cols = new HashSet<>();
Set<Integer> diag1 = new HashSet<>(); // row - col
Set<Integer> diag2 = new HashSet<>(); // row + col

private boolean isValid(int row, int col) {
    return !cols.contains(col) && 
           !diag1.contains(row - col) && 
           !diag2.contains(row + col);
}
```

## Time and Space Complexity

### Typical Complexities
- **Permutations**: O(n! × n) time, O(n) space
- **Combinations**: O(C(n,k) × k) time, O(k) space  
- **Subsets**: O(2^n × n) time, O(n) space
- **N-Queens**: O(n!) time, O(n²) space

### Space Optimization Tips
1. **In-place modifications**: Modify input instead of using extra visited arrays
2. **Iterative deepening**: For memory-constrained environments
3. **Bit manipulation**: Use integers instead of boolean arrays for small state spaces

## Common Pitfalls and Best Practices

### Pitfalls to Avoid
1. **Forgetting to backtrack**: Always undo changes made during recursion
2. **Incorrect base cases**: Ensure all termination conditions are covered
3. **Modifying immutable objects**: Create new objects when needed
4. **Stack overflow**: Consider iterative solutions for deep recursion

### Best Practices
1. **Clear variable names**: Use descriptive names for clarity
2. **Validate inputs**: Check for edge cases and invalid inputs
3. **Add comments**: Explain the backtracking logic
4. **Test thoroughly**: Include edge cases in testing

## Debugging Techniques

### 1. Add Debug Prints
```java
private void backtrack(List<Integer> path, int level) {
    System.out.println("Level " + level + ", Path: " + path);
    
    // Base case
    if (isComplete(path)) {
        System.out.println("Solution found: " + path);
        return;
    }
    
    // ... rest of logic
}
```

### 2. Visualize Call Stack
```java
private String indent(int level) {
    return "  ".repeat(level);
}

private void backtrack(int level) {
    System.out.println(indent(level) + "Entering level " + level);
    // ... logic ...
    System.out.println(indent(level) + "Exiting level " + level);
}
```

## Summary

Recursion and backtracking are powerful problem-solving techniques that excel at:
- **Tree/Graph traversal**
- **Combinatorial problems**
- **Constraint satisfaction**
- **Game solving**

**Key takeaways:**
1. Always define clear base cases
2. Ensure progress toward base case
3. Make and unmake choices systematically
4. Use pruning to optimize performance
5. Practice with various problem types

**Remember the mantra**: *"Try, recurse, backtrack"*