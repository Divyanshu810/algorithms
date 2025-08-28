package practice.airbnb;

import java.util.*;

/**
 * You are given a list of rooms in a hotel, where each room has: check-in time, check-out time. You need to determine:
 * the minimum number of rooms required to accommodate all bookings without conflicts,
 * whether a given booking can be accommodated given existing bookings,
 * find time intervals when the hotel is fully booked or has vacancies,
 * detect any overlaps/conflicts among bookings.
 * Extension: output the schedule of which bookings goes into which room.
 */
public class NoOfRooms {
    
    public static class Booking {
        int id;
        int checkIn;
        int checkOut;
        String guestName;
        
        public Booking(int id, int checkIn, int checkOut, String guestName) {
            this.id = id;
            this.checkIn = checkIn;
            this.checkOut = checkOut;
            this.guestName = guestName;
        }
        
        public boolean overlapsWith(Booking other) {
            return this.checkIn < other.checkOut && other.checkIn < this.checkOut;
        }
        
        @Override
        public String toString() {
            return String.format("Booking[id=%d, guest=%s, %d-%d]", id, guestName, checkIn, checkOut);
        }
    }
    
    public static class Event {
        int time;
        boolean isCheckIn;
        Booking booking;
        
        public Event(int time, boolean isCheckIn, Booking booking) {
            this.time = time;
            this.isCheckIn = isCheckIn;
            this.booking = booking;
        }
    }
    
    public static class RoomAllocation {
        int roomNumber;
        List<Booking> bookings;
        
        public RoomAllocation(int roomNumber) {
            this.roomNumber = roomNumber;
            this.bookings = new ArrayList<>();
        }
        
        public boolean canAccommodate(Booking booking) {
            for (Booking existing : bookings) {
                if (existing.overlapsWith(booking)) {
                    return false;
                }
            }
            return true;
        }
        
        public void addBooking(Booking booking) {
            bookings.add(booking);
        }
        
        @Override
        public String toString() {
            return String.format("Room %d: %s", roomNumber, bookings);
        }
    }
    
    /**
     * Method 1: Sweep Line Algorithm - Minimum rooms required
     * Time Complexity: O(n log n), Space Complexity: O(n)
     */
    public static int minRoomsRequired(List<Booking> bookings) {
        List<Event> events = new ArrayList<>();
        
        // Create events for check-in and check-out
        for (Booking booking : bookings) {
            events.add(new Event(booking.checkIn, true, booking));
            events.add(new Event(booking.checkOut, false, booking));
        }
        
        // Sort events by time. If times are equal, check-out comes before check-in
        events.sort((a, b) -> {
            if (a.time != b.time) return Integer.compare(a.time, b.time);
            return Boolean.compare(a.isCheckIn, b.isCheckIn); // false (checkout) comes before true (checkin)
        });
        
        int currentRooms = 0;
        int maxRooms = 0;
        
        for (Event event : events) {
            if (event.isCheckIn) {
                currentRooms++;
                maxRooms = Math.max(maxRooms, currentRooms);
            } else {
                currentRooms--;
            }
        }
        
        return maxRooms;
    }
    
    /**
     * Method 2: Priority Queue approach - Alternative implementation
     * Time Complexity: O(n log n), Space Complexity: O(n)
     */
    public static int minRoomsRequiredPQ(List<Booking> bookings) {
        if (bookings.isEmpty()) return 0;
        
        // Sort bookings by check-in time
        bookings.sort(Comparator.comparingInt(b -> b.checkIn));
        
        // Priority queue to track check-out times of occupied rooms
        PriorityQueue<Integer> checkOutTimes = new PriorityQueue<>();
        
        for (Booking booking : bookings) {
            // Remove rooms that have checked out before current check-in
            while (!checkOutTimes.isEmpty() && checkOutTimes.peek() <= booking.checkIn) {
                checkOutTimes.poll();
            }
            
            // Add current booking's check-out time
            checkOutTimes.offer(booking.checkOut);
        }
        
        return checkOutTimes.size();
    }
    
    /**
     * Check if a new booking can be accommodated
     */
    public static boolean canAccommodateBooking(List<Booking> existingBookings, Booking newBooking) {
        for (Booking existing : existingBookings) {
            if (existing.overlapsWith(newBooking)) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * Detect all conflicts/overlaps among bookings
     */
    public static List<List<Booking>> findConflicts(List<Booking> bookings) {
        List<List<Booking>> conflicts = new ArrayList<>();
        
        for (int i = 0; i < bookings.size(); i++) {
            for (int j = i + 1; j < bookings.size(); j++) {
                if (bookings.get(i).overlapsWith(bookings.get(j))) {
                    conflicts.add(Arrays.asList(bookings.get(i), bookings.get(j)));
                }
            }
        }
        
        return conflicts;
    }
    
    /**
     * Find time intervals when hotel has specific occupancy
     */
    public static List<int[]> findOccupancyIntervals(List<Booking> bookings, int targetOccupancy) {
        List<Event> events = new ArrayList<>();
        
        for (Booking booking : bookings) {
            events.add(new Event(booking.checkIn, true, booking));
            events.add(new Event(booking.checkOut, false, booking));
        }
        
        events.sort((a, b) -> {
            if (a.time != b.time) return Integer.compare(a.time, b.time);
            return Boolean.compare(a.isCheckIn, b.isCheckIn);
        });
        
        List<int[]> intervals = new ArrayList<>();
        int currentOccupancy = 0;
        int intervalStart = -1;
        
        for (Event event : events) {
            if (currentOccupancy == targetOccupancy && intervalStart != -1) {
                intervals.add(new int[]{intervalStart, event.time});
                intervalStart = -1;
            }
            
            if (event.isCheckIn) {
                currentOccupancy++;
            } else {
                currentOccupancy--;
            }
            
            if (currentOccupancy == targetOccupancy && intervalStart == -1) {
                intervalStart = event.time;
            }
        }
        
        return intervals;
    }
    
    /**
     * Assign bookings to specific rooms (room scheduling)
     */
    public static List<RoomAllocation> assignRoomsToBookings(List<Booking> bookings) {
        List<RoomAllocation> rooms = new ArrayList<>();
        
        // Sort bookings by check-in time for better allocation
        bookings.sort(Comparator.comparingInt(b -> b.checkIn));
        
        for (Booking booking : bookings) {
            boolean assigned = false;
            
            // Try to assign to existing room
            for (RoomAllocation room : rooms) {
                if (room.canAccommodate(booking)) {
                    room.addBooking(booking);
                    assigned = true;
                    break;
                }
            }
            
            // Create new room if needed
            if (!assigned) {
                RoomAllocation newRoom = new RoomAllocation(rooms.size() + 1);
                newRoom.addBooking(booking);
                rooms.add(newRoom);
            }
        }
        
        return rooms;
    }
    
    /**
     * Advanced: Greedy room assignment with preference for room reuse
     */
    public static List<RoomAllocation> assignRoomsOptimized(List<Booking> bookings) {
        List<RoomAllocation> rooms = new ArrayList<>();
        
        // Sort bookings by check-in time
        bookings.sort(Comparator.comparingInt(b -> b.checkIn));
        
        // Priority queue to track when each room becomes available
        PriorityQueue<RoomAllocation> availableRooms = new PriorityQueue<>((a, b) -> {
            int lastCheckOutA = a.bookings.isEmpty() ? 0 : a.bookings.get(a.bookings.size() - 1).checkOut;
            int lastCheckOutB = b.bookings.isEmpty() ? 0 : b.bookings.get(b.bookings.size() - 1).checkOut;
            return Integer.compare(lastCheckOutA, lastCheckOutB);
        });
        
        for (Booking booking : bookings) {
            // Check if any room is available for reuse
            while (!availableRooms.isEmpty()) {
                RoomAllocation room = availableRooms.peek();
                int lastCheckOut = room.bookings.isEmpty() ? 0 : room.bookings.get(room.bookings.size() - 1).checkOut;
                
                if (lastCheckOut <= booking.checkIn) {
                    // Room is available for reuse
                    availableRooms.poll();
                    room.addBooking(booking);
                    availableRooms.offer(room);
                    break;
                } else {
                    break; // No more available rooms
                }
            }
            
            // If no room was reused, create a new one
            if (availableRooms.isEmpty() || 
                availableRooms.peek().bookings.get(availableRooms.peek().bookings.size() - 1).checkOut > booking.checkIn) {
                RoomAllocation newRoom = new RoomAllocation(rooms.size() + 1);
                newRoom.addBooking(booking);
                rooms.add(newRoom);
                availableRooms.offer(newRoom);
            }
        }
        
        return rooms;
    }
    
    public static void main(String[] args) {
        // Test data
        List<Booking> bookings = Arrays.asList(
            new Booking(1, 9, 10, "Alice"),
            new Booking(2, 10, 12, "Bob"),
            new Booking(3, 11, 13, "Charlie"),
            new Booking(4, 14, 16, "David"),
            new Booking(5, 15, 17, "Eve"),
            new Booking(6, 9, 11, "Frank"),
            new Booking(7, 13, 15, "Grace")
        );
        
        System.out.println("=== Hotel Room Booking Analysis ===");
        System.out.println("Bookings:");
        for (Booking booking : bookings) {
            System.out.println("  " + booking);
        }
        
        // Test minimum rooms required
        System.out.println("\n=== Minimum Rooms Required ===");
        int minRooms1 = minRoomsRequired(new ArrayList<>(bookings));
        int minRooms2 = minRoomsRequiredPQ(new ArrayList<>(bookings));
        System.out.println("Sweep Line Algorithm: " + minRooms1 + " rooms");
        System.out.println("Priority Queue Algorithm: " + minRooms2 + " rooms");
        System.out.println("Results match: " + (minRooms1 == minRooms2));
        
        // Test conflict detection
        System.out.println("\n=== Conflicts Detection ===");
        List<List<Booking>> conflicts = findConflicts(bookings);
        if (conflicts.isEmpty()) {
            System.out.println("No conflicts found");
        } else {
            System.out.println("Found " + conflicts.size() + " conflicts:");
            for (List<Booking> conflict : conflicts) {
                System.out.println("  " + conflict.get(0).guestName + " conflicts with " + conflict.get(1).guestName);
            }
        }
        
        // Test room assignment
        System.out.println("\n=== Room Assignment ===");
        List<RoomAllocation> roomAllocations = assignRoomsToBookings(new ArrayList<>(bookings));
        for (RoomAllocation room : roomAllocations) {
            System.out.println(room);
        }
        
        // Test optimized room assignment
        System.out.println("\n=== Optimized Room Assignment ===");
        List<RoomAllocation> optimizedAllocations = assignRoomsOptimized(new ArrayList<>(bookings));
        for (RoomAllocation room : optimizedAllocations) {
            System.out.println(room);
        }
        
        // Test occupancy intervals
        System.out.println("\n=== Occupancy Analysis ===");
        System.out.println("Intervals with 2+ guests:");
        List<int[]> highOccupancy = findOccupancyIntervals(bookings, 2);
        for (int[] interval : highOccupancy) {
            System.out.println("  Time " + interval[0] + " to " + interval[1]);
        }
        
        System.out.println("Intervals with 0 guests (vacant):");
        List<int[]> vacant = findOccupancyIntervals(bookings, 0);
        for (int[] interval : vacant) {
            System.out.println("  Time " + interval[0] + " to " + interval[1]);
        }
        
        // Test new booking accommodation
        System.out.println("\n=== New Booking Test ===");
        Booking newBooking = new Booking(8, 12, 14, "Helen");
        boolean canAccommodate = canAccommodateBooking(bookings, newBooking);
        System.out.println("Can accommodate " + newBooking + "? " + canAccommodate);
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        long startTime, endTime;
        
        startTime = System.nanoTime();
        int result1 = minRoomsRequired(new ArrayList<>(bookings));
        endTime = System.nanoTime();
        System.out.println("Sweep Line: " + (endTime - startTime) / 1000 + " μs, Result: " + result1);
        
        startTime = System.nanoTime();
        int result2 = minRoomsRequiredPQ(new ArrayList<>(bookings));
        endTime = System.nanoTime();
        System.out.println("Priority Queue: " + (endTime - startTime) / 1000 + " μs, Result: " + result2);
    }
}
