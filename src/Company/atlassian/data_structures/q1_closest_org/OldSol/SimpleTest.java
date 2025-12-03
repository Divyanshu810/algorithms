package Company.atlassian.data_structures.q1_closest_org.OldSol;
import java.util.*;

public class SimpleTest {
    
    public static void main(String[] args) {
        System.out.println("=== Running Simple Tests for Solution ===");
        
        Solution org = new Solution();
        
        // Create a test hierarchy
        org.addGroup("company", "Atlassian", null);
        org.addGroup("engineering", "Engineering", "company");
        org.addGroup("sales", "Sales", "company");
        org.addGroup("backend", "Backend Team", "engineering");
        org.addGroup("frontend", "Frontend Team", "engineering");
        
        // Add employees
        org.addEmployee("alice", "Alice", "backend");
        org.addEmployee("bob", "Bob", "backend");
        org.addEmployee("charlie", "Charlie", "frontend");
        
        // Test cases
        testBasicFunctionality(org);
        testEdgeCases(org);
        testFlatHierarchy();
        
        System.out.println("All tests completed successfully!");
    }
    
    private static void testBasicFunctionality(Solution org) {
        System.out.println("--- Testing Basic Functionality ---");
        
        // Test employees in same team
        String result1 = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assert "backend".equals(result1) : "Expected 'backend', got: " + result1;
        System.out.println("✓ Same team test passed");
        
        // Test employees in different teams but same department
        String result2 = org.findClosestCommonGroup(Arrays.asList("alice", "charlie"));
        assert "engineering".equals(result2) : "Expected 'engineering', got: " + result2;
        System.out.println("✓ Different teams test passed");
        
        // Test single employee
        String result3 = org.findClosestCommonGroup(Arrays.asList("alice"));
        assert "backend".equals(result3) : "Expected 'backend', got: " + result3;
        System.out.println("✓ Single employee test passed");
    }
    
    private static void testEdgeCases(Solution org) {
        System.out.println("--- Testing Edge Cases ---");
        
        // Test empty list
        String result1 = org.findClosestCommonGroup(new ArrayList<>());
        assert result1 == null : "Expected null, got: " + result1;
        System.out.println("✓ Empty list test passed");
        
        // Test nonexistent employee
        String result2 = org.findClosestCommonGroup(Arrays.asList("nonexistent"));
        assert result2 == null : "Expected null, got: " + result2;
        System.out.println("✓ Nonexistent employee test passed");
        
        // Test group removal
        String beforeRemoval = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assert "backend".equals(beforeRemoval) : "Expected 'backend' before removal";
        
        org.removeGroup("backend");
        String afterRemoval = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assert "engineering".equals(afterRemoval) : "Expected 'engineering' after removal, got: " + afterRemoval;
        System.out.println("✓ Group removal test passed");
    }
    
    private static void testFlatHierarchy() {
        System.out.println("--- Testing Flat Hierarchy ---");
        
        Solution.FlatOrganizationHierarchy flatOrg = new Solution.FlatOrganizationHierarchy();
        
        flatOrg.addEmployeeToGroup("alice", "backend");
        flatOrg.addEmployeeToGroup("alice", "engineering");
        flatOrg.addEmployeeToGroup("bob", "backend");
        flatOrg.addEmployeeToGroup("bob", "engineering");
        
        Set<String> result = flatOrg.findCommonGroups(Arrays.asList("alice", "bob"));
        Set<String> expected = new HashSet<>(Arrays.asList("backend", "engineering"));
        
        assert result.equals(expected) : "Expected " + expected + ", got: " + result;
        System.out.println("✓ Flat hierarchy test passed");
    }
}