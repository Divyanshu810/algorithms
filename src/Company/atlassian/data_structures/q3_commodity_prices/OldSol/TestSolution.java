package Company.atlassian.data_structures.q3_commodity_prices.OldSol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;

public class TestSolution {
    
    private Solution.CommodityPriceTracker tracker;
    private Solution.SimpleCommodityPriceTracker simpleTracker;
    private Solution.DualStructurePriceTracker dualTracker;
    
    @BeforeEach
    void setUp() {
        tracker = new Solution.CommodityPriceTracker();
        simpleTracker = new Solution.SimpleCommodityPriceTracker();
        dualTracker = new Solution.DualStructurePriceTracker();
    }
    
    @Test
    @DisplayName("Test basic upsert and getMax functionality")
    void testBasicFunctionality() {
        tracker.upsert(1000, 100.5);
        assertEquals(100.5, tracker.getMaxCommodityPrice(), 0.001);
        
        tracker.upsert(2000, 95.3);
        assertEquals(100.5, tracker.getMaxCommodityPrice(), 0.001);
        
        tracker.upsert(3000, 110.7);
        assertEquals(110.7, tracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test out-of-order timestamp insertion")
    void testOutOfOrderInsertion() {
        tracker.upsert(3000, 90.0);
        tracker.upsert(1000, 100.0);
        tracker.upsert(2000, 95.0);
        tracker.upsert(1500, 120.0); // Out of order, but highest price
        
        assertEquals(120.0, tracker.getMaxCommodityPrice(), 0.001);
        assertEquals(4, tracker.size());
    }
    
    @Test
    @DisplayName("Test duplicate timestamp handling (update)")
    void testDuplicateTimestampUpdate() {
        tracker.upsert(1000, 100.0);
        tracker.upsert(2000, 90.0);
        assertEquals(100.0, tracker.getMaxCommodityPrice(), 0.001);
        
        // Update the max price entry with lower value
        tracker.upsert(1000, 80.0);
        assertEquals(90.0, tracker.getMaxCommodityPrice(), 0.001);
        assertEquals(2, tracker.size());
        
        // Update with higher value
        tracker.upsert(1000, 150.0);
        assertEquals(150.0, tracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test empty tracker")
    void testEmptyTracker() {
        // Expected to throw IllegalStateException for empty tracker
        assertThrows(IllegalStateException.class, () -> tracker.getMaxCommodityPrice());
        assertEquals(0, tracker.size());
    }
    
    @Test
    @DisplayName("Test single data point")
    void testSingleDataPoint() {
        tracker.upsert(1000, 42.5);
        assertEquals(42.5, tracker.getMaxCommodityPrice(), 0.001);
        assertEquals(1, tracker.size());
    }
    
    @Test
    @DisplayName("Test price retrieval by timestamp")
    void testPriceByTimestamp() {
        tracker.upsert(1000, 100.0);
        tracker.upsert(2000, 90.0);
        tracker.upsert(3000, 110.0);
        
        assertEquals(100.0, tracker.getPriceAtTimestamp(1000), 0.001);
        assertEquals(90.0, tracker.getPriceAtTimestamp(2000), 0.001);
        assertEquals(110.0, tracker.getPriceAtTimestamp(3000), 0.001);
        assertNull(tracker.getPriceAtTimestamp(4000));
    }
    
    @Test
    @DisplayName("Test negative and zero prices")
    void testNegativeAndZeroPrices() {
        tracker.upsert(1000, -10.0);
        tracker.upsert(2000, 0.0);
        tracker.upsert(3000, -5.0);
        
        assertEquals(0.0, tracker.getMaxCommodityPrice(), 0.001);
        
        tracker.upsert(4000, 5.0);
        assertEquals(5.0, tracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test very large numbers")
    void testLargeNumbers() {
        tracker.upsert(Long.MAX_VALUE, Double.MAX_VALUE);
        tracker.upsert(Long.MIN_VALUE, Double.MAX_VALUE - 1);
        
        assertEquals(Double.MAX_VALUE, tracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test simple tracker with lazy recalculation")
    void testSimpleTracker() {
        simpleTracker.upsert(1000, 100.0);
        simpleTracker.upsert(2000, 90.0);
        simpleTracker.upsert(3000, 110.0);
        
        assertEquals(110.0, simpleTracker.getMaxCommodityPrice(), 0.001);
        
        // Test duplicate timestamp
        simpleTracker.upsert(3000, 80.0);
        assertEquals(100.0, simpleTracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test dual structure tracker")
    void testDualStructureTracker() {
        dualTracker.upsert(1000, 100.0);
        dualTracker.upsert(2000, 90.0);
        dualTracker.upsert(3000, 110.0);
        
        assertEquals(110.0, dualTracker.getMaxCommodityPrice(), 0.001);
        
        // Test duplicate timestamp
        dualTracker.upsert(3000, 80.0);
        assertEquals(100.0, dualTracker.getMaxCommodityPrice(), 0.001);
        
        // Test multiple entries with same price
        dualTracker.upsert(4000, 100.0);
        assertEquals(100.0, dualTracker.getMaxCommodityPrice(), 0.001);
    }
    
    @Test
    @DisplayName("Test concurrent operations")
    void testConcurrentOperations() throws InterruptedException {
        Solution.ConcurrentCommodityPriceTracker concurrentTracker = 
            new Solution.ConcurrentCommodityPriceTracker();
        
        int numThreads = 10;
        int operationsPerThread = 1000;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);
        
        Random random = new Random(42);
        
        for (int i = 0; i < numThreads; i++) {
            final int threadId = i;
            executor.submit(() -> {
                try {
                    for (int j = 0; j < operationsPerThread; j++) {
                        long timestamp = threadId * operationsPerThread + j;
                        double price = random.nextDouble() * 1000;
                        concurrentTracker.upsert(timestamp, price);
                        
                        // Occasionally read the max
                        if (j % 100 == 0) {
                            try {
                                concurrentTracker.getMaxCommodityPrice();
                            } catch (IllegalStateException e) {
                                // Expected for early operations
                            }
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Verify final state
        assertEquals(numThreads * operationsPerThread, concurrentTracker.size());
        double maxPrice = concurrentTracker.getMaxCommodityPrice();
        assertTrue(maxPrice >= 0.0 && maxPrice <= 1000.0);
    }
    
    @Test
    @DisplayName("Test performance with frequent reads and writes")
    void testPerformanceFrequentOperations() {
        Random random = new Random(42);
        int numOperations = 10000;
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numOperations; i++) {
            tracker.upsert(random.nextLong(), random.nextDouble() * 1000);
            
            // Frequent reads (every 10 writes)
            if (i % 10 == 0 && i > 0) {
                tracker.getMaxCommodityPrice();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Performance test completed in: " + duration + "ms");
        assertTrue(duration < 5000); // Should complete within 5 seconds
        
        double finalMax = tracker.getMaxCommodityPrice();
        assertTrue(finalMax >= 0.0 && finalMax <= 1000.0);
    }
    
    @Test
    @DisplayName("Test consistency across different implementations")
    void testConsistencyAcrossImplementations() {
        Random random = new Random(123);
        List<Long> timestamps = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        
        // Generate test data
        for (int i = 0; i < 1000; i++) {
            timestamps.add(random.nextLong());
            prices.add(random.nextDouble() * 1000);
        }
        
        // Apply same operations to all trackers
        for (int i = 0; i < timestamps.size(); i++) {
            tracker.upsert(timestamps.get(i), prices.get(i));
            simpleTracker.upsert(timestamps.get(i), prices.get(i));
            dualTracker.upsert(timestamps.get(i), prices.get(i));
        }
        
        // All should return same max
        double max1 = tracker.getMaxCommodityPrice();
        double max2 = simpleTracker.getMaxCommodityPrice();
        double max3 = dualTracker.getMaxCommodityPrice();
        
        assertEquals(max1, max2, 0.001);
        assertEquals(max2, max3, 0.001);
        
        // All should have same size
        assertEquals(tracker.size(), simpleTracker.size());
        assertEquals(simpleTracker.size(), dualTracker.size());
    }
    
    @Test
    @DisplayName("Test edge case - same timestamp multiple updates")
    void testSameTimestampMultipleUpdates() {
        long timestamp = 1000;
        
        tracker.upsert(timestamp, 100.0);
        assertEquals(100.0, tracker.getMaxCommodityPrice(), 0.001);
        
        tracker.upsert(timestamp, 150.0);
        assertEquals(150.0, tracker.getMaxCommodityPrice(), 0.001);
        
        tracker.upsert(timestamp, 50.0);
        assertEquals(50.0, tracker.getMaxCommodityPrice(), 0.001);
        
        assertEquals(1, tracker.size()); // Should still be only one entry
    }
    
    @Test
    @DisplayName("Test getAllData functionality")
    void testGetAllData() {
        tracker.upsert(3000, 90.0);
        tracker.upsert(1000, 100.0);
        tracker.upsert(2000, 95.0);
        
        Map<Long, Double> allData = tracker.getAllData();
        assertEquals(3, allData.size());
        
        // TreeMap should maintain sorted order
        Long[] sortedKeys = allData.keySet().toArray(new Long[0]);
        assertEquals(Long.valueOf(1000), sortedKeys[0]);
        assertEquals(Long.valueOf(2000), sortedKeys[1]);
        assertEquals(Long.valueOf(3000), sortedKeys[2]);
        
        // Verify values
        assertEquals(100.0, allData.get(1000L), 0.001);
        assertEquals(95.0, allData.get(2000L), 0.001);
        assertEquals(90.0, allData.get(3000L), 0.001);
    }
}