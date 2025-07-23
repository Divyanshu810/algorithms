package MiscConcepts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DiameterPathBinaryTree {
}

class TreeNode {
    int val;
    TreeNode left, right;
    TreeNode(int val) { this.val = val; }
}

class SolutionPath {
    int maxDiameter = 0;
    List<Integer> diameterPath = new ArrayList<>();

    public List<Integer> diameterOfBinaryTree(TreeNode root) {
        dfs(root);
        return diameterPath;
    }

    // Returns height and builds path
    private List<Integer> dfs(TreeNode node) {
        if (node == null) return new ArrayList<>();

        List<Integer> leftPath = dfs(node.left);
        List<Integer> rightPath = dfs(node.right);

        // Check diameter through current node
        int currDiameter = leftPath.size() + rightPath.size();
        if (currDiameter > maxDiameter) {
            maxDiameter = currDiameter;

            // Reverse leftPath to go up from leaf to node
            List<Integer> newPath = new ArrayList<>();
            Collections.reverse(leftPath);
            newPath.addAll(leftPath);
            newPath.add(node.val);
            newPath.addAll(rightPath);
            diameterPath = newPath;
        }

        // Return the longer path + current node
        List<Integer> longer = (leftPath.size() > rightPath.size()) ? leftPath : rightPath;
        longer = new ArrayList<>(longer); // copy to avoid side effects
        longer.add(node.val);
        return longer;
    }
}

