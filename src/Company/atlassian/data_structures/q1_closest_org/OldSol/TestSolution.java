package Company.atlassian.data_structures.q1_closest_org.OldSol;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.*;
import java.util.concurrent.*;

public class TestSolution {

    private Solution org;

    @BeforeEach
    void setUp() {
        org = new Solution();

        // Create a test hierarchy:
        // company
        // ├── engineering
        // │   ├── backend
        // │   ├── frontend
        // │   └── mobile
        // └── sales
        //     ├── enterprise
        //     └── smb

        org.addGroup("company", "Atlassian", null);
        org.addGroup("engineering", "Engineering", "company");
        org.addGroup("sales", "Sales", "company");
        org.addGroup("backend", "Backend Team", "engineering");
        org.addGroup("frontend", "Frontend Team", "engineering");
        org.addGroup("mobile", "Mobile Team", "engineering");
        org.addGroup("enterprise", "Enterprise Sales", "sales");
        org.addGroup("smb", "SMB Sales", "sales");

        // Add employees
        org.addEmployee("alice", "Alice", "backend");
        org.addEmployee("bob", "Bob", "backend");
        org.addEmployee("charlie", "Charlie", "frontend");
        org.addEmployee("david", "David", "mobile");
        org.addEmployee("eve", "Eve", "enterprise");
        org.addEmployee("frank", "Frank", "smb");
    }

    @Test
    @DisplayName("Test employees in the same immediate group")
    void testSameTeamEmployees() {
        String result = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assertEquals("backend", result);
    }

    @Test
    @DisplayName("Test employees in same department but different teams")
    void testSameDepartmentDifferentTeams() {
        String result1 = org.findClosestCommonGroup(Arrays.asList("alice", "charlie"));
        assertEquals("engineering", result1);

        String result2 = org.findClosestCommonGroup(Arrays.asList("alice", "david"));
        assertEquals("engineering", result2);

        String result3 = org.findClosestCommonGroup(Arrays.asList("charlie", "david"));
        assertEquals("engineering", result3);
    }

    @Test
    @DisplayName("Test employees from completely different departments")
    void testDifferentDepartments() {
        String result1 = org.findClosestCommonGroup(Arrays.asList("alice", "eve"));
        assertEquals("company", result1);

        String result2 = org.findClosestCommonGroup(Arrays.asList("charlie", "frank"));
        assertEquals("company", result2);
    }

    @Test
    @DisplayName("Test multiple employees from the same team")
    void testMultipleEmployeesSameTeam() {
        // Add more backend employees
        org.addEmployee("george", "George", "backend");
        String result = org.findClosestCommonGroup(Arrays.asList("alice", "bob", "george"));
        assertEquals("backend", result);
    }

    @Test
    @DisplayName("Test multiple employees from different departments")
    void testMultipleEmployeesMixedDepartments() {
        String result = org.findClosestCommonGroup(Arrays.asList("alice", "charlie", "eve"));
        assertEquals("company", result);
    }

    @Test
    @DisplayName("Test with single employee")
    void testSingleEmployee() {
        String result = org.findClosestCommonGroup(Arrays.asList("alice"));
        assertEquals("backend", result);
    }

    @Test
    @DisplayName("Test with empty employee list")
    void testEmptyList() {
        String result = org.findClosestCommonGroup(new ArrayList<>());
        assertNull(result);
    }

    @Test
    @DisplayName("Test with nonexistent employee")
    void testNonexistentEmployee() {
        String result = org.findClosestCommonGroup(Arrays.asList("nonexistent"));
        assertNull(result);
    }

    @Test
    @DisplayName("Test with mix of existing and nonexistent employees")
    void testMixedExistingNonexistent() {
        String result = org.findClosestCommonGroup(Arrays.asList("alice", "nonexistent"));
        assertNull(result);
    }

    @Test
    @DisplayName("Test dynamic group removal")
    void testGroupRemoval() {
        // Verify initial state
        String result1 = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assertEquals("backend", result1);

        // Remove backend group
        org.removeGroup("backend");

        // Employees should now be in engineering
        String result2 = org.findClosestCommonGroup(Arrays.asList("alice", "bob"));
        assertEquals("engineering", result2);
    }

    @Test
    @DisplayName("Test employee belonging to multiple groups (shared hierarchy)")
    void testSharedEmployeeMultipleGroups() {
        // Add Alice to frontend as well
        org.addEmployee("alice", "Alice", "frontend");

        // Alice and Charlie should have frontend as common group
        String result = org.findClosestCommonGroup(Arrays.asList("alice", "charlie"));
        assertTrue(result.equals("frontend") || result.equals("engineering"));
    }
}

class TestFlatOrganizationHierarchy {

    private Solution.FlatOrganizationHierarchy flatOrg;

    @BeforeEach
    void setUp() {
        flatOrg = new Solution.FlatOrganizationHierarchy();

        // Add employees to groups
        flatOrg.addEmployeeToGroup("alice", "backend");
        flatOrg.addEmployeeToGroup("alice", "engineering");
        flatOrg.addEmployeeToGroup("bob", "backend");
        flatOrg.addEmployeeToGroup("bob", "engineering");
        flatOrg.addEmployeeToGroup("charlie", "frontend");
        flatOrg.addEmployeeToGroup("charlie", "engineering");
    }

    @Test
    @DisplayName("Test finding common groups for employees in same team")
    void testCommonGroupsSameTeam() {
        Set<String> result = flatOrg.findCommonGroups(Arrays.asList("alice", "bob"));
        Set<String> expected = new HashSet<>(Arrays.asList("backend", "engineering"));
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test finding common groups for employees in different teams")
    void testCommonGroupsDifferentTeams() {
        Set<String> result = flatOrg.findCommonGroups(Arrays.asList("alice", "charlie"));
        Set<String> expected = new HashSet<>(Arrays.asList("engineering"));
        assertEquals(expected, result);
    }

    @Test
    @DisplayName("Test when employees have no common groups")
    void testNoCommonGroups() {
        flatOrg.addEmployeeToGroup("david", "sales");
        Set<String> result = flatOrg.findCommonGroups(Arrays.asList("alice", "david"));
        assertEquals(new HashSet<>(), result);
    }

    @Test
    @DisplayName("Test with empty employee list")
    void testEmptyEmployeeList() {
        Set<String> result = flatOrg.findCommonGroups(new ArrayList<>());
        assertEquals(new HashSet<>(), result);
    }
}

class TestConcurrentOperations {

    private Solution org;

    @BeforeEach
    void setUp() {
        org = new Solution();
        setupHierarchy();
    }

    void setupHierarchy() {
        org.addGroup("company", "Company", null);
        org.addGroup("engineering", "Engineering", "company");
        org.addGroup("backend", "Backend", "engineering");

        for (int i = 0; i < 10; i++) {
            org.addEmployee("emp" + i, "Employee" + i, "backend");
        }
    }

    @Test
    @DisplayName("Test multiple concurrent read operations")
    void testConcurrentReads() throws InterruptedException {
        List<String> results = Collections.synchronizedList(new ArrayList<>());
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            Thread thread = new Thread(() -> {
                for (int j = 0; j < 50; j++) {
                    String result = org.findClosestCommonGroup(Arrays.asList("emp0", "emp1"));
                    if (result != null) {
                        results.add(result);
                    }
                    try {
                        Thread.sleep(1);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
            });
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // All reads should return the same result
        String expectedResult = "backend";
        assertTrue(results.stream().allMatch(result -> result.equals(expectedResult)));
        assertTrue(results.size() > 0);
    }

    @Test
    @DisplayName("Test concurrent read and write operations")
    void testConcurrentReadWrite() throws InterruptedException {
        List<String> readResults = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch writeCompleted = new CountDownLatch(1);

        Thread readThread = new Thread(() -> {
            while (writeCompleted.getCount() > 0) {
                String result = org.findClosestCommonGroup(Arrays.asList("emp0", "emp1"));
                if (result != null) {
                    readResults.add(result);
                }
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        Thread writeThread = new Thread(() -> {
            try {
                Thread.sleep(10);
                org.addGroup("backend_team_a", "Backend Team A", "backend");
                org.addEmployee("emp0", "Employee0", "backend_team_a");
                org.addEmployee("emp1", "Employee1", "backend_team_a");
                writeCompleted.countDown();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        readThread.start();
        writeThread.start();

        writeThread.join();
        readThread.join();

        // Verify that reads always returned valid results
        Set<String> validResults = new HashSet<>(Arrays.asList("backend", "backend_team_a"));
        assertTrue(readResults.stream().allMatch(validResults::contains));
        assertTrue(readResults.size() > 0);
    }
}

class TestEdgeCases {

    @Test
    @DisplayName("Test with a very deep hierarchy")
    void testDeepHierarchy() {
        Solution org = new Solution();

        // Create a 10-level deep hierarchy
        String currentParent = null;
        for (int i = 0; i < 10; i++) {
            String groupId = "level_" + i;
            org.addGroup(groupId, "Level " + i, currentParent);
            currentParent = groupId;
        }

        // Add employees at the deepest level
        org.addEmployee("emp1", "Employee 1", "level_9");
        org.addEmployee("emp2", "Employee 2", "level_9");

        String result = org.findClosestCommonGroup(Arrays.asList("emp1", "emp2"));
        assertEquals("level_9", result);
    }

    @Test
    @DisplayName("Test performance with many employees")
    void testLargeNumberOfEmployees() {
        Solution org = new Solution();

        org.addGroup("company", "Company", null);
        org.addGroup("large_team", "Large Team", "company");

        // Add 1000 employees
        List<String> employeeIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String empId = "emp_" + i;
            org.addEmployee(empId, "Employee " + i, "large_team");
            employeeIds.add(empId);
        }

        // Test with subset of employees
        long startTime = System.currentTimeMillis();
        String result = org.findClosestCommonGroup(employeeIds.subList(0, 100));
        long endTime = System.currentTimeMillis();

        assertEquals("large_team", result);
        assertTrue(endTime - startTime < 1000); // Should complete within 1 second
    }
}