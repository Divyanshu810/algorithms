package Company.atlassian.code_design.Q8_RateLimiter;

/*
┌─────────────────────────────────────────────────────────────────┐
│                <<interface>>                                     │
│               RateLimitStrategy                                  │
├─────────────────────────────────────────────────────────────────┤
│ + allowRequest(): boolean                                       │
└─────────────────────────────────────────────────────────────────┘
          ▲                              ▲
          │                              │
┌─────────────────────┐      ┌─────────────────────────┐
│ FixedWindowCounter  │      │ SlidingWindowCounter    │
├─────────────────────┤      ├─────────────────────────┤
│ - windowSize: long  │      │ - windowSize: long      │
│ - maxRequests: int  │      │ - maxRequests: int      │
│ - windowStart: long │      │ - requestTimestamps:    │
│ - requestCount: int │      │     List<Long>          │
├─────────────────────┤      ├─────────────────────────┤
│ + allowRequest()    │      │ + allowRequest()        │
└─────────────────────┘      └─────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     RateLimiter                                  │
├─────────────────────────────────────────────────────────────────┤
│ - strategies: Map<String, RateLimitStrategy>                    │
├─────────────────────────────────────────────────────────────────┤
│ + registerResource(resourceId, strategy): void                  │
│ + allowRequest(resourceId): boolean                             │
└─────────────────────────────────────────────────────────────────┘
 */

import java.util.*;

import java.util.concurrent.ConcurrentHashMap;
public class RateLimiterSol {

    public interface RateLimitStrategy {
        boolean allowRequest();
    }

    public class ThreadSafeFixedWindowCounter implements RateLimitStrategy {

        private final long windowSizeMs;
        private final int maxRequests;

        private long windowStart;
        private int requestCount;

        private final Object lock = new Object();

        public ThreadSafeFixedWindowCounter(long windowSizeMs, int maxRequests) {
            this.windowSizeMs = windowSizeMs;
            this.maxRequests = maxRequests;
            this.windowStart = System.currentTimeMillis();
            this.requestCount = 0;
        }

        @Override
        public boolean allowRequest() {
            synchronized (lock) {
                long now = System.currentTimeMillis();

                // Check if we're in a new window
                if (now - windowStart >= windowSizeMs) {
                    windowStart = now;
                    requestCount = 0;
                }

                // Check if under limit
                if (requestCount < maxRequests) {
                    requestCount++;
                    return true;
                }

                return false;
            }
        }
    }



    public class ThreadSafeSlidingWindowCounter implements RateLimitStrategy {

        private final long windowSizeMs;
        private final int maxRequests;
        private final List<Long> requestTimestamps;

        private final Object lock = new Object();

        public ThreadSafeSlidingWindowCounter(long windowSizeMs, int maxRequests) {
            this.windowSizeMs = windowSizeMs;
            this.maxRequests = maxRequests;
            this.requestTimestamps = new ArrayList<>();
        }

        @Override
        public boolean allowRequest() {
            synchronized (lock) {
                long now = System.currentTimeMillis();
                long windowStart = now - windowSizeMs;

                // Remove expired timestamps
                while (!requestTimestamps.isEmpty() && requestTimestamps.get(0) <= windowStart) {
                    requestTimestamps.remove(0);
                }

                // Check if under limit
                if (requestTimestamps.size() < maxRequests) {
                    requestTimestamps.add(now);
                    return true;
                }

                return false;
            }
        }
    }


    public class ThreadSafeCreditBasedLimiter implements RateLimitStrategy {

        private final long windowSizeMs;
        private final int baseLimit;
        private final int maxCredits;

        private long windowStart;
        private int requestCount;
        private int credits;

        private final Object lock = new Object();

        public ThreadSafeCreditBasedLimiter(long windowSizeMs, int baseLimit, int maxCredits) {
            this.windowSizeMs = windowSizeMs;
            this.baseLimit = baseLimit;
            this.maxCredits = maxCredits;
            this.windowStart = System.currentTimeMillis();
            this.requestCount = 0;
            this.credits = 0;
        }

        @Override
        public boolean allowRequest() {
            synchronized (lock) {
                long now = System.currentTimeMillis();

                // Check if we're in a new window
                if (now - windowStart >= windowSizeMs) {
                    // Calculate and store credits
                    int availableInWindow = baseLimit + credits;
                    int unused = availableInWindow - requestCount;
                    credits = Math.min(Math.max(unused, 0), maxCredits);

                    // Reset window
                    windowStart = now;
                    requestCount = 0;
                }

                // Current available = base limit + credits
                int currentLimit = baseLimit + credits;

                if (requestCount < currentLimit) {
                    requestCount++;
                    return true;
                }

                return false;
            }
        }

        public int getCredits() {
            synchronized (lock) {
                return credits;
            }
        }

        public int getCurrentLimit() {
            synchronized (lock) {
                return baseLimit + credits;
            }
        }
    }

    public class RateLimiter {

        private final Map<String, RateLimitStrategy> strategies;

        public RateLimiter() {
            // Thread-safe map for registrations
            this.strategies = new ConcurrentHashMap<>();
        }

        public void registerFixedWindow(String resourceId, long windowSizeMs, int maxRequests) {
            strategies.put(resourceId, new ThreadSafeFixedWindowCounter(windowSizeMs, maxRequests));
        }

        public void registerSlidingWindow(String resourceId, long windowSizeMs, int maxRequests) {
            strategies.put(resourceId, new ThreadSafeSlidingWindowCounter(windowSizeMs, maxRequests));
        }

        public void registerCreditBased(String resourceId, long windowSizeMs,
                                        int baseLimit, int maxCredits) {
            strategies.put(resourceId, new ThreadSafeCreditBasedLimiter(windowSizeMs, baseLimit, maxCredits));
        }

        public boolean allowRequest(String resourceId) {
            RateLimitStrategy strategy = strategies.get(resourceId);

            if (strategy == null) {
                return true;
            }

            return strategy.allowRequest();
        }
    }


}
