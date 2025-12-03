package Company.atlassian.code_design.q2_middleware_router.NewSol;
import java.util.*;
public class HashMapSol {
    class SimpleRouter {
        private Map<String, String> routes = new HashMap<>();

        public void addRoute(String path, String result) {
            routes.put(path, result);
        }

        public String callRoute(String path) {
            return routes.get(path);  // O(1) lookup
        }
    }
}
