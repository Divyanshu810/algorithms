package Company.atlassian.code_design.q1_customer_satisfaction.NewSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

    public class RatingServiceTest {

        private RatingService service;

        @BeforeEach
        void setUp() {
            service = new RatingService();
            service.addAgent("A1", "Alice");
            service.addAgent("A2", "Bob");
            service.addAgent("A3", "Charlie");
        }

        @Nested
        @DisplayName("Part A: Basic Rating Tests")
        class BasicRatingTests {

            @Test
            @DisplayName("Should accept ratings and calculate average")
            void testAddRatingAndAverage() {
                service.addRating("A1", 5);
                service.addRating("A1", 4);
                service.addRating("A1", 3);

                List<AgentStats> stats = service.getAgentsSortedByRating();

                assertEquals(1, stats.size());
                assertEquals(4.0, stats.get(0).getAverageRating(), 0.01);
            }

            @Test
            @DisplayName("Should sort agents by average rating (highest first)")
            void testSortByRating() {
                service.addRating("A1", 3);
                service.addRating("A2", 5);
                service.addRating("A3", 4);

                List<AgentStats> stats = service.getAgentsSortedByRating();

                assertEquals("Bob", stats.get(0).getAgentName());
                assertEquals("Charlie", stats.get(1).getAgentName());
                assertEquals("Alice", stats.get(2).getAgentName());
            }

            @Test
            @DisplayName("Should reject invalid rating score")
            void testInvalidScore() {
                assertThrows(IllegalArgumentException.class, () -> service.addRating("A1", 0));
                assertThrows(IllegalArgumentException.class, () -> service.addRating("A1", 6));
            }

            @Test
            @DisplayName("Should reject rating for unknown agent")
            void testUnknownAgent() {
                assertThrows(IllegalArgumentException.class, () -> service.addRating("X1", 5));
            }
        }

        @Nested
        @DisplayName("Part B: Tie Breaking Tests")
        class TieBreakingTests {

            @BeforeEach
            void setUpTies() {
                // Both get average of 4.0
                service.addRating("A1", 4);
                service.addRating("A1", 4);
                service.addRating("A2", 4);
            }

            @Test
            @DisplayName("Should break ties by total ratings count")
            void testTieBreakByCount() {
                service.setTieBreaker(TieBreakStrategies.BY_TOTAL_RATINGS);
                List<AgentStats> stats = service.getAgentsSortedByRating();

                // Alice has more ratings (2 vs 1)
                assertEquals("Alice", stats.get(0).getAgentName());
                assertEquals("Bob", stats.get(1).getAgentName());
            }

            @Test
            @DisplayName("Should break ties by name alphabetically")
            void testTieBreakByName() {
                service.setTieBreaker(TieBreakStrategies.BY_NAME);
                List<AgentStats> stats = service.getAgentsSortedByRating();

                // Alice comes before Bob alphabetically
                assertEquals("Alice", stats.get(0).getAgentName());
                assertEquals("Bob", stats.get(1).getAgentName());
            }

            @Test
            @DisplayName("Should break ties by agent ID")
            void testTieBreakById() {
                service.setTieBreaker(TieBreakStrategies.BY_ID);
                List<AgentStats> stats = service.getAgentsSortedByRating();

                // A1 comes before A2
                assertEquals("A1", stats.get(0).getAgentId());
                assertEquals("A2", stats.get(1).getAgentId());
            }
        }

        @Nested
        @DisplayName("Part C: Monthly Best Agent Tests")
        class MonthlyBestAgentTests {

            @Test
            @DisplayName("Should get best agent for specific month")
            void testBestAgentByMonth() {
                // January ratings
                service.addRating("A1", 5, LocalDateTime.of(2024, 1, 15, 10, 0));
                service.addRating("A2", 3, LocalDateTime.of(2024, 1, 20, 10, 0));

                // February ratings
                service.addRating("A1", 2, LocalDateTime.of(2024, 2, 10, 10, 0));
                service.addRating("A2", 5, LocalDateTime.of(2024, 2, 15, 10, 0));

                AgentStats janBest = service.getBestAgentByMonth(2024, 1);
                AgentStats febBest = service.getBestAgentByMonth(2024, 2);

                assertEquals("Alice", janBest.getAgentName());
                assertEquals("Bob", febBest.getAgentName());
            }

            @Test
            @DisplayName("Should return null if no ratings for month")
            void testNoRatingsForMonth() {
                service.addRating("A1", 5, LocalDateTime.of(2024, 1, 15, 10, 0));

                AgentStats result = service.getBestAgentByMonth(2024, 6);

                assertNull(result);
            }
        }

        @Nested
        @DisplayName("Part D: Export Tests")
        class ExportTests {

            @BeforeEach
            void setUpMonthlyData() {
                service.addRating("A1", 5, LocalDateTime.of(2024, 1, 15, 10, 0));
                service.addRating("A2", 4, LocalDateTime.of(2024, 1, 20, 10, 0));
                service.addRating("A1", 3, LocalDateTime.of(2024, 2, 10, 10, 0));
            }

            @Test
            @DisplayName("Should export to CSV format")
            void testCSVExport() {
                String csv = service.exportMonthlyRatings(new CSVExporter());

                assertTrue(csv.contains("Month,AgentId,AgentName,AverageRating,TotalRatings"));
                assertTrue(csv.contains("2024-01"));
                assertTrue(csv.contains("Alice"));
                assertTrue(csv.contains("Bob"));
            }

            @Test
            @DisplayName("Should export to JSON format")
            void testJSONExport() {
                String json = service.exportMonthlyRatings(new JSONExporter());

                assertTrue(json.contains("\"2024-01\""));
                assertTrue(json.contains("\"agentName\": \"Alice\""));
                assertTrue(json.contains("\"averageRating\":"));
            }

            @Test
            @DisplayName("Should get monthly ratings grouped by month")
            void testGetMonthlyRatings() {
                Map<String, List<AgentStats>> monthly = service.getMonthlyRatings();

                assertEquals(2, monthly.size());
                assertTrue(monthly.containsKey("2024-01"));
                assertTrue(monthly.containsKey("2024-02"));
                assertEquals(2, monthly.get("2024-01").size());  // Alice and Bob
                assertEquals(1, monthly.get("2024-02").size());  // Only Alice
            }
        }

        @Nested
        @DisplayName("Part E: Unsorted and Total Tests")
        class UnsortedAndTotalTests {

            @BeforeEach
            void setUpRatings() {
                service.addRating("A1", 5);
                service.addRating("A1", 3);
                service.addRating("A2", 4);
            }

            @Test
            @DisplayName("Should return unsorted list")
            void testUnsorted() {
                List<AgentStats> unsorted = service.getAgentsUnsorted();

                assertEquals(2, unsorted.size());
                // Just verify all agents are present, order doesn't matter
                assertTrue(unsorted.stream().anyMatch(s -> s.getAgentName().equals("Alice")));
                assertTrue(unsorted.stream().anyMatch(s -> s.getAgentName().equals("Bob")));
            }

            @Test
            @DisplayName("Should return total score for each agent")
            void testTotalRatings() {
                List<AgentStats> totals = service.getAgentTotalRatings();

                AgentStats aliceStats = totals.stream()
                        .filter(s -> s.getAgentName().equals("Alice"))
                        .findFirst().orElse(null);

                assertNotNull(aliceStats);
                assertEquals(8, aliceStats.getTotalScore());  // 5 + 3
                assertEquals(2, aliceStats.getTotalRatings());
            }
        }

        @Nested
        @DisplayName("Edge Cases")
        class EdgeCaseTests {

            @Test
            @DisplayName("Should handle agent with no ratings")
            void testAgentWithNoRatings() {
                service.addRating("A1", 5);

                List<AgentStats> stats = service.getAgentsSortedByRating();

                // Only A1 should be in the list (agents with no ratings are excluded)
                assertEquals(1, stats.size());
                assertEquals("Alice", stats.get(0).getAgentName());
            }

            @Test
            @DisplayName("Should handle multiple ratings for same agent")
            void testMultipleRatings() {
                service.addRating("A1", 5);
                service.addRating("A1", 4);
                service.addRating("A1", 3);
                service.addRating("A1", 2);
                service.addRating("A1", 1);

                List<AgentStats> stats = service.getAgentsSortedByRating();

                assertEquals(3.0, stats.get(0).getAverageRating(), 0.01);
                assertEquals(5, stats.get(0).getTotalRatings());
            }
        }
    }
