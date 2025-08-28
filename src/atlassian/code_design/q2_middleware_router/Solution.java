package atlassian.code_design.q2_middleware_router;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

interface Router {
    void addRoute(String path, String result);
    String callRoute(String path);
}

class RouteResult {
    private String result;
    private Map<String, String> pathParams;
    
    public RouteResult(String result) {
        this.result = result;
        this.pathParams = new HashMap<>();
    }
    
    public RouteResult(String result, Map<String, String> pathParams) {
        this.result = result;
        this.pathParams = pathParams;
    }
    
    public String getResult() { return result; }
    public Map<String, String> getPathParams() { return pathParams; }
}

public class Solution {
    
    // Approach 1: Simple HashMap Router
    public static class SimpleRouter implements Router {
        private Map<String, String> routes;
        
        public SimpleRouter() {
            this.routes = new ConcurrentHashMap<>();
        }
        
        @Override
        public void addRoute(String path, String result) {
            if (path == null || result == null) {
                throw new IllegalArgumentException("Path and result cannot be null");
            }
            routes.put(path, result);
        }
        
        @Override
        public String callRoute(String path) {
            return routes.get(path);
        }
        
        public int getRouteCount() {
            return routes.size();
        }
    }
    
    // Approach 2: Trie-based Router with Wildcard and Path Parameter Support
    public static class TrieRouter implements Router {
        private TrieNode root;
        private List<RouteEntry> orderedRoutes;
        private ReadWriteLock lock;
        
        private static class TrieNode {
            Map<String, TrieNode> children;
            String result;
            boolean isWildcard;
            String paramName;
            
            public TrieNode() {
                this.children = new HashMap<>();
                this.result = null;
                this.isWildcard = false;
                this.paramName = null;
            }
        }
        
        private static class RouteEntry {
            String path;
            String result;
            boolean hasWildcard;
            boolean hasParams;
            
            public RouteEntry(String path, String result, boolean hasWildcard, boolean hasParams) {
                this.path = path;
                this.result = result;
                this.hasWildcard = hasWildcard;
                this.hasParams = hasParams;
            }
        }
        
        public TrieRouter() {
            this.root = new TrieNode();
            this.orderedRoutes = new ArrayList<>();
            this.lock = new ReentrantReadWriteLock();
        }
        
        @Override
        public void addRoute(String path, String result) {
            if (path == null || result == null) {
                throw new IllegalArgumentException("Path and result cannot be null");
            }

            lock.writeLock().lock();
            try {
                String[] segments = parsePath(path);
                TrieNode current = root;
                boolean hasWildcard = false;
                boolean hasParams = false;
                for (String segment : segments) {
                    if (segment.equals("*")) {
                        hasWildcard = true;
                        if (!current.children.containsKey("*")) {
                            TrieNode wildcardNode = new TrieNode();
                            wildcardNode.isWildcard = true;
                            current.children.put("*", wildcardNode);
                        }
                        current = current.children.get("*");
                    } else if (segment.startsWith(":")) {
                        hasParams = true;
                        String paramName = segment.substring(1);
                        if (!current.children.containsKey(":param")) {
                            TrieNode paramNode = new TrieNode();
                            paramNode.paramName = paramName;
                            current.children.put(":param", paramNode);
                        }
                        current = current.children.get(":param");
                    } else {
                        current.children.putIfAbsent(segment, new TrieNode());
                        current = current.children.get(segment);
                    }
                }
                
                current.result = result;
                orderedRoutes.add(new RouteEntry(path, result, hasWildcard, hasParams));
            } finally {
                lock.writeLock().unlock();
            }
        }
        
        @Override
        public String callRoute(String path) {
            RouteResult result = callRouteWithParams(path);
            return result != null ? result.getResult() : null;
        }
        
        public RouteResult callRouteWithParams(String path) {
            if (path == null) {
                return null;
            }
            
            lock.readLock().lock();
            try {
                String[] segments = parsePath(path);
                
                // Try exact match first
                RouteResult exactMatch = findExactMatch(segments);
                if (exactMatch != null) {
                    return exactMatch;
                }
                
                // Try wildcard and parameter matches in order
                for (RouteEntry route : orderedRoutes) {
                    if (route.hasWildcard || route.hasParams) {
                        RouteResult match = tryMatchRoute(segments, route);
                        if (match != null) {
                            return match;
                        }
                    }
                }
                
                return null;
            } finally {
                lock.readLock().unlock();
            }
        }
        
        private RouteResult findExactMatch(String[] segments) {
            TrieNode current = root;
            
            for (String segment : segments) {
                current = current.children.get(segment);
                if (current == null) {
                    return null;
                }
            }
            
            return current.result != null ? new RouteResult(current.result) : null;
        }
        private RouteResult tryMatchRoute(String[] pathSegments, RouteEntry route) {
            String[] routeSegments = parsePath(route.path);
            
            if (routeSegments.length != pathSegments.length) {
                return null;
            }
            
            Map<String, String> pathParams = new HashMap<>();
            
            for (int i = 0; i < routeSegments.length; i++) {
                String routeSegment = routeSegments[i];
                String pathSegment = pathSegments[i];
                
                if (routeSegment.equals("*")) {
                    // Wildcard matches anything
                    continue;
                } else if (routeSegment.startsWith(":")) {
                    // Path parameter
                    String paramName = routeSegment.substring(1);
                    pathParams.put(paramName, pathSegment);
                } else if (!routeSegment.equals(pathSegment)) {
                    // Exact segment doesn't match
                    return null;
                }
            }
            
            return new RouteResult(route.result, pathParams);
        }
        
        private String[] parsePath(String path) {
            if (path == null || path.isEmpty() || path.equals("/")) {
                return new String[0];
            }
            // Remove leading and trailing slashes
            String cleanPath = path.replaceAll("^/+|/+$", "");
            return cleanPath.split("/");
        }
        
        public List<String> getAllRoutes() {
            lock.readLock().lock();
            try {
                return orderedRoutes.stream()
                    .map(route -> route.path)
                    .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);
            } finally {
                lock.readLock().unlock();
            }
        }
        
        public int getRouteCount() {
            return orderedRoutes.size();
        }
    }
    
    // Approach 3: Pattern-based Router using Regex
    public static class PatternRouter implements Router {
        private Map<String, String> exactRoutes;
        private List<RoutePattern> patternRoutes;
        private ReadWriteLock lock;
        
        private static class RoutePattern {
            String originalPath;
            String pattern;
            String result;
            List<String> paramNames;
            
            public RoutePattern(String originalPath, String pattern, String result, List<String> paramNames) {
                this.originalPath = originalPath;
                this.pattern = pattern;
                this.result = result;
                this.paramNames = paramNames;
            }
        }
        
        public PatternRouter() {
            this.exactRoutes = new ConcurrentHashMap<>();
            this.patternRoutes = new ArrayList<>();
            this.lock = new ReentrantReadWriteLock();
        }
        
        @Override
        public void addRoute(String path, String result) {
            if (path == null || result == null) {
                throw new IllegalArgumentException("Path and result cannot be null");
            }
            
            if (isExactRoute(path)) {
                exactRoutes.put(path, result);
            } else {
                lock.writeLock().lock();
                try {
                    String pattern = buildPattern(path);
                    List<String> paramNames = extractParamNames(path);
                    patternRoutes.add(new RoutePattern(path, pattern, result, paramNames));
                } finally {
                    lock.writeLock().unlock();
                }
            }
        }
        
        @Override
        public String callRoute(String path) {
            // Try exact match first
            String exactResult = exactRoutes.get(path);
            if (exactResult != null) {
                return exactResult;
            }
            
            // Try pattern matches
            lock.readLock().lock();
            try {
                for (RoutePattern routePattern : patternRoutes) {
                    if (path.matches(routePattern.pattern)) {
                        return routePattern.result;
                    }
                }
            } finally {
                lock.readLock().unlock();
            }
            
            return null;
        }
        
        private boolean isExactRoute(String path) {
            return !path.contains("*") && !path.contains(":");
        }
        
        private String buildPattern(String path) {
            String pattern = path;
            
            // Replace wildcards
            pattern = pattern.replace("*", "[^/]+");
            
            // Replace path parameters
            pattern = pattern.replaceAll(":[^/]+", "([^/]+)");
            
            // Escape special regex characters
            pattern = pattern.replace("/", "\\/");
            
            return "^" + pattern + "$";
        }
        
        private List<String> extractParamNames(String path) {
            List<String> paramNames = new ArrayList<>();
            String[] segments = path.split("/");
            
            for (String segment : segments) {
                if (segment.startsWith(":")) {
                    paramNames.add(segment.substring(1));
                }
            }
            
            return paramNames;
        }
        
        public int getRouteCount() {
            return exactRoutes.size() + patternRoutes.size();
        }
    }
    
    // Router Factory
    public static class RouterFactory {
        public static Router createSimpleRouter() {
            return new SimpleRouter();
        }
        
        public static Router createTrieRouter() {
            return new TrieRouter();
        }
        
        public static Router createPatternRouter() {
            return new PatternRouter();
        }
    }
    
    public static void main(String[] args) {
        System.out.println("=== Testing Simple Router ===");
        testSimpleRouter();
        
        System.out.println("\n=== Testing Trie Router ===");
        testTrieRouter();
        
        System.out.println("\n=== Testing Pattern Router ===");
        testPatternRouter();
        
        System.out.println("\n=== Performance Comparison ===");
        performanceTest();
    }
    
    private static void testSimpleRouter() {
        Router router = RouterFactory.createSimpleRouter();
        
        router.addRoute("/users", "Users list");
        router.addRoute("/users/profile", "User profile");
        router.addRoute("/products", "Products list");
        
        System.out.println("Route '/users': " + router.callRoute("/users"));
        System.out.println("Route '/users/profile': " + router.callRoute("/users/profile"));
        System.out.println("Route '/nonexistent': " + router.callRoute("/nonexistent"));
    }
    
    private static void testTrieRouter() {
        TrieRouter router = (TrieRouter) RouterFactory.createTrieRouter();
        
        // Add exact routes
        router.addRoute("/users", "Users list");
        router.addRoute("/users/profile", "User profile");
        
        // Add wildcard routes
        router.addRoute("/users/*/posts", "User posts");
        router.addRoute("/api/*/data", "API data");
        
        // Add parameterized routes
        router.addRoute("/users/:id", "User by ID");
        router.addRoute("/users/:id/posts/:postId", "Specific user post");
        
        System.out.println("Route '/users': " + router.callRoute("/users"));
        System.out.println("Route '/users/123/posts': " + router.callRoute("/users/123/posts"));
        System.out.println("Route '/api/v1/data': " + router.callRoute("/api/v1/data"));
        
        RouteResult result = router.callRouteWithParams("/users/123");
        if (result != null) {
            System.out.println("Route '/users/123': " + result.getResult());
            System.out.println("Path params: " + result.getPathParams());
        }
        
        result = router.callRouteWithParams("/users/456/posts/789");
        if (result != null) {
            System.out.println("Route '/users/456/posts/789': " + result.getResult());
            System.out.println("Path params: " + result.getPathParams());
        }
    }
    
    private static void testPatternRouter() {
        Router router = RouterFactory.createPatternRouter();
        
        router.addRoute("/users", "Users list");
        router.addRoute("/users/*/posts", "User posts");
        router.addRoute("/users/:id", "User by ID");
        router.addRoute("/api/*/data", "API data");
        
        System.out.println("Route '/users': " + router.callRoute("/users"));
        System.out.println("Route '/users/123/posts': " + router.callRoute("/users/123/posts"));
        System.out.println("Route '/users/456': " + router.callRoute("/users/456"));
        System.out.println("Route '/api/v2/data': " + router.callRoute("/api/v2/data"));
    }
    
    private static void performanceTest() {
        int numRoutes = 1000;
        int numLookups = 10000;
        
        // Test Simple Router
        long start = System.currentTimeMillis();
        Router simpleRouter = RouterFactory.createSimpleRouter();
        for (int i = 0; i < numRoutes; i++) {
            simpleRouter.addRoute("/route" + i, "result" + i);
        }
        for (int i = 0; i < numLookups; i++) {
            simpleRouter.callRoute("/route" + (i % numRoutes));
        }
        long simpleTime = System.currentTimeMillis() - start;
        
        // Test Trie Router
        start = System.currentTimeMillis();
        Router trieRouter = RouterFactory.createTrieRouter();
        for (int i = 0; i < numRoutes; i++) {
            trieRouter.addRoute("/route" + i, "result" + i);
        }
        for (int i = 0; i < numLookups; i++) {
            trieRouter.callRoute("/route" + (i % numRoutes));
        }
        long trieTime = System.currentTimeMillis() - start;
        
        System.out.println("Simple Router: " + simpleTime + "ms");
        System.out.println("Trie Router: " + trieTime + "ms");
        System.out.println("Routes: " + numRoutes + ", Lookups: " + numLookups);
    }
}