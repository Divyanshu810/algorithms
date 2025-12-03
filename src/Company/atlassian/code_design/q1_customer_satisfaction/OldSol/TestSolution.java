//// import org.junit.jupiter.api.Test;
//// import org.junit.jupiter.api.DisplayName;
//// import org.junit.jupiter.api.BeforeEach;
//import practice.atlassian.code_design.q1_customer_satisfaction.Solution;
//
//// import static org.junit.jupiter.api.Assertions.*;
//import java.time.LocalDate;
//import java.time.YearMonth;
//import java.util.*;
//
//public class TestSolution {
//
//    private Solution.CustomerSatisfactionService service;
//
//    // @BeforeEach
//    void setUp() {
//        service = new Solution.CustomerSatisfactionService();
//
//        // Add test agents
//        service.addAgent("agent1", "Alice Smith", LocalDate.of(2023, 1, 15));
//        service.addAgent("agent2", "Bob Johnson", LocalDate.of(2023, 2, 1));
//        service.addAgent("agent3", "Carol Davis", LocalDate.of(2023, 1, 10));
//    }
//
//    // @Test
//    // @DisplayName("Test basic rating submission and retrieval")
//    void testBasicRatingSubmission() {
//        service.submitRating("agent1", 5, "customer1");
//        service.submitRating("agent1", 4, "customer2");
//
//        Agent agent = service.getAgent("agent1");
//        assertEqualsLocal(4.5, agent.getAverageRating(), 0.01);
//        assertEqualsLocal(2, agent.getTotalRatings());
//    }
//
//    // @Test
//    // @DisplayName("Test agent ordering by average rating")
//    void testAgentOrdering() {
//        service.submitRating("agent1", 5, "customer1");
//        service.submitRating("agent2", 3, "customer2");
//        service.submitRating("agent3", 4, "customer3");
//
//        List<Agent> ordered = service.getAllAgentsOrderedByRating();
//
//        assertEqualsLocal("agent1", ordered.get(0).getAgentId()); // Highest rating (5.0)
//        assertEqualsLocal("agent3", ordered.get(1).getAgentId()); // Middle rating (4.0)
//        assertEqualsLocal("agent2", ordered.get(2).getAgentId()); // Lowest rating (3.0)
//    }
//
//    // @Test
//    // @DisplayName("Test tie-breaking strategies")
//    void testTieBreakingStrategies() {
//        // Create tie situation - both agents have same average rating
//        service.submitRating("agent1", 4, "customer1");
//        service.submitRating("agent2", 4, "customer2");
//
//        // Test AGENT_ID_ASC (default)
//        service.setTieBreakingStrategy(TieBreakingStrategy.AGENT_ID_ASC);
//        List<Agent> orderedById = service.getAllAgentsOrderedByRating();
//        assertEqualsLocal("agent1", orderedById.get(0).getAgentId());
//        assertEqualsLocal("agent2", orderedById.get(1).getAgentId());
//
//        // Test AGENT_NAME_ASC
//        service.setTieBreakingStrategy(TieBreakingStrategy.AGENT_NAME_ASC);
//        List<Agent> orderedByName = service.getAllAgentsOrderedByRating();
//        assertEqualsLocal("Alice Smith", orderedByName.get(0).getName()); // Alice < Bob alphabetically
//        assertEqualsLocal("Bob Johnson", orderedByName.get(1).getName());
//    }
//
//    // @Test
//    // @DisplayName("Test monthly rating tracking")
//    void testMonthlyRatingTracking() {
//        LocalDate jan = LocalDate.of(2024, 1, 15);
//        LocalDate feb = LocalDate.of(2024, 2, 15);
//
//        service.submitRating("agent1", 5, "customer1", jan);
//        service.submitRating("agent1", 3, "customer2", feb);
//
//        Agent agent = service.getAgent("agent1");
//
//        assertEqualsLocal(5.0, agent.getAverageRatingForMonth(YearMonth.of(2024, 1)), 0.01);
//        assertEqualsLocal(3.0, agent.getAverageRatingForMonth(YearMonth.of(2024, 2)), 0.01);
//        assertEqualsLocal(4.0, agent.getAverageRating(), 0.01); // Overall average
//    }
//
//    // @Test
//    // @DisplayName("Test best agents for specific month")
//    void testBestAgentsForMonth() {
//        LocalDate jan = LocalDate.of(2024, 1, 15);
//
//        service.submitRating("agent1", 5, "customer1", jan);
//        service.submitRating("agent2", 3, "customer2", jan);
//        service.submitRating("agent3", 4, "customer3", jan);
//
//        List<Agent> januaryBest = service.getBestAgentsForMonth(YearMonth.of(2024, 1));
//
//        assertEqualsLocal(3, januaryBest.size());
//        assertEqualsLocal("agent1", januaryBest.get(0).getAgentId()); // Highest in January
//        assertEqualsLocal("agent3", januaryBest.get(1).getAgentId());
//        assertEqualsLocal("agent2", januaryBest.get(2).getAgentId());
//    }
//
//    // @Test
//    // @DisplayName("Test CSV export format")
//    void testCSVExport() {
//        service.submitRating("agent1", 5, "customer1");
//        service.submitRating("agent2", 4, "customer2");
//
//        ExportFormat csvFormat = new CSVExportFormat();
//        String csv = service.exportAgentRatings(csvFormat);
//
//        assertTrueLocal(csv.contains("AgentId,Name,OverallAverageRating,TotalRatings"));
//        assertTrueLocal(csv.contains("agent1,Alice Smith,5.00,1"));
//        assertTrueLocal(csv.contains("agent2,Bob Johnson,4.00,1"));
//    }
//
//    // @Test
//    // @DisplayName("Test JSON export format")
//    void testJSONExport() {
//        service.submitRating("agent1", 5, "customer1");
//
//        ExportFormat jsonFormat = new JSONExportFormat();
//        String json = service.exportAgentRatings(jsonFormat);
//
//        assertTrueLocal(json.contains("\\"agents\\": ["));
//        assertTrueLocal(json.contains("\\"agentId\\": \\"agent1\\""));
//        assertTrueLocal(json.contains("\\"name\\": \\"Alice Smith\\""));
//        assertTrueLocal(json.contains("\\"averageRating\\": 5.00"));
//    }
//
//    // @Test
//    // @DisplayName("Test XML export format")
//    void testXMLExport() {
//        service.submitRating("agent1", 4, "customer1");
//
//        ExportFormat xmlFormat = new XMLExportFormat();
//        String xml = service.exportAgentRatings(xmlFormat);
//
//        assertTrueLocal(xml.contains("<?xml version=\\"1.0\\" encoding=\\"UTF-8\\"?>"));
//        assertTrueLocal(xml.contains("<agents>"));
//        assertTrueLocal(xml.contains("<agentId>agent1</agentId>"));
//        assertTrueLocal(xml.contains("<name>Alice Smith</name>"));
//        assertTrueLocal(xml.contains("<averageRating>4.00</averageRating>"));
//    }
//
//    // @Test
//    // @DisplayName("Test monthly export")
//    void testMonthlyExport() {
//        LocalDate jan = LocalDate.of(2024, 1, 15);
//        service.submitRating("agent1", 5, "customer1", jan);
//
//        ExportFormat csvFormat = new CSVExportFormat();
//        String csv = service.exportMonthlyAgentRatings(YearMonth.of(2024, 1), csvFormat);
//
//        assertTrueLocal(csv.contains("AgentId,Name,MonthlyAverageRating,MonthlyTotalRatings"));
//        assertTrueLocal(csv.contains("agent1,Alice Smith,5.00,1"));
//    }
//
//    // @Test
//    // @DisplayName("Test agents with total rating sum")
//    void testAgentsWithTotalRating() {
//        service.submitRating("agent1", 5, "customer1");
//        service.submitRating("agent1", 5, "customer2"); // Total: 10
//        service.submitRating("agent2", 4, "customer3");
//        service.submitRating("agent2", 4, "customer4");
//        service.submitRating("agent2", 4, "customer5"); // Total: 12
//
//        List<Agent> byTotalRating = service.getAgentsWithTotalRating();
//
//        assertEqualsLocal("agent2", byTotalRating.get(0).getAgentId()); // Higher total sum (12)
//        assertEqualsLocal("agent1", byTotalRating.get(1).getAgentId()); // Lower total sum (10)
//    }
//
//    // @Test
//    // @DisplayName("Test unsorted results")
//    void testUnsortedResults() {
//        service.submitRating("agent1", 5, "customer1");
//        service.submitRating("agent2", 3, "customer2");
//        service.submitRating("agent3", 4, "customer3");
//
//        List<Agent> unsorted = service.getAllAgentsOrderedByRating(true);
//        List<Agent> sorted = service.getAllAgentsOrderedByRating(false);
//
//        // Unsorted should not necessarily be in rating order
//        assertEqualsLocal(3, unsorted.size());
//        assertEqualsLocal(3, sorted.size());
//
//        // But sorted should be in proper order
//        assertTrueLocal(sorted.get(0).getAverageRating() >= sorted.get(1).getAverageRating());
//        assertTrueLocal(sorted.get(1).getAverageRating() >= sorted.get(2).getAverageRating());
//    }
//
//    // @Test
//    // @DisplayName("Test rating validation")
//    void testRatingValidation() {
//        // Test invalid ratings
//// Test commented out - assertThrows not available
//            service.submitRating("agent1", 0, "customer1");
//
//// Test commented out - assertThrows not available
//            service.submitRating("agent1", 6, "customer1");
//
//        // Test valid ratings
//// Test commented out - assertDoesNotThrow not available
//            service.submitRating("agent1", 1, "customer1");
//            service.submitRating("agent1", 5, "customer2");
//    }
//
//    // @Test
//    // @DisplayName("Test nonexistent agent")
//    void testNonexistentAgent() {
//// Test commented out - assertThrows not available
//            service.submitRating("nonexistent", 5, "customer1");
//    }
//
//    // @Test
//    // @DisplayName("Test duplicate agent addition")
//    void testDuplicateAgentAddition() {
//// Test commented out - assertThrows not available
//            service.addAgent("agent1", "Duplicate Alice", LocalDate.now());
//    }
//
//    // @Test
//    // @DisplayName("Test agent with no ratings")
//    void testAgentWithNoRatings() {
//        Agent agent = service.getAgent("agent1");
//
//        assertEqualsLocal(0.0, agent.getAverageRating(), 0.01);
//        assertEqualsLocal(0, agent.getTotalRatings());
//        assertEqualsLocal(0.0, agent.getTotalRatingSum(), 0.01);
//    }
//
//    // @Test
//    // @DisplayName("Test monthly data for nonexistent month")
//    void testMonthlyDataForNonexistentMonth() {
//        service.submitRating("agent1", 5, "customer1", LocalDate.of(2024, 1, 15));
//
//        Agent agent = service.getAgent("agent1");
//
//        assertEqualsLocal(0.0, agent.getAverageRatingForMonth(YearMonth.of(2024, 2)), 0.01);
//        assertEqualsLocal(0, agent.getTotalRatingsForMonth(YearMonth.of(2024, 2)));
//        assertEqualsLocal(0.0, agent.getTotalRatingSumForMonth(YearMonth.of(2024, 2)), 0.01);
//    }
//
//    // @Test
//    // @DisplayName("Test active months tracking")
//    void testActiveMonthsTracking() {
//        LocalDate jan = LocalDate.of(2024, 1, 15);
//        LocalDate feb = LocalDate.of(2024, 2, 15);
//        LocalDate mar = LocalDate.of(2024, 3, 15);
//
//        service.submitRating("agent1", 5, "customer1", jan);
//        service.submitRating("agent1", 4, "customer2", feb);
//        service.submitRating("agent2", 3, "customer3", mar);
//
//        Set<YearMonth> allActiveMonths = service.getAllActiveMonths();
//        assertEqualsLocal(3, allActiveMonths.size());
//        assertTrueLocal(allActiveMonths.contains(YearMonth.of(2024, 1)));
//        assertTrueLocal(allActiveMonths.contains(YearMonth.of(2024, 2)));
//        assertTrueLocal(allActiveMonths.contains(YearMonth.of(2024, 3)));
//
//        Agent agent1 = service.getAgent("agent1");
//        Set<YearMonth> agent1Months = agent1.getActiveMonths();
//        assertEqualsLocal(2, agent1Months.size());
//        assertTrueLocal(agent1Months.contains(YearMonth.of(2024, 1)));
//        assertTrueLocal(agent1Months.contains(YearMonth.of(2024, 2)));
//    }
//
//    // @Test
//    // @DisplayName("Test rating trend for agent")
//    void testRatingTrendForAgent() {
//        LocalDate jan = LocalDate.of(2024, 1, 15);
//        LocalDate feb = LocalDate.of(2024, 2, 15);
//        LocalDate mar = LocalDate.of(2024, 3, 15);
//
//        service.submitRating("agent1", 3, "customer1", jan);
//        service.submitRating("agent1", 4, "customer2", feb);
//        service.submitRating("agent1", 5, "customer3", mar);
//
//        Map<YearMonth, Double> trend = service.getAverageRatingTrendForAgent("agent1");
//
//        assertEqualsLocal(3, trend.size());
//        assertEqualsLocal(3.0, trend.get(YearMonth.of(2024, 1)), 0.01);
//        assertEqualsLocal(4.0, trend.get(YearMonth.of(2024, 2)), 0.01);
//        assertEqualsLocal(5.0, trend.get(YearMonth.of(2024, 3)), 0.01);
//    }
//
//    // @Test
//    // @DisplayName("Test all agent IDs retrieval")
//    void testGetAllAgentIds() {
//        Set<String> agentIds = service.getAllAgentIds();
//
//        assertEqualsLocal(3, agentIds.size());
//        assertTrueLocal(agentIds.contains("agent1"));
//        assertTrueLocal(agentIds.contains("agent2"));
//        assertTrueLocal(agentIds.contains("agent3"));
//    }
//
//    // @Test
//    // @DisplayName("Test complex tie-breaking scenarios")
//    void testComplexTieBreaking() {
//        // Create complex tie scenario
//        service.submitRating("agent1", 4, "customer1");
//        service.submitRating("agent2", 4, "customer2");
//        service.submitRating("agent3", 4, "customer3");
//
//        // Test TOTAL_RATINGS_DESC tie-breaking
//        service.submitRating("agent2", 4, "customer4"); // agent2 now has 2 ratings
//        service.setTieBreakingStrategy(TieBreakingStrategy.TOTAL_RATINGS_DESC);
//
//        List<Agent> orderedByTotalRatings = service.getAllAgentsOrderedByRating();
//        assertEqualsLocal("agent2", orderedByTotalRatings.get(0).getAgentId()); // Has most ratings
//    }
//
//    // @Test
//    // @DisplayName("Test performance with many ratings")
//    void testPerformanceWithManyRatings() {
//        // Add many ratings to test performance
//        Random random = new Random(42);
//
//        long startTime = System.currentTimeMillis();
//
//        for (int i = 0; i < 1000; i++) {
//            String agentId = "agent" + (random.nextInt(3) + 1);
//            int rating = random.nextInt(5) + 1;
//            service.submitRating(agentId, rating, "customer" + i);
//        }
//
//        List<Agent> ordered = service.getAllAgentsOrderedByRating();
//
//        long endTime = System.currentTimeMillis();
//
//        // Should complete quickly
//        assertTrueLocal(endTime - startTime < 1000); // Less than 1 second
//        assertEqualsLocal(3, ordered.size());
//
//        // Verify ordering is correct
//        for (int i = 0; i < ordered.size() - 1; i++) {
//            assertTrueLocal(ordered.get(i).getAverageRating() >= ordered.get(i + 1).getAverageRating());
//        }
//    }
//
//    // Helper assertion methods
//    private static void assertEqualsLocal(Object expected, Object actual) {
//        if (!expected.equals(actual)) {
//            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
//        }
//    }
//
//    private static void assertTrueLocal(boolean condition) {
//        if (!condition) {
//            throw new AssertionError("Expected true, but was false");
//        }
//    }
//
//    private static void assertFalseLocal(boolean condition) {
//        if (condition) {
//            throw new AssertionError("Expected false, but was true");
//        }
//    }
//
//    private static void assertNotNullLocal(Object actual) {
//        if (actual == null) {
//            throw new AssertionError("Expected non-null, but was null");
//        }
//    }
//
//    private static void assertNullLocal(Object actual) {
//        if (actual != null) {
//            throw new AssertionError("Expected null, but was: " + actual);
//        }
//    }
//}
