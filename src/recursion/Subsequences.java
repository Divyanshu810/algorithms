package recursion;

import java.util.ArrayList;
import java.util.List;

public class Subsequences {

    public static void main(String[] args) {
        int[] arr = {3,1,2};
        List<Integer> l = new ArrayList<>();
        List<List<Integer>> ans = new ArrayList<>();
        ans.add(l);
        subsequences(0, l, arr);
        
    }
    static void subsequences(int i, List<Integer> a, int[] arr) {
        if(i >= arr.length){
            for(int k : a)
                System.out.print(k + " ");
            if(a.isEmpty())
                System.out.println("{}");
            System.out.println();
            return;
        }
        a.add(arr[i]);
        subsequences(i + 1, a, arr);
        a.remove(a.size() - 1);
        subsequences(i+1, a, arr);
    }
}
