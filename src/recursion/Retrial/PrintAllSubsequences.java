package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class PrintAllSubsequences {

    public static void main(String[] args) {
        int[] num = {1,2,1};
        recursion(num,2);
    }


    private static void recursion(int[] num, int t) {
        List<List<Integer>> sol = new ArrayList<>();
        List<Integer> arr = new ArrayList<>();
        rec(0, arr, 0, num, t, sol);
        for(List<Integer> l : sol){
            for(int i : l){
                System.out.print(i);
            }
            System.out.println();
        }
    }

    private static void rec(int i, List<Integer> arr, int sum, int[] num, int t, List<List<Integer>> sol) {
        if(i == num.length) {
            if(sum == t) {
                sol.add(new ArrayList<>(arr));
            }
            return;
        }

        arr.add(num[i]);
        sum += num[i];
        rec(i+1, arr, sum, num, t, sol);
        arr.remove(arr.size()-1);
        sum -= num[i];
        rec(i+1, arr, sum, num, t, sol);
    }
}
