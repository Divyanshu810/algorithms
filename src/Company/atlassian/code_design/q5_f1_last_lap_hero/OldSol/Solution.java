package Company.atlassian.code_design.q5_f1_last_lap_hero.OldSol;

import java.util.*;

/**
 * Solution for F1 Last Lap Hero problem.
 * Identifies the driver with the biggest improvement on their last lap compared to their average lap time.
 */
public class Solution {
    
    public static void main(String[] args) {
        // Example usage
        F1RaceManager raceManager = new F1RaceManager();
        
        // Add some drivers with lap times
        raceManager.addLapTime("Hamilton", 90.5);
        raceManager.addLapTime("Hamilton", 91.2);
        raceManager.addLapTime("Hamilton", 90.8);
        raceManager.addLapTime("Hamilton", 89.9); // Last lap is faster than average
        
        raceManager.addLapTime("Verstappen", 90.2);
        raceManager.addLapTime("Verstappen", 89.8);
        raceManager.addLapTime("Verstappen", 89.5);
        raceManager.addLapTime("Verstappen", 89.2); // Last lap is faster than average but less improvement
        
        raceManager.addLapTime("Leclerc", 91.0);
        raceManager.addLapTime("Leclerc", 90.8);
        raceManager.addLapTime("Leclerc", 90.5);
        raceManager.addLapTime("Leclerc", 91.1); // Last lap is slower than average
        
        // Get the last lap hero
        System.out.println("Last Lap Hero: " + raceManager.getLastLapHero());
        
        // Scale-up 1: With pit stops
        F1RaceManagerWithPitStops raceManagerWithPitStops = new F1RaceManagerWithPitStops();
        
        raceManagerWithPitStops.addLapTime("Hamilton", 90.5);
        raceManagerWithPitStops.addLapTime("Hamilton", 91.2);
        raceManagerWithPitStops.addLapTime("Hamilton", 120.0, true); // Pit stop lap
        raceManagerWithPitStops.addLapTime("Hamilton", 89.9);
        
        raceManagerWithPitStops.addLapTime("Verstappen", 90.2);
        raceManagerWithPitStops.addLapTime("Verstappen", 89.8);
        raceManagerWithPitStops.addLapTime("Verstappen", 89.5);
        raceManagerWithPitStops.addLapTime("Verstappen", 89.2);
        
        System.out.println("Last Lap Hero (with pit stops excluded): " + 
                raceManagerWithPitStops.getLastLapHero());
        
        // Scale-up 2: Telemetry reporting
        F1RaceManagerWithTelemetry telemetryManager = new F1RaceManagerWithTelemetry();
        
        // Add a telemetry listener
        telemetryManager.addTelemetryListener(event -> 
            System.out.println("Telemetry Event: " + event.getMessage()));
        
        // Add lap times to observe changes in the Last Lap Hero
        telemetryManager.addLapTime("Hamilton", 90.5);
        telemetryManager.addLapTime("Verstappen", 90.2);
        telemetryManager.addLapTime("Hamilton", 89.0);
        telemetryManager.addLapTime("Verstappen", 88.5);
        telemetryManager.addLapTime("Leclerc", 91.0);
        telemetryManager.addLapTime("Leclerc", 89.0);
        
        // Scale-down implementations
        System.out.println("\nScale-down implementations:");
        
        // Scale-down 1: Fastest lap
        F1FastestLapFinder fastestLapFinder = new F1FastestLapFinder();
        fastestLapFinder.addLapTime("Hamilton", 90.5);
        fastestLapFinder.addLapTime("Hamilton", 89.9);
        fastestLapFinder.addLapTime("Verstappen", 90.2);
        fastestLapFinder.addLapTime("Verstappen", 89.2);
        
        System.out.println("Driver with fastest lap: " + fastestLapFinder.getDriverWithFastestLap());
        
        // Scale-down 2: Single driver stats
        F1SingleDriverStats singleDriverStats = new F1SingleDriverStats("Hamilton");
        singleDriverStats.addLapTime(90.5);
        singleDriverStats.addLapTime(91.2);
        singleDriverStats.addLapTime(89.9);
        
        System.out.println("Hamilton's fastest lap: " + singleDriverStats.getFastestLapTime());
        System.out.println("Hamilton's average lap time: " + singleDriverStats.getAverageLapTime());
    }
}

/**
 * Represents a Formula 1 driver with lap times and statistics.
 */
class Driver {
    private String name;
    private List<Double> lapTimes;
    
    public Driver(String name) {
        this.name = name;
        this.lapTimes = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void addLapTime(double lapTime) {
        lapTimes.add(lapTime);
    }
    
    public List<Double> getLapTimes() {
        return new ArrayList<>(lapTimes);
    }
    
    public double getLastLapTime() {
        if (lapTimes.isEmpty()) {
            return 0;
        }
        return lapTimes.get(lapTimes.size() - 1);
    }
    
    public double getAverageLapTimeExcludingLast() {
        if (lapTimes.size() <= 1) {
            return 0;
        }
        
        double sum = 0;
        for (int i = 0; i < lapTimes.size() - 1; i++) {
            sum += lapTimes.get(i);
        }
        
        return sum / (lapTimes.size() - 1);
    }
    
    public double getLastLapImprovement() {
        double average = getAverageLapTimeExcludingLast();
        if (average == 0) {
            return 0;
        }
        
        // Improvement is the difference between average and last lap
        // Positive value means the last lap was faster (better)
        return average - getLastLapTime();
    }
    
    public double getFastestLapTime() {
        if (lapTimes.isEmpty()) {
            return 0;
        }
        
        return Collections.min(lapTimes);
    }
    
    public double getAverageLapTime() {
        if (lapTimes.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        for (double lapTime : lapTimes) {
            sum += lapTime;
        }
        
        return sum / lapTimes.size();
    }
}

/**
 * Manages a Formula 1 race with multiple drivers.
 */
class F1RaceManager {
    protected Map<String, Driver> drivers;
    
    public F1RaceManager() {
        this.drivers = new HashMap<>();
    }
    
    public void addLapTime(String driverName, double lapTime) {
        Driver driver = drivers.computeIfAbsent(driverName, Driver::new);
        driver.addLapTime(lapTime);
    }
    
    public String getLastLapHero() {
        if (drivers.isEmpty()) {
            return "No drivers in the race";
        }
        
        String heroName = null;
        double maxImprovement = Double.NEGATIVE_INFINITY;
        
        for (Driver driver : drivers.values()) {
            if (driver.getLapTimes().size() > 1) {
                double improvement = driver.getLastLapImprovement();
                if (improvement > maxImprovement) {
                    maxImprovement = improvement;
                    heroName = driver.getName();
                }
            }
        }
        
        return heroName != null ? heroName : "No eligible drivers";
    }
}

/**
 * Extended F1RaceManager that handles pit stops.
 */
class F1RaceManagerWithPitStops {
    private Map<String, DriverWithPitStops> drivers;
    
    public F1RaceManagerWithPitStops() {
        this.drivers = new HashMap<>();
    }
    
    public void addLapTime(String driverName, double lapTime) {
        addLapTime(driverName, lapTime, false);
    }
    
    public void addLapTime(String driverName, double lapTime, boolean isPitStop) {
        DriverWithPitStops driver = drivers.computeIfAbsent(driverName, DriverWithPitStops::new);
        driver.addLapTime(lapTime, isPitStop);
    }
    
    public String getLastLapHero() {
        if (drivers.isEmpty()) {
            return "No drivers in the race";
        }
        
        String heroName = null;
        double maxImprovement = Double.NEGATIVE_INFINITY;
        
        for (DriverWithPitStops driver : drivers.values()) {
            if (driver.hasMultipleLaps()) {
                double improvement = driver.getLastLapImprovement();
                if (improvement > maxImprovement) {
                    maxImprovement = improvement;
                    heroName = driver.getName();
                }
            }
        }
        
        return heroName != null ? heroName : "No eligible drivers";
    }
    
    // Also provide a method to get hero including pit stops if needed
    public String getLastLapHeroIncludingPitStops() {
        if (drivers.isEmpty()) {
            return "No drivers in the race";
        }
        
        String heroName = null;
        double maxImprovement = Double.NEGATIVE_INFINITY;
        
        for (DriverWithPitStops driver : drivers.values()) {
            if (driver.hasMultipleLaps()) {
                double improvement = driver.getLastLapImprovementIncludingPitStops();
                if (improvement > maxImprovement) {
                    maxImprovement = improvement;
                    heroName = driver.getName();
                }
            }
        }
        
        return heroName != null ? heroName : "No eligible drivers";
    }
}

/**
 * Represents a driver with additional pit stop tracking.
 */
class DriverWithPitStops {
    private String name;
    private List<LapRecord> lapRecords;
    
    public DriverWithPitStops(String name) {
        this.name = name;
        this.lapRecords = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }
    
    public void addLapTime(double lapTime, boolean isPitStop) {
        lapRecords.add(new LapRecord(lapTime, isPitStop));
    }
    
    public boolean hasMultipleLaps() {
        return lapRecords.size() > 1;
    }
    
    public double getLastLapTime() {
        if (lapRecords.isEmpty()) {
            return 0;
        }
        return lapRecords.get(lapRecords.size() - 1).getLapTime();
    }
    
    public double getAverageLapTimeExcludingLast() {
        if (lapRecords.size() <= 1) {
            return 0;
        }
        
        double sum = 0;
        int count = 0;
        
        for (int i = 0; i < lapRecords.size() - 1; i++) {
            LapRecord lap = lapRecords.get(i);
            if (!lap.isPitStop()) {
                sum += lap.getLapTime();
                count++;
            }
        }
        
        return count > 0 ? sum / count : 0;
    }
    
    public double getAverageLapTimeExcludingLastIncludingPitStops() {
        if (lapRecords.size() <= 1) {
            return 0;
        }
        
        double sum = 0;
        for (int i = 0; i < lapRecords.size() - 1; i++) {
            sum += lapRecords.get(i).getLapTime();
        }
        
        return sum / (lapRecords.size() - 1);
    }
    
    public double getLastLapImprovement() {
        double average = getAverageLapTimeExcludingLast();
        if (average == 0) {
            return 0;
        }
        
        double lastLapTime = getLastLapTime();
        return average - lastLapTime;
    }
    
    public double getLastLapImprovementIncludingPitStops() {
        double average = getAverageLapTimeExcludingLastIncludingPitStops();
        if (average == 0) {
            return 0;
        }
        
        double lastLapTime = getLastLapTime();
        return average - lastLapTime;
    }
}

/**
 * Represents a single lap record with pit stop information.
 */
class LapRecord {
    private double lapTime;
    private boolean pitStop;
    
    public LapRecord(double lapTime, boolean pitStop) {
        this.lapTime = lapTime;
        this.pitStop = pitStop;
    }
    
    public double getLapTime() {
        return lapTime;
    }
    
    public boolean isPitStop() {
        return pitStop;
    }
}

/**
 * Interface for telemetry listeners to receive race updates.
 */
interface TelemetryListener {
    void onEvent(TelemetryEvent event);
}

/**
 * Event class to represent telemetry information.
 */
class TelemetryEvent {
    private String message;
    private long timestamp;
    
    public TelemetryEvent(String message) {
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }
    
    public String getMessage() {
        return message;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
}

/**
 * Extended F1RaceManager with telemetry reporting.
 */
class F1RaceManagerWithTelemetry {
    private Map<String, Driver> drivers;
    private List<TelemetryListener> listeners;
    private String currentLastLapHero;
    
    public F1RaceManagerWithTelemetry() {
        this.drivers = new HashMap<>();
        this.listeners = new ArrayList<>();
        this.currentLastLapHero = null;
    }
    
    public void addTelemetryListener(TelemetryListener listener) {
        listeners.add(listener);
    }
    
    public void removeTelemetryListener(TelemetryListener listener) {
        listeners.remove(listener);
    }
    
    private void notifyListeners(String message) {
        TelemetryEvent event = new TelemetryEvent(message);
        for (TelemetryListener listener : listeners) {
            listener.onEvent(event);
        }
    }
    
    public void addLapTime(String driverName, double lapTime) {
        Driver driver = drivers.computeIfAbsent(driverName, Driver::new);
        driver.addLapTime(lapTime);
        
        // Check if the Last Lap Hero has changed
        updateLastLapHero();
    }
    
    private void updateLastLapHero() {
        String newHero = calculateLastLapHero();
        
        // If hero is different from current hero, notify listeners
        if (newHero != null && !newHero.equals(currentLastLapHero)) {
            String previousHero = currentLastLapHero;
            currentLastLapHero = newHero;
            
            String message;
            if (previousHero == null) {
                message = "New Last Lap Hero: " + newHero;
            } else {
                message = "Last Lap Hero changed from " + previousHero + " to " + newHero;
            }
            
            notifyListeners(message);
        }
    }
    
    private String calculateLastLapHero() {
        if (drivers.isEmpty()) {
            return null;
        }
        
        String heroName = null;
        double maxImprovement = Double.NEGATIVE_INFINITY;
        
        for (Driver driver : drivers.values()) {
            if (driver.getLapTimes().size() > 1) {
                double improvement = driver.getLastLapImprovement();
                if (improvement > maxImprovement) {
                    maxImprovement = improvement;
                    heroName = driver.getName();
                }
            }
        }
        
        return heroName;
    }
    
    public String getLastLapHero() {
        return currentLastLapHero != null ? currentLastLapHero : "No eligible drivers";
    }
}

/**
 * Scale-down implementation: Only finds the driver with the fastest lap.
 */
class F1FastestLapFinder {
    private Map<String, Driver> drivers;
    
    public F1FastestLapFinder() {
        this.drivers = new HashMap<>();
    }
    
    public void addLapTime(String driverName, double lapTime) {
        Driver driver = drivers.computeIfAbsent(driverName, Driver::new);
        driver.addLapTime(lapTime);
    }
    
    public String getDriverWithFastestLap() {
        if (drivers.isEmpty()) {
            return "No drivers in the race";
        }
        
        String fastestDriver = null;
        double fastestLap = Double.MAX_VALUE;
        
        for (Driver driver : drivers.values()) {
            if (!driver.getLapTimes().isEmpty()) {
                double driverFastestLap = driver.getFastestLapTime();
                if (driverFastestLap < fastestLap) {
                    fastestLap = driverFastestLap;
                    fastestDriver = driver.getName();
                }
            }
        }
        
        return fastestDriver != null ? fastestDriver + " (" + fastestLap + "s)" : "No laps recorded";
    }
}

/**
 * Scale-down implementation: Stats for a single driver.
 */
class F1SingleDriverStats {
    private String driverName;
    private List<Double> lapTimes;
    
    public F1SingleDriverStats(String driverName) {
        this.driverName = driverName;
        this.lapTimes = new ArrayList<>();
    }
    
    public void addLapTime(double lapTime) {
        lapTimes.add(lapTime);
    }
    
    public double getFastestLapTime() {
        if (lapTimes.isEmpty()) {
            return 0;
        }
        return Collections.min(lapTimes);
    }
    
    public double getAverageLapTime() {
        if (lapTimes.isEmpty()) {
            return 0;
        }
        
        double sum = 0;
        for (double lapTime : lapTimes) {
            sum += lapTime;
        }
        
        return sum / lapTimes.size();
    }
}