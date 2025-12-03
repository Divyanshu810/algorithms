package Company.atlassian.data_structures.q1_closest_org.NewSol;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Find Closest Common Group for Target Employees
 *
 * ===================================================================================
 * PROBLEM OVERVIEW:
 * ===================================================================================
 * Given an organization hierarchy where:
 * - Groups can contain subgroups and/or employees
 * - Find the closest (lowest) common parent group for a set of employees
 *
 * This is essentially the Lowest Common Ancestor (LCA) problem for multiple nodes.
 *
 * ===================================================================================
 * PART A: Basic Tree Structure (Groups form a tree, each employee in one group)
 * PART B: DAG Structure (Groups/employees can be shared across multiple parents)
 * PART C: Concurrency handling with ReadWriteLock
 * PART D: Single level groups (no subgroups)
 * ===================================================================================
 */
public class ClosestCommonGroup {

    // ==================== PART A: Tree Structure ====================
    /**
     * Basic hierarchy where:
     * - Each group has exactly ONE parent (except root)
     * - Each employee belongs to exactly ONE group
     *
     * Approach (Brute Force):
     * 1. For each employee, collect all ancestor groups (path to root)
     * 2. Find intersection of all ancestor sets
     * 3. Return the deepest (closest) common ancestor
     *
     * Time: O(E * H) where E = employees, H = height of tree
     * Space: O(E * H) for storing ancestor paths
     */
    static class PartA_TreeStructure {

        private Map<String, String> groupParent;      // group -> parent group
        private Map<String, String> employeeGroup;    // employee -> their group
        private String rootGroup;

        public PartA_TreeStructure(String rootGroup) {
            this.rootGroup = rootGroup;
            this.groupParent = new HashMap<String, String>();
            this.employeeGroup = new HashMap<String, String>();
        }

        public void addGroup(String group, String parent) {
            groupParent.put(group, parent);
        }

        public void addEmployee(String employee, String group) {
            employeeGroup.put(employee, group);
        }

        /**
         * Brute Force: Collect all ancestors for each employee, find deepest common one
         */
        public String getCommonGroupForEmployees(List<String> employees) {
            if (employees == null || employees.isEmpty()) {
                return null;
            }

            if (employees.size() == 1) {
                return employeeGroup.get(employees.get(0));
            }

            // Get ancestors for first employee (with depth tracking)
            Map<String, Integer> commonAncestors = getAncestorsWithDepth(employees.get(0));

            // Intersect with ancestors of remaining employees
            for (int i = 1; i < employees.size(); i++) {
                Map<String, Integer> currentAncestors = getAncestorsWithDepth(employees.get(i));
                commonAncestors.keySet().retainAll(currentAncestors.keySet());

                if (commonAncestors.isEmpty()) {
                    return null;
                }
            }

            // Find closest common ancestor (minimum depth = closest to employees)
            String closestGroup = null;
            int minDepth = Integer.MAX_VALUE;

            for (Map.Entry<String, Integer> entry : commonAncestors.entrySet()) {
                if (entry.getValue() < minDepth) {
                    minDepth = entry.getValue();
                    closestGroup = entry.getKey();
                }
            }

            return closestGroup;
        }

        private Map<String, Integer> getAncestorsWithDepth(String employee) {
            Map<String, Integer> ancestors = new LinkedHashMap<String, Integer>();
            String currentGroup = employeeGroup.get(employee);
            int depth = 0;

            while (currentGroup != null) {
                ancestors.put(currentGroup, depth);
                depth++;
                currentGroup = groupParent.get(currentGroup);
            }

            return ancestors;
        }
    }

    // ==================== PART B: DAG Structure (Shared Groups/Employees) ====================
    /**
     * Extended hierarchy where:
     * - A group can have MULTIPLE parents (DAG, not tree)
     * - An employee can belong to MULTIPLE groups
     *
     * ===================================================================================
     * EXAMPLE DAG STRUCTURE:
     * ===================================================================================
     *
     *                      ┌─────────────┐
     *                      │  Atlassian  │  (root)
     *                      └─────────────┘
     *                       /           \
     *                      /             \
     *            ┌─────────────┐    ┌─────────────┐
     *            │ Engineering │    │    Sales    │
     *            └─────────────┘    └─────────────┘
     *                 /    \            /
     *                /      \          /
     *    ┌──────────┐      ┌──────────┐
     *    │ Backend  │      │ Frontend │  ← Frontend has TWO parents!
     *    └──────────┘      └──────────┘
     *         |                 |
     *     [Alice]          [Bob, Charlie]
     *     [Charlie] ← Charlie is in BOTH Backend and Frontend!
     *
     * ===================================================================================
     * QUERY EXAMPLES:
     * ===================================================================================
     *
     * Query: getCommonGroup([Bob, Charlie])
     *
     *   Step 1: Find groups containing employees
     *           Bob → {Frontend}
     *           Charlie → {Frontend, Backend}
     *
     *   Step 2: Check if any group contains all employees
     *           Frontend: visitCount = 2 (Bob + Charlie) ✓
     *
     *   Answer: Frontend (both employees are directly in this group)
     *
     * ─────────────────────────────────────────────────────────────────────────────────
     *
     * Query: getCommonGroup([Alice, Bob])
     *
     *   Step 1: Find groups containing employees
     *           Alice → {Backend}
     *           Bob → {Frontend}
     *
     *   Step 2: Neither group contains both, so BFS upward:
     *
     *           Backend → Engineering (visitCount: 1)
     *           Frontend → Engineering (visitCount: 2) ✓
     *           Frontend → Sales (visitCount: 1)
     *
     *   Answer: Engineering (first group reached by both paths)
     *
     * ===================================================================================
     *
     * Approach (BFS from employees upward):
     * 1. Start BFS from all groups containing the employees
     * 2. Track visit count for each group
     * 3. First group visited by ALL employee paths = closest common group
     *
     * Time: O(E * G) where E = employees, G = total groups
     * Space: O(G) for visit tracking
     */
    static class PartB_DAGStructure {

        private Map<String, Set<String>> groupParents;     // group -> set of parent groups
        private Map<String, Set<String>> employeeGroups;   // employee -> set of groups
        private Set<String> rootGroups;

        public PartB_DAGStructure() {
            this.groupParents = new HashMap<String, Set<String>>();
            this.employeeGroups = new HashMap<String, Set<String>>();
            this.rootGroups = new HashSet<String>();
        }

        public void addGroup(String group, String parent) {
            if (!groupParents.containsKey(group)) {
                groupParents.put(group, new HashSet<String>());
            }

            if (parent != null) {
                groupParents.get(group).add(parent);
                if (!groupParents.containsKey(parent)) {
                    groupParents.put(parent, new HashSet<String>());
                }
            } else {
                rootGroups.add(group);
            }
        }

        public void addEmployeeToGroup(String employee, String group) {
            if (!employeeGroups.containsKey(employee)) {
                employeeGroups.put(employee, new HashSet<String>());
            }
            employeeGroups.get(employee).add(group);
        }

        /**
         * BFS approach: Find first group reachable from ALL employees
         */
        public String getCommonGroupForEmployees(List<String> employees) {
            if (employees == null || employees.isEmpty()) {
                return null;
            }

            int employeeCount = employees.size();

            // Track how many employee paths have visited each group
            Map<String, Integer> visitCount = new HashMap<String, Integer>();

            // BFS queue
            Queue<String> queue = new LinkedList<String>();
            Set<String> inQueue = new HashSet<String>();

            // Initialize: add all groups containing the employees
            for (String employee : employees) {
                Set<String> groups = employeeGroups.get(employee);
                if (groups == null) {
                    return null;
                }

                for (String group : groups) {
                    int count = visitCount.containsKey(group) ? visitCount.get(group) + 1 : 1;
                    visitCount.put(group, count);

                    if (count == employeeCount) {
                        return group;
                    }

                    if (!inQueue.contains(group)) {
                        queue.offer(group);
                        inQueue.add(group);
                    }
                }
            }

            // BFS upward through parents
            while (!queue.isEmpty()) {
                String current = queue.poll();
                int currentVisits = visitCount.containsKey(current) ? visitCount.get(current) : 0;

                Set<String> parents = groupParents.get(current);
                if (parents == null) {
                    continue;
                }

                for (String parent : parents) {
                    int oldCount = visitCount.containsKey(parent) ? visitCount.get(parent) : 0;
                    int newCount = oldCount + currentVisits;
                    visitCount.put(parent, newCount);

                    if (newCount >= employeeCount) {
                        return parent;
                    }

                    if (!inQueue.contains(parent)) {
                        queue.offer(parent);
                        inQueue.add(parent);
                    }
                }
            }

            return null;
        }
    }

    // ==================== PART C: Concurrent Read/Write Handling ====================
    /**
     * Thread-safe version using ReadWriteLock:
     * - Multiple threads can READ simultaneously
     * - WRITE operations get exclusive access
     * - Reads always see the latest state
     *
     * Methods:
     * - getCommonGroupForEmployees() - READ operation
     * - addGroup(), removeGroup(), addEmployee(), removeEmployee() - WRITE operations
     */
    static class PartC_ConcurrentStructure {

        private Map<String, Set<String>> groupParents;
        private Map<String, Set<String>> groupChildren;
        private Map<String, Set<String>> employeeGroups;
        private ReadWriteLock lock;

        public PartC_ConcurrentStructure() {
            this.groupParents = new HashMap<String, Set<String>>();
            this.groupChildren = new HashMap<String, Set<String>>();
            this.employeeGroups = new HashMap<String, Set<String>>();
            this.lock = new ReentrantReadWriteLock();
        }

        // ===== WRITE OPERATIONS (Exclusive Lock) =====

        public void addGroup(String group, String parent) {
            lock.writeLock().lock();
            try {
                if (!groupParents.containsKey(group)) {
                    groupParents.put(group, new HashSet<String>());
                }
                if (parent != null) {
                    groupParents.get(group).add(parent);
                    if (!groupChildren.containsKey(parent)) {
                        groupChildren.put(parent, new HashSet<String>());
                    }
                    groupChildren.get(parent).add(group);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void removeGroup(String group) {
            lock.writeLock().lock();
            try {
                // Remove from parents' children list
                Set<String> parents = groupParents.remove(group);
                if (parents != null) {
                    for (String parent : parents) {
                        Set<String> children = groupChildren.get(parent);
                        if (children != null) {
                            children.remove(group);
                        }
                    }
                }

                // Remove from children's parent list
                Set<String> children = groupChildren.remove(group);
                if (children != null) {
                    for (String child : children) {
                        Set<String> childParents = groupParents.get(child);
                        if (childParents != null) {
                            childParents.remove(group);
                        }
                    }
                }

                // Remove employees from this group
                for (Set<String> groups : employeeGroups.values()) {
                    groups.remove(group);
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void addEmployee(String employee, String group) {
            lock.writeLock().lock();
            try {
                if (!employeeGroups.containsKey(employee)) {
                    employeeGroups.put(employee, new HashSet<String>());
                }
                employeeGroups.get(employee).add(group);
            } finally {
                lock.writeLock().unlock();
            }
        }

        public void removeEmployee(String employee, String group) {
            lock.writeLock().lock();
            try {
                Set<String> groups = employeeGroups.get(employee);
                if (groups != null) {
                    groups.remove(group);
                    if (groups.isEmpty()) {
                        employeeGroups.remove(employee);
                    }
                }
            } finally {
                lock.writeLock().unlock();
            }
        }

        // ===== READ OPERATION (Shared Lock) =====

        public String getCommonGroupForEmployees(List<String> employees) {
            lock.readLock().lock();
            try {
                if (employees == null || employees.isEmpty()) {
                    return null;
                }

                int employeeCount = employees.size();
                Map<String, Integer> visitCount = new HashMap<String, Integer>();
                Queue<String> queue = new LinkedList<String>();
                Set<String> inQueue = new HashSet<String>();

                // Initialize with employee groups
                for (String employee : employees) {
                    Set<String> groups = employeeGroups.get(employee);
                    if (groups == null || groups.isEmpty()) {
                        return null;
                    }

                    for (String group : groups) {
                        int count = visitCount.containsKey(group) ? visitCount.get(group) + 1 : 1;
                        visitCount.put(group, count);

                        if (count == employeeCount) {
                            return group;
                        }
                        if (!inQueue.contains(group)) {
                            queue.offer(group);
                            inQueue.add(group);
                        }
                    }
                }

                // BFS upward
                while (!queue.isEmpty()) {
                    String current = queue.poll();
                    int currentVisits = visitCount.containsKey(current) ? visitCount.get(current) : 0;

                    Set<String> parents = groupParents.get(current);
                    if (parents == null) {
                        continue;
                    }

                    for (String parent : parents) {
                        int oldCount = visitCount.containsKey(parent) ? visitCount.get(parent) : 0;
                        int newCount = oldCount + currentVisits;
                        visitCount.put(parent, newCount);

                        if (newCount >= employeeCount) {
                            return parent;
                        }
                        if (!inQueue.contains(parent)) {
                            queue.offer(parent);
                            inQueue.add(parent);
                        }
                    }
                }

                return null;
            } finally {
                lock.readLock().unlock();
            }
        }
    }

    // ==================== PART D: Single Level Groups (No Subgroups) ====================
    /**
     * Simplified structure:
     * - Only ONE level of groups (no hierarchy)
     * - Each group directly contains employees
     * - "Closest common group" = any group containing ALL target employees
     *
     * Approach:
     * 1. Find all groups for first employee
     * 2. Intersect with groups of remaining employees
     * 3. Return any group from intersection (or null if none)
     *
     * Time: O(E * G) where E = employees, G = groups per employee
     * Space: O(G)
     */
    static class PartD_SingleLevelGroups {

        private Map<String, Set<String>> employeeGroups;  // employee -> groups
        private Map<String, Set<String>> groupEmployees;  // group -> employees

        public PartD_SingleLevelGroups() {
            this.employeeGroups = new HashMap<String, Set<String>>();
            this.groupEmployees = new HashMap<String, Set<String>>();
        }

        public void addGroup(String group) {
            if (!groupEmployees.containsKey(group)) {
                groupEmployees.put(group, new HashSet<String>());
            }
        }

        public void addEmployeeToGroup(String employee, String group) {
            if (!employeeGroups.containsKey(employee)) {
                employeeGroups.put(employee, new HashSet<String>());
            }
            employeeGroups.get(employee).add(group);

            if (!groupEmployees.containsKey(group)) {
                groupEmployees.put(group, new HashSet<String>());
            }
            groupEmployees.get(group).add(employee);
        }

        /**
         * Find any group that contains ALL target employees
         */
        public String getCommonGroupForEmployees(List<String> employees) {
            if (employees == null || employees.isEmpty()) {
                return null;
            }

            // Start with groups of first employee
            Set<String> firstEmpGroups = employeeGroups.get(employees.get(0));
            if (firstEmpGroups == null) {
                return null;
            }

            Set<String> commonGroups = new HashSet<String>(firstEmpGroups);

            // Intersect with groups of other employees
            for (int i = 1; i < employees.size(); i++) {
                Set<String> groups = employeeGroups.get(employees.get(i));
                if (groups == null) {
                    return null;
                }
                commonGroups.retainAll(groups);

                if (commonGroups.isEmpty()) {
                    return null;
                }
            }

            // Return any common group
            return commonGroups.iterator().next();
        }

        /**
         * Alternative: Return ALL common groups
         */
        public Set<String> getAllCommonGroups(List<String> employees) {
            if (employees == null || employees.isEmpty()) {
                return new HashSet<String>();
            }

            Set<String> firstEmpGroups = employeeGroups.get(employees.get(0));
            if (firstEmpGroups == null) {
                return new HashSet<String>();
            }

            Set<String> commonGroups = new HashSet<String>(firstEmpGroups);

            for (int i = 1; i < employees.size(); i++) {
                Set<String> groups = employeeGroups.get(employees.get(i));
                if (groups == null) {
                    return new HashSet<String>();
                }
                commonGroups.retainAll(groups);
            }

            return commonGroups;
        }
    }

    // ==================== UNIT TESTS ====================

    public static void main(String[] args) {
        System.out.println("=== Testing Closest Common Group Solutions ===\n");

        testPartA();
        testPartB();
        testPartC();
        testPartD();

        System.out.println("=== All Tests Completed ===");
    }

    private static void testPartA() {
        System.out.println("--- Part A: Tree Structure ---");

        /**
         * Hierarchy:
         *           Atlassian (root)
         *           /        \
         *     Engineering    Sales
         *       /    \          \
         *   Backend  Frontend   APAC
         *   [Alice]  [Bob]      [Charlie]
         */
        PartA_TreeStructure tree = new PartA_TreeStructure("Atlassian");
        tree.addGroup("Engineering", "Atlassian");
        tree.addGroup("Sales", "Atlassian");
        tree.addGroup("Backend", "Engineering");
        tree.addGroup("Frontend", "Engineering");
        tree.addGroup("APAC", "Sales");

        tree.addEmployee("Alice", "Backend");
        tree.addEmployee("Bob", "Frontend");
        tree.addEmployee("Charlie", "APAC");

        // Test 1: Alice & Bob -> Engineering
        String result1 = tree.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        System.out.println("Alice & Bob common group: " + result1);
        assertResult("Engineering", result1);

        // Test 2: Alice & Charlie -> Atlassian
        String result2 = tree.getCommonGroupForEmployees(Arrays.asList("Alice", "Charlie"));
        System.out.println("Alice & Charlie common group: " + result2);
        assertResult("Atlassian", result2);

        // Test 3: All three -> Atlassian
        String result3 = tree.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob", "Charlie"));
        System.out.println("All three common group: " + result3);
        assertResult("Atlassian", result3);

        System.out.println("Part A: PASSED\n");
    }

    private static void testPartB() {
        System.out.println("--- Part B: DAG Structure (Shared Groups) ---");

        /**
         * Hierarchy (DAG):
         *              Atlassian
         *             /         \
         *       Engineering    Sales
         *          /    \      /
         *     Backend  Frontend
         *     [Alice]  [Bob, Charlie]
         *
         * Note: Frontend reports to BOTH Engineering and Sales
         */
        PartB_DAGStructure dag = new PartB_DAGStructure();
        dag.addGroup("Atlassian", null);
        dag.addGroup("Engineering", "Atlassian");
        dag.addGroup("Sales", "Atlassian");
        dag.addGroup("Backend", "Engineering");
        dag.addGroup("Frontend", "Engineering");
        dag.addGroup("Frontend", "Sales");  // Frontend also under Sales

        dag.addEmployeeToGroup("Alice", "Backend");
        dag.addEmployeeToGroup("Bob", "Frontend");
        dag.addEmployeeToGroup("Charlie", "Frontend");
        dag.addEmployeeToGroup("Charlie", "Backend");  // Charlie in both groups

        // Test 1: Bob & Charlie -> Frontend (both are in Frontend)
        String result1 = dag.getCommonGroupForEmployees(Arrays.asList("Bob", "Charlie"));
        System.out.println("Bob & Charlie common group: " + result1);
        assertOneOf(result1, "Frontend", "Backend");

        // Test 2: Alice & Bob -> Engineering
        String result2 = dag.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        System.out.println("Alice & Bob common group: " + result2);
        assertResult("Engineering", result2);

        System.out.println("Part B: PASSED\n");
    }

    private static void testPartC() {
        System.out.println("--- Part C: Concurrent Structure ---");

        PartC_ConcurrentStructure concurrent = new PartC_ConcurrentStructure();
        concurrent.addGroup("Atlassian", null);
        concurrent.addGroup("Engineering", "Atlassian");
        concurrent.addGroup("Backend", "Engineering");
        concurrent.addGroup("Frontend", "Engineering");

        concurrent.addEmployee("Alice", "Backend");
        concurrent.addEmployee("Bob", "Frontend");

        // Test read operation
        String result1 = concurrent.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        System.out.println("Alice & Bob common group: " + result1);
        assertResult("Engineering", result1);

        // Test write then read
        concurrent.addEmployee("Charlie", "Backend");
        String result2 = concurrent.getCommonGroupForEmployees(Arrays.asList("Alice", "Charlie"));
        System.out.println("Alice & Charlie common group: " + result2);
        assertResult("Backend", result2);

        // Test remove then read
        concurrent.removeEmployee("Charlie", "Backend");
        concurrent.addEmployee("Charlie", "Frontend");
        String result3 = concurrent.getCommonGroupForEmployees(Arrays.asList("Bob", "Charlie"));
        System.out.println("Bob & Charlie common group: " + result3);
        assertResult("Frontend", result3);

        System.out.println("Part C: PASSED\n");
    }

    private static void testPartD() {
        System.out.println("--- Part D: Single Level Groups ---");

        /**
         * Flat structure:
         * - Group "TeamA" has [Alice, Bob]
         * - Group "TeamB" has [Bob, Charlie]
         * - Group "TeamC" has [Alice, Bob, Charlie]
         */
        PartD_SingleLevelGroups flat = new PartD_SingleLevelGroups();
        flat.addGroup("TeamA");
        flat.addGroup("TeamB");
        flat.addGroup("TeamC");

        flat.addEmployeeToGroup("Alice", "TeamA");
        flat.addEmployeeToGroup("Alice", "TeamC");
        flat.addEmployeeToGroup("Bob", "TeamA");
        flat.addEmployeeToGroup("Bob", "TeamB");
        flat.addEmployeeToGroup("Bob", "TeamC");
        flat.addEmployeeToGroup("Charlie", "TeamB");
        flat.addEmployeeToGroup("Charlie", "TeamC");

        // Test 1: Alice & Bob -> TeamA or TeamC
        String result1 = flat.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob"));
        System.out.println("Alice & Bob common group: " + result1);
        assertOneOf(result1, "TeamA", "TeamC");

        // Test 2: Bob & Charlie -> TeamB or TeamC
        String result2 = flat.getCommonGroupForEmployees(Arrays.asList("Bob", "Charlie"));
        System.out.println("Bob & Charlie common group: " + result2);
        assertOneOf(result2, "TeamB", "TeamC");

        // Test 3: All three -> TeamC (only group with all)
        String result3 = flat.getCommonGroupForEmployees(Arrays.asList("Alice", "Bob", "Charlie"));
        System.out.println("All three common group: " + result3);
        assertResult("TeamC", result3);

        // Test 4: Get all common groups
        Set<String> allCommon = flat.getAllCommonGroups(Arrays.asList("Alice", "Bob"));
        System.out.println("Alice & Bob ALL common groups: " + allCommon);
        assertContains(allCommon, "TeamA");
        assertContains(allCommon, "TeamC");

        System.out.println("Part D: PASSED\n");
    }

    // Simple assertion helpers
    private static void assertResult(String expected, String actual) {
        if (!expected.equals(actual)) {
            throw new RuntimeException("Expected " + expected + " but got " + actual);
        }
    }

    private static void assertOneOf(String actual, String... options) {
        for (String option : options) {
            if (option.equals(actual)) {
                return;
            }
        }
        throw new RuntimeException("Expected one of " + Arrays.toString(options) + " but got " + actual);
    }

    private static void assertContains(Set<String> set, String value) {
        if (!set.contains(value)) {
            throw new RuntimeException("Expected set to contain " + value);
        }
    }
}