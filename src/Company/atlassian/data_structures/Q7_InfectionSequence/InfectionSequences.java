package Company.atlassian.data_structures.Q7_InfectionSequence;

import java.util.*;

/**
 * Infection Sequences Count Problem
 *
 * Problem: Given n houses in a line and initially infected houses, count distinct infection sequences.
 *
 * ===================================================================================
 * KEY INSIGHT:
 * ===================================================================================
 * - Each house gets infected based on distance to nearest initially infected house
 * - Houses infected on the SAME DAY can be ordered in ANY order among themselves
 * - Total sequences = Product of (houses_infected_on_day_i)! for all days
 *
 * Example: If Day 1 has 3 houses and Day 2 has 2 houses:
 *          Total = 3! × 2! = 6 × 2 = 12 sequences
 *
 * ===================================================================================
 * SOLUTION APPROACHES:
 * ===================================================================================
 *
 * Solution 1 - BFS + Factorial (Brute Force):
 *   - Use multi-source BFS starting from all infected houses
 *   - Each BFS level = one day of infection spread
 *   - Count houses per level, multiply factorials
 *   - Time: O(n), Space: O(n)
 *
 * Solution 2 - Binary Search:
 *   - For each house, binary search to find nearest infected house (= distance = day)
 *   - Group houses by their infection day, multiply factorials
 *   - Time: O(n log m), Space: O(n)
 *
 * Solution 3 - Segment Analysis (Optimal):
 *   - Divide houses into segments: left, middle(s), right
 *   - Left/Right segments: 1 house per day (infection from one side)
 *   - Middle segments: 2 houses per day (infection from both sides)
 *   - Directly calculate day counts without iterating all houses
 *   - Time: O(m log m), Space: O(n) for factorials
 */
public class InfectionSequences {

    private static final int MOD = 1_000_000_007;

    // ==================== SOLUTION 1: BFS + Factorial ====================
    /**
     * Approach:
     * 1. Start BFS from ALL initially infected houses simultaneously
     * 2. Each BFS level represents one "day" of infection spread
     * 3. Count how many houses get infected on each day
     * 4. Answer = product of factorials of daily counts
     *
     * Why it works:
     * - BFS naturally gives us houses grouped by their distance from source
     * - Houses at same distance (same BFS level) get infected on same day
     * - Houses infected on same day can be ordered arbitrarily → factorial ways
     *
     * Time: O(n) - each house visited once
     * Space: O(n) - for visited array and queue
     */
    static class BFSSolution {

        public int countSequences(int n, int[] infectedHouses) {
            long[] factorial = precomputeFactorials(n);

            // Track visited houses
            boolean[] visited = new boolean[n + 1];

            // Initialize BFS queue with all initially infected houses
            Queue<Integer> queue = new LinkedList<>();
            for (int house : infectedHouses) {
                visited[house] = true;
                queue.offer(house);
            }

            long result = 1;

            // BFS level by level (each level = one day)
            while (!queue.isEmpty()) {
                int levelSize = queue.size();
                int newlyInfectedCount = 0;

                // Process all houses infected on current day
                for (int i = 0; i < levelSize; i++) {
                    int current = queue.poll();

                    // Try to infect left neighbor
                    if (current > 1 && !visited[current - 1]) {
                        visited[current - 1] = true;
                        queue.offer(current - 1);
                        newlyInfectedCount++;
                    }

                    // Try to infect right neighbor
                    if (current < n && !visited[current + 1]) {
                        visited[current + 1] = true;
                        queue.offer(current + 1);
                        newlyInfectedCount++;
                    }
                }

                // Multiply by factorial of houses infected this day
                if (newlyInfectedCount > 0) {
                    result = (result * factorial[newlyInfectedCount]) % MOD;
                }
            }

            return (int) result;
        }

        private long[] precomputeFactorials(int n) {
            long[] factorial = new long[n + 1];
            factorial[0] = 1;
            for (int i = 1; i <= n; i++) {
                factorial[i] = (factorial[i - 1] * i) % MOD;
            }
            return factorial;
        }
    }

    // ==================== SOLUTION 2: Binary Search ====================
    /**
     * Approach:
     * 1. Sort infected houses for binary search
     * 2. For each uninfected house, find distance to nearest infected house
     * 3. Distance = day on which it gets infected
     * 4. Group houses by day, multiply factorials
     *
     * Why it works:
     * - Distance to nearest infected = number of days until infection
     * - Binary search finds nearest infected in O(log m) time
     *
     * Time: O(n log m) where m = number of initially infected houses
     * Space: O(n)
     */
    static class BinarySearchSolution {

        public int countSequences(int n, int[] infectedHouses) {
            long[] factorial = precomputeFactorials(n);

            int[] sortedInfected = infectedHouses.clone();
            Arrays.sort(sortedInfected);

            Set<Integer> infectedSet = new HashSet<>();
            for (int house : sortedInfected) {
                infectedSet.add(house);
            }

            // Count houses infected on each day
            Map<Integer, Integer> dayCount = new HashMap<>();

            for (int house = 1; house <= n; house++) {
                if (infectedSet.contains(house)) {
                    continue;
                }

                int day = findDistanceToNearest(house, sortedInfected);
                dayCount.merge(day, 1, Integer::sum);
            }

            long result = 1;
            for (int count : dayCount.values()) {
                result = (result * factorial[count]) % MOD;
            }

            return (int) result;
        }

        private int findDistanceToNearest(int house, int[] sortedInfected) {
            int idx = Arrays.binarySearch(sortedInfected, house);

            if (idx >= 0) {
                return 0;
            }

            int insertionPoint = -idx - 1;
            int minDistance = Integer.MAX_VALUE;

            // Check left neighbor in sorted array
            if (insertionPoint > 0) {
                minDistance = Math.min(minDistance, house - sortedInfected[insertionPoint - 1]);
            }

            // Check right neighbor in sorted array
            if (insertionPoint < sortedInfected.length) {
                minDistance = Math.min(minDistance, sortedInfected[insertionPoint] - house);
            }

            return minDistance;
        }

        private long[] precomputeFactorials(int n) {
            long[] factorial = new long[n + 1];
            factorial[0] = 1;
            for (int i = 1; i <= n; i++) {
                factorial[i] = (factorial[i - 1] * i) % MOD;
            }
            return factorial;
        }
    }

    // ==================== SOLUTION 3: Segment Analysis (Optimal) ====================
    /**
     * Approach:
     * Infected houses divide the line into segments. Analyze each segment type:
     *
     * 1. LEFT SEGMENT (houses left of leftmost infected):
     *    - Infection spreads right-to-left, one house per day
     *    - Example: [1,2,3] with 4 infected → Day1:3, Day2:2, Day3:1
     *
     * 2. RIGHT SEGMENT (houses right of rightmost infected):
     *    - Infection spreads left-to-right, one house per day
     *    - Example: [6,7,8] with 5 infected → Day1:6, Day2:7, Day3:8
     *
     * 3. MIDDLE SEGMENTS (houses between two infected):
     *    - Infection spreads from BOTH ends, two houses per day
     *    - Until they meet in the middle (odd length has 1 middle house)
     *    - Example: [3,4,5] between 2 and 6 → Day1:3,5  Day2:4
     *
     * Why it works:
     * - We directly compute how many houses get infected each day
     * - No need to iterate through all houses
     *
     * Time: O(m log m) for sorting + O(m) for segments + O(n) for factorials
     * Space: O(n) for factorial array
     */
    static class OptimalSolution {

        public int countSequences(int n, int[] infectedHouses) {
            long[] factorial = precomputeFactorials(n);

            int[] sortedInfected = infectedHouses.clone();
            Arrays.sort(sortedInfected);
            int m = sortedInfected.length;

            // dayCount[i] = number of houses infected on day i
            int[] dayCount = new int[n + 1];

            // Process left segment: houses 1 to (first_infected - 1)
            int leftLength = sortedInfected[0] - 1;
            for (int day = 1; day <= leftLength; day++) {
                dayCount[day] += 1;
            }

            // Process right segment: houses (last_infected + 1) to n
            int rightLength = n - sortedInfected[m - 1];
            for (int day = 1; day <= rightLength; day++) {
                dayCount[day] += 1;
            }

            // Process middle segments
            for (int i = 0; i < m - 1; i++) {
                int segmentLength = sortedInfected[i + 1] - sortedInfected[i] - 1;

                if (segmentLength == 0) {
                    continue;
                }

                int daysWithTwoHouses = segmentLength / 2;
                boolean hasMiddleHouse = (segmentLength % 2 == 1);

                // Days 1 to daysWithTwoHouses: 2 houses each day
                for (int day = 1; day <= daysWithTwoHouses; day++) {
                    dayCount[day] += 2;
                }

                // If odd length, middle house infected on day (length+1)/2
                if (hasMiddleHouse) {
                    dayCount[daysWithTwoHouses + 1] += 1;
                }
            }

            // Calculate result: product of factorials
            long result = 1;
            for (int day = 1; day <= n; day++) {
                if (dayCount[day] > 0) {
                    result = (result * factorial[dayCount[day]]) % MOD;
                }
            }

            return (int) result;
        }

        private long[] precomputeFactorials(int n) {
            long[] factorial = new long[n + 1];
            factorial[0] = 1;
            for (int i = 1; i <= n; i++) {
                factorial[i] = (factorial[i - 1] * i) % MOD;
            }
            return factorial;
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Infection Sequences Solutions ===\n");

        BFSSolution bfsSolution = new BFSSolution();
        BinarySearchSolution binarySearchSolution = new BinarySearchSolution();
        OptimalSolution optimalSolution = new OptimalSolution();

        // Test Case 1: n=5, infectedHouses=[1,5], expected=2
        runTest("Test 1", 5, new int[]{1, 5}, 2,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 2: n=6, infectedHouses=[3,5], expected=6
        runTest("Test 2", 6, new int[]{3, 5}, 6,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 3: n=4, infectedHouses=[1], expected=1
        runTest("Test 3", 4, new int[]{1}, 1,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 4: Single middle house
        runTest("Test 4 (single middle)", 3, new int[]{1, 3}, 1,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 5: Only right segment
        runTest("Test 5 (right only)", 4, new int[]{4}, 1,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 6: Multiple infected - all at distance 1
        runTest("Test 6 (multiple infected)", 7, new int[]{2, 4, 6}, 24,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 7: Long middle segment
        runTest("Test 7 (long middle)", 7, new int[]{1, 7}, 4,
                bfsSolution, binarySearchSolution, optimalSolution);

        // Test Case 8: Adjacent infected houses
        runTest("Test 8 (adjacent infected)", 5, new int[]{2, 3}, 2,
                bfsSolution, binarySearchSolution, optimalSolution);

        System.out.println("=== All Tests Completed ===");
    }

    private static void runTest(String testName, int n, int[] infectedHouses, int expected,
                                BFSSolution bfs, BinarySearchSolution bs, OptimalSolution opt) {
        System.out.println(testName + ": n=" + n + ", infected=" + Arrays.toString(infectedHouses));

        int bfsResult = bfs.countSequences(n, infectedHouses.clone());
        int bsResult = bs.countSequences(n, infectedHouses.clone());
        int optResult = opt.countSequences(n, infectedHouses.clone());

        boolean allPassed = (bfsResult == expected) && (bsResult == expected) && (optResult == expected);

        System.out.println("  Expected: " + expected);
        System.out.println("  BFS:           " + bfsResult + " " + (bfsResult == expected ? "✓" : "✗"));
        System.out.println("  BinarySearch:  " + bsResult + " " + (bsResult == expected ? "✓" : "✗"));
        System.out.println("  Optimal:       " + optResult + " " + (optResult == expected ? "✓" : "✗"));
        System.out.println("  Status: " + (allPassed ? "PASSED" : "FAILED") + "\n");
    }
}