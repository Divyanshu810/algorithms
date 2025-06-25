package recursion.Retrial;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationsSumII {

    //https://leetcode.com/problems/combination-sum-ii/description/

    public static void main(String[] args) {
        int[] nums = {10,1,2,7,6,1,5};
        int t = 8;
       rec(t, nums);

    }
    private static void rec(int t, int[] nums) {
        List<List<Integer>> sol = new ArrayList<>();
        Arrays.sort(nums);

        func(0, t, nums, sol, new ArrayList<>());
        for(List<Integer> l : sol) {
            for(int i : l) {
                System.out.print(i);
            }
            System.out.println();
        }
    }

    private static void func(int i, int t, int[] nums, List<List<Integer>> sol, List<Integer> arr) {
        if(t == 0) {
            sol.add(new ArrayList<>(arr));
            return;
        }

        for(int j = i; j<nums.length; j++) {
            if(j > i && nums[j] == nums[j-1]) continue;
            if(nums[j] >t) break;

            arr.add(nums[j]);
            func(j+1, t-nums[j], nums, sol, arr);
            arr.remove(arr.size() -1);
        }
    }
}
