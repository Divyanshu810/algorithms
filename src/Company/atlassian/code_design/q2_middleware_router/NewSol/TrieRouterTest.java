package Company.atlassian.code_design.q2_middleware_router.NewSol;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TrieRouterTest {

    @Nested
    @DisplayName("Exact Match Tests")
    class ExactMatchTests {

        private TrieRouter router;

        @BeforeEach
        void setUp() {
            router = new TrieRouter();
            router.addRoute("/home", "home-page");
            router.addRoute("/api/users", "users-list");
            router.addRoute("/api/posts", "posts-list");
        }

        @Test
        @DisplayName("Should match exact routes")
        void testExactMatches() {
            assertEquals("home-page", router.callRoute("/home"));
            assertEquals("users-list", router.callRoute("/api/users"));
            assertEquals("posts-list", router.callRoute("/api/posts"));
        }

        @Test
        @DisplayName("Should return null for unknown routes")
        void testNotFound() {
            assertNull(router.callRoute("/unknown"));
            assertNull(router.callRoute("/api/unknown"));
        }
    }

    @Nested
    @DisplayName("Wildcard Tests")
    class WildcardTests {

        private TrieRouter router;

        @BeforeEach
        void setUp() {
            router = new TrieRouter();
            router.addRoute("/api/*/test", "wildcard-result");
            router.addRoute("/files/*/download", "download-result");
        }

        @Test
        @DisplayName("Should match single segment wildcard")
        void testWildcardMatches() {
            assertEquals("wildcard-result", router.callRoute("/api/v1/test"));
            assertEquals("wildcard-result", router.callRoute("/api/v2/test"));
            assertEquals("wildcard-result", router.callRoute("/api/anything/test"));
            assertEquals("download-result", router.callRoute("/files/images/download"));
        }

        @Test
        @DisplayName("Should not match wrong path after wildcard")
        void testWildcardNotMatch() {
            assertNull(router.callRoute("/api/v1/wrong"));
            assertNull(router.callRoute("/files/images/upload"));
        }

        @Test
        @DisplayName("Wildcard should not capture params")
        void testWildcardNoParams() {
            router.callRoute("/api/v1/test");
            assertTrue(router.getPathParams().isEmpty());
        }
    }

    @Nested
    @DisplayName("Path Param Tests")
    class ParamTests {

        private TrieRouter router;

        @BeforeEach
        void setUp() {
            router = new TrieRouter();
            router.addRoute("/user/{id}", "user-result");
            router.addRoute("/user/{userId}/post/{postId}", "post-result");
        }

        @Test
        @DisplayName("Should match and capture single param")
        void testSingleParam() {
            assertEquals("user-result", router.callRoute("/user/123"));
            assertEquals("123", router.getPathParams().get("id"));

            assertEquals("user-result", router.callRoute("/user/456"));
            assertEquals("456", router.getPathParams().get("id"));
        }

        @Test
        @DisplayName("Should capture string param values")
        void testStringParam() {
            assertEquals("user-result", router.callRoute("/user/john"));
            assertEquals("john", router.getPathParams().get("id"));
        }

        @Test
        @DisplayName("Should match and capture multiple params")
        void testMultipleParams() {
            assertEquals("post-result", router.callRoute("/user/100/post/200"));

            Map<String, String> params = router.getPathParams();
            assertEquals("100", params.get("userId"));
            assertEquals("200", params.get("postId"));
        }

        @Test
        @DisplayName("Should capture different values on each call")
        void testParamValueChanges() {
            router.callRoute("/user/first");
            assertEquals("first", router.getPathParams().get("id"));

            router.callRoute("/user/second");
            assertEquals("second", router.getPathParams().get("id"));
        }
    }

    @Nested
    @DisplayName("Priority Tests")
    class PriorityTests {

        private TrieRouter router;

        @BeforeEach
        void setUp() {
            router = new TrieRouter();
            router.addRoute("/api/users", "exact-result");
            router.addRoute("/api/{resource}", "param-result");
            router.addRoute("/api/*", "wildcard-result");
        }

        @Test
        @DisplayName("Exact match should have highest priority")
        void testExactPriority() {
            assertEquals("exact-result", router.callRoute("/api/users"));
        }

        @Test
        @DisplayName("Param should have priority over wildcard")
        void testParamOverWildcard() {
            assertEquals("param-result", router.callRoute("/api/posts"));
            assertEquals("posts", router.getPathParams().get("resource"));
        }
    }

    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {

        private TrieRouter router;

        @BeforeEach
        void setUp() {
            router = new TrieRouter();
            router.addRoute("/", "root");
            router.addRoute("/a/b/c/d", "deep-path");
        }

        @Test
        @DisplayName("Should match root path")
        void testRootPath() {
            assertEquals("root", router.callRoute("/"));
        }

        @Test
        @DisplayName("Should match deep nested path")
        void testDeepPath() {
            assertEquals("deep-path", router.callRoute("/a/b/c/d"));
        }

        @Test
        @DisplayName("Should not match partial path")
        void testPartialPath() {
            assertNull(router.callRoute("/a/b"));
            assertNull(router.callRoute("/a/b/c"));
        }

        @Test
        @DisplayName("Should handle empty string")
        void testEmptyString() {
            assertNull(router.callRoute(""));
        }
    }
}