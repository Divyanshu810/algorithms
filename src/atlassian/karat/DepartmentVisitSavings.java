package practice.atlassian.karat;

import java.util.*;

public class DepartmentVisitSavings {
    // Map item to department
    public static Map<String, String> buildItemToDepartment(Map<String, List<String>> departments) {
        Map<String, String> itemToDept = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : departments.entrySet()) {
            for (String item : entry.getValue()) {
                itemToDept.put(item, entry.getKey());
            }
        }
        return itemToDept;
    }

    // Visits with order
    public static int visitsWithOrder(List<String> shoppingList, Map<String, String> itemToDept) {
        if (shoppingList.isEmpty()) return 0;
        int visits = 1;
        String prevDept = itemToDept.get(shoppingList.get(0));
        for (int i = 1; i < shoppingList.size(); i++) {
            String currDept = itemToDept.get(shoppingList.get(i));
            if (!currDept.equals(prevDept)) {
                visits++;
                prevDept = currDept;
            }
        }
        return visits;
    }

    // Visits without order
    public static int visitsWithoutOrder(List<String> shoppingList, Map<String, String> itemToDept) {
        Set<String> depts = new HashSet<>();
        for (String item : shoppingList) {
            depts.add(itemToDept.get(item));
        }
        return depts.size();
    }

    // Visits saved
    public static int visitsSaved(List<String> shoppingList, Map<String, List<String>> departments) {
        Map<String, String> itemToDept = buildItemToDepartment(departments);
        int withOrder = visitsWithOrder(shoppingList, itemToDept);
        int withoutOrder = visitsWithoutOrder(shoppingList, itemToDept);
        return withOrder - withoutOrder;
    }

    public static void main(String[] args) {
        Map<String, List<String>> departments = new HashMap<>();
        departments.put("Electronics", Arrays.asList("TV", "Laptop", "Phone"));
        departments.put("Groceries", Arrays.asList("Milk", "Bread", "Eggs"));
        departments.put("Clothing", Arrays.asList("Shirt", "Pants"));

        List<String> shoppingList = Arrays.asList("TV", "Milk", "Laptop", "Bread", "Phone");

        int savings = visitsSaved(shoppingList, departments);
        System.out.println("Visits saved: " + savings); // Output: Visits saved: 1
    }
}