package practice.airbnb;

import java.util.*;

/**
 *  got leetcode (3076. Shortest Uncommon Substring in an Array) with different form of input
 * and output was expected in different way.
 * eg
 * // star_wars_titles = [
 * // 'The Phantom Menace',
 * // 'Attack of the Clones',
 * // 'Revenge of the Sith',
 * // 'A New Hope',
 * // 'The Empire Strikes Back',
 * // 'The Return of the Jedi',
 * // 'The Force Awakens',
 * // 'The Last Jedi',
 * // ]
 *
 * // Then the smallest unique substring you could type in to specify each is:
 *
 * // {
 * // 'The Phantom Menace': 'to',
 * // 'Attack of the Clones': 'tt',
 * // 'Revenge of the Sith': 'v',
 * // 'A New Hope': 'ho',
 * // 'The Empire Strikes Back': 'b',
 * // 'The Return of the Jedi': 'u',
 * // 'The Force Awakens': 'aw',
 * // 'The Last Jedi': 'tj',
 * // }
 */
public class ShortestUncommonSubstring {
    
    /**
     * Find the shortest unique substring for each string in the array.
     * A unique substring for a string is one that appears in that string but not in any other string.
     * Case-insensitive matching is used.
     * 
     * @param strings Array of strings
     * @return Map from each string to its shortest unique substring, or null if none exists
     */
    public static Map<String, String> findShortestUniqueSubstrings(String[] strings) {
        Map<String, String> result = new HashMap<>();
        
        // Convert all strings to lowercase for case-insensitive comparison
        String[] lowerStrings = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            lowerStrings[i] = strings[i].toLowerCase();
        }
        
        for (int i = 0; i < strings.length; i++) {
            String currentString = lowerStrings[i];
            String shortestUnique = findShortestUniqueForString(currentString, lowerStrings, i);
            result.put(strings[i], shortestUnique); // Use original string as key
        }
        
        return result;
    }
    
    /**
     * Find the shortest unique substring for a specific string
     */
    private static String findShortestUniqueForString(String target, String[] allStrings, int targetIndex) {
        String shortest = null;
        
        // Try all possible substring lengths starting from 1
        for (int len = 1; len <= target.length(); len++) {
            // Try all substrings of current length
            for (int start = 0; start <= target.length() - len; start++) {
                String candidate = target.substring(start, start + len);
                
                // Check if this substring is unique (appears only in target string)
                if (isUniqueSubstring(candidate, allStrings, targetIndex)) {
                    if (shortest == null || candidate.length() < shortest.length() || 
                        (candidate.length() == shortest.length() && candidate.compareTo(shortest) < 0)) {
                        shortest = candidate;
                    }
                }
            }
            
            // If we found a unique substring of this length, return it (shortest possible)
            if (shortest != null) {
                return shortest;
            }
        }
        
        return shortest; // null if no unique substring found
    }
    
    /**
     * Check if a substring appears only in the target string and not in any other string
     */
    private static boolean isUniqueSubstring(String substring, String[] allStrings, int targetIndex) {
        // Check if substring appears in any other string
        for (int i = 0; i < allStrings.length; i++) {
            if (i == targetIndex) continue; // Skip the target string itself
            
            if (allStrings[i].contains(substring)) {
                return false; // Found in another string, not unique
            }
        }
        return true; // Only found in target string
    }
    
    /**
     * Optimized version using precomputed substring map for better performance with many strings
     * Case-insensitive matching is used.
     */
    public static Map<String, String> findShortestUniqueSubstringsOptimized(String[] strings) {
        Map<String, String> result = new HashMap<>();
        
        // Convert all strings to lowercase for case-insensitive comparison
        String[] lowerStrings = new String[strings.length];
        for (int i = 0; i < strings.length; i++) {
            lowerStrings[i] = strings[i].toLowerCase();
        }
        
        // Build a map of all substrings to the strings that contain them
        Map<String, Set<Integer>> substringToStrings = new HashMap<>();
        
        // Generate all substrings for all strings
        for (int i = 0; i < lowerStrings.length; i++) {
            String str = lowerStrings[i];
            Set<String> substrings = new HashSet<>();
            
            for (int start = 0; start < str.length(); start++) {
                for (int end = start + 1; end <= str.length(); end++) {
                    String substring = str.substring(start, end);
                    substrings.add(substring);
                }
            }
            
            // Add to global map
            for (String substring : substrings) {
                substringToStrings.computeIfAbsent(substring, k -> new HashSet<>()).add(i);
            }
        }
        
        // For each string, find its shortest unique substring
        for (int i = 0; i < lowerStrings.length; i++) {
            String currentString = lowerStrings[i];
            String shortest = null;
            
            // Try all possible substring lengths starting from 1
            for (int len = 1; len <= currentString.length(); len++) {
                boolean foundUnique = false;
                
                for (int start = 0; start <= currentString.length() - len; start++) {
                    String candidate = currentString.substring(start, start + len);
                    
                    // Check if this substring is unique to current string
                    Set<Integer> containingStrings = substringToStrings.get(candidate);
                    if (containingStrings != null && containingStrings.size() == 1 && containingStrings.contains(i)) {
                        if (shortest == null || candidate.compareTo(shortest) < 0) {
                            shortest = candidate;
                        }
                        foundUnique = true;
                    }
                }
                
                // If we found a unique substring of this length, we're done (shortest possible)
                if (foundUnique) {
                    break;
                }
            }
            
            result.put(strings[i], shortest); // Use original string as key
        }
        
        return result;
    }
    
    public static void main(String[] args) {
        // Test with Star Wars titles from the example
        String[] starWarsTitles = {
            "The Phantom Menace",
            "Attack of the Clones", 
            "Revenge of the Sith",
            "A New Hope",
            "The Empire Strikes Back",
            "The Return of the Jedi",
            "The Force Awakens",
            "The Last Jedi"
        };
        
        System.out.println("=== Star Wars Titles Test ===");
        Map<String, String> result = findShortestUniqueSubstringsOptimized(starWarsTitles);
        
        for (String title : starWarsTitles) {
            String unique = result.get(title);
            System.out.println("'" + title + "': '" + (unique != null ? unique : "null") + "'");
        }
        
        // Debug specific cases
        System.out.println("\n=== Manual Verification ===");
        String phantom = "The Phantom Menace";
        System.out.println("Checking '" + phantom + "':");
        System.out.println("  Our result: '" + result.get(phantom) + "', Expected: 'to'");
        System.out.println("  Does 'to' appear in '" + phantom.toLowerCase() + "'? " + phantom.toLowerCase().contains("to"));
        System.out.println("  Let's check all length-2 substrings:");
        String phantomLower = phantom.toLowerCase();
        for (int i = 0; i <= phantomLower.length() - 2; i++) {
            String sub = phantomLower.substring(i, i + 2);
            boolean unique = true;
            for (String title : starWarsTitles) {
                if (!title.equals(phantom) && title.toLowerCase().contains(sub)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                System.out.println("    Unique 2-char substring: '" + sub + "'");
            }
        }
        
        String attack = "Attack of the Clones";
        System.out.println("Checking '" + attack + "':");
        System.out.println("  Our result: '" + result.get(attack) + "', Expected: 'tt'");
        System.out.println("  Does 'tt' appear in '" + attack.toLowerCase() + "'? " + attack.toLowerCase().contains("tt"));
        System.out.println("  Let's check all length-2 substrings:");
        String attackLower = attack.toLowerCase();
        for (int i = 0; i <= attackLower.length() - 2; i++) {
            String sub = attackLower.substring(i, i + 2);
            boolean unique = true;
            for (String title : starWarsTitles) {
                if (!title.equals(attack) && title.toLowerCase().contains(sub)) {
                    unique = false;
                    break;
                }
            }
            if (unique) {
                System.out.println("    Unique 2-char substring: '" + sub + "'");
            }
        }
        
        // Test with simpler example
        System.out.println("\n=== Simple Test ===");
        String[] simpleTest = {"abc", "def", "ghi"};
        Map<String, String> simpleResult = findShortestUniqueSubstrings(simpleTest);
        
        for (String str : simpleTest) {
            String unique = simpleResult.get(str);
            System.out.println("'" + str + "': '" + (unique != null ? unique : "null") + "'");
        }
        
        // Test with overlapping strings
        System.out.println("\n=== Overlapping Test ===");
        String[] overlappingTest = {"hello", "world", "help", "word"};
        Map<String, String> overlappingResult = findShortestUniqueSubstrings(overlappingTest);
        
        for (String str : overlappingTest) {
            String unique = overlappingResult.get(str);
            System.out.println("'" + str + "': '" + (unique != null ? unique : "null") + "'");
        }
        
        // Performance comparison
        System.out.println("\n=== Performance Comparison ===");
        long startTime, endTime;
        
        startTime = System.nanoTime();
        Map<String, String> basicResult = findShortestUniqueSubstrings(starWarsTitles);
        endTime = System.nanoTime();
        System.out.println("Basic approach: " + (endTime - startTime) / 1000 + " μs");
        
        startTime = System.nanoTime();
        Map<String, String> optimizedResult = findShortestUniqueSubstringsOptimized(starWarsTitles);
        endTime = System.nanoTime();
        System.out.println("Optimized approach: " + (endTime - startTime) / 1000 + " μs");
        
        // Verify both give same results
        boolean resultsMatch = basicResult.equals(optimizedResult);
        System.out.println("Results match: " + resultsMatch);
    }
}
