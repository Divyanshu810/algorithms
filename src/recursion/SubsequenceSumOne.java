package recursion;

import java.util.ArrayList;
import java.util.List;

public class SubsequenceSumOne {
    public static void main(String[] args) {
        int[] arr = {1,2,3,1};
//        subsequenceSumOne(0, new ArrayList<>(), arr, 0, 4, arr.length);
//        subsequenceSumOneRev(0, new ArrayList<>(), arr, 0, 4, arr.length);

    }

    // print 1 subsequence with sum = target
    static boolean subsequenceSumOne(int ind, List<Integer> l, int[] nums, int sum, int t, int n) {
        if(ind == n){
            if(sum == t){
                for(int i : l)
                    System.out.print(i + " ");
                return true;
            }
            return false;
        }
        l.add(nums[ind]);
        sum += nums[ind];
        if(subsequenceSumOne(ind+1, l, nums, sum, t, n)){
            return true;
        }
        l.remove(l.size()-1);
        sum -= nums[ind];
        if(subsequenceSumOne(ind+1, l, nums, sum, t, n)){
            return true;
        }
        return false;
    }

//    static boolean subsequenceSumOneRev(int ind, List<Integer> l, int[] nums, int sum, int t, int n) {
//        if(ind == n){
//            if(sum == t){
//                for(int i : l)
//                    System.out.print(i + " ");
//                return true;
//            }
//            return false;
//        }
//        //not take
//        if(subsequenceSumOneRev(ind+1, l, nums, sum, t, n)){
//            return true;
//        }
//
//        // take
//        l.add(nums[ind]);
//        sum += nums[ind];
//        if(subsequenceSumOneRev(ind+1, l, nums, sum, t, n)){
//            return true;
//        }
//        return false;
//    }

}
