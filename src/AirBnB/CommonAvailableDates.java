package practice.airbnb;

import java.time.LocalDate;
import java.util.*;

public class CommonAvailableDates {
    
    public static class DateRange {
        LocalDate start;
        LocalDate end;
        
        public DateRange(LocalDate start, LocalDate end) {
            this.start = start;
            this.end = end;
        }
        
        @Override
        public String toString() {
            return String.format("[%s - %s]", start, end);
        }
    }
    
    /**
     * Find common available dates using set intersection approach
     * Time: O(n * m) where n is number of hotels, m is average dates per hotel
     * Space: O(m) for the result set
     */
    public static List<LocalDate> findCommonDatesSetIntersection(List<List<LocalDate>> hotelDates) {
        if (hotelDates == null || hotelDates.isEmpty()) {
            return new ArrayList<>();
        }
        
        Set<LocalDate> commonDates = new HashSet<>(hotelDates.get(0));
        
        for (int i = 1; i < hotelDates.size(); i++) {
            commonDates.retainAll(hotelDates.get(i));
            if (commonDates.isEmpty()) {
                break;
            }
        }
        
        List<LocalDate> result = new ArrayList<>(commonDates);
        Collections.sort(result);
        return result;
    }
    
    /**
     * Find common available dates using CUSTOM intersection logic (no retainAll)
     * Time: O(n * m * log m) where n is number of hotels, m is average dates per hotel
     * Space: O(m) for the result set
     * 
     * OPTIMIZATION: Manual intersection avoids retainAll overhead and provides better control
     */
    public static List<LocalDate> findCommonDatesCustomIntersection(List<List<LocalDate>> hotelDates) {
        if (hotelDates == null || hotelDates.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Start with first hotel's dates
        Set<LocalDate> commonDates = new HashSet<>(hotelDates.get(0));
        
        // Manually intersect with each subsequent hotel
        for (int i = 1; i < hotelDates.size(); i++) {
            Set<LocalDate> currentHotelDates = new HashSet<>(hotelDates.get(i));
            Set<LocalDate> intersection = new HashSet<>();
            
            // Custom intersection logic - only keep dates present in both sets
            for (LocalDate date : commonDates) {
                if (currentHotelDates.contains(date)) {
                    intersection.add(date);
                }
            }
            
            commonDates = intersection;
            
            // Early termination if no common dates remain
            if (commonDates.isEmpty()) {
                break;
            }
        }
        
        List<LocalDate> result = new ArrayList<>(commonDates);
        Collections.sort(result);
        return result;
    }
    
    /**
     * OPTIMIZED: Find common dates using frequency counting approach
     * Time: O(n * m) where n is number of hotels, m is average dates per hotel
     * Space: O(total_unique_dates) for the frequency map
     * 
     * ADVANTAGE: Single pass through all dates, more cache-friendly
     */
    public static List<LocalDate> findCommonDatesFrequencyCount(List<List<LocalDate>> hotelDates) {
        if (hotelDates == null || hotelDates.isEmpty()) {
            return new ArrayList<>();
        }
        
        Map<LocalDate, Integer> dateFrequency = new HashMap<>();
        int totalHotels = hotelDates.size();
        
        // Count frequency of each date across all hotels
        for (List<LocalDate> hotelDateList : hotelDates) {
            Set<LocalDate> uniqueDatesForHotel = new HashSet<>(hotelDateList); // Remove duplicates within hotel
            for (LocalDate date : uniqueDatesForHotel) {
                dateFrequency.put(date, dateFrequency.getOrDefault(date, 0) + 1);
            }
        }
        
        // Collect dates that appear in ALL hotels
        List<LocalDate> commonDates = new ArrayList<>();
        for (Map.Entry<LocalDate, Integer> entry : dateFrequency.entrySet()) {
            if (entry.getValue() == totalHotels) {
                commonDates.add(entry.getKey());
            }
        }
        
        Collections.sort(commonDates);
        return commonDates;
    }
    
    /**
     * OPTIMIZED: Find common dates using sorted list intersection (for pre-sorted data)
     * Time: O(n * m) where n is number of hotels, m is average dates per hotel
     * Space: O(m) for the result set
     * 
     * ADVANTAGE: Most efficient when input lists are already sorted
     */
    public static List<LocalDate> findCommonDatesSortedIntersection(List<List<LocalDate>> hotelDates) {
        if (hotelDates == null || hotelDates.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Sort all hotel date lists first
        List<List<LocalDate>> sortedHotelDates = new ArrayList<>();
        for (List<LocalDate> dates : hotelDates) {
            List<LocalDate> sortedDates = new ArrayList<>(dates);
            Collections.sort(sortedDates);
            sortedHotelDates.add(sortedDates);
        }
        
        // Start with first hotel's dates
        List<LocalDate> result = new ArrayList<>(sortedHotelDates.get(0));
        
        // Intersect with each subsequent hotel using sorted merge
        for (int i = 1; i < sortedHotelDates.size(); i++) {
            result = intersectSortedLists(result, sortedHotelDates.get(i));
            if (result.isEmpty()) {
                break; // Early termination
            }
        }
        
        return result;
    }
    
    /**
     * Helper method to intersect two sorted lists efficiently
     * Time: O(min(a, b)) where a, b are list sizes
     */
    private static List<LocalDate> intersectSortedLists(List<LocalDate> list1, List<LocalDate> list2) {
        List<LocalDate> intersection = new ArrayList<>();
        int i = 0, j = 0;
        
        while (i < list1.size() && j < list2.size()) {
            LocalDate date1 = list1.get(i);
            LocalDate date2 = list2.get(j);
            
            int comparison = date1.compareTo(date2);
            if (comparison == 0) {
                // Dates match - add to intersection
                intersection.add(date1);
                i++;
                j++;
            } else if (comparison < 0) {
                // date1 is earlier - advance first list
                i++;
            } else {
                // date2 is earlier - advance second list
                j++;
            }
        }
        
        return intersection;
    }
    
    /**
     * Find common available dates using K-way merge approach (assumes sorted input)
     * Time: O(n * m) where n is number of hotels, m is average dates per hotel
     * Space: O(n) additional space (excluding result)
     */
    public static List<LocalDate> findCommonDatesKWayMerge(List<List<LocalDate>> hotelDates) {
        if (hotelDates == null || hotelDates.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Check if any hotel has no available dates
        for (List<LocalDate> dates : hotelDates) {
            if (dates.isEmpty()) {
                return new ArrayList<>();
            }
        }
        
        List<LocalDate> result = new ArrayList<>();
        int[] pointers = new int[hotelDates.size()];
        
        while (true) {
            // Find the maximum date at current pointers
            LocalDate maxDate = null;
            for (int i = 0; i < hotelDates.size(); i++) {
                if (pointers[i] >= hotelDates.get(i).size()) {
                    return result; // One hotel exhausted
                }
                LocalDate currentDate = hotelDates.get(i).get(pointers[i]);
                if (maxDate == null || currentDate.isAfter(maxDate)) {
                    maxDate = currentDate;
                }
            }
            
            // Check if all hotels have this max date
            boolean allHaveDate = true;
            for (int i = 0; i < hotelDates.size(); i++) {
                LocalDate currentDate = hotelDates.get(i).get(pointers[i]);
                if (!currentDate.equals(maxDate)) {
                    allHaveDate = false;
                    break;
                }
            }
            
            if (allHaveDate) {
                result.add(maxDate);
                // Advance all pointers
                for (int i = 0; i < pointers.length; i++) {
                    pointers[i]++;
                }
            } else {
                // Advance pointers that are behind
                for (int i = 0; i < hotelDates.size(); i++) {
                    LocalDate currentDate = hotelDates.get(i).get(pointers[i]);
                    if (currentDate.isBefore(maxDate)) {
                        pointers[i]++;
                        if (pointers[i] >= hotelDates.get(i).size()) {
                            return result; // One hotel exhausted
                        }
                    }
                }
            }
        }
    }
    
    /**
     * Find the longest consecutive streak of common available dates
     * Time: O(n * m + k log k) where k is number of common dates
     * Space: O(k) for storing common dates
     */
    public static DateRange findLongestStreak(List<List<LocalDate>> hotelDates) {
        List<LocalDate> commonDates = findCommonDatesSetIntersection(hotelDates);
        
        if (commonDates.isEmpty()) {
            return null;
        }
        
        if (commonDates.size() == 1) {
            return new DateRange(commonDates.get(0), commonDates.get(0));
        }
        
        LocalDate longestStart = commonDates.get(0);
        LocalDate longestEnd = commonDates.get(0);
        int longestLength = 1;
        
        LocalDate currentStart = commonDates.get(0);
        LocalDate currentEnd = commonDates.get(0);
        int currentLength = 1;
        
        for (int i = 1; i < commonDates.size(); i++) {
            LocalDate prev = commonDates.get(i - 1);
            LocalDate curr = commonDates.get(i);
            
            // Check if consecutive (next day)
            if (prev.plusDays(1).equals(curr)) {
                currentEnd = curr;
                currentLength++;
            } else {
                // Streak broken, check if current is longest
                if (currentLength > longestLength) {
                    longestStart = currentStart;
                    longestEnd = currentEnd;
                    longestLength = currentLength;
                }
                // Start new streak
                currentStart = curr;
                currentEnd = curr;
                currentLength = 1;
            }
        }
        
        // Check final streak
        if (currentLength > longestLength) {
            longestStart = currentStart;
            longestEnd = currentEnd;
        }
        
        return new DateRange(longestStart, longestEnd);
    }
    
    /**
     * Find all consecutive date ranges from common dates
     */
    public static List<DateRange> findAllStreaks(List<List<LocalDate>> hotelDates) {
        List<LocalDate> commonDates = findCommonDatesSetIntersection(hotelDates);
        List<DateRange> streaks = new ArrayList<>();
        
        if (commonDates.isEmpty()) {
            return streaks;
        }
        
        LocalDate currentStart = commonDates.get(0);
        LocalDate currentEnd = commonDates.get(0);
        
        for (int i = 1; i < commonDates.size(); i++) {
            LocalDate prev = commonDates.get(i - 1);
            LocalDate curr = commonDates.get(i);
            
            if (prev.plusDays(1).equals(curr)) {
                currentEnd = curr;
            } else {
                streaks.add(new DateRange(currentStart, currentEnd));
                currentStart = curr;
                currentEnd = curr;
            }
        }
        
        // Add the final streak
        streaks.add(new DateRange(currentStart, currentEnd));
        return streaks;
    }
    
    // Helper method to create dates easily
    private static LocalDate date(String dateStr) {
        return LocalDate.parse(dateStr);
    }
    
    public static void main(String[] args) {
        // Test case 1: Basic intersection
        List<List<LocalDate>> hotels1 = Arrays.asList(
            Arrays.asList(date("2024-01-01"), date("2024-01-02"), date("2024-01-03"), date("2024-01-05")),
            Arrays.asList(date("2024-01-02"), date("2024-01-03"), date("2024-01-04"), date("2024-01-05")),
            Arrays.asList(date("2024-01-01"), date("2024-01-03"), date("2024-01-05"), date("2024-01-06"))
        );
        
        System.out.println("Hotel availability:");
        for (int i = 0; i < hotels1.size(); i++) {
            System.out.println("Hotel " + (i+1) + ": " + hotels1.get(i));
        }
        
        System.out.println("\n=== PERFORMANCE COMPARISON ===");
        
        // Test all approaches and measure performance
        long startTime, endTime;
        
        startTime = System.nanoTime();
        List<LocalDate> common1 = findCommonDatesSetIntersection(hotels1);
        endTime = System.nanoTime();
        long time1 = endTime - startTime;
        System.out.println("Set Intersection (retainAll): " + common1 + " (" + time1/1000 + " μs)");
        
        startTime = System.nanoTime();
        List<LocalDate> common2 = findCommonDatesCustomIntersection(hotels1);
        endTime = System.nanoTime();
        long time2 = endTime - startTime;
        System.out.println("Custom Intersection (manual): " + common2 + " (" + time2/1000 + " μs)");
        
        startTime = System.nanoTime();
        List<LocalDate> common3 = findCommonDatesFrequencyCount(hotels1);
        endTime = System.nanoTime();
        long time3 = endTime - startTime;
        System.out.println("Frequency Count: " + common3 + " (" + time3/1000 + " μs)");
        
        startTime = System.nanoTime();
        List<LocalDate> common4 = findCommonDatesSortedIntersection(hotels1);
        endTime = System.nanoTime();
        long time4 = endTime - startTime;
        System.out.println("Sorted Intersection: " + common4 + " (" + time4/1000 + " μs)");
        
        startTime = System.nanoTime();
        List<LocalDate> common5 = findCommonDatesKWayMerge(hotels1);
        endTime = System.nanoTime();
        long time5 = endTime - startTime;
        System.out.println("K-way Merge: " + common5 + " (" + time5/1000 + " μs)");
        
        // Verify all methods return same result
        boolean allMatch = common1.equals(common2) && common2.equals(common3) && 
                          common3.equals(common4) && common4.equals(common5);
        System.out.println("\nAll methods return same result: " + allMatch);
        
        System.out.println("\n=== Longest Streak ===");
        DateRange longestStreak = findLongestStreak(hotels1);
        System.out.println("Longest streak: " + longestStreak);
        
        System.out.println("\n=== All Streaks ===");
        List<DateRange> allStreaks = findAllStreaks(hotels1);
        System.out.println("All streaks: " + allStreaks);
        
        // Test case 2: Consecutive dates
        System.out.println("\n==================================================");
        System.out.println("Test case 2: Consecutive dates");
        
        List<List<LocalDate>> hotels2 = Arrays.asList(
            Arrays.asList(date("2024-02-01"), date("2024-02-02"), date("2024-02-03"), 
                         date("2024-02-04"), date("2024-02-07"), date("2024-02-08")),
            Arrays.asList(date("2024-02-02"), date("2024-02-03"), date("2024-02-04"), 
                         date("2024-02-05"), date("2024-02-07"), date("2024-02-08")),
            Arrays.asList(date("2024-02-01"), date("2024-02-02"), date("2024-02-03"), 
                         date("2024-02-04"), date("2024-02-08"), date("2024-02-09"))
        );
        
        System.out.println("Hotel availability:");
        for (int i = 0; i < hotels2.size(); i++) {
            System.out.println("Hotel " + (i+1) + ": " + hotels2.get(i));
        }
        
        List<LocalDate> consecutiveCommon = findCommonDatesSetIntersection(hotels2);
        System.out.println("Common dates: " + consecutiveCommon);
        
        DateRange longestStreak2 = findLongestStreak(hotels2);
        System.out.println("Longest streak: " + longestStreak2);
        
        List<DateRange> allStreaks2 = findAllStreaks(hotels2);
        System.out.println("All streaks: " + allStreaks2);
        
        // Test case 3: No common dates
        System.out.println("\n==================================================");
        System.out.println("Test case 3: No common dates");
        
        List<List<LocalDate>> hotels3 = Arrays.asList(
            Arrays.asList(date("2024-03-01"), date("2024-03-02")),
            Arrays.asList(date("2024-03-03"), date("2024-03-04")),
            Arrays.asList(date("2024-03-05"), date("2024-03-06"))
        );
        
        List<LocalDate> noneCommon = findCommonDatesSetIntersection(hotels3);
        System.out.println("Common dates: " + noneCommon);
        
        DateRange longestStreak3 = findLongestStreak(hotels3);
        System.out.println("Longest streak: " + longestStreak3);
    }

    /*
     * ==========================================
     * TIME AND SPACE COMPLEXITY ANALYSIS
     * ==========================================
     * 
     * Let:
     * - h = number of hotels
     * - d = average number of dates per hotel
     * - k = number of common dates found
     * - max_d = maximum dates in any single hotel
     * 
     * 1. SET INTERSECTION APPROACH (findCommonDatesSetIntersection):
     *    Time Complexity: O(h * d)
     *    - Initialize HashSet with first hotel's dates: O(d)
     *    - For each remaining hotel: retainAll() operation takes O(d) time
     *    - Final sorting: O(k log k) where k ≤ d
     *    - Overall: O(h * d + k log k) ≈ O(h * d) since k ≤ d
     *    Space Complexity: O(d)
     *    - HashSet stores up to d dates from first hotel
     *    - Result list stores up to k common dates
     * 
     * 1a. CUSTOM INTERSECTION APPROACH (findCommonDatesCustomIntersection):
     *    Time Complexity: O(h * d * log d)
     *    - Manual intersection avoids retainAll() overhead
     *    - For each hotel: iterate through commonDates and check contains(): O(d)
     *    - HashSet creation for each hotel: O(d)
     *    - Better control over early termination
     *    Space Complexity: O(d)
     *    - Similar to retainAll approach but with explicit intersection sets
     * 
     * 1b. FREQUENCY COUNT APPROACH (findCommonDatesFrequencyCount):
     *    Time Complexity: O(h * d)
     *    - Single pass through all dates across all hotels
     *    - Count frequency of each date: O(1) per date
     *    - Filter dates appearing in all hotels: O(total_unique_dates)
     *    - Most cache-friendly approach
     *    Space Complexity: O(total_unique_dates)
     *    - HashMap stores frequency count for each unique date
     * 
     * 1c. SORTED INTERSECTION APPROACH (findCommonDatesSortedIntersection):
     *    Time Complexity: O(h * d * log d + h * d)
     *    - Sort each hotel's dates: O(h * d * log d)
     *    - Intersect sorted lists using two-pointer technique: O(h * d)
     *    - Most efficient when input is already sorted
     *    Space Complexity: O(h * d)
     *    - Stores sorted copies of all hotel date lists
     * 
     * 2. K-WAY MERGE APPROACH (findCommonDatesKWayMerge):
     *    Time Complexity: O(h * d)
     *    - Each date in each hotel is examined at most once
     *    - For each position, we scan all h hotels to find max: O(h)
     *    - Total dates across all hotels: h * d
     *    Space Complexity: O(h + k)
     *    - Array of h pointers: O(h)
     *    - Result list: O(k)
     *    - More space-efficient than set intersection
     * 
     * 3. LONGEST STREAK DETECTION (findLongestStreak):
     *    Time Complexity: O(h * d + k log k + k)
     *    - Finding common dates: O(h * d)
     *    - Sorting common dates: O(k log k)
     *    - Single pass through k common dates: O(k)
     *    Space Complexity: O(d + k)
     *    - Common dates list: O(k)
     *    - Temporary variables: O(1)
     * 
     * 4. ALL STREAKS DETECTION (findAllStreaks):
     *    Time Complexity: O(h * d + k log k + k)
     *    - Same as longest streak detection
     *    - Single pass to identify all consecutive ranges: O(k)
     *    Space Complexity: O(d + k + s)
     *    - Common dates list: O(k)
     *    - Result list of s streaks: O(s) where s ≤ k
     * 
     * ALGORITHM COMPARISON:
     * 
     * PERFORMANCE RANKING (Best to Worst):
     * 1. Frequency Count: O(h * d) - Single pass, most cache-friendly
     * 2. Sorted Intersection: O(h * d) - Best when data is pre-sorted
     * 3. Set Intersection (retainAll): O(h * d) - Simple, built-in optimization
     * 4. Custom Intersection: O(h * d * log d) - More control, manual logic
     * 5. K-way Merge: O(h * d) - Good for sorted data but complex setup
     * 
     * SPACE EFFICIENCY RANKING:
     * 1. K-way Merge: O(h + k) - Most compact
     * 2. Set Intersection: O(d) - Minimal extra space
     * 3. Custom Intersection: O(d) - Similar to retainAll
     * 4. Frequency Count: O(total_unique_dates) - May use more space
     * 5. Sorted Intersection: O(h * d) - Stores all sorted copies
     * 
     * PRACTICAL CONSIDERATIONS:
     * - Frequency Count: Best for large datasets with many duplicates
     * - Sorted Intersection: Optimal when input is already sorted
     * - Custom Intersection: Better control and debugging, avoids retainAll
     * - Set Intersection (retainAll): Simple and reliable for small datasets
     * - K-way Merge: Most memory-efficient for very large datasets
     * 
     * WHEN TO AVOID retainAll():
     * - Large datasets where performance matters
     * - Need for debugging intersection logic
     * - Custom optimizations (early termination, logging)
     * - Better control over memory allocation
     */
}