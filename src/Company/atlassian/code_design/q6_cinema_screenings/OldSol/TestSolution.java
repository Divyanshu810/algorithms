package Company.atlassian.code_design.q6_cinema_screenings.OldSol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Test class for the Cinema Screenings solution.
 */
public class TestSolution {
    
    private Cinema cinema;
    private CinemaWithRevenue cinemaWithRevenue;
    private MultiRoomCinema multiRoomCinema;
    
    @BeforeEach
    void setUp() {
        cinema = new Cinema();
        cinemaWithRevenue = new CinemaWithRevenue();
        multiRoomCinema = new MultiRoomCinema(3); // 3 rooms
    }
    
    /**
     * Helper method to format time in minutes to HH:MM format.
     */
    private String formatTime(int minutes) {
        int hours = minutes / 60;
        int mins = minutes % 60;
        return String.format("%02d:%02d", hours, mins);
    }
    
    @Test
    @DisplayName("Test adding to empty schedule")
    void testEmptySchedule() {
        Screening newMovie = new Screening("Test Movie 1", 120);
        boolean canAdd = cinema.canAddScreening(newMovie);
        
        assertTrue(canAdd, "Should be able to add to empty schedule");
    }
    
    @Test
    @DisplayName("Test adding with existing screenings")
    void testWithExistingScreenings() {
        // Add some screenings
        cinema.addScreening(new Screening("Movie A", 600, 750));  // 10:00-12:30
        cinema.addScreening(new Screening("Movie B", 800, 950));  // 13:20-15:50
        cinema.addScreening(new Screening("Movie C", 1000, 1150));  // 16:40-19:10
        cinema.addScreening(new Screening("Movie D", 1200, 1350));  // 20:00-22:30
        
        // Test case 1: Movie fits in a gap
        Screening shortMovie = new Screening("Test Movie 2", 40);  // Short movie
        boolean canAddShort = cinema.canAddScreening(shortMovie);
        assertTrue(canAddShort, "Should be able to add short movie in gaps");
        
        // Test case 2: Movie with longer duration
        Screening mediumMovie = new Screening("Test Movie 3", 120);  // 2-hour movie
        boolean canAddMedium = cinema.canAddScreening(mediumMovie);
        assertTrue(canAddMedium, "Should be able to add 2-hour movie");
        
        // Check available time slots
        List<TimeSlot> availableSlots = cinema.findAvailableTimeSlots(120);
        assertNotNull(availableSlots, "Available slots should not be null");
    }
    
    @Test
    @DisplayName("Test revenue calculation")
    void testRevenue() {
        // Add screenings with revenue
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie A", 600, 750, 1000));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie B", 800, 950, 1200));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie C", 1000, 1150, 1500));
        
        // Test case 1: Calculate total revenue
        double totalRevenue = cinemaWithRevenue.getTotalRevenue();
        assertEquals(3700, totalRevenue, 0.001, "Total revenue should be 3700");
        
        // Test case 2: Add another screening
        ScreeningWithRevenue newMovie = new ScreeningWithRevenue("Movie D", 120, 800);
        cinemaWithRevenue.addScreening(newMovie);
        
        double newTotalRevenue = cinemaWithRevenue.getTotalRevenue();
        assertEquals(4500, newTotalRevenue, 0.001, "New total revenue should be 4500");
    }
    
    @Test
    @DisplayName("Test adding to full schedule")
    void testFullSchedule() {
        // Create a full schedule with no gaps
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie A", 600, 800, 1000));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie B", 800, 1000, 800));  // Least profitable
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie C", 1000, 1200, 1500));
        cinemaWithRevenue.addScreening(new ScreeningWithRevenue("Movie D", 1200, 1380, 1200));
        
        double initialRevenue = cinemaWithRevenue.getTotalRevenue();
        
        // Add more profitable movie to full schedule
        ScreeningWithRevenue newMovie = new ScreeningWithRevenue("New Movie", 200, 1100);  // More profitable than B
        ScreeningWithRevenue replaced = cinemaWithRevenue.addToFullSchedule(newMovie);
        
        assertNotNull(replaced, "Should replace a screening");
        assertEquals("Movie B", replaced.getTitle(), "Should replace the least profitable movie");
        
        double newRevenue = cinemaWithRevenue.getTotalRevenue();
        assertTrue(newRevenue > initialRevenue, "New revenue should be higher");
    }
    
    @Test
    @DisplayName("Test multi-room implementation")
    void testMultiRoom() {
        // Add screenings to different rooms
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Movie A1", 600, 800, 1000), 0);
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Movie A2", 900, 1100, 1200), 0);
        
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Movie B1", 700, 900, 900), 1);
        multiRoomCinema.addScreening(new ScreeningWithRevenue("Movie B2", 1000, 1200, 1100), 1);
        
        // Room 2 is empty
        
        // Test case 1: Check total revenue
        double totalRevenue = multiRoomCinema.getTotalRevenue();
        assertEquals(4200, totalRevenue, 0.001, "Total revenue should be 4200");
        
        // Test case 2: Optimal room assignment
        ScreeningWithRevenue newMovie = new ScreeningWithRevenue("New Movie", 120, 1000);
        int roomAssigned = multiRoomCinema.addScreeningOptimized(newMovie);
        
        assertEquals(2, roomAssigned, "Should assign to the empty room (room 2)");
        
        // Test case 3: Check available slots in each room
        List<TimeSlot> slotsRoom0 = multiRoomCinema.findAvailableTimeSlots(120, 0);
        List<TimeSlot> slotsRoom1 = multiRoomCinema.findAvailableTimeSlots(120, 1);
        List<TimeSlot> slotsRoom2 = multiRoomCinema.findAvailableTimeSlots(120, 2);
        
        assertNotNull(slotsRoom0, "Room 0 slots should not be null");
        assertNotNull(slotsRoom1, "Room 1 slots should not be null");
        assertNotNull(slotsRoom2, "Room 2 slots should not be null");
    }
    
    @Test
    @DisplayName("Test scale-down implementation")
    void testScaleDownImplementation() {
        // Add some screenings
        cinema.addScreening(new Screening("Movie A", 600, 750));
        cinema.addScreening(new Screening("Movie B", 800, 950));
        
        // Get screenings for simple output
        List<Screening> screenings = cinema.getScreenings();
        assertEquals(2, screenings.size(), "Should have 2 screenings");
        
        // Print schedule in simple format (just for demonstration)
        for (Screening s : screenings) {
            String formattedSchedule = s.getTitle() + ": " + 
                    formatTime(s.getStartTime()) + " - " + 
                    formatTime(s.getEndTime());
            assertNotNull(formattedSchedule);
        }
    }
}