package Company.atlassian.data_structures.q5_weighted_graph.OldSol;

import java.util.*;

class Edge {
    String to;
    int weight;
    
    public Edge(String to, int weight) {
        this.to = to;
        this.weight = weight;
    }
    
    @Override
    public String toString() {
        return to + "(" + weight + ")";
    }
}

class PathResult {
    boolean canReach;
    int shortestTime;
    List<String> path;
    
    public PathResult(boolean canReach, int shortestTime, List<String> path) {
        this.canReach = canReach;
        this.shortestTime = shortestTime;
        this.path = path;
    }
    
    @Override
    public String toString() {
        if (!canReach) {
            return "No path exists";
        }
        return "Shortest time: " + shortestTime + ", Path: " + String.join(" -> ", path);
    }
}

class BridgeResult {
    String fromNode;
    String toNode;
    int shortestTime;
    List<String> path;
    
    public BridgeResult(String fromNode, String toNode, int shortestTime, List<String> path) {
        this.fromNode = fromNode;
        this.toNode = toNode;
        this.shortestTime = shortestTime;
        this.path = path;
    }
    
    @Override
    public String toString() {
        return "Best bridge: " + fromNode + " -> " + toNode + 
               ", Shortest time: " + shortestTime + 
               ", Path: " + String.join(" -> ", path);
    }
}

public class Solution {
    
    public static class WeightedGraph {
        private Map<String, List<Edge>> adjacencyList;
        private Set<String> nodes;
        
        public WeightedGraph() {
            this.adjacencyList = new HashMap<>();
            this.nodes = new HashSet<>();
        }
        
        public void addNode(String node) {
            nodes.add(node);
            adjacencyList.putIfAbsent(node, new ArrayList<>());
        }
        
        public void addEdge(String from, String to, int weight) {
            addNode(from);
            addNode(to);
            adjacencyList.get(from).add(new Edge(to, weight));
        }
        
        public Set<String> getNodes() {
            return new HashSet<>(nodes);
        }
        
        public List<Edge> getNeighbors(String node) {
            return adjacencyList.getOrDefault(node, new ArrayList<>());
        }
        
        // Dijkstra's algorithm implementation
        public PathResult findShortestPath(String source, String destination) {
            if (!nodes.contains(source) || !nodes.contains(destination)) {
                return new PathResult(false, -1, new ArrayList<>());
            }
            
            Map<String, Integer> distances = new HashMap<>();
            Map<String, String> previous = new HashMap<>();
            PriorityQueue<String> pq = new PriorityQueue<>((a, b) -> 
                distances.getOrDefault(a, Integer.MAX_VALUE) - distances.getOrDefault(b, Integer.MAX_VALUE));
            
            // Initialize distances
            for (String node : nodes) {
                distances.put(node, Integer.MAX_VALUE);
            }
            distances.put(source, 0);
            pq.offer(source);
            
            while (!pq.isEmpty()) {
                String current = pq.poll();
                
                // Early termination when destination is reached
                if (current.equals(destination)) {
                    break;
                }
                
                //  Skip if 1. The current node hasn't been discovered yet (no path has been found to it)
                //  2. Or the node is unreachable from the source node
                if (distances.get(current) == Integer.MAX_VALUE) {
                    continue;
                }
                
                for (Edge edge : getNeighbors(current)) {
                    int newDistance = distances.get(current) + edge.weight;
                    
                    if (newDistance < distances.get(edge.to)) {
                        distances.put(edge.to, newDistance);
                        previous.put(edge.to, current);
                        pq.offer(edge.to);
                    }
                }
            }
            
            // Check if destination is reachable
            int shortestTime = distances.get(destination);
            if (shortestTime == Integer.MAX_VALUE) {
                return new PathResult(false, -1, new ArrayList<>());
            }
            
            // Reconstruct path
            List<String> path = reconstructPath(previous, source, destination);
            return new PathResult(true, shortestTime, path);
        }
        
        private List<String> reconstructPath(Map<String, String> previous, String source, String destination) {
            List<String> path = new ArrayList<>();
            String current = destination;
            
            while (current != null) {
                path.add(current);
                current = previous.get(current);
            }
            
            Collections.reverse(path);
            
            // Verify path starts with source
            if (path.isEmpty() || !path.get(0).equals(source)) {
                return new ArrayList<>();
            }
            
            return path;
        }
        
        // Find optimal bridge to connect source to destination
        public BridgeResult findOptimalBridge(String source, String destination, 
                                            List<PotentialEdge> potentialEdges) {
            BridgeResult bestResult = null;
            int bestTime = Integer.MAX_VALUE;
            
            for (PotentialEdge potentialEdge : potentialEdges) {
                // Temporarily add the edge
                addEdge(potentialEdge.from, potentialEdge.to, potentialEdge.weight);
                
                // Find shortest path with this edge
                PathResult result = findShortestPath(source, destination);
                
                if (result.canReach && result.shortestTime < bestTime) {
                    bestTime = result.shortestTime;
                    bestResult = new BridgeResult(potentialEdge.from, potentialEdge.to, 
                                                result.shortestTime, result.path);
                }
                
                // Remove the temporary edge
                removeEdge(potentialEdge.from, potentialEdge.to);
            }
            
            return bestResult;
        }
        
        // Naive approach: try all possible bridges
        public BridgeResult findOptimalBridgeNaive(String source, String destination, int bridgeWeight) {
            BridgeResult bestResult = null;
            int bestTime = Integer.MAX_VALUE;
            
            // Try all possible node pairs as potential bridges
            for (String from : nodes) {
                for (String to : nodes) {
                    if (from.equals(to)) continue;
                    
                    // Check if edge already exists
                    if (hasDirectEdge(from, to)) continue;
                    
                    // Temporarily add the edge
                    addEdge(from, to, bridgeWeight);
                    
                    // Find shortest path with this edge
                    PathResult result = findShortestPath(source, destination);
                    
                    if (result.canReach && result.shortestTime < bestTime) {
                        bestTime = result.shortestTime;
                        bestResult = new BridgeResult(from, to, result.shortestTime, result.path);
                    }
                    
                    // Remove the temporary edge
                    removeEdge(from, to);
                }
            }
            
            return bestResult;
        }
        
        private boolean hasDirectEdge(String from, String to) {
            List<Edge> neighbors = getNeighbors(from);
            return neighbors.stream().anyMatch(edge -> edge.to.equals(to));
        }
        
        private void removeEdge(String from, String to) {
            List<Edge> neighbors = adjacencyList.get(from);
            if (neighbors != null) {
                neighbors.removeIf(edge -> edge.to.equals(to));
            }
        }
        
        // Floyd-Warshall for all-pairs shortest paths (alternative approach)
        public Map<String, Map<String, Integer>> computeAllPairsShortestPaths() {
            List<String> nodeList = new ArrayList<>(nodes);
            int n = nodeList.size();
            int[][] dist = new int[n][n];
            
            // Initialize distance matrix
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    if (i == j) {
                        dist[i][j] = 0;
                    } else {
                        dist[i][j] = Integer.MAX_VALUE;
                    }
                }
            }
            
            // Set direct edge weights
            for (int i = 0; i < n; i++) {
                String from = nodeList.get(i);
                for (Edge edge : getNeighbors(from)) {
                    int j = nodeList.indexOf(edge.to);
                    if (j >= 0) {
                        dist[i][j] = edge.weight;
                    }
                }
            }
            
            // Floyd-Warshall algorithm
            for (int k = 0; k < n; k++) {
                for (int i = 0; i < n; i++) {
                    for (int j = 0; j < n; j++) {
                        if (dist[i][k] != Integer.MAX_VALUE && 
                            dist[k][j] != Integer.MAX_VALUE &&
                            dist[i][k] + dist[k][j] < dist[i][j]) {
                            dist[i][j] = dist[i][k] + dist[k][j];
                        }
                    }
                }
            }
            
            // Convert back to map format
            Map<String, Map<String, Integer>> result = new HashMap<>();
            for (int i = 0; i < n; i++) {
                String from = nodeList.get(i);
                Map<String, Integer> distances = new HashMap<>();
                for (int j = 0; j < n; j++) {
                    String to = nodeList.get(j);
                    distances.put(to, dist[i][j]);
                }
                result.put(from, distances);
            }
            
            return result;
        }
        
        public void printGraph() {
            System.out.println("Graph structure:");
            for (String node : nodes) {
                System.out.print(node + " -> ");
                List<Edge> neighbors = getNeighbors(node);
                if (neighbors.isEmpty()) {
                    System.out.println("[]");
                } else {
                    System.out.println(neighbors);
                }
            }
        }
    }
    
    public static class PotentialEdge {
        String from;
        String to;
        int weight;
        
        public PotentialEdge(String from, String to, int weight) {
            this.from = from;
            this.to = to;
            this.weight = weight;
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Weighted Graph Shortest Path ===");
        
        WeightedGraph graph = new WeightedGraph();
        
        // Build example graph based on problem description
        // A -> B (1), A -> C (4)
        // B -> C (2), B -> D (5)
        // C -> D (1), C -> E (3)
        // D -> E (2)
        graph.addEdge("A", "B", 1);
        graph.addEdge("A", "C", 4);
        graph.addEdge("B", "C", 2);
        graph.addEdge("B", "D", 5);
        graph.addEdge("C", "D", 1);
        graph.addEdge("C", "E", 3);
        graph.addEdge("D", "E", 2);
        
        graph.printGraph();
        
        // Test shortest path from A to E
        System.out.println("=== Finding Shortest Path A -> E ===");
        PathResult result = graph.findShortestPath("A", "E");
        System.out.println(result);
        
        // Test unreachable path
        System.out.println("=== Testing Unreachable Path ===");
        graph.addNode("F"); // Isolated node
        PathResult unreachableResult = graph.findShortestPath("A", "F");
        System.out.println(unreachableResult);
        
        // Test bridge optimization with potential edges
        System.out.println("=== Bridge Optimization ===");
        List<PotentialEdge> potentialEdges = Arrays.asList(
            new PotentialEdge("A", "E", 8), // Direct but expensive
            new PotentialEdge("B", "E", 3), // Better option
            new PotentialEdge("A", "D", 6)  // Another option
        );
        
        BridgeResult bridgeResult = graph.findOptimalBridge("A", "F", potentialEdges);
        if (bridgeResult != null) {
            System.out.println(bridgeResult);
        } else {
            System.out.println("No bridge can connect A to F");
        }
        
        // Test naive bridge approach
        System.out.println("=== Naive Bridge Approach ===");
        BridgeResult naiveBridge = graph.findOptimalBridgeNaive("A", "F", 5);
        if (naiveBridge != null) {
            System.out.println(naiveBridge);
        } else {
            System.out.println("No bridge found with naive approach");
        }
        
        // Performance test
        System.out.println("=== Performance Test ===");
        performanceTest();
    }
    
    private static void performanceTest() {
        WeightedGraph largeGraph = new WeightedGraph();
        Random random = new Random(42);
        
        // Create a larger graph for performance testing
        int numNodes = 100;
        for (int i = 0; i < numNodes; i++) {
            largeGraph.addNode("Node" + i);
        }
        
        // Add random edges
        for (int i = 0; i < numNodes * 3; i++) {
            String from = "Node" + random.nextInt(numNodes);
            String to = "Node" + random.nextInt(numNodes);
            int weight = random.nextInt(10) + 1;
            
            if (!from.equals(to)) {
                largeGraph.addEdge(from, to, weight);
            }
        }
        
        long startTime = System.currentTimeMillis();
        
        // Test multiple shortest path queries
        for (int i = 0; i < 100; i++) {
            String source = "Node" + random.nextInt(numNodes);
            String dest = "Node" + random.nextInt(numNodes);
            largeGraph.findShortestPath(source, dest);
        }
        
        long endTime = System.currentTimeMillis();
        System.out.println("100 shortest path queries completed in: " + (endTime - startTime) + "ms");
    }
}