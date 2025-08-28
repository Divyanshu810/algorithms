package practice.atlassian.code_design;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the OrganizationHierarchy class.
 */
public class OrganizationHierarchyTest {
    
    private OrganizationHierarchy organizationHierarchy;

    @BeforeEach
    public void setUp() {
        organizationHierarchy = new OrganizationHierarchy();
        
        // Create a test hierarchy
        //             Company
        //            /       \
        //    Engineering     Sales
        //      /    \         /  \
        //  Backend  Frontend APAC EMEA
        
        organizationHierarchy.addGroup("Company", null);
        organizationHierarchy.addGroup("Engineering", "Company");
        organizationHierarchy.addGroup("Sales", "Company");
        organizationHierarchy.addGroup("Backend", "Engineering");
        organizationHierarchy.addGroup("Frontend", "Engineering");
        organizationHierarchy.addGroup("APAC", "Sales");
        organizationHierarchy.addGroup("EMEA", "Sales");
        
        // Add test employees
        organizationHierarchy.addEmployee("emp1", "Backend");
        organizationHierarchy.addEmployee("emp2", "Backend");
        organizationHierarchy.addEmployee("emp3", "Frontend");
        organizationHierarchy.addEmployee("emp4", "APAC");
        organizationHierarchy.addEmployee("emp5", "EMEA");
    }

    @Test
    public void testFindClosestCommonParentForEmptyList() {
        assertNull(organizationHierarchy.findClosestCommonParent(Collections.emptyList()));
    }

    @Test
    public void testFindClosestCommonParentForSingleEmployee() {
        assertEquals("Backend", organizationHierarchy.findClosestCommonParent(Collections.singletonList("emp1")));
    }

    @Test
    public void testFindClosestCommonParentForSameGroup() {
        // Two employees from the same group
        assertEquals("Backend", organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp2")));
    }

    @Test
    public void testFindClosestCommonParentForSiblingGroups() {
        // Two employees from sibling groups (different groups, same department)
        assertEquals("Engineering", organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp3")));
    }

    @Test
    public void testFindClosestCommonParentForDistantGroups() {
        // Three employees from different departments
        assertEquals("Company", organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp3", "emp4")));
    }

    @Test
    public void testFindClosestCommonParentWithAllEmployees() {
        // All employees from different groups
        assertEquals("Company", organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp2", "emp3", "emp4", "emp5")));
    }

    @Test
    public void testFindClosestCommonParentForNonExistentEmployee() {
        // Test with a non-existent employee
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "nonExistentEmployee"));
        });
        
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    public void testAddGroupWithNonExistentParent() {
        // Test adding a group with a non-existent parent
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationHierarchy.addGroup("TestGroup", "NonExistentParent");
        });
        
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    public void testAddEmployeeToNonExistentGroup() {
        // Test adding an employee to a non-existent group
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            organizationHierarchy.addEmployee("newEmp", "NonExistentGroup");
        });
        
        assertTrue(exception.getMessage().contains("does not exist"));
    }

    @Test
    public void testFindClosestCommonParentCacheEfficiency() {
        // Call the method multiple times to test caching
        String result1 = organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp3"));
        String result2 = organizationHierarchy.findClosestCommonParent(Arrays.asList("emp1", "emp3"));
        
        assertEquals(result1, result2);
        assertEquals("Engineering", result1);
    }
}