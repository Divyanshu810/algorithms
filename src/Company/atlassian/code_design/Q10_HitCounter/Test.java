package Company.atlassian.code_design.Q10_HitCounter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

public class HitCounterTest {

    private HitCounter counter;

    @BeforeEach
    void setUp() {
        counter = new HitCounter();
    }

    @Nested
    @DisplayName("Record Hit Tests")
    class RecordHitTests {

        @Test
        @DisplayName("Should record single hit")
        void testSingleHit() {
            counter.recordHit("/home");

            assertEquals(1, counter.getHitCount("/home"));
        }

        @Test
        @DisplayName("Should record multiple hits for same page")
        void testMultipleHits() {
            counter.recordHit("/home");
            counter.recordHit("/home");
            counter.recordHit("/home");

            assertEquals(3, counter.getHitCount("/home"));
        }

        @Test
        @DisplayName("Should record hits for different pages")
        void testDifferentPages() {
            counter.recordHit("/home");
            counter.recordHit("/about");
            counter.recordHit("/home");

            assertEquals(2, counter.getHitCount("/home"));
            assertEquals(1, counter.getHitCount("/about"));
        }
    }

    @Nested
    @DisplayName("Get Hit Count Tests")
    class GetHitCountTests {

        @Test
        @DisplayName("Should return zero for unknown page")
        void testUnknownPage() {
            assertEquals(0, counter.getHitCount("/unknown"));
        }

        @Test
        @DisplayName("Should return correct count")
        void testCorrectCount() {
            for (int i = 0; i < 100; i++) {
                counter.recordHit("/popular");
            }

            assertEquals(100, counter.getHitCount("/popular"));
        }
    }

    @Nested
    @DisplayName("Total Hits Tests")
    class TotalHitsTests {

        @Test
        @DisplayName("Should calculate total hits across all pages")
        void testTotalHits() {
            counter.recordHit("/home");
            counter.recordHit("/home");
            counter.recordHit("/about");
            counter.recordHit("/products");
            counter.recordHit("/products");
            counter.recordHit("/products");

            assertEquals(6, counter.getTotalHits());
        }

        @Test
        @DisplayName("Should return zero for empty counter")
        void testEmptyCounter() {
            assertEquals(0, counter.getTotalHits());
        }
    }

    @Nested
    @DisplayName("Top Pages Tests")
    class TopPagesTests {

        @BeforeEach
        void setUpPages() {
            // /home: 5 hits
            for (int i = 0; i < 5; i++) counter.recordHit("/home");

            // /about: 2 hits
            for (int i = 0; i < 2; i++) counter.recordHit("/about");

            // /products: 10 hits
            for (int i = 0; i < 10; i++) counter.recordHit("/products");

            // /contact: 1 hit
            counter.recordHit("/contact");
        }

        @Test
        @DisplayName("Should return top N pages by hits")
        void testTopPages() {
            List<PageStats> top2 = counter.getTopPages(2);

            assertEquals(2, top2.size());
            assertEquals("/products", top2.get(0).getPageId());
            assertEquals(10, top2.get(0).getHitCount());
            assertEquals("/home", top2.get(1).getPageId());
            assertEquals(5, top2.get(1).getHitCount());
        }

        @Test
        @DisplayName("Should handle requesting more than available")
        void testTopMoreThanAvailable() {
            List<PageStats> top = counter.getTopPages(100);

            assertEquals(4, top.size());
        }
    }

    @Nested
    @DisplayName("Time-Based Hits Tests")
    class TimeBasedTests {

        @Test
        @DisplayName("Should count hits in time window")
        void testHitsInTimeWindow() throws InterruptedException {
            counter.recordHit("/home");
            counter.recordHit("/home");

            // Should see both hits within last 5 seconds
            long count = counter.getHitsInLastNSeconds("/home", 5);
            assertEquals(2, count);
        }

        @Test
        @DisplayName("Should exclude old hits")
        void testExcludeOldHits() throws InterruptedException {
            counter.recordHit("/home");

            // Wait 2 seconds
            Thread.sleep(2100);

            counter.recordHit("/home");

            // Only 1 hit in last 1 second
            long recentCount = counter.getHitsInLastNSeconds("/home", 1);
            assertEquals(1, recentCount);

            // Both hits in last 5 seconds
            long allCount = counter.getHitsInLastNSeconds("/home", 5);
            assertEquals(2, allCount);
        }

        @Test
        @DisplayName("Should return zero for unknown page")
        void testTimeBasedUnknownPage() {
            assertEquals(0, counter.getHitsInLastNSeconds("/unknown", 60));
        }
    }

    @Nested
    @DisplayName("Reset Tests")
    class ResetTests {

        @Test
        @DisplayName("Should reset single page")
        void testResetPage() {
            counter.recordHit("/home");
            counter.recordHit("/about");

            counter.resetPage("/home");

            assertEquals(0, counter.getHitCount("/home"));
            assertEquals(1, counter.getHitCount("/about"));
        }

        @Test
        @DisplayName("Should reset all pages")
        void testResetAll() {
            counter.recordHit("/home");
            counter.recordHit("/about");

            counter.resetAll();

            assertEquals(0, counter.getTotalHits());
            assertEquals(0, counter.getPageCount());
        }
    }

    @Nested
    @DisplayName("Thread-Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent hits correctly")
        void testConcurrentHits() throws InterruptedException {
            ThreadSafeHitCounter safeCounter = new ThreadSafeHitCounter();

            int numThreads = 10;
            int hitsPerThread = 1000;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < hitsPerThread; j++) {
                        safeCounter.recordHit("/home");
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // Total should be exactly 10 * 1000 = 10000
            assertEquals(10000, safeCounter.getHitCount("/home"));
        }

        @Test
        @DisplayName("Should handle concurrent hits on different pages")
        void testConcurrentDifferentPages() throws InterruptedException {
            ThreadSafeHitCounter safeCounter = new ThreadSafeHitCounter();

            int numThreads = 10;
            int hitsPerThread = 100;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);

            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    String page = "/page" + (threadId % 3);  // 3 different pages
                    for (int j = 0; j < hitsPerThread; j++) {
                        safeCounter.recordHit(page);
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // Total across all pages should be 10 * 100 = 1000
            assertEquals(1000, safeCounter.getTotalHits());
        }
    }
}