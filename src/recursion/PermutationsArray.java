package recursion;

import java.util.ArrayList;
import java.util.List;

public class PermutationsArray {

    public static void main(String[] args) {
        int[] nums = {1,2,3};
        permutations(nums);
    }

    private static void permutations(int[] nums){
        List<List<Integer>> ans = new ArrayList<>();
        List<Integer> ds = new ArrayList<>();
        boolean[] freq = new boolean[nums.length];
        combinations(ds, nums, ans, freq);
        for(List<Integer> l: ans){
            for(Integer i:l){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }
    private static void combinations(List<Integer> ds, int[] nums, List<List<Integer>> ans, boolean[] freq){
        if(ds.size() == nums.length){
            ans.add(new ArrayList<>(ds));
            return;
        }
        for(int i = 0; i<nums.length; i++){
            if(!freq[i]){
                freq[i] = true;
                ds.add(nums[i]);
                combinations(ds, nums, ans, freq);
                ds.remove(ds.size()-1);
                freq[i] = false;
            }
        }

    }
}
