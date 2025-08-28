package practice.atlassian.karat;

import java.util.*;

public class RobotFactoryTopoSort {
    public static Map<String, Set<String>> findStartToEndLocationsTopo(List<String[]> paths) {
        Map<String, List<String>> graph = new HashMap<>();
        Map<String, Integer> inDegree = new HashMap<>();
        Set<String> allNodes = new HashSet<>();

        // Build graph and in-degree map
        for (String[] path : paths) {
            String from = path[0], to = path[1];
            graph.computeIfAbsent(from, k -> new ArrayList<>()).add(to);
            inDegree.put(to, inDegree.getOrDefault(to, 0) + 1);
            inDegree.putIfAbsent(from, 0);
            allNodes.add(from);
            allNodes.add(to);
        }

        // Topological sort
        Queue<String> queue = new LinkedList<>();
        for (String node : allNodes) {
            if (inDegree.get(node) == 0) queue.add(node);
        }
        List<String> topoOrder = new ArrayList<>();
        while (!queue.isEmpty()) {
            String node = queue.poll();
            topoOrder.add(node);
            for (String next : graph.getOrDefault(node, Collections.emptyList())) {
                inDegree.put(next, inDegree.get(next) - 1);
                if (inDegree.get(next) == 0) queue.add(next);
            }
        }

        // Find start nodes (nodes with zero in-degree)
        Set<String> starts = new HashSet<>();
        for (String node : allNodes) {
            if (!inDegree.containsKey(node) || inDegree.get(node) == 0) {
                starts.add(node);
            }
        }

        // For each start, collect reachable leaf nodes
        Map<String, Set<String>> result = new HashMap<>();
        for (String start : starts) {
            Set<String> ends = new HashSet<>();
            collectLeaves(graph, start, ends, new HashSet<>());
            result.put(start, ends);
        }
        return result;
    }

    private static void collectLeaves(Map<String, List<String>> graph, String node, Set<String> leaves, Set<String> visited) {
        if (!visited.add(node)) return;
        if (!graph.containsKey(node)) {
            leaves.add(node);
            return;
        }
        for (String next : graph.get(node)) {
            collectLeaves(graph, next, leaves, visited);
        }
    }
}