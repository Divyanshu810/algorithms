package practice.airbnb;

import java.util.*;

/**
 * given a list of menu items, how would you pick the most cost optimal option - [ 8.0 : "pizza",
 * 9.0: "pasta",
 * 10.0: "pizza, pasta",
 * 11.0:"burger",
 * 12.0:"burger, pizza, pasta"
 * ]
 * order is [burger, pizza] and the answer should be 12.0 ["burger", "pizza", "pasta"]
 */
public class MenuSubOptimalChoice {
    private static double minPrice = Double.MAX_VALUE;
    private static List<Integer> bestCombo = new ArrayList<>();

    public static double findOptimalChoice(double[] prices, String[] items, String[] order) {
        Set<String> orderSet = new HashSet<>(Arrays.asList(order));
        Map<String, Double> memo = new HashMap<>();
        backtrack(prices, items, orderSet, 0, new TreeSet<>(), 0.0, new ArrayList<>(), memo);
        if (minPrice == Double.MAX_VALUE) {
            System.out.println("No combination found.");
            return -1;
        }
        System.out.print("Optimal choice: ");
        for (int idx : bestCombo) {
            System.out.print("[" + items[idx] + "] ");
        }
        System.out.println("with price: " + minPrice);
        return minPrice;
    }

    private static void backtrack(double[] prices, String[] items, Set<String> orderSet, int idx,
                                  Set<String> covered, double total, List<Integer> combo, Map<String, Double> memo) {
        if (covered.containsAll(orderSet)) {
            if (total < minPrice) {
                minPrice = total;
                bestCombo = new ArrayList<>(combo);
            }
            return;
        }
        if (idx == prices.length || total >= minPrice) return;
        List<String> coveredList = new ArrayList<>(covered);
        Collections.sort(coveredList);
        String key = coveredList + "|" + idx;
        if (memo.containsKey(key) && memo.get(key) <= total) return;
        memo.put(key, total);
        // Include current item
        Set<String> newCovered = new HashSet<>(covered);
        for (String item : items[idx].split(",")) {
            newCovered.add(item.trim());
        }
        combo.add(idx);
        backtrack(prices, items, orderSet, idx + 1, newCovered, total + prices[idx], combo, memo);
        combo.remove(combo.size() - 1);

        // Exclude current item
        backtrack(prices, items, orderSet, idx + 1, covered, total, combo, memo);
    }


//    public static double findOptimalChoice(double[] prices, String[] items, String[] order) {
//        double minPrice = Double.MAX_VALUE;
//        String optimalChoice = "";
//
//        for (int i = 0; i < prices.length; i++) {
//            boolean canOrder = true;
//            for (String item : order) {
//                if (!items[i].contains(item)) {
//                    canOrder = false;
//                    break;
//                }
//            }
//            if (canOrder && prices[i] < minPrice) {
//                minPrice = prices[i];
//                optimalChoice = items[i];
//            }
//        }
//
//        System.out.println("Optimal choice: " + optimalChoice + " with price: " + minPrice);
//        return minPrice;
//    }

    /**
     * OPTIMAL DYNAMIC PROGRAMMING APPROACH
     * Uses bitmask DP to represent covered items efficiently
     */
    public static class OptimalResult {
        double cost;
        List<Integer> indices;
        List<String> menuItems;
        
        public OptimalResult(double cost, List<Integer> indices, List<String> menuItems) {
            this.cost = cost;
            this.indices = new ArrayList<>(indices);
            this.menuItems = new ArrayList<>(menuItems);
        }
        
        @Override
        public String toString() {
            return String.format("Cost: %.1f, Items: %s, Indices: %s", cost, menuItems, indices);
        }
    }
    
    public static OptimalResult findOptimalChoiceDP(double[] prices, String[] items, String[] order) {
        // Create mapping of unique items to bit positions
        Set<String> allItems = new HashSet<>();
        for (String itemStr : items) {
            for (String item : itemStr.split(",")) {
                allItems.add(item.trim());
            }
        }
        
        List<String> itemList = new ArrayList<>(allItems);
        Map<String, Integer> itemToBit = new HashMap<>();
        for (int i = 0; i < itemList.size(); i++) {
            itemToBit.put(itemList.get(i), i);
        }
//        System.out.println(itemToBit);
        // Create target mask for required order
        int targetMask = 0;
        for (String item : order) {
            if (itemToBit.containsKey(item)) {
                targetMask |= (1 << itemToBit.get(item));
            }
        }
//        System.out.println("targetMask"+Integer.toBinaryString(targetMask));
        
        // Precompute masks for each menu item
        int[] menuMasks = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            int mask = 0;
            for (String item : items[i].split(",")) {
                String trimmed = item.trim();
                if (itemToBit.containsKey(trimmed)) {
                    mask |= (1 << itemToBit.get(trimmed));
                }
            }
            menuMasks[i] = mask;
        }
//        System.out.println("menu "+Arrays.toString(menuMasks));
        // DP: dp[mask] = minimum cost to achieve this mask
        int maxMask = 1 << itemList.size();
        double[] dp = new double[maxMask];
        int[] parentItem = new int[maxMask];  // Store which menu item was used
        Arrays.fill(dp, Double.MAX_VALUE);
        Arrays.fill(parentItem, -1);
        
        dp[0] = 0;
        
        // Fill DP table
        for (int i = 0; i < items.length; i++) {
            // Process in reverse order to avoid using updated values in same iteration
            for (int mask = maxMask - 1; mask >= 0; mask--) {
                if (dp[mask] == Double.MAX_VALUE) continue;
                
                int newMask = mask | menuMasks[i];
                double newCost = dp[mask] + prices[i];
                
                if (newCost < dp[newMask]) {
                    dp[newMask] = newCost;
                    parentItem[newMask] = i;
                }
            }
        }
        
        // Check if target is achievable
        if (dp[targetMask] == Double.MAX_VALUE) {
            return null; // No solution
        }
        
        // Reconstruct solution
        List<Integer> indices = new ArrayList<>();
        List<String> selectedItems = new ArrayList<>();
        int currentMask = targetMask;
        
        while (currentMask != 0 && parentItem[currentMask] != -1) {
            int menuIndex = parentItem[currentMask];
            indices.add(menuIndex);
            selectedItems.add(items[menuIndex]);
            currentMask = currentMask & ~menuMasks[menuIndex];
        }
        
        Collections.reverse(indices);
        Collections.reverse(selectedItems);
        
        return new OptimalResult(dp[targetMask], indices, selectedItems);
    }
    
    /**
     * Alternative DP approach using Set-based state representation
     * More intuitive but potentially less efficient for large item sets
     */
    public static OptimalResult findOptimalChoiceSetDP(double[] prices, String[] items, String[] order) {
        Set<String> required = new HashSet<>(Arrays.asList(order));
        
        // DP: state -> (min_cost, selected_indices)
        Map<Set<String>, Double> dp = new HashMap<>();
        Map<Set<String>, List<Integer>> dpIndices = new HashMap<>();
        
        dp.put(new HashSet<>(), 0.0);
        dpIndices.put(new HashSet<>(), new ArrayList<>());
        
        // Process each menu item
        for (int i = 0; i < items.length; i++) {
            Set<String> currentItems = new HashSet<>();
            for (String item : items[i].split(",")) {
                currentItems.add(item.trim());
            }
            
            Map<Set<String>, Double> newDp = new HashMap<>(dp);
            Map<Set<String>, List<Integer>> newDpIndices = new HashMap<>(dpIndices);
            
            for (Map.Entry<Set<String>, Double> entry : dp.entrySet()) {
                Set<String> currentState = entry.getKey();
                double currentCost = entry.getValue();
                
                Set<String> newState = new HashSet<>(currentState);
                newState.addAll(currentItems);
                
                double newCost = currentCost + prices[i];
                
                if (!newDp.containsKey(newState) || newCost < newDp.get(newState)) {
                    newDp.put(new HashSet<>(newState), newCost);
                    List<Integer> newIndices = new ArrayList<>(dpIndices.get(currentState));
                    newIndices.add(i);
                    newDpIndices.put(new HashSet<>(newState), newIndices);
                }
            }
            
            dp = newDp;
            dpIndices = newDpIndices;
        }
        
        // Find minimum cost solution that covers all required items
        double minCost = Double.MAX_VALUE;
        List<Integer> bestIndices = null;
        
        for (Map.Entry<Set<String>, Double> entry : dp.entrySet()) {
            if (entry.getKey().containsAll(required) && entry.getValue() < minCost) {
                minCost = entry.getValue();
                bestIndices = dpIndices.get(entry.getKey());
            }
        }
        
        if (bestIndices == null) {
            return null; // No solution
        }
        
        List<String> selectedItems = new ArrayList<>();
        for (int idx : bestIndices) {
            selectedItems.add(items[idx]);
        }
        
        return new OptimalResult(minCost, bestIndices, selectedItems);
    }

    /**
     * BFS + Priority Queue approach (Dijkstra-like algorithm)
     * Explores states in order of minimum cost, guaranteeing optimal solution
     */
    public static class SearchState {
        Set<String> coveredItems;
        double cost;
        List<Integer> menuIndices;
        
        public SearchState(Set<String> coveredItems, double cost, List<Integer> menuIndices) {
            this.coveredItems = new HashSet<>(coveredItems);
            this.cost = cost;
            this.menuIndices = new ArrayList<>(menuIndices);
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof SearchState)) return false;
            SearchState other = (SearchState) obj;
            return coveredItems.equals(other.coveredItems);
        }

        @Override
        public String toString(){
            return String.format("State{coveredItems=%s, cost=%.2f, menuIndices=%s}",
                                 coveredItems, cost, menuIndices);
        }
        
        @Override
        public int hashCode() {
            return coveredItems.hashCode();
        }
    }
    
    public static OptimalResult findOptimalChoiceBFSPQ(double[] prices, String[] items, String[] order) {
        Set<String> required = new HashSet<>(Arrays.asList(order));
        
        // Priority queue ordered by cost (min-heap)
        PriorityQueue<SearchState> pq = new PriorityQueue<>((a, b) -> Double.compare(a.cost, b.cost));
        Map<Set<String>, Double> visited = new HashMap<>(); // Track best cost to reach each state
        
        // Start with empty state
        SearchState initial = new SearchState(new HashSet<>(), 0.0, new ArrayList<>());
        pq.offer(initial);
        visited.put(new HashSet<>(), 0.0);
        
        while (!pq.isEmpty()) {
            SearchState current = pq.poll();
//            System.out.println(current);
            
            // Skip if we've found a better path to this state
            if (visited.containsKey(current.coveredItems) && 
                visited.get(current.coveredItems) < current.cost) {
                continue;
            }
            
            // Check if we've covered all required items
            if (current.coveredItems.containsAll(required)) {
                // Found optimal solution (due to priority queue ordering)
                List<String> selectedItems = new ArrayList<>();
                for (int idx : current.menuIndices) {
                    selectedItems.add(items[idx]);
                }
                return new OptimalResult(current.cost, current.menuIndices, selectedItems);
            }
            
            // Explore adding each menu item
            for (int i = 0; i < items.length; i++) {
                // Skip if already selected
                if (current.menuIndices.contains(i)) continue;
                
                // Parse items in this menu
                Set<String> menuItems = new HashSet<>();
                for (String item : items[i].split(",")) {
                    menuItems.add(item.trim());
                }
                
                // Create new state
                Set<String> newCovered = new HashSet<>(current.coveredItems);
                newCovered.addAll(menuItems);
                double newCost = current.cost + prices[i];
                
                // Skip if we've seen this state with better cost
                if (visited.containsKey(newCovered) && visited.get(newCovered) <= newCost) {
                    continue;
                }
                
                List<Integer> newIndices = new ArrayList<>(current.menuIndices);
                newIndices.add(i);
                
                SearchState newState = new SearchState(newCovered, newCost, newIndices);
                pq.offer(newState);
                visited.put(new HashSet<>(newCovered), newCost);
            }
        }
        
        return null; // No solution found
    }

    /**
     * Optimized BFS + PQ using bitmask for state representation
     * More efficient than set-based approach for small number of unique items
     */
    public static OptimalResult findOptimalChoiceBFSPQBitmask(double[] prices, String[] items, String[] order) {
        // Create mapping of unique items to bit positions
        Set<String> allItems = new HashSet<>();
        for (String itemStr : items) {
            for (String item : itemStr.split(",")) {
                allItems.add(item.trim());
            }
        }
        
        List<String> itemList = new ArrayList<>(allItems);
        Map<String, Integer> itemToBit = new HashMap<>();
        for (int i = 0; i < itemList.size(); i++) {
            itemToBit.put(itemList.get(i), i);
        }
        
        // Create target mask for required order
        int targetMask = 0;
        for (String item : order) {
            if (itemToBit.containsKey(item)) {
                targetMask |= (1 << itemToBit.get(item));
            }
        }
        
        // Precompute masks for each menu item
        int[] menuMasks = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            int mask = 0;
            for (String item : items[i].split(",")) {
                String trimmed = item.trim();
                if (itemToBit.containsKey(trimmed)) {
                    mask |= (1 << itemToBit.get(trimmed));
                }
            }
            menuMasks[i] = mask;
        }
        
        // Priority queue with bitmask states
        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Double.compare(a[1], b[1]));
        Map<Integer, Double> visited = new HashMap<>();
        Map<Integer, List<Integer>> stateToIndices = new HashMap<>();
        
        // State: [mask, cost_as_int, menu_index_list...]
        pq.offer(new int[]{0, 0}); // mask=0, cost=0
        visited.put(0, 0.0);
        stateToIndices.put(0, new ArrayList<>());
        
        while (!pq.isEmpty()) {
            int[] current = pq.poll();
            int currentMask = current[0];
            double currentCost = current[1] / 100.0; // Convert back from int
            
            // Skip if we've found better path to this state
            if (visited.containsKey(currentMask) && visited.get(currentMask) < currentCost) {
                continue;
            }
            
            // Check if target achieved
            if ((currentMask & targetMask) == targetMask) {
                List<Integer> indices = stateToIndices.get(currentMask);
                List<String> selectedItems = new ArrayList<>();
                for (int idx : indices) {
                    selectedItems.add(items[idx]);
                }
                return new OptimalResult(currentCost, indices, selectedItems);
            }
            
            // Try adding each menu item
            for (int i = 0; i < items.length; i++) {
                List<Integer> currentIndices = stateToIndices.get(currentMask);
                if (currentIndices.contains(i)) continue; // Already selected
                
                int newMask = currentMask | menuMasks[i];
                double newCost = currentCost + prices[i];
                
                if (visited.containsKey(newMask) && visited.get(newMask) <= newCost) {
                    continue;
                }
                
                List<Integer> newIndices = new ArrayList<>(currentIndices);
                newIndices.add(i);
                
                pq.offer(new int[]{newMask, (int)(newCost * 100)}); // Convert to int for array
                visited.put(newMask, newCost);
                stateToIndices.put(newMask, newIndices);
            }
        }
        
        return null; // No solution
    }
    
    /**
     * TOP-DOWN DP WITH BITMASKING APPROACH
     * Uses memoization with recursive calls
     */
    public static OptimalResult findOptimalChoiceTopDownDP(double[] prices, String[] items, String[] order) {
        // Create mapping of unique items to bit positions
        Set<String> allItems = new HashSet<>();
        for (String itemStr : items) {
            for (String item : itemStr.split(",")) {
                allItems.add(item.trim());
            }
        }
        
        List<String> itemList = new ArrayList<>(allItems);
        Map<String, Integer> itemToBit = new HashMap<>();
        for (int i = 0; i < itemList.size(); i++) {
            itemToBit.put(itemList.get(i), i);
        }
        
        // Create target mask for required order
        int targetMask = 0;
        for (String item : order) {
            if (itemToBit.containsKey(item)) {
                targetMask |= (1 << itemToBit.get(item));
            }
        }
        
        // Precompute masks for each menu item
        int[] menuMasks = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            int mask = 0;
            for (String item : items[i].split(",")) {
                String trimmed = item.trim();
                if (itemToBit.containsKey(trimmed)) {
                    mask |= (1 << itemToBit.get(trimmed));
                }
            }
            menuMasks[i] = mask;
        }
        
        // Memoization map: (mask,menuIndex) -> (cost, parent_item)
        Map<String, double[]> memo = new HashMap<>();
        
        // Recursive helper function
        double result = topDownHelper(0, 0, targetMask, menuMasks, prices, memo);
        
        if (result == Double.MAX_VALUE) {
            return null; // No solution
        }
        
        // Reconstruct solution
        List<Integer> indices = new ArrayList<>();
        List<String> selectedItems = new ArrayList<>();
        
        int currentMask = 0;
        int menuIndex = 0;
        
        while (currentMask != targetMask && menuIndex < items.length) {
            if (memo.containsKey(currentMask)) {
                int parentItem = (int) memo.get(currentMask)[1];
                if (parentItem != -1) {
                    indices.add(parentItem);
                    selectedItems.add(items[parentItem]);
                    currentMask |= menuMasks[parentItem];
                    menuIndex = parentItem + 1;
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        
        return new OptimalResult(result, indices, selectedItems);
    }
    
    /**
     * Recursive helper for top-down DP
     * @param menuIndex current menu item index
     * @param currentMask current coverage bitmask
     * @param targetMask target coverage bitmask
     * @param menuMasks precomputed masks for each menu item
     * @param prices menu item prices
     * @param memo memoization map
     * @return minimum cost to achieve target from current state
     */
    private static double topDownHelper(int menuIndex, int currentMask, int targetMask, 
                                       int[] menuMasks, double[] prices, Map<String, double[]> memo) {
        
        // Base case: target achieved
        if ((currentMask & targetMask) == targetMask) {
            return 0.0;
        }
        
        // Base case: no more menu items
        if (menuIndex >= menuMasks.length) {
            return Double.MAX_VALUE;
        }
        
        // Check memoization with correct key
        String memoKey = currentMask + "," + menuIndex;
        if (memo.containsKey(memoKey)) {
            return memo.get(memoKey)[0];
        }
        
        double minCost = Double.MAX_VALUE;
        int bestItem = -1;
        
        // Option 1: Take current menu item
        int newMask = currentMask | menuMasks[menuIndex];
        double takeResult = topDownHelper(menuIndex + 1, newMask, targetMask, menuMasks, prices, memo);
        
        if (takeResult != Double.MAX_VALUE) {
            double totalCost = prices[menuIndex] + takeResult;
            if (totalCost < minCost) {
                minCost = totalCost;
                bestItem = menuIndex;
            }
        }
        
        // Option 2: Skip current menu item
        double skipResult = topDownHelper(menuIndex + 1, currentMask, targetMask, menuMasks, prices, memo);
        if (skipResult < minCost) {
            minCost = skipResult;
            bestItem = -1;
        }
        
        // Memoize result with correct key: [cost, parent_item]
        memo.put(memoKey, new double[]{minCost, bestItem});
        return minCost;
    }
    
    /**
     * OPTIMIZED TOP-DOWN DP WITH BITMASKING
     * Better state representation and pruning
     */
    public static OptimalResult findOptimalChoiceTopDownDPOptimized(double[] prices, String[] items, String[] order) {
        // Setup bitmasks (same as above)
        Set<String> allItems = new HashSet<>();
        for (String itemStr : items) {
            for (String item : itemStr.split(",")) {
                allItems.add(item.trim());
            }
        }

        List<String> itemList = new ArrayList<>(allItems);
        Map<String, Integer> itemToBit = new HashMap<>();
        for (int i = 0; i < itemList.size(); i++) {
            itemToBit.put(itemList.get(i), i);
        }
        
        int targetMask = 0;
        for (String item : order) {
            if (itemToBit.containsKey(item)) {
                targetMask |= (1 << itemToBit.get(item));
            }
        }
        
        int[] menuMasks = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            int mask = 0;
            for (String item : items[i].split(",")) {
                String trimmed = item.trim();
                if (itemToBit.containsKey(trimmed)) {
                    mask |= (1 << itemToBit.get(trimmed));
                }
            }
            menuMasks[i] = mask;
        }
        
        // Optimized memoization: (mask, menuIndex) -> (cost, solution_path)
        Map<String, OptimalState> memo = new HashMap<>();
        
        OptimalState result = topDownOptimizedHelper(0, 0, targetMask, menuMasks, prices, items, memo);
        
        if (result == null || result.cost == Double.MAX_VALUE) {
            return null;
        }
        
        return new OptimalResult(result.cost, result.indices, result.menuItems);
    }
    
    private static class OptimalState {
        double cost;
        List<Integer> indices;
        List<String> menuItems;
        
        OptimalState(double cost, List<Integer> indices, List<String> menuItems) {
            this.cost = cost;
            this.indices = new ArrayList<>(indices);
            this.menuItems = new ArrayList<>(menuItems);
        }
    }
    
    private static OptimalState topDownOptimizedHelper(int menuIndex, int currentMask, int targetMask,
                                                      int[] menuMasks, double[] prices, String[] items,
                                                      Map<String, OptimalState> memo) {
        
        // Base case: target achieved
        if ((currentMask & targetMask) == targetMask) {
            return new OptimalState(0.0, new ArrayList<>(), new ArrayList<>());
        }
        
        // Base case: no more items
        if (menuIndex >= menuMasks.length) {
            return null;
        }
        
        // Check memoization
        String key = currentMask + "," + menuIndex;
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        
        OptimalState bestState = null;
        
        // Option 1: Include current menu item
        int newMask = currentMask | menuMasks[menuIndex];
        OptimalState includeState = topDownOptimizedHelper(menuIndex + 1, newMask, targetMask, 
                                                          menuMasks, prices, items, memo);
        
        if (includeState != null) {
            double totalCost = prices[menuIndex] + includeState.cost;
            List<Integer> newIndices = new ArrayList<>();
            newIndices.add(menuIndex);
            newIndices.addAll(includeState.indices);
            
            List<String> newMenuItems = new ArrayList<>();
            newMenuItems.add(items[menuIndex]);
            newMenuItems.addAll(includeState.menuItems);
            
            bestState = new OptimalState(totalCost, newIndices, newMenuItems);
        }
        
        // Option 2: Skip current menu item
        OptimalState skipState = topDownOptimizedHelper(menuIndex + 1, currentMask, targetMask,
                                                       menuMasks, prices, items, memo);
        
        if (skipState != null && (bestState == null || skipState.cost < bestState.cost)) {
            bestState = skipState;
        }
        
        // Memoize and return
        memo.put(key, bestState);
        return bestState;
    }

    public static void main(String[] args) {
        double[] prices = {8.0, 9.0, 1.0, 11.0, 12.0};
        String[] items = {"pizza", "pasta", "pizza, pasta", "burger", "pizza, pasta"};
        String[] order = {"burger", "pizza"};

        System.out.println("Menu items:");
        for (int i = 0; i < items.length; i++) {
            System.out.printf("  %d: %s ($%.1f)%n", i, items[i], prices[i]);
        }
        System.out.println("Required: " + Arrays.toString(order));
        
        System.out.println("\n=== BACKTRACKING SOLUTION ===");
        long startTime, endTime;

        startTime = System.nanoTime();
        findOptimalChoice(prices, items, order);
        endTime = System.nanoTime();
        long time1 = endTime - startTime;
        System.out.println("BACKTRACKING" + " (" + time1/1000 + " μs)");
        // Reset static variables for fair comparison
        minPrice = Double.MAX_VALUE;
        bestCombo = new ArrayList<>();
        startTime = System.nanoTime();
        System.out.println("\n=== OPTIMAL DP SOLUTION (Bitmask) ===");
        OptimalResult dpResult = findOptimalChoiceDP(prices, items, order);
        if (dpResult != null) {
            System.out.println("Optimal choice: " + dpResult.menuItems + " with price: " + dpResult.cost);
            System.out.println("Menu indices: " + dpResult.indices);
        } else {
            System.out.println("No solution found");
        }
        endTime = System.nanoTime();
         time1 = endTime - startTime;
        System.out.println("OPTIMAL DP SOLUTION (Bitmask)" + " (" + time1/1000 + " μs)");
        
        System.out.println("\n=== OPTIMAL DP SOLUTION (Set-based) ===");
        OptimalResult setDpResult = findOptimalChoiceSetDP(prices, items, order);
        if (setDpResult != null) {
            System.out.println("Optimal choice: " + setDpResult.menuItems + " with price: " + setDpResult.cost);
            System.out.println("Menu indices: " + setDpResult.indices);
        } else {
            System.out.println("No solution found");
        }

        System.out.println("\n=== BFS + PRIORITY QUEUE SOLUTION ===");
        startTime = System.nanoTime();
        OptimalResult bfsResult = findOptimalChoiceBFSPQ(prices, items, order);
        if (bfsResult != null) {
            System.out.println("Optimal choice: " + bfsResult.menuItems + " with price: " + bfsResult.cost);
            System.out.println("Menu indices: " + bfsResult.indices);
        } else {
            System.out.println("No solution found");
        }
        endTime = System.nanoTime();
        time1 = endTime - startTime;
        System.out.println("OPTIMAL BFS + PRIORITY QUEUE SOLUTION" + " (" + time1/1000 + " μs)");
        System.out.println("\n=== BFS + PQ (Bitmask) SOLUTION ===");
        startTime = System.nanoTime();
        OptimalResult bfsBitmaskResult = findOptimalChoiceBFSPQBitmask(prices, items, order);
        if (bfsBitmaskResult != null) {
            System.out.println("Optimal choice: " + bfsBitmaskResult.menuItems + " with price: " + bfsBitmaskResult.cost);
            System.out.println("Menu indices: " + bfsBitmaskResult.indices);
        } else {
            System.out.println("No solution found");
        }
        endTime = System.nanoTime();
        time1 = endTime - startTime;
        System.out.println("OPTIMAL BFS + PRIORITY QUEUE Bitmask SOLUTION" + " (" + time1/1000 + " μs)");
        
        System.out.println("\n=== TOP-DOWN DP SOLUTION ===");
        startTime = System.nanoTime();
        OptimalResult topDownResult = findOptimalChoiceTopDownDP(prices, items, order);
        if (topDownResult != null) {
            System.out.println("Optimal choice: " + topDownResult.menuItems + " with price: " + topDownResult.cost);
            System.out.println("Menu indices: " + topDownResult.indices);
        } else {
            System.out.println("No solution found");
        }
        endTime = System.nanoTime();
        time1 = endTime - startTime;
        System.out.println("TOP-DOWN DP SOLUTION" + " (" + time1/1000 + " μs)");
        
        System.out.println("\n=== OPTIMIZED TOP-DOWN DP SOLUTION ===");
        startTime = System.nanoTime();
        OptimalResult topDownOptResult = findOptimalChoiceTopDownDPOptimized(prices, items, order);
        if (topDownOptResult != null) {
            System.out.println("Optimal choice: " + topDownOptResult.menuItems + " with price: " + topDownOptResult.cost);
            System.out.println("Menu indices: " + topDownOptResult.indices);
        } else {
            System.out.println("No solution found");
        }
        endTime = System.nanoTime();
        time1 = endTime - startTime;
        System.out.println("OPTIMIZED TOP-DOWN DP SOLUTION" + " (" + time1/1000 + " μs)");
        
        // Test with a more complex example
        System.out.println("\n============================================================");
        System.out.println("COMPLEX TEST CASE");
        
        double[] prices2 = {5.0, 7.0, 6.0, 8.0, 12.0, 15.0, 10.0};
        String[] items2 = {
            "pizza", 
            "burger", 
            "pasta", 
            "pizza, burger", 
            "burger, pasta, salad", 
            "pizza, pasta, burger, salad, fries",
            "salad, fries"
        };
        String[] order2 = {"pizza", "burger", "salad"};
        
        System.out.println("Menu items:");
        for (int i = 0; i < items2.length; i++) {
            System.out.printf("  %d: %s ($%.1f)%n", i, items2[i], prices2[i]);
        }
        System.out.println("Required: " + Arrays.toString(order2));
        
        OptimalResult complex = findOptimalChoiceDP(prices2, items2, order2);
        if (complex != null) {
            System.out.println("Optimal solution: " + complex);
        } else {
            System.out.println("No solution found with bitmask DP");
        }
        
        // Try with set-based DP as well
        OptimalResult complexSet = findOptimalChoiceSetDP(prices2, items2, order2);
        if (complexSet != null) {
            System.out.println("Set-based DP solution: " + complexSet);
        } else {
            System.out.println("No solution found with set-based DP");
        }
        
        // Test BFS + PQ approaches
        OptimalResult complexBFS = findOptimalChoiceBFSPQ(prices2, items2, order2);
        if (complexBFS != null) {
            System.out.println("BFS + PQ solution: " + complexBFS);
        } else {
            System.out.println("No solution found with BFS + PQ");
        }
        
        OptimalResult complexBFSBitmask = findOptimalChoiceBFSPQBitmask(prices2, items2, order2);
        if (complexBFSBitmask != null) {
            System.out.println("BFS + PQ (Bitmask) solution: " + complexBFSBitmask);
        } else {
            System.out.println("No solution found with BFS + PQ (Bitmask)");
        }
    }

    /*
     * ==========================================
     * TIME AND SPACE COMPLEXITY ANALYSIS
     * ==========================================
     * 
     * Let:
     * - n = number of menu items
     * - k = number of unique food items across all menus
     * - r = number of required items in order
     * 
     * 1. BACKTRACKING SOLUTION (Original findOptimalChoice):
     *    Time Complexity: O(2^n * k)
     *    - Explores all 2^n possible combinations of menu items
     *    - For each combination, checks coverage and updates memoization: O(k)
     *    - Memoization helps but worst case remains exponential
     *    Space Complexity: O(2^k * n + n)
     *    - Memoization map can store up to 2^k different coverage states
     *    - Recursion stack depth: O(n)
     *    - Each memo entry stores combination indices
     * 
     * 2. OPTIMAL BITMASK DP (findOptimalChoiceDP):
     *    Time Complexity: O(2^k * n)
     *    - DP table has 2^k possible states (all subsets of unique items)
     *    - For each state, try all n menu items
     *    - Bitwise operations are O(1)
     *    Space Complexity: O(2^k)
     *    - DP array of size 2^k
     *    - Parent array for solution reconstruction
     *    - Significantly more space-efficient than backtracking
     * 
     * 3. SET-BASED DP (findOptimalChoiceSetDP):
     *    Time Complexity: O(n * 2^k * k)
     *    - Process each of n menu items
     *    - Up to 2^k possible states in DP map
     *    - Set operations (union, containsAll) take O(k) time
     *    Space Complexity: O(2^k * (k + n))
     *    - HashMap can store up to 2^k states
     *    - Each state stores a set of k items and list of n indices
     * 
     * 4. BFS + PRIORITY QUEUE (findOptimalChoiceBFSPQ):
     *    Time Complexity: O(2^k * n * k * log(2^k))
     *    - Up to 2^k states in priority queue
     *    - For each state, try n menu items
     *    - Set operations take O(k) time
     *    - Priority queue operations take O(log(2^k)) = O(k) time
     *    Space Complexity: O(2^k * (k + n))
     *    - Priority queue stores up to 2^k states
     *    - Each state contains set of k items and list of n indices
     * 
     * 5. BFS + PQ BITMASK (findOptimalChoiceBFSPQBitmask):
     *    Time Complexity: O(2^k * n * log(2^k))
     *    - Up to 2^k states in priority queue
     *    - For each state, try n menu items
     *    - Bitwise operations are O(1)
     *    - Priority queue operations take O(k) time
     *    Space Complexity: O(2^k * n)
     *    - Priority queue and visited map store up to 2^k states
     *    - Each state maps to list of n menu indices
     * 
     * ALGORITHM COMPARISON:
     * 
     * PERFORMANCE RANKING (Best to Worst):
     * 1. Bitmask DP: O(2^k * n) - Most efficient offline computation
     * 2. BFS + PQ Bitmask: O(2^k * n * log(2^k)) - Best online/early termination
     * 3. Set-based DP: O(n * 2^k * k) - Clean and reliable
     * 4. BFS + PQ Set: O(2^k * n * k * log(2^k)) - Good for interactive search
     * 5. Backtracking: O(2^n * k) - Exponential in menu items, poor scaling
     * 
     * SPACE EFFICIENCY RANKING:
     * 1. Bitmask DP: O(2^k) - Most compact
     * 2. BFS + PQ Bitmask: O(2^k * n) - Moderate
     * 3. Set-based DP: O(2^k * (k + n)) - Good
     * 4. BFS + PQ Set: O(2^k * (k + n)) - Good
     * 5. Backtracking: O(2^k * n + n) - Least efficient
     * 
     * PRACTICAL CONSIDERATIONS:
     * - For k ≤ 20: All approaches feasible, bitmask DP recommended for batch processing
     * - For k > 20: Consider approximation algorithms or heuristics
     * - Bitmask DP: Best for offline computation, complete solution space
     * - BFS + PQ: Best for online/interactive systems, early termination possible
     * - Set-based approaches: More readable, easier to debug and maintain
     * - Backtracking: Should be avoided for this problem type
     * 
     * WHEN TO USE BFS + PRIORITY QUEUE:
     * - Interactive applications where you want to show progress
     * - When early termination is valuable (stop at first valid solution)
     * - Memory-constrained environments (can limit queue size)
     * - When you need to explore solutions in cost order
     * - Real-time systems where you can time-bound the search
     * 
     * REAL-WORLD APPLICATIONS:
     * - Restaurant menu optimization
     * - Product bundle selection
     * - Resource allocation with constraints
     * - Feature selection in machine learning pipelines
     */
}