package Company.atlassian.code_design.q5_f1_last_lap_hero.NewSol;
import java.util.*;

/*
┌─────────────────────────────────────────────────────────────────┐
│                       Driver                                     │
├─────────────────────────────────────────────────────────────────┤
│ - id: String                                                    │
│ - name: String                                                  │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                       LapTime                                    │
├─────────────────────────────────────────────────────────────────┤
│ - driverId: String                                              │
│ - time: double (seconds)                                        │
│ - isPitStop: boolean                                            │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     DriverStats                                  │
├─────────────────────────────────────────────────────────────────┤
│ - driverId: String                                              │
│ - driverName: String                                            │
│ - averageLapTime: double                                        │
│ - lastLapTime: double                                           │
│ - improvement: double                                           │
│ - fastestLap: double                                            │
│ - totalLaps: int                                                │
└─────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────┐
│                     RaceService                                  │
├─────────────────────────────────────────────────────────────────┤
│ - drivers: Map<String, Driver>                                  │
│ - laps: List<LapTime>                                           │
│ - heroChangeLog: List<String>                                   │
├─────────────────────────────────────────────────────────────────┤
│ + addDriver(id, name): void                                     │
│ + addLapTime(driverId, time): void                              │
│ + addLapTime(driverId, time, isPitStop): void                   │
│ + getLastLapHero(): DriverStats                                 │
│ + getLastLapHeroExcludingPitStops(): DriverStats                │
│ + getFastestLapDriver(): DriverStats                            │
│ + getSingleDriverStats(driverId): DriverStats                   │
│ + getHeroChangeLog(): List<String>                              │
└─────────────────────────────────────────────────────────────────┘
 */

public class Sol {

    public class Driver {
        private final String id;
        private final String name;

        public Driver(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }
    }

    public class LapTime {
        private final String driverId;
        private final double time;  // in seconds
        private final boolean isPitStop;

        public LapTime(String driverId, double time) {
            this(driverId, time, false);
        }

        public LapTime(String driverId, double time, boolean isPitStop) {
            this.driverId = driverId;
            this.time = time;
            this.isPitStop = isPitStop;
        }

        public String getDriverId() {
            return driverId;
        }

        public double getTime() {
            return time;
        }

        public boolean isPitStop() {
            return isPitStop;
        }
    }

    public class DriverStats {
        private final String driverId;
        private final String driverName;
        private final double averageLapTime;
        private final double lastLapTime;
        private final double improvement;  // positive = improved
        private final double fastestLap;
        private final int totalLaps;

        public DriverStats(String driverId, String driverName, double averageLapTime,
                           double lastLapTime, double fastestLap, int totalLaps) {
            this.driverId = driverId;
            this.driverName = driverName;
            this.averageLapTime = averageLapTime;
            this.lastLapTime = lastLapTime;
            this.improvement = averageLapTime - lastLapTime;  // positive means faster
            this.fastestLap = fastestLap;
            this.totalLaps = totalLaps;
        }

        public String getDriverId() {
            return driverId;
        }

        public String getDriverName() {
            return driverName;
        }

        public double getAverageLapTime() {
            return averageLapTime;
        }

        public double getLastLapTime() {
            return lastLapTime;
        }

        public double getImprovement() {
            return improvement;
        }

        public double getFastestLap() {
            return fastestLap;
        }

        public int getTotalLaps() {
            return totalLaps;
        }

        @Override
        public String toString() {
            return driverName + " - Avg: " + averageLapTime + "s, Last: " + lastLapTime +
                    "s, Improvement: " + improvement + "s";
        }
    }



    public class RaceService {

        private final Map<String, Driver> drivers;
        private final List<LapTime> laps;
        private final List<String> heroChangeLog;  // Scale-Up 2: Telemetry
        private String currentHero;

        public RaceService() {
            this.drivers = new HashMap<>();
            this.laps = new ArrayList<>();
            this.heroChangeLog = new ArrayList<>();
            this.currentHero = null;
        }

        public void addDriver(String id, String name) {
            drivers.put(id, new Driver(id, name));
        }

        // Base: Add lap time
        public void addLapTime(String driverId, double time) {
            addLapTime(driverId, time, false);
        }

        // Scale-Up 1: Add lap time with pit stop flag
        public void addLapTime(String driverId, double time, boolean isPitStop) {
            if (!drivers.containsKey(driverId)) {
                throw new IllegalArgumentException("Driver not found: " + driverId);
            }

            laps.add(new LapTime(driverId, time, isPitStop));

            // Scale-Up 2: Track hero changes
            trackHeroChange();
        }

        // Base: Get Last Lap Hero (including pit stops in average)
        public DriverStats getLastLapHero() {
            return getLastLapHero(false);
        }

        // Scale-Up 1: Get Last Lap Hero excluding pit stops from average
        public DriverStats getLastLapHeroExcludingPitStops() {
            return getLastLapHero(true);
        }

        private DriverStats getLastLapHero(boolean excludePitStops) {
            List<DriverStats> allStats = getAllDriverStats(excludePitStops);

            if (allStats.isEmpty()) {
                return null;
            }

            // Find driver with biggest improvement
            DriverStats hero = allStats.get(0);
            for (DriverStats stats : allStats) {
                if (stats.getImprovement() > hero.getImprovement()) {
                    hero = stats;
                }
            }

            return hero;
        }

        // Scale-Down 1: Get driver with fastest lap
        public DriverStats getFastestLapDriver() {
            List<DriverStats> allStats = getAllDriverStats(false);

            if (allStats.isEmpty()) {
                return null;
            }

            DriverStats fastest = allStats.get(0);
            for (DriverStats stats : allStats) {
                if (stats.getFastestLap() < fastest.getFastestLap()) {
                    fastest = stats;
                }
            }

            return fastest;
        }

        // Scale-Down 2: Get single driver stats
        public DriverStats getSingleDriverStats(String driverId) {
            if (!drivers.containsKey(driverId)) {
                throw new IllegalArgumentException("Driver not found: " + driverId);
            }

            List<LapTime> driverLaps = getLapsForDriver(driverId);

            if (driverLaps.isEmpty()) {
                return null;
            }

            return calculateStats(driverId, driverLaps, false);
        }

        // Scale-Up 2: Get hero change log
        public List<String> getHeroChangeLog() {
            return new ArrayList<>(heroChangeLog);
        }

        // Helper: Get all driver stats
        private List<DriverStats> getAllDriverStats(boolean excludePitStops) {
            List<DriverStats> result = new ArrayList<>();

            // Get unique driver IDs from laps
            Set<String> driverIds = new HashSet<>();
            for (LapTime lap : laps) {
                driverIds.add(lap.getDriverId());
            }

            // Calculate stats for each driver
            for (String driverId : driverIds) {
                List<LapTime> driverLaps = getLapsForDriver(driverId);

                if (driverLaps.size() < 2) {
                    continue;  // Need at least 2 laps for comparison
                }

                DriverStats stats = calculateStats(driverId, driverLaps, excludePitStops);
                if (stats != null) {
                    result.add(stats);
                }
            }

            return result;
        }

        // Helper: Get laps for a specific driver
        private List<LapTime> getLapsForDriver(String driverId) {
            List<LapTime> driverLaps = new ArrayList<>();
            for (LapTime lap : laps) {
                if (lap.getDriverId().equals(driverId)) {
                    driverLaps.add(lap);
                }
            }
            return driverLaps;
        }

        // Helper: Calculate stats for a driver
        private DriverStats calculateStats(String driverId, List<LapTime> driverLaps,
                                           boolean excludePitStops) {
            if (driverLaps.isEmpty()) {
                return null;
            }

            Driver driver = drivers.get(driverId);

            // Calculate average (optionally excluding pit stops)
            double totalTime = 0;
            int count = 0;
            double fastestLap = Double.MAX_VALUE;

            for (LapTime lap : driverLaps) {
                // For average calculation
                if (!excludePitStops || !lap.isPitStop()) {
                    totalTime += lap.getTime();
                    count++;
                }

                // Fastest lap considers all laps
                if (lap.getTime() < fastestLap) {
                    fastestLap = lap.getTime();
                }
            }

            if (count == 0) {
                return null;
            }

            double average = totalTime / count;
            double lastLap = driverLaps.get(driverLaps.size() - 1).getTime();

            return new DriverStats(driverId, driver.getName(), average,
                    lastLap, fastestLap, driverLaps.size());
        }

        // Scale-Up 2: Track hero changes
        private void trackHeroChange() {
            DriverStats newHero = getLastLapHero();

            if (newHero == null) {
                return;
            }

            String newHeroId = newHero.getDriverId();

            if (currentHero == null) {
                currentHero = newHeroId;
                heroChangeLog.add("Lap " + laps.size() + ": " + newHero.getDriverName() +
                        " is the first Last Lap Hero");
            } else if (!currentHero.equals(newHeroId)) {
                heroChangeLog.add("Lap " + laps.size() + ": Hero changed from " +
                        drivers.get(currentHero).getName() + " to " +
                        newHero.getDriverName());
                currentHero = newHeroId;
            }
        }
    }
}
