package AirBnB;

import java.util.*;

public class MenuOptimizer {

    // Helper function to trim leading and trailing spaces from a string
    public static String trim(String str) {
        return str == null ? null : str.replaceAll("^\\s+|\\s+$", "");
    }

    static class Pair {
        double cost;
        List<Integer> items;

        public Pair(double cost, List<Integer> items) {
            this.cost = cost;
            this.items = items;
        }
    }

    public static Pair findOptimalChoice(double[] prices, String[] items, String[] order) {
        int n = prices.length;
        int m = (1 << order.length);
        Pair[][] dp = new Pair[n + 1][m];

        for (int i = 0; i <= n; i++) {
            for (int mask = 0; mask < m; mask++) {
                dp[i][mask] = new Pair(Double.POSITIVE_INFINITY, new ArrayList<>());
            }
        }

        dp[0][0] = new Pair(0, new ArrayList<>());

        for (int i = 1; i <= n; ++i) {
            for (int mask = 0; mask < m; ++mask) {
                // Not take
                if (dp[i - 1][mask].cost < dp[i][mask].cost) {
                    dp[i][mask] = new Pair(dp[i - 1][mask].cost, new ArrayList<>(dp[i - 1][mask].items));
                }

                // Take
                int newMask = mask;
                String[] itemDishes = items[i - 1].split(",");

                for (int j = 0; j < order.length; ++j) {
                    boolean dishFound = false;
                    for (String itemDish : itemDishes) {
                        String trimmedDish = trim(itemDish);
                        if (trimmedDish.equals(order[j])) {
                            dishFound = true;
                            break;
                        }
                    }
                    if (dishFound) {
                        newMask |= (1 << j);
                        System.out.println(newMask);
                    }
                }

                if (dp[i - 1][mask].cost + prices[i - 1] < dp[i][newMask].cost) {
                    List<Integer> newItemList = new ArrayList<>(dp[i - 1][mask].items);
                    newItemList.add(i - 1); // Add the index of the selected item
                    dp[i][newMask] = new Pair(dp[i - 1][mask].cost + prices[i - 1], newItemList);
                }
            }
        }

        return dp[n][m - 1];
    }

    public static void main(String[] args) {
        double[] prices = {8.0, 9.0, 10.0, 11.0, 12.0};
        String[] items = {"pizza", "pasta", "pizza, pasta", "burger", "burger, pizza, pasta"};
        String[] order = {"burger", "pizza"};

        Pair optimal = findOptimalChoice(prices, items, order);

        System.out.println("Optimal cost: " + optimal.cost);
        System.out.println("Selected items: " + optimal.items);
    }
}