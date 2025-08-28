package practice.airbnb;

import java.util.*;

public class PreferredServiceMatcher {

    public static Map<String, List<String>> matchHotels(
        Map<String, List<String>> userPreferences,
        Map<String, List<String>> hotelOfferings,
        int k
    ) {
        Map<String, List<String>> result = new HashMap<>();

        // Step 1: Build inverted index: service -> list of hotelIds
        Map<String, List<String>> serviceToHotels = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : hotelOfferings.entrySet()) {
            String hotelId = entry.getKey();
            for (String service : entry.getValue()) {
                serviceToHotels
                    .computeIfAbsent(service, s -> new ArrayList<>())
                    .add(hotelId);
            }
        }
        // Step 2: For each user, count how many of their preferred services each hotel offers
        for (Map.Entry<String, List<String>> userEntry : userPreferences.entrySet()) {
            String userId = userEntry.getKey();
            List<String> preferences = userEntry.getValue();

            // Early termination: if user has fewer than k preferences, no hotels can match
            if (preferences.size() < k) {
                result.put(userId, new ArrayList<>());
                continue;
            }

            Map<String, Integer> hotelMatchCount = new HashMap<>();

            for (String service : preferences) {
                List<String> hotelsOfferingService = serviceToHotels.getOrDefault(service, Collections.emptyList());
                for (String hotelId : hotelsOfferingService) {
                    hotelMatchCount.put(hotelId, hotelMatchCount.getOrDefault(hotelId, 0) + 1);
                }
            }

            // Step 3: Filter hotels that matched at least k services
            List<String> matchedHotels = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : hotelMatchCount.entrySet()) {
                if (entry.getValue() >= k) {
                    matchedHotels.add(entry.getKey());
                }
            }
            result.put(userId, matchedHotels);
        }

        return result;
    }

    // Sample driver code
    public static void main(String[] args) {
        Map<String, List<String>> userPreferences = new HashMap<>();
        userPreferences.put("user1", Arrays.asList("wifi", "pool", "gym"));
        userPreferences.put("user2", Arrays.asList("spa", "restaurant"));


        Map<String, List<String>> hotelOfferings = new HashMap<>();
        hotelOfferings.put("hotelA", Arrays.asList("wifi", "spa", "gym"));
        hotelOfferings.put("hotelB", Arrays.asList("restaurant", "wifi"));
        hotelOfferings.put("hotelC", Arrays.asList("pool", "gym"));

        int k = 2;
        long startTime, endTime;
        startTime = System.nanoTime();
        Map<String, List<String>> matches = matchHotels(userPreferences, hotelOfferings, k);
        endTime = System.nanoTime();
        long time1 = endTime - startTime;
        System.out.println("BACKTRACKING" + " (" + time1/1000 + " μs)");
        for (Map.Entry<String, List<String>> entry : matches.entrySet()) {
            System.out.println(entry.getKey() + " => " + entry.getValue());
        }
    }
}
// Optimized time complexity: O(H*S + U*P*avgHotelsPerService) 
// where H = hotels, S = services per hotel, U = users, P = preferences per user
// Space complexity: O(H*S + U*H) for service index and result storage