package recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SubsetSumII {

    //https://leetcode.com/problems/subsets-ii/description/

    public static void main(String[] args) {
        int[] nums = {1,2,2};
        subset(nums);

    }

    public static void subset(int[] nums){
        Arrays.sort(nums);
        List<List<Integer>> ans = new ArrayList<>();
        combinations(0, nums, ans, new LinkedList<>());
        for(List<Integer> l:ans){
            for(Integer i: l){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static void combinations(int ind, int[] nums, List<List<Integer>> ans, LinkedList<Integer> ds){
        ans.add(new ArrayList<>(ds));

        for(int i = ind; i<nums.length; i++){
            if(i!= ind && nums[i] == nums[i-1])continue;
            ds.add(nums[i]);
            combinations(i+1, nums, ans, ds);
            ds.removeLast();
        }
    }
}
