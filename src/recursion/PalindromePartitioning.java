package recursion;

import java.util.ArrayList;
import java.util.List;

public class PalindromePartitioning {

    public static void main(String[] args) {
        String s = "aabb";
        palindromeP(s);
    }

    private static void palindromeP(String s){
        List<List<String>> res = new ArrayList<>();
        List<String> path = new ArrayList<>();
        combinations(0, res, path, s);
        System.out.println(res.size());
        for(List<String> l : res){
            for(String z : l){
                System.out.print(z + ",");
            }
            System.out.println();
        }
//        for(int i = 0; i<res.size(); i++){
//            for(int j = 0; j< res.get(i).size(); j++){
//                System.out.println(res.get(i).get(j));
//            }
//        }
    }
    private static void combinations(int ind, List<List<String>> res, List<String> path, String s){
        if(ind == s.length()){
            res.add(new ArrayList<>(path));
            return;
        }

        for(int i = ind; i< s.length(); i++){
            if(isPalindrome(s, ind, i)){
                path.add(s.substring(ind, i+1));
                combinations(i+1, res, path, s);
                path.remove(path.size()-1);
            }
        }
    }
    private static boolean isPalindrome(String s, int ind, int i){
        while(ind <= i){
            if(s.charAt(ind++) != s.charAt(i--))return false;
        }
        return true;
    }
}
