package Company.atlassian.code_design.Q9_FileCollectionTracker;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileTrackerTest {

    private FileTracker tracker;

    @BeforeEach
    void setUp() {
        tracker = new FileTracker();
    }

    @Nested
    @DisplayName("Add File Tests")
    class AddFileTests {

        @Test
        @DisplayName("Should add file with collections")
        void testAddFileWithCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "work"));

            FileRecord record = tracker.getFile("file1.txt");

            assertNotNull(record);
            assertEquals(100, record.getFileSize());
            assertEquals(2, record.getCollections().size());
            assertTrue(record.getCollections().contains("docs"));
            assertTrue(record.getCollections().contains("work"));
        }

        @Test
        @DisplayName("Should add file without collections")
        void testAddFileWithoutCollections() {
            tracker.addFile("file1.txt", 100, null);

            FileRecord record = tracker.getFile("file1.txt");

            assertNotNull(record);
            assertEquals(0, record.getCollections().size());
        }

        @Test
        @DisplayName("Should add file with empty collections list")
        void testAddFileEmptyCollections() {
            tracker.addFile("file1.txt", 100, Collections.emptyList());

            FileRecord record = tracker.getFile("file1.txt");

            assertNotNull(record);
            assertEquals(0, record.getCollections().size());
        }

        @Test
        @DisplayName("Should update if file already exists")
        void testAddExistingFile() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.addFile("file1.txt", 200, Arrays.asList("work"));

            FileRecord record = tracker.getFile("file1.txt");

            assertEquals(200, record.getFileSize());
            assertEquals(1, record.getCollections().size());
            assertTrue(record.getCollections().contains("work"));
        }
    }

    @Nested
    @DisplayName("Update File Tests")
    class UpdateFileTests {

        @Test
        @DisplayName("Should update file size")
        void testUpdateFileSize() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.updateFile("file1.txt", 200, Arrays.asList("docs"));

            assertEquals(200, tracker.getFile("file1.txt").getFileSize());
        }

        @Test
        @DisplayName("Should update file collections")
        void testUpdateFileCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "work"));
            tracker.updateFile("file1.txt", 100, Arrays.asList("photos"));

            FileRecord record = tracker.getFile("file1.txt");

            assertEquals(1, record.getCollections().size());
            assertTrue(record.getCollections().contains("photos"));
            assertFalse(record.getCollections().contains("docs"));
        }

        @Test
        @DisplayName("Should remove file from old collections on update")
        void testUpdateRemovesFromOldCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.updateFile("file1.txt", 100, Arrays.asList("work"));

            List<FileRecord> docsFiles = tracker.getFilesInCollection("docs");
            List<FileRecord> workFiles = tracker.getFilesInCollection("work");

            assertEquals(0, docsFiles.size());
            assertEquals(1, workFiles.size());
        }
    }

    @Nested
    @DisplayName("Remove File Tests")
    class RemoveFileTests {

        @Test
        @DisplayName("Should remove file completely")
        void testRemoveFile() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.removeFile("file1.txt");

            assertNull(tracker.getFile("file1.txt"));
            assertEquals(0, tracker.getTotalFileCount());
        }

        @Test
        @DisplayName("Should remove file from all collections")
        void testRemoveFileFromCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "work"));
            tracker.removeFile("file1.txt");

            assertEquals(0, tracker.getFilesInCollection("docs").size());
            assertEquals(0, tracker.getFilesInCollection("work").size());
        }

        @Test
        @DisplayName("Should handle removing non-existent file")
        void testRemoveNonExistentFile() {
            assertDoesNotThrow(() -> tracker.removeFile("nonexistent.txt"));
        }
    }

    @Nested
    @DisplayName("Total Size Tests")
    class TotalSizeTests {

        @Test
        @DisplayName("Should calculate total size of all files")
        void testTotalSize() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.addFile("file2.pdf", 200, Arrays.asList("docs"));
            tracker.addFile("file3.jpg", 500, null);

            assertEquals(800, tracker.getTotalSize());
        }

        @Test
        @DisplayName("Should count each file once even if in multiple collections")
        void testTotalSizeNoDuplicates() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "work", "photos"));
            tracker.addFile("file2.pdf", 200, Arrays.asList("docs", "work"));

            // file1 is in 3 collections but counted once (100)
            // file2 is in 2 collections but counted once (200)
            assertEquals(300, tracker.getTotalSize());
        }

        @Test
        @DisplayName("Should return zero for empty tracker")
        void testTotalSizeEmpty() {
            assertEquals(0, tracker.getTotalSize());
        }
    }

    @Nested
    @DisplayName("Top Collections by Size Tests")
    class TopCollectionsBySizeTests {

        @BeforeEach
        void setUpCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "work"));
            tracker.addFile("file2.pdf", 200, Arrays.asList("docs"));
            tracker.addFile("file3.jpg", 500, Arrays.asList("photos"));
            tracker.addFile("file4.mp4", 1000, Arrays.asList("videos"));
        }

        @Test
        @DisplayName("Should return top N collections by size")
        void testTopBySize() {
            List<CollectionStats> top2 = tracker.getTopCollectionsBySize(2);

            assertEquals(2, top2.size());
            assertEquals("videos", top2.get(0).getName());  // 1000
            assertEquals("photos", top2.get(1).getName());  // 500
        }

        @Test
        @DisplayName("Should calculate collection size correctly")
        void testCollectionSizeCalculation() {
            // docs has file1 (100) + file2 (200) = 300
            List<CollectionStats> all = tracker.getTopCollectionsBySize(10);

            CollectionStats docs = null;
            for (CollectionStats stats : all) {
                if (stats.getName().equals("docs")) {
                    docs = stats;
                    break;
                }
            }

            assertNotNull(docs);
            assertEquals(300, docs.getTotalSize());
            assertEquals(2, docs.getFileCount());
        }

        @Test
        @DisplayName("Should handle requesting more than available")
        void testTopMoreThanAvailable() {
            List<CollectionStats> top = tracker.getTopCollectionsBySize(100);

            assertEquals(4, top.size());  // Only 4 collections exist
        }
    }

    @Nested
    @DisplayName("Top Collections by File Count Tests")
    class TopCollectionsByFileCountTests {

        @BeforeEach
        void setUpCollections() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.addFile("file2.pdf", 200, Arrays.asList("docs"));
            tracker.addFile("file3.jpg", 500, Arrays.asList("docs"));
            tracker.addFile("file4.mp4", 1000, Arrays.asList("videos"));
            tracker.addFile("file5.mp3", 50, Arrays.asList("videos"));
        }

        @Test
        @DisplayName("Should return top N collections by file count")
        void testTopByFileCount() {
            List<CollectionStats> top1 = tracker.getTopCollectionsByFileCount(1);

            assertEquals(1, top1.size());
            assertEquals("docs", top1.get(0).getName());  // 3 files
            assertEquals(3, top1.get(0).getFileCount());
        }
    }

    @Nested
    @DisplayName("Files Without Collection Tests")
    class FilesWithoutCollectionTests {

        @Test
        @DisplayName("Should find files without collections")
        void testFilesWithoutCollection() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs"));
            tracker.addFile("file2.pdf", 200, null);
            tracker.addFile("file3.jpg", 500, Collections.emptyList());

            List<FileRecord> orphans = tracker.getFilesWithoutCollection();

            assertEquals(2, orphans.size());
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        @Test
        @DisplayName("Should handle file in same collection twice in input")
        void testDuplicateCollectionInInput() {
            tracker.addFile("file1.txt", 100, Arrays.asList("docs", "docs", "docs"));

            Sol.FileRecord record = tracker.getFile("file1.txt");

            // Should deduplicate
            assertEquals(1, record.getCollections().size());
        }

        @Test
        @DisplayName("Should handle empty tracker operations")
        void testEmptyTrackerOperations() {
            assertEquals(0, tracker.getTotalSize());
            assertEquals(0, tracker.getTotalFileCount());
            assertEquals(0, tracker.getTopCollectionsBySize(5).size());
            assertEquals(0, tracker.getFilesWithoutCollection().size());
        }
    }
}