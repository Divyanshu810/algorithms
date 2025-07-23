package Tree;
import java.util.ArrayList;
import java.util.List;


// TreeNode structure
class TreeNode {
    int val;
    TreeNode left;
    TreeNode right;

    public TreeNode(int x) {
        val = x;
        left = null;
        right = null;
    }
}

public class BinaryTreePaths {
    // Function to find the path from the
    // root to a given node with value 'x'
    public List<String> binaryTreePaths(TreeNode root) {
        List<List<Integer>> ans = new ArrayList<>();
        List<String> a = new ArrayList<>();
        func(root, ans, new ArrayList<>());
        System.out.println(ans.size());
        for(List<Integer> l : ans) {
            System.out.println(l.toString());
            if(l.size()==0) continue;
            String s = "";
            s = s + l.get(0);
            if(l.size() < 1) continue;
            for(int i  = 1; i<l.size(); i++) {
                s= s + "->";
                s = s + l.get(i);
            }
            a.add(s);
        }
        return a;
    }

    private static void func(TreeNode n, List<List<Integer>> ans, List<Integer> p) {
        if(n == null){
            return;
        }

        if(n.left == null && n.right == null) {
            p.add(n.val);
            ans.add(new ArrayList<>(p));
            p.remove(p.size()-1);
            return;
        }
        p.add(n.val);
        func(n.left, ans, p);
        func(n.right, ans, p);
        p.remove(p.size()-1);
    }

    // Function to find and return the path from
    // the root to a given node with value 'B'
    public List<Integer> solve(TreeNode A, int B) {
        // Initialize an empty
        // list to store the path
        List<Integer> arr = new ArrayList<>();

        // If the root node is null,
        // return the empty path list
        if (A == null) {
            return arr;
        }

        // Call the getPath function to find
        // the path to the node with value 'B'
//        getPath(A, arr, B);

        // Return the path list
        return arr;
    }

    public static void main(String[] args) {
        TreeNode root = new TreeNode(3);
        root.left = new TreeNode(5);
        root.right = new TreeNode(1);
        root.left.left = new TreeNode(6);
        root.left.right = new TreeNode(2);
        root.right.left = new TreeNode(0);
        root.right.right = new TreeNode(8);
        root.left.right.left = new TreeNode(7);
        root.left.right.right = new TreeNode(4);

        BinaryTreePaths sol = new BinaryTreePaths();

        int targetLeafValue = 7;

//        List<Integer> path = sol.solve(root, targetLeafValue);
        List<String> paths = sol.binaryTreePaths(root);
        System.out.print("Path from root to leaf with value " +
                targetLeafValue + ": ");
//        for (int i = 0; i < path.size(); ++i) {
//            System.out.print(path.get(i));
//            if (i < path.size() - 1) {
//                System.out.print(" -> ");
//            }
//        }
    }
}

