package practice.atlassian.code_design.q5_f1_last_lap_hero;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class for the F1 Last Lap Hero solution.
 */
public class TestSolution {
    
    private F1RaceManager raceManager;
    private F1RaceManagerWithPitStops pitStopManager;
    private F1RaceManagerWithTelemetry telemetryManager;
    
    @BeforeEach
    void setUp() {
        raceManager = new F1RaceManager();
        pitStopManager = new F1RaceManagerWithPitStops();
        telemetryManager = new F1RaceManagerWithTelemetry();
    }
    
    @Test
    @DisplayName("Test base implementation with single driver")
    void testSingleDriver() {
        // Test case 1: Single driver, multiple laps
        raceManager.addLapTime("Hamilton", 90.5);
        raceManager.addLapTime("Hamilton", 91.2);
        raceManager.addLapTime("Hamilton", 89.0);  // Faster than average
        
        assertEquals("Hamilton", raceManager.getLastLapHero(), "Expected Hamilton as Last Lap Hero");
    }
    
    @Test
    @DisplayName("Test base implementation with multiple drivers")
    void testMultipleDrivers() {
        // Setup driver 1
        raceManager.addLapTime("Hamilton", 90.5);
        raceManager.addLapTime("Hamilton", 91.2);
        raceManager.addLapTime("Hamilton", 89.0);  // Faster than average
        
        // Add second driver
        raceManager.addLapTime("Verstappen", 90.0);
        raceManager.addLapTime("Verstappen", 89.8);
        raceManager.addLapTime("Verstappen", 89.7);  // Small improvement
        
        assertEquals("Hamilton", raceManager.getLastLapHero(), "Expected Hamilton as Last Lap Hero");
    }
    
    @Test
    @DisplayName("Test driver with last lap worse than average")
    void testLastLapWorseDriver() {
        // Driver 1 with worse last lap
        raceManager.addLapTime("Hamilton", 90.0);
        raceManager.addLapTime("Hamilton", 89.0);
        raceManager.addLapTime("Hamilton", 91.0);  // Worse than average
        
        // Driver 2 with better last lap
        raceManager.addLapTime("Verstappen", 92.0);
        raceManager.addLapTime("Verstappen", 91.0);
        raceManager.addLapTime("Verstappen", 90.5);  // Better than average
        
        assertEquals("Verstappen", raceManager.getLastLapHero(), "Expected Verstappen as Last Lap Hero");
    }
    
    @Test
    @DisplayName("Test empty race")
    void testEmptyRace() {
        String result = raceManager.getLastLapHero();
        assertTrue(result.contains("No"), "Empty race should return 'No drivers' message");
    }
    
    @Test
    @DisplayName("Test with only one lap per driver")
    void testSingleLapPerDriver() {
        raceManager.addLapTime("Hamilton", 90.0);
        raceManager.addLapTime("Verstappen", 89.0);
        
        String result = raceManager.getLastLapHero();
        assertTrue(result.contains("No eligible"), "Single lap per driver should return 'No eligible drivers' message");
    }
    
    @Test
    @DisplayName("Test pit stop implementation with excluded pit stop")
    void testPitStopExcluded() {
        // Driver with pit stop
        pitStopManager.addLapTime("Hamilton", 90.5);
        pitStopManager.addLapTime("Hamilton", 91.2);
        pitStopManager.addLapTime("Hamilton", 120.0, true);  // Pit stop lap (should be excluded from average)
        pitStopManager.addLapTime("Hamilton", 89.0);  // Better than non-pit-stop average
        
        // Driver without pit stop
        pitStopManager.addLapTime("Verstappen", 90.0);
        pitStopManager.addLapTime("Verstappen", 89.8);
        pitStopManager.addLapTime("Verstappen", 89.5);  // Small improvement
        
        assertEquals("Hamilton", pitStopManager.getLastLapHero(), "Expected Hamilton as Last Lap Hero");
    }
    
    @Test
    @DisplayName("Test pit stop implementation with pit stop as last lap")
    void testPitStopAsLastLap() {
        // Driver with pit stop as last lap
        pitStopManager.addLapTime("Hamilton", 90.5);
        pitStopManager.addLapTime("Hamilton", 91.2);
        pitStopManager.addLapTime("Hamilton", 120.0, true);  // Pit stop as last lap (much worse than average)
        
        // Driver without pit stop
        pitStopManager.addLapTime("Verstappen", 90.0);
        pitStopManager.addLapTime("Verstappen", 89.8);
        pitStopManager.addLapTime("Verstappen", 89.5);  // Better than average
        
        assertEquals("Verstappen", pitStopManager.getLastLapHero(), "Expected Verstappen as Last Lap Hero");
    }
    
    @Test
    @DisplayName("Test getting last lap hero including pit stops")
    void testLastLapHeroIncludingPitStops() {
        // Setup driver with pit stop as last lap
        pitStopManager.addLapTime("Hamilton", 90.5);
        pitStopManager.addLapTime("Hamilton", 91.2);
        pitStopManager.addLapTime("Hamilton", 120.0, true);  // Pit stop as last lap
        
        String result = pitStopManager.getLastLapHeroIncludingPitStops();
        assertTrue(result.contains("No eligible") || result.equals("Hamilton"), 
                "Expected Hamilton or No eligible drivers for including pit stops");
    }
    
    @Test
    @DisplayName("Test telemetry implementation")
    void testTelemetryEvents() {
        List<String> eventLog = new ArrayList<>();
        
        // Add a telemetry listener
        telemetryManager.addTelemetryListener(event -> {
            String message = event.getMessage();
            eventLog.add(message);
        });
        
        // First driver becomes the hero
        telemetryManager.addLapTime("Hamilton", 90.5);
        telemetryManager.addLapTime("Hamilton", 89.0);
        
        // Second driver becomes the hero
        telemetryManager.addLapTime("Verstappen", 89.0);
        telemetryManager.addLapTime("Verstappen", 87.0);  // Bigger improvement than Hamilton
        
        // Original driver regains hero status
        telemetryManager.addLapTime("Hamilton", 85.0);  // Huge improvement
        
        // Verify event count
        assertEquals(3, eventLog.size(), "Expected 3 telemetry events");
        
        // Verify content of events
        assertTrue(eventLog.get(0).contains("Hamilton"), "First event should mention Hamilton");
        assertTrue(eventLog.get(1).contains("Verstappen"), "Second event should mention Verstappen");
        assertTrue(eventLog.get(2).contains("Hamilton"), "Third event should mention Hamilton");
        
        // Verify final hero
        assertEquals("Hamilton", telemetryManager.getLastLapHero(), "Final hero should be Hamilton");
    }
    
    @Test
    @DisplayName("Test fastest lap finder implementation")
    void testFastestLapFinder() {
        F1FastestLapFinder fastestLapFinder = new F1FastestLapFinder();
        fastestLapFinder.addLapTime("Hamilton", 90.5);
        fastestLapFinder.addLapTime("Hamilton", 89.9);
        fastestLapFinder.addLapTime("Verstappen", 90.2);
        fastestLapFinder.addLapTime("Verstappen", 89.2);  // Fastest overall
        
        String fastestDriver = fastestLapFinder.getDriverWithFastestLap();
        assertTrue(fastestDriver.startsWith("Verstappen"), "Expected Verstappen to have fastest lap");
    }
    
    @Test
    @DisplayName("Test single driver stats implementation")
    void testSingleDriverStats() {
        F1SingleDriverStats singleDriverStats = new F1SingleDriverStats("Hamilton");
        singleDriverStats.addLapTime(90.5);
        singleDriverStats.addLapTime(91.2);
        singleDriverStats.addLapTime(89.9);  // Fastest lap
        
        double fastestLap = singleDriverStats.getFastestLapTime();
        double averageLap = singleDriverStats.getAverageLapTime();
        
        assertEquals(89.9, fastestLap, 0.001, "Expected fastest lap to be 89.9");
        assertEquals((90.5 + 91.2 + 89.9) / 3, averageLap, 0.001, "Average lap calculation incorrect");
    }
}