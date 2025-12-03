// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.BeforeEach;
// import static org.junit.jupiter.api.Assertions.*;
package Company.atlassian.data_structures.q4_popular_content.OldSol;
import java.util.*;
import java.util.concurrent.*;

public class TestSolution {
    
    private Solution.PopularContentTracker tracker;
    private Solution.PopularContentTrackerHeap heapTracker;
    private Solution.SimplePopularContentTracker simpleTracker;
    
    // @BeforeEach
    void setUp() {
        tracker = new Solution.PopularContentTracker();
        heapTracker = new Solution.PopularContentTrackerHeap();
        simpleTracker = new Solution.SimplePopularContentTracker();
    }
    
    // @Test
    // @DisplayName("Test initial state returns -1")
    void testInitialState() {
        assertEqualsLocal(-1, tracker.getMostPopularContent());
        assertEqualsLocal(-1, heapTracker.getMostPopularContent());
        assertEqualsLocal(-1, simpleTracker.getMostPopularContent());
    }
    
    // @Test
    // @DisplayName("Test single content increase")
    void testSingleContentIncrease() {
        tracker.increasePopularity(1);
        assertEqualsLocal(1, tracker.getMostPopularContent());
        assertEqualsLocal(1, tracker.getPopularity(1));
        
        heapTracker.increasePopularity(1);
        assertEqualsLocal(1, heapTracker.getMostPopularContent());
        
        simpleTracker.increasePopularity(1);
        assertEqualsLocal(1, simpleTracker.getMostPopularContent());
    }
    
    // @Test
    // @DisplayName("Test multiple increases same content")
    void testMultipleIncreasesSameContent() {
        for (int i = 0; i < 5; i++) {
            tracker.increasePopularity(1);
        }
        
        assertEqualsLocal(1, tracker.getMostPopularContent());
        assertEqualsLocal(5, tracker.getPopularity(1));
    }
    
    // @Test
    // @DisplayName("Test multiple contents with different popularities")
    void testMultipleContentsWithDifferentPopularities() {
        tracker.increasePopularity(1); // popularity 1
        tracker.increasePopularity(2); // popularity 1
        tracker.increasePopularity(2); // popularity 2
        tracker.increasePopularity(3); // popularity 1
        tracker.increasePopularity(1); // popularity 2
        tracker.increasePopularity(2); // popularity 3
        
        assertEqualsLocal(2, tracker.getMostPopularContent()); // Content 2 has highest popularity (3)
        assertEqualsLocal(2, tracker.getPopularity(1));
        assertEqualsLocal(3, tracker.getPopularity(2));
        assertEqualsLocal(1, tracker.getPopularity(3));
    }
    
    // @Test
    // @DisplayName("Test decrease popularity")
    void testDecreasePopularity() {
        tracker.increasePopularity(1);
        tracker.increasePopularity(1);
        tracker.increasePopularity(2);
        
        assertEqualsLocal(1, tracker.getMostPopularContent()); // Content 1 has popularity 2
        
        tracker.decreasePopularity(1);
        // Now both content 1 and 2 have popularity 1, either could be returned
        int mostPopular = tracker.getMostPopularContent();
        assertTrueLocal(mostPopular == 1 || mostPopular == 2);
        
        tracker.decreasePopularity(1);
        assertEqualsLocal(2, tracker.getMostPopularContent()); // Content 1 now has popularity 0
    }
    
    // @Test
    // @DisplayName("Test decrease to zero popularity")
    void testDecreaseToZeroPopularity() {
        tracker.increasePopularity(1);
        tracker.decreasePopularity(1);
        
        assertEqualsLocal(-1, tracker.getMostPopularContent());
        assertEqualsLocal(0, tracker.getPopularity(1));
    }
    
    // @Test
    // @DisplayName("Test decrease below zero")
    void testDecreaseBelowZero() {
        tracker.increasePopularity(1);
        tracker.decreasePopularity(1);
        tracker.decreasePopularity(1); // Should not go negative
        
        assertEqualsLocal(-1, tracker.getMostPopularContent());
        assertEqualsLocal(0, tracker.getPopularity(1));
    }
    
    // @Test
    // @DisplayName("Test decrease non-existent content")
    void testDecreaseNonExistentContent() {
        tracker.decreasePopularity(999);
        assertEqualsLocal(-1, tracker.getMostPopularContent());
        assertEqualsLocal(0, tracker.getPopularity(999));
    }
    
    // @Test
    // @DisplayName("Test mixed operations")
    void testMixedOperations() {
        // Build up popularity
        tracker.increasePopularity(1);
        tracker.increasePopularity(2);
        tracker.increasePopularity(1);
        tracker.increasePopularity(3);
        
        assertEqualsLocal(1, tracker.getMostPopularContent()); // Content 1 has popularity 2
        
        // Decrease and change leader
        tracker.decreasePopularity(1);
        tracker.decreasePopularity(1);
        tracker.increasePopularity(3);
        
        assertEqualsLocal(3, tracker.getMostPopularContent()); // Content 3 now has popularity 2
    }
    
    // @Test
    // @DisplayName("Test all trackers consistency")
    void testAllTrackersConsistency() {
        List<Integer> operations = Arrays.asList(1, 1, 2, 1, 3, 2);
        
        // Apply same operations to all trackers
        for (int contentId : operations) {
            tracker.increasePopularity(contentId);
            heapTracker.increasePopularity(contentId);
            simpleTracker.increasePopularity(contentId);
        }
        
        // All should return same result
        int result1 = tracker.getMostPopularContent();
        int result2 = heapTracker.getMostPopularContent();
        int result3 = simpleTracker.getMostPopularContent();
        
        assertEqualsLocal(result1, result2);
        assertEqualsLocal(result2, result3);
        
        // Test decreases
        tracker.decreasePopularity(1);
        heapTracker.decreasePopularity(1);
        simpleTracker.decreasePopularity(1);
        
        result1 = tracker.getMostPopularContent();
        result2 = heapTracker.getMostPopularContent();
        result3 = simpleTracker.getMostPopularContent();
        
        assertEqualsLocal(result1, result2);
        assertEqualsLocal(result2, result3);
    }
    
    // @Test
    // @DisplayName("Test top K contents")
    void testTopKContents() {
        tracker.increasePopularity(1); // popularity 1
        tracker.increasePopularity(2); // popularity 1
        tracker.increasePopularity(2); // popularity 2
        tracker.increasePopularity(3); // popularity 1
        tracker.increasePopularity(3); // popularity 2
        tracker.increasePopularity(3); // popularity 3
        tracker.increasePopularity(4); // popularity 1
        
        List<Integer> top3 = tracker.getTopKContents(3);
        assertEqualsLocal(3, top3.size());
        
        // Should include content 3 (highest popularity)
        assertTrueLocal(top3.contains(3));
        
        // Should get exactly 3 contents
        assertEqualsLocal(3, top3.size());
        
        // Test more than available
        List<Integer> top10 = tracker.getTopKContents(10);
        assertEqualsLocal(4, top10.size()); // Only 4 contents total
    }
    
    // @Test
    // @DisplayName("Test concurrent operations")
    void testConcurrentOperations() throws InterruptedException {
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
                        int contentId = threadId * 100 + (j % 100) + 1; // Unique content IDs per thread
                        
                        if (random.nextBoolean()) {
                            tracker.increasePopularity(contentId);
                        } else {
                            tracker.decreasePopularity(contentId);
                        }
                        
                        // Occasionally check most popular
                        if (j % 100 == 0) {
                            tracker.getMostPopularContent();
                        }
                    }
                } finally {
                    latch.countDown();
                }
            });
        }
        
        latch.await(10, TimeUnit.SECONDS);
        executor.shutdown();
        
        // Verify final state is consistent
        int mostPopular = tracker.getMostPopularContent();
        if (mostPopular != -1) {
            assertTrueLocal(tracker.getPopularity(mostPopular) > 0);
        }
    }
    
    // @Test
    // @DisplayName("Test performance with many operations")
    void testPerformanceWithManyOperations() {
        Random random = new Random(42);
        int numOperations = 50000;
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < numOperations; i++) {
            int contentId = random.nextInt(1000) + 1;
            
            if (random.nextBoolean()) {
                tracker.increasePopularity(contentId);
            } else {
                tracker.decreasePopularity(contentId);
            }
            
            // Frequent reads
            if (i % 100 == 0) {
                tracker.getMostPopularContent();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Performance test completed in: " + duration + "ms");
        assertTrueLocal(duration < 5000); // Should complete within 5 seconds
    }
    
    // @Test
    // @DisplayName("Test heap tracker lazy deletion")
    void testHeapTrackerLazyDeletion() {
        // This test specifically validates the heap tracker's lazy deletion mechanism
        heapTracker.increasePopularity(1);
        heapTracker.increasePopularity(1);
        heapTracker.increasePopularity(2);
        
        assertEqualsLocal(1, heapTracker.getMostPopularContent()); // Content 1 has popularity 2
        
        // Decrease content 1 popularity to 0
        heapTracker.decreasePopularity(1);
        heapTracker.decreasePopularity(1);
        
        assertEqualsLocal(2, heapTracker.getMostPopularContent()); // Should find content 2
        
        // Decrease content 2 to 0
        heapTracker.decreasePopularity(2);
        
        assertEqualsLocal(-1, heapTracker.getMostPopularContent()); // No content with positive popularity
    }
    
    // @Test
    // @DisplayName("Test edge case - same popularity multiple contents")
    void testSamePopularityMultipleContents() {
        tracker.increasePopularity(1);
        tracker.increasePopularity(2);
        tracker.increasePopularity(3);
        
        // All have popularity 1
        int mostPopular = tracker.getMostPopularContent();
        assertTrueLocal(mostPopular >= 1 && mostPopular <= 3);
        
        // Verify the returned content actually has the max popularity
        int maxPop = 0;
        for (int i = 1; i <= 3; i++) {
            maxPop = Math.max(maxPop, tracker.getPopularity(i));
        }
        assertEqualsLocal(maxPop, tracker.getPopularity(mostPopular));
    }
    
    // @Test
    // @DisplayName("Test getAllPopularities")
    void testGetAllPopularities() {
        tracker.increasePopularity(1);
        tracker.increasePopularity(1);
        tracker.increasePopularity(2);
        tracker.increasePopularity(3);
        tracker.increasePopularity(3);
        tracker.increasePopularity(3);
        
        Map<Integer, Integer> allPop = tracker.getAllPopularities();
        assertEqualsLocal(3, allPop.size());
        assertEqualsLocal(2, (int) allPop.get(1));
        assertEqualsLocal(1, (int) allPop.get(2));
        assertEqualsLocal(3, (int) allPop.get(3));
    }
    
    // @Test
    // @DisplayName("Test large content IDs")
    void testLargeContentIds() {
        int largeId = Integer.MAX_VALUE;
        tracker.increasePopularity(largeId);
        assertEqualsLocal(largeId, tracker.getMostPopularContent());
        assertEqualsLocal(1, tracker.getPopularity(largeId));
    }
    
    // Helper assertion methods
    private static void assertEqualsLocal(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private static void assertTrueLocal(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true, but was false");
        }
    }
    
    private static void assertFalseLocal(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false, but was true");
        }
    }
    
    private static void assertNotNullLocal(Object actual) {
        if (actual == null) {
            throw new AssertionError("Expected non-null, but was null");
        }
    }
    
    private static void assertNullLocal(Object actual) {
        if (actual != null) {
            throw new AssertionError("Expected null, but was: " + actual);
        }
    }
}
