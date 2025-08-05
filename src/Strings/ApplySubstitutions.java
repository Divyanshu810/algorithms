package Strings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class ApplySubstitutions {
    /*
        str = abc%A%_xyz%B%
        A -> PQR
        B -> LMN_%A%
     */

    public static void main(String[] args) {
        List<List<String>> subs = Arrays.asList(
                Arrays.asList("name", "John"),
                Arrays.asList("farewell", "Goodbye, %name%")
        );
        String text = "Hello, %name%! %farewell%!";
        String result = applySubstitutions(subs, text);
        System.out.println(result);

    }

    private static String applySubstitutions(List<List<String>> subs, String str) {
        HashMap<String, String> hm = new HashMap<>();
        for(List<String> st : subs) {
            hm.put(st.get(0), st.get(1));
        }

        return dfs(hm, str);
    }
    private static String dfs(HashMap<String, String> hm, String str) {
        int idx1 = str.indexOf('%');
        if(idx1 == -1) return str;

        int idx2 = str.indexOf('%', idx1 + 1);
        if(idx2 == -1) return str;

        String key = str.substring(idx1+1, idx2);
        String value = dfs(hm, hm.getOrDefault(key, ""));

        return str.substring(0, idx1) + value + dfs(hm, str.substring(idx2+1));
    }
}
