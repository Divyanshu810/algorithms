package Misc;

/*
You need to find the height of a tree given its parent-child relationship in an array where each index represents a node, and the value at that index represents its parent. The root node has a value of -1.
Input: [4, 3, 0, 6, 6, 3, -1, 0]
Output: 4

Create adj list node-> childs
DFS on each nodes
 */
import java.util.*;

public class ParentChildTreeHeight {
    public static int findHeight(int[] parent) {
        int n = parent.length;
        Map<Integer, List<Integer>> tree = new HashMap<>();
        int root = -1;

        // Build the tree (children map)
        for (int i = 0; i < n; i++) {
            int p = parent[i];
            if (p == -1) {
                root = i;
            } else {
                tree.computeIfAbsent(p, k -> new ArrayList<>()).add(i);
            }
        }

        // DFS to find the height
        return dfs(tree, root);
    }

    private static int dfs(Map<Integer, List<Integer>> tree, int node) {
        if (!tree.containsKey(node)) return 1; // leaf node

        int maxHeight = 0;
        for (int child : tree.get(node)) {
            maxHeight = Math.max(maxHeight, dfs(tree, child));
        }
        return maxHeight + 1;
    }

    public static void main(String[] args) {
        int[] parent = {4, 3, 0, 6, 6, 3, -1, 0};
        int height = findHeight(parent);
        System.out.println("Height of tree: " + height);  // Output: 4
    }
}
