package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class Subsequences {

    public static void main(String[] args) {

        int[] a = {3,1,2};
        List<Integer> ans = new ArrayList<>();
        subseq(0, ans, a);

    }

    private static void subseq(int i, List<Integer> ans, int[] a) {
        if(i >= a.length){
            for(int k : ans){
                System.out.print(k + " ");
            }
            System.out.println();
            return;
        }

        ans.add(a[i]);
        subseq(i+1, ans, a);
        ans.remove(ans.size()-1);
        subseq(i+1, ans, a);
    }
}
