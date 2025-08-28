package practice.atlassian.code_design.q6_cinema_screenings;

import java.util.*;

/**
 * Solution for Cinema Screenings problem.
 * Determines if a new movie can be added to the existing schedule without removing any current movies.
 */
public class Solution {
    
    public static void main(String[] args) {
        // Example usage of the base implementation
        Cinema cinema = new Cinema();
        
        // Add some movies to the schedule
        cinema.addScreening(new Screening("Inception", 600, 780));  // 10:00-13:00
        cinema.addScreening(new Screening("The Dark Knight", 800, 980));  // 13:20-16:20
//        cinema.addScreening(new Screening("Interstellar", 1000, 1200));  // 16:40-20:00
        cinema.addScreening(new Screening("Tenet", 1220, 1380));  // 20:20-23:00
        
        // Print the current schedule
        System.out.println("Current Schedule:");
        cinema.printSchedule();
        
        // Check if a new movie can be added
        Screening newMovie = new Screening("Dune", 120);  // 2-hour movie
        boolean canAdd = cinema.canAddScreening(newMovie);
        System.out.println("\nCan add '" + newMovie.getTitle() + "' (duration: " + 
                newMovie.getDuration() + " mins)? " + canAdd);
        
        // Find available time slots
        List<TimeSlot> availableSlots = cinema.findAvailableTimeSlots(newMovie.getDuration());
        System.out.println("\nAvailable time slots for a " + newMovie.getDuration() + 
                " minute movie:");
        for (TimeSlot slot : availableSlots) {
            System.out.println("  " + formatTime(slot.start) + " - " + formatTime(slot.end) + 
                    " (duration: " + (slot.end - slot.start) + " mins)");
        }
        
        // Scale-up 1: With revenue
        System.out.println("\n--- Scale-up 1: With Revenue ---");
        CinemaWithRevenue cinemaWithRevenue = new CinemaWithRevenue();
        
        // Add screenings with revenue
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Inception", 600, 780, 1000));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("The Dark Knight", 800, 980, 1200));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Interstellar", 1000, 1200, 1500));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Tenet", 1220, 1380, 1100));
        
        System.out.println("Schedule with Revenue:");
        cinemaWithRevenue.printSchedule();
        System.out.println("Total Revenue: $" + cinemaWithRevenue.getTotalRevenue());
        
        // Scale-up 2: Insert into full schedule
        System.out.println("\n--- Scale-up 2: Insert into Full Schedule ---");
        ScreeningWithRevenue newMovieWithRevenue = new ScreeningWithRevenue("Dune", 120, 1300);
        ScreeningWithRevenue replacedScreening = cinemaWithRevenue.addToFullSchedule(newMovieWithRevenue);
        
        if (replacedScreening != null) {
            System.out.println("Replaced '" + replacedScreening.getTitle() + "' with '" + 
                    newMovieWithRevenue.getTitle() + "'");
            System.out.println("Updated Schedule:");
            cinemaWithRevenue.printSchedule();
            System.out.println("New Total Revenue: $" + cinemaWithRevenue.getTotalRevenue());
        }
        
        // Scale-up 3: Multiple rooms
        System.out.println("\n--- Scale-up 3: Multiple Rooms ---");
        MultiRoomCinema multiRoomCinema = new MultiRoomCinema(2);  // 2 rooms
        
        // Add screenings to different rooms
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Inception", 600, 780, 1000), 0);
        multiRoomCinema.addScreening(new ScreeningWithRevenue("The Dark Knight", 800, 980, 1200), 0);
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Interstellar", 600, 800, 1500), 1);
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Tenet", 820, 980, 1100), 1);
        
        System.out.println("Multi-room Schedule:");
        multiRoomCinema.printSchedule();
        System.out.println("Total Revenue: $" + multiRoomCinema.getTotalRevenue());
        
        // Try to add a new movie, optimizing for revenue
        ScreeningWithRevenue anotherMovie = new ScreeningWithRevenue("The Matrix", 120, 1000);
        int roomAssigned = multiRoomCinema.addScreeningOptimized(anotherMovie);
        
        if (roomAssigned >= 0) {
            System.out.println("\nAdded '" + anotherMovie.getTitle() + "' to Room " + (roomAssigned + 1));
            System.out.println("Updated Multi-room Schedule:");
            multiRoomCinema.printSchedule();
            System.out.println("New Total Revenue: $" + multiRoomCinema.getTotalRevenue());
        } else {
            System.out.println("\nCould not add '" + anotherMovie.getTitle() + "' to any room");
        }
        
        // Scale-down: Just print the schedule
        System.out.println("\n--- Scale-down: Simple Schedule Printing ---");
        simplePrintSchedule(cinema.getScreenings());
    }
    
    /**
     * Helper method to format time in minutes to HH:MM format.
     */
    private static String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
    
    /**
     * Scale-down implementation: Simple schedule printing.
     */
    private static void simplePrintSchedule(List<Screening> screenings) {
        System.out.println("Simple Schedule:");
        for (Screening screening : screenings) {
            System.out.println(screening.getTitle() + ": " + 
                    formatTime(screening.getStartTime()) + " - " + 
                    formatTime(screening.getEndTime()));
        }
    }
}

/**
 * Represents a time slot with start and end times.
 */
class TimeSlot {
    int start;
    int end;
    
    public TimeSlot(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

/**
 * Represents a movie screening with title, start time, and end time.
 */
class Screening {
    private String title;
    private int startTime;
    private int endTime;
    private int duration;
    
    public Screening(String title, int startTime, int endTime) {
        this.title = title;
        this.startTime = startTime;
        this.endTime = endTime;
        this.duration = endTime - startTime;
    }
    
    public Screening(String title, int duration) {
        this.title = title;
        this.duration = duration;
        // Start and end times will be set when scheduled
        this.startTime = -1;
        this.endTime = -1;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getStartTime() {
        return startTime;
    }
    
    public int getEndTime() {
        return endTime;
    }
    
    public int getDuration() {
        return duration;
    }
    
    public void schedule(int startTime) {
        this.startTime = startTime;
        this.endTime = startTime + duration;
    }
    
    @Override
    public String toString() {
        return title + " (" + startTime + "-" + endTime + ")";
    }
}

/**
 * Represents a screening with additional revenue information.
 */
class ScreeningWithRevenue extends Screening {
    private double revenue;
    
    public ScreeningWithRevenue(String title, int startTime, int endTime, double revenue) {
        super(title, startTime, endTime);
        this.revenue = revenue;
    }
    
    public ScreeningWithRevenue(String title, int duration, double revenue) {
        super(title, duration);
        this.revenue = revenue;
    }
    
    public double getRevenue() {
        return revenue;
    }
    
    public double getRevenuePerMinute() {
        return revenue / getDuration();
    }
}

/**
 * Basic cinema class that manages a schedule of movie screenings.
 */
class Cinema {
    protected List<Screening> screenings;
    protected static final int OPENING_TIME = 600;  // 10:00
    protected static final int CLOSING_TIME = 1380;  // 23:00
    
    public Cinema() {
        this.screenings = new ArrayList<>();
    }
    
    public List<Screening> getScreenings() {
        return new ArrayList<>(screenings);
    }
    
    public void addScreening(Screening screening) {
        screenings.add(screening);
        // Sort screenings by start time for easier management
        screenings.sort(Comparator.comparingInt(Screening::getStartTime));
    }
    
    public boolean canAddScreening(Screening newScreening) {
        // Find available slots and check if any can fit the new screening
        List<TimeSlot> availableSlots = findAvailableTimeSlots(newScreening.getDuration());
        return !availableSlots.isEmpty();
    }
    
    public List<TimeSlot> findAvailableTimeSlots(int duration) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        
        // If there are no screenings, the entire day is available
        if (screenings.isEmpty()) {
            availableSlots.add(new TimeSlot(OPENING_TIME, CLOSING_TIME));
            return availableSlots;
        }
        
        // Check if there's a gap before the first screening
        int firstStart = screenings.get(0).getStartTime();
        if (firstStart - OPENING_TIME >= duration) {
            availableSlots.add(new TimeSlot(OPENING_TIME, firstStart));
        }
        
        // Check for gaps between screenings
        for (int i = 0; i < screenings.size() - 1; i++) {
            int currentEnd = screenings.get(i).getEndTime();
            int nextStart = screenings.get(i + 1).getStartTime();
            
            if (nextStart - currentEnd >= duration) {
                availableSlots.add(new TimeSlot(currentEnd, nextStart));
            }
        }
        
        // Check if there's a gap after the last screening
        int lastEnd = screenings.get(screenings.size() - 1).getEndTime();
        if (CLOSING_TIME - lastEnd >= duration) {
            availableSlots.add(new TimeSlot(lastEnd, CLOSING_TIME));
        }
        
        return availableSlots;
    }
    
    public void printSchedule() {
        for (Screening screening : screenings) {
            System.out.println(screening.getTitle() + ": " + 
                    formatTime(screening.getStartTime()) + " - " + 
                    formatTime(screening.getEndTime()) + 
                    " (duration: " + screening.getDuration() + " mins)");
        }
    }
    
    String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}

/**
 * Extended cinema class that handles revenue tracking.
 */
class CinemaWithRevenue extends Cinema {
    public double getTotalRevenue() {
        double total = 0;
        for (Screening s : screenings) {
            if (s instanceof ScreeningWithRevenue) {
                total += ((ScreeningWithRevenue) s).getRevenue();
            }
        }
        return total;
    }
    
    /**
     * Adds a new screening to a full schedule by removing the least profitable screening.
     * Returns the screening that was replaced.
     */
    public ScreeningWithRevenue addToFullSchedule(ScreeningWithRevenue newScreening) {
        // First check if there's any available slot
        if (canAddScreening(newScreening)) {
            // If there's space, just add it normally
            scheduleAtBestSlot(newScreening);
            return null;
        }
        
        // Find the least profitable screening
        ScreeningWithRevenue leastProfitable = findLeastProfitableScreening();
        
        // Make sure the new screening is more profitable
        if (leastProfitable != null && 
                newScreening.getRevenue() > leastProfitable.getRevenue()) {
            // Remove the least profitable screening
            screenings.remove(leastProfitable);
            
            // Add the new screening
            scheduleAtBestSlot(newScreening);
            
            return leastProfitable;
        }
        
        return null;  // Could not add the new screening
    }
    
    private ScreeningWithRevenue findLeastProfitableScreening() {
        ScreeningWithRevenue leastProfitable = null;
        double minRevenue = Double.MAX_VALUE;
        
        for (Screening s : screenings) {
            if (s instanceof ScreeningWithRevenue) {
                ScreeningWithRevenue swr = (ScreeningWithRevenue) s;
                if (swr.getRevenue() < minRevenue) {
                    minRevenue = swr.getRevenue();
                    leastProfitable = swr;
                }
            }
        }
        
        return leastProfitable;
    }
    
    private void scheduleAtBestSlot(ScreeningWithRevenue screening) {
        List<TimeSlot> availableSlots = findAvailableTimeSlots(screening.getDuration());
        
        if (!availableSlots.isEmpty()) {
            // For simplicity, schedule at the first available slot
            TimeSlot slot = availableSlots.get(0);
            screening.schedule(slot.start);
            addScreening(screening);
        }
    }
    
    @Override
    public void printSchedule() {
        for (Screening s : screenings) {
            String revenueInfo = "";
            if (s instanceof ScreeningWithRevenue) {
                revenueInfo = " - Revenue: $" + ((ScreeningWithRevenue) s).getRevenue();
            }
            
            System.out.println(s.getTitle() + ": " + 
                    formatTime(s.getStartTime()) + " - " + 
                    formatTime(s.getEndTime()) + 
                    " (duration: " + s.getDuration() + " mins)" + 
                    revenueInfo);
        }
    }
}

/**
 * Cinema with multiple rooms, each with its own schedule.
 */
class MultiRoomCinema {
    private List<List<ScreeningWithRevenue>> roomSchedules;
    private int numRooms;
    private static final int OPENING_TIME = 600;  // 10:00
    private static final int CLOSING_TIME = 1380;  // 23:00
    
    public MultiRoomCinema(int numRooms) {
        this.numRooms = numRooms;
        this.roomSchedules = new ArrayList<>(numRooms);
        
        for (int i = 0; i < numRooms; i++) {
            roomSchedules.add(new ArrayList<>());
        }
    }
    
    public void addScreening(ScreeningWithRevenue screening, int roomNumber) {
        if (roomNumber < 0 || roomNumber >= numRooms) {
            throw new IllegalArgumentException("Invalid room number: " + roomNumber);
        }
        
        roomSchedules.get(roomNumber).add(screening);
        // Sort screenings by start time
        roomSchedules.get(roomNumber).sort(Comparator.comparingInt(Screening::getStartTime));
    }
    
    public boolean canAddScreening(ScreeningWithRevenue screening, int roomNumber) {
        if (roomNumber < 0 || roomNumber >= numRooms) {
            return false;
        }
        
        List<TimeSlot> availableSlots = findAvailableTimeSlots(screening.getDuration(), roomNumber);
        return !availableSlots.isEmpty();
    }
    
    public List<TimeSlot> findAvailableTimeSlots(int duration, int roomNumber) {
        List<TimeSlot> availableSlots = new ArrayList<>();
        List<ScreeningWithRevenue> roomScreenings = roomSchedules.get(roomNumber);
        
        // If there are no screenings, the entire day is available
        if (roomScreenings.isEmpty()) {
            availableSlots.add(new TimeSlot(OPENING_TIME, CLOSING_TIME));
            return availableSlots;
        }
        
        // Check if there's a gap before the first screening
        int firstStart = roomScreenings.get(0).getStartTime();
        if (firstStart - OPENING_TIME >= duration) {
            availableSlots.add(new TimeSlot(OPENING_TIME, firstStart));
        }
        
        // Check for gaps between screenings
        for (int i = 0; i < roomScreenings.size() - 1; i++) {
            int currentEnd = roomScreenings.get(i).getEndTime();
            int nextStart = roomScreenings.get(i + 1).getStartTime();
            
            if (nextStart - currentEnd >= duration) {
                availableSlots.add(new TimeSlot(currentEnd, nextStart));
            }
        }
        
        // Check if there's a gap after the last screening
        int lastEnd = roomScreenings.get(roomScreenings.size() - 1).getEndTime();
        if (CLOSING_TIME - lastEnd >= duration) {
            availableSlots.add(new TimeSlot(lastEnd, CLOSING_TIME));
        }
        
        return availableSlots;
    }
    
    public int addScreeningOptimized(ScreeningWithRevenue screening) {
        int bestRoom = -1;
        TimeSlot bestSlot = null;
        
        // Try to find the best room and time slot
        for (int room = 0; room < numRooms; room++) {
            List<TimeSlot> slots = findAvailableTimeSlots(screening.getDuration(), room);
            
            if (!slots.isEmpty()) {
                // For simplicity, choose the first available slot in each room
                TimeSlot slot = slots.get(0);
                
                if (bestSlot == null || slot.end - slot.start < bestSlot.end - bestSlot.start) {
                    bestRoom = room;
                    bestSlot = slot;
                }
            }
        }
        
        if (bestRoom >= 0 && bestSlot != null) {
            // Schedule the screening in the best room and slot
            screening.schedule(bestSlot.start);
            addScreening(screening, bestRoom);
            return bestRoom;
        }
        
        return -1;  // Could not add the screening to any room
    }
    
    public double getTotalRevenue() {
        double total = 0;
        
        for (List<ScreeningWithRevenue> roomSchedule : roomSchedules) {
            for (ScreeningWithRevenue screening : roomSchedule) {
                total += screening.getRevenue();
            }
        }
        
        return total;
    }
    
    public void printSchedule() {
        for (int room = 0; room < numRooms; room++) {
            System.out.println("Room " + (room + 1) + ":");
            
            List<ScreeningWithRevenue> roomScreenings = roomSchedules.get(room);
            if (roomScreenings.isEmpty()) {
                System.out.println("  No screenings scheduled");
                continue;
            }
            
            for (ScreeningWithRevenue screening : roomScreenings) {
                System.out.println("  " + screening.getTitle() + ": " + 
                        formatTime(screening.getStartTime()) + " - " + 
                        formatTime(screening.getEndTime()) + 
                        " (Revenue: $" + screening.getRevenue() + ")");
            }
        }
    }
    
    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
}