package recursion.Retrial;

import java.util.ArrayList;
import java.util.List;

public class PermutationsInPlace {

    public static void main(String[] args) {
        int[] n = {1,2,3};
        List<List<Integer>> sol = new ArrayList<>();
        permute(0, n, sol);
        for(List<Integer> l : sol) {
            for(int i : l) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
    }

    private static void permute(int i, int[] n, List<List<Integer>> sol) {
        if(i== n.length) {
            List<Integer> l = new ArrayList<>();
            for(int k : n) {
                l.add(k);
            }
            sol.add(l);
        }

        for(int k = i; k < n.length; k++) {
            swap(k,i,n);
            permute(i+1, n, sol);
            swap(k,i,n);
        }
    }

    private static void swap(int k, int i, int[] n) {
        int t = n[i];
        n[i] = n[k];
        n[k] = t;
    }
}
