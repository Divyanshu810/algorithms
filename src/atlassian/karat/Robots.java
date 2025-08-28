package practice.atlassian.karat;

import java.util.*;
/**
 *You have:
 *
 * all_parts: list of strings like ["RX-head", "RX-body", "RX-leg", "TX-head", "TX-body"]
 *
 * required_parts: comma-separated string like "head,body,leg" (i.e., each robot needs these parts)
 *
 * Each part in all_parts is in format <robot_name>-<part_name>
 *
 * You need to find robot names for which all required parts are available.
 */
public class Robots {
    public static String[] get_robots(String[] all_parts, String required_parts){
        List<String> result = new ArrayList<>();
        HashMap<String, Set<String>> robotPartsMap = new HashMap<>();
        Set<String> required = new HashSet<>(Arrays.asList(required_parts.split(",")));
        for (String part : all_parts) {
            String[] splitPart = part.split("-");
            if (splitPart.length == 2) {
                String robotName = splitPart[0];
                String partName = splitPart[1];
                robotPartsMap.putIfAbsent(robotName, new HashSet<>());
                robotPartsMap.get(robotName).add(partName);
            }
        }
        for(Map.Entry<String, Set<String>> entry : robotPartsMap.entrySet()) {
            String robotName = entry.getKey();
            Set<String> parts = entry.getValue();
            boolean hasAllParts = new HashSet<>(parts).containsAll(required);
//            for (String requiredPart : requiredPartsArray) {
//                if (!parts.contains(requiredPart)) {
//                    hasAllParts = false;
//                    break;
//                }
//            }
            if (hasAllParts) {
                result.add(robotName);
            }
        }
        return  result.toArray(new String[0]);
    }

    public static void main(String[] args) {
        String[] all_parts = {"RX-head", "RX-body", "RX-leg", "TX-head", "TX-body"};
        String required_parts = "head,body,leg";
        String[] robots = get_robots(all_parts, required_parts);
        System.out.println(Arrays.toString(robots)); // Output: [RX, TX]
    }
}
