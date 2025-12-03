package Company.atlassian.data_structures.q4_popular_content.OldSol;
import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Solution {
    
    // Approach 1: HashMap + Max Heap with Lazy Deletion
    public static class PopularContentTrackerHeap {
        private Map<Integer, Integer> contentPopularity;
        private PriorityQueue<ContentEntry> maxHeap;
        
        private static class ContentEntry {
            int contentId;
            int popularity;
            long timestamp; // For handling stale entries
            
            ContentEntry(int contentId, int popularity, long timestamp) {
                this.contentId = contentId;
                this.popularity = popularity;
                this.timestamp = timestamp;
            }
        }
        
        private Map<Integer, Long> contentTimestamp; // Track latest update timestamp
        private long currentTime = 0;
        
        public PopularContentTrackerHeap() {
            this.contentPopularity = new HashMap<>();
            this.maxHeap = new PriorityQueue<>((a, b) -> {
                if (a.popularity != b.popularity) {
                    return Integer.compare(b.popularity, a.popularity); // Max heap
                }
                return Integer.compare(a.contentId, b.contentId); // Tie-breaker
            });
            this.contentTimestamp = new HashMap<>();
        }
        
        public void increasePopularity(int contentId) {
            int newPopularity = contentPopularity.getOrDefault(contentId, 0) + 1;
            contentPopularity.put(contentId, newPopularity);
            
            long timestamp = ++currentTime;
            contentTimestamp.put(contentId, timestamp);
            maxHeap.offer(new ContentEntry(contentId, newPopularity, timestamp));
        }
        
        public void decreasePopularity(int contentId) {
            if (!contentPopularity.containsKey(contentId)) {
                return;
            }
            
            int newPopularity = contentPopularity.get(contentId) - 1;
            if (newPopularity <= 0) {
                contentPopularity.remove(contentId);
                contentTimestamp.remove(contentId);
            } else {
                contentPopularity.put(contentId, newPopularity);
                long timestamp = ++currentTime;
                contentTimestamp.put(contentId, timestamp);
                maxHeap.offer(new ContentEntry(contentId, newPopularity, timestamp));
            }
        }
        
        public int getMostPopularContent() {
            // Clean stale entries from heap top
            while (!maxHeap.isEmpty()) {
                ContentEntry top = maxHeap.peek();
                
                // Check if this entry is stale
                if (!contentPopularity.containsKey(top.contentId) ||
                    contentPopularity.get(top.contentId) != top.popularity ||
                    !contentTimestamp.get(top.contentId).equals(top.timestamp)) {
                    maxHeap.poll(); // Remove stale entry
                    continue;
                }
                
                // Valid entry found
                if (top.popularity > 0) {
                    return top.contentId;
                }
                break;
            }
            
            return -1;
        }
        
        public int getPopularity(int contentId) {
            return contentPopularity.getOrDefault(contentId, 0);
        }
    }
    
    // Approach 2: HashMap + TreeMap (Recommended)
    public static class PopularContentTracker {
        private Map<Integer, Integer> contentPopularity;
        private TreeMap<Integer, Set<Integer>> popularityToContents;
        private ReadWriteLock lock;
        
        public PopularContentTracker() {
            this.contentPopularity = new HashMap<>();
            this.popularityToContents = new TreeMap<>();
            this.lock = new ReentrantReadWriteLock();
        }
        
        public void increasePopularity(int contentId) {
            lock.writeLock().lock();
            try {
                int oldPopularity = contentPopularity.getOrDefault(contentId, 0);
                int newPopularity = oldPopularity + 1;
                
                // Remove from old popularity bucket
                if (oldPopularity > 0) {
                    removeFromPopularityBucket(oldPopularity, contentId);
                }
                
                // Add to new popularity bucket
                contentPopularity.put(contentId, newPopularity);
                addToPopularityBucket(newPopularity, contentId);
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public void decreasePopularity(int contentId) {
            lock.writeLock().lock();
            try {
                if (!contentPopularity.containsKey(contentId)) {
                    return;
                }
                
                int oldPopularity = contentPopularity.get(contentId);
                int newPopularity = oldPopularity - 1;
                
                // Remove from old popularity bucket
                removeFromPopularityBucket(oldPopularity, contentId);
                
                if (newPopularity <= 0) {
                    // Remove content entirely
                    contentPopularity.remove(contentId);
                } else {
                    // Add to new popularity bucket
                    contentPopularity.put(contentId, newPopularity);
                    addToPopularityBucket(newPopularity, contentId);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public int getMostPopularContent() {
            lock.readLock().lock();
            try {
                if (popularityToContents.isEmpty()) {
                    return -1;
                }
                
                Map.Entry<Integer, Set<Integer>> lastEntry = popularityToContents.lastEntry();
                if (lastEntry == null || lastEntry.getKey() <= 0) {
                    return -1;
                }
                
                Set<Integer> contentsWithMaxPopularity = lastEntry.getValue();
                if (contentsWithMaxPopularity.isEmpty()) {
                    return -1;
                }
                
                // Return any content with max popularity (deterministic with LinkedHashSet)
                return contentsWithMaxPopularity.iterator().next();
            } finally {
                lock.readLock().unlock();
            }
        }
        
        private void addToPopularityBucket(int popularity, int contentId) {
            popularityToContents.computeIfAbsent(popularity, k -> new LinkedHashSet<>()).add(contentId);
        }
        
        private void removeFromPopularityBucket(int popularity, int contentId) {
            Set<Integer> contents = popularityToContents.get(popularity);
            if (contents != null) {
                contents.remove(contentId);
                if (contents.isEmpty()) {
                    popularityToContents.remove(popularity);
                }
            }
        }
        
        public int getPopularity(int contentId) {
            lock.readLock().lock();
            try {
                return contentPopularity.getOrDefault(contentId, 0);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public Map<Integer, Integer> getAllPopularities() {
            lock.readLock().lock();
            try {
                return new HashMap<>(contentPopularity);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public List<Integer> getTopKContents(int k) {
            lock.readLock().lock();
            try {
                List<Integer> result = new ArrayList<>();
                
                // Iterate from highest to lowest popularity
                for (Map.Entry<Integer, Set<Integer>> entry : popularityToContents.descendingMap().entrySet()) {
                    if (entry.getKey() <= 0) break;
                    
                    for (Integer contentId : entry.getValue()) {
                        result.add(contentId);
                        if (result.size() >= k) {
                            return result;
                        }
                    }
                }
                
                return result;
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    // Approach 3: Simple HashMap with Manual Max Tracking
    public static class SimplePopularContentTracker {
        private Map<Integer, Integer> contentPopularity;
        private int mostPopularContent;
        private int maxPopularity;
        
        public SimplePopularContentTracker() {
            this.contentPopularity = new HashMap<>();
            this.mostPopularContent = -1;
            this.maxPopularity = 0;
        }
        
        public void increasePopularity(int contentId) {
            int newPopularity = contentPopularity.getOrDefault(contentId, 0) + 1;
            contentPopularity.put(contentId, newPopularity);
            
            if (newPopularity > maxPopularity) {
                maxPopularity = newPopularity;
                mostPopularContent = contentId;
            }
        }
        
        public void decreasePopularity(int contentId) {
            if (!contentPopularity.containsKey(contentId)) {
                return;
            }
            
            int oldPopularity = contentPopularity.get(contentId);
            int newPopularity = oldPopularity - 1;
            
            if (newPopularity <= 0) {
                contentPopularity.remove(contentId);
                if (contentId == mostPopularContent) {
                    recomputeMax();
                }
            } else {
                contentPopularity.put(contentId, newPopularity);
                if (contentId == mostPopularContent && newPopularity < maxPopularity) {
                    recomputeMax();
                }
            }
        }
        
        public int getMostPopularContent() {
            return maxPopularity > 0 ? mostPopularContent : -1;
        }
        
        private void recomputeMax() {
            mostPopularContent = -1;
            maxPopularity = 0;
            
            for (Map.Entry<Integer, Integer> entry : contentPopularity.entrySet()) {
                if (entry.getValue() > maxPopularity) {
                    maxPopularity = entry.getValue();
                    mostPopularContent = entry.getKey();
                }
            }
        }
        
        public int getPopularity(int contentId) {
            return contentPopularity.getOrDefault(contentId, 0);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Testing PopularContentTracker ===");
        
        PopularContentTracker tracker = new PopularContentTracker();
        
        // Test basic functionality
        System.out.println("Initial most popular: " + tracker.getMostPopularContent()); // Should be -1
        
        // Increase popularity
        tracker.increasePopularity(1);
        tracker.increasePopularity(2);
        tracker.increasePopularity(1);
        tracker.increasePopularity(3);
        
        System.out.println("Most popular after increases: " + tracker.getMostPopularContent()); // Should be 1 (popularity 2)
        System.out.println("Content 1 popularity: " + tracker.getPopularity(1)); // Should be 2
        
        // Decrease popularity
        tracker.decreasePopularity(1);
        tracker.decreasePopularity(1);
        System.out.println("Most popular after decreasing 1: " + tracker.getMostPopularContent()); // Should be 2 or 3
        
        // Test edge cases
        tracker.decreasePopularity(999); // Non-existent content
        System.out.println("After decreasing non-existent: " + tracker.getMostPopularContent());
        
        // Test top K
        tracker.increasePopularity(4);
        tracker.increasePopularity(4);
        tracker.increasePopularity(4);
        List<Integer> top3 = tracker.getTopKContents(3);
        System.out.println("Top 3 contents: " + top3);
        
        // Performance test
        System.out.println("=== Performance Test ===");
        PopularContentTracker perfTracker = new PopularContentTracker();
        
        long startTime = System.currentTimeMillis();
        Random random = new Random(42);
        
        for (int i = 0; i < 100000; i++) {
            int contentId = random.nextInt(1000) + 1;
            if (random.nextBoolean()) {
                perfTracker.increasePopularity(contentId);
            } else {
                perfTracker.decreasePopularity(contentId);
            }
            
            if (i % 10000 == 0) {
                perfTracker.getMostPopularContent();
            }
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("Performance test completed in: " + (endTime - startTime) + "ms");
        System.out.println("Final most popular: " + perfTracker.getMostPopularContent());
    }
}