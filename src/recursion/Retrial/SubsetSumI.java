package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class SubsetSumI {

    public static void main(String[] args) {
        int[] nums = {5, 6, 7};
        int[] nu = {2,3};
        List<Integer> sol = new ArrayList<>();
        subsetSum(0, nums , 0, sol);
        for(int i : sol) {
            System.out.println(i);
        }
    }

    private static void subsetSum(int i, int[] nums, int s, List<Integer> sol ) {
        if(i == nums.length) {
            sol.add(s);
            return;
        }

        subsetSum(i+1, nums, s + nums[i], sol);
        subsetSum(i+1, nums, s, sol);

    }
}
