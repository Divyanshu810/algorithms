# Backtracking Algorithm Guide

## Table of Contents
1. [What is Backtracking?](#what-is-backtracking)
2. [When to Use Backtracking](#when-to-use-backtracking)
3. [How to Detect Backtracking Problems](#how-to-detect-backtracking-problems)
4. [Implementation Template](#implementation-template)
5. [Common Use Cases](#common-use-cases)
6. [Optimization Techniques](#optimization-techniques)
7. [Examples](#examples)

## What is Backtracking?

Backtracking is a systematic method for solving problems by **exploring all possible solutions** and **abandoning paths** that cannot lead to a valid solution. It's essentially a refined brute force approach that prunes the search space intelligently.

**Core Concept**: Try a solution step by step, and if at any point you realize the current path cannot lead to a solution, **backtrack** (undo the last step) and try a different path.

### Key Characteristics
- **Incremental construction**: Build solution piece by piece
- **Constraint checking**: Validate partial solutions early
- **Backtracking**: Undo choices when they lead to dead ends
- **Complete exploration**: Guarantees finding all solutions (if they exist)

## When to Use Backtracking

### Problem Types That Fit Backtracking
- **Combinatorial problems**: Finding all combinations/permutations
- **Constraint satisfaction**: N-Queens, Sudoku, Graph Coloring
- **Optimization problems**: Finding best solution among many candidates
- **Decision problems**: Determining if a solution exists

### Characteristics of Backtracking Problems
- Problem can be broken into **sequential decisions**
- Each decision has **multiple options**
- **Constraints** can be checked incrementally
- Need to explore **multiple possibilities**
- Solution space forms a **tree structure**

## How to Detect Backtracking Problems

### 🔍 Detection Checklist

**✅ Strong Indicators:**
- Keywords: "find all", "count all", "generate all"
- Phrases: "place N items", "arrange elements", "satisfy constraints"
- Requirements: exploring all possibilities, exhaustive search

**✅ Problem Structure:**
- Multiple choices at each step
- Constraints that eliminate invalid paths
- Need to try different combinations
- Solution built incrementally

**✅ Examples:**
- "Find all permutations of..."
- "Count ways to arrange..."
- "Generate all valid combinations..."
- "Place N queens on chessboard..."
- "Solve Sudoku puzzle..."

### 🚫 When NOT to Use Backtracking
- Simple optimization (use greedy/DP instead)
- Single path problems (use DFS/BFS)
- Mathematical formulas exist
- Problem has optimal substructure (use DP)

## Implementation Template

### Basic Backtracking Structure

```java
public class BacktrackingTemplate {
    private List<Solution> results = new ArrayList<>();
    
    public List<Solution> solve(Problem problem) {
        backtrack(problem, new PartialSolution(), 0);
        return results;
    }
    
    private void backtrack(Problem problem, PartialSolution current, int position) {
        // Base case: complete solution found
        if (isComplete(current)) {
            if (isValid(current)) {
                results.add(new Solution(current)); // Make copy!
            }
            return;
        }
        
        // Try all possible choices at current position
        for (Choice choice : getPossibleChoices(problem, current, position)) {
            // Check if choice is valid (early pruning)
            if (isValidChoice(current, choice)) {
                // Make choice
                current.add(choice);
                
                // Recurse to next position
                backtrack(problem, current, position + 1);
                
                // Backtrack: undo choice
                current.remove(choice);
            }
        }
    }
    
    private boolean isComplete(PartialSolution solution) {
        // Check if we've made all required decisions
        return solution.size() == targetSize;
    }
    
    private boolean isValid(PartialSolution solution) {
        // Validate complete solution against all constraints
        return checkAllConstraints(solution);
    }
    
    private boolean isValidChoice(PartialSolution current, Choice choice) {
        // Early constraint checking (pruning)
        return !violatesConstraints(current, choice);
    }
}
```

### Key Implementation Points

1. **State Management**: Carefully manage what constitutes "current state"
2. **Choice and Unchoice**: Always undo changes when backtracking
3. **Early Pruning**: Check constraints as early as possible
4. **Copy Solutions**: Make copies when storing complete solutions
5. **Base Cases**: Handle both complete and invalid states

## Common Use Cases

### 1. **Permutations and Combinations**

```java
// Generate all permutations of [1,2,3]
public List<List<Integer>> permute(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, new ArrayList<>(), new boolean[nums.length], result);
    return result;
}

private void backtrack(int[] nums, List<Integer> current, 
                      boolean[] used, List<List<Integer>> result) {
    if (current.size() == nums.length) {
        result.add(new ArrayList<>(current));
        return;
    }
    
    for (int i = 0; i < nums.length; i++) {
        if (!used[i]) {
            current.add(nums[i]);
            used[i] = true;
            
            backtrack(nums, current, used, result);
            
            current.remove(current.size() - 1);
            used[i] = false;
        }
    }
}
```

### 2. **N-Queens Problem**

```java
public List<List<String>> solveNQueens(int n) {
    List<List<String>> result = new ArrayList<>();
    char[][] board = new char[n][n];
    // Initialize board with '.'
    for (int i = 0; i < n; i++) {
        Arrays.fill(board[i], '.');
    }
    
    backtrack(board, 0, result);
    return result;
}

private void backtrack(char[][] board, int row, List<List<String>> result) {
    if (row == board.length) {
        result.add(constructBoard(board));
        return;
    }
    
    for (int col = 0; col < board.length; col++) {
        if (isValidPlacement(board, row, col)) {
            board[row][col] = 'Q';
            backtrack(board, row + 1, result);
            board[row][col] = '.';  // Backtrack
        }
    }
}

private boolean isValidPlacement(char[][] board, int row, int col) {
    // Check column
    for (int i = 0; i < row; i++) {
        if (board[i][col] == 'Q') return false;
    }
    
    // Check diagonal (top-left to bottom-right)
    for (int i = row - 1, j = col - 1; i >= 0 && j >= 0; i--, j--) {
        if (board[i][j] == 'Q') return false;
    }
    
    // Check diagonal (top-right to bottom-left)
    for (int i = row - 1, j = col + 1; i >= 0 && j < board.length; i--, j++) {
        if (board[i][j] == 'Q') return false;
    }
    
    return true;
}
```

### 3. **Subset Generation**

```java
// Generate all subsets of [1,2,3]
public List<List<Integer>> subsets(int[] nums) {
    List<List<Integer>> result = new ArrayList<>();
    backtrack(nums, 0, new ArrayList<>(), result);
    return result;
}

private void backtrack(int[] nums, int start, List<Integer> current, 
                      List<List<Integer>> result) {
    // Add current subset to result
    result.add(new ArrayList<>(current));
    
    // Try adding each remaining element
    for (int i = start; i < nums.length; i++) {
        current.add(nums[i]);
        backtrack(nums, i + 1, current, result);
        current.remove(current.size() - 1);
    }
}
```

### 4. **Word Search in Grid**

```java
public boolean wordSearch(char[][] board, String word) {
    for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[0].length; j++) {
            if (backtrack(board, word, i, j, 0)) {
                return true;
            }
        }
    }
    return false;
}

private boolean backtrack(char[][] board, String word, int row, int col, int index) {
    // Base case: found complete word
    if (index == word.length()) return true;
    
    // Boundary checks
    if (row < 0 || row >= board.length || col < 0 || col >= board[0].length) {
        return false;
    }
    
    // Character mismatch or already visited
    if (board[row][col] != word.charAt(index)) return false;
    
    // Mark as visited
    char temp = board[row][col];
    board[row][col] = '#';
    
    // Explore all 4 directions
    boolean found = backtrack(board, word, row + 1, col, index + 1) ||
                   backtrack(board, word, row - 1, col, index + 1) ||
                   backtrack(board, word, row, col + 1, index + 1) ||
                   backtrack(board, word, row, col - 1, index + 1);
    
    // Backtrack: restore original character
    board[row][col] = temp;
    
    return found;
}
```

## Optimization Techniques

### 1. **Early Pruning**
```java
// Instead of checking constraints at the end
if (isComplete(solution) && isValid(solution)) {
    addSolution(solution);
}

// Check constraints early to prune invalid paths
if (!isValidSoFar(partialSolution)) {
    return; // Prune this branch
}
```

### 2. **Memoization** (when subproblems overlap)
```java
private Map<String, Boolean> memo = new HashMap<>();

private boolean backtrack(State state) {
    String key = state.toString();
    if (memo.containsKey(key)) {
        return memo.get(key);
    }
    
    boolean result = // ... backtracking logic
    memo.put(key, result);
    return result;
}
```

### 3. **Constraint Propagation**
```java
// For Sudoku: when placing a number, immediately mark
// related cells as invalid for that number
private void placeNumber(int row, int col, int num) {
    board[row][col] = num;
    // Update constraints for row, column, and box
    updateConstraints(row, col, num);
}
```

### 4. **Ordering Heuristics**
```java
// Try most constrained variables first (MRV heuristic)
// Try least constraining values first (LCV heuristic)
private List<Choice> getPossibleChoices(State state) {
    List<Choice> choices = getAllChoices(state);
    // Sort by some heuristic (e.g., most promising first)
    choices.sort(byPromiseHeuristic);
    return choices;
}
```

## Time and Space Complexity

### Time Complexity
- **Worst case**: O(b^d) where b = branching factor, d = depth
- **With pruning**: Often much better in practice
- **Exponential** in nature for most problems

### Space Complexity
- **Recursion stack**: O(d) where d = maximum depth
- **Solution storage**: Depends on number of solutions
- **State representation**: O(state_size)

## Common Pitfalls

### 1. **Forgetting to Backtrack**
```java
// ❌ Wrong: no backtracking
current.add(choice);
backtrack(current, position + 1);
// Missing: current.remove(choice);

// ✅ Correct: proper backtracking
current.add(choice);
backtrack(current, position + 1);
current.remove(choice);  // Undo the choice
```

### 2. **Shallow vs Deep Copy**
```java
// ❌ Wrong: all solutions reference same object
solutions.add(currentSolution);

// ✅ Correct: create new copy
solutions.add(new ArrayList<>(currentSolution));
```

### 3. **Inefficient Constraint Checking**
```java
// ❌ Wrong: check all constraints every time
if (violatesAnyConstraint(completeSolution)) return;

// ✅ Better: incremental constraint checking
if (violatesNewConstraint(partialSolution, newChoice)) return;
```

### 4. **Missing Base Cases**
```java
// ❌ Incomplete: only checks one condition
if (solution.size() == target) {
    addSolution(solution);
    return;
}

// ✅ Complete: handles all termination conditions
if (solution.size() == target) {
    if (isValid(solution)) {
        addSolution(solution);
    }
    return;
}
if (position >= maxPosition) return; // Prevent infinite recursion
```

## Practice Problems

### Beginner
- Generate all permutations of a string
- Generate all subsets of an array
- Combination sum
- Letter combinations of phone number

### Intermediate  
- N-Queens problem
- Sudoku solver
- Word search in grid
- Palindrome partitioning

### Advanced
- Expression add operators
- Remove invalid parentheses
- Cryptarithmetic puzzles
- Graph coloring

## Summary

Backtracking is a powerful technique for exploring solution spaces systematically. Key points to remember:

1. **Identify the pattern**: Sequential decisions with multiple choices
2. **Structure your approach**: Choose → Recurse → Backtrack
3. **Optimize early**: Prune invalid paths as soon as possible
4. **Handle state carefully**: Always undo changes when backtracking
5. **Consider alternatives**: Sometimes DP or greedy approaches are better

The key to mastering backtracking is recognizing when problems fit the pattern and implementing the recursive structure correctly with proper state management.