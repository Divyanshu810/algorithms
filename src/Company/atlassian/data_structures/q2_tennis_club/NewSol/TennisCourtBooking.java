package Company.atlassian.data_structures.q2_tennis_club.NewSol;

import java.util.*;

/**
 * Tennis Court Booking System
 *
 * ===================================================================================
 * PROBLEM OVERVIEW:
 * ===================================================================================
 * Given a list of tennis court bookings with start and finish times, assign each
 * booking to a specific court using minimum number of courts.
 *
 * ===================================================================================
 * PARTS:
 * ===================================================================================
 * Part A: Basic assignment - minimum courts, no maintenance
 * Part B: Add fixed maintenance time X after each booking
 * Part C: Add maintenance time Y after every X bookings (durability)
 * Part D: Just find minimum courts needed (no assignment)
 * Part E: Check if two bookings conflict
 * ===================================================================================
 */
public class TennisCourtBooking {

    // ==================== DATA CLASSES ====================

    static class BookingRecord {
        int id;
        int startTime;
        int finishTime;

        BookingRecord(int id, int startTime, int finishTime) {
            this.id = id;
            this.startTime = startTime;
            this.finishTime = finishTime;
        }
    }

    static class Court {
        int courtId;
        List<BookingRecord> bookings;

        Court(int courtId) {
            this.courtId = courtId;
            this.bookings = new ArrayList<>();
        }

        void addBooking(BookingRecord booking) {
            bookings.add(booking);
        }
    }

    // Helper class for heap: tracks (availableTime, courtIndex)
    static class CourtState {
        int availableTime;
        int courtIndex;

        CourtState(int availableTime, int courtIndex) {
            this.availableTime = availableTime;
            this.courtIndex = courtIndex;
        }
    }

    // Helper class for sweep line events
    static class Event {
        int time;
        int type;  // +1 for start, -1 for end

        Event(int time, int type) {
            this.time = time;
            this.type = type;
        }
    }

    // ==================== PART A: Basic Assignment ====================
    /**
     * Assign bookings to courts using minimum number of courts.
     *
     * Approach (Greedy with Min-Heap):
     * 1. Sort bookings by start time
     * 2. Use min-heap to track (endTime, courtIndex) - earliest available court
     * 3. For each booking:
     *    - If earliest court is free (endTime <= booking.start), reuse it
     *    - Otherwise, create a new court
     *
     * Why Min-Heap?
     * - We always want the court that becomes free earliest
     * - If that court isn't free, no court is free → need new court
     *
     * Time: O(n log n) for sorting and heap operations
     * Space: O(n) for courts and heap
     */
    static class PartA_BasicAssignment {

        public List<Court> assignCourts(List<BookingRecord> bookingRecords) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return new ArrayList<>();
            }

            // Sort bookings by start time
            List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
            sortedBookings.sort((a, b) -> a.startTime - b.startTime);

            List<Court> courts = new ArrayList<>();

            // Min-heap: court that becomes free earliest
            PriorityQueue<CourtState> minHeap = new PriorityQueue<>(
                    (a, b) -> a.availableTime - b.availableTime
            );

            for (BookingRecord booking : sortedBookings) {

                if (!minHeap.isEmpty() && minHeap.peek().availableTime <= booking.startTime) {
                    // Reuse the earliest available court
                    CourtState earliest = minHeap.poll();

                    courts.get(earliest.courtIndex).addBooking(booking);
                    minHeap.offer(new CourtState(booking.finishTime, earliest.courtIndex));
                } else {
                    // Need a new court
                    int courtIndex = courts.size();
                    Court newCourt = new Court(courtIndex + 1);
                    newCourt.addBooking(booking);
                    courts.add(newCourt);

                    minHeap.offer(new CourtState(booking.finishTime, courtIndex));
                }
            }

            return courts;
        }
    }

    // ==================== PART B: With Fixed Maintenance Time ====================
    /**
     * After each booking, court needs X time units for maintenance.
     *
     * Modification:
     * - When court finishes a booking, next available time = finishTime + maintenanceTime
     *
     * Example: Booking ends at 10, maintenance = 2 → court available at 12
     */
    static class PartB_WithMaintenance {

        public List<Court> assignCourtsWithMaintenance(List<BookingRecord> bookingRecords,
                                                       int maintenanceTime) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return new ArrayList<>();
            }

            // Sort bookings by start time
            List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
            sortedBookings.sort((a, b) -> a.startTime - b.startTime);

            List<Court> courts = new ArrayList<>();

            // Min-heap: (availableTime, courtIndex)
            PriorityQueue<CourtState> minHeap = new PriorityQueue<>(
                    (a, b) -> a.availableTime - b.availableTime
            );

            for (BookingRecord booking : sortedBookings) {

                if (!minHeap.isEmpty() && minHeap.peek().availableTime <= booking.startTime) {
                    // Reuse the earliest available court
                    CourtState earliest = minHeap.poll();

                    courts.get(earliest.courtIndex).addBooking(booking);

                    // Next available = finish time + maintenance
                    int nextAvailable = booking.finishTime + maintenanceTime;
                    minHeap.offer(new CourtState(nextAvailable, earliest.courtIndex));
                } else {
                    // Need a new court
                    int courtIndex = courts.size();
                    Court newCourt = new Court(courtIndex + 1);
                    newCourt.addBooking(booking);
                    courts.add(newCourt);

                    int nextAvailable = booking.finishTime + maintenanceTime;
                    minHeap.offer(new CourtState(nextAvailable, courtIndex));
                }
            }

            return courts;
        }
    }

    // ==================== PART C: Maintenance After X Bookings (Durability) ====================
    /**
     * Court needs maintenance time Y after every X bookings (durability).
     *
     * Track per court:
     * - availableTime: when court is next available
     * - bookingCount: number of bookings since last maintenance
     *
     * When bookingCount reaches durability X:
     * - Add maintenance time Y to available time
     * - Reset booking count to 0
     */
    static class PartC_WithDurability {

        public List<Court> assignCourtsWithDurability(List<BookingRecord> bookingRecords,
                                                      int maintenanceTime,
                                                      int durability) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return new ArrayList<>();
            }

            // Sort bookings by start time
            List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
            sortedBookings.sort((a, b) -> a.startTime - b.startTime);

            List<Court> courts = new ArrayList<>();

            // Track booking count per court
            Map<Integer, Integer> courtBookingCount = new HashMap<>();

            // Min-heap: (availableTime, courtIndex)
            PriorityQueue<CourtState> minHeap = new PriorityQueue<>(
                    (a, b) -> a.availableTime - b.availableTime
            );

            for (BookingRecord booking : sortedBookings) {

                if (!minHeap.isEmpty() && minHeap.peek().availableTime <= booking.startTime) {
                    // Reuse the earliest available court
                    CourtState earliest = minHeap.poll();
                    int courtIndex = earliest.courtIndex;

                    courts.get(courtIndex).addBooking(booking);

                    // Update booking count
                    int count = courtBookingCount.get(courtIndex) + 1;
                    int nextAvailable = booking.finishTime;

                    // Check if maintenance is needed
                    if (count >= durability) {
                        nextAvailable += maintenanceTime;
                        count = 0;  // Reset count after maintenance
                    }

                    courtBookingCount.put(courtIndex, count);
                    minHeap.offer(new CourtState(nextAvailable, courtIndex));
                } else {
                    // Need a new court
                    int courtIndex = courts.size();
                    Court newCourt = new Court(courtIndex + 1);
                    newCourt.addBooking(booking);
                    courts.add(newCourt);

                    // Initialize booking count for new court
                    int count = 1;
                    int nextAvailable = booking.finishTime;

                    // Check if maintenance needed (if durability = 1)
                    if (count >= durability) {
                        nextAvailable += maintenanceTime;
                        count = 0;
                    }

                    courtBookingCount.put(courtIndex, count);
                    minHeap.offer(new CourtState(nextAvailable, courtIndex));
                }
            }

            return courts;
        }
    }

    // ==================== PART D: Minimum Courts Needed (Count Only) ====================
    /**
     * Find minimum number of courts needed without assigning bookings.
     *
     * Approach 1: Sweep Line with Sorting
     * - Create events, sort by time
     * - Time: O(n log n), Space: O(n)
     *
     * Approach 2: Sweep Line with Array (No Sorting)
     * - Use difference array technique
     * - Time: O(n + T), Space: O(T) where T = time range
     *
     * Approach 3: Min-Heap
     * - Same as Part A but just count courts
     * - Time: O(n log n), Space: O(n)
     */
    static class PartD_MinimumCourtsCount {

        // Approach 1: Sweep Line with Sorting - O(n log n)
        public int findMinCourts_SweepLine(List<BookingRecord> bookingRecords) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return 0;
            }

            // Create events: +1 for start, -1 for end
            List<Event> events = new ArrayList<>();

            for (BookingRecord booking : bookingRecords) {
                events.add(new Event(booking.startTime, 1));    // Start: +1
                events.add(new Event(booking.finishTime, -1));  // End: -1
            }

            // Sort by time, then by type (ends before starts at same time)
            events.sort((a, b) -> {
                if (a.time != b.time) {
                    return a.time - b.time;
                }
                return a.type - b.type;  // -1 before +1
            });

            int currentCourts = 0;
            int maxCourts = 0;

            for (Event event : events) {
                currentCourts += event.type;
                maxCourts = Math.max(maxCourts, currentCourts);
            }

            return maxCourts;
        }

        // Approach 2: Sweep Line with Array (No Sorting) - O(n + T)
        /**
         * Uses difference array technique:
         * 1. Find min and max time
         * 2. Create array where arr[t] = change in court count at time t
         * 3. Sweep through array to find max concurrent bookings
         *
         * Best when: time range is small (e.g., 0-1000)
         * Avoid when: time range is huge (e.g., 0 to 10^9)
         */
        public int findMinCourts_Array(List<BookingRecord> bookingRecords) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return 0;
            }

            // Step 1: Find min and max time
            int minTime = Integer.MAX_VALUE;
            int maxTime = Integer.MIN_VALUE;

            for (BookingRecord booking : bookingRecords) {
                minTime = Math.min(minTime, booking.startTime);
                maxTime = Math.max(maxTime, booking.finishTime);
            }

            // Step 2: Create difference array
            // arr[t - minTime] = change in court count at time t
            int[] diff = new int[maxTime - minTime + 1];

            for (BookingRecord booking : bookingRecords) {
                diff[booking.startTime - minTime] += 1;   // Court needed at start
                diff[booking.finishTime - minTime] -= 1;  // Court freed at end
            }

            // Step 3: Sweep through to find max
            int currentCourts = 0;
            int maxCourts = 0;

            for (int change : diff) {
                currentCourts += change;
                maxCourts = Math.max(maxCourts, currentCourts);
            }

            return maxCourts;
        }

        // Approach 3: Min-Heap - O(n log n)
        public int findMinCourts_Heap(List<BookingRecord> bookingRecords) {
            if (bookingRecords == null || bookingRecords.isEmpty()) {
                return 0;
            }

            // Sort bookings by start time
            List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
            sortedBookings.sort((a, b) -> a.startTime - b.startTime);

            // Min-heap of end times
            PriorityQueue<Integer> endTimes = new PriorityQueue<>();

            for (BookingRecord booking : sortedBookings) {
                // If earliest ending court is free, reuse it
                if (!endTimes.isEmpty() && endTimes.peek() <= booking.startTime) {
                    endTimes.poll();
                }
                endTimes.offer(booking.finishTime);
            }

            return endTimes.size();
        }
    }

    // ==================== PART E: Check Booking Conflict ====================
    /**
     * Check if two bookings conflict (overlap).
     *
     * Two intervals [s1, e1) and [s2, e2) overlap if:
     *   s1 < e2 AND s2 < e1
     *
     * They DON'T overlap if:
     *   e1 <= s2 OR e2 <= s1  (one ends before other starts)
     *
     * Time: O(1)
     */
    static class PartE_ConflictCheck {

        public boolean hasConflict(BookingRecord booking1, BookingRecord booking2) {
            // Conflict if they overlap
            // Overlap: start1 < end2 AND start2 < end1
            return booking1.startTime < booking2.finishTime
                    && booking2.startTime < booking1.finishTime;
        }

        // Alternative: No conflict check (more intuitive for some)
        public boolean noConflict(BookingRecord booking1, BookingRecord booking2) {
            // No conflict if one ends before other starts
            return booking1.finishTime <= booking2.startTime
                    || booking2.finishTime <= booking1.startTime;
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Tennis Court Booking System ===\n");

        testPartA();
        testPartB();
        testPartC();
        testPartD();
        testPartE();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testPartA() {
        System.out.println("--- Part A: Basic Assignment ---");

        /**
         * Bookings:
         * B1: [0, 5)
         * B2: [2, 7)
         * B3: [6, 10)
         * B4: [8, 12)
         *
         * Timeline:
         * Court 1: |--B1--|     |--B3--|
         * Court 2:    |--B2--|     |--B4--|
         *
         * Expected: 2 courts
         */
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 5));
        bookings.add(new BookingRecord(2, 2, 7));
        bookings.add(new BookingRecord(3, 6, 10));
        bookings.add(new BookingRecord(4, 8, 12));

        PartA_BasicAssignment solver = new PartA_BasicAssignment();
        List<Court> courts = solver.assignCourts(bookings);

        System.out.println("Courts needed: " + courts.size());
        assertResult(2, courts.size(), "Part A court count");
        System.out.println("Part A: PASSED\n");
    }

    private static void testPartB() {
        System.out.println("--- Part B: With Maintenance Time ---");

        /**
         * Bookings:
         * B1: [0, 5)
         * B2: [6, 10)
         * Maintenance time: 2
         *
         * Without maintenance: 1 court (B2 starts at 6, B1 ends at 5)
         * With maintenance: Court 1 available at 5+2=7, B2 starts at 6 → need 2 courts
         */
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 5));
        bookings.add(new BookingRecord(2, 6, 10));

        PartB_WithMaintenance solver = new PartB_WithMaintenance();

        // Without enough gap for maintenance
        List<Court> courts = solver.assignCourtsWithMaintenance(bookings, 2);
        System.out.println("Maintenance time: 2, Courts needed: " + courts.size());
        assertResult(2, courts.size(), "Part B with tight schedule");

        // With enough gap for maintenance
        List<BookingRecord> bookings2 = new ArrayList<>();
        bookings2.add(new BookingRecord(1, 0, 5));
        bookings2.add(new BookingRecord(2, 8, 12));  // Starts at 8, court free at 7

        List<Court> courts2 = solver.assignCourtsWithMaintenance(bookings2, 2);
        System.out.println("With enough gap, Courts needed: " + courts2.size());
        assertResult(1, courts2.size(), "Part B with enough gap");

        System.out.println("Part B: PASSED\n");
    }

    private static void testPartC() {
        System.out.println("--- Part C: With Durability ---");

        /**
         * Bookings: B1[0,2), B2[3,5), B3[6,8), B4[9,11)
         * Durability: 2 (maintenance after every 2 bookings)
         * Maintenance time: 3
         *
         * Court 1: B1[0,2) → B2[3,5) → maintenance until 8 → B4[9,11)
         * Court 2: B3[6,8)
         *
         * After B2 (2nd booking), court 1 needs maintenance: available at 5+3=8
         * B3 starts at 6, court 1 not available → need court 2
         */
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 2));
        bookings.add(new BookingRecord(2, 3, 5));
        bookings.add(new BookingRecord(3, 6, 8));
        bookings.add(new BookingRecord(4, 9, 11));

        PartC_WithDurability solver = new PartC_WithDurability();
        List<Court> courts = solver.assignCourtsWithDurability(bookings, 3, 2);

        System.out.println("Durability: 2, Maintenance time: 3");
        System.out.println("Courts needed: " + courts.size());

        assertResult(2, courts.size(), "Part C durability");
        System.out.println("Part C: PASSED\n");
    }

    private static void testPartD() {
        System.out.println("--- Part D: Minimum Courts Count ---");

        /**
         * Bookings:
         * B1: [0, 30)
         * B2: [5, 10)
         * B3: [15, 20)
         *
         * Max overlap at time 5-10: B1 and B2 → 2 courts
         */
        List<BookingRecord> bookings = new ArrayList<>();
        bookings.add(new BookingRecord(1, 0, 30));
        bookings.add(new BookingRecord(2, 5, 10));
        bookings.add(new BookingRecord(3, 15, 20));

        PartD_MinimumCourtsCount solver = new PartD_MinimumCourtsCount();

        int countSweep = solver.findMinCourts_SweepLine(bookings);
        int countArray = solver.findMinCourts_Array(bookings);
        int countHeap = solver.findMinCourts_Heap(bookings);

        System.out.println("Min courts (Sweep Line): " + countSweep);
        System.out.println("Min courts (Array):      " + countArray);
        System.out.println("Min courts (Heap):       " + countHeap);

        assertResult(2, countSweep, "Part D sweep line");
        assertResult(2, countArray, "Part D array");
        assertResult(2, countHeap, "Part D heap");

        // Test with 3 overlapping
        List<BookingRecord> bookings2 = new ArrayList<>();
        bookings2.add(new BookingRecord(1, 0, 10));
        bookings2.add(new BookingRecord(2, 2, 8));
        bookings2.add(new BookingRecord(3, 3, 7));

        int count2 = solver.findMinCourts_Array(bookings2);
        System.out.println("3 overlapping bookings, Min courts: " + count2);
        assertResult(3, count2, "Part D 3 overlapping");

        System.out.println("Part D: PASSED\n");
    }

    private static void testPartE() {
        System.out.println("--- Part E: Conflict Check ---");

        PartE_ConflictCheck checker = new PartE_ConflictCheck();

        // Test 1: Overlapping bookings [0,10) vs [5,15)
        BookingRecord b1 = new BookingRecord(1, 0, 10);
        BookingRecord b2 = new BookingRecord(2, 5, 15);
        boolean conflict1 = checker.hasConflict(b1, b2);
        System.out.println("[0,10) vs [5,15) → Conflict: " + conflict1);
        assertResult(true, conflict1, "Overlapping bookings");

        // Test 2: Non-overlapping (adjacent) [0,10) vs [10,20)
        BookingRecord b3 = new BookingRecord(3, 0, 10);
        BookingRecord b4 = new BookingRecord(4, 10, 20);
        boolean conflict2 = checker.hasConflict(b3, b4);
        System.out.println("[0,10) vs [10,20) → Conflict: " + conflict2);
        assertResult(false, conflict2, "Adjacent bookings");

        // Test 3: Non-overlapping (gap) [0,5) vs [10,15)
        BookingRecord b5 = new BookingRecord(5, 0, 5);
        BookingRecord b6 = new BookingRecord(6, 10, 15);
        boolean conflict3 = checker.hasConflict(b5, b6);
        System.out.println("[0,5) vs [10,15) → Conflict: " + conflict3);
        assertResult(false, conflict3, "Non-overlapping bookings");

        // Test 4: One contains other [0,20) vs [5,10)
        BookingRecord b7 = new BookingRecord(7, 0, 20);
        BookingRecord b8 = new BookingRecord(8, 5, 10);
        boolean conflict4 = checker.hasConflict(b7, b8);
        System.out.println("[0,20) vs [5,10) → Conflict: " + conflict4);
        assertResult(true, conflict4, "Contained booking");

        System.out.println("Part E: PASSED\n");
    }

    // Simple assertion helpers
    private static void assertResult(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
    }

    private static void assertResult(boolean expected, boolean actual, String testName) {
        if (expected != actual) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
    }
}