package practice.atlassian.data_structures.q3_commodity_prices;// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class DataPoint {
    private long timestamp;
    private double commodityPrice;
    
    public DataPoint(long timestamp, double commodityPrice) {
        this.timestamp = timestamp;
        this.commodityPrice = commodityPrice;
    }
    
    public long getTimestamp() { return timestamp; }
    public double getCommodityPrice() { return commodityPrice; }
    
    @Override
    public String toString() {
        return "DataPoint(" + timestamp + ", " + commodityPrice + ")";
    }
}

public class Solution {
    
    // Approach 1: TreeMap with Max Tracking (Thread-Safe)
    public static class CommodityPriceTracker {
        private final TreeMap<Long, Double> timestampToPriceMap;
        private double maxPrice;
        private boolean hasData;
        private final ReadWriteLock lock;
        
        public CommodityPriceTracker() {
            this.timestampToPriceMap = new TreeMap<>();
            this.maxPrice = Double.MIN_VALUE;
            this.hasData = false;
            this.lock = new ReentrantReadWriteLock();
        }
        
        public void upsert(long timestamp, double commodityPrice) {
            lock.writeLock().lock();
            try {
                Double oldPrice = timestampToPriceMap.put(timestamp, commodityPrice);
                
                if (!hasData) {
                    maxPrice = commodityPrice;
                    hasData = true;
                } else if (oldPrice == null) {
                    // New timestamp
                    maxPrice = Math.max(maxPrice, commodityPrice);
                } else if (oldPrice == maxPrice && commodityPrice < maxPrice) {
                    // We're updating the max price with a smaller value
                    recalculateMax();
                } else {
                    // Update existing timestamp or new price is not affecting max
                    maxPrice = Math.max(maxPrice, commodityPrice);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        public double getMaxCommodityPrice() {
            lock.readLock().lock();
            try {
                if (!hasData) {
                    throw new IllegalStateException("No data available");
                }
                return maxPrice;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        private void recalculateMax() {
            if (timestampToPriceMap.isEmpty()) {
                hasData = false;
                maxPrice = Double.MIN_VALUE;
            } else {
                maxPrice = Collections.max(timestampToPriceMap.values());
            }
        }
        
        public int size() {
            lock.readLock().lock();
            try {
                return timestampToPriceMap.size();
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public Double getPriceAtTimestamp(long timestamp) {
            lock.readLock().lock();
            try {
                return timestampToPriceMap.get(timestamp);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public Map<Long, Double> getAllData() {
            lock.readLock().lock();
            try {
                return new TreeMap<>(timestampToPriceMap);
            } finally {
                lock.readLock().unlock();
            }
        }
    }
    
    // Approach 2: Concurrent Version using ConcurrentSkipListMap
    public static class ConcurrentCommodityPriceTracker {
        private final ConcurrentSkipListMap<Long, Double> timestampToPriceMap;
        private volatile double maxPrice;
        private volatile boolean hasData;
        private final Object maxLock = new Object();
        
        public ConcurrentCommodityPriceTracker() {
            this.timestampToPriceMap = new ConcurrentSkipListMap<>();
            this.maxPrice = Double.MIN_VALUE;
            this.hasData = false;
        }
        
        public void upsert(long timestamp, double commodityPrice) {
            Double oldPrice = timestampToPriceMap.put(timestamp, commodityPrice);
            
            synchronized (maxLock) {
                if (!hasData) {
                    maxPrice = commodityPrice;
                    hasData = true;
                } else if (oldPrice == null) {
                    maxPrice = Math.max(maxPrice, commodityPrice);
                } else if (Double.compare(oldPrice, maxPrice) == 0 && commodityPrice < maxPrice) {
                    recalculateMax();
                } else {
                    maxPrice = Math.max(maxPrice, commodityPrice);
                }
            }
        }
        
        public double getMaxCommodityPrice() {
            if (!hasData) {
                throw new IllegalStateException("No data available");
            }
            return maxPrice;
        }
        
        private void recalculateMax() {
            if (timestampToPriceMap.isEmpty()) {
                hasData = false;
                maxPrice = Double.MIN_VALUE;
            } else {
                maxPrice = timestampToPriceMap.values().stream()
                    .mapToDouble(Double::doubleValue)
                    .max()
                    .orElse(Double.MIN_VALUE);
            }
        }
        
        public int size() {
            return timestampToPriceMap.size();
        }
        
        public Double getPriceAtTimestamp(long timestamp) {
            return timestampToPriceMap.get(timestamp);
        }
    }
    
    // Approach 3: Simple Version with Lazy Recalculation
    public static class SimpleCommodityPriceTracker {
        private final TreeMap<Long, Double> timestampToPriceMap;
        private Double cachedMax;
        private boolean maxCacheDirty;
        
        public SimpleCommodityPriceTracker() {
            this.timestampToPriceMap = new TreeMap<>();
            this.cachedMax = null;
            this.maxCacheDirty = true;
        }
        
        public void upsert(long timestamp, double commodityPrice) {
            timestampToPriceMap.put(timestamp, commodityPrice);
            
            // Mark cache as dirty if we need to recalculate
            if (cachedMax == null || commodityPrice > cachedMax) {
                cachedMax = commodityPrice;
                maxCacheDirty = false;
            } else {
                maxCacheDirty = true;
            }
        }
        
        public double getMaxCommodityPrice() {
            if (timestampToPriceMap.isEmpty()) {
                throw new IllegalStateException("No data available");
            }
            
            if (maxCacheDirty || cachedMax == null) {
                cachedMax = Collections.max(timestampToPriceMap.values());
                maxCacheDirty = false;
            }
            
            return cachedMax;
        }
        
        public int size() {
            return timestampToPriceMap.size();
        }
        
        public Double getPriceAtTimestamp(long timestamp) {
            return timestampToPriceMap.get(timestamp);
        }
    }
    
    // Best approach using TreeMap + TreeSet for prices for 0(1) max retrieval, log(n) upsert
    public static class DualStructurePriceTracker {
        private final TreeMap<Long, Double> timestampToPriceMap;
        private final TreeMap<Double, Integer> priceToCountMap;
        
        public DualStructurePriceTracker() {
            this.timestampToPriceMap = new TreeMap<>();
            this.priceToCountMap = new TreeMap<>();
        }
        
        public void upsert(long timestamp, double commodityPrice) {
            Double oldPrice = timestampToPriceMap.put(timestamp, commodityPrice);
            
            // Remove old price from count map
            if (oldPrice != null) {
                Integer count = priceToCountMap.get(oldPrice);
                if (count != null) {
                    if (count == 1) {
                        priceToCountMap.remove(oldPrice);
                    } else {
                        priceToCountMap.put(oldPrice, count - 1);
                    }
                }
            }
            
            // Add new price to count map
            priceToCountMap.merge(commodityPrice, 1, Integer::sum);
        }
        
        public double getMaxCommodityPrice() {
            if (priceToCountMap.isEmpty()) {
                throw new IllegalStateException("No data available");
            }
            return priceToCountMap.lastKey(); //o(1) max retrieval
        }
        
        public int size() {
            return timestampToPriceMap.size();
        }
        
        public Double getPriceAtTimestamp(long timestamp) {
            return timestampToPriceMap.get(timestamp);
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Testing CommodityPriceTracker ===");
        
        CommodityPriceTracker tracker = new CommodityPriceTracker();
        
        // Test basic functionality
        tracker.upsert(1000, 100.5);
        tracker.upsert(2000, 95.3);
        tracker.upsert(3000, 110.7);
        
        System.out.println("Max price: " + tracker.getMaxCommodityPrice()); // Should be 110.7
        
        // Test out-of-order insertion
        tracker.upsert(1500, 120.0);
        System.out.println("Max after out-of-order insert: " + tracker.getMaxCommodityPrice()); // Should be 120.0
        
        // Test duplicate timestamp (update)
        tracker.upsert(1500, 80.0);
        System.out.println("Max after updating max entry: " + tracker.getMaxCommodityPrice()); // Should recalculate
        
        // Test performance comparison
        System.out.println("\n=== Performance Comparison ===");
        
        CommodityPriceTracker tracker1 = new CommodityPriceTracker();
        SimpleCommodityPriceTracker tracker2 = new SimpleCommodityPriceTracker();
        DualStructurePriceTracker tracker3 = new DualStructurePriceTracker();
        
        Random random = new Random(42);
        int numOperations = 10000;
        
        // Test TreeMap with Max Tracking
        long start = System.currentTimeMillis();
        for (int i = 0; i < numOperations; i++) {
            tracker1.upsert(random.nextLong(), random.nextDouble() * 1000);
            if (i % 1000 == 0) {
                tracker1.getMaxCommodityPrice();
            }
        }
        long time1 = System.currentTimeMillis() - start;
        
        // Test Simple with Lazy Recalculation
        start = System.currentTimeMillis();
        for (int i = 0; i < numOperations; i++) {
            tracker2.upsert(random.nextLong(), random.nextDouble() * 1000);
            if (i % 1000 == 0) {
                tracker2.getMaxCommodityPrice();
            }
        }
        long time2 = System.currentTimeMillis() - start;
        
        // Test Dual Structure
        start = System.currentTimeMillis();
        for (int i = 0; i < numOperations; i++) {
            tracker3.upsert(random.nextLong(), random.nextDouble() * 1000);
            if (i % 1000 == 0) {
                tracker3.getMaxCommodityPrice();
            }
        }
        long time3 = System.currentTimeMillis() - start;
        
        System.out.println("TreeMap with Max Tracking: " + time1 + "ms");
        System.out.println("Simple Lazy Recalculation: " + time2 + "ms");
        System.out.println("Dual Structure: " + time3 + "ms");
        
        System.out.println("\nFinal sizes:");
        System.out.println("Tracker1: " + tracker1.size());
        System.out.println("Tracker2: " + tracker2.size());
        System.out.println("Tracker3: " + tracker3.size());
    }
}