package Company.atlassian.data_structures.q1_closest_org.OldSol;

import java.util.*;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class Group {
    private String groupId;
    private String name;
    private Set<String> parentGroups;
    private Set<String> childGroups;
    private Set<String> employees;
    
    public Group(String groupId, String name) {
        this.groupId = groupId;
        this.name = name;
        this.parentGroups = new HashSet<>();
        this.childGroups = new HashSet<>();
        this.employees = new HashSet<>();
    }
    
    public String getGroupId() { return groupId; }
    public String getName() { return name; }
    public Set<String> getParentGroups() { return parentGroups; }
    public Set<String> getChildGroups() { return childGroups; }
    public Set<String> getEmployees() { return employees; }
    
    @Override
    public String toString() {
        return "Group(" + groupId + ", " + name + ")";
    }
}

class Employee {
    private String employeeId;
    private String name;
    private Set<String> groups;
    
    public Employee(String employeeId, String name) {
        this.employeeId = employeeId;
        this.name = name;
        this.groups = new HashSet<>();
    }
    
    public String getEmployeeId() { return employeeId; }
    public String getName() { return name; }
    public Set<String> getGroups() { return groups; }
    
    @Override
    public String toString() {
        return "Employee(" + employeeId + ", " + name + ")";
    }
}

public class Solution {
    private Map<String, Group> groups;
    private Map<String, Employee> employees;
    private Set<String> rootGroups;
    private ReadWriteLock readWriteLock;
    
    public Solution() {
        this.groups = new HashMap<>();
        this.employees = new HashMap<>();
        this.rootGroups = new HashSet<>();
        this.readWriteLock = new ReentrantReadWriteLock();
    }
    
    public void addGroup(String groupId, String name, String parentGroupId) {
        readWriteLock.writeLock().lock();
        try {
            if (!groups.containsKey(groupId)) {
                groups.put(groupId, new Group(groupId, name));
            }
            
            if (parentGroupId != null) {
                if (!groups.containsKey(parentGroupId)) {
                    groups.put(parentGroupId, new Group(parentGroupId, "Group_" + parentGroupId));
                }
                
                groups.get(groupId).getParentGroups().add(parentGroupId);
                groups.get(parentGroupId).getChildGroups().add(groupId);
                
                rootGroups.remove(groupId);
            } else {
                rootGroups.add(groupId);
            }
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    
    public void addEmployee(String employeeId, String name, String groupId) {
        readWriteLock.writeLock().lock();
        try {
            if (!employees.containsKey(employeeId)) {
                employees.put(employeeId, new Employee(employeeId, name));
            }
            
            if (!groups.containsKey(groupId)) {
                groups.put(groupId, new Group(groupId, "Group_" + groupId));
                rootGroups.add(groupId);
            }
            
            employees.get(employeeId).getGroups().add(groupId);
            groups.get(groupId).getEmployees().add(employeeId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    
    public void removeGroup(String groupId) {
        readWriteLock.writeLock().lock();
        try {
            if (!groups.containsKey(groupId)) {
                return;
            }
            
            Group group = groups.get(groupId);
            
            // Reassign children to parents
            for (String childId : group.getChildGroups()) {
                Group childGroup = groups.get(childId);
                childGroup.getParentGroups().remove(groupId);
                for (String parentId : group.getParentGroups()) {
                    childGroup.getParentGroups().add(parentId);
                    groups.get(parentId).getChildGroups().add(childId);
                }
            }
            
            // Remove from parents
            for (String parentId : group.getParentGroups()) {
                groups.get(parentId).getChildGroups().remove(groupId);
            }
            
            // Move employees to parent groups
            for (String employeeId : group.getEmployees()) {
                Employee employee = employees.get(employeeId);
                employee.getGroups().remove(groupId);
                for (String parentId : group.getParentGroups()) {
                    employee.getGroups().add(parentId);
                    groups.get(parentId).getEmployees().add(employeeId);
                }
            }
            
            groups.remove(groupId);
            rootGroups.remove(groupId);
        } finally {
            readWriteLock.writeLock().unlock();
        }
    }
    
    private List<List<String>> findEmployeePaths(String employeeId) {
        if (!employees.containsKey(employeeId)) {
            return new ArrayList<>();
        }
        
        Employee employee = employees.get(employeeId);
        List<List<String>> allPaths = new ArrayList<>();
        
        for (String groupId : employee.getGroups()) {
            List<List<String>> paths = findGroupPaths(groupId);
            allPaths.addAll(paths);
        }
        
        return allPaths;
    }
    
    private List<List<String>> findGroupPaths(String groupId) {
        if (!groups.containsKey(groupId)) {
            return new ArrayList<>();
        }
        
        Group group = groups.get(groupId);
        
        if (group.getParentGroups().isEmpty()) {
            List<String> path = new ArrayList<>();
            path.add(groupId);
            List<List<String>> result = new ArrayList<>();
            result.add(path);
            return result;
        }
        
        List<List<String>> allPaths = new ArrayList<>();
//        String parentId=group.getParent();
        for (String parentId : group.getParentGroups()) {
            List<List<String>> parentPaths = findGroupPaths(parentId);
//            System.out.println(parentPaths);
            for (List<String> path : parentPaths) {
                List<String> newPath = new ArrayList<>(path);
                newPath.add(groupId);
                allPaths.add(newPath);
            }
        }
        
        return allPaths;
    }
    
    public String findClosestCommonGroup(List<String> employeeIds) {
        readWriteLock.readLock().lock();
        try {
            if (employeeIds == null || employeeIds.isEmpty()) {
                return null;
            }
            
            if (employeeIds.size() == 1) {
                Employee employee = employees.get(employeeIds.get(0));
                if (employee == null || employee.getGroups().isEmpty()) {
                    return null;
                }
                return employee.getGroups().iterator().next();
            }
            
            // Get all paths for all employees
            List<List<List<String>>> allEmployeePaths = new ArrayList<>();
            for (String employeeId : employeeIds) {
                List<List<String>> paths = findEmployeePaths(employeeId);
                System.out.println(paths);
                if (paths.isEmpty()) {
                    return null;
                }
                allEmployeePaths.add(paths);
            }
            
            // Find common ancestors across all path combinations
            Set<String> commonGroups = new HashSet<>();
            
            // Generate all combinations of paths (one from each employee)
            generateAndProcessPathCombinations(allEmployeePaths, 0, new ArrayList<>(), commonGroups);
            
            if (commonGroups.isEmpty()) {
                return null;
            }
            
            return findDeepestGroup(commonGroups);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }
    
    private void generateAndProcessPathCombinations(List<List<List<String>>> allPaths, int index, 
                                                  List<List<String>> currentCombination, Set<String> commonGroups) {
        if (index == allPaths.size()) {
            String commonAncestor = findCommonAncestorFromPaths(currentCombination);
            if (commonAncestor != null) {
                commonGroups.add(commonAncestor);
            }
            return;
        }
        
        for (List<String> path : allPaths.get(index)) {
            currentCombination.add(path);
            generateAndProcessPathCombinations(allPaths, index + 1, currentCombination, commonGroups);
            currentCombination.remove(currentCombination.size() - 1);
        }
    }
    
    private String findCommonAncestorFromPaths(List<List<String>> paths) {
        if (paths.isEmpty()) {
            return null;
        }
        
        // Find the shortest path length
        int minLength = paths.stream().mapToInt(List::size).min().orElse(0);
        
        // Compare level by level from root
        for (int i = 0; i < minLength; i++) {
            Set<String> currentGroups = new HashSet<>();
            for (List<String> path : paths) {
                currentGroups.add(path.get(i));
            }
            
            if (currentGroups.size() > 1) {
                // Divergence found, return previous level
                return i > 0 ? paths.get(0).get(i - 1) : null;
            }
        }
        
        // All paths are identical up to minLength
        return paths.get(0).get(minLength - 1);
    }
    
    private String findDeepestGroup(Set<String> groupIds) {
        String deepestGroup = null;
        int maxDepth = -1;
        
        for (String groupId : groupIds) {
            List<List<String>> paths = findGroupPaths(groupId);
            if (!paths.isEmpty()) {
                int depth = paths.stream().mapToInt(List::size).max().orElse(0);
                if (depth > maxDepth) {
                    maxDepth = depth;
                    deepestGroup = groupId;
                }
            }
        }
        
        return deepestGroup != null ? deepestGroup : groupIds.iterator().next();
    }
    
    // Flat hierarchy for scale-down
    public static class FlatOrganizationHierarchy {
        private Map<String, Set<String>> groups;
        private Map<String, Set<String>> employeeGroups;
        
        public FlatOrganizationHierarchy() {
            this.groups = new HashMap<>();
            this.employeeGroups = new HashMap<>();
        }
        
        public void addEmployeeToGroup(String employeeId, String groupId) {
            groups.computeIfAbsent(groupId, k -> new HashSet<>()).add(employeeId);
            employeeGroups.computeIfAbsent(employeeId, k -> new HashSet<>()).add(groupId);
        }
        
        public Set<String> findCommonGroups(List<String> employeeIds) {
            if (employeeIds == null || employeeIds.isEmpty()) {
                return new HashSet<>();
            }
            
            Set<String> commonGroups = new HashSet<>(employeeGroups.getOrDefault(employeeIds.get(0), new HashSet<>()));
            
            for (int i = 1; i < employeeIds.size(); i++) {
                Set<String> employeeGroupSet = employeeGroups.getOrDefault(employeeIds.get(i), new HashSet<>());
                commonGroups.retainAll(employeeGroupSet);
            }
            
            return commonGroups;
        }
    }
    
    public static void main(String[] args) {
        Solution org = new Solution();
        
        // Add groups (Company -> Engineering -> Backend/Frontend)
//        org.addGroup("company", "Atlassian", null);
        org.addGroup("engineering", "Engineering", "company");
        org.addGroup("backend", "Backend Team", "engineering");
        org.addGroup("frontend", "Frontend Team", "engineering");
        org.addGroup("mobile", "Mobile Team", "engineering");
        
        // Add employees
        org.addEmployee("emp1", "Alice", "backend");
        org.addEmployee("emp2", "Bob", "backend");
        org.addEmployee("emp3", "Charlie", "frontend");
        org.addEmployee("emp4", "David", "mobile");
        
        // Find common groups
        System.out.println("Common group for Alice and Bob: " + 
            org.findClosestCommonGroup(Arrays.asList("emp1", "emp2")));
        System.out.println("Common group for Alice and Charlie: " + 
            org.findClosestCommonGroup(Arrays.asList("emp1", "emp3")));
        System.out.println("Common group for all engineers: " + 
            org.findClosestCommonGroup(Arrays.asList("emp1", "emp2", "emp3", "emp4")));
    }
}