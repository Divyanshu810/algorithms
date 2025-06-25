package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class PrintOneSubsequenceSum {

    public static void main(String[] args) {
        int[] num = {1,2,1};
        recu(num, 0);

    }

    private static void recu(int[] num, int t) {
        System.out.println(rec(0, 0, new ArrayList<>(), num, t));
    }

    private static boolean rec(int i, int sum, List<Integer> arr, int[] num, int t) {
        if(i == num.length) {
            if(sum == t) {
                return true;
            }
            return false;
        }

        arr.add(num[i]);
        sum += num[i];
        if(rec(i+1, sum, arr, num, t))
            return true;

        arr.remove(arr.size()-1);
        sum -= num[i];
        if(rec(i+1, sum, arr, num, t))
            return true;

        return false;
    }
}
