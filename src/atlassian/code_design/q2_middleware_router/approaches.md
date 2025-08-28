# Middleware Router - Implementation Approaches

## Problem Analysis
We need to implement a middleware router that:
1. Maps URL paths to result strings
2. Supports wildcards (scale-up 1)
3. Supports path parameters (scale-up 2)
4. Uses ordered checking for wildcards

## Approach 1: HashMap-based Simple Router

### Description
Use a simple HashMap to store exact path matches.

### Implementation
```java
class SimpleRouter {
    private Map<String, String> routes = new HashMap<>();
    
    public void addRoute(String path, String result) {
        routes.put(path, result);
    }
    
    public String callRoute(String path) {
        return routes.get(path);
    }
}
```

### Pros
- O(1) lookup time
- Simple implementation
- Fast for exact matches
- Memory efficient for small route sets

### Cons
- No wildcard support
- No path parameter extraction
- Limited functionality for real-world routing

### Time Complexity
- Add Route: O(1)
- Call Route: O(1)

### Space Complexity
- O(n) where n is number of routes

---

## Approach 2: Trie-based Router with Wildcard Support

### Description
Use a Trie (prefix tree) to store routes, supporting wildcards (*) and path parameters.

### Implementation
```java
class TrieRouter {
    class TrieNode {
        Map<String, TrieNode> children = new HashMap<>();
        String result = null;
        boolean isWildcard = false;
        String paramName = null;
    }
    
    private TrieNode root = new TrieNode();
    private List<Route> orderedRoutes = new ArrayList<>();
}
```

### Pros
- Supports wildcards and path parameters
- Efficient prefix matching
- Ordered checking for wildcards
- Scalable for large route sets
- Can extract path parameters

### Cons
- More complex implementation
- Higher memory usage
- Slightly slower for simple exact matches

### Time Complexity
- Add Route: O(k) where k is path length
- Call Route: O(k + r) where r is number of wildcard routes to check

### Space Complexity
- O(n * k) where n is routes, k is average path length

---

## Approach 3: Hybrid Router with Route Patterns

### Description
Combine HashMap for exact matches with regex/pattern matching for wildcards.

### Implementation
```java
class HybridRouter {
    private Map<String, String> exactRoutes = new HashMap<>();
    private List<RoutePattern> patternRoutes = new ArrayList<>();
    
    class RoutePattern {
        Pattern pattern;
        String template;
        String result;
        List<String> paramNames;
    }
}
```

### Pros
- Fast exact matching (O(1))
- Flexible pattern matching with regex
- Support for complex routing patterns
- Good balance of performance and features

### Cons
- Regex compilation overhead
- More complex pattern management
- Potential regex performance issues

### Time Complexity
- Add Route: O(1) for exact, O(k) for patterns
- Call Route: O(1) for exact, O(p) for patterns where p is pattern count

### Space Complexity
- O(n + p) where n is exact routes, p is pattern routes

---

## Recommended Approach: Trie-based Router (Approach 2)

### Why Trie-based?
1. **Scalability**: Handles both simple and complex routing efficiently
2. **Feature Complete**: Supports wildcards, path parameters, and ordered checking
3. **Performance**: Good balance for real-world usage patterns
4. **Extensibility**: Easy to add new routing features

### Key Features Implementation:
1. **Exact Matching**: Direct path traversal in trie
2. **Wildcard Support**: Special wildcard nodes that match any segment
3. **Path Parameters**: Extract parameters during traversal
4. **Ordered Checking**: Process routes in registration order

### Production Considerations:
1. Thread safety with concurrent reads/writes
2. Route validation and conflict detection
3. Performance monitoring and metrics
4. Route priority and fallback handling