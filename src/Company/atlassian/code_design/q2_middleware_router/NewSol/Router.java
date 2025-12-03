package Company.atlassian.code_design.q2_middleware_router.NewSol;

// ============ Router.java ============
public interface Router {
    void addRoute(String path, String result);
    String callRoute(String path);
}
