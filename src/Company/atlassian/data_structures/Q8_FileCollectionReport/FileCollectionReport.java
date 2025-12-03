package Company.atlassian.data_structures.Q8_FileCollectionReport;

/*
 - Each file has a collectionId attached. How would you generate a report to show:
 - The total size of all files.
 - The top N collections ranked by total file size.
 - How would you modify the system if multiple collections can be associated with a single file?
 - How would you design and optimize this solution for a multithreaded environment to ensure correctness and efficiency?
 */

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * File Collection Reporting System
 *
 * ===================================================================================
 * PROBLEM:
 * ===================================================================================
 * - Each file has a size and belongs to collection(s)
 * - Generate reports:
 *   1. Total size of all files
 *   2. Top N collections by total file size
 * - Handle single collection per file (Part A)
 * - Handle multiple collections per file (Part B)
 * - Make thread-safe (Part C)
 *
 * ===================================================================================
 * PARTS:
 * ===================================================================================
 * Part A: Single collection per file
 * Part B: Multiple collections per file (many-to-many)
 * Part C: Thread-safe with ReadWriteLock
 * ===================================================================================
 */
public class FileCollectionReport {

    // ==================== DATA CLASSES ====================

    static class File {
        String fileId;
        long size;

        File(String fileId, long size) {
            this.fileId = fileId;
            this.size = size;
        }
    }

    // ==================== PART A: Single Collection Per File ====================
    /**
     * Each file belongs to exactly ONE collection.
     *
     * Data Structures:
     * - Map<fileId, File> for file lookup
     * - Map<fileId, collectionId> for file-to-collection mapping
     * - Map<collectionId, Set<fileId>> for collection-to-files mapping
     *
     * Operations:
     * - addFile: O(1)
     * - getTotalSize: O(n) or O(1) with caching
     * - getTopNCollections: O(c log c) where c = number of collections
     */
    static class SingleCollectionSystem {

        private Map<String, File> files;                      // fileId -> File
        private Map<String, String> fileToCollection;         // fileId -> collectionId
        private Map<String, Set<String>> collectionToFiles;   // collectionId -> set of fileIds

        public SingleCollectionSystem() {
            this.files = new HashMap<>();
            this.fileToCollection = new HashMap<>();
            this.collectionToFiles = new HashMap<>();
        }

        // O(1)
        public void addFile(String fileId, long size, String collectionId) {
            File file = new File(fileId, size);
            files.put(fileId, file);
            fileToCollection.put(fileId, collectionId);
            collectionToFiles.computeIfAbsent(collectionId, k -> new HashSet<>()).add(fileId);
        }

        // O(1)
        public void removeFile(String fileId) {
            if (!files.containsKey(fileId)) return;

            files.remove(fileId);
            String collectionId = fileToCollection.remove(fileId);
            if (collectionId != null && collectionToFiles.containsKey(collectionId)) {
                collectionToFiles.get(collectionId).remove(fileId);
                if (collectionToFiles.get(collectionId).isEmpty()) {
                    collectionToFiles.remove(collectionId);
                }
            }
        }

        // O(n) - scan all files
        public long getTotalSize() {
            long total = 0;
            for (File file : files.values()) {
                total += file.size;
            }
            return total;
        }

        // O(c log c) where c = number of collections
        public List<Map.Entry<String, Long>> getTopNCollections(int n) {
            // Step 1: Calculate size per collection
            Map<String, Long> collectionSizes = new HashMap<>();

            for (Map.Entry<String, Set<String>> entry : collectionToFiles.entrySet()) {
                String collectionId = entry.getKey();
                long totalSize = 0;

                for (String fileId : entry.getValue()) {
                    totalSize += files.get(fileId).size;
                }

                collectionSizes.put(collectionId, totalSize);
            }

            // Step 2: Use min-heap to find top N
            // Min-heap: smallest at top, evict when size > N
            PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
                    (a, b) -> Long.compare(a.getValue(), b.getValue())
            );

            for (Map.Entry<String, Long> entry : collectionSizes.entrySet()) {
                minHeap.offer(entry);
                if (minHeap.size() > n) {
                    minHeap.poll();  // Remove smallest
                }
            }

            // Step 3: Extract results (convert to descending order)
            List<Map.Entry<String, Long>> result = new ArrayList<>();
            while (!minHeap.isEmpty()) {
                result.add(minHeap.poll());
            }
            Collections.reverse(result);  // Largest first

            return result;
        }

        // Get size of specific collection - O(f) where f = files in collection
        public long getCollectionSize(String collectionId) {
            Set<String> fileIds = collectionToFiles.get(collectionId);
            if (fileIds == null) return 0;

            long total = 0;
            for (String fileId : fileIds) {
                total += files.get(fileId).size;
            }
            return total;
        }
    }

    // ==================== PART B: Multiple Collections Per File ====================
    /**
     * Each file can belong to MULTIPLE collections (many-to-many).
     *
     * Key Change: fileToCollection becomes fileToCollections (Set)
     *
     * Important: When calculating total size, don't double-count files!
     * - Total size = sum of all unique files (not sum of collection sizes)
     */
    static class MultiCollectionSystem {

        private Map<String, File> files;                       // fileId -> File
        private Map<String, Set<String>> fileToCollections;    // fileId -> set of collectionIds
        private Map<String, Set<String>> collectionToFiles;    // collectionId -> set of fileIds

        public MultiCollectionSystem() {
            this.files = new HashMap<>();
            this.fileToCollections = new HashMap<>();
            this.collectionToFiles = new HashMap<>();
        }

        // O(1)
        public void addFile(String fileId, long size) {
            files.put(fileId, new File(fileId, size));
            fileToCollections.computeIfAbsent(fileId, k -> new HashSet<>());
        }

        // O(1)
        public void addFileToCollection(String fileId, String collectionId) {
            if (!files.containsKey(fileId)) return;

            fileToCollections.computeIfAbsent(fileId, k -> new HashSet<>()).add(collectionId);
            collectionToFiles.computeIfAbsent(collectionId, k -> new HashSet<>()).add(fileId);
        }

        // O(c) where c = collections the file belongs to
        public void removeFile(String fileId) {
            if (!files.containsKey(fileId)) return;

            files.remove(fileId);

            // Remove from all collections
            Set<String> collections = fileToCollections.remove(fileId);
            if (collections != null) {
                for (String collectionId : collections) {
                    Set<String> filesInCollection = collectionToFiles.get(collectionId);
                    if (filesInCollection != null) {
                        filesInCollection.remove(fileId);
                        if (filesInCollection.isEmpty()) {
                            collectionToFiles.remove(collectionId);
                        }
                    }
                }
            }
        }

        // O(1)
        public void removeFileFromCollection(String fileId, String collectionId) {
            Set<String> collections = fileToCollections.get(fileId);
            if (collections != null) {
                collections.remove(collectionId);
            }

            Set<String> filesInCollection = collectionToFiles.get(collectionId);
            if (filesInCollection != null) {
                filesInCollection.remove(fileId);
                if (filesInCollection.isEmpty()) {
                    collectionToFiles.remove(collectionId);
                }
            }
        }

        // O(n) - Total of all UNIQUE files (no double counting)
        public long getTotalSize() {
            long total = 0;
            for (File file : files.values()) {
                total += file.size;
            }
            return total;
        }

        // O(c × f) where c = collections, f = avg files per collection
        public List<Map.Entry<String, Long>> getTopNCollections(int n) {
            // Calculate size per collection
            Map<String, Long> collectionSizes = new HashMap<>();

            for (Map.Entry<String, Set<String>> entry : collectionToFiles.entrySet()) {
                String collectionId = entry.getKey();
                long totalSize = 0;

                for (String fileId : entry.getValue()) {
                    totalSize += files.get(fileId).size;
                }

                collectionSizes.put(collectionId, totalSize);
            }

            // Use min-heap to find top N
            PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
                    (a, b) -> Long.compare(a.getValue(), b.getValue())
            );

            for (Map.Entry<String, Long> entry : collectionSizes.entrySet()) {
                minHeap.offer(entry);
                if (minHeap.size() > n) {
                    minHeap.poll();
                }
            }

            List<Map.Entry<String, Long>> result = new ArrayList<>();
            while (!minHeap.isEmpty()) {
                result.add(minHeap.poll());
            }
            Collections.reverse(result);

            return result;
        }

        // Get size of specific collection
        public long getCollectionSize(String collectionId) {
            Set<String> fileIds = collectionToFiles.get(collectionId);
            if (fileIds == null) return 0;

            long total = 0;
            for (String fileId : fileIds) {
                total += files.get(fileId).size;
            }
            return total;
        }
    }

    // ==================== PART C: Thread-Safe with ReadWriteLock ====================
    /**
     * Thread-safe implementation using ReadWriteLock.
     *
     * - Multiple readers can read simultaneously
     * - Writers get exclusive access
     *
     * Read operations: getTotalSize, getTopNCollections, getCollectionSize
     * Write operations: addFile, removeFile, addFileToCollection, removeFileFromCollection
     */
    static class ConcurrentFileSystem {

        private Map<String, File> files;
        private Map<String, Set<String>> fileToCollections;
        private Map<String, Set<String>> collectionToFiles;

        private final ReadWriteLock lock;
        private final Lock readLock;
        private final Lock writeLock;

        public ConcurrentFileSystem() {
            this.files = new HashMap<>();
            this.fileToCollections = new HashMap<>();
            this.collectionToFiles = new HashMap<>();

            this.lock = new ReentrantReadWriteLock();
            this.readLock = lock.readLock();
            this.writeLock = lock.writeLock();
        }

        // WRITE operation
        public void addFile(String fileId, long size) {
            writeLock.lock();
            try {
                files.put(fileId, new File(fileId, size));
                fileToCollections.computeIfAbsent(fileId, k -> new HashSet<>());
            } finally {
                writeLock.unlock();
            }
        }

        // WRITE operation
        public void addFileToCollection(String fileId, String collectionId) {
            writeLock.lock();
            try {
                if (!files.containsKey(fileId)) return;

                fileToCollections.computeIfAbsent(fileId, k -> new HashSet<>()).add(collectionId);
                collectionToFiles.computeIfAbsent(collectionId, k -> new HashSet<>()).add(fileId);
            } finally {
                writeLock.unlock();
            }
        }

        // WRITE operation
        public void removeFile(String fileId) {
            writeLock.lock();
            try {
                if (!files.containsKey(fileId)) return;

                files.remove(fileId);

                Set<String> collections = fileToCollections.remove(fileId);
                if (collections != null) {
                    for (String collectionId : collections) {
                        Set<String> filesInCollection = collectionToFiles.get(collectionId);
                        if (filesInCollection != null) {
                            filesInCollection.remove(fileId);
                            if (filesInCollection.isEmpty()) {
                                collectionToFiles.remove(collectionId);
                            }
                        }
                    }
                }
            } finally {
                writeLock.unlock();
            }
        }

        // READ operation
        public long getTotalSize() {
            readLock.lock();
            try {
                long total = 0;
                for (File file : files.values()) {
                    total += file.size;
                }
                return total;
            } finally {
                readLock.unlock();
            }
        }

        // READ operation
        public List<Map.Entry<String, Long>> getTopNCollections(int n) {
            readLock.lock();
            try {
                Map<String, Long> collectionSizes = new HashMap<>();

                for (Map.Entry<String, Set<String>> entry : collectionToFiles.entrySet()) {
                    String collectionId = entry.getKey();
                    long totalSize = 0;

                    for (String fileId : entry.getValue()) {
                        totalSize += files.get(fileId).size;
                    }

                    collectionSizes.put(collectionId, totalSize);
                }

                PriorityQueue<Map.Entry<String, Long>> minHeap = new PriorityQueue<>(
                        (a, b) -> Long.compare(a.getValue(), b.getValue())
                );

                for (Map.Entry<String, Long> entry : collectionSizes.entrySet()) {
                    minHeap.offer(entry);
                    if (minHeap.size() > n) {
                        minHeap.poll();
                    }
                }

                List<Map.Entry<String, Long>> result = new ArrayList<>();
                while (!minHeap.isEmpty()) {
                    result.add(minHeap.poll());
                }
                Collections.reverse(result);

                return result;
            } finally {
                readLock.unlock();
            }
        }

        // READ operation
        public long getCollectionSize(String collectionId) {
            readLock.lock();
            try {
                Set<String> fileIds = collectionToFiles.get(collectionId);
                if (fileIds == null) return 0;

                long total = 0;
                for (String fileId : fileIds) {
                    total += files.get(fileId).size;
                }
                return total;
            } finally {
                readLock.unlock();
            }
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing File Collection Report System ===\n");

        testSingleCollection();
        testMultiCollection();
        testConcurrent();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testSingleCollection() {
        System.out.println("--- Part A: Single Collection Per File ---");

        SingleCollectionSystem system = new SingleCollectionSystem();

        // Add files to collections
        system.addFile("file1", 100, "photos");
        system.addFile("file2", 200, "photos");
        system.addFile("file3", 150, "videos");
        system.addFile("file4", 300, "videos");
        system.addFile("file5", 50, "docs");

        // Test total size
        long totalSize = system.getTotalSize();
        System.out.println("Total size: " + totalSize);
        assertResult(800L, totalSize, "Total size");

        // Test top N collections
        List<Map.Entry<String, Long>> top2 = system.getTopNCollections(2);
        System.out.println("Top 2 collections:");
        for (Map.Entry<String, Long> entry : top2) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }
        assertResult("videos", top2.get(0).getKey(), "Top collection");
        assertResult(450L, top2.get(0).getValue(), "Top collection size");

        // Test remove
        system.removeFile("file4");
        totalSize = system.getTotalSize();
        System.out.println("After removing file4, total: " + totalSize);
        assertResult(500L, totalSize, "Total after remove");

        System.out.println("Part A: PASSED\n");
    }

    private static void testMultiCollection() {
        System.out.println("--- Part B: Multiple Collections Per File ---");

        MultiCollectionSystem system = new MultiCollectionSystem();

        // Add files
        system.addFile("file1", 100);
        system.addFile("file2", 200);
        system.addFile("file3", 150);

        // Add to multiple collections
        system.addFileToCollection("file1", "photos");
        system.addFileToCollection("file1", "favorites");  // file1 in 2 collections!
        system.addFileToCollection("file2", "photos");
        system.addFileToCollection("file3", "videos");
        system.addFileToCollection("file3", "favorites");  // file3 in 2 collections!

        // Total size should NOT double count
        long totalSize = system.getTotalSize();
        System.out.println("Total size (no double count): " + totalSize);
        assertResult(450L, totalSize, "Total size (unique files)");

        // Collection sizes (can exceed total because files are shared)
        System.out.println("Collection sizes:");
        System.out.println("  photos: " + system.getCollectionSize("photos"));     // 100 + 200 = 300
        System.out.println("  favorites: " + system.getCollectionSize("favorites")); // 100 + 150 = 250
        System.out.println("  videos: " + system.getCollectionSize("videos"));     // 150

        assertResult(300L, system.getCollectionSize("photos"), "Photos size");
        assertResult(250L, system.getCollectionSize("favorites"), "Favorites size");

        // Top N
        List<Map.Entry<String, Long>> top2 = system.getTopNCollections(2);
        System.out.println("Top 2 collections:");
        for (Map.Entry<String, Long> entry : top2) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue());
        }

        System.out.println("Part B: PASSED\n");
    }

    private static void testConcurrent() {
        System.out.println("--- Part C: Concurrent System ---");

        ConcurrentFileSystem system = new ConcurrentFileSystem();

        // Add files
        system.addFile("file1", 100);
        system.addFile("file2", 200);
        system.addFileToCollection("file1", "photos");
        system.addFileToCollection("file2", "photos");

        // Test reads
        long totalSize = system.getTotalSize();
        System.out.println("Total size: " + totalSize);
        assertResult(300L, totalSize, "Concurrent total");

        long photosSize = system.getCollectionSize("photos");
        System.out.println("Photos size: " + photosSize);
        assertResult(300L, photosSize, "Concurrent photos");

        // Simulate concurrent access (simplified)
        System.out.println("ReadWriteLock ensures thread safety:");
        System.out.println("  - Multiple readers can read simultaneously");
        System.out.println("  - Writers get exclusive access");

        System.out.println("Part C: PASSED\n");
    }

    // Assertion helpers
    private static void assertResult(long expected, long actual, String testName) {
        if (expected != actual) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
        System.out.println("  ✓ " + testName + ": " + actual);
    }

    private static void assertResult(String expected, String actual, String testName) {
        if (!expected.equals(actual)) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
        System.out.println("  ✓ " + testName + ": " + actual);
    }
}