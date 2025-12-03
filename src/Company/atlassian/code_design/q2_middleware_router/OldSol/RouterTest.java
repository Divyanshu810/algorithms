package Company.atlassian.code_design.q2_middleware_router.OldSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Router implementations.
 */
public class RouterTest {

    private Router simpleRouter;
    private Router trieRouter;
    private Router patternRouter;

    @BeforeEach
    public void setUp() {
        // Create fresh router instances for each test
        simpleRouter = Solution.RouterFactory.createSimpleRouter();
        trieRouter = Solution.RouterFactory.createTrieRouter();
        patternRouter = Solution.RouterFactory.createPatternRouter();

        // Add common routes for all routers
        String[][] routes = {
            {"/users", "Users list"},
            {"/users/profile", "User profile"},
            {"/products", "Products list"}
        };

        for (String[] route : routes) {
            simpleRouter.addRoute(route[0], route[1]);
            trieRouter.addRoute(route[0], route[1]);
            patternRouter.addRoute(route[0], route[1]);
        }
    }

    @Test
    @DisplayName("SimpleRouter: Test exact matches")
    public void testSimpleRouterExactMatches() {
        assertEquals("Users list", simpleRouter.callRoute("/users"));
        assertEquals("User profile", simpleRouter.callRoute("/users/profile"));
        assertEquals("Products list", simpleRouter.callRoute("/products"));
    }

    @Test
    @DisplayName("SimpleRouter: Test non-existent routes")
    public void testSimpleRouterNonExistentRoutes() {
        assertNull(simpleRouter.callRoute("/nonexistent"));
        assertNull(simpleRouter.callRoute("/users/nonexistent"));
    }

    @Test
    @DisplayName("SimpleRouter: Test illegal arguments")
    public void testSimpleRouterIllegalArguments() {
        assertThrows(IllegalArgumentException.class, () -> simpleRouter.addRoute(null, "value"));
        assertThrows(IllegalArgumentException.class, () -> simpleRouter.addRoute("/path", null));
        assertNull(simpleRouter.callRoute(null));
    }

    @Test
    @DisplayName("TrieRouter: Test exact matches")
    public void testTrieRouterExactMatches() {
        assertEquals("Users list", trieRouter.callRoute("/users"));
        assertEquals("User profile", trieRouter.callRoute("/users/profile"));
        assertEquals("Products list", trieRouter.callRoute("/products"));
    }

    @Test
    @DisplayName("TrieRouter: Test wildcard routes")
    public void testTrieRouterWildcardRoutes() {
        trieRouter.addRoute("/users/*/posts", "User posts");
        trieRouter.addRoute("/api/*/data", "API data");

        assertEquals("User posts", trieRouter.callRoute("/users/123/posts"));
        assertEquals("User posts", trieRouter.callRoute("/users/john/posts"));
        assertEquals("API data", trieRouter.callRoute("/api/v1/data"));
        assertEquals("API data", trieRouter.callRoute("/api/v2/data"));
    }

    @Test
    @DisplayName("TrieRouter: Test parameterized routes")
    public void testTrieRouterParameterizedRoutes() {
        trieRouter.addRoute("/users/:id", "User by ID");
        trieRouter.addRoute("/users/:id/posts/:postId", "Specific user post");

        Solution.TrieRouter routerWithParams = (Solution.TrieRouter) trieRouter;
        
        RouteResult result = routerWithParams.callRouteWithParams("/users/123");
        assertNotNull(result);
        assertEquals("User by ID", result.getResult());
        assertEquals("123", result.getPathParams().get("id"));

        result = routerWithParams.callRouteWithParams("/users/456/posts/789");
        assertNotNull(result);
        assertEquals("Specific user post", result.getResult());
        assertEquals("456", result.getPathParams().get("id"));
        assertEquals("789", result.getPathParams().get("postId"));
    }

    @Test
    @DisplayName("TrieRouter: Test priority order")
    public void testTrieRouterPriorityOrder() {
        // Exact routes should have priority over wildcard and parameterized routes
        trieRouter.addRoute("/users/:id", "User by ID");
        trieRouter.addRoute("/users/*", "Any user path");
        
        assertEquals("Users list", trieRouter.callRoute("/users"));
        assertEquals("User profile", trieRouter.callRoute("/users/profile"));
        
        Solution.TrieRouter routerWithParams = (Solution.TrieRouter) trieRouter;
        RouteResult result = routerWithParams.callRouteWithParams("/users/123");
        assertNotNull(result);
        assertEquals("User by ID", result.getResult());
    }

    @Test
    @DisplayName("PatternRouter: Test exact matches")
    public void testPatternRouterExactMatches() {
        assertEquals("Users list", patternRouter.callRoute("/users"));
        assertEquals("User profile", patternRouter.callRoute("/users/profile"));
        assertEquals("Products list", patternRouter.callRoute("/products"));
    }

    @Test
    @DisplayName("PatternRouter: Test wildcard and parameter routes")
    public void testPatternRouterWildcardAndParameterRoutes() {
        patternRouter.addRoute("/users/*/posts", "User posts");
        patternRouter.addRoute("/users/:id", "User by ID");
        patternRouter.addRoute("/api/*/data", "API data");

        assertEquals("User posts", patternRouter.callRoute("/users/123/posts"));
        assertEquals("User by ID", patternRouter.callRoute("/users/456"));
        assertEquals("API data", patternRouter.callRoute("/api/v2/data"));
    }

    @Test
    @DisplayName("All Routers: Test concurrent access")
    public void testConcurrentAccess() throws InterruptedException {
        // This test simulates concurrent access to the routers
        // We'll use multiple threads to add and retrieve routes
        
        final int THREAD_COUNT = 10;
        final int ROUTES_PER_THREAD = 100;
        
        Thread[] threads = new Thread[THREAD_COUNT];
        
        for (int t = 0; t < THREAD_COUNT; t++) {
            final int threadId = t;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ROUTES_PER_THREAD; i++) {
                    String path = "/thread" + threadId + "/route" + i;
                    String result = "Result " + threadId + "-" + i;
                    
                    simpleRouter.addRoute(path, result);
                    trieRouter.addRoute(path, result);
                    patternRouter.addRoute(path, result);
                    
                    assertEquals(result, simpleRouter.callRoute(path));
                    assertEquals(result, trieRouter.callRoute(path));
                    assertEquals(result, patternRouter.callRoute(path));
                }
            });
            
            threads[t].start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Verify a few random routes
        for (int t = 0; t < THREAD_COUNT; t++) {
            for (int i = 0; i < ROUTES_PER_THREAD; i += 20) {
                String path = "/thread" + t + "/route" + i;
                String expectedResult = "Result " + t + "-" + i;
                
                assertEquals(expectedResult, simpleRouter.callRoute(path));
                assertEquals(expectedResult, trieRouter.callRoute(path));
                assertEquals(expectedResult, patternRouter.callRoute(path));
            }
        }
    }
}