package practice.atlassian.code_design;

import java.util.Arrays;

/**
 * Demo class showing the usage of the OrganizationHierarchy class
 * with the enhanced OOP concepts and design patterns.
 */
public class OrganizationHierarchyDemo {
    
    public static void main(String[] args) {
        // Create a new organization hierarchy
        OrganizationHierarchy org = new OrganizationHierarchy();
        
        // Add an observer to track hierarchy changes
        org.registerObserver(new OrganizationHierarchy.HierarchyChangeObserver() {
            @Override
            public void onGroupAdded(String groupId, String parentId) {
                System.out.println("Group added: " + groupId + 
                                  (parentId != null ? " under " + parentId : " as root"));
            }
            
            @Override
            public void onEmployeeAdded(String employeeId, String groupId) {
                System.out.println("Employee added: " + employeeId + " to group " + groupId);
            }
        });
        
        System.out.println("\n--- Building Organization Structure ---");
        
        // Create organization structure
        org.addGroup("Company", null);                // Root
        org.addGroup("Engineering", "Company");       // Engineering under Company
        org.addGroup("Sales", "Company");             // Sales under Company
        org.addGroup("Backend", "Engineering");       // Backend under Engineering
        org.addGroup("Frontend", "Engineering");      // Frontend under Engineering
        org.addGroup("APAC", "Sales");                // APAC under Sales
        org.addGroup("EMEA", "Sales");                // EMEA under Sales
        
        System.out.println("\n--- Adding Employees ---");
        
        // Add employees
        org.addEmployee("emp1", "Backend");
        org.addEmployee("emp2", "Backend");
        org.addEmployee("emp3", "Frontend");
        org.addEmployee("emp4", "APAC");
        org.addEmployee("emp5", "EMEA");
        
        System.out.println("\n--- Finding Common Parent Groups ---");
        
        // Find closest common parent for employees from the same group
        System.out.println("Closest common parent for emp1 and emp2: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp2")));
        
        // Find closest common parent for employees from different groups but same department
        System.out.println("Closest common parent for emp1 and emp3: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp3")));
        
        // Find closest common parent for employees from completely different departments
        System.out.println("Closest common parent for emp1, emp3, and emp4: " + 
                          org.findClosestCommonParent(Arrays.asList("emp1", "emp3", "emp4")));
        
        System.out.println("\n--- Employees By Group ---");
        
        // Get all employees in the Backend group
        System.out.println("Employees in Backend group: " + 
                          org.getEmployeesInGroup("Backend"));
        
        // Get all employees in the Engineering group
        try {
            System.out.println("Employees in Engineering group: " + 
                              org.getEmployeesInGroup("Engineering"));
        } catch (Exception e) {
            // This should not throw an exception
            System.err.println("Error: " + e.getMessage());
        }
        
        System.out.println("\n--- Error Handling Examples ---");
        
        // Try to add a group with a non-existent parent (should throw exception)
        try {
            org.addGroup("TestGroup", "NonExistentParent");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        
        // Try to add an employee to a non-existent group (should throw exception)
        try {
            org.addEmployee("newEmp", "NonExistentGroup");
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
        
        // Try to find common parent with a non-existent employee (should throw exception)
        try {
            org.findClosestCommonParent(Arrays.asList("emp1", "nonExistentEmployee"));
        } catch (IllegalArgumentException e) {
            System.out.println("Expected error: " + e.getMessage());
        }
    }
}