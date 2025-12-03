package Company.atlassian.code_design.q5_f1_last_lap_hero.NewSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Test {
    public class RaceServiceTest {

        private Sol.RaceService service;

        @BeforeEach
        void setUp() {
            service = new Sol.RaceService();
            service.addDriver("HAM", "Lewis Hamilton");
            service.addDriver("VER", "Max Verstappen");
            service.addDriver("LEC", "Charles Leclerc");
        }

        @Nested
        @DisplayName("Base: Last Lap Hero Tests")
        class LastLapHeroTests {

            @Test
            @DisplayName("Should find driver with biggest improvement")
            void testLastLapHero() {
                // Hamilton: avg = 90, last = 88, improvement = 2
                service.addLapTime("HAM", 92);
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                // Verstappen: avg = 89, last = 85, improvement = 4
                service.addLapTime("VER", 91);
                service.addLapTime("VER", 89);
                service.addLapTime("VER", 85);

                DriverStats hero = service.getLastLapHero();

                assertEquals("Max Verstappen", hero.getDriverName());
                assertEquals(85, hero.getLastLapTime());
            }

            @Test
            @DisplayName("Should handle single driver")
            void testSingleDriver() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                DriverStats hero = service.getLastLapHero();

                assertEquals("Lewis Hamilton", hero.getDriverName());
            }

            @Test
            @DisplayName("Should return null when no laps")
            void testNoLaps() {
                DriverStats hero = service.getLastLapHero();

                assertNull(hero);
            }

            @Test
            @DisplayName("Should need at least 2 laps for comparison")
            void testMinimumLaps() {
                service.addLapTime("HAM", 90);  // Only 1 lap

                DriverStats hero = service.getLastLapHero();

                assertNull(hero);
            }
        }

        @Nested
        @DisplayName("Scale-Up 1: Pit Stop Tests")
        class PitStopTests {

            @Test
            @DisplayName("Should include pit stops in average by default")
            void testIncludePitStops() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 120, true);  // Pit stop
                service.addLapTime("HAM", 88);

                // Average with pit stop: (90 + 120 + 88) / 3 = 99.33
                // Improvement: 99.33 - 88 = 11.33
                DriverStats hero = service.getLastLapHero();

                assertEquals("Lewis Hamilton", hero.getDriverName());
                assertTrue(hero.getImprovement() > 11);
            }

            @Test
            @DisplayName("Should exclude pit stops from average when requested")
            void testExcludePitStops() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 120, true);  // Pit stop - excluded from avg
                service.addLapTime("HAM", 88);

                // Average without pit stop: (90 + 88) / 2 = 89
                // Improvement: 89 - 88 = 1
                DriverStats hero = service.getLastLapHeroExcludingPitStops();

                assertEquals(1, hero.getImprovement(), 0.01);
            }

            @Test
            @DisplayName("Should allow pit stop as valid last lap")
            void testPitStopAsLastLap() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);
                service.addLapTime("HAM", 120, true);  // Last lap is pit stop

                DriverStats hero = service.getLastLapHero();

                assertEquals(120, hero.getLastLapTime());
            }
        }

        @Nested
        @DisplayName("Scale-Up 2: Telemetry Tests")
        class TelemetryTests {

            @Test
            @DisplayName("Should track hero changes")
            void testHeroChangeLog() {
                // Hamilton becomes first hero
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                // Verstappen takes over with better improvement
                service.addLapTime("VER", 95);
                service.addLapTime("VER", 84);

                List<String> log = service.getHeroChangeLog();

                assertEquals(2, log.size());
                assertTrue(log.get(0).contains("Lewis Hamilton"));
                assertTrue(log.get(1).contains("Max Verstappen"));
            }

            @Test
            @DisplayName("Should not log when hero stays same")
            void testNoChangeLog() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);
                service.addLapTime("HAM", 86);  // Hamilton still hero

                List<String> log = service.getHeroChangeLog();

                assertEquals(1, log.size());  // Only initial hero log
            }
        }

        @Nested
        @DisplayName("Scale-Down 1: Fastest Lap Tests")
        class FastestLapTests {

            @Test
            @DisplayName("Should find driver with fastest lap")
            void testFastestLap() {
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                service.addLapTime("VER", 91);
                service.addLapTime("VER", 85);  // Fastest

                service.addLapTime("LEC", 89);
                service.addLapTime("LEC", 87);

                DriverStats fastest = service.getFastestLapDriver();

                assertEquals("Max Verstappen", fastest.getDriverName());
                assertEquals(85, fastest.getFastestLap());
            }
        }

        @Nested
        @DisplayName("Scale-Down 2: Single Driver Stats Tests")
        class SingleDriverStatsTests {

            @Test
            @DisplayName("Should return stats for single driver")
            void testSingleDriverStats() {
                service.addLapTime("HAM", 92);
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                DriverStats stats = service.getSingleDriverStats("HAM");

                assertEquals("Lewis Hamilton", stats.getDriverName());
                assertEquals(90, stats.getAverageLapTime(), 0.01);
                assertEquals(88, stats.getFastestLap());
                assertEquals(88, stats.getLastLapTime());
                assertEquals(3, stats.getTotalLaps());
            }

            @Test
            @DisplayName("Should throw for unknown driver")
            void testUnknownDriver() {
                assertThrows(IllegalArgumentException.class,
                        () -> service.getSingleDriverStats("XXX"));
            }

            @Test
            @DisplayName("Should return null for driver with no laps")
            void testDriverNoLaps() {
                DriverStats stats = service.getSingleDriverStats("HAM");

                assertNull(stats);
            }
        }

        @Nested
        @DisplayName("Edge Cases")
        class EdgeCaseTests {

            @Test
            @DisplayName("Should handle negative improvement (slower last lap)")
            void testNegativeImprovement() {
                service.addLapTime("HAM", 88);
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 95);  // Got slower

                DriverStats stats = service.getSingleDriverStats("HAM");

                assertTrue(stats.getImprovement() < 0);
            }

            @Test
            @DisplayName("Should handle tie in improvement")
            void testTieImprovement() {
                // Both have same improvement
                service.addLapTime("HAM", 90);
                service.addLapTime("HAM", 88);

                service.addLapTime("VER", 92);
                service.addLapTime("VER", 90);

                DriverStats hero = service.getLastLapHero();

                // Either can be hero
                assertNotNull(hero);
            }

            @Test
            @DisplayName("Should reject unknown driver lap")
            void testUnknownDriverLap() {
                assertThrows(IllegalArgumentException.class,
                        () -> service.addLapTime("XXX", 90));
            }
        }
    }
```

        ---

        ## Project Structure
```
    src/
            ├── main/java/
            │   ├── Driver.java
│   ├── LapTime.java
│   ├── DriverStats.java
│   └── RaceService.java
└── test/java/
            └── RaceServiceTest.java
}
