package Company.atlassian.code_design.Q10_HitCounter;


/*
┌─────────────────────────────────────────────────────────────────┐
│                     HitCounter                                   │
├─────────────────────────────────────────────────────────────────┤
│ - pageHits: Map<String, Long>                                   │
│ - pageTimestamps: Map<String, List<Long>> (for time-based)      │
├─────────────────────────────────────────────────────────────────┤
│ + recordHit(pageId): void                                       │
│ + getHitCount(pageId): long                                     │
│ + getTotalHits(): long                                          │
│ + getTopPages(n): List<PageStats>                               │
│ + getHitsInLastNSeconds(pageId, seconds): long                  │
└─────────────────────────────────────────────────────────────────┘

User visits different pages:

Visit 1: /home
Visit 2: /home
Visit 3: /about
Visit 4: /home
Visit 5: /products

pageHits:
┌─────────────────────────────────────────────────────────────────┐
│ "/home"     → 3                                                 │
│ "/about"    → 1                                                 │
│ "/products" → 1                                                 │
└─────────────────────────────────────────────────────────────────┘

Total hits: 5
Top page: /home (3 hits)
 */


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Sol {

    public class PageStats {
        private final String pageId;
        private final long hitCount;

        public PageStats(String pageId, long hitCount) {
            this.pageId = pageId;
            this.hitCount = hitCount;
        }

        public String getPageId() {
            return pageId;
        }

        public long getHitCount() {
            return hitCount;
        }

        @Override
        public String toString() {
            return pageId + ": " + hitCount + " hits";
        }
    }

    public class HitCounter {

        // pageId → total hit count
        private final Map<String, Long> pageHits;

        // pageId → list of timestamps (for time-based queries)
        private final Map<String, List<Long>> pageTimestamps;

        public HitCounter() {
            this.pageHits = new HashMap<>();
            this.pageTimestamps = new HashMap<>();
        }

        // Record a hit for a page
        public void recordHit(String pageId) {
            // Update total count
            long currentCount = pageHits.getOrDefault(pageId, 0L);
            pageHits.put(pageId, currentCount + 1);

            // Store timestamp for time-based queries
            if (!pageTimestamps.containsKey(pageId)) {
                pageTimestamps.put(pageId, new ArrayList<>());
            }
            pageTimestamps.get(pageId).add(System.currentTimeMillis());
        }

        // Get hit count for a specific page
        public long getHitCount(String pageId) {
            return pageHits.getOrDefault(pageId, 0L);
        }

        // Get total hits across all pages
        public long getTotalHits() {
            long total = 0;
            for (long count : pageHits.values()) {
                total += count;
            }
            return total;
        }

        // Get all pages with their hit counts
        public List<PageStats> getAllPageStats() {
            List<PageStats> result = new ArrayList<>();
            for (Map.Entry<String, Long> entry : pageHits.entrySet()) {
                result.add(new PageStats(entry.getKey(), entry.getValue()));
            }
            return result;
        }

        // Get top N pages by hit count
        public List<PageStats> getTopPages(int n) {
            List<PageStats> allStats = getAllPageStats();

            // Sort by hit count descending
            Collections.sort(allStats, (a, b) -> Long.compare(b.getHitCount(), a.getHitCount()));

            // Return top N
            List<PageStats> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, allStats.size()); i++) {
                result.add(allStats.get(i));
            }
            return result;
        }

        // Get hits in last N seconds for a page
        public long getHitsInLastNSeconds(String pageId, int seconds) {
            List<Long> timestamps = pageTimestamps.get(pageId);
            if (timestamps == null || timestamps.isEmpty()) {
                return 0;
            }

            long cutoffTime = System.currentTimeMillis() - (seconds * 1000L);

            long count = 0;
            for (Long timestamp : timestamps) {
                if (timestamp >= cutoffTime) {
                    count++;
                }
            }
            return count;
        }

        // Get total page count
        public int getPageCount() {
            return pageHits.size();
        }

        // Reset counter for a page
        public void resetPage(String pageId) {
            pageHits.remove(pageId);
            pageTimestamps.remove(pageId);
        }

        // Reset all counters
        public void resetAll() {
            pageHits.clear();
            pageTimestamps.clear();
        }
    }

    public class ThreadSafeHitCounter {

        // pageId → atomic hit count
        private final Map<String, AtomicLong> pageHits;

        // pageId → synchronized list of timestamps
        private final Map<String, List<Long>> pageTimestamps;

        public ThreadSafeHitCounter() {
            this.pageHits = new ConcurrentHashMap<>();
            this.pageTimestamps = new ConcurrentHashMap<>();
        }

        // Record a hit (thread-safe)
        public void recordHit(String pageId) {
            // Atomic increment
            pageHits.computeIfAbsent(pageId, k -> new AtomicLong(0)).incrementAndGet();

            // Store timestamp (synchronized)
            pageTimestamps.computeIfAbsent(pageId, k -> Collections.synchronizedList(new ArrayList<>()))
                    .add(System.currentTimeMillis());
        }

        // Get hit count (thread-safe)
        public long getHitCount(String pageId) {
            AtomicLong count = pageHits.get(pageId);
            return count != null ? count.get() : 0;
        }

        // Get total hits
        public long getTotalHits() {
            long total = 0;
            for (AtomicLong count : pageHits.values()) {
                total += count.get();
            }
            return total;
        }

        // Get top N pages
        public List<PageStats> getTopPages(int n) {
            List<PageStats> allStats = new ArrayList<>();

            for (Map.Entry<String, AtomicLong> entry : pageHits.entrySet()) {
                allStats.add(new PageStats(entry.getKey(), entry.getValue().get()));
            }

            Collections.sort(allStats, (a, b) -> Long.compare(b.getHitCount(), a.getHitCount()));

            List<PageStats> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, allStats.size()); i++) {
                result.add(allStats.get(i));
            }
            return result;
        }

        // Get hits in last N seconds
        public long getHitsInLastNSeconds(String pageId, int seconds) {
            List<Long> timestamps = pageTimestamps.get(pageId);
            if (timestamps == null) {
                return 0;
            }

            long cutoffTime = System.currentTimeMillis() - (seconds * 1000L);

            synchronized (timestamps) {
                long count = 0;
                for (Long timestamp : timestamps) {
                    if (timestamp >= cutoffTime) {
                        count++;
                    }
                }
                return count;
            }
        }
    }
}
