package practice.atlassian;

import java.util.*;
import java.util.concurrent.locks.*;
import java.util.concurrent.ConcurrentHashMap;

public class AtlassianOrgDirectory {

    // Group representation
    public static class Group {
        final String groupId;
        Set<Group> subGroups = new HashSet<>();    // For subgroups in hierarchy
        Group parent;                              // parent group
        Set<String> employees = new HashSet<>(); // employee ids

        public Group(String id) {
            this.groupId = id;
        }
    }

    // Maps for groups and employee-to-groups mapping for quick lookup
    private final ConcurrentHashMap<String, Group> groups = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Set<Group>> employeeGroups = new ConcurrentHashMap<>();

    // ReadWriteLock for concurrent reads/writes
    private final ReadWriteLock rwLock = new ReentrantReadWriteLock();

    // Adds or updates a group with parent and employees
    public void updateGroup(String groupId, String parentGroupId, Set<String> employees) {
        rwLock.writeLock().lock();
        try {
            Group group = groups.computeIfAbsent(groupId, Group::new);

            // Update parent link
            if (parentGroupId != null) {
                Group parent = groups.computeIfAbsent(parentGroupId, Group::new);
                if (group.parent != null) {
                    group.parent.subGroups.remove(group);
                }
                group.parent = parent;
                parent.subGroups.add(group);
            }

            // Remove old employee-group references
            for (String emp : group.employees) {
                Set<Group> empGroups = employeeGroups.get(emp);
                if (empGroups != null) {
                    empGroups.remove(group);
                    if (empGroups.isEmpty()) {
                        employeeGroups.remove(emp);
                    }
                }
            }

            // Update employees
            group.employees = new HashSet<>(employees);
            for (String emp : employees) {
                employeeGroups.computeIfAbsent(emp, k -> ConcurrentHashMap.newKeySet()).add(group);
            }
        } finally {
            rwLock.writeLock().unlock();
        }
    }

    // Method to find closest common parent group for a set of employees
    public Optional<String> getCommonGroupForEmployees(Set<String> employeeIds) {
        rwLock.readLock().lock();
        try {
            if (employeeIds.isEmpty()) return Optional.empty();

            // For each employee get groups they belong to
            List<Set<Group>> groupsPerEmployee = new ArrayList<>();
            for (String emp : employeeIds) {
                Set<Group> gs = employeeGroups.get(emp);
                if (gs == null) return Optional.empty(); // Employee not found in any group
                groupsPerEmployee.add(gs);
            }

            // Find intersection of groups across all employees as candidate groups
            Set<Group> commonGroups = new HashSet<>(groupsPerEmployee.get(0));
            for (int i = 1; i < groupsPerEmployee.size(); i++) {
                commonGroups.retainAll(groupsPerEmployee.get(i));
                if (commonGroups.isEmpty()) {
                    // No common group directly, find common ancestor groups in hierarchy
                    return findLowestCommonAncestorOfEmployees(employeeIds);
                }
            }

            // If there are common groups, find the "closest" one (lowest in hierarchy)
            return commonGroups.stream()
                    .min(Comparator.comparingInt(this::depth))
                    .map(g -> g.groupId);

        } finally {
            rwLock.readLock().unlock();
        }
    }

    // Helper: Find depth of group in tree
    private int depth(Group g) {
        int depth = 0;
        while (g.parent != null) {
            depth++;
            g = g.parent;
        }
        return depth;
    }

    // Find lowest common ancestor group for employees when no direct common group exists
    private Optional<String> findLowestCommonAncestorOfEmployees(Set<String> employeeIds) {
        // For each employee, find all ancestor groups of all groups employee belongs to
        List<Set<Group>> ancestriesPerEmployee = new ArrayList<>();
        for (String emp : employeeIds) {
            Set<Group> empGroups = employeeGroups.get(emp);
            if (empGroups == null) return Optional.empty();
            Set<Group> ancestorGroups = new HashSet<>();
            for (Group g : empGroups) {
                collectAncestors(g, ancestorGroups);
            }
            ancestriesPerEmployee.add(ancestorGroups);
        }

        // Find intersection of all ancestor groups
        Set<Group> commonAncestors = new HashSet<>(ancestriesPerEmployee.get(0));
        for (int i = 1; i < ancestriesPerEmployee.size(); i++) {
            commonAncestors.retainAll(ancestriesPerEmployee.get(i));
            if (commonAncestors.isEmpty()) return Optional.empty();
        }

        // Return the closest (lowest depth) among common ancestors
        return commonAncestors.stream()
                .min(Comparator.comparingInt(this::depth))
                .map(g -> g.groupId);
    }

    // Recursively collect all ancestors of a group including itself
    private void collectAncestors(Group g, Set<Group> ancestors) {
        ancestors.add(g);
        if (g.parent != null) collectAncestors(g.parent, ancestors);
    }

    // For question d) - single level groups, no hierarchy
    // Optimized method assuming no subgroups
    public Optional<String> getCommonGroupSingleLevel(Set<String> employeeIds) {
        rwLock.readLock().lock();
        try {
            if (employeeIds.isEmpty()) return Optional.empty();

            Set<String> candidateGroups = null;
            for (String emp : employeeIds) {
                Set<Group> empGs = employeeGroups.get(emp);
                if (empGs == null || empGs.isEmpty()) return Optional.empty();

                Set<String> empGroupIds = new HashSet<>();
                for (Group g : empGs) {
                    empGroupIds.add(g.groupId);
                }

                if (candidateGroups == null) {
                    candidateGroups = empGroupIds;
                } else {
                    candidateGroups.retainAll(empGroupIds);
                    if (candidateGroups.isEmpty()) return Optional.empty();
                }
            }

            if (candidateGroups == null || candidateGroups.isEmpty()) return Optional.empty();

            // If multiple, arbitrarily return one closest common - here just return any
            return candidateGroups.stream().findFirst();

        } finally {
            rwLock.readLock().unlock();
        }
    }

}
