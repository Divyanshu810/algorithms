package recursion.Retrial;

import java.util.List;
import java.util.ArrayList;

public class SubsetsII {

    public static void main(String[] args) {
        int[] num = {1,2,2};
        List<List<Integer>> sol = new ArrayList<>();
        subsetsII(0, new ArrayList<>(), sol,num);
        for(List<Integer> l : sol) {
            for(int i : l){
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static void subsetsII(int i, List<Integer> ds, List<List<Integer>> ans, int[] num) {
        ans.add(new ArrayList<>(ds));

        for(int j = i; j< num.length; j++) {
            if(j>i && num[j] == num[j-1]) continue;
            ds.add(num[j]);
            subsetsII(j+1, ds, ans, num);
            ds.remove(ds.size()-1);
        }

    }
}
