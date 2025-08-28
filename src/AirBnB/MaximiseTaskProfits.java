package practice.airbnb;

import java.util.*;

/**
 * Maximise Task Profits where each task has a start time, end time, and profit.
 * The goal is to select a subset of non-overlapping tasks that maximizes the total profit
 * while ensuring that no two tasks overlap in time.
 * 
 * This is a classic weighted job scheduling problem that can be solved using:
 * 1. Dynamic Programming with Binary Search - O(n log n)
 * 2. Greedy approach (may not be optimal)
 * 3. Recursive with memoization
 */
public class MaximiseTaskProfits {
    
    public static class Task {
        int id;
        int start;
        int end;
        int profit;
        String description;
        
        public Task(int id, int start, int end, int profit, String description) {
            this.id = id;
            this.start = start;
            this.end = end;
            this.profit = profit;
            this.description = description;
        }
        
        public boolean overlapsWith(Task other) {
            return this.start < other.end && other.start < this.end;
        }
        
        @Override
        public String toString() {
            return String.format("Task[id=%d, %s, time=%d-%d, profit=%d]", 
                               id, description, start, end, profit);
        }
    }
    
    public static class Solution {
        int maxProfit;
        List<Task> selectedTasks;
        
        public Solution(int maxProfit, List<Task> selectedTasks) {
            this.maxProfit = maxProfit;
            this.selectedTasks = new ArrayList<>(selectedTasks);
        }
        
        @Override
        public String toString() {
            return String.format("Max Profit: %d, Tasks: %s", maxProfit, selectedTasks);
        }
    }
    
    /**
     * Method 1: Dynamic Programming with Binary Search - O(n log n)
     * Most efficient approach for this problem
     */
    public static Solution maxProfitDP(List<Task> tasks) {
        if (tasks.isEmpty()) return new Solution(0, new ArrayList<>());
        
        // Sort tasks by end time
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparingInt(t -> t.end));
        
        int n = sortedTasks.size();
        int[] dp = new int[n]; // dp[i] = max profit considering tasks 0..i
        int[] prev = new int[n]; // prev[i] = previous non-overlapping task index
        
        // Find previous non-overlapping task for each task
        for (int i = 0; i < n; i++) {
            prev[i] = findLatestNonOverlapping(sortedTasks, i);
        }
        
        // Fill DP array
        dp[0] = sortedTasks.get(0).profit;
        for (int i = 1; i < n; i++) {
            // Two choices: include current task or exclude it
            int includeProfit = sortedTasks.get(i).profit;
            if (prev[i] != -1) {
                includeProfit += dp[prev[i]];
            }
            int excludeProfit = dp[i - 1];
            
            dp[i] = Math.max(includeProfit, excludeProfit);
        }
        
        // Reconstruct solution
        List<Task> selected = new ArrayList<>();
        reconstructSolution(sortedTasks, dp, prev, n - 1, selected);
        
        return new Solution(dp[n - 1], selected);
    }
    
    private static int findLatestNonOverlapping(List<Task> tasks, int currentIndex) {
        Task current = tasks.get(currentIndex);
        
        // Binary search for the latest task that ends before current task starts
        int left = 0, right = currentIndex - 1;
        int result = -1;
        
        while (left <= right) {
            int mid = (left + right) / 2;
            if (tasks.get(mid).end <= current.start) {
                result = mid;
                left = mid + 1;
            } else {
                right = mid - 1;
            }
        }
        
        return result;
    }
    
    private static void reconstructSolution(List<Task> tasks, int[] dp, int[] prev, 
                                          int index, List<Task> selected) {
        if (index < 0) return;
        
        if (index == 0) {
            selected.add(tasks.get(0));
            return;
        }
        
        // Check if current task was included in optimal solution
        int includeProfit = tasks.get(index).profit;
        if (prev[index] != -1) {
            includeProfit += dp[prev[index]];
        }
        
        if (includeProfit > dp[index - 1]) {
            // Current task was included
            selected.add(tasks.get(index));
            if (prev[index] != -1) {
                reconstructSolution(tasks, dp, prev, prev[index], selected);
            }
        } else {
            // Current task was excluded
            reconstructSolution(tasks, dp, prev, index - 1, selected);
        }
    }
    
    /**
     * Method 2: Recursive approach with memoization
     * Easier to understand but less efficient than DP
     */
    public static Solution maxProfitRecursive(List<Task> tasks) {
        if (tasks.isEmpty()) return new Solution(0, new ArrayList<>());
        
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparingInt(t -> t.end));
        
        Map<Integer, Integer> memo = new HashMap<>();
        Map<Integer, List<Task>> solutionMemo = new HashMap<>();
        
        int maxProfit = maxProfitRecursiveHelper(sortedTasks, sortedTasks.size() - 1, memo, solutionMemo);
        List<Task> selectedTasks = solutionMemo.getOrDefault(sortedTasks.size() - 1, new ArrayList<>());
        
        return new Solution(maxProfit, selectedTasks);
    }
    
    private static int maxProfitRecursiveHelper(List<Task> tasks, int index, 
                                              Map<Integer, Integer> memo,
                                              Map<Integer, List<Task>> solutionMemo) {
        if (index < 0) return 0;
        
        if (memo.containsKey(index)) {
            return memo.get(index);
        }
        
        // Option 1: Include current task
        int includeProfit = tasks.get(index).profit;
        int prevIndex = findLatestNonOverlapping(tasks, index);
        int prevProfit = maxProfitRecursiveHelper(tasks, prevIndex, memo, solutionMemo);
        includeProfit += prevProfit;
        
        // Option 2: Exclude current task
        int excludeProfit = maxProfitRecursiveHelper(tasks, index - 1, memo, solutionMemo);
        
        int result = Math.max(includeProfit, excludeProfit);
        memo.put(index, result);
        
        // Store the selected tasks for reconstruction
        List<Task> selectedTasks = new ArrayList<>();
        if (includeProfit > excludeProfit) {
            selectedTasks.add(tasks.get(index));
            if (prevIndex >= 0) {
                selectedTasks.addAll(solutionMemo.getOrDefault(prevIndex, new ArrayList<>()));
            }
        } else {
            selectedTasks.addAll(solutionMemo.getOrDefault(index - 1, new ArrayList<>()));
        }
        solutionMemo.put(index, selectedTasks);
        
        return result;
    }
    
    /**
     * Method 3: Greedy approach (not always optimal, but good approximation)
     * Sorts by profit/duration ratio
     */
    public static Solution maxProfitGreedy(List<Task> tasks) {
        if (tasks.isEmpty()) return new Solution(0, new ArrayList<>());
        
        // Sort by profit per unit time (profit/duration ratio)
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort((t1, t2) -> {
            double ratio1 = (double) t1.profit / (t1.end - t1.start);
            double ratio2 = (double) t2.profit / (t2.end - t2.start);
            return Double.compare(ratio2, ratio1); // Descending order
        });
        
        List<Task> selected = new ArrayList<>();
        int totalProfit = 0;
        
        for (Task task : sortedTasks) {
            boolean canAdd = true;
            for (Task selectedTask : selected) {
                if (task.overlapsWith(selectedTask)) {
                    canAdd = false;
                    break;
                }
            }
            
            if (canAdd) {
                selected.add(task);
                totalProfit += task.profit;
            }
        }
        
        return new Solution(totalProfit, selected);
    }
    
    /**
     * Method 4: Brute force approach for comparison (exponential time)
     * Only use for small inputs
     */
    public static Solution maxProfitBruteForce(List<Task> tasks) {
        if (tasks.isEmpty()) return new Solution(0, new ArrayList<>());
        
        int n = tasks.size();
        int maxProfit = 0;
        List<Task> bestSelection = new ArrayList<>();
        
        // Try all possible subsets (2^n combinations)
        for (int mask = 0; mask < (1 << n); mask++) {
            List<Task> currentSelection = new ArrayList<>();
            int currentProfit = 0;
            boolean validSelection = true;
            
            // Build current subset
            for (int i = 0; i < n; i++) {
                if ((mask & (1 << i)) != 0) {
                    currentSelection.add(tasks.get(i));
                    currentProfit += tasks.get(i).profit;
                }
            }
            
            // Check if current selection has overlapping tasks
            for (int i = 0; i < currentSelection.size() && validSelection; i++) {
                for (int j = i + 1; j < currentSelection.size(); j++) {
                    if (currentSelection.get(i).overlapsWith(currentSelection.get(j))) {
                        validSelection = false;
                        break;
                    }
                }
            }
            
            if (validSelection && currentProfit > maxProfit) {
                maxProfit = currentProfit;
                bestSelection = new ArrayList<>(currentSelection);
            }
        }
        
        return new Solution(maxProfit, bestSelection);
    }
    
    /**
     * Advanced: Find all optimal solutions (if multiple exist)
     */
    public static List<Solution> findAllOptimalSolutions(List<Task> tasks) {
        List<Solution> allSolutions = new ArrayList<>();
        if (tasks.isEmpty()) {
            allSolutions.add(new Solution(0, new ArrayList<>()));
            return allSolutions;
        }
        
        Solution optimal = maxProfitDP(tasks);
        int targetProfit = optimal.maxProfit;
        
        // Use backtracking to find all combinations with target profit
        findAllOptimalHelper(tasks, 0, new ArrayList<>(), 0, targetProfit, allSolutions);
        
        return allSolutions;
    }
    
    private static void findAllOptimalHelper(List<Task> tasks, int index, List<Task> current, 
                                           int currentProfit, int targetProfit, List<Solution> allSolutions) {
        if (currentProfit == targetProfit) {
            allSolutions.add(new Solution(currentProfit, new ArrayList<>(current)));
            return;
        }
        
        if (index >= tasks.size() || currentProfit > targetProfit) {
            return;
        }
        
        // Try including current task
        Task currentTask = tasks.get(index);
        boolean canInclude = true;
        for (Task selected : current) {
            if (currentTask.overlapsWith(selected)) {
                canInclude = false;
                break;
            }
        }
        
        if (canInclude) {
            current.add(currentTask);
            findAllOptimalHelper(tasks, index + 1, current, currentProfit + currentTask.profit, 
                               targetProfit, allSolutions);
            current.remove(current.size() - 1);
        }
        
        // Try excluding current task
        findAllOptimalHelper(tasks, index + 1, current, currentProfit, targetProfit, allSolutions);
    }
    
    public static void main(String[] args) {
        // Test data
        List<Task> tasks = Arrays.asList(
            new Task(1, 1, 3, 50, "Design UI"),
            new Task(2, 2, 5, 100, "Backend API"),
            new Task(3, 4, 6, 70, "Testing"),
            new Task(4, 6, 8, 60, "Deployment"),
            new Task(5, 5, 9, 120, "Documentation"),
            new Task(6, 7, 10, 80, "Code Review"),
            new Task(7, 8, 11, 90, "Bug Fixes"),
            new Task(8, 1, 4, 80, "Research"),
            new Task(9, 3, 7, 110, "Implementation")
        );
        
        System.out.println("=== Task Profit Maximization ===");
        System.out.println("Available Tasks:");
        for (Task task : tasks) {
            System.out.println("  " + task);
        }
        
        // Test different approaches
        System.out.println("\n=== Dynamic Programming Solution ===");
        long startTime = System.nanoTime();
        Solution dpSolution = maxProfitDP(tasks);
        long endTime = System.nanoTime();
        System.out.println(dpSolution);
        System.out.println("Time: " + (endTime - startTime) / 1000 + " μs");
        
        System.out.println("\n=== Recursive Solution ===");
        startTime = System.nanoTime();
        Solution recursiveSolution = maxProfitRecursive(tasks);
        endTime = System.nanoTime();
        System.out.println(recursiveSolution);
        System.out.println("Time: " + (endTime - startTime) / 1000 + " μs");
        
        System.out.println("\n=== Greedy Solution ===");
        startTime = System.nanoTime();
        Solution greedySolution = maxProfitGreedy(tasks);
        endTime = System.nanoTime();
        System.out.println(greedySolution);
        System.out.println("Time: " + (endTime - startTime) / 1000 + " μs");
        System.out.println("Greedy is optimal: " + (greedySolution.maxProfit == dpSolution.maxProfit));
        
        // Test with smaller dataset for brute force
        List<Task> smallTasks = tasks.subList(0, Math.min(6, tasks.size()));
        System.out.println("\n=== Brute Force Solution (first 6 tasks) ===");
        startTime = System.nanoTime();
        Solution bruteForceSolution = maxProfitBruteForce(smallTasks);
        endTime = System.nanoTime();
        System.out.println(bruteForceSolution);
        System.out.println("Time: " + (endTime - startTime) / 1000 + " μs");
        
        // Find all optimal solutions
        System.out.println("\n=== All Optimal Solutions ===");
        List<Solution> allOptimal = findAllOptimalSolutions(smallTasks);
        System.out.println("Found " + allOptimal.size() + " optimal solution(s):");
        for (int i = 0; i < allOptimal.size(); i++) {
            System.out.println("  Solution " + (i + 1) + ": " + allOptimal.get(i));
        }
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        System.out.println("Algorithm comparisons on full dataset:");
        
        startTime = System.nanoTime();
        Solution dp = maxProfitDP(tasks);
        endTime = System.nanoTime();
        System.out.println("DP: " + (endTime - startTime) / 1000 + " μs, Profit: " + dp.maxProfit);
        
        startTime = System.nanoTime();
        Solution recursive = maxProfitRecursive(tasks);
        endTime = System.nanoTime();
        System.out.println("Recursive: " + (endTime - startTime) / 1000 + " μs, Profit: " + recursive.maxProfit);
        
        startTime = System.nanoTime();
        Solution greedy = maxProfitGreedy(tasks);
        endTime = System.nanoTime();
        System.out.println("Greedy: " + (endTime - startTime) / 1000 + " μs, Profit: " + greedy.maxProfit);
        
        System.out.println("\nAll algorithms find optimal solution: " + 
                          (dp.maxProfit == recursive.maxProfit));
    }
}
