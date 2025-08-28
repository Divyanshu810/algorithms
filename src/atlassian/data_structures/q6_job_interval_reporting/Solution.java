package practice.atlassian.data_structures.q6_job_interval_reporting;

import java.util.*;

class TimeInterval {
    int start;
    int end;
    
    public TimeInterval(int start, int end) {
        this.start = start;
        this.end = end;
    }
    
    @Override
    public String toString() {
        return "{" + start + ", " + end + "}";
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        TimeInterval that = (TimeInterval) obj;
        return start == that.start && end == that.end;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(start, end);
    }
}

class TimeEvent {
    int time;
    int type; // +1 for start, -1 for end
    
    public TimeEvent(int time, int type) {
        this.time = time;
        this.type = type;
    }
    
    @Override
    public String toString() {
        return "(" + time + ", " + (type > 0 ? "START" : "END") + ")";
    }
}

class IntervalReport {
    List<TimeInterval> intervals;
    int maxConcurrentJobs;
    List<TimeInterval> busiestWindows;
    
    public IntervalReport(List<TimeInterval> intervals, int maxConcurrentJobs, List<TimeInterval> busiestWindows) {
        this.intervals = intervals;
        this.maxConcurrentJobs = maxConcurrentJobs;
        this.busiestWindows = busiestWindows;
    }
    
    @Override
    public String toString() {
        return "Intervals: " + intervals + 
               ", Max concurrent: " + maxConcurrentJobs + 
               ", Busiest windows: " + busiestWindows;
    }
}

public class Solution {
    
    // Approach 1: Simple Sort and Merge (for basic case)
    public static class SimpleIntervalMerger {
        
        public List<TimeInterval> mergeIntervals(List<TimeInterval> intervals) {
            if (intervals == null || intervals.isEmpty()) {
                return new ArrayList<>();
            }
            
            // Sort by start time
            List<TimeInterval> sorted = new ArrayList<>(intervals);
            sorted.sort(Comparator.comparingInt(a -> a.start));
            
            List<TimeInterval> merged = new ArrayList<>();
            TimeInterval current = sorted.get(0);
            
            for (int i = 1; i < sorted.size(); i++) {
                TimeInterval next = sorted.get(i);
                
                if (current.end >= next.start) {
                    // Overlapping intervals - merge them
                    current = new TimeInterval(current.start, Math.max(current.end, next.end));
                } else {
                    // Non-overlapping - add current and move to next
                    merged.add(current);
                    current = next;
                }
            }
            
            merged.add(current);
            return merged;
        }
    }
    
    // Approach 2: Event-Based Timeline Sweep (Recommended)
    public static class CIPipelineAnalyzer {
        
        public IntervalReport analyzeJobIntervals(List<TimeInterval> jobIntervals) {
            if (jobIntervals == null || jobIntervals.isEmpty()) {
                return new IntervalReport(new ArrayList<>(), 0, new ArrayList<>());
            }
            
            // Create timeline events
            List<TimeEvent> events = createTimelineEvents(jobIntervals);
            
            // Sort events (END events before START events for same time)
            events.sort((a, b) -> {
                if (a.time != b.time) return Integer.compare(a.time, b.time);
                return Integer.compare(a.type, b.type); // -1 (END) comes before +1 (START)
            });
            
            // Sweep through timeline
            return sweepTimeline(events);
        }
        
        private List<TimeEvent> createTimelineEvents(List<TimeInterval> intervals) {
            List<TimeEvent> events = new ArrayList<>();
            
            for (TimeInterval interval : intervals) {
                events.add(new TimeEvent(interval.start, 1));  // Job starts
                events.add(new TimeEvent(interval.end, -1));   // Job ends
            }
            
            return events;
        }

        private IntervalReport sweepTimeline(List<TimeEvent> events) {
            List<TimeInterval> atLeastOneJob = new ArrayList<>();
            List<TimeInterval> busiestWindows = new ArrayList<>();

            int currentJobs = 0;
            int maxConcurrentJobs = 0;
            Integer intervalStart = null;
            Integer maxJobsStart = null;

            for (TimeEvent event : events) {
                int prevJobs = currentJobs;
                currentJobs += event.type;

                // Track max concurrent jobs and busiest windows
                if (currentJobs > maxConcurrentJobs) {
                    maxConcurrentJobs = currentJobs;
                    busiestWindows.clear();
                    maxJobsStart = event.time;
                } else if (currentJobs == maxConcurrentJobs && maxJobsStart == null) {
                    maxJobsStart = event.time;
                } else if (currentJobs < maxConcurrentJobs && maxJobsStart != null) {
                    busiestWindows.add(new TimeInterval(maxJobsStart, event.time));
                    maxJobsStart = null;
                }

                // Track intervals with at least one job
                if (prevJobs == 0 && currentJobs > 0) {
                    intervalStart = event.time;
                } else if (prevJobs > 0 && currentJobs == 0 && intervalStart != null) {
                    atLeastOneJob.add(new TimeInterval(intervalStart, event.time));
                    intervalStart = null;
                }
            }
            // Close any open max window at the end
            if (maxJobsStart != null && currentJobs == maxConcurrentJobs) {
                busiestWindows.add(new TimeInterval(maxJobsStart, events.get(events.size() - 1).time));
            }

            return new IntervalReport(atLeastOneJob, maxConcurrentJobs, busiestWindows);
        }

        public List<TimeInterval> findIntervalsWithAtLeastNJobs(List<TimeInterval> jobIntervals, int minJobs) {
            if (jobIntervals == null || jobIntervals.isEmpty() || minJobs <= 0) {
                return new ArrayList<>();
            }
            
            List<TimeEvent> events = createTimelineEvents(jobIntervals);
            events.sort((a, b) -> {
                if (a.time != b.time) return Integer.compare(a.time, b.time);
                return Integer.compare(a.type, b.type);
            });
            
            List<TimeInterval> result = new ArrayList<>();
            int currentJobs = 0;
            Integer intervalStart = null;
            
            for (TimeEvent event : events) {
                int prevJobs = currentJobs;
                currentJobs += event.type;
                
                if (prevJobs < minJobs && currentJobs >= minJobs) {
                    intervalStart = event.time;
                } else if (prevJobs >= minJobs && currentJobs < minJobs) {
                    if (intervalStart != null) {
                        result.add(new TimeInterval(intervalStart, event.time));
                        intervalStart = null;
                    }
                }
            }
            
            return result;
        }
        
        public List<TimeInterval> findBusiestWindows(List<TimeInterval> jobIntervals) {
            IntervalReport report = analyzeJobIntervals(jobIntervals);
            return report.busiestWindows;
        }
        
        public int getMaxConcurrentJobs(List<TimeInterval> jobIntervals) {
            if (jobIntervals == null || jobIntervals.isEmpty()) {
                return 0;
            }
            
            List<TimeEvent> events = createTimelineEvents(jobIntervals);
            events.sort((a, b) -> {
                if (a.time != b.time) return Integer.compare(a.time, b.time);
                return Integer.compare(a.type, b.type);
            });
            
            int currentJobs = 0;
            int maxJobs = 0;
            
            for (TimeEvent event : events) {
                currentJobs += event.type;
                maxJobs = Math.max(maxJobs, currentJobs);
            }
            
            return maxJobs;
        }
    }
    
    // Utility class for performance testing and validation
    public static class IntervalValidator {
        
        public static boolean validateMergedIntervals(List<TimeInterval> intervals) {
            if (intervals.isEmpty()) return true;
            
            for (int i = 0; i < intervals.size() - 1; i++) {
                TimeInterval current = intervals.get(i);
                TimeInterval next = intervals.get(i + 1);
                
                // Check if intervals are sorted
                if (current.start > next.start) {
                    return false;
                }
                
                // Check if intervals are properly merged (no overlap)
                if (current.end >= next.start) {
                    return false;
                }
                
                // Check if individual interval is valid
                if (current.start > current.end) {
                    return false;
                }
            }
            
            // Check last interval
            TimeInterval last = intervals.get(intervals.size() - 1);
            return last.start <= last.end;
        }
        
        public static boolean compareMergeResults(List<TimeInterval> simple, List<TimeInterval> sweep) {
            if (simple.size() != sweep.size()) {
                return false;
            }
            
            for (int i = 0; i < simple.size(); i++) {
                TimeInterval a = simple.get(i);
                TimeInterval b = sweep.get(i);
                if (a.start != b.start || a.end != b.end) {
                    return false;
                }
            }
            
            return true;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== CI Pipeline Interval Analysis ===");
        
        // Test case from problem description
        List<TimeInterval> testIntervals = Arrays.asList(
            new TimeInterval(2, 5),
            new TimeInterval(12, 15),
            new TimeInterval(4, 8)
        );
        
        CIPipelineAnalyzer analyzer = new CIPipelineAnalyzer();
        
        // Basic interval merging
        System.out.println("Input intervals: " + testIntervals);
        IntervalReport report = analyzer.analyzeJobIntervals(testIntervals);
        System.out.println("Merged intervals (>=1 job): " + report.intervals);
        System.out.println("Expected: [{2, 8}, {12, 15}]");
        
        // Scale-up 1: At least two jobs running
        List<TimeInterval> twoJobsIntervals = analyzer.findIntervalsWithAtLeastNJobs(testIntervals, 2);
        System.out.println("Intervals with >=2 jobs: " + twoJobsIntervals);
        
        // Scale-up 2: Busiest windows
        System.out.println("Max concurrent jobs: " + report.maxConcurrentJobs);
        System.out.println("Busiest windows: " + report.busiestWindows);
        
        // More complex test case
        System.out.println("=== Complex Test Case ===");
        List<TimeInterval> complexIntervals = Arrays.asList(
            new TimeInterval(1, 3),
            new TimeInterval(2, 4),
            new TimeInterval(3, 5),
            new TimeInterval(7, 9),
            new TimeInterval(8, 10),
            new TimeInterval(9, 11)
        );
        
        IntervalReport complexReport = analyzer.analyzeJobIntervals(complexIntervals);
        System.out.println("Complex input: " + complexIntervals);
        System.out.println("Merged intervals: " + complexReport.intervals);
        System.out.println("Intervals with >=2 jobs: "+analyzer.findIntervalsWithAtLeastNJobs(complexIntervals,2));
        System.out.println("Max concurrent jobs: " + complexReport.maxConcurrentJobs);
        System.out.println("Busiest windows: " + complexReport.busiestWindows);
        
        // Validation test
        System.out.println("=== Validation Test ===");
        SimpleIntervalMerger simpleMerger = new SimpleIntervalMerger();
        List<TimeInterval> simpleResult = simpleMerger.mergeIntervals(testIntervals);
        List<TimeInterval> sweepResult = complexReport.intervals;
        
        System.out.println("Simple merge result: " + simpleResult);
        System.out.println("Sweep merge result: " + sweepResult);
        
        // Performance test
        System.out.println("=== Performance Test ===");
        performanceTest();
    }
    
    private static void performanceTest() {
        Random random = new Random(42);
        List<TimeInterval> largeDataset = new ArrayList<>();
        
        // Generate 10000 random intervals
        for (int i = 0; i < 10000; i++) {
            int start = random.nextInt(1000);
            int duration = random.nextInt(50) + 1;
            largeDataset.add(new TimeInterval(start, start + duration));
        }
        
        CIPipelineAnalyzer analyzer = new CIPipelineAnalyzer();
        
        long startTime = System.currentTimeMillis();
        IntervalReport report = analyzer.analyzeJobIntervals(largeDataset);
        long endTime = System.currentTimeMillis();
        
        System.out.println("Processed " + largeDataset.size() + " intervals in " + (endTime - startTime) + "ms");
        System.out.println("Result: " + report.intervals.size() + " merged intervals");
        System.out.println("Max concurrent jobs: " + report.maxConcurrentJobs);
        System.out.println("Busiest periods: " + report.busiestWindows.size());
    }
}