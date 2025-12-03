package Company.atlassian.code_design.Q8_RateLimiter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class RateLimiterTest {

    private RateLimiterSol.RateLimiter rateLimiter;

    @BeforeEach
    void setUp() {
        rateLimiter = new RateLimiterSol.RateLimiter();
    }

    @Nested
    @DisplayName("Credit-Based Limiter Tests")
    class CreditBasedTests {

        @Test
        @DisplayName("Should allow base limit when no credits")
        void testBaseLimitNoCredits() {
            CreditBasedRateLimiter limiter = new CreditBasedRateLimiter(1000, 5, 10);

            for (int i = 0; i < 5; i++) {
                assertTrue(limiter.allowRequest());
            }
            assertFalse(limiter.allowRequest());
        }

        @Test
        @DisplayName("Should accumulate credits from unused requests")
        void testCreditAccumulation() throws InterruptedException {
            CreditBasedRateLimiter limiter = new CreditBasedRateLimiter(100, 5, 10);

            // Use only 2 of 5 in first window
            assertTrue(limiter.allowRequest());
            assertTrue(limiter.allowRequest());

            // Wait for new window
            Thread.sleep(150);

            // Trigger new window calculation
            limiter.allowRequest();

            // Should have 3 credits (5 - 2 = 3)
            assertEquals(3, limiter.getCredits());
        }

        @Test
        @DisplayName("Should allow extra requests with credits")
        void testUseCredits() throws InterruptedException {
            CreditBasedRateLimiter limiter = new CreditBasedRateLimiter(100, 5, 10);

            // Use 2 of 5 → earn 3 credits
            limiter.allowRequest();
            limiter.allowRequest();

            Thread.sleep(150);

            // New window: limit = 5 + 3 = 8
            int allowed = 0;
            for (int i = 0; i < 10; i++) {
                if (limiter.allowRequest()) {
                    allowed++;
                }
            }

            assertEquals(8, allowed);
        }

        @Test
        @DisplayName("Should cap credits at max")
        void testCreditCap() throws InterruptedException {
            CreditBasedRateLimiter limiter = new CreditBasedRateLimiter(100, 10, 5);

            // Use 0 of 10 → would earn 10 credits but capped at 5
            Thread.sleep(150);
            limiter.allowRequest();

            assertEquals(5, limiter.getCredits());
        }
    }

    @Nested
    @DisplayName("Thread-Safety Tests")
    class ThreadSafetyTests {

        @Test
        @DisplayName("Should handle concurrent requests correctly")
        void testConcurrentRequests() throws InterruptedException {
            rateLimiter.registerFixedWindow("api/test", 1000, 100);

            int numThreads = 10;
            int requestsPerThread = 50;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger allowedCount = new AtomicInteger(0);

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        if (rateLimiter.allowRequest("api/test")) {
                            allowedCount.incrementAndGet();
                        }
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // Total requests = 10 * 50 = 500
            // Limit = 100
            // Should allow exactly 100
            assertEquals(100, allowedCount.get());
        }

        @Test
        @DisplayName("Should handle concurrent requests on different resources")
        void testConcurrentDifferentResources() throws InterruptedException {
            rateLimiter.registerFixedWindow("api/users", 1000, 50);
            rateLimiter.registerFixedWindow("api/orders", 1000, 50);

            int numThreads = 10;
            int requestsPerThread = 20;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger usersAllowed = new AtomicInteger(0);
            AtomicInteger ordersAllowed = new AtomicInteger(0);

            for (int i = 0; i < numThreads; i++) {
                final int threadId = i;
                executor.submit(() -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        // Even threads hit users, odd threads hit orders
                        if (threadId % 2 == 0) {
                            if (rateLimiter.allowRequest("api/users")) {
                                usersAllowed.incrementAndGet();
                            }
                        } else {
                            if (rateLimiter.allowRequest("api/orders")) {
                                ordersAllowed.incrementAndGet();
                            }
                        }
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // 5 threads * 20 requests = 100 requests per resource
            // Limit = 50 per resource
            assertEquals(50, usersAllowed.get());
            assertEquals(50, ordersAllowed.get());
        }

        @Test
        @DisplayName("Thread-safe sliding window should handle concurrent requests")
        void testConcurrentSlidingWindow() throws InterruptedException {
            rateLimiter.registerSlidingWindow("api/sliding", 1000, 100);

            int numThreads = 10;
            int requestsPerThread = 50;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger allowedCount = new AtomicInteger(0);

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        if (rateLimiter.allowRequest("api/sliding")) {
                            allowedCount.incrementAndGet();
                        }
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            assertEquals(100, allowedCount.get());
        }
    }

    @Nested
    @DisplayName("Credit-Based Thread-Safety Tests")
    class CreditBasedThreadSafetyTests {

        @Test
        @DisplayName("Credit-based limiter should be thread-safe")
        void testConcurrentCreditBased() throws InterruptedException {
            rateLimiter.registerCreditBased("api/credit", 1000, 100, 50);

            int numThreads = 10;
            int requestsPerThread = 50;

            ExecutorService executor = Executors.newFixedThreadPool(numThreads);
            CountDownLatch latch = new CountDownLatch(numThreads);
            AtomicInteger allowedCount = new AtomicInteger(0);

            for (int i = 0; i < numThreads; i++) {
                executor.submit(() -> {
                    for (int j = 0; j < requestsPerThread; j++) {
                        if (rateLimiter.allowRequest("api/credit")) {
                            allowedCount.incrementAndGet();
                        }
                    }
                    latch.countDown();
                });
            }

            latch.await();
            executor.shutdown();

            // No credits initially, so limit = 100
            assertEquals(100, allowedCount.get());
        }
    }
}