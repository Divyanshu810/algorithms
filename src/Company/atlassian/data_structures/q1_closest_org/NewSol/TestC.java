package Company.atlassian.data_structures.q1_closest_org.NewSol;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;

public class TestC {

    @Test
    public void testingBaseCase() {
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
        assertEquals("Engineering", result1);

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
}
