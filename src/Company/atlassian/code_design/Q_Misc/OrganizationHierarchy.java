package Company.atlassian.code_design.Q_Misc;

import java.util.*;

/**
 * Organization Hierarchy system that finds the closest common parent group
 * for a set of employees in an organization.
 * 
 * This implementation uses:
 * 1. Encapsulation: Private fields with public methods
 * 2. Immutability: Entity objects are immutable
 * 3. Design Patterns:
 *    - Builder pattern for Group creation
 *    - Observer pattern for tracking hierarchy changes
 *    - Caching strategy for performance optimization
 */
public class OrganizationHierarchy {
    // Observer interface for hierarchy changes
    public interface HierarchyChangeObserver {
        void onGroupAdded(String groupId, String parentId);
        void onEmployeeAdded(String employeeId, String groupId);
    }
    
    // Immutable Group entity
    public static final class Group {
        private final String id;
        private final String parentId;
        
        private Group(Builder builder) {
            this.id = builder.id;
            this.parentId = builder.parentId;
        }
        
        public String getId() {
            return id;
        }
        
        public String getParentId() {
            return parentId;
        }
        
        // Builder pattern for Group creation
        public static class Builder {
            private String id;
            private String parentId;
            
            public Builder(String id) {
                this.id = id;
            }
            
            public Builder withParent(String parentId) {
                this.parentId = parentId;
                return this;
            }
            
            public Group build() {
                return new Group(this);
            }
        }
    }
    
    // Immutable Employee entity
    public static final class Employee {
        private final String id;
        private final String groupId;
        
        public Employee(String id, String groupId) {
            this.id = id;
            this.groupId = groupId;
        }
        
        public String getId() {
            return id;
        }
        
        public String getGroupId() {
            return groupId;
        }
    }
    
    // Data stores
    private final Map<String, Group> groups;
    private final Map<String, Employee> employees;
    
    // Cache for optimizing path calculations
    private final Map<String, List<String>> pathCache;
    
    // Observers for hierarchy changes
    private final List<HierarchyChangeObserver> observers;

    /**
     * Constructor for OrganizationHierarchy
     */
    public OrganizationHierarchy() {
        this.groups = new HashMap<>();
        this.employees = new HashMap<>();
        this.pathCache = new HashMap<>();
        this.observers = new ArrayList<>();
    }
    
    /**
     * Register an observer to be notified of hierarchy changes
     * 
     * @param observer The observer to register
     */
    public void registerObserver(HierarchyChangeObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Add a group to the organization with an optional parent group.
     * 
     * @param groupId The ID of the group to add
     * @param parentId The ID of the parent group (can be null for root groups)
     * @throws IllegalArgumentException if the parent group doesn't exist
     */
    public void addGroup(String groupId, String parentId) {
        if (parentId != null && !groups.containsKey(parentId)) {
            throw new IllegalArgumentException("Parent group " + parentId + " does not exist");
        }
        
        Group group = new Group.Builder(groupId)
                            .withParent(parentId)
                            .build();
        
        groups.put(groupId, group);
        
        // Clear path cache when hierarchy changes
        pathCache.clear();
        
        // Notify observers
        for (HierarchyChangeObserver observer : observers) {
            observer.onGroupAdded(groupId, parentId);
        }
    }
    
    /**
     * Add an employee to a specific group.
     * 
     * @param employeeId The ID of the employee
     * @param groupId The ID of the group
     * @throws IllegalArgumentException if the group doesn't exist
     */
    public void addEmployee(String employeeId, String groupId) {
        if (!groups.containsKey(groupId)) {
            throw new IllegalArgumentException("Group " + groupId + " does not exist");
        }
        
        Employee employee = new Employee(employeeId, groupId);
        employees.put(employeeId, employee);
        
        // Notify observers
        for (HierarchyChangeObserver observer : observers) {
            observer.onEmployeeAdded(employeeId, groupId);
        }
    }
    
    /**
     * Get the path from a group to the root of the organization.
     * 
     * @param groupId The ID of the group
     * @return List of group IDs from the given group to the root
     */
    private List<String> getPathToRoot(String groupId) {
        // Return cached path if available
        if (pathCache.containsKey(groupId)) {
            return new ArrayList<>(pathCache.get(groupId)); // Return a copy to prevent modification
        }
        
        List<String> path = new ArrayList<>();
        String current = groupId;
        
        while (current != null) {
            path.add(current);
            Group group = groups.get(current);
            current = group != null ? group.getParentId() : null;
        }
        
        // Cache the result for future use
        pathCache.put(groupId, new ArrayList<>(path)); // Store a copy
        return path;
    }
    
    /**
     * Find the closest common parent group for a set of employees.
     * Optimized version using set operations for better performance.
     * 
     * @param employeeIds List of employee IDs
     * @return The ID of the closest common parent group, or null if no common parent exists
     * @throws IllegalArgumentException if any employee doesn't exist
     */
    public String findClosestCommonParent(List<String> employeeIds) {
        if (employeeIds == null || employeeIds.isEmpty()) {
            return null;
        }
        
        // Handle single employee case
        if (employeeIds.size() == 1) {
            String empId = employeeIds.get(0);
            Employee employee = employees.get(empId);
            if (employee == null) {
                throw new IllegalArgumentException("Employee " + empId + " does not exist");
            }
            return employee.getGroupId();
        }
        
        // Convert employee IDs to groups
        List<String> groups = new ArrayList<>();
        for (String empId : employeeIds) {
            Employee employee = employees.get(empId);
            if (employee == null) {
                throw new IllegalArgumentException("Employee " + empId + " does not exist");
            }
            groups.add(employee.getGroupId());
        }
        
        // If all employees are in the same group, return that group
        boolean allSameGroup = true;
        String firstGroup = groups.get(0);
        for (int i = 1; i < groups.size(); i++) {
            if (!groups.get(i).equals(firstGroup)) {
                allSameGroup = false;
                break;
            }
        }
        
        if (allSameGroup) {
            return firstGroup;
        }
        
        // Use a set to track common ancestors
        Set<String> commonAncestors = new HashSet<>();
        
        // Get path for the first group
        List<String> firstPath = getPathToRoot(groups.get(0));
        // Add all groups in the first path to the set
        commonAncestors.addAll(firstPath);
        
        // Find common ancestors by intersecting the paths
        for (int i = 1; i < groups.size(); i++) {
            Set<String> currentAncestors = new HashSet<>();
            List<String> currentPath = getPathToRoot(groups.get(i));
            
            // Add all groups in the current path to a temporary set
            currentAncestors.addAll(currentPath);
            
            // Retain only common ancestors
            commonAncestors.retainAll(currentAncestors);
            
            // If no common ancestors, return null early
            if (commonAncestors.isEmpty()) {
                return null;
            }
        }
        
        // Find the closest common ancestor (the one with the longest path to root)
        String closestCommonAncestor = null;
        int maxDistance = -1;
        
        for (String group : commonAncestors) {
            List<String> path = getPathToRoot(group);
            if (path.size() > maxDistance) {
                maxDistance = path.size();
                closestCommonAncestor = group;
            }
        }
        
        return closestCommonAncestor;
    }
    
    /**
     * Clear all caches to free memory or force recalculation
     */
    public void clearCaches() {
        pathCache.clear();
    }
    
    /**
     * Get all employees in a specific group
     * 
     * @param groupId The ID of the group
     * @return List of employee IDs in the group
     */
    public List<String> getEmployeesInGroup(String groupId) {
        if (!groups.containsKey(groupId)) {
            throw new IllegalArgumentException("Group " + groupId + " does not exist");
        }
        
        List<String> result = new ArrayList<>();
        for (Employee employee : employees.values()) {
            if (employee.getGroupId().equals(groupId)) {
                result.add(employee.getId());
            }
        }
        return result;
    }
}