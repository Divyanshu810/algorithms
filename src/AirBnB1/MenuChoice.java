package AirBnB;

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
public class MenuChoice {
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

    public static void main(String[] args) {
        double[] prices = {8.0, 9.0, 10.0, 11.0, 12.0};
        String[] items = {"pizza", "pasta", "pizza, pasta", "burger", "pizza, pasta, burger"};
        String[] order = {"burger", "pizza"};

        findOptimalChoice(prices, items, order);
    }
}