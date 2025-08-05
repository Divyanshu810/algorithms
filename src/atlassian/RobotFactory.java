package atlassian;

/**
 * You work in an automated robot factory. Once robots are assembled, they are sent to the shipping center via a series of autonomous delivery carts, each of which moves packages on a one-way route.
 * <p>
 * Given input that provides the (directed) steps that each cart takes as pairs, write a function that identifies all the start locations, and a collection of all of the possible ending locations for each start location.
 * In this diagram, starting locations are at the top and destinations are at the bottom - i.e. the graph is directed exclusively downward.
 * <p>
 * A E J Key: [Origins]
 * / \ / \ \
 * B C F L M [Destinations]
 * \ / \ /
 * K G
 * /
 * H I
 * <p>
 * paths = [
 * ["B", "K"],
 * ["C", "K"],
 * ["E", "L"],
 * ["F", "G"],
 * ["J", "M"],
 * ["E", "F"],
 * ["C", "G"],
 * ["A", "B"],
 * ["A", "C"],
 * ["G", "H"],
 * ["G", "I"]
 * ]
 * <p>
 * Expected output (unordered):
 * [
 * "A": ["K", "H", "I"],
 * "E": ["H", "L", "I"],
 * "J": ["M"]
 * ]
 * N: Number of pairs in the input.
 */

import java.util.*;

public class RobotFactory {
    public static Map<String, Set<String>> findStartToEndLocations(List<String[]> paths) {
        Map<String, List<String>> graph = new HashMap<>();
        Set<String> destinations = new HashSet<>();
        Set<String> origins = new HashSet<>();

        // Build graph and track origins/destinations
        for (String[] path : paths) {
            String from = path[0], to = path[1];
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            destinations.add(to);
            origins.add(from);
        }

        // Start locations: origins not in destinations
        Set<String> starts = new HashSet<>(origins);
        starts.removeAll(destinations);

        Map<String, Set<String>> result = new HashMap<>();
        for (String start : starts) {
            Set<String> ends = new HashSet<>();
            dfs(graph, start, ends);
            result.put(start, ends);
        }
        return result;
    }

    private static void dfs(Map<String, List<String>> graph, String node, Set<String> ends) {
        if (!graph.containsKey(node)) {
            ends.add(node);
            return;
        }
        for (String next : graph.get(node)) {
            dfs(graph, next, ends);
        }
    }
    public static void main(String[] args) {
        List<String[]> paths = Arrays.asList(
                new String[]{"B", "K"},
                new String[]{"C", "K"},
                new String[]{"E", "L"},
                new String[]{"F", "G"},
                new String[]{"J", "M"},
                new String[]{"E", "F"},
                new String[]{"C", "G"},
                new String[]{"A", "B"},
                new String[]{"A", "C"},
                new String[]{"G", "H"},
                new String[]{"G", "I"}
        );

        Map<String, Set<String>> result = findStartToEndLocations(paths);
        for (String start : result.keySet()) {
            System.out.println(start + ": " + result.get(start));
        }
    }
}

