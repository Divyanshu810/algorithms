package Company.atlassian.data_structures.q6_job_interval_reporting.NewSol;

import java.util.*;

/**
 * Job Interval Reporting - CI Pipeline Analysis
 *
 * ===================================================================================
 * PROBLEM:
 * ===================================================================================
 * - Given list of CI pipeline time windows {startTime, endTime}
 * - Find various reporting metrics for cost optimization
 *
 * ===================================================================================
 * PARTS:
 * ===================================================================================
 *
 * Part 1: Find time windows where at least ONE pipeline is running
 *         (Merge overlapping intervals)
 *
 * Part 2: Find time windows where at least TWO pipelines are running
 *         (Find overlapping regions)
 *
 * Part 3: Find the busiest window(s) with MAXIMUM pipelines running
 *         (Sweep line to find peak concurrency)
 *
 * ===================================================================================
 */
public class JobIntervalReporting {

    // ==================== DATA CLASS ====================

    static class Interval {
        int start;
        int end;

        Interval(int start, int end) {
            this.start = start;
            this.end = end;
        }

        public String toString() {
            return "{" + start + ", " + end + "}";
        }
    }

    // ==================== PART 1: Merge Intervals (At Least 1 Pipeline) ====================
    /**
     * Find time windows where at least ONE pipeline is running.
     * This is the classic "Merge Overlapping Intervals" problem.
     *
     * Approach:
     * 1. Sort intervals by start time
     * 2. Iterate and merge overlapping intervals
     *
     * Time: O(n log n) for sorting
     * Space: O(n) for result
     *
     * Example:
     * Input:  [{2,5}, {12,15}, {4,8}]
     * Sorted: [{2,5}, {4,8}, {12,15}]
     * Merge:  [{2,8}, {12,15}]  (2-5 and 4-8 overlap)
     */
    public static List<Interval> mergeIntervals(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return new ArrayList<>();
        }

        // Sort by start time
        List<Interval> sorted = new ArrayList<>(intervals);
        sorted.sort((a, b) -> a.start - b.start);

        List<Interval> result = new ArrayList<>();
        Interval current = sorted.get(0);

        for (int i = 1; i < sorted.size(); i++) {
            Interval next = sorted.get(i);

            if (next.start <= current.end) {
                // Overlapping - extend current interval
                current.end = Math.max(current.end, next.end);
            } else {
                // No overlap - add current to result, start new interval
                result.add(current);
                current = next;
            }
        }

        // Don't forget the last interval
        result.add(current);

        return result;
    }

    // ==================== PART 2: At Least 2 Pipelines Running ====================
    /**
     * Find time windows where at least N pipelines are running simultaneously.
     *
     * Approach 1: Sweep Line with Sorting - O(n log n)
     * Approach 2: Difference Array (No Sorting) - O(n + T) where T = time range
     */

    // Approach 1: Sweep Line with Sorting - O(n log n)
    public static List<Interval> findAtLeastNPipelines_SweepLine(List<Interval> intervals, int n) {
        if (intervals == null || intervals.isEmpty() || n <= 0) {
            return new ArrayList<>();
        }

        // Create events: (time, type) where type: +1 = start, -1 = end
        List<int[]> events = new ArrayList<>();

        for (Interval interval : intervals) {
            events.add(new int[]{interval.start, 1});   // Start: +1
            events.add(new int[]{interval.end, -1});    // End: -1
        }

        // Sort by time, then by type (end before start at same time)
        events.sort((a, b) -> {
            if (a[0] != b[0]) {
                return a[0] - b[0];
            }
            return a[1] - b[1];  // -1 before +1
        });

        List<Interval> result = new ArrayList<>();
        int runningCount = 0;
        Integer windowStart = null;

        for (int[] event : events) {
            int time = event[0];
            int type = event[1];

            int prevCount = runningCount;
            runningCount += type;

            // Transition INTO n or more pipelines
            if (prevCount < n && runningCount >= n) {
                windowStart = time;
            }

            // Transition OUT OF n or more pipelines
            if (prevCount >= n && runningCount < n) {
                if (windowStart != null) {
                    result.add(new Interval(windowStart, time));
                    windowStart = null;
                }
            }
        }

        return result;
    }

    // Approach 2: Difference Array (No Sorting) - O(n + T)
    /**
     * Uses difference array technique:
     * 1. Find min and max time
     * 2. Create array where diff[t] = change in count at time t
     * 3. Sweep through array to find windows with count >= n
     *
     * Best when: time range is small
     * Avoid when: time range is huge (e.g., 0 to 10^9)
     */
    public static List<Interval> findAtLeastNPipelines(List<Interval> intervals, int n) {
        if (intervals == null || intervals.isEmpty() || n <= 0) {
            return new ArrayList<>();
        }

        // Step 1: Find min and max time
        int minTime = Integer.MAX_VALUE;
        int maxTime = Integer.MIN_VALUE;

        for (Interval interval : intervals) {
            minTime = Math.min(minTime, interval.start);
            maxTime = Math.max(maxTime, interval.end);
        }

        // Step 2: Create difference array
        int[] diff = new int[maxTime - minTime + 2];  // +2 for safety

        for (Interval interval : intervals) {
            diff[interval.start - minTime] += 1;
            diff[interval.end - minTime] -= 1;
        }

        // Step 3: Sweep through to find windows with count >= n
        List<Interval> result = new ArrayList<>();
        int runningCount = 0;
        Integer windowStart = null;

        for (int i = 0; i < diff.length; i++) {
            int prevCount = runningCount;
            runningCount += diff[i];

            int actualTime = i + minTime;

            // Transition INTO n or more
            if (prevCount < n && runningCount >= n) {
                windowStart = actualTime;
            }

            // Transition OUT OF n or more
            if (prevCount >= n && runningCount < n) {
                if (windowStart != null) {
                    result.add(new Interval(windowStart, actualTime));
                    windowStart = null;
                }
            }
        }

        return result;
    }

    // ==================== PART 3: Busiest Window (Maximum Pipelines) ====================
    /**
     * Find the window(s) where MAXIMUM number of pipelines are running.
     *
     * Approach 1: Sweep Line with Sorting - O(n log n)
     * Approach 2: Difference Array (No Sorting) - O(n + T)
     */

    static class BusiestWindowResult {
        int maxPipelines;
        List<Interval> windows;

        BusiestWindowResult(int maxPipelines, List<Interval> windows) {
            this.maxPipelines = maxPipelines;
            this.windows = windows;
        }
    }

    // Approach 1: Sweep Line with Sorting - O(n log n)
    public static BusiestWindowResult findBusiestWindows_SweepLine(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return new BusiestWindowResult(0, new ArrayList<>());
        }

        // Create events
        List<int[]> events = new ArrayList<>();

        for (Interval interval : intervals) {
            events.add(new int[]{interval.start, 1});
            events.add(new int[]{interval.end, -1});
        }

        // Sort by time, then by type (end before start at same time)
        events.sort((a, b) -> {
            if (a[0] != b[0]) {
                return a[0] - b[0];
            }
            return a[1] - b[1];
        });

        // First pass: find maximum count
        int maxCount = 0;
        int runningCount = 0;

        for (int[] event : events) {
            runningCount += event[1];
            maxCount = Math.max(maxCount, runningCount);
        }

        // Second pass: find all windows with max count
        List<Interval> result = new ArrayList<>();
        runningCount = 0;
        Integer windowStart = null;

        for (int[] event : events) {
            int time = event[0];
            int type = event[1];

            int prevCount = runningCount;
            runningCount += type;

            // Entering max count
            if (prevCount < maxCount && runningCount == maxCount) {
                windowStart = time;
            }

            // Leaving max count
            if (prevCount == maxCount && runningCount < maxCount) {
                if (windowStart != null) {
                    result.add(new Interval(windowStart, time));
                    windowStart = null;
                }
            }
        }

        return new BusiestWindowResult(maxCount, result);
    }

    // Approach 2: Difference Array (No Sorting) - O(n + T)
    public static BusiestWindowResult findBusiestWindows(List<Interval> intervals) {
        if (intervals == null || intervals.isEmpty()) {
            return new BusiestWindowResult(0, new ArrayList<>());
        }

        // Step 1: Find min and max time
        int minTime = Integer.MAX_VALUE;
        int maxTime = Integer.MIN_VALUE;

        for (Interval interval : intervals) {
            minTime = Math.min(minTime, interval.start);
            maxTime = Math.max(maxTime, interval.end);
        }

        // Step 2: Create difference array
        int[] diff = new int[maxTime - minTime + 2];

        for (Interval interval : intervals) {
            diff[interval.start - minTime] += 1;
            diff[interval.end - minTime] -= 1;
        }

        // Step 3: First pass - find max count
        int maxCount = 0;
        int runningCount = 0;

        for (int change : diff) {
            runningCount += change;
            maxCount = Math.max(maxCount, runningCount);
        }

        // Step 4: Second pass - find windows with max count
        List<Interval> result = new ArrayList<>();
        runningCount = 0;
        Integer windowStart = null;

        for (int i = 0; i < diff.length; i++) {
            int prevCount = runningCount;
            runningCount += diff[i];

            int actualTime = i + minTime;

            // Entering max count
            if (prevCount < maxCount && runningCount == maxCount) {
                windowStart = actualTime;
            }

            // Leaving max count
            if (prevCount == maxCount && runningCount < maxCount) {
                if (windowStart != null) {
                    result.add(new Interval(windowStart, actualTime));
                    windowStart = null;
                }
            }
        }

        return new BusiestWindowResult(maxCount, result);
    }

    // ==================== BONUS: Count Pipelines at Any Time ====================
    /**
     * Given a specific time, return how many pipelines are running.
     *
     * Approach: Binary search on sorted events
     * Time: O(log n) per query after O(n log n) preprocessing
     */
    static class PipelineCounter {
        private List<int[]> events;  // Sorted (time, cumulativeCount)

        public PipelineCounter(List<Interval> intervals) {
            // Create and sort events
            List<int[]> rawEvents = new ArrayList<>();
            for (Interval interval : intervals) {
                rawEvents.add(new int[]{interval.start, 1});
                rawEvents.add(new int[]{interval.end, -1});
            }

            rawEvents.sort((a, b) -> {
                if (a[0] != b[0]) return a[0] - b[0];
                return a[1] - b[1];
            });

            // Build cumulative count
            events = new ArrayList<>();
            int count = 0;

            for (int[] event : rawEvents) {
                count += event[1];
                events.add(new int[]{event[0], count});
            }
        }

        // O(log n)
        public int countAt(int time) {
            if (events.isEmpty()) return 0;

            // Binary search for largest event time <= given time
            int left = 0, right = events.size() - 1;
            int result = 0;

            while (left <= right) {
                int mid = left + (right - left) / 2;
                if (events.get(mid)[0] <= time) {
                    result = events.get(mid)[1];
                    left = mid + 1;
                } else {
                    right = mid - 1;
                }
            }

            return result;
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Job Interval Reporting ===\n");

        testMergeIntervals();
        testAtLeastTwoPipelines();
        testBusiestWindow();
        testPipelineCounter();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testMergeIntervals() {
        System.out.println("--- Part 1: Merge Intervals (At Least 1 Pipeline) ---");

        /**
         * Input:  [{2,5}, {12,15}, {4,8}]
         * Output: [{2,8}, {12,15}]
         */
        List<Interval> intervals = Arrays.asList(
                new Interval(2, 5),
                new Interval(12, 15),
                new Interval(4, 8)
        );

        System.out.println("Input: " + intervals);
        List<Interval> merged = mergeIntervals(intervals);
        System.out.println("Output: " + merged);

        assertResult(2, merged.size(), "Number of merged intervals");
        assertResult(2, merged.get(0).start, "First interval start");
        assertResult(8, merged.get(0).end, "First interval end");

        // Test with no overlap
        List<Interval> noOverlap = Arrays.asList(
                new Interval(1, 3),
                new Interval(5, 7),
                new Interval(9, 11)
        );
        List<Interval> mergedNoOverlap = mergeIntervals(noOverlap);
        System.out.println("\nNo overlap input: " + noOverlap);
        System.out.println("Output: " + mergedNoOverlap);
        assertResult(3, mergedNoOverlap.size(), "No overlap - same count");

        System.out.println("\nPart 1: PASSED\n");
    }

    private static void testAtLeastTwoPipelines() {
        System.out.println("--- Part 2: At Least 2 Pipelines Running ---");

        /**
         * Input: [{1,5}, {2,6}, {8,10}]
         *
         * Timeline:
         * 1----5
         *   2----6
         *           8--10
         *
         * Count: 1 at t=1, 2 at t=2-5, 1 at t=5-6, 0, 1 at t=8-10
         * Output: [{2,5}] (where count >= 2)
         */
        List<Interval> intervals = Arrays.asList(
                new Interval(1, 5),
                new Interval(2, 6),
                new Interval(8, 10)
        );

        System.out.println("Input: " + intervals);

        // Test both approaches
        List<Interval> resultSweep = findAtLeastNPipelines_SweepLine(intervals, 2);
        List<Interval> resultArray = findAtLeastNPipelines(intervals, 2);

        System.out.println("Sweep Line result: " + resultSweep);
        System.out.println("Diff Array result: " + resultArray);

        assertResult(1, resultSweep.size(), "Sweep Line - one window");
        assertResult(1, resultArray.size(), "Diff Array - one window");
        assertResult(2, resultArray.get(0).start, "Window start");
        assertResult(5, resultArray.get(0).end, "Window end");

        // Test with 3 overlapping
        List<Interval> threeOverlap = Arrays.asList(
                new Interval(1, 10),
                new Interval(2, 8),
                new Interval(3, 6)
        );
        System.out.println("\nInput: " + threeOverlap);
        List<Interval> atLeast3 = findAtLeastNPipelines(threeOverlap, 3);
        System.out.println("At least 3 pipelines: " + atLeast3);
        assertResult(3, atLeast3.get(0).start, "3 overlap start");
        assertResult(6, atLeast3.get(0).end, "3 overlap end");

        System.out.println("\nPart 2: PASSED\n");
    }

    private static void testBusiestWindow() {
        System.out.println("--- Part 3: Busiest Window (Maximum Pipelines) ---");

        /**
         * Input: [{1,5}, {2,6}, {3,7}, {10,12}]
         *
         * Timeline:
         * 1----5
         *   2----6
         *     3----7
         *              10-12
         *
         * Count: 1, 2, 3, 2, 1, 0, 1, 0
         * Max = 3 at window {3, 5}
         */
        List<Interval> intervals = Arrays.asList(
                new Interval(1, 5),
                new Interval(2, 6),
                new Interval(3, 7),
                new Interval(10, 12)
        );

        System.out.println("Input: " + intervals);

        // Test both approaches
        BusiestWindowResult resultSweep = findBusiestWindows_SweepLine(intervals);
        BusiestWindowResult resultArray = findBusiestWindows(intervals);

        System.out.println("Sweep Line - Max: " + resultSweep.maxPipelines + ", Windows: " + resultSweep.windows);
        System.out.println("Diff Array - Max: " + resultArray.maxPipelines + ", Windows: " + resultArray.windows);

        assertResult(3, resultSweep.maxPipelines, "Sweep Line max");
        assertResult(3, resultArray.maxPipelines, "Diff Array max");
        assertResult(3, resultArray.windows.get(0).start, "Busiest start");
        assertResult(5, resultArray.windows.get(0).end, "Busiest end");

        // Test with multiple busiest windows
        List<Interval> multiPeak = Arrays.asList(
                new Interval(1, 3),
                new Interval(2, 4),
                new Interval(6, 8),
                new Interval(7, 9)
        );
        System.out.println("\nInput: " + multiPeak);
        BusiestWindowResult multiResult = findBusiestWindows(multiPeak);
        System.out.println("Max pipelines: " + multiResult.maxPipelines);
        System.out.println("Busiest windows: " + multiResult.windows);
        assertResult(2, multiResult.maxPipelines, "Two peaks max");
        assertResult(2, multiResult.windows.size(), "Two busiest windows");

        System.out.println("\nPart 3: PASSED\n");
    }

    private static void testPipelineCounter() {
        System.out.println("--- Bonus: Pipeline Counter at Any Time ---");

        List<Interval> intervals = Arrays.asList(
                new Interval(1, 5),
                new Interval(2, 6),
                new Interval(3, 7)
        );

        PipelineCounter counter = new PipelineCounter(intervals);

        System.out.println("Input: " + intervals);
        System.out.println("Count at t=0: " + counter.countAt(0));
        System.out.println("Count at t=1: " + counter.countAt(1));
        System.out.println("Count at t=3: " + counter.countAt(3));
        System.out.println("Count at t=5: " + counter.countAt(5));
        System.out.println("Count at t=7: " + counter.countAt(7));

        assertResult(0, counter.countAt(0), "Before any pipeline");
        assertResult(1, counter.countAt(1), "One pipeline at t=1");
        assertResult(3, counter.countAt(3), "Three pipelines at t=3");
        assertResult(2, counter.countAt(5), "Two pipelines at t=5");
        assertResult(0, counter.countAt(7), "Zero after all end");

        System.out.println("\nBonus: PASSED\n");
    }

    private static void assertResult(int expected, int actual, String testName) {
        if (expected != actual) {
            throw new RuntimeException(testName + ": Expected " + expected + " but got " + actual);
        }
        System.out.println("  " + testName + ": " + actual + " ✓");
    }
}