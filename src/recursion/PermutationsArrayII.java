package recursion;

import java.util.ArrayList;
import java.util.List;

public class PermutationsArrayII {
    public static void main(String[] args) {
        int[] nums = {1,2,3};
        permutations(nums);
    }

    private static void permutations(int[] nums){
        List<List<Integer>> ans = new ArrayList<>();
        combinations(0, nums, ans);
        for(List<Integer> l: ans){
            for(Integer i:l){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
    private static void combinations(int ind, int[] nums, List<List<Integer>> ans){
        if(ind == nums.length){
            List<Integer> ds = new ArrayList<>();
            for(int i: nums){
                ds.add(i);
            }
            ans.add(ds);
            return;
        }

        for(int i = ind; i<nums.length; i++) {
            swap(i, ind, nums);
            combinations(ind+1, nums, ans);
            swap(i, ind, nums);
        }
    }

    private static void swap(int i, int j, int[] nums){
        int k = nums[i];
        nums[i] = nums[j];
        nums[j] = k;
    }
}
