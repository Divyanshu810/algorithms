package atlassian;

import java.util.*;

public class q1_closest_org {
    /**
     * Organization Hierarchy class that maintains groups and employees
     * and provides functionality to find the closest common parent group.
     */
    public static class OrganizationHierarchy {
        // Maps group ID to its parent group ID
        private Map<String, String> groupToParent;
        // Maps employee ID to their group ID
        private Map<String, String> employeeToGroup;
        // Cache for storing paths to avoid recalculating
        private Map<String, List<String>> pathCache;

        public OrganizationHierarchy() {
            this.groupToParent = new HashMap<>();
            this.employeeToGroup = new HashMap<>();
            this.pathCache = new HashMap<>();
        }

        /**
         * Add a group to the organization with an optional parent group.
         * 
         * @param groupId The ID of the group to add
         * @param parentId The ID of the parent group (can be null for root groups)
         * @throws IllegalArgumentException if the parent group doesn't exist
         */
        public void addGroup(String groupId, String parentId) {
            if (parentId != null && !groupToParent.containsKey(parentId)) {
                throw new IllegalArgumentException("Parent group " + parentId + " does not exist");
            }
            
            groupToParent.put(groupId, parentId);
        }

        /**
         * Add an employee to a specific group.
         * 
         * @param employeeId The ID of the employee
         * @param groupId The ID of the group
         * @throws IllegalArgumentException if the group doesn't exist
         */
        public void addEmployee(String employeeId, String groupId) {
            if (!groupToParent.containsKey(groupId)) {
                throw new IllegalArgumentException("Group " + groupId + " does not exist");
            }
            
            employeeToGroup.put(employeeId, groupId);
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
                return pathCache.get(groupId);
            }
            
            List<String> path = new ArrayList<>();
            String current = groupId;
            
            while (current != null) {
                path.add(current);
                current = groupToParent.get(current);
            }
            
            // Cache the result for future use
            pathCache.put(groupId, path);
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
                if (!employeeToGroup.containsKey(empId)) {
                    throw new IllegalArgumentException("Employee " + empId + " does not exist");
                }
                return employeeToGroup.get(empId);
            }
            
            // Convert employee IDs to groups
            List<String> groups = new ArrayList<>();
            for (String empId : employeeIds) {
                if (!employeeToGroup.containsKey(empId)) {
                    throw new IllegalArgumentException("Employee " + empId + " does not exist");
                }
                groups.add(employeeToGroup.get(empId));
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
    }

    /**
     * Example usage of the OrganizationHierarchy class.
     */
    public static void main(String[] args) {
        // Create organization hierarchy
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        // Add groups
        org.addGroup("Company", null);            // Root
        org.addGroup("Engineering", "Company");   // Engineering under Company
        org.addGroup("Sales", "Company");         // Sales under Company
        org.addGroup("Backend", "Engineering");   // Backend under Engineering
        org.addGroup("Frontend", "Engineering");  // Frontend under Engineering
        org.addGroup("APAC", "Sales");            // APAC under Sales
        org.addGroup("EMEA", "Sales");            // EMEA under Sales
        
        // Add employees
        org.addEmployee("emp1", "Backend");
        org.addEmployee("emp2", "Backend");
        org.addEmployee("emp3", "Frontend");
        org.addEmployee("emp4", "APAC");
        org.addEmployee("emp5", "EMEA");
        
        // Find closest common parent for employees from the same group
        System.out.println("Closest common parent for emp1 and emp2: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp2")));
        
        // Find closest common parent for employees from different groups but same department
        System.out.println("Closest common parent for emp1 and emp3: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp3")));
        
        // Find closest common parent for employees from completely different departments
        System.out.println("Closest common parent for emp1, emp3, and emp4: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp3", "emp4")));
    }
}
// Time Complexity: O(N) in the worst case for finding paths, but optimized with caching
// Space Complexity: O(N) for storing the hierarchy and paths