package practice.airbnb;

import java.util.*;

public class RentalPackageOptimizer {

    public static class Package {
        String name;
        Set<String> features;
        int price;

        public Package(String name, Set<String> features, int price) {
            this.name = name;
            this.features = new HashSet<>(features);
            this.price = price;
        }

        @Override
        public String toString() {
            return String.format("%s (Price: %d, Features: %s)", name, price, features);
        }
    }

    public static class Solution {
        int cost;
        List<String> packages;
        Set<String> coveredFeatures;

        public Solution(int cost, List<String> packages, Set<String> coveredFeatures) {
            this.cost = cost;
            this.packages = new ArrayList<>(packages);
            this.coveredFeatures = new HashSet<>(coveredFeatures);
        }

        @Override
        public String toString() {
            return String.format("Cost: %d, Packages: %s, Features: %s",
                    cost, packages, coveredFeatures);
        }
    }

    /**
     * Dynamic Programming approach using Set-based memoization
     * @param packages List of available packages
     * @param requiredFeatures Set of features that must be covered
     * @return Solution object or null if impossible
     */
    public static Solution findMinimumCost(List<Package> packages, Set<String> requiredFeatures) {
        // Check if solution is possible
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }

        if (!allFeatures.containsAll(requiredFeatures)) {
            return null; // Impossible to cover all features
        }

        // Memoization map: (covered features, package index) -> (min cost, selected packages)
        Map<String, DPEntry> memo = new HashMap<>();

        DPEntry result = findMinCostRecursive(packages, requiredFeatures, new HashSet<>(), 0, memo);

        if (result == null || result.cost == Integer.MAX_VALUE) {
            return null;
        }

        // Calculate actual covered features
        Set<String> actualCovered = new HashSet<>();
        for (String packageName : result.selectedPackages) {
            for (Package pkg : packages) {
                if (pkg.name.equals(packageName)) {
                    actualCovered.addAll(pkg.features);
                    break;
                }
            }
        }

        return new Solution(result.cost, result.selectedPackages, actualCovered);
    }

    private static class DPEntry {
        int cost;
        List<String> selectedPackages;

        public DPEntry(int cost, List<String> selectedPackages) {
            this.cost = cost;
            this.selectedPackages = new ArrayList<>(selectedPackages);
        }
    }

    private static DPEntry findMinCostRecursive(List<Package> packages, Set<String> requiredFeatures,
                                                Set<String> currentlyCovered, int packageIndex,
                                                Map<String, DPEntry> memo) {

        // Base case: all required features covered
        if (currentlyCovered.containsAll(requiredFeatures)) {
            return new DPEntry(0, new ArrayList<>());
        }

        // Base case: no more packages to consider
        if (packageIndex >= packages.size()) {
            return new DPEntry(Integer.MAX_VALUE, new ArrayList<>());
        }

        // Create memo key that includes both covered features and package index
        String memoKey = currentlyCovered + ":" + packageIndex;
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey);
        }

        Package currentPackage = packages.get(packageIndex);

        // Option 1: Don't take current package
        DPEntry withoutCurrent = findMinCostRecursive(packages, requiredFeatures,
                currentlyCovered, packageIndex + 1, memo);

        // Option 2: Take current package
        Set<String> newCovered = new HashSet<>(currentlyCovered);
        newCovered.addAll(currentPackage.features);

        DPEntry withCurrent = findMinCostRecursive(packages, requiredFeatures,
                newCovered, packageIndex + 1, memo);

        DPEntry result;
        if (withCurrent.cost != Integer.MAX_VALUE) {
            int totalCostWithCurrent = currentPackage.price + withCurrent.cost;
            if (withoutCurrent.cost == Integer.MAX_VALUE || totalCostWithCurrent < withoutCurrent.cost) {
                List<String> newPackages = new ArrayList<>();
                newPackages.add(currentPackage.name);
                newPackages.addAll(withCurrent.selectedPackages);
                result = new DPEntry(totalCostWithCurrent, newPackages);
            } else {
                result = withoutCurrent;
            }
        } else {
            result = withoutCurrent;
        }

        memo.put(memoKey, result);
        return result;
    }

    /**
     * OPTIMIZED: Dynamic Programming using Bitmasking
     * Represents feature sets as bitmasks for O(1) operations
     * @param packages List of available packages
     * @param requiredFeatures Set of features that must be covered
     * @return Solution object or null if impossible
     */
    public static Solution findMinimumCostBitmask(List<Package> packages, Set<String> requiredFeatures) {
        // Create feature-to-bit mapping
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }
        
        if (!allFeatures.containsAll(requiredFeatures)) {
            return null; // Impossible to cover all features
        }
        
        List<String> featureList = new ArrayList<>(allFeatures);
        Map<String, Integer> featureToBit = new HashMap<>();
        for (int i = 0; i < featureList.size(); i++) {
            featureToBit.put(featureList.get(i), i);
        }
        
        // Create target mask for required features
        int targetMask = 0;
        for (String feature : requiredFeatures) {
            targetMask |= (1 << featureToBit.get(feature));
        }
        
        // Precompute package masks
        int[] packageMasks = new int[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            int mask = 0;
            for (String feature : packages.get(i).features) {
                mask |= (1 << featureToBit.get(feature));
            }
            packageMasks[i] = mask;
        }
        
        // DP: dp[mask] = minimum cost to achieve this feature mask
        int maxMask = 1 << featureList.size();
        int[] dp = new int[maxMask];
        int[] parent = new int[maxMask]; // Track which package was used
        
        Arrays.fill(dp, Integer.MAX_VALUE);
        Arrays.fill(parent, -1);
        dp[0] = 0;
        
        // Fill DP table
        for (int i = 0; i < packages.size(); i++) {
            // Process in reverse to avoid using updated values in same iteration
            for (int mask = maxMask - 1; mask >= 0; mask--) {
                if (dp[mask] == Integer.MAX_VALUE) continue;
                
                int newMask = mask | packageMasks[i];
                int newCost = dp[mask] + packages.get(i).price;
                
                if (newCost < dp[newMask]) {
                    dp[newMask] = newCost;
                    parent[newMask] = i;
                }
            }
        }
        
        // Check if target is achievable
        if (dp[targetMask] == Integer.MAX_VALUE) {
            return null;
        }
        
        // Reconstruct solution
        List<String> selectedPackages = new ArrayList<>();
        Set<String> coveredFeatures = new HashSet<>();
        int currentMask = targetMask;
        
        while (currentMask != 0 && parent[currentMask] != -1) {
            int packageIndex = parent[currentMask];
            Package pkg = packages.get(packageIndex);
            selectedPackages.add(pkg.name);
            coveredFeatures.addAll(pkg.features);
            currentMask = currentMask & ~packageMasks[packageIndex];
        }
        
        Collections.reverse(selectedPackages);
        return new Solution(dp[targetMask], selectedPackages, coveredFeatures);
    }
    
    /**
     * OPTIMIZED: Top-Down DP with Bitmasking
     * Uses memoization with bitmask state representation
     */
    public static Solution findMinimumCostTopDownBitmask(List<Package> packages, Set<String> requiredFeatures) {
        // Setup bitmasks (same as above)
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }
        
        if (!allFeatures.containsAll(requiredFeatures)) {
            return null;
        }
        
        List<String> featureList = new ArrayList<>(allFeatures);
        Map<String, Integer> featureToBit = new HashMap<>();
        for (int i = 0; i < featureList.size(); i++) {
            featureToBit.put(featureList.get(i), i);
        }
        
        int targetMask = 0;
        for (String feature : requiredFeatures) {
            targetMask |= (1 << featureToBit.get(feature));
        }
        
        int[] packageMasks = new int[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            int mask = 0;
            for (String feature : packages.get(i).features) {
                mask |= (1 << featureToBit.get(feature));
            }
            packageMasks[i] = mask;
        }
        
        // Memoization: (mask, packageIndex) -> (cost, packageList)
        Map<String, BitDPEntry> memo = new HashMap<>();
        
        BitDPEntry result = topDownBitmaskHelper(0, 0, targetMask, packages, packageMasks, memo);
        
        if (result == null || result.cost == Integer.MAX_VALUE) {
            return null;
        }
        
        // Calculate covered features
        Set<String> coveredFeatures = new HashSet<>();
        for (String packageName : result.selectedPackages) {
            for (Package pkg : packages) {
                if (pkg.name.equals(packageName)) {
                    coveredFeatures.addAll(pkg.features);
                    break;
                }
            }
        }
        
        return new Solution(result.cost, result.selectedPackages, coveredFeatures);
    }
    
    private static class BitDPEntry {
        int cost;
        List<String> selectedPackages;
        
        BitDPEntry(int cost, List<String> selectedPackages) {
            this.cost = cost;
            this.selectedPackages = new ArrayList<>(selectedPackages);
        }
    }
    
    private static BitDPEntry topDownBitmaskHelper(int packageIndex, int currentMask, int targetMask,
                                                  List<Package> packages, int[] packageMasks,
                                                  Map<String, BitDPEntry> memo) {
        
        // Base case: target achieved
        if ((currentMask & targetMask) == targetMask) {
            return new BitDPEntry(0, new ArrayList<>());
        }
        
        // Base case: no more packages
        if (packageIndex >= packages.size()) {
            return new BitDPEntry(Integer.MAX_VALUE, new ArrayList<>());
        }
        
        // Check memoization
        String key = currentMask + "," + packageIndex;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        
        // Option 1: Skip current package
        BitDPEntry skipResult = topDownBitmaskHelper(packageIndex + 1, currentMask, targetMask,
                                                    packages, packageMasks, memo);
        
        // Option 2: Take current package
        int newMask = currentMask | packageMasks[packageIndex];
        BitDPEntry takeResult = topDownBitmaskHelper(packageIndex + 1, newMask, targetMask,
                                                    packages, packageMasks, memo);
        
        BitDPEntry bestResult;
        if (takeResult.cost != Integer.MAX_VALUE) {
            int totalCostWithCurrent = packages.get(packageIndex).price + takeResult.cost;
            if (skipResult.cost == Integer.MAX_VALUE || totalCostWithCurrent < skipResult.cost) {
                List<String> newPackages = new ArrayList<>();
                newPackages.add(packages.get(packageIndex).name);
                newPackages.addAll(takeResult.selectedPackages);
                bestResult = new BitDPEntry(totalCostWithCurrent, newPackages);
            } else {
                bestResult = skipResult;
            }
        } else {
            bestResult = skipResult;
        }
        
        memo.put(key, bestResult);
        return bestResult;
    }
    
    /**
     * OPTIMIZED: Bottom-Up DP with Bitmasking (derived from top-down approach)
     * Builds solutions iteratively from smaller subproblems using tabulation
     */
    public static Solution bottomUpBitmaskDP(List<Package> packages, Set<String> requiredFeatures) {
        // Setup bitmasks (same as top-down approach)
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }
        
        if (!allFeatures.containsAll(requiredFeatures)) {
            return null;
        }
        
        List<String> featureList = new ArrayList<>(allFeatures);
        Map<String, Integer> featureToBit = new HashMap<>();
        for (int i = 0; i < featureList.size(); i++) {
            featureToBit.put(featureList.get(i), i);
        }
        
        int targetMask = 0;
        for (String feature : requiredFeatures) {
            targetMask |= (1 << featureToBit.get(feature));
        }
        
        int[] packageMasks = new int[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            int mask = 0;
            for (String feature : packages.get(i).features) {
                mask |= (1 << featureToBit.get(feature));
            }
            packageMasks[i] = mask;
        }
        
        // Bottom-up DP table: dp[mask][pkgIndex] = min cost to achieve mask using packages 0...pkgIndex-1
        int maxMask = 1 << featureList.size();
        int[][] dp = new int[maxMask][packages.size() + 1];
        int[][] parent = new int[maxMask][packages.size() + 1]; // Track which package was used
        
        // Initialize DP table
        for (int mask = 0; mask < maxMask; mask++) {
            Arrays.fill(dp[mask], Integer.MAX_VALUE);
            Arrays.fill(parent[mask], -1);
        }
        
        // Base case: no features needed, no packages used
        for (int i = 0; i <= packages.size(); i++) {
            dp[0][i] = 0;
        }
        
        // Fill DP table bottom-up
        for (int pkgIndex = 1; pkgIndex <= packages.size(); pkgIndex++) {
            Package currentPkg = packages.get(pkgIndex - 1);
            int currentMask = packageMasks[pkgIndex - 1];
            
            for (int mask = 0; mask < maxMask; mask++) {
                // Option 1: Don't take current package
                dp[mask][pkgIndex] = dp[mask][pkgIndex - 1];
                parent[mask][pkgIndex] = -1; // No package taken
                
                // Option 2: Take current package (if it helps achieve the mask)
                int prevMask = mask & ~currentMask; // Remove features that current package provides
                if (dp[prevMask][pkgIndex - 1] != Integer.MAX_VALUE) {
                    int costWithCurrent = dp[prevMask][pkgIndex - 1] + currentPkg.price;
                    if (costWithCurrent < dp[mask][pkgIndex]) {
                        dp[mask][pkgIndex] = costWithCurrent;
                        parent[mask][pkgIndex] = pkgIndex - 1; // Package index taken
                    }
                }
            }
        }
        
        // Check if target is achievable
        if (dp[targetMask][packages.size()] == Integer.MAX_VALUE) {
            return null;
        }
        
        // Reconstruct solution by backtracking
        List<String> selectedPackages = new ArrayList<>();
        Set<String> coveredFeatures = new HashSet<>();
        int currentMask = targetMask;
        int currentPkgIndex = packages.size();
        
        while (currentMask != 0 && currentPkgIndex > 0) {
            int packageIndex = parent[currentMask][currentPkgIndex];
            if (packageIndex != -1) {
                // Package was taken
                Package pkg = packages.get(packageIndex);
                selectedPackages.add(pkg.name);
                coveredFeatures.addAll(pkg.features);
                currentMask = currentMask & ~packageMasks[packageIndex];
            }
            currentPkgIndex--;
        }
        
        Collections.reverse(selectedPackages);
        return new Solution(dp[targetMask][packages.size()], selectedPackages, coveredFeatures);
    }

    /**
     * OPTIMIZED: Iterative DP with Bitmasking using Priority Queue
     * Combines Dijkstra-like exploration with efficient bitmask operations
     */
    public static Solution iterativeDPBitmask(List<Package> packages, Set<String> requiredFeatures) {
        // Setup bitmasks
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }
        
        if (!allFeatures.containsAll(requiredFeatures)) {
            return null;
        }
        
        List<String> featureList = new ArrayList<>(allFeatures);
        Map<String, Integer> featureToBit = new HashMap<>();
        for (int i = 0; i < featureList.size(); i++) {
            featureToBit.put(featureList.get(i), i);
        }
        
        int targetMask = 0;
        for (String feature : requiredFeatures) {
            targetMask |= (1 << featureToBit.get(feature));
        }
        
        int[] packageMasks = new int[packages.size()];
        for (int i = 0; i < packages.size(); i++) {
            int mask = 0;
            for (String feature : packages.get(i).features) {
                mask |= (1 << featureToBit.get(feature));
            }
            packageMasks[i] = mask;
        }
        
        // Priority queue with bitmask states
        PriorityQueue<BitState> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.cost, b.cost));
        Map<Integer, Integer> visited = new HashMap<>(); // mask -> min cost
        
        pq.offer(new BitState(0, 0, new ArrayList<>()));
        visited.put(0, 0);
        
        while (!pq.isEmpty()) {
            BitState current = pq.poll();
            
            // Skip if we've found better path to this state
            if (visited.containsKey(current.mask) && visited.get(current.mask) < current.cost) {
                continue;
            }
            
            // Check if target achieved
            if ((current.mask & targetMask) == targetMask) {
                Set<String> coveredFeatures = new HashSet<>();
                for (String packageName : current.selectedPackages) {
                    for (Package pkg : packages) {
                        if (pkg.name.equals(packageName)) {
                            coveredFeatures.addAll(pkg.features);
                            break;
                        }
                    }
                }
                return new Solution(current.cost, current.selectedPackages, coveredFeatures);
            }
            
            // Try adding each package
            for (int i = 0; i < packages.size(); i++) {
                if (current.selectedPackages.contains(packages.get(i).name)) continue;
                
                int newMask = current.mask | packageMasks[i];
                int newCost = current.cost + packages.get(i).price;
                
                if (visited.containsKey(newMask) && visited.get(newMask) <= newCost) {
                    continue;
                }
                
                List<String> newSelected = new ArrayList<>(current.selectedPackages);
                newSelected.add(packages.get(i).name);
                
                pq.offer(new BitState(newMask, newCost, newSelected));
                visited.put(newMask, newCost);
            }
        }
        
        return null;
    }
    
    private static class BitState {
        int mask;
        int cost;
        List<String> selectedPackages;
        
        BitState(int mask, int cost, List<String> selectedPackages) {
            this.mask = mask;
            this.cost = cost;
            this.selectedPackages = new ArrayList<>(selectedPackages);
        }
    }

    /**
     * Greedy approximation algorithm
     * @param packages List of available packages
     * @param requiredFeatures Set of features that must be covered
     * @return Solution object or null if impossible
     */
    public static Solution greedyApproach(List<Package> packages, Set<String> requiredFeatures) {
        Set<String> remaining = new HashSet<>(requiredFeatures);
        Set<String> covered = new HashSet<>();
        List<String> selected = new ArrayList<>();
        int totalCost = 0;

        while (!remaining.isEmpty()) {
            Package bestPackage = null;
            double bestRatio = 0;
            int bestNewFeatures = 0;

            for (Package pkg : packages) {
                if (selected.contains(pkg.name)) continue;

                Set<String> newFeatures = new HashSet<>(pkg.features);
                newFeatures.retainAll(remaining);
                int newFeatureCount = newFeatures.size();

                if (newFeatureCount > 0) {
                    double ratio = (double) newFeatureCount / pkg.price;
                    if (bestPackage == null || ratio > bestRatio ||
                            (Math.abs(ratio - bestRatio) < 1e-9 && newFeatureCount > bestNewFeatures)) {
                        bestPackage = pkg;
                        bestRatio = ratio;
                        bestNewFeatures = newFeatureCount;
                    }
                }
            }

            if (bestPackage == null) {
                return null; // No solution possible
            }

            selected.add(bestPackage.name);
            covered.addAll(bestPackage.features);
            remaining.removeAll(bestPackage.features);
            totalCost += bestPackage.price;
        }

        return new Solution(totalCost, selected, covered);
    }

    /**
     * Iterative Dynamic Programming using Dijkstra-like approach with priority queue
     * @param packages List of available packages
     * @param requiredFeatures Set of features that must be covered
     * @return Solution object or null if impossible
     */
    public static Solution iterativeDPSolution(List<Package> packages, Set<String> requiredFeatures) {
        // Check if solution is possible
        Set<String> allFeatures = new HashSet<>();
        for (Package pkg : packages) {
            allFeatures.addAll(pkg.features);
        }

        if (!allFeatures.containsAll(requiredFeatures)) {
            return null;
        }

        // Priority queue for Dijkstra-like approach: states ordered by cost
        PriorityQueue<State> pq = new PriorityQueue<>((a, b) -> Integer.compare(a.cost, b.cost));
        Map<Set<String>, Integer> visited = new HashMap<>(); // features -> min cost to achieve them

        pq.offer(new State(new HashSet<>(), 0, new ArrayList<>()));
        visited.put(new HashSet<>(), 0);

        while (!pq.isEmpty()) {
            State current = pq.poll();

            // Skip if we've already found a better path to this state
            if (visited.containsKey(current.coveredFeatures) && 
                visited.get(current.coveredFeatures) < current.cost) {
                continue;
            }

            // If all features covered, we found the optimal solution
            if (current.coveredFeatures.containsAll(requiredFeatures)) {
                Set<String> actualCovered = new HashSet<>();
                for (String packageName : current.selectedPackages) {
                    for (Package pkg : packages) {
                        if (pkg.name.equals(packageName)) {
                            actualCovered.addAll(pkg.features);
                            break;
                        }
                    }
                }
                return new Solution(current.cost, current.selectedPackages, actualCovered);
            }

            // Try adding each package
            for (Package pkg : packages) {
                if (current.selectedPackages.contains(pkg.name)) continue;

                Set<String> newCovered = new HashSet<>(current.coveredFeatures);
                newCovered.addAll(pkg.features);
                int newCost = current.cost + pkg.price;

                // Skip if we've seen this state with better or equal cost
                if (visited.containsKey(newCovered) && visited.get(newCovered) <= newCost) {
                    continue;
                }

                List<String> newSelected = new ArrayList<>(current.selectedPackages);
                newSelected.add(pkg.name);

                pq.offer(new State(newCovered, newCost, newSelected));
                visited.put(new HashSet<>(newCovered), newCost);
            }
        }

        return null;
    }

    private static class State {
        Set<String> coveredFeatures;
        int cost;
        List<String> selectedPackages;

        public State(Set<String> coveredFeatures, int cost, List<String> selectedPackages) {
            this.coveredFeatures = new HashSet<>(coveredFeatures);
            this.cost = cost;
            this.selectedPackages = new ArrayList<>(selectedPackages);
        }
    }

    // Helper method to create sets easily for Java versions without Set.of()
    private static Set<String> createSet(String... elements) {
        Set<String> set = new HashSet<>();
        for (String element : elements) {
            set.add(element);
        }
        return set;
    }

    // Example usage and test
    public static void main(String[] args) {
        // Create sample packages
        List<Package> packages = Arrays.asList(
                new Package("Basic", createSet("wifi", "parking"), 100),
                new Package("Premium", createSet("wifi", "gym", "pool"), 200),
                new Package("Deluxe", createSet("parking", "gym", "spa"), 250),
                new Package("Ultimate", createSet("wifi", "parking", "gym", "pool", "spa"), 400),
                new Package("Budget", createSet("wifi"), 50),
                new Package("Sports", createSet("gym", "pool"), 150)
        );

        Set<String> required = createSet("wifi", "gym", "parking");

        System.out.println("Available packages:");
        for (Package pkg : packages) {
            System.out.println("  " + pkg);
        }
        System.out.println("\nRequired features: " + required);

        // Performance comparison
        long startTime, endTime;
        
        System.out.println("\n=== PERFORMANCE COMPARISON ===");
        
        startTime = System.nanoTime();
        Solution dpSolution = findMinimumCost(packages, required);
        endTime = System.nanoTime();
        System.out.println("Original Recursive DP: " + (endTime - startTime)/1000 + " μs");
        if (dpSolution != null) System.out.println("  " + dpSolution);
        
        startTime = System.nanoTime();
        Solution bitmaskSolution = findMinimumCostBitmask(packages, required);
        endTime = System.nanoTime();
        System.out.println("Optimized Bitmask DP: " + (endTime - startTime)/1000 + " μs");
        if (bitmaskSolution != null) System.out.println("  " + bitmaskSolution);
        
        startTime = System.nanoTime();
        Solution topDownBitmaskSolution = findMinimumCostTopDownBitmask(packages, required);
        endTime = System.nanoTime();
        System.out.println("Top-Down Bitmask DP: " + (endTime - startTime)/1000 + " μs");
        if (topDownBitmaskSolution != null) System.out.println("  " + topDownBitmaskSolution);
        
        startTime = System.nanoTime();
        Solution bottomUpBitmaskSolution = bottomUpBitmaskDP(packages, required);
        endTime = System.nanoTime();
        System.out.println("Bottom-Up Bitmask DP: " + (endTime - startTime)/1000 + " μs");
        if (bottomUpBitmaskSolution != null) System.out.println("  " + bottomUpBitmaskSolution);
        
        startTime = System.nanoTime();
        Solution iterativeSolution = iterativeDPSolution(packages, required);
        endTime = System.nanoTime();
        System.out.println("Original Iterative DP: " + (endTime - startTime)/1000 + " μs");
        if (iterativeSolution != null) System.out.println("  " + iterativeSolution);
        
        startTime = System.nanoTime();
        Solution iterativeBitmaskSolution = iterativeDPBitmask(packages, required);
        endTime = System.nanoTime();
        System.out.println("Iterative Bitmask DP: " + (endTime - startTime)/1000 + " μs");
        if (iterativeBitmaskSolution != null) System.out.println("  " + iterativeBitmaskSolution);
        
        startTime = System.nanoTime();
        Solution greedy = greedyApproach(packages, required);
        endTime = System.nanoTime();
        System.out.println("Greedy Approach: " + (endTime - startTime)/1000 + " μs");
        if (greedy != null) System.out.println("  " + greedy);

        System.out.println("\n=== Recursive DP Solution ===");
        Solution dpSolution1 = findMinimumCost(packages, required);
        if (dpSolution1 != null) {
            System.out.println(dpSolution1);
        } else {
            System.out.println("No solution possible");
        }

        System.out.println("\n=== Iterative DP Solution ===");
        Solution iterativeSolution1 = iterativeDPSolution(packages, required);
        if (iterativeSolution1 != null) {
            System.out.println(iterativeSolution1);
        } else {
            System.out.println("No solution possible");
        }

        System.out.println("\n=== Greedy Solution ===");
        Solution greedy1 = greedyApproach(packages, required);
        if (greedy != null) {
            System.out.println(greedy1);
        } else {
            System.out.println("No solution possible");
        }

        // Test edge case: impossible requirements
        System.out.println("\n=== Edge Case: Impossible Requirements ===");
        Set<String> impossible = createSet("wifi", "gym", "parking", "nonexistent");
        Solution impossibleSolution = findMinimumCost(packages, impossible);
        System.out.println(impossibleSolution != null ? impossibleSolution : "No solution possible");

        // Test single package solution
        System.out.println("\n=== Test: Single Package Covers All ===");
        Set<String> singlePackageNeeds = createSet("wifi", "gym", "pool");
        Solution singleSolution = findMinimumCost(packages, singlePackageNeeds);
        System.out.println(singleSolution != null ? singleSolution : "No solution possible");

        // Test case where greedy fails to find optimal solution
        System.out.println("\n=== Greedy vs Optimal Comparison ===");
        List<Package> greedyFailCase = Arrays.asList(
                new Package("A", createSet("x", "y"), 10),
                new Package("B", createSet("x", "z"), 10), 
                new Package("C", createSet("y", "z"), 10),
                new Package("D", createSet("x", "y", "z"), 19)
        );
        
        Set<String> greedyRequired = createSet("x", "y", "z");
        
        System.out.println("Greedy failure test packages:");
        for (Package pkg : greedyFailCase) {
            System.out.println("  " + pkg);
        }
        System.out.println("Required features: " + greedyRequired);
        
        Solution optimalSol = findMinimumCost(greedyFailCase, greedyRequired);
        Solution greedySol = greedyApproach(greedyFailCase, greedyRequired);
        
        System.out.println("Optimal solution: " + optimalSol);
        System.out.println("Greedy solution:  " + greedySol);
        
        if (optimalSol != null && greedySol != null) {
            System.out.println("Optimal cost: " + optimalSol.cost);
            System.out.println("Greedy cost:  " + greedySol.cost);
            System.out.println("Greedy is optimal: " + (optimalSol.cost == greedySol.cost));
        }
    }

    /*
     * ==========================================
     * TIME AND SPACE COMPLEXITY ANALYSIS
     * ==========================================
     * 
     * Let:
     * - n = number of packages
     * - f = number of unique features across all packages
     * - r = number of required features
     * 
     * 1. RECURSIVE DP SOLUTION (findMinimumCost):
     *    Time Complexity: O(2^n * f)
     *    - Each package has 2 choices (take/don't take)
     *    - For each state, we perform Set operations (toString, containsAll)
     *    - Memoization reduces duplicate work, but worst case is exponential
     *    Space Complexity: O(2^f * n + n)
     *    - Memoization map can store up to 2^f different feature combinations
     *    - Each memo entry stores package lists
     *    - Recursion depth is O(n)
     * 
     * 2. ITERATIVE DP SOLUTION (iterativeDPSolution):
     *    Time Complexity: O(2^f * n * f)
     *    - Priority queue can contain up to 2^f states (all possible feature combinations)
     *    - For each state, we try n packages
     *    - Set operations (HashSet creation, containsAll) take O(f) time
     *    Space Complexity: O(2^f * (n + f))
     *    - Priority queue and visited map can store up to 2^f states
     *    - Each state stores feature set O(f) and package list O(n)
     * 
     * 3. BITMASK DP SOLUTIONS (findMinimumCostBitmask, topDownBitmask, bottomUpBitmaskDP):
     *    Time Complexity: O(2^f * n)
     *    - There are 2^f possible feature combinations (bitmasks)
     *    - For each mask state, we consider n packages
     *    - Bitwise operations are O(1)
     *    Space Complexity: O(2^f * n) for bottom-up, O(2^f) for others
     *    - Bottom-up: 2D table dp[2^f][n+1] 
     *    - Top-down: memoization map stores up to 2^f states
     *    - Standard bitmask: 1D array of size 2^f
     * 
     * 4. ITERATIVE BITMASK DP (iterativeDPBitmask):
     *    Time Complexity: O(2^f * n^2)
     *    - Priority queue processes up to 2^f states
     *    - For each state, tries n packages
     *    - Package duplicate checking takes O(n) per state
     *    Space Complexity: O(2^f * n)
     *    - Priority queue and visited map store states with package lists
     * 
     * 5. GREEDY APPROACH (greedyApproach):
     *    Time Complexity: O(r * n * f)
     *    - While loop runs at most r times (one per required feature)
     *    - Inner loop over n packages
     *    - Set operations (retainAll, removeAll) take O(f) time
     *    Space Complexity: O(f + n)
     *    - Storage for remaining features O(f), covered features O(f), selected packages O(n)
     * 
     * PRACTICAL CONSIDERATIONS:
     * - Recursive DP: Best for small number of packages (n ≤ 20)
     * - Bitmask DP variants: Optimal for moderate feature counts (f ≤ 20), very efficient
     * - Bottom-up bitmask: More memory but avoids recursion overhead
     * - Top-down bitmask: Less memory, good for sparse state spaces
     * - Iterative DP: Better for larger package sets but many feature combinations
     * - Greedy: Fast approximation, good for large inputs, may not find optimal solution
     * - Feature set size (f) is the main driver of exponential complexity in exact algorithms
     */
}