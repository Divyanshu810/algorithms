package Company.atlassian.data_structures.q3_commodity_prices.NewSol;

import java.util.*;

/**
 * Commodity Prices - Max Price Tracker
 *
 * ===================================================================================
 * PROBLEM:
 * ===================================================================================
 * - Stream of (timestamp, price) data points
 * - Timestamps can be duplicate → upsert (update if exists)
 * - Need to return maxCommodityPrice at any time
 * - Optimize for frequent reads and writes
 *
 * ===================================================================================
 * APPROACHES:
 * ===================================================================================
 *
 * Approach 1: Brute Force (HashMap only)
 *   - upsert: O(1)
 *   - getMax: O(n) - scan all values
 *
 * Approach 2: HashMap + Cached Max Variable
 *   - upsert: O(1) average, O(n) when max is invalidated
 *   - getMax: O(1)
 *
 * ===================================================================================
 */
public class CommodityPrices {

    // ==================== APPROACH 1: Brute Force ====================
    /**
     * Simple HashMap storage.
     *
     * upsert: O(1)
     * getMax: O(n) - must scan all values each time
     */
    static class BruteForce {

        private Map<Long, Double> timestampToPrice;

        public BruteForce() {
            this.timestampToPrice = new HashMap<>();
        }

        // O(1)
        public void upsert(long timestamp, double price) {
            timestampToPrice.put(timestamp, price);
        }

        // O(n) - scan all values
        public Double getMaxPrice() {
            if (timestampToPrice.isEmpty()) {
                return null;
            }

            double maxPrice = Double.MIN_VALUE;
            for (double price : timestampToPrice.values()) {
                maxPrice = Math.max(maxPrice, price);
            }
            return maxPrice;
        }

        // O(1)
        public Double getPrice(long timestamp) {
            return timestampToPrice.get(timestamp);
        }

        // O(1)
        public void remove(long timestamp) {
            timestampToPrice.remove(timestamp);
        }
    }

    // ==================== APPROACH 2: O(1) getMax with Cached Variable ====================
    /**
     * HashMap + cached max variable.
     *
     * Key insight:
     * - Keep a variable tracking current max
     * - Only recalculate when max might be invalidated
     *
     * upsert: O(1) average, O(n) worst case (when old max is replaced with lower value)
     * getMax: O(1) always
     *
     * When is recalculation needed?
     * - When we UPDATE a timestamp that HAD the max price to a LOWER price
     * - When we REMOVE a timestamp that had the max price
     */
    static class OptimizedO1Read {

        private Map<Long, Double> timestampToPrice;
        private Double cachedMaxPrice;

        public OptimizedO1Read() {
            this.timestampToPrice = new HashMap<>();
            this.cachedMaxPrice = null;
        }

        // O(1) average, O(n) when max is invalidated
        public void upsert(long timestamp, double newPrice) {
            Double oldPrice = timestampToPrice.get(timestamp);

            // Update the map
            timestampToPrice.put(timestamp, newPrice);

            // Case 1: New price is higher than current max (or first entry)
            if (cachedMaxPrice == null || newPrice > cachedMaxPrice) {
                cachedMaxPrice = newPrice;
                return;
            }

            // Case 2: We updated the max timestamp to a lower value
            if (oldPrice != null && oldPrice.equals(cachedMaxPrice) && newPrice < cachedMaxPrice) {
                recalculateMax();
            }
        }

        // O(1)
        public Double getMaxPrice() {
            return cachedMaxPrice;
        }

        // O(1)
        public Double getPrice(long timestamp) {
            return timestampToPrice.get(timestamp);
        }

        // O(1) average, O(n) when removing max
        public void remove(long timestamp) {
            Double removedPrice = timestampToPrice.remove(timestamp);

            // If we removed the max price, recalculate
            if (removedPrice != null && removedPrice.equals(cachedMaxPrice)) {
                recalculateMax();
            }
        }

        // O(n) - only called when max is invalidated
        private void recalculateMax() {
            if (timestampToPrice.isEmpty()) {
                cachedMaxPrice = null;
                return;
            }

            double max = Double.MIN_VALUE;
            for (double price : timestampToPrice.values()) {
                max = Math.max(max, price);
            }
            cachedMaxPrice = max;
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Commodity Prices Solutions ===\n");

        testBruteForce();
        testOptimizedO1Read();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testBruteForce() {
        System.out.println("--- Approach 1: Brute Force ---");

        BruteForce tracker = new BruteForce();

        tracker.upsert(100, 50.0);
        tracker.upsert(200, 75.0);
        tracker.upsert(150, 60.0);  // Out of order timestamp

        assertResult(75.0, tracker.getMaxPrice(), "Initial max");

        // Update existing timestamp (duplicate timestamp → upsert)
        tracker.upsert(200, 40.0);
        assertResult(60.0, tracker.getMaxPrice(), "After update");

        // Remove
        tracker.remove(150);
        assertResult(50.0, tracker.getMaxPrice(), "After remove");

        System.out.println("Brute Force: PASSED\n");
    }

    private static void testOptimizedO1Read() {
        System.out.println("--- Approach 2: O(1) getMax ---");

        OptimizedO1Read tracker = new OptimizedO1Read();

        // Test 1: Basic insertions
        tracker.upsert(100, 50.0);
        assertResult(50.0, tracker.getMaxPrice(), "First entry");

        tracker.upsert(200, 75.0);
        assertResult(75.0, tracker.getMaxPrice(), "New max");

        tracker.upsert(150, 60.0);  // Out of order, not max
        assertResult(75.0, tracker.getMaxPrice(), "Max unchanged");

        // Test 2: Update max timestamp to lower value → triggers recalculation
        tracker.upsert(200, 40.0);
        assertResult(60.0, tracker.getMaxPrice(), "After downgrading max");

        // Test 3: Update non-max timestamp → no recalculation
        tracker.upsert(100, 55.0);
        assertResult(60.0, tracker.getMaxPrice(), "Non-max update");

        // Test 4: Remove max → triggers recalculation
        tracker.remove(150);
        assertResult(55.0, tracker.getMaxPrice(), "After removing max");

        // Test 5: Remove non-max → no recalculation
        tracker.remove(200);
        assertResult(55.0, tracker.getMaxPrice(), "After removing non-max");

        // Test 6: New max after removals
        tracker.upsert(300, 100.0);
        assertResult(100.0, tracker.getMaxPrice(), "New max added");

        // Test 7: Empty tracker
        tracker.remove(100);
        tracker.remove(300);
        assertResult(null, tracker.getMaxPrice(), "Empty tracker");

        System.out.println("Optimized O(1) Read: PASSED\n");
    }

    private static void assertResult(Double expected, Double actual, String testName) {
        if (expected == null && actual == null) {
            System.out.println("  " + testName + ": null ✓");
            return;
        }
        if (expected == null || actual == null || !expected.equals(actual)) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
        System.out.println("  " + testName + ": " + actual + " ✓");
    }
}
