package practice.atlassian.data_structures.q6_job_interval_reporting;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class TestSolution {
    
    private Solution.CIPipelineAnalyzer analyzer;
    private Solution.SimpleIntervalMerger simpleMerger;
    
     @BeforeEach
    void setUp() {
        analyzer = new Solution.CIPipelineAnalyzer();
        simpleMerger = new Solution.SimpleIntervalMerger();
    }
    
     @Test
    // @DisplayName("Test basic interval merging from problem description")
    void testBasicIntervalMerging() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(2, 5),
            new TimeInterval(12, 15),
            new TimeInterval(4, 8)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        List<TimeInterval> expected = Arrays.asList(
            new TimeInterval(2, 8),
            new TimeInterval(12, 15)
        );
        
        assertEqualsLocal(expected, report.intervals);
    }
    
     @Test
    // @DisplayName("Test simple merger consistency")
    void testSimpleMergerConsistency() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(2, 5),
            new TimeInterval(12, 15),
            new TimeInterval(4, 8)
        );
        
        List<TimeInterval> simpleResult = simpleMerger.mergeIntervals(intervals);
        IntervalReport sweepResult = analyzer.analyzeJobIntervals(intervals);
        
        assertEqualsLocal(simpleResult, sweepResult.intervals);
    }
    
    // @Test
    // @DisplayName("Test non-overlapping intervals")
    void testNonOverlappingIntervals() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 2),
            new TimeInterval(3, 4),
            new TimeInterval(5, 6)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        assertEqualsLocal(intervals, report.intervals);
    }
    
    // @Test
    // @DisplayName("Test completely overlapping intervals")
    void testCompletelyOverlappingIntervals() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 10),
            new TimeInterval(2, 8),
            new TimeInterval(3, 5)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        List<TimeInterval> expected = Arrays.asList(new TimeInterval(1, 10));
        
        assertEqualsLocal(expected, report.intervals);
    }
    
     @Test
    // @DisplayName("Test adjacent intervals")
    void testAdjacentIntervals() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 3),
            new TimeInterval(3, 5),
            new TimeInterval(5, 7)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        List<TimeInterval> expected = Arrays.asList(new TimeInterval(1, 7));
        
        assertEqualsLocal(expected, report.intervals);
    }
    
     @Test
    // @DisplayName("Test empty input")
    void testEmptyInput() {
        List<TimeInterval> intervals = new ArrayList<>();
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        assertTrueLocal(report.intervals.isEmpty());
        assertEqualsLocal(0, report.maxConcurrentJobs);
        assertTrueLocal(report.busiestWindows.isEmpty());
    }
    
    // @Test
    // @DisplayName("Test single interval")
    void testSingleInterval() {
        List<TimeInterval> intervals = Arrays.asList(new TimeInterval(5, 10));
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        assertEqualsLocal(intervals, report.intervals);
        assertEqualsLocal(1, report.maxConcurrentJobs);
        assertEqualsLocal(intervals, report.busiestWindows);
    }
    
    // @Test
    // @DisplayName("Test intervals with at least two jobs")
    void testIntervalsWithAtLeastTwoJobs() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 5),
            new TimeInterval(3, 7),
            new TimeInterval(6, 9),
            new TimeInterval(10, 12)
        );
        
        List<TimeInterval> result = analyzer.findIntervalsWithAtLeastNJobs(intervals, 2);
        
        // Should have overlap from 3-5 and 6-7
        List<TimeInterval> expected = Arrays.asList(
            new TimeInterval(3, 5),
            new TimeInterval(6, 7)
        );
        
        assertEqualsLocal(expected, result);
    }
    
    // @Test
    // @DisplayName(\"Test maximum concurrent jobs calculation\")
    void testMaxConcurrentJobs() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 4),
            new TimeInterval(2, 5),
            new TimeInterval(3, 6),
            new TimeInterval(7, 8)
        );
        
        int maxJobs = analyzer.getMaxConcurrentJobs(intervals);
        assertEqualsLocal(3, maxJobs); // Jobs overlap at time 3-4
    }
    
    // @Test
    // @DisplayName(\"Test busiest windows identification\")
    void testBusiestWindows() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 5),
            new TimeInterval(2, 6),
            new TimeInterval(3, 4), // Creates 3-job overlap
            new TimeInterval(10, 15),
            new TimeInterval(11, 16),
            new TimeInterval(12, 13) // Another 3-job overlap
        );
        
        List<TimeInterval> busiest = analyzer.findBusiestWindows(intervals);
        
        // Should find the two periods with 3 concurrent jobs
        assertEqualsLocal(2, busiest.size());
        assertTrueLocal(busiest.contains(new TimeInterval(3, 4)));
        assertTrueLocal(busiest.contains(new TimeInterval(12, 13)));
    }
    
    // @Test
    // @DisplayName(\"Test zero-duration intervals\")
    void testZeroDurationIntervals() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 1),
            new TimeInterval(2, 5),
            new TimeInterval(3, 3)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        // Zero-duration intervals shouldn't affect the result significantly
        assertFalseLocal(report.intervals.isEmpty());
        assertTrueLocal(report.maxConcurrentJobs >= 1);
    }
    
    // @Test
    // @DisplayName(\"Test unsorted input\")
    void testUnsortedInput() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(10, 15),
            new TimeInterval(2, 5),
            new TimeInterval(1, 3),
            new TimeInterval(12, 18)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        // Should handle unsorted input correctly
        List<TimeInterval> expected = Arrays.asList(
            new TimeInterval(1, 5),
            new TimeInterval(10, 18)
        );
        
        assertEqualsLocal(expected, report.intervals);
    }
    
    // @Test
    // @DisplayName(\"Test large overlapping groups\")
    void testLargeOverlappingGroups() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 10),
            new TimeInterval(2, 11),
            new TimeInterval(3, 12),
            new TimeInterval(4, 13),
            new TimeInterval(5, 14)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        // All should merge into one large interval
        assertEqualsLocal(1, report.intervals.size());
        assertEqualsLocal(new TimeInterval(1, 14), report.intervals.get(0));
        assertEqualsLocal(5, report.maxConcurrentJobs);
    }
    
    // @Test
    // @DisplayName(\"Test intervals with same start times\")
    void testIntervalsWithSameStartTimes() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(5, 8),
            new TimeInterval(5, 10),
            new TimeInterval(5, 12)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        assertEqualsLocal(1, report.intervals.size());
        assertEqualsLocal(new TimeInterval(5, 12), report.intervals.get(0));
        assertEqualsLocal(3, report.maxConcurrentJobs);
    }
    
    // @Test
    // @DisplayName(\"Test intervals with same end times\")
    void testIntervalsWithSameEndTimes() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 10),
            new TimeInterval(3, 10),
            new TimeInterval(5, 10)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        assertEqualsLocal(1, report.intervals.size());
        assertEqualsLocal(new TimeInterval(1, 10), report.intervals.get(0));
        assertEqualsLocal(3, report.maxConcurrentJobs);
    }
    
    // @Test
    // @DisplayName(\"Test result validation\")
    void testResultValidation() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 5),
            new TimeInterval(3, 8),
            new TimeInterval(10, 15),
            new TimeInterval(12, 20)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        // Validate merged intervals are properly formatted
        assertTrueLocal(Solution.IntervalValidator.validateMergedIntervals(report.intervals));
        
        // Compare with simple merger
        List<TimeInterval> simpleResult = simpleMerger.mergeIntervals(intervals);
        assertTrueLocal(Solution.IntervalValidator.compareMergeResults(simpleResult, report.intervals));
    }
    
    // @Test
    // @DisplayName(\"Test performance with many intervals\")
    void testPerformanceWithManyIntervals() {
        Random random = new Random(42);
        List<TimeInterval> intervals = new ArrayList<>();
        
        // Generate 1000 random intervals
        for (int i = 0; i < 1000; i++) {
            int start = random.nextInt(500);
            int end = start + random.nextInt(50) + 1;
            intervals.add(new TimeInterval(start, end));
        }
        
        long startTime = System.currentTimeMillis();
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        long endTime = System.currentTimeMillis();
        
        // Should complete quickly
        assertTrueLocal(endTime - startTime < 1000); // Less than 1 second
        
        // Results should be valid
        assertNotNullLocal(report.intervals);
        assertTrueLocal(report.maxConcurrentJobs >= 0);
        assertNotNullLocal(report.busiestWindows);
    }
    
    // @Test
    // @DisplayName(\"Test edge case - all intervals identical\")
    void testAllIntervalsIdentical() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(5, 10),
            new TimeInterval(5, 10),
            new TimeInterval(5, 10)
        );
        
        IntervalReport report = analyzer.analyzeJobIntervals(intervals);
        
        assertEqualsLocal(1, report.intervals.size());
        assertEqualsLocal(new TimeInterval(5, 10), report.intervals.get(0));
        assertEqualsLocal(3, report.maxConcurrentJobs);
        assertEqualsLocal(1, report.busiestWindows.size());
    }
    
    // @Test
    // @DisplayName(\"Test null input handling\")
    void testNullInputHandling() {
        IntervalReport report = analyzer.analyzeJobIntervals(null);
        
        assertTrueLocal(report.intervals.isEmpty());
        assertEqualsLocal(0, report.maxConcurrentJobs);
        assertTrueLocal(report.busiestWindows.isEmpty());
    }
    
    // @Test
    // @DisplayName(\"Test minimum jobs filter edge cases\")
    void testMinimumJobsFilterEdgeCases() {
        List<TimeInterval> intervals = Arrays.asList(
            new TimeInterval(1, 5),
            new TimeInterval(3, 7)
        );
        
        // Test with 0 minimum jobs (should return nothing meaningful)
        List<TimeInterval> result0 = analyzer.findIntervalsWithAtLeastNJobs(intervals, 0);
        assertTrueLocal(result0.isEmpty());
        
        // Test with 1 minimum job
        List<TimeInterval> result1 = analyzer.findIntervalsWithAtLeastNJobs(intervals, 1);
        assertEqualsLocal(1, result1.size());
        assertEqualsLocal(new TimeInterval(1, 7), result1.get(0));
        
        // Test with 2 minimum jobs
        List<TimeInterval> result2 = analyzer.findIntervalsWithAtLeastNJobs(intervals, 2);
        assertEqualsLocal(1, result2.size());
        assertEqualsLocal(new TimeInterval(3, 5), result2.get(0));
        
        // Test with 3 minimum jobs (impossible)
        List<TimeInterval> result3 = analyzer.findIntervalsWithAtLeastNJobs(intervals, 3);
        assertTrueLocal(result3.isEmpty());
    }
    
    // Helper assertion methods
    private static void assertEqualsLocal(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private static void assertTrueLocal(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true, but was false");
        }
    }
    
    private static void assertFalseLocal(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false, but was true");
        }
    }
    
    private static void assertNotNullLocal(Object actual) {
        if (actual == null) {
            throw new AssertionError("Expected non-null, but was null");
        }
    }
    
    private static void assertNullLocal(Object actual) {
        if (actual != null) {
            throw new AssertionError("Expected null, but was: " + actual);
        }
    }
}
