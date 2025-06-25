package recursion;

import java.util.ArrayList;
import java.util.List;

public class CombinationSum {
    // tc : (2^t)*k

    public static void main(String[] args) {
        int[] arr = {2,2,1,3,4,7};
        List<List<Integer>> ans = combinationSum(arr, 7);
//        System.out.println(ans.size());
        for(List<Integer> list : ans) {
            for(Integer i : list) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public static List<List<Integer>> combinationSum(int[] arr, int t){
        List<List<Integer>> res = new ArrayList<>();
        findCombinations(0, arr, res, t, new ArrayList<>());
        return res;
    }

    private static void findCombinations(int ind, int[] arr, List<List<Integer>> res, int t, List<Integer> ds) {
        if(ind == arr.length){
            if(t == 0){
                res.add(new ArrayList<>(ds));
            }
            return;
        }
        if(arr[ind]<=t){
            ds.add(arr[ind]);
            findCombinations(ind, arr, res, t-arr[ind], ds);
            ds.remove(ds.size()-1);
        }
        findCombinations(ind+1, arr, res, t, ds);
    }
}
