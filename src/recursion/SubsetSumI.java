package recursion;

import java.util.ArrayList;

public class SubsetSumI {
    public static void main(String[] args) {
        int[] arr = {3,2,1};
        subsetSum(arr, 3);
    }

    public static void subsetSum(int[] arr, int N){
        ArrayList<Integer> sum = new ArrayList<>();
        combination(0, N, sum, 0, arr);
        for(Integer a: sum){
            System.out.print(a + " ");
        }
    }

    public static void combination(int ind, int N, ArrayList<Integer> sum, int s, int[] arr){
        if(ind == N){
            sum.add(s);
            return;
        }
        combination(ind+1, N, sum, s + arr[ind], arr);
        combination(ind+1, N, sum, s, arr);
    }
}
