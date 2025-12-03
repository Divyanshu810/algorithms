package Company.atlassian.code_design.Q9_FileCollectionTracker;

/*
┌─────────────────────────────────────────────────────────────────┐
│                       FileRecord                                 │
├─────────────────────────────────────────────────────────────────┤
│ - fileName: String                                              │
│ - fileSize: long                                                │
│ - collections: Set<String>                                      │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                    CollectionStats                               │
├─────────────────────────────────────────────────────────────────┤
│ - name: String                                                  │
│ - totalSize: long                                               │
│ - fileCount: int                                                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     FileTracker                                  │
├─────────────────────────────────────────────────────────────────┤
│ - files: Map<String, FileRecord>                                │
│ - collectionFiles: Map<String, Set<String>>                     │
├─────────────────────────────────────────────────────────────────┤
│ + addFile(name, size, collections): void                        │
│ + updateFile(name, size, collections): void                     │
│ + removeFile(name): void                                        │
│ + getTotalSize(): long                                          │
│ + getTopCollectionsBySize(n): List<CollectionStats>             │
│ + getTopCollectionsByFileCount(n): List<CollectionStats>        │
└─────────────────────────────────────────────────────────────────┘

Files Map:
┌─────────────────────────────────────────────────────────────────┐
│ files = {                                                        │
│   "file1.txt" → FileRecord("file1.txt", 100, ["docs", "work"])  │
│   "file2.pdf" → FileRecord("file2.pdf", 200, ["docs"])          │
│   "file3.jpg" → FileRecord("file3.jpg", 500, [])                │
│ }                                                                │
└─────────────────────────────────────────────────────────────────┘

Collection → Files Map (reverse index for efficient lookups):
┌─────────────────────────────────────────────────────────────────┐
│ collectionFiles = {                                              │
│   "docs" → {"file1.txt", "file2.pdf"}                           │
│   "work" → {"file1.txt"}                                        │
│ }                                                                │
└─────────────────────────────────────────────────────────────────┘
 */


import java.util.*;
public class Sol {

    public class FileRecord {
        private final String fileName;
        private long fileSize;
        private Set<String> collections;

        public FileRecord(String fileName, long fileSize, List<String> collections) {
            this.fileName = fileName;
            this.fileSize = fileSize;
            this.collections = new HashSet<>();
            if (collections != null) {
                this.collections.addAll(collections);
            }
        }

        public String getFileName() {
            return fileName;
        }

        public long getFileSize() {
            return fileSize;
        }

        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }

        public Set<String> getCollections() {
            return collections;
        }

        public void setCollections(List<String> collections) {
            this.collections.clear();
            if (collections != null) {
                this.collections.addAll(collections);
            }
        }

        @Override
        public String toString() {
            return fileName + " (" + fileSize + " bytes) - " + collections;
        }
    }

    public class CollectionStats {
        private final String name;
        private final long totalSize;
        private final int fileCount;

        public CollectionStats(String name, long totalSize, int fileCount) {
            this.name = name;
            this.totalSize = totalSize;
            this.fileCount = fileCount;
        }

        public String getName() {
            return name;
        }

        public long getTotalSize() {
            return totalSize;
        }

        public int getFileCount() {
            return fileCount;
        }

        @Override
        public String toString() {
            return name + " - Size: " + totalSize + " bytes, Files: " + fileCount;
        }
    }

    import java.util.*;

    public class FileTracker {

        // fileName → FileRecord
        private final Map<String, FileRecord> files;

        // collectionName → Set of fileNames (reverse index)
        private final Map<String, Set<String>> collectionFiles;

        public FileTracker() {
            this.files = new HashMap<>();
            this.collectionFiles = new HashMap<>();
        }

        // Add a new file
        public void addFile(String fileName, long fileSize, List<String> collections) {
            if (files.containsKey(fileName)) {
                // File exists, update instead
                updateFile(fileName, fileSize, collections);
                return;
            }

            // Create new file record
            FileRecord record = new FileRecord(fileName, fileSize, collections);
            files.put(fileName, record);

            // Add to collection index
            if (collections != null) {
                for (String collection : collections) {
                    addFileToCollection(fileName, collection);
                }
            }
        }

        // Update existing file
        public void updateFile(String fileName, long fileSize, List<String> newCollections) {
            FileRecord record = files.get(fileName);
            if (record == null) {
                // File doesn't exist, add it
                addFile(fileName, fileSize, newCollections);
                return;
            }

            // Get old collections
            Set<String> oldCollections = record.getCollections();

            // Remove file from old collections
            for (String oldCollection : oldCollections) {
                removeFileFromCollection(fileName, oldCollection);
            }

            // Update file record
            record.setFileSize(fileSize);
            record.setCollections(newCollections);

            // Add file to new collections
            if (newCollections != null) {
                for (String newCollection : newCollections) {
                    addFileToCollection(fileName, newCollection);
                }
            }
        }

        // Remove a file
        public void removeFile(String fileName) {
            FileRecord record = files.get(fileName);
            if (record == null) {
                return;
            }

            // Remove from all collections
            for (String collection : record.getCollections()) {
                removeFileFromCollection(fileName, collection);
            }

            // Remove from files map
            files.remove(fileName);
        }

        // Get total size of all files
        public long getTotalSize() {
            long total = 0;
            for (FileRecord record : files.values()) {
                total += record.getFileSize();
            }
            return total;
        }

        // Get total file count
        public int getTotalFileCount() {
            return files.size();
        }

        // Get top N collections by size
        public List<CollectionStats> getTopCollectionsBySize(int n) {
            List<CollectionStats> allStats = getAllCollectionStats();

            // Sort by size (descending)
            Collections.sort(allStats, (a, b) -> Long.compare(b.getTotalSize(), a.getTotalSize()));

            // Return top N
            List<CollectionStats> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, allStats.size()); i++) {
                result.add(allStats.get(i));
            }
            return result;
        }

        // Get top N collections by file count
        public List<CollectionStats> getTopCollectionsByFileCount(int n) {
            List<CollectionStats> allStats = getAllCollectionStats();

            // Sort by file count (descending)
            Collections.sort(allStats, (a, b) -> Integer.compare(b.getFileCount(), a.getFileCount()));

            // Return top N
            List<CollectionStats> result = new ArrayList<>();
            for (int i = 0; i < Math.min(n, allStats.size()); i++) {
                result.add(allStats.get(i));
            }
            return result;
        }

        // Get stats for a specific collection
        public CollectionStats getCollectionStats(String collectionName) {
            Set<String> fileNames = collectionFiles.get(collectionName);
            if (fileNames == null || fileNames.isEmpty()) {
                return new CollectionStats(collectionName, 0, 0);
            }

            long totalSize = 0;
            for (String fileName : fileNames) {
                FileRecord record = files.get(fileName);
                if (record != null) {
                    totalSize += record.getFileSize();
                }
            }

            return new CollectionStats(collectionName, totalSize, fileNames.size());
        }

        // Get files in a collection
        public List<FileRecord> getFilesInCollection(String collectionName) {
            Set<String> fileNames = collectionFiles.get(collectionName);
            if (fileNames == null) {
                return new ArrayList<>();
            }

            List<FileRecord> result = new ArrayList<>();
            for (String fileName : fileNames) {
                FileRecord record = files.get(fileName);
                if (record != null) {
                    result.add(record);
                }
            }
            return result;
        }

        // Get files not in any collection
        public List<FileRecord> getFilesWithoutCollection() {
            List<FileRecord> result = new ArrayList<>();
            for (FileRecord record : files.values()) {
                if (record.getCollections().isEmpty()) {
                    result.add(record);
                }
            }
            return result;
        }

        // Helper: Add file to collection index
        private void addFileToCollection(String fileName, String collectionName) {
            if (!collectionFiles.containsKey(collectionName)) {
                collectionFiles.put(collectionName, new HashSet<>());
            }
            collectionFiles.get(collectionName).add(fileName);
        }

        // Helper: Remove file from collection index
        private void removeFileFromCollection(String fileName, String collectionName) {
            Set<String> fileNames = collectionFiles.get(collectionName);
            if (fileNames != null) {
                fileNames.remove(fileName);

                // Remove empty collection
                if (fileNames.isEmpty()) {
                    collectionFiles.remove(collectionName);
                }
            }
        }

        // Helper: Get all collection stats
        private List<CollectionStats> getAllCollectionStats() {
            List<CollectionStats> result = new ArrayList<>();

            for (String collectionName : collectionFiles.keySet()) {
                CollectionStats stats = getCollectionStats(collectionName);
                result.add(stats);
            }

            return result;
        }

        // For testing/debugging
        public FileRecord getFile(String fileName) {
            return files.get(fileName);
        }

        public int getCollectionCount() {
            return collectionFiles.size();
        }
    }
}
