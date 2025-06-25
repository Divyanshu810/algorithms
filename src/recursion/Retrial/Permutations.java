package recursion.Retrial;

import java.util.List;
import java.util.ArrayList;

public class Permutations {
    public static void main(String[] args) {
        int[] nums = {1,2,3};
        boolean[] freq = new boolean[nums.length];
        List<List<Integer>> sol = new ArrayList<>();

        permute(nums, sol, freq, new ArrayList<>());
        for(List<Integer> l : sol) {
            for(int i : l) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static void permute(int[] nums, List<List<Integer>> sol, boolean[] f, List<Integer> ds) {
        if(ds.size() == nums.length) {
            sol.add(new ArrayList<>(ds));
            return;
        }

        for(int i = 0; i<nums.length; i++) {
            if(!f[i]) {
                f[i] = true;
                ds.add(nums[i]);
                permute(nums, sol, f, ds);
                ds.remove(ds.size()-1);
                f[i] = false;
            }
        }
    }
}
