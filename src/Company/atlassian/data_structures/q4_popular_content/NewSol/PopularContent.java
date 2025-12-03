package Company.atlassian.data_structures.q4_popular_content.NewSol;

import java.util.*;

/**
 * Popular Content - Most Popular Content Tracker
 *
 * ===================================================================================
 * PROBLEM:
 * ===================================================================================
 * - Stream of (contentId, action) where action is increasePopularity or decreasePopularity
 * - Each action changes popularity by 1
 * - Return the most popular contentId at any time
 * - Return -1 if no contentId has popularity > 0
 *
 * ===================================================================================
 * KEY DIFFERENCE FROM COMMODITY PRICES:
 * ===================================================================================
 * - Multiple contentIds can have the SAME popularity
 * - We return the contentId (not the popularity value)
 * - Need to handle popularity <= 0 case
 *
 * ===================================================================================
 * APPROACHES:
 * ===================================================================================
 *
 * Approach 1: Brute Force (HashMap only)
 *   - increase/decrease: O(1)
 *   - getMostPopular: O(n)
 *
 * Approach 2: HashMap + TreeMap
 *   - increase/decrease: O(log n)
 *   - getMostPopular: O(1)
 *
 * Approach 3: Cached Max Variable
 *   - increase/decrease: O(1) avg, O(n) when max invalidated
 *   - getMostPopular: O(1)
 *
 * ===================================================================================
 */
public class PopularContent {

    // ==================== APPROACH 1: Brute Force ====================
    /**
     * Simple HashMap storage.
     *
     * increase/decrease: O(1)
     * getMostPopular: O(n) - scan all entries
     */
    static class BruteForce {

        private Map<Integer, Integer> contentToPopularity;

        public BruteForce() {
            this.contentToPopularity = new HashMap<>();
        }

        // O(1)
        public void increasePopularity(int contentId) {
            contentToPopularity.put(contentId, contentToPopularity.getOrDefault(contentId, 0) + 1);
        }

        // O(1)
        public void decreasePopularity(int contentId) {
            contentToPopularity.put(contentId, contentToPopularity.getOrDefault(contentId, 0) - 1);
        }

        // O(n) - scan all entries
        public int getMostPopular() {
            int maxPopularity = 0;  // Must be > 0
            int mostPopularId = -1;

            for (Map.Entry<Integer, Integer> entry : contentToPopularity.entrySet()) {
                if (entry.getValue() > maxPopularity) {
                    maxPopularity = entry.getValue();
                    mostPopularId = entry.getKey();
                }
            }

            return mostPopularId;
        }

        // O(1)
        public int getPopularity(int contentId) {
            return contentToPopularity.getOrDefault(contentId, 0);
        }
    }

    // ==================== APPROACH 2: HashMap + TreeMap ====================
    /**
     * HashMap for contentId lookup + TreeMap for sorted popularities.
     *
     * Key insight: Multiple contentIds can have same popularity,
     * so we track: popularity -> Set of contentIds
     *
     * increase/decrease: O(log n) - TreeMap operations
     * getMostPopular: O(1) - TreeMap.lastEntry()
     */
    static class OptimizedTreeMap {

        private Map<Integer, Integer> contentToPopularity;       // contentId -> popularity
        private TreeMap<Integer, Set<Integer>> popularityToContents;  // popularity -> set of contentIds

        public OptimizedTreeMap() {
            this.contentToPopularity = new HashMap<>();
            this.popularityToContents = new TreeMap<>();
        }

        // O(log n)
        public void increasePopularity(int contentId) {
            int oldPopularity = contentToPopularity.getOrDefault(contentId, 0);
            int newPopularity = oldPopularity + 1;

            updatePopularity(contentId, oldPopularity, newPopularity);
        }

        // O(log n)
        public void decreasePopularity(int contentId) {
            int oldPopularity = contentToPopularity.getOrDefault(contentId, 0);
            int newPopularity = oldPopularity - 1;

            updatePopularity(contentId, oldPopularity, newPopularity);
        }

        // O(1)
        public int getMostPopular() {
            // Get highest popularity > 0
            Map.Entry<Integer, Set<Integer>> highest = popularityToContents.lastEntry();

            if (highest == null || highest.getKey() <= 0) {
                return -1;
            }

            // Return any contentId with highest popularity
            return highest.getValue().iterator().next();
        }

        // O(1)
        public int getPopularity(int contentId) {
            return contentToPopularity.getOrDefault(contentId, 0);
        }

        // O(log n)
        private void updatePopularity(int contentId, int oldPopularity, int newPopularity) {
            // Remove from old popularity bucket
            if (contentToPopularity.containsKey(contentId)) {
                Set<Integer> oldSet = popularityToContents.get(oldPopularity);
                if (oldSet != null) {
                    oldSet.remove(contentId);
                    if (oldSet.isEmpty()) {
                        popularityToContents.remove(oldPopularity);
                    }
                }
            }

            // Update contentToPopularity
            contentToPopularity.put(contentId, newPopularity);

            // Add to new popularity bucket
            popularityToContents.computeIfAbsent(newPopularity, k -> new HashSet<>()).add(contentId);
        }
    }

    // ==================== APPROACH 3: Cached Max Variable ====================
    /**
     * HashMap + cached max variables.
     *
     * increase/decrease: O(1) average, O(n) when max invalidated
     * getMostPopular: O(1)
     *
     * When is recalculation needed?
     * - When we DECREASE the content that currently has max popularity
     *   AND its new popularity is no longer the max
     */
    static class OptimizedCachedMax {

        private Map<Integer, Integer> contentToPopularity;
        private int cachedMaxPopularity;
        private int cachedMostPopularId;

        public OptimizedCachedMax() {
            this.contentToPopularity = new HashMap<>();
            this.cachedMaxPopularity = 0;
            this.cachedMostPopularId = -1;
        }

        // O(1)
        public void increasePopularity(int contentId) {
            int newPopularity = contentToPopularity.getOrDefault(contentId, 0) + 1;
            contentToPopularity.put(contentId, newPopularity);

            // If this is now the new max, update cache
            if (newPopularity > cachedMaxPopularity) {
                cachedMaxPopularity = newPopularity;
                cachedMostPopularId = contentId;
            }
        }

        // O(1) average, O(n) worst case
        public void decreasePopularity(int contentId) {
            int oldPopularity = contentToPopularity.getOrDefault(contentId, 0);
            int newPopularity = oldPopularity - 1;
            contentToPopularity.put(contentId, newPopularity);

            // If we decreased the most popular content, need to check/recalculate
            if (contentId == cachedMostPopularId) {
                // It might still be the max if it was much higher than others
                // But we need to recalculate to be sure
                recalculateMax();
            }
        }

        // O(1)
        public int getMostPopular() {
            if (cachedMaxPopularity <= 0) {
                return -1;
            }
            return cachedMostPopularId;
        }

        // O(1)
        public int getPopularity(int contentId) {
            return contentToPopularity.getOrDefault(contentId, 0);
        }

        // O(n) - only called when max might be invalidated
        private void recalculateMax() {
            cachedMaxPopularity = 0;
            cachedMostPopularId = -1;

            for (Map.Entry<Integer, Integer> entry : contentToPopularity.entrySet()) {
                if (entry.getValue() > cachedMaxPopularity) {
                    cachedMaxPopularity = entry.getValue();
                    cachedMostPopularId = entry.getKey();
                }
            }
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Popular Content Solutions ===\n");

        testBruteForce();
        testOptimizedTreeMap();
        testOptimizedCachedMax();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testBruteForce() {
        System.out.println("--- Approach 1: Brute Force ---");

        BruteForce tracker = new BruteForce();

        // Test 1: Empty - should return -1
        assertResult(-1, tracker.getMostPopular(), "Empty tracker");

        // Test 2: Single content
        tracker.increasePopularity(101);
        assertResult(101, tracker.getMostPopular(), "Single content");

        // Test 3: Multiple contents
        tracker.increasePopularity(102);
        tracker.increasePopularity(102);
        tracker.increasePopularity(102);
        assertResult(102, tracker.getMostPopular(), "102 has popularity 3");

        // Test 4: Decrease popularity
        tracker.decreasePopularity(102);
        tracker.decreasePopularity(102);
        assertResult(102, tracker.getMostPopular(), "102 still has popularity 1");

        // Test 5: Another content becomes most popular
        tracker.increasePopularity(101);
        tracker.increasePopularity(101);
        assertResult(101, tracker.getMostPopular(), "101 now has popularity 3");

        // Test 6: All popularity <= 0
        tracker.decreasePopularity(101);
        tracker.decreasePopularity(101);
        tracker.decreasePopularity(101);
        tracker.decreasePopularity(102);
        assertResult(-1, tracker.getMostPopular(), "All popularity <= 0");

        System.out.println("Brute Force: PASSED\n");
    }

    private static void testOptimizedTreeMap() {
        System.out.println("--- Approach 2: TreeMap O(log n) ---");

        OptimizedTreeMap tracker = new OptimizedTreeMap();

        // Test 1: Empty
        assertResult(-1, tracker.getMostPopular(), "Empty tracker");

        // Test 2: Basic operations
        tracker.increasePopularity(101);
        tracker.increasePopularity(102);
        tracker.increasePopularity(102);
        assertResult(102, tracker.getMostPopular(), "102 has popularity 2");

        // Test 3: Same popularity - should return one of them
        tracker.increasePopularity(101);
        int result = tracker.getMostPopular();
        boolean validResult = (result == 101 || result == 102);
        System.out.println("  Same popularity (101 or 102): " + result + (validResult ? " ✓" : " ✗"));

        // Test 4: One becomes higher
        tracker.increasePopularity(103);
        tracker.increasePopularity(103);
        tracker.increasePopularity(103);
        assertResult(103, tracker.getMostPopular(), "103 has popularity 3");

        // Test 5: Decrease max
        tracker.decreasePopularity(103);
        tracker.decreasePopularity(103);
        tracker.decreasePopularity(103);
        result = tracker.getMostPopular();
        validResult = (result == 101 || result == 102);
        System.out.println("  After 103 decreases (101 or 102): " + result + (validResult ? " ✓" : " ✗"));

        System.out.println("TreeMap O(log n): PASSED\n");
    }

    private static void testOptimizedCachedMax() {
        System.out.println("--- Approach 3: Cached Max O(1) ---");

        OptimizedCachedMax tracker = new OptimizedCachedMax();

        // Test 1: Empty
        assertResult(-1, tracker.getMostPopular(), "Empty tracker");

        // Test 2: Increase creates new max
        tracker.increasePopularity(101);
        assertResult(101, tracker.getMostPopular(), "First content");

        tracker.increasePopularity(102);
        tracker.increasePopularity(102);
        assertResult(102, tracker.getMostPopular(), "102 is new max");

        // Test 3: Increase non-max doesn't change max
        tracker.increasePopularity(101);
        assertResult(102, tracker.getMostPopular(), "102 still max");

        // Test 4: Decrease max triggers recalculation
        tracker.decreasePopularity(102);
        tracker.decreasePopularity(102);
        assertResult(101, tracker.getMostPopular(), "101 is new max after recalc");

        // Test 5: Decrease non-max doesn't trigger recalc
        tracker.increasePopularity(103);
        tracker.increasePopularity(103);
        tracker.increasePopularity(103);
        assertResult(103, tracker.getMostPopular(), "103 is max");

        tracker.decreasePopularity(101);  // Decrease non-max
        assertResult(103, tracker.getMostPopular(), "103 still max, no recalc needed");

        // Test 6: All <= 0
        for (int i = 0; i < 5; i++) {
            tracker.decreasePopularity(103);
        }
        tracker.decreasePopularity(101);
        tracker.decreasePopularity(102);
        assertResult(-1, tracker.getMostPopular(), "All <= 0");

        System.out.println("Cached Max O(1): PASSED\n");
    }

    private static void assertResult(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
        System.out.println("  " + testName + ": " + actual + " ✓");
    }
}
