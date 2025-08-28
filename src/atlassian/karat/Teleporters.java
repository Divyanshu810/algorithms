package practice.atlassian.karat;

/**
 * Given a board with numbers from 0 to lastNumber, startPosition,
 * teleporters like. ("3,1", "5,10", "8,2") and maxValue of a die.
 * Also, if after rollin the die, a number greater than lastNumber is reached then teleport to the lastNumber.
 * Find all final positions which can be reached by rolling the die once (1 to maxValue). (Working solution of this one).
 */
import java.util.*;

public class Teleporters {
    public static Set<Integer> findFinalPositions(
            int lastNumber,
            int startPosition,
            List<String> teleporters,
            int maxValue) {

        // Parse teleporters into a map
        Map<Integer, Integer> teleMap = new HashMap<>();
        for (String t : teleporters) {
            String[] parts = t.split(",");
            teleMap.put(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
        }

        Set<Integer> result = new HashSet<>();
        for (int roll = 1; roll <= maxValue; roll++) {
            int next = startPosition + roll;
            if (next > lastNumber) next = lastNumber;
            if (teleMap.containsKey(next)) next = teleMap.get(next);
            System.out.println(roll+" "+next);
            result.add(next);
        }
        return result;
    }

    // Example usage
    public static void main(String[] args) {
        int lastNumber = 10;
        int startPosition = 2;
        List<String> teleporters = Arrays.asList("3,1", "5,10", "8,2");
        int maxValue = 6;
        Set<Integer> finals = findFinalPositions(lastNumber, startPosition, teleporters, maxValue);
        System.out.println(finals); // Example output: [1,2, 4, 6, 7, 10, 2]
    }
}