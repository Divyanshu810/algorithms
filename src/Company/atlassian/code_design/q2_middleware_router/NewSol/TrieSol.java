package Company.atlassian.code_design.q2_middleware_router.NewSol;

import java.util.*;

class TrieNode {
    Map<String, TrieNode> children = new HashMap<>();
    TrieNode wildcardChild = null;
    TrieNode paramChild = null;
    String paramName = null;
    String result = null;
    boolean isEnd = false;
}

class TrieRouter implements Router {
    private TrieNode root = new TrieNode();
    private Map<String, String> pathParams = new HashMap<>();

    @Override
    public void addRoute(String path, String result) {
        TrieNode current = root;
        String[] segments = path.split("/");

        for (String seg : segments) {
            if (seg.isEmpty()) continue;

            if (seg.equals("*")) {
                if (current.wildcardChild == null) {
                    current.wildcardChild = new TrieNode();
                }
                current = current.wildcardChild;
            } else if (seg.startsWith("{") && seg.endsWith("}")) {
                if (current.paramChild == null) {
                    current.paramChild = new TrieNode();
                    current.paramName = seg.substring(1, seg.length() - 1);
                }
                current = current.paramChild;
            } else {
                current.children.putIfAbsent(seg, new TrieNode());
                current = current.children.get(seg);
            }
        }

        current.isEnd = true;
        current.result = result;
    }

    @Override
    public String callRoute(String path) {
        pathParams.clear();
        String[] segments = path.split("/");

        TrieNode current = root;
        Map<String, String> params = new HashMap<>();

        for (String seg : segments) {
            if (seg.isEmpty()) continue;

            if (current == null) return null;

            if (current.children.containsKey(seg)) {
                current = current.children.get(seg);
            } else if (current.paramChild != null) {
                params.put(current.paramName, seg);
                current = current.paramChild;
            } else if (current.wildcardChild != null) {
                current = current.wildcardChild;
            } else {
                return null;
            }
        }

        if (current != null && current.isEnd) {
            pathParams = params;
            return current.result;
        }
        return null;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }
}

public class TrieSol {
    public static void main(String[] args) {
        TrieRouter router = new TrieRouter();

        // Exact matches
        router.addRoute("/bar", "bar-result");
        router.addRoute("/api/users", "users-result");

        // Wildcard
        router.addRoute("/api/*/test", "wildcard-result");

        // Path params
        router.addRoute("/user/{id}", "user-result");
        router.addRoute("/user/{id}/posts", "posts-result");

        // Test exact
        System.out.println("/bar -> " + router.callRoute("/bar"));
        // Output: bar-result

        System.out.println("/api/users -> " + router.callRoute("/api/users"));
        // Output: users-result

        // Test wildcard
        System.out.println("/api/v1/test -> " + router.callRoute("/api/v1/test"));
        // Output: wildcard-result

        System.out.println("/api/v2/test -> " + router.callRoute("/api/v2/test"));
        // Output: wildcard-result

        // Test params
        System.out.println("/user/123 -> " + router.callRoute("/user/123"));
        System.out.println("Params: " + router.getPathParams());
        // Output: user-result, {id=123}

        System.out.println("/user/456/posts -> " + router.callRoute("/user/456/posts"));
        System.out.println("Params: " + router.getPathParams());
        // Output: posts-result, {id=456}

        // Test not found
        System.out.println("/unknown -> " + router.callRoute("/unknown"));
        // Output: null
    }
}
/*
```

        ---

        ## Recursion vs Iteration Comparison

| Aspect | Recursive | Iterative (For Loop) |
        |--------|-----------|---------------------|
        | **Code Complexity** | Slightly more | Simpler |
        | **Stack Usage** | O(M) call stack | O(1) stack |
        | **Backtracking** | Natural | Not supported* |
        | **Readability** | Good | Better |
        | **Performance** | Slightly slower | Faster |

        *Note: The iterative version uses **greedy matching** (first valid path wins). For most routing use cases, this is sufficient and actually preferred behavior.

        ---

        ## When Would You Need Backtracking?

        **Scenario:**
        ```
Routes:
        /user/{id}/profile  → "profile-result"
        /user/admin/settings → "admin-result"

Query: /user/admin/settings
```

        **Greedy (Iterative):**
        ```
        1. "user" → exact match ✓
        2. "admin" → no exact, paramChild exists → takes {id} path
3. "settings" → no "settings" under {id}/profile path
4. Returns null ✗ (wrong!)
        ```

        **With Backtracking (Recursive):**
        ```
        1. "user" → exact match ✓
        2. "admin" → try paramChild first → fails later → backtrack
3. "admin" → try exact match → "admin" exists ✓
        4. "settings" → exact match ✓
        5. Returns "admin-result" ✓

 */