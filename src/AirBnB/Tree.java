package practice.airbnb;

import java.util.*;

/**
 * How can I modify this tree so that it becomes a subsequence of the given sequence?
 * How can I do this in a way that minimizes the number of steps (overwriting existing node values or inserting new nodes both count as 1)?
 * 
 * Problem: Given a binary tree and a target sequence, find the minimum number of operations
 * to make any root-to-leaf path in the tree match a subsequence of the target sequence.
 * Operations: 
 * 1. Change value of existing node (cost = 1)
 * 2. Insert new node (cost = 1)
 */
public class Tree {
    
    public static class TreeNode {
        int val;
        TreeNode left;
        TreeNode right;
        
        public TreeNode(int val) {
            this.val = val;
        }
        
        public TreeNode(int val, TreeNode left, TreeNode right) {
            this.val = val;
            this.left = left;
            this.right = right;
        }
        
        @Override
        public String toString() {
            return String.valueOf(val);
        }
    }
    
    /**
     * Find minimum operations to make tree a subsequence of target sequence
     * Using Dynamic Programming with memoization
     */
    public static int minOperationsToSubsequence(TreeNode root, int[] target) {
        if (root == null || target.length == 0) return 0;
        
        Map<String, Integer> memo = new HashMap<>();
        return dfs(root, target, 0, memo);
    }
    
    private static int dfs(TreeNode node, int[] target, int targetIndex, Map<String, Integer> memo) {
        if (node == null) return 0;
        
        // Memoization key: node value + target index + whether it's leaf
        String key = node.val + "," + targetIndex + "," + (node.left == null && node.right == null);
        if (memo.containsKey(key)) {
            return memo.get(key);
        }
        
        int result = Integer.MAX_VALUE;
        
        // If this is a leaf node, we need to match with some element in target[targetIndex:]
        if (node.left == null && node.right == null) {
            // Option 1: Don't change this node, find matching element in remaining target
            for (int i = targetIndex; i < target.length; i++) {
                if (target[i] == node.val) {
                    result = Math.min(result, 0); // No operation needed if we find a match
                    break;
                }
            }
            
            // Option 2: Change this node to match the next available element
            if (targetIndex < target.length) {
                result = Math.min(result, 1); // 1 operation to change the value
            }
            
            memo.put(key, result == Integer.MAX_VALUE ? 1 : result);
            return memo.get(key);
        }
        
        // For non-leaf nodes, try all possibilities
        // Option 1: Keep current value and try to match with target
        for (int i = targetIndex; i < target.length; i++) {
            if (target[i] == node.val) {
                // Found a match, continue with children
                int leftCost = node.left != null ? dfs(node.left, target, i + 1, memo) : 0;
                int rightCost = node.right != null ? dfs(node.right, target, i + 1, memo) : 0;
                result = Math.min(result, leftCost + rightCost);
                break; // Take the first match (greedy approach for subsequence)
            }
        }
        
        // Option 2: Change current value to match next target element
        if (targetIndex < target.length) {
            int leftCost = node.left != null ? dfs(node.left, target, targetIndex + 1, memo) : 0;
            int rightCost = node.right != null ? dfs(node.right, target, targetIndex + 1, memo) : 0;
            result = Math.min(result, 1 + leftCost + rightCost);
        }
        
        // Option 3: Skip current node (conceptually, but we can't actually skip)
        // This is handled by trying different target indices above
        
        memo.put(key, result == Integer.MAX_VALUE ? target.length : result);
        return memo.get(key);
    }
    
    /**
     * Alternative approach: Find minimum operations for any root-to-leaf path
     * to become a subsequence of target
     */
    public static int minOperationsAnyPath(TreeNode root, int[] target) {
        if (root == null) return 0;
        
        List<List<Integer>> allPaths = new ArrayList<>();
        getAllPaths(root, new ArrayList<>(), allPaths);
        
        int minOps = Integer.MAX_VALUE;
        for (List<Integer> path : allPaths) {
            int ops = minOperationsForPath(path, target);
            minOps = Math.min(minOps, ops);
        }
        
        return minOps == Integer.MAX_VALUE ? 0 : minOps;
    }
    
    private static void getAllPaths(TreeNode node, List<Integer> currentPath, List<List<Integer>> allPaths) {
        if (node == null) return;
        
        currentPath.add(node.val);
        
        if (node.left == null && node.right == null) {
            allPaths.add(new ArrayList<>(currentPath));
        } else {
            getAllPaths(node.left, currentPath, allPaths);
            getAllPaths(node.right, currentPath, allPaths);
        }
        
        currentPath.remove(currentPath.size() - 1);
    }
    
    private static int minOperationsForPath(List<Integer> path, int[] target) {
        int m = path.size();
        int n = target.length;
        
        // dp[i][j] = min operations to make path[0..i-1] a subsequence of target[0..j-1]
        int[][] dp = new int[m + 1][n + 1];
        
        // Initialize: path elements that don't have target elements need to be changed
        for (int i = 1; i <= m; i++) {
            dp[i][0] = i; // All path elements need to be changed
        }
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                if (path.get(i - 1).equals(target[j - 1])) {
                    // Match found, no operation needed for this element
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    // Two options:
                    // 1. Change current path element to match target[j-1]
                    // 2. Skip target[j-1] and try to match with previous target elements
                    dp[i][j] = Math.min(
                        dp[i - 1][j - 1] + 1,  // Change path[i-1] to target[j-1]
                        dp[i][j - 1]           // Skip target[j-1]
                    );
                }
            }
        }
        
        return dp[m][n];
    }
    
    /**
     * Advanced: Modify tree in-place to minimize operations
     */
    public static class ModificationResult {
        int operations;
        TreeNode modifiedTree;
        
        public ModificationResult(int operations, TreeNode modifiedTree) {
            this.operations = operations;
            this.modifiedTree = modifiedTree;
        }
        
        @Override
        public String toString() {
            return "Operations: " + operations + ", Tree: " + treeToString(modifiedTree);
        }
    }
    
    public static ModificationResult modifyTreeOptimal(TreeNode root, int[] target) {
        if (root == null) return new ModificationResult(0, null);
        
        TreeNode modifiedRoot = copyTree(root);
        int operations = modifyTreeDFS(modifiedRoot, target, 0);
        
        return new ModificationResult(operations, modifiedRoot);
    }
    
    private static TreeNode copyTree(TreeNode root) {
        if (root == null) return null;
        
        TreeNode copy = new TreeNode(root.val);
        copy.left = copyTree(root.left);
        copy.right = copyTree(root.right);
        return copy;
    }
    
    private static int modifyTreeDFS(TreeNode node, int[] target, int targetIndex) {
        if (node == null) return 0;
        
        int operations = 0;
        
        // Try to find a matching element in target starting from targetIndex
        boolean found = false;
        int matchIndex = -1;
        
        for (int i = targetIndex; i < target.length; i++) {
            if (target[i] == node.val) {
                found = true;
                matchIndex = i;
                break;
            }
        }
        
        if (!found && targetIndex < target.length) {
            // No match found, change current node to next target element
            node.val = target[targetIndex];
            operations = 1;
            matchIndex = targetIndex;
        }
        
        // Continue with children
        if (matchIndex != -1 && matchIndex + 1 < target.length) {
            operations += modifyTreeDFS(node.left, target, matchIndex + 1);
            operations += modifyTreeDFS(node.right, target, matchIndex + 1);
        }
        
        return operations;
    }
    
    /**
     * Helper method to visualize tree
     */
    public static String treeToString(TreeNode root) {
        if (root == null) return "null";
        
        StringBuilder sb = new StringBuilder();
        Queue<TreeNode> queue = new LinkedList<>();
        queue.offer(root);
        
        while (!queue.isEmpty()) {
            TreeNode node = queue.poll();
            if (node == null) {
                sb.append("null,");
            } else {
                sb.append(node.val).append(",");
                queue.offer(node.left);
                queue.offer(node.right);
            }
        }
        
        return sb.toString().replaceAll(",null,null$", "").replaceAll(",$", "");
    }
    
    /**
     * Print tree in a readable format
     */
    public static void printTree(TreeNode root) {
        printTreeHelper(root, "", false);
    }
    
    private static void printTreeHelper(TreeNode node, String prefix, boolean isLeft) {
        if (node == null) return;
        
        System.out.println(prefix + (isLeft ? "├── " : "└── ") + node.val);
        
        if (node.left != null || node.right != null) {
            if (node.left != null) {
                printTreeHelper(node.left, prefix + (isLeft ? "│   " : "    "), true);
            }
            if (node.right != null) {
                printTreeHelper(node.right, prefix + (isLeft ? "│   " : "    "), false);
            }
        }
    }
    
    public static void main(String[] args) {
        // Test Case 1: Simple tree
        TreeNode root1 = new TreeNode(1);
        root1.left = new TreeNode(2);
        root1.right = new TreeNode(3);
        root1.left.left = new TreeNode(4);
        root1.left.right = new TreeNode(5);
        
        int[] target1 = {1, 2, 5, 3};
        
        System.out.println("=== Test Case 1 ===");
        System.out.println("Original Tree:");
        printTree(root1);
        System.out.println("Target sequence: " + Arrays.toString(target1));
        
        int ops1 = minOperationsToSubsequence(root1, target1);
        System.out.println("Min operations (DP): " + ops1);
        
        int ops2 = minOperationsAnyPath(root1, target1);
        System.out.println("Min operations (Any Path): " + ops2);
        
        ModificationResult result1 = modifyTreeOptimal(root1, target1);
        System.out.println("Modification result: " + result1);
        System.out.println("Modified Tree:");
        printTree(result1.modifiedTree);
        
        // Test Case 2: Different tree
        TreeNode root2 = new TreeNode(5);
        root2.left = new TreeNode(3);
        root2.right = new TreeNode(8);
        root2.left.left = new TreeNode(1);
        root2.left.right = new TreeNode(4);
        root2.right.right = new TreeNode(9);
        
        int[] target2 = {5, 3, 4, 8, 9};
        
        System.out.println("\n=== Test Case 2 ===");
        System.out.println("Original Tree:");
        printTree(root2);
        System.out.println("Target sequence: " + Arrays.toString(target2));
        
        int ops3 = minOperationsToSubsequence(root2, target2);
        System.out.println("Min operations (DP): " + ops3);
        
        int ops4 = minOperationsAnyPath(root2, target2);
        System.out.println("Min operations (Any Path): " + ops4);
        
        ModificationResult result2 = modifyTreeOptimal(root2, target2);
        System.out.println("Modification result: " + result2);
        System.out.println("Modified Tree:");
        printTree(result2.modifiedTree);
        
        // Test Case 3: Edge case - single node
        TreeNode root3 = new TreeNode(7);
        int[] target3 = {1, 2, 3, 7, 8};
        
        System.out.println("\n=== Test Case 3 (Single Node) ===");
        System.out.println("Original Tree:");
        printTree(root3);
        System.out.println("Target sequence: " + Arrays.toString(target3));
        
        int ops5 = minOperationsToSubsequence(root3, target3);
        System.out.println("Min operations (DP): " + ops5);
        
        int ops6 = minOperationsAnyPath(root3, target3);
        System.out.println("Min operations (Any Path): " + ops6);
        
        ModificationResult result3 = modifyTreeOptimal(root3, target3);
        System.out.println("Modification result: " + result3);
    }
}
