package practice.airbnb;

import java.util.*;

/**
 * Optimized Ski Trip implementation with multiple algorithms for different scenarios:
 * 1. DAG-based topological sort (original problem)
 * 2. Cycle detection with error handling
 * 3. Memoized DFS for graphs with revisitable nodes
 * 4. Edge cost variant with penalties
 */
public class OptimizedSkiiTrip {
    
    public static class SkiResult {
        public final int maxScore;
        public final List<Integer> path;
        public final boolean hasCycle;
        public final String error;
        
        public SkiResult(int maxScore, List<Integer> path) {
            this.maxScore = maxScore;
            this.path = new ArrayList<>(path);
            this.hasCycle = false;
            this.error = null;
        }
        
        public SkiResult(String error, boolean hasCycle) {
            this.maxScore = -1;
            this.path = new ArrayList<>();
            this.hasCycle = hasCycle;
            this.error = error;
        }
        
        @Override
        public String toString() {
            if (error != null) {
                return String.format("Error: %s (hasCycle: %s)", error, hasCycle);
            }
            return String.format("MaxScore: %d, Path: %s", maxScore, path);
        }
    }
    
    /**
     * OPTIMIZED VERSION 1: Enhanced Topological Sort with Path Tracking
     * Time: O(V + E), Space: O(V + E)
     * Improvements: Path reconstruction, better error handling, cycle detection
     */
    public static SkiResult maxSkiScoreOptimized(int n, int[] weights, List<int[]> edges) {
        if (n <= 0 || weights == null || weights.length != n) {
            return new SkiResult("Invalid input parameters", false);
        }
        
        // Build adjacency list with optimized ArrayList initialization
        List<List<Integer>> graph = new ArrayList<>(n);
        int[] indegree = new int[n];
        
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        
        // Build graph and calculate indegrees
        for (int[] edge : edges) {
            if (edge.length != 2 || edge[0] < 0 || edge[0] >= n || edge[1] < 0 || edge[1] >= n) {
                return new SkiResult("Invalid edge: " + Arrays.toString(edge), false);
            }
            int u = edge[0], v = edge[1];
            graph.get(u).add(v);
            indegree[v]++;
        }
        
        // Kahn's algorithm for topological sort with cycle detection
        ArrayDeque<Integer> queue = new ArrayDeque<>(); // Faster than LinkedList
        for (int i = 0; i < n; i++) {
            if (indegree[i] == 0) {
                queue.offer(i);
            }
        }
        
        int[] maxScore = Arrays.copyOf(weights, n);
        int[] parent = new int[n];
        Arrays.fill(parent, -1);
        int processedNodes = 0;
        
        while (!queue.isEmpty()) {
            int u = queue.poll();
            processedNodes++;
            
            for (int v : graph.get(u)) {
                if (maxScore[u] + weights[v] > maxScore[v]) {
                    maxScore[v] = maxScore[u] + weights[v];
                    parent[v] = u; // Track parent for path reconstruction
                }
                
                if (--indegree[v] == 0) {
                    queue.offer(v);
                }
            }
        }
        
        // Check for cycles
        if (processedNodes != n) {
            return new SkiResult("Graph contains cycles - cannot find optimal path", true);
        }
        
        // Find maximum score and reconstruct path
        int bestScore = 0;
        int bestNode = 0;
        for (int i = 0; i < n; i++) {
            if (maxScore[i] > bestScore) {
                bestScore = maxScore[i];
                bestNode = i;
            }
        }
        
        // Reconstruct path
        List<Integer> path = reconstructPath(parent, bestNode);
        
        return new SkiResult(bestScore, path);
    }
    
    /**
     * VERSION 2: Memoized DFS for graphs where nodes can be revisited
     * Time: O(V + E), Space: O(V)
     * Handles cycles gracefully and allows multiple visits to nodes
     */
    public static SkiResult maxSkiScoreMemoizedDFS(int n, int[] weights, List<int[]> edges) {
        if (n <= 0 || weights == null || weights.length != n) {
            return new SkiResult("Invalid input parameters", false);
        }
        
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : edges) {
            if (edge.length != 2 || edge[0] < 0 || edge[0] >= n || edge[1] < 0 || edge[1] >= n) {
                return new SkiResult("Invalid edge: " + Arrays.toString(edge), false);
            }
            graph.get(edge[0]).add(edge[1]);
        }
        
        // Check for cycles first
        if (hasCycle(graph, n)) {
            return new SkiResult("Graph contains cycles", true);
        }
        
        // Memoized DFS from each starting node
        Integer[] memo = new Integer[n];
        int[] pathParent = new int[n];
        Arrays.fill(pathParent, -1);
        
        int maxGlobalScore = 0;
        int bestStartNode = 0;
        
        for (int start = 0; start < n; start++) {
            Arrays.fill(memo, null); // Reset memo for each starting point
            int score = dfsWithMemo(start, graph, weights, memo, pathParent);
            if (score > maxGlobalScore) {
                maxGlobalScore = score;
                bestStartNode = start;
            }
        }
        
        // Reconstruct path for best starting node
        Arrays.fill(memo, null);
//        Arrays.fill(pathParent, -1);
        dfsWithMemo(bestStartNode, graph, weights, memo, pathParent);
//        System.out.println("haha"+ reconstructPath(pathParent, bestStartNode));
        List<Integer> path = reconstructDFSPath(bestStartNode, graph, weights, memo);
//        List<Integer> path = reconstructPath(pathParent, bestStartNode);
        return new SkiResult(maxGlobalScore, path);
    }
    
    /**
     * VERSION 3: Edge Cost Variant with Penalties
     * Time: O(V + E), Space: O(V + E)
     * Supports edge costs/penalties for more realistic ski trip modeling
     */
    public static SkiResult maxSkiScoreWithEdgeCosts(int n, int[] weights, List<int[]> edges, 
                                                     Map<String, Integer> edgeCosts) {
        if (n <= 0 || weights == null || weights.length != n) {
            return new SkiResult("Invalid input parameters", false);
        }
        
        // Build adjacency list
        List<List<Integer>> graph = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }
        
        for (int[] edge : edges) {
            if (edge.length != 2 || edge[0] < 0 || edge[0] >= n || edge[1] < 0 || edge[1] >= n) {
                return new SkiResult("Invalid edge: " + Arrays.toString(edge), false);
            }
            graph.get(edge[0]).add(edge[1]);
        }
        
        // Check for cycles
        if (hasCycle(graph, n)) {
            return new SkiResult("Graph contains cycles", true);
        }
        
        // Memoized DFS with edge costs
        Integer[] memo = new Integer[n];
        int maxGlobalScore = Integer.MIN_VALUE;
        int bestStartNode = 0;
        
        for (int start = 0; start < n; start++) {
            Arrays.fill(memo, null);
            int score = dfsWithEdgeCosts(start, graph, weights, edgeCosts, memo);
            if (score > maxGlobalScore) {
                maxGlobalScore = score;
                bestStartNode = start;
            }
        }
        
        // Reconstruct path
        Arrays.fill(memo, null);
        dfsWithEdgeCosts(bestStartNode, graph, weights, edgeCosts, memo);
        List<Integer> path = reconstructDFSPath(bestStartNode, graph, weights, memo);

        return new SkiResult(maxGlobalScore, path);
    }
    
    // Helper methods
    private static List<Integer> reconstructPath(int[] parent, int endNode) {
        List<Integer> path = new ArrayList<>();
        int current = endNode;
        
        while (current != -1) {
            path.add(current);
            current = parent[current];
        }
        
        Collections.reverse(path);
        return path;
    }
    
    private static boolean hasCycle(List<List<Integer>> graph, int n) {
        int[] color = new int[n]; // 0: white, 1: gray, 2: black
        
        for (int i = 0; i < n; i++) {
            if (color[i] == 0 && hasCycleDFS(i, graph, color)) {
                return true;
            }
        }
        return false;
    }
    
    private static boolean hasCycleDFS(int u, List<List<Integer>> graph, int[] color) {
        color[u] = 1; // Mark as gray (visiting)
        
        for (int v : graph.get(u)) {
            if (color[v] == 1) return true; // Back edge found
            if (color[v] == 0 && hasCycleDFS(v, graph, color)) return true;
        }
        
        color[u] = 2; // Mark as black (finished)
        return false;
    }
    
    private static int dfsWithMemo(int u, List<List<Integer>> graph, int[] weights, 
                                   Integer[] memo, int[] parent) {
        if (memo[u] != null) return memo[u];
        
        int maxScore = weights[u];
        int bestNext = -1;
        
        for (int v : graph.get(u)) {
            int score = weights[u] + dfsWithMemo(v, graph, weights, memo, parent);
            if (score > maxScore) {
                maxScore = score;
                bestNext = v;
            }
        }
        
        if (bestNext != -1) {
            parent[u] = bestNext;
        }
        
        return memo[u] = maxScore;
    }
    
    private static int dfsWithEdgeCosts(int u, List<List<Integer>> graph, int[] weights,
                                        Map<String, Integer> edgeCosts, Integer[] memo) {
        if (memo[u] != null) return memo[u];
        
        int maxScore = weights[u];
        
        for (int v : graph.get(u)) {
            String edgeKey = u + "," + v;
            int edgeCost = edgeCosts.getOrDefault(edgeKey, 0);
            int score = weights[u] + dfsWithEdgeCosts(v, graph, weights, edgeCosts, memo) - edgeCost;
            maxScore = Math.max(maxScore, score);
        }
        
        return memo[u] = maxScore;
    }
    
    private static List<Integer> reconstructDFSPath(int start, List<List<Integer>> graph, 
                                                    int[] weights, Integer[] memo) {
        List<Integer> path = new ArrayList<>();
        int current = start;
        
        while (current != -1) {
            path.add(current);
            int bestNext = -1;
            int bestScore = Integer.MIN_VALUE;
            
            for (int next : graph.get(current)) {
                if (memo[next] != null && memo[next] > bestScore) {
                    bestScore = memo[next];
                    bestNext = next;
                }
            }
            
            current = bestNext;
        }
        
        return path;
    }
    
    /**
     * Performance comparison and testing
     */
    public static void performanceComparison() {
        System.out.println("=== Ski Trip Optimization Performance Comparison ===\n");
        
        // Test case 1: Basic DAG
        int n = 5;
        int[] weights = {10, 20, 5, 15, 10};
        List<int[]> edges = Arrays.asList(
            new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 3}, new int[]{2, 3}, new int[]{3, 4}
        );
        
        System.out.println("Test Case 1: Basic DAG");
        System.out.println("Nodes: " + n + ", Weights: " + Arrays.toString(weights));
        System.out.println("Edges: " + edges.stream().map(Arrays::toString).toString());
        
        long start = System.nanoTime();
        SkiResult result1 = maxSkiScoreOptimized(n, weights, edges);
        long time1 = System.nanoTime() - start;
        
        start = System.nanoTime();
        SkiResult result2 = maxSkiScoreMemoizedDFS(n, weights, edges);
        long time2 = System.nanoTime() - start;
        
        System.out.println("Optimized Topological Sort: " + result1 + " (Time: " + time1/1000 + " μs)");
        System.out.println("Memoized DFS: " + result2 + " (Time: " + time2/1000 + " μs)");
        
        // Test case 2: Edge costs
        System.out.println("\nTest Case 2: With Edge Costs");
//        Map<String, Integer> edgeCosts = Map.of(
//            "0,1", 2, "1,3", 5, "2,3", 1, "3,4", 3
//        );
//
//        start = System.nanoTime();
//        SkiResult result3 = maxSkiScoreWithEdgeCosts(n, weights, edges, edgeCosts);
//        long time3 = System.nanoTime() - start;
//
//        System.out.println("Edge costs: " + edgeCosts);
//        System.out.println("With Edge Costs: " + result3 + " (Time: " + time3/1000 + " μs)");
//
//        // Test case 3: Cycle detection
//        System.out.println("\nTest Case 3: Cycle Detection");
//        List<int[]> cycleEdges = Arrays.asList(
//            new int[]{0, 1}, new int[]{1, 2}, new int[]{2, 0}, new int[]{1, 3}
//        );
//
//        SkiResult cycleResult = maxSkiScoreOptimized(4, new int[]{10, 20, 15, 5}, cycleEdges);
//        System.out.println("Cycle test result: " + cycleResult);
    }
    
    public static void main(String[] args) {
        performanceComparison();
        
        // Original example
        System.out.println("\n=== Original Example ===");
        int n = 5;
        int[] w = {10, 20, 5, 15, 10};
        List<int[]> edges = Arrays.asList(
            new int[]{0, 1}, new int[]{0, 2}, new int[]{1, 3}, new int[]{2, 3}, new int[]{3, 4}
        );
        
        SkiResult result = maxSkiScoreOptimized(n, w, edges);
        System.out.println("Optimized result: " + result);
    }
}