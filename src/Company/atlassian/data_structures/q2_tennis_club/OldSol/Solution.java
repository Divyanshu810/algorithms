package Company.atlassian.data_structures.q2_tennis_club.OldSol;
import java.util.*;

class BookingRecord {
    private int id;
    private int startTime;
    private int finishTime;
    
    public BookingRecord(int id, int startTime, int finishTime) {
        this.id = id;
        this.startTime = startTime;
        this.finishTime = finishTime;
    }
    
    public int getId() { return id; }
    public int getStartTime() { return startTime; }
    public int getFinishTime() { return finishTime; }
    
    @Override
    public String toString() {
        return "Booking(" + id + ", " + startTime + "-" + finishTime + ")";
    }
}

class Court {
    private int courtId;
    private List<BookingRecord> bookings;
    private int nextAvailableTime;
    private int usageCount;
    private int lastMaintenanceTime;
    
    public Court(int courtId) {
        this.courtId = courtId;
        this.bookings = new ArrayList<>();
        this.nextAvailableTime = 0;
        this.usageCount = 0;
        this.lastMaintenanceTime = 0;
    }
    
    public int getCourtId() { return courtId; }
    public List<BookingRecord> getBookings() { return bookings; }
    public int getNextAvailableTime() { return nextAvailableTime; }
    public int getUsageCount() { return usageCount; }
    
    public void addBooking(BookingRecord booking) {
        bookings.add(booking);
        nextAvailableTime = booking.getFinishTime();
        usageCount++;
    }
    
    public void addBookingWithMaintenance(BookingRecord booking, int maintenanceTime) {
        bookings.add(booking);
        nextAvailableTime = booking.getFinishTime() + maintenanceTime;
        usageCount++;
    }
    
    public void addBookingWithDurabilityMaintenance(BookingRecord booking, int maintenanceTime, int durability) {
        bookings.add(booking);
        usageCount++;
        
        if (usageCount % durability == 0) {
            nextAvailableTime = booking.getFinishTime() + maintenanceTime;
            lastMaintenanceTime = booking.getFinishTime();
        } else {
            nextAvailableTime = booking.getFinishTime();
        }
    }
    
    public void setNextAvailableTime(int time) {
        this.nextAvailableTime = time;
    }
    
    @Override
    public String toString() {
        return "Court(" + courtId + ", available at: " + nextAvailableTime + ", bookings: " + bookings.size() + ")";
    }
}

public class Solution {
    
    // Basic court assignment - minimum courts needed
    public List<Court> assignCourts(List<BookingRecord> bookingRecords) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Sort bookings by start time
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        sortedBookings.sort((a,b)->a.getStartTime()- b.getStartTime());
        
        // Priority queue to track court availability (min-heap by next available time)
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getNextAvailableTime)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            // Check if any existing court is available
            if (!availableCourts.isEmpty() && 
                availableCourts.peek().getNextAvailableTime() <= booking.getStartTime()) {
                assignedCourt = availableCourts.poll();
            } else {
                // Create new court
                assignedCourt = new Court(courtIdCounter++);
                allCourts.add(assignedCourt);
            }
            
            // Assign booking to court
            assignedCourt.addBooking(booking);
            availableCourts.add(assignedCourt);
        }
        
        return allCourts;
    }
    
    // Court assignment with maintenance time after each booking
    public List<Court> assignCourtsWithMaintenance(List<BookingRecord> bookingRecords, int maintenanceTime) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        sortedBookings.sort(Comparator.comparingInt(BookingRecord::getStartTime));
        
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getNextAvailableTime)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            if (!availableCourts.isEmpty() && 
                availableCourts.peek().getNextAvailableTime() <= booking.getStartTime()) {
                assignedCourt = availableCourts.poll();
            } else {
                assignedCourt = new Court(courtIdCounter++);
                allCourts.add(assignedCourt);
            }
            
            assignedCourt.addBookingWithMaintenance(booking, maintenanceTime);
            availableCourts.add(assignedCourt);
        }
        
        return allCourts;
    }
    
    // Court assignment with durability-based maintenance
    public List<Court> assignCourtsWithDurabilityMaintenance(List<BookingRecord> bookingRecords, 
                                                            int maintenanceTime, int durability) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BookingRecord> sortedBookings = new ArrayList<>(bookingRecords);
        sortedBookings.sort(Comparator.comparingInt(BookingRecord::getStartTime));
        
        PriorityQueue<Court> availableCourts = new PriorityQueue<>(
            Comparator.comparingInt(Court::getNextAvailableTime)
        );
        
        List<Court> allCourts = new ArrayList<>();
        int courtIdCounter = 1;
        
        for (BookingRecord booking : sortedBookings) {
            Court assignedCourt = null;
            
            if (!availableCourts.isEmpty() && 
                availableCourts.peek().getNextAvailableTime() <= booking.getStartTime()) {
                assignedCourt = availableCourts.poll();
            } else {
                assignedCourt = new Court(courtIdCounter++);
                allCourts.add(assignedCourt);
            }
            
            assignedCourt.addBookingWithDurabilityMaintenance(booking, maintenanceTime, durability);
            availableCourts.add(assignedCourt);
        }
        
        return allCourts;
    }
    
    // Simplified version - just return minimum number of courts needed
    public int getMinimumCourtsNeeded(List<BookingRecord> bookingRecords) {
        if (bookingRecords == null || bookingRecords.isEmpty()) {
            return 0;
        }
        
        // Create timeline events
        List<int[]> events = new ArrayList<>();
        for (BookingRecord booking : bookingRecords) {
            events.add(new int[]{booking.getStartTime(), 1});  // Start event
            events.add(new int[]{booking.getFinishTime(), -1}); // End event
        }
        
        // Sort events by time, with end events before start events at same time
        events.sort((a, b) -> {
            if (a[0] != b[0]) return Integer.compare(a[0], b[0]);
            return Integer.compare(a[1], b[1]); // -1 comes before 1
        });
        
        int currentCourts = 0;
        int maxCourts = 0;
        
        for (int[] event : events) {
            currentCourts += event[1];
            maxCourts = Math.max(maxCourts, currentCourts);
        }
        
        return maxCourts;
    }
    
    // Check if two bookings conflict
    public boolean doBookingsConflict(BookingRecord booking1, BookingRecord booking2) {
        return booking1.getFinishTime() > booking2.getStartTime() && 
               booking1.getStartTime() < booking2.getFinishTime();
    }
    
    // Get all conflicting pairs
    public List<int[]> findConflictingBookings(List<BookingRecord> bookingRecords) {
        List<int[]> conflicts = new ArrayList<>();
        
        for (int i = 0; i < bookingRecords.size(); i++) {
            for (int j = i + 1; j < bookingRecords.size(); j++) {
                if (doBookingsConflict(bookingRecords.get(i), bookingRecords.get(j))) {
                    conflicts.add(new int[]{bookingRecords.get(i).getId(), bookingRecords.get(j).getId()});
                }
            }
        }
        
        return conflicts;
    }
    
    // Utility method to print court assignments
    public void printCourtAssignments(List<Court> courts) {
        System.out.println("Court Assignments:");
        for (Court court : courts) {
            System.out.println("Court " + court.getCourtId() + ":");
            for (BookingRecord booking : court.getBookings()) {
                System.out.println("  " + booking);
            }
        }
        System.out.println("Total courts needed: " + courts.size());
    }
    
    public static void main(String[] args) {
        Solution solution = new Solution();
        
        // Test case 1: Basic court assignment
        List<BookingRecord> bookings = Arrays.asList(
            new BookingRecord(1, 1, 4),   // 1-4
            new BookingRecord(2, 2, 6),   // 2-6
            new BookingRecord(3, 5, 7),   // 5-7
            new BookingRecord(4, 3, 5),   // 3-5
            new BookingRecord(5, 8, 9)    // 8-9
        );
        
        System.out.println("=== Basic Court Assignment ===");
        List<Court> courts = solution.assignCourts(bookings);
        solution.printCourtAssignments(courts);
        
        System.out.println(" === Minimum Courts Calculation ===");
        int minCourts = solution.getMinimumCourtsNeeded(bookings);
        System.out.println("Minimum courts needed: " + minCourts);
        
        // Test case 2: With maintenance time
        System.out.println(" === With Maintenance Time (1 hour) ===");
        List<Court> courtsWithMaintenance = solution.assignCourtsWithMaintenance(bookings, 1);
        solution.printCourtAssignments(courtsWithMaintenance);
        
        // Test case 3: With durability maintenance
        System.out.println(" === With Durability Maintenance (every 2 bookings, 2 hour maintenance) ===");
        List<Court> courtsWithDurability = solution.assignCourtsWithDurabilityMaintenance(bookings, 2, 2);
        solution.printCourtAssignments(courtsWithDurability);
        
        // Test case 4: Conflict detection
        System.out.println(" === Booking Conflicts ===");
        List<int[]> conflicts = solution.findConflictingBookings(bookings);
        if (conflicts.isEmpty()) {
            System.out.println("No conflicts found");
        } else {
            for (int[] conflict : conflicts) {
                System.out.println("Conflict between booking " + conflict[0] + " and booking " + conflict[1]);
            }
        }
    }
}