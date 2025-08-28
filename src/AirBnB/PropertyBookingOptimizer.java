package practice.airbnb;

import java.util.*;

/**
 * Property Booking Optimizer - Find optimal combination of properties for group accommodation
 * 
 * Problem: Given properties with (id, neighborhood, capacity) and a group size,
 * find the minimum capacity combination that accommodates the group with fewest properties.
 * 
 * This is a variant of the "Minimum Subset Sum" problem with additional constraints.
 */
public class PropertyBookingOptimizer {
    
    public static class Property {
        public final int id;
        public final String neighborhood;
        public final int capacity;
        
        public Property(int id, String neighborhood, int capacity) {
            this.id = id;
            this.neighborhood = neighborhood;
            this.capacity = capacity;
        }
        
        @Override
        public String toString() {
            return String.format("Property(%d, %s, %d)", id, neighborhood, capacity);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Property)) return false;
            Property other = (Property) obj;
            return id == other.id && Objects.equals(neighborhood, other.neighborhood) && capacity == other.capacity;
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(id, neighborhood, capacity);
        }
    }
    
    public static class BookingResult {
        public final List<Integer> propertyIds;
        public final int totalCapacity;
        public final boolean isValid;
        public final String algorithm;
        
        public BookingResult(List<Integer> propertyIds, int totalCapacity, String algorithm) {
            this.propertyIds = new ArrayList<>(propertyIds);
            this.totalCapacity = totalCapacity;
            this.isValid = true;
            this.algorithm = algorithm;
        }
        
        public BookingResult() {
            this.propertyIds = new ArrayList<>();
            this.totalCapacity = 0;
            this.isValid = false;
            this.algorithm = "No Solution";
        }
        
        @Override
        public String toString() {
            if (!isValid) return "No valid combination found";
            return String.format("Properties: %s, Total Capacity: %d, Algorithm: %s", 
                               propertyIds, totalCapacity, algorithm);
        }
    }
    
    /**
     * ALGORITHM 1: Dynamic Programming Approach
     * Time: O(n * capacity_sum), Space: O(capacity_sum)
     * 
     * Uses classic subset sum DP to find minimum capacity >= groupSize
     */
    public static BookingResult findOptimalPropertiesDP(List<Property> properties, 
                                                        int groupSize, String neighborhood) {
        // Filter properties by neighborhood
        List<Property> validProperties = properties.stream()
            .filter(p -> p.neighborhood.equals(neighborhood))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        if (validProperties.isEmpty()) {
            return new BookingResult();
        }
        
        // Calculate total capacity to determine DP array size
        int totalCapacity = validProperties.stream().mapToInt(p -> p.capacity).sum();
        if (totalCapacity < groupSize) {
            return new BookingResult();
        }
        
        // DP array: dp[i][capacity] = minimum properties to achieve capacity using first i properties
        int n = validProperties.size();
        int[][] dp = new int[n + 1][totalCapacity + 1];
        boolean[][] canAchieve = new boolean[n + 1][totalCapacity + 1];
        
        // Initialize DP
        for (int i = 0; i <= n; i++) {
            Arrays.fill(dp[i], Integer.MAX_VALUE);
        }
        dp[0][0] = 0;
        canAchieve[0][0] = true;
        
        // Fill DP table
        for (int i = 1; i <= n; i++) {
            Property prop = validProperties.get(i - 1);
            
            for (int cap = 0; cap <= totalCapacity; cap++) {
                // Don't take current property
                if (canAchieve[i-1][cap]) {
                    dp[i][cap] = dp[i-1][cap];
                    canAchieve[i][cap] = true;
                }
                
                // Take current property
                if (cap >= prop.capacity && canAchieve[i-1][cap - prop.capacity]) {
                    int newCount = dp[i-1][cap - prop.capacity] + 1;
                    if (!canAchieve[i][cap] || newCount < dp[i][cap]) {
                        dp[i][cap] = newCount;
                        canAchieve[i][cap] = true;
                    }
                }
            }
        }
        
        // Find minimum capacity >= groupSize with minimum properties
        int bestCapacity = -1;
        int minProperties = Integer.MAX_VALUE;
        
        for (int cap = groupSize; cap <= totalCapacity; cap++) {
            if (canAchieve[n][cap] && dp[n][cap] < minProperties) {
                minProperties = dp[n][cap];
                bestCapacity = cap;
            }
        }
        
        if (bestCapacity == -1) {
            return new BookingResult();
        }
        
        // Reconstruct solution
        List<Integer> selectedIds = reconstructDPSolution(validProperties, dp, canAchieve, bestCapacity);
        return new BookingResult(selectedIds, bestCapacity, "Dynamic Programming");
    }
    
    /**
     * ALGORITHM 2: Backtracking with Pruning
     * Time: O(2^n) worst case, but with aggressive pruning
     * Space: O(n) for recursion stack
     * 
     * More intuitive approach with early termination and branch pruning
     */
    public static BookingResult findOptimalPropertiesBacktrack(List<Property> properties, 
                                                               int groupSize, String neighborhood) {
        List<Property> validProperties = properties.stream()
            .filter(p -> p.neighborhood.equals(neighborhood))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        if (validProperties.isEmpty()) {
            return new BookingResult();
        }
        
        // Sort properties by capacity/efficiency for better pruning
        validProperties.sort((a, b) -> Integer.compare(b.capacity, a.capacity));
        
        BacktrackState bestSolution = new BacktrackState();
        backtrackHelper(validProperties, 0, groupSize, new ArrayList<>(), 0, bestSolution);
        
        if (bestSolution.isValid) {
            return new BookingResult(bestSolution.propertyIds, bestSolution.totalCapacity, "Backtracking");
        }
        return new BookingResult();
    }
    
    /**
     * ALGORITHM 3: Greedy Approximation
     * Time: O(n log n), Space: O(n)
     * 
     * Fast approximation that may not always find optimal solution
     */
    public static BookingResult findOptimalPropertiesGreedy(List<Property> properties, 
                                                            int groupSize, String neighborhood) {
        List<Property> validProperties = properties.stream()
            .filter(p -> p.neighborhood.equals(neighborhood))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        if (validProperties.isEmpty()) {
            return new BookingResult();
        }
        
        // Sort by capacity descending (take largest first)
        validProperties.sort((a, b) -> Integer.compare(b.capacity, a.capacity));
        
        List<Integer> selectedIds = new ArrayList<>();
        int totalCapacity = 0;
        
        for (Property prop : validProperties) {
            if (totalCapacity >= groupSize) break;
            selectedIds.add(prop.id);
            totalCapacity += prop.capacity;
        }
        
        if (totalCapacity >= groupSize) {
            return new BookingResult(selectedIds, totalCapacity, "Greedy");
        }
        return new BookingResult();
    }
    
    /**
     * ALGORITHM 4: Branch and Bound (Optimal)
     * Time: O(2^n) worst case, but often much better with good bounds
     * Space: O(n) for recursion stack
     * 
     * Guaranteed optimal solution with sophisticated pruning
     */
    public static BookingResult findOptimalPropertiesBranchBound(List<Property> properties, 
                                                                 int groupSize, String neighborhood) {
        List<Property> validProperties = properties.stream()
            .filter(p -> p.neighborhood.equals(neighborhood))
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
        
        if (validProperties.isEmpty()) {
            return new BookingResult();
        }
        
        // Sort by capacity for better bounds
        validProperties.sort((a, b) -> Integer.compare(b.capacity, a.capacity));
        
        BranchBoundState globalBest = new BranchBoundState();
        branchBoundHelper(validProperties, 0, groupSize, new ArrayList<>(), 0, globalBest);
        
        if (globalBest.isValid) {
            return new BookingResult(globalBest.propertyIds, globalBest.totalCapacity, "Branch & Bound");
        }
        return new BookingResult();
    }
    
    // Helper classes and methods
    private static class BacktrackState {
        List<Integer> propertyIds = new ArrayList<>();
        int totalCapacity = Integer.MAX_VALUE;
        boolean isValid = false;
    }
    
    private static class BranchBoundState {
        List<Integer> propertyIds = new ArrayList<>();
        int totalCapacity = Integer.MAX_VALUE;
        boolean isValid = false;
    }
    
    private static void backtrackHelper(List<Property> properties, int index, int remaining,
                                        List<Integer> current, int currentCapacity, BacktrackState best) {
        // Base case: found valid solution
        if (currentCapacity >= remaining) {
            if (!best.isValid || currentCapacity < best.totalCapacity || 
                (currentCapacity == best.totalCapacity && current.size() < best.propertyIds.size())) {
                best.propertyIds = new ArrayList<>(current);
                best.totalCapacity = currentCapacity;
                best.isValid = true;
            }
            return;
        }
        
        // Pruning: if we've exceeded current best capacity, stop
        if (best.isValid && currentCapacity >= best.totalCapacity) {
            return;
        }
        
        // Try remaining properties
        for (int i = index; i < properties.size(); i++) {
            Property prop = properties.get(i);
            current.add(prop.id);
            backtrackHelper(properties, i + 1, remaining, current, currentCapacity + prop.capacity, best);
            current.remove(current.size() - 1);
        }
    }
    
    private static void branchBoundHelper(List<Property> properties, int index, int remaining,
                                          List<Integer> current, int currentCapacity, BranchBoundState best) {
        // Bound check: can we possibly improve?
        if (best.isValid && currentCapacity >= best.totalCapacity) {
            return;
        }
        
        // Base case: valid solution found
        if (currentCapacity >= remaining) {
            if (!best.isValid || currentCapacity < best.totalCapacity || 
                (currentCapacity == best.totalCapacity && current.size() < best.propertyIds.size())) {
                best.propertyIds = new ArrayList<>(current);
                best.totalCapacity = currentCapacity;
                best.isValid = true;
            }
            return;
        }
        
        // Branch: try including/excluding each remaining property
        for (int i = index; i < properties.size(); i++) {
            Property prop = properties.get(i);
            
            // Include property
            current.add(prop.id);
            branchBoundHelper(properties, i + 1, remaining, current, currentCapacity + prop.capacity, best);
            current.remove(current.size() - 1);
        }
    }
    
    private static List<Integer> reconstructDPSolution(List<Property> properties, int[][] dp, 
                                                       boolean[][] canAchieve, int targetCapacity) {
        List<Integer> result = new ArrayList<>();
        int n = properties.size();
        int cap = targetCapacity;
        
        for (int i = n; i > 0 && cap > 0; i--) {
            Property prop = properties.get(i - 1);
            
            // Check if current property was used
            if (cap >= prop.capacity && canAchieve[i-1][cap - prop.capacity] && 
                dp[i][cap] == dp[i-1][cap - prop.capacity] + 1) {
                result.add(prop.id);
                cap -= prop.capacity;
            }
        }
        
        return result;
    }
    
    /**
     * Performance comparison of all algorithms
     */
    public static void performanceComparison() {
        System.out.println("=== Property Booking Optimizer Performance Comparison ===\n");
        
        // Create test properties
        List<Property> properties = Arrays.asList(
            new Property(1, "area1", 5),
            new Property(2, "area1", 3),
            new Property(3, "area1", 2),
            new Property(4, "area2", 4),
            new Property(5, "area1", 1),
            new Property(6, "area1", 4)
        );
        
        int groupSize = 6;
        String neighborhood = "area1";
        
        System.out.println("Properties: " + properties);
        System.out.println("Group Size: " + groupSize + ", Neighborhood: " + neighborhood);
        System.out.println();
        
        // Test all algorithms
        long startTime, endTime;
        
        startTime = System.nanoTime();
        BookingResult dpResult = findOptimalPropertiesDP(properties, groupSize, neighborhood);
        endTime = System.nanoTime();
        System.out.println("DP Result: " + dpResult + " (Time: " + (endTime - startTime)/1000 + " μs)");
        
        startTime = System.nanoTime();
        BookingResult backtrackResult = findOptimalPropertiesBacktrack(properties, groupSize, neighborhood);
        endTime = System.nanoTime();
        System.out.println("Backtrack Result: " + backtrackResult + " (Time: " + (endTime - startTime)/1000 + " μs)");
        
        startTime = System.nanoTime();
        BookingResult greedyResult = findOptimalPropertiesGreedy(properties, groupSize, neighborhood);
        endTime = System.nanoTime();
        System.out.println("Greedy Result: " + greedyResult + " (Time: " + (endTime - startTime)/1000 + " μs)");
        
        startTime = System.nanoTime();
        BookingResult bbResult = findOptimalPropertiesBranchBound(properties, groupSize, neighborhood);
        endTime = System.nanoTime();
        System.out.println("Branch & Bound Result: " + bbResult + " (Time: " + (endTime - startTime)/1000 + " μs)");
    }
    
    public static void main(String[] args) {
        System.out.println("=== Property Booking Optimizer Examples ===\n");
        
        // Example 1
        List<Property> properties1 = Arrays.asList(
            new Property(1, "area1", 5),
            new Property(2, "area1", 3),
            new Property(3, "area1", 2),
            new Property(4, "area2", 4)
        );
        
        System.out.println("Example 1:");
        System.out.println("Properties: " + properties1);
        System.out.println("Group Size: 5, Neighborhood: area1");
        BookingResult result1 = findOptimalPropertiesDP(properties1, 5, "area1");
        System.out.println("Result: " + result1);
        System.out.println();
        
        // Example 2
        System.out.println("Example 2:");
        System.out.println("Same properties, Group Size: 6, Neighborhood: area1");
        BookingResult result2 = findOptimalPropertiesDP(properties1, 6, "area1");
        System.out.println("Result: " + result2);
        System.out.println();
        
        // Example 3
        List<Property> properties3 = Arrays.asList(
            new Property(1, "area1", 5),
            new Property(2, "area1", 3)
        );
        
        System.out.println("Example 3:");
        System.out.println("Properties: " + properties3);
        System.out.println("Group Size: 10, Neighborhood: area1");
        BookingResult result3 = findOptimalPropertiesDP(properties3, 10, "area1");
        System.out.println("Result: " + result3);
        System.out.println();
        
        // Performance comparison
        performanceComparison();
    }

    /*
     * ==========================================
     * ALGORITHM COMPLEXITY ANALYSIS
     * ==========================================
     * 
     * Let:
     * - n = number of properties in target neighborhood
     * - C = total capacity sum of all properties
     * - G = group size (target capacity needed)
     * 
     * 1. DYNAMIC PROGRAMMING APPROACH:
     *    Time Complexity: O(n * C)
     *    - Build DP table of size (n+1) × (C+1)
     *    - Each cell computed in O(1) time
     *    - Solution reconstruction: O(n)
     *    Space Complexity: O(n * C)
     *    - 2D DP table storage
     *    - Can be optimized to O(C) with rolling array
     * 
     * 2. BACKTRACKING WITH PRUNING:
     *    Time Complexity: O(2^n) worst case, O(k) average with pruning
     *    - Explores all possible subset combinations
     *    - Aggressive pruning reduces search space significantly
     *    - Best case: O(n) when greedy choice works
     *    Space Complexity: O(n)
     *    - Recursion stack depth
     *    - Current solution storage
     * 
     * 3. GREEDY APPROXIMATION:
     *    Time Complexity: O(n log n)
     *    - Sorting properties by capacity: O(n log n)
     *    - Linear scan to select properties: O(n)
     *    Space Complexity: O(n)
     *    - Storage for sorted properties and result
     *    - Not guaranteed to find optimal solution
     * 
     * 4. BRANCH AND BOUND:
     *    Time Complexity: O(2^n) worst case, often much better
     *    - Explores search tree with sophisticated bounds
     *    - Early termination when bounds exceeded
     *    - Guaranteed optimal solution
     *    Space Complexity: O(n)
     *    - Recursion stack and current solution tracking
     * 
     * ALGORITHM COMPARISON:
     * 
     * OPTIMALITY RANKING:
     * 1. Dynamic Programming: Always optimal ✓
     * 2. Branch & Bound: Always optimal ✓
     * 3. Backtracking: Always optimal ✓
     * 4. Greedy: Approximation, may not be optimal ✗
     * 
     * PERFORMANCE RANKING (for typical inputs):
     * 1. Greedy: O(n log n) - Fastest but not optimal
     * 2. Dynamic Programming: O(n * C) - Good for reasonable capacity sums
     * 3. Branch & Bound: O(2^k) where k << n with good pruning
     * 4. Backtracking: O(2^n) - Can be slow without good pruning
     * 
     * SPACE EFFICIENCY RANKING:
     * 1. Greedy: O(n) - Most memory efficient
     * 2. Backtracking: O(n) - Recursion stack only
     * 3. Branch & Bound: O(n) - Recursion stack only
     * 4. Dynamic Programming: O(n * C) - Can be memory intensive
     * 
     * WHEN TO USE EACH ALGORITHM:
     * 
     * - Dynamic Programming: When C is reasonable (< 10,000) and optimal solution needed
     * - Branch & Bound: When n is small-medium (< 30) and optimal solution required
     * - Backtracking: When n is small (< 20) and you want simple optimal solution
     * - Greedy: When speed is critical and approximate solution acceptable
     * 
     * REAL-WORLD CONSIDERATIONS:
     * - For typical property booking (n < 50, C < 1000): DP is best choice
     * - For large property sets (n > 100): Use greedy for speed
     * - For critical bookings requiring optimality: Use branch & bound
     * - For rapid prototyping: Start with greedy, upgrade to DP if needed
     * 
     * PROBLEM VARIANTS:
     * - With property costs: Add cost dimension to DP
     * - With property preferences: Use weighted objective function
     * - With location constraints: Filter properties first
     * - With time windows: Add temporal dimension to state space
     */
}