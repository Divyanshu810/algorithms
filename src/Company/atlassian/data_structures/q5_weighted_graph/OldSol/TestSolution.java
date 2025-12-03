// import org.junit.jupiter.api.Test;
// import org.junit.jupiter.api.DisplayName;
// import org.junit.jupiter.api.BeforeEach;
// import static org.junit.jupiter.api.Assertions.*;
package Company.atlassian.data_structures.q5_weighted_graph.OldSol;

import org.junit.jupiter.api.BeforeEach;

import java.util.*;

public class TestSolution {
    
    private Solution.WeightedGraph graph;
    
     @BeforeEach
    void setUp() {
        graph = new Solution.WeightedGraph();
        
        // Build test graph based on problem description
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
    }
    
    // @Test
    // @DisplayName("Test shortest path A to E")
    void testShortestPathAToE() {
        PathResult result = graph.findShortestPath("A", "E");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(6, result.shortestTime); // A->B(1)->C(2)->D(1)->E(2) = 6
        assertFalseLocal(result.path.isEmpty());
        assertEqualsLocal("A", result.path.get(0));
        assertEqualsLocal("E", result.path.get(result.path.size() - 1));
    }
    
    // @Test
    // @DisplayName("Test direct path A to B")
    void testDirectPath() {
        PathResult result = graph.findShortestPath("A", "B");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(1, result.shortestTime);
        assertEqualsLocal(Arrays.asList("A", "B"), result.path);
    }
    
    // @Test
    // @DisplayName("Test path to same node")
    void testSameNode() {
        PathResult result = graph.findShortestPath("A", "A");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(0, result.shortestTime);
        assertEqualsLocal(Arrays.asList("A"), result.path);
    }
    
    // @Test
    // @DisplayName("Test unreachable destination")
    void testUnreachableDestination() {
        graph.addNode("F"); // Isolated node
        PathResult result = graph.findShortestPath("A", "F");
        
        assertFalseLocal(result.canReach);
        assertEqualsLocal(-1, result.shortestTime);
        assertTrueLocal(result.path.isEmpty());
    }
    
    // @Test
    // @DisplayName("Test nonexistent nodes")
    void testNonexistentNodes() {
        PathResult result1 = graph.findShortestPath("X", "A");
        assertFalseLocal(result1.canReach);
        
        PathResult result2 = graph.findShortestPath("A", "X");
        assertFalseLocal(result2.canReach);
        
        PathResult result3 = graph.findShortestPath("X", "Y");
        assertFalseLocal(result3.canReach);
    }
    
    // @Test
    // @DisplayName("Test multiple paths same cost")
    void testMultiplePathsSameCost() {
        // Add alternative path with same cost
        graph.addEdge("A", "D", 3); // A->D(3)->E(2) = 5
        
        PathResult result = graph.findShortestPath("A", "E");
        
        assertTrueLocal(result.canReach);
        // Should find one of the shortest paths
        assertTrueLocal(result.shortestTime <= 6);
    }
    
    // @Test
    // @DisplayName("Test bridge optimization")
    void testBridgeOptimization() {
        graph.addNode("F"); // Add isolated node
        
        List<Solution.PotentialEdge> potentialEdges = Arrays.asList(
            new Solution.PotentialEdge("E", "F", 2),
            new Solution.PotentialEdge("D", "F", 5),
            new Solution.PotentialEdge("A", "F", 10)
        );
        
        BridgeResult result = graph.findOptimalBridge("A", "F", potentialEdges);
        
        assertNotNullLocal(result);
        assertEqualsLocal("E", result.fromNode);
        assertEqualsLocal("F", result.toNode);
        assertEqualsLocal(8, result.shortestTime); // A->B->C->D->E->F = 1+2+1+2+2 = 8
    }
    
    // @Test
    // @DisplayName("Test naive bridge approach")
    void testNaiveBridgeApproach() {
        graph.addNode("F"); // Add isolated node
        
        BridgeResult result = graph.findOptimalBridgeNaive("A", "F", 3);
        
        assertNotNullLocal(result);
        // Should find some bridge that connects A to F
        assertTrueLocal(result.shortestTime > 0);
        assertFalseLocal(result.path.isEmpty());
        assertEqualsLocal("A", result.path.get(0));
        assertEqualsLocal("F", result.path.get(result.path.size() - 1));
    }
    
    // @Test
    // @DisplayName("Test Floyd-Warshall all pairs shortest paths")
    void testFloydWarshall() {
        Map<String, Map<String, Integer>> allPairs = graph.computeAllPairsShortestPaths();
        
        // Test some known shortest distances
        assertEqualsLocal(1, (int) allPairs.get("A").get("B"));
        assertEqualsLocal(3, (int) allPairs.get("A").get("C")); // A->B->C
        assertEqualsLocal(4, (int) allPairs.get("A").get("D")); // A->B->C->D
        assertEqualsLocal(6, (int) allPairs.get("A").get("E")); // A->B->C->D->E
        
        // Test reverse direction (should be unreachable in directed graph)
        assertEqualsLocal(Integer.MAX_VALUE, (int) allPairs.get("E").get("A"));
    }
    
    // @Test
    // @DisplayName("Test graph with cycles")
    void testGraphWithCycles() {
        // Add edge to create cycle: E -> A
        graph.addEdge("E", "A", 1);
        
        PathResult result = graph.findShortestPath("A", "E");
        
        assertTrueLocal(result.canReach);
        // Should still find shortest path despite cycle
        assertEqualsLocal(6, result.shortestTime);
    }
    
    // @Test
    // @DisplayName("Test single node graph")
    void testSingleNodeGraph() {
        Solution.WeightedGraph singleGraph = new Solution.WeightedGraph();
        singleGraph.addNode("X");
        
        PathResult result = singleGraph.findShortestPath("X", "X");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(0, result.shortestTime);
        assertEqualsLocal(Arrays.asList("X"), result.path);
    }
    
    // @Test
    // @DisplayName("Test empty graph")
    void testEmptyGraph() {
        Solution.WeightedGraph emptyGraph = new Solution.WeightedGraph();
        
        PathResult result = emptyGraph.findShortestPath("A", "B");
        
        assertFalseLocal(result.canReach);
        assertEqualsLocal(-1, result.shortestTime);
        assertTrueLocal(result.path.isEmpty());
    }
    
    // @Test
    // @DisplayName("Test large weights")
    void testLargeWeights() {
        Solution.WeightedGraph heavyGraph = new Solution.WeightedGraph();
        heavyGraph.addEdge("X", "Y", Integer.MAX_VALUE - 1000);
        heavyGraph.addEdge("Y", "Z", 500);
        
        PathResult result = heavyGraph.findShortestPath("X", "Z");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(Integer.MAX_VALUE - 500, result.shortestTime);
    }
    
    // @Test
    // @DisplayName("Test zero weight edges")
    void testZeroWeightEdges() {
        graph.addEdge("A", "E", 0); // Direct zero-weight path
        
        PathResult result = graph.findShortestPath("A", "E");
        
        assertTrueLocal(result.canReach);
        assertEqualsLocal(0, result.shortestTime);
        assertEqualsLocal(Arrays.asList("A", "E"), result.path);
    }
    
    // @Test
    // @DisplayName("Test dense graph performance")
    void testDenseGraphPerformance() {
        Solution.WeightedGraph denseGraph = new Solution.WeightedGraph();
        
        // Create dense graph with 50 nodes
        int numNodes = 50;
        for (int i = 0; i < numNodes; i++) {
            for (int j = 0; j < numNodes; j++) {
                if (i != j) {
                    denseGraph.addEdge("Node" + i, "Node" + j, (i + j) % 10 + 1);
                }
            }
        }
        
        long startTime = System.currentTimeMillis();
        PathResult result = denseGraph.findShortestPath("Node0", "Node49");
        long endTime = System.currentTimeMillis();
        
        assertTrueLocal(result.canReach);
        assertTrueLocal(endTime - startTime < 1000); // Should complete within 1 second
    }
    
    // @Test
    // @DisplayName("Test bridge when no path exists initially")
    void testBridgeForDisconnectedComponents() {
        Solution.WeightedGraph disconnected = new Solution.WeightedGraph();
        
        // Component 1: A -> B
        disconnected.addEdge("A", "B", 1);
        
        // Component 2: C -> D
        disconnected.addEdge("C", "D", 1);
        
        // No path from A to C initially
        PathResult beforeBridge = disconnected.findShortestPath("A", "C");
        assertFalseLocal(beforeBridge.canReach);
        
        // Test bridge that connects components
        List<Solution.PotentialEdge> bridges = Arrays.asList(
            new Solution.PotentialEdge("B", "C", 2)
        );
        
        BridgeResult bridgeResult = disconnected.findOptimalBridge("A", "C", bridges);
        
        assertNotNullLocal(bridgeResult);
        assertEqualsLocal("B", bridgeResult.fromNode);
        assertEqualsLocal("C", bridgeResult.toNode);
        assertEqualsLocal(3, bridgeResult.shortestTime); // A->B->C = 1+2 = 3
    }
    
    // @Test
    // @DisplayName("Test multiple potential bridges")
    void testMultiplePotentialBridges() {
        graph.addNode("F");
        
        List<Solution.PotentialEdge> bridges = Arrays.asList(
            new Solution.PotentialEdge("A", "F", 20), // Expensive direct
            new Solution.PotentialEdge("C", "F", 1),  // Cheap from C
            new Solution.PotentialEdge("D", "F", 3),  // Medium from D
            new Solution.PotentialEdge("E", "F", 2)   // Cheap from E
        );
        
        BridgeResult result = graph.findOptimalBridge("A", "F", bridges);
        
        assertNotNullLocal(result);
        // Should choose the bridge that gives overall shortest path
        assertTrueLocal(result.shortestTime < 20); // Better than direct expensive bridge
    }
    
    // @Test
    // @DisplayName("Test path reconstruction accuracy")
    void testPathReconstructionAccuracy() {
        PathResult result = graph.findShortestPath("A", "E");
        
        assertTrueLocal(result.canReach);
        assertFalseLocal(result.path.isEmpty());
        
        // Verify path is valid by checking each edge exists and computing total weight
        int totalWeight = 0;
        for (int i = 0; i < result.path.size() - 1; i++) {
            String from = result.path.get(i);
            String to = result.path.get(i + 1);
            
            List<Edge> neighbors = graph.getNeighbors(from);
            Edge edge = neighbors.stream()
                .filter(e -> e.to.equals(to))
                .findFirst()
                .orElse(null);
            
            assertNotNullLocal(edge, "Edge " + from + " -> " + to + " should exist");
            totalWeight += edge.weight;
        }
        
        assertEqualsLocal(result.shortestTime, totalWeight);
    }
    
    // Helper assertion methods
    private static void assertEqualsLocal(Object expected, Object actual) {
        if (!expected.equals(actual)) {
            throw new AssertionError("Expected: " + expected + ", but was: " + actual);
        }
    }
    
    private static void assertTrueLocal(boolean condition) {
        if (!condition) {
            throw new AssertionError("Expected true, but was false");
        }
    }
    
    private static void assertFalseLocal(boolean condition) {
        if (condition) {
            throw new AssertionError("Expected false, but was true");
        }
    }
    
    private static void assertNotNullLocal(Object actual) {
        if (actual == null) {
            throw new AssertionError("Expected non-null, but was null");
        }
    }
    
    private static void assertNotNullLocal(Object actual, String message) {
        if (actual == null) {
            throw new AssertionError(message);
        }
    }
}