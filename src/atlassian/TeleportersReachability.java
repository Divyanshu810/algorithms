package atlassian;

import java.util.*;

public class TeleportersReachability {
    public static boolean canReachLastNumber(
            int lastNumber,
            int startPosition,
            List<String> teleporters,
            int maxValue) {

        Map<Integer, Integer> teleMap = new HashMap<>();
        for (String t : teleporters) {
            String[] parts = t.split(",");
            teleMap.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();
        queue.add(startPosition);
        visited.add(startPosition);

        while (!queue.isEmpty()) {
            int pos = queue.poll();
            if (pos == lastNumber) return true;

            for (int roll = 1; roll <= maxValue; roll++) {
                int next = pos + roll;
                if (next > lastNumber) next = lastNumber;
                if (teleMap.containsKey(next)) next = teleMap.get(next);
                if (!visited.contains(next)) {
                    visited.add(next);
                    queue.add(next);
                }
            }
        }
        return false;
    }

    // Example usage
    public static void main(String[] args) {
        int lastNumber = 10;
        int startPosition = 2;
        List<String> teleporters = Arrays.asList("3,1", "5,10", "8,2");
        int maxValue = 6;
        boolean canReach = canReachLastNumber(lastNumber, startPosition, teleporters, maxValue);
        System.out.println("Can reach lastNumber: " + canReach);
    }
}