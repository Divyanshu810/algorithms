package recursion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CombinationSumII {
    //sort the array!

    public static void main(String[] args) {
        int[] arr = {10,1,2,7,6,1,5};
        List<List<Integer>> ans = combinationSum2(arr, 8);

        for(List<Integer> l: ans){
            for(Integer i:l){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    public static List<List<Integer>> combinationSum2(int[] candidates, int target) {
        List<List<Integer>> ans = new ArrayList<>();
        Arrays.sort(candidates);
        findCombinations(0, target, ans, new ArrayList<>(), candidates);
        return ans;
    }

    public static void findCombinations(int ind, int t, List<List<Integer>> ans, List<Integer> ds, int[] arr ) {
        if(t == 0){
            ans.add(new ArrayList<>(ds));
            return;
        }

        for(int i = ind; i<arr.length; i++){
            if(i > ind && arr[i] == arr[i-1]) continue;
            if(arr[i]>t)break;

            ds.add(arr[i]);
            findCombinations(i+1, t-arr[i], ans, ds, arr);
            ds.remove(ds.size()-1);
        }
    }
}
