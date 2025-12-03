package Company.atlassian.data_structures.q1_closest_org.NewSol;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PartA_TreeStructure {

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