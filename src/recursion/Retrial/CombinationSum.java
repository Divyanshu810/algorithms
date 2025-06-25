package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class CombinationSum {
    //https://leetcode.com/problems/combination-sum/description/
    //type I

    public static void main(String[] args) {

        int[] nums = {1,2,3,4,5};
        int[] nums1 = {2,3,5};
        List<List<Integer>> sol = new ArrayList<>();
//        combinations(0,7,nums, new ArrayList<>(),sol);
        combinations(0,8,nums1, new ArrayList<>(),sol);

        for(List<Integer> l : sol) {
            for(int i : l) {
                System.out.print(i);
            }
            System.out.println();
        }

    }

    private static void combinations(int i, int t, int[] nums, List<Integer> arr, List<List<Integer>> sol) {
        if(i == nums.length){
            if(t == 0){
                sol.add(new ArrayList<>(arr));
            }
            return;
        }

        if(t-nums[i] >=0){
            arr.add(nums[i]);
            combinations(i, t-nums[i],nums, arr, sol);
            arr.remove(arr.size()-1);
        }
        combinations(i+1, t, nums, arr, sol);
    }
}
