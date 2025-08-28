package atlassian.code_design.q2_middleware_router;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
public class option {


    // Router interface assumed from context; keeping original signatures and adding safe variants.
    interface Router {
        void addRoute(String path, String result);
        String callRoute(String path); // legacy/compat
    }

    public final class SimpleRouter implements Router {
        private final Map<String, String> routes;

        public SimpleRouter() {
            this.routes = new ConcurrentHashMap<>();
        }

        @Override
        public void addRoute(String path, String result) {
            // Null checks first (clear messages)
            Objects.requireNonNull(path, "path cannot be null");
            Objects.requireNonNull(result, "result cannot be null");

            String normalized = normalize(path);
            if (isBlank(normalized)) {
                throw new IllegalArgumentException("path cannot be blank");
            }
            if (isBlank(result)) {
                throw new IllegalArgumentException("result cannot be blank");
            }
            routes.put(normalized, result);
        }

        /**
         * Legacy/compat method. Returns the mapped value or null if missing.
         * Prefer callRouteOptional or callRouteOrDefault to avoid nulls.
         */
        @Override
        public String callRoute(String path) {
            String normalized = requireAndNormalizePath(path);
            return routes.get(normalized); // may return null for legacy callers
        }

        /** Safe: returns Optional instead of null. */
        public Optional<String> callRouteOptional(String path) {
            String normalized = requireAndNormalizePath(path);
            return Optional.ofNullable(routes.get(normalized));
        }

        /** Safe: returns defaultValue if route is absent. defaultValue may be null (caller’s choice). */
        public String callRouteOrDefault(String path, String defaultValue) {
            String normalized = requireAndNormalizePath(path);
            return routes.getOrDefault(normalized, defaultValue);
        }

        public int getRouteCount() {
            return routes.size();
        }

        // ---------- helpers (small, simple) ----------

        private String requireAndNormalizePath(String path) {
            Objects.requireNonNull(path, "path cannot be null");
            String normalized = normalize(path);
            if (isBlank(normalized)) {
                throw new IllegalArgumentException("path cannot be blank");
            }
            return normalized;
        }

        private static boolean isBlank(String s) {
            if (s == null) return true;
            for (int i = 0; i < s.length(); i++) {
                if (!Character.isWhitespace(s.charAt(i))) return false;
            }
            return true;
        }

        /**
         * Normalizes a path:
         * - trims whitespace
         * - ensures leading '/'
         * - removes trailing '/' unless path is root
         */
        private static String normalize(String raw) {
            if (raw == null) return null; // upstream handles null
            String s = raw.trim();
            if (s.isEmpty()) return s;
            if (!s.startsWith("/")) s = "/" + s;
            if (s.length() > 1 && s.endsWith("/")) s = s.substring(0, s.length() - 1);
            return s;
        }
    }

}
