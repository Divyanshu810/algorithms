package practice.atlassian.data_structures.q2_tennis_club;// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import static org.junit.jupiter.api.Assertions.*;
import java.util.*;

public class TestSolution {
    
    private Solution solution = new Solution();
    
    // @Test
    // @DisplayName("Test basic court assignment with no overlaps")
    void testBasicNoOverlaps() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 2),
            new BookingRecord(2, 3, 4),
            new BookingRecord(3, 5, 6)
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(1, courts.size());
        assertEqualsLocal(3, courts.get(0).getBookings().size());
    }
    
    // @Test
    // @DisplayName("Test court assignment with complete overlaps")
    void testCompleteOverlaps() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4),
            new BookingRecord(2, 1, 4),
            new BookingRecord(3, 1, 4)
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(3, courts.size());
        
        for (Court court : courts) {
            assertEqualsLocal(1, court.getBookings().size());
        }
    }
    
    // @Test
    // @DisplayName("Test court assignment with partial overlaps")
    void testPartialOverlaps() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4),
            new BookingRecord(2, 2, 6),
            new BookingRecord(3, 5, 7),
            new BookingRecord(4, 3, 5)
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(3, courts.size());
        
        // Verify total bookings assigned
        int totalBookings = courts.stream().mapToInt(c -> c.getBookings().size()).sum();
        assertEqualsLocal(4, totalBookings);
    }
    
    // @Test
    // @DisplayName("Test minimum courts calculation")
    void testMinimumCourtsCalculation() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4),
            new BookingRecord(2, 2, 6),
            new BookingRecord(3, 5, 7),
            new BookingRecord(4, 3, 5),
            new BookingRecord(5, 8, 9)
        );
        
        int minCourts = solution.getMinimumCourtsNeeded(bookings);
        assertEqualsLocal(3, minCourts);
        
        // Verify assignment produces same number
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(minCourts, courts.size());
    }
    
    // @Test
    // @DisplayName("Test court assignment with maintenance time")
    void testCourtAssignmentWithMaintenance() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 3),
            new BookingRecord(2, 4, 6)  // Would normally fit on same court
        );
        
        // Without maintenance - should use 1 court
        List<Court> courtsNoMaintenance = solution.assignCourts(bookings);
        assertEqualsLocal(1, courtsNoMaintenance.size());
        
        // With 2-hour maintenance - should need 2 courts
        List<Court> courtsWithMaintenance = solution.assignCourtsWithMaintenance(bookings, 2);
        assertEqualsLocal(2, courtsWithMaintenance.size());
    }
    
    // @Test
    // @DisplayName("Test court assignment with durability maintenance")
    void testCourtAssignmentWithDurabilityMaintenance() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 2),
            new BookingRecord(2, 3, 4),
            new BookingRecord(3, 5, 6),  // This triggers maintenance
            new BookingRecord(4, 7, 8)   // Might need new court due to maintenance
        );
        
        // Maintenance after every 2 bookings
        List<Court> courts = solution.assignCourtsWithDurabilityMaintenance(bookings, 3, 2);
        
        // Verify maintenance is applied
        assertNotNullLocal(courts);
        assertTrueLocal(courts.size() >= 1);
    }
    
    // @Test
    // @DisplayName("Test booking conflict detection")
    void testBookingConflictDetection() {
        BookingRecord booking1 = new BookingRecord(1, 1, 4);
        BookingRecord booking2 = new BookingRecord(2, 3, 6);
        BookingRecord booking3 = new BookingRecord(3, 5, 7);
        
        // booking1 and booking2 overlap (1-4 and 3-6)
        assertTrueLocal(solution.doBookingsConflict(booking1, booking2));
        
        // booking2 and booking3 overlap (3-6 and 5-7)
        assertTrueLocal(solution.doBookingsConflict(booking2, booking3));
        
        // booking1 and booking3 don't overlap (1-4 and 5-7)
        assertFalseLocal(solution.doBookingsConflict(booking1, booking3));
    }
    
    // @Test
    // @DisplayName("Test finding all conflicting bookings")
    void testFindAllConflicts() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4),
            new BookingRecord(2, 3, 6),
            new BookingRecord(3, 5, 7),
            new BookingRecord(4, 8, 9)
        );
        
        List<int[]> conflicts = solution.findConflictingBookings(bookings);
        assertEqualsLocal(2, conflicts.size());
        
        // Check specific conflicts
        Set<String> conflictPairs = new HashSet<>();
        for (int[] conflict : conflicts) {
            conflictPairs.add(conflict[0] + "-" + conflict[1]);
        }
        
        assertTrueLocal(conflictPairs.contains("1-2")); // bookings 1 and 2 conflict
        assertTrueLocal(conflictPairs.contains("2-3")); // bookings 2 and 3 conflict
    }
    
    // @Test
    // @DisplayName("Test edge case - same start and end times")
    void testSameStartEndTimes() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 3),
            new BookingRecord(2, 3, 5)  // Starts when first ends
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(1, courts.size());
        assertEqualsLocal(2, courts.get(0).getBookings().size());
    }
    
    // @Test
    // @DisplayName("Test empty booking list")
    void testEmptyBookingList() {
        List<BookingRecord> bookings = new ArrayList<>();
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(0, courts.size());
        
        int minCourts = solution.getMinimumCourtsNeeded(bookings);
        assertEqualsLocal(0, minCourts);
    }
    
    // @Test
    // @DisplayName("Test single booking")
    void testSingleBooking() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4)
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(1, courts.size());
        assertEqualsLocal(1, courts.get(0).getBookings().size());
        
        int minCourts = solution.getMinimumCourtsNeeded(bookings);
        assertEqualsLocal(1, minCourts);
    }
    
    // @Test
    // @DisplayName("Test unsorted input bookings")
    void testUnsortedBookings() {
        // Bookings in random order
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(3, 5, 7),
            new BookingRecord(1, 1, 4),
            new BookingRecord(4, 3, 5),
            new BookingRecord(2, 2, 6)
        );
        
        List<Court> courts = solution.assignCourts(bookings);
        assertEqualsLocal(3, courts.size());
        
        // Verify all bookings are assigned
        int totalBookings = courts.stream().mapToInt(c -> c.getBookings().size()).sum();
        assertEqualsLocal(4, totalBookings);
    }
    
    // @Test
    // @DisplayName("Test maintenance time edge cases")
    void testMaintenanceEdgeCases() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 2),
            new BookingRecord(2, 3, 4)
        );
        
        // Zero maintenance time - should behave like normal assignment
        List<Court> courtsZeroMaintenance = solution.assignCourtsWithMaintenance(bookings, 0);
        assertEqualsLocal(1, courtsZeroMaintenance.size());
        
        // Large maintenance time - should force separate courts
        List<Court> courtsLargeMaintenance = solution.assignCourtsWithMaintenance(bookings, 10);
        assertEqualsLocal(2, courtsLargeMaintenance.size());
    }
    
    // @Test
    // @DisplayName("Test durability maintenance edge cases")
    void testDurabilityMaintenanceEdgeCases() {
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 2),
            new BookingRecord(2, 3, 4),
            new BookingRecord(3, 5, 6)
        );
        
        // Durability of 1 - maintenance after every booking
        List<Court> courts1 = solution.assignCourtsWithDurabilityMaintenance(bookings, 1, 1);
        assertNotNullLocal(courts1);
        
        // Very high durability - no maintenance needed
        List<Court> courts2 = solution.assignCourtsWithDurabilityMaintenance(bookings, 1, 100);
        assertEqualsLocal(1, courts2.size());
    }
    
    // @Test
    // @DisplayName("Test performance with many bookings")
    void testPerformanceWithManyBookings() {
        List<BookingRecord> bookings = new ArrayList<>();
        
        // Create 1000 non-overlapping bookings
        for (int i = 0; i < 1000; i++) {
            bookings.add(new BookingRecord(i, i * 2, i * 2 + 1));
        }
        
        long startTime = System.currentTimeMillis();
        List<Court> courts = solution.assignCourts(bookings);
        long endTime = System.currentTimeMillis();
        
        assertEqualsLocal(1, courts.size()); // All should fit on one court
        assertEqualsLocal(1000, courts.get(0).getBookings().size());
        assertTrueLocal(endTime - startTime < 1000); // Should complete within 1 second
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
}