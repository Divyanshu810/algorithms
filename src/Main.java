import java.util.Arrays;

public class Main {
//    public static void main(String[] args) {
//
//
//        System.out.println("Hello world!");
//
////        Arrays.sort();
//            String s = "asdn";
////            s.l
//        func();
//
//    }

    private static void func () {
        int[] a = new int[]{805,913,142,791,685,436,658,93,573,900,803,693,939,986,409,593,82,215,25,953,82,958,26,334,538,585,956,989,94,809,394,373,772,171,520,57,972,871,607,353,209,613,329,731,802,112,455,269,668};
        Arrays.sort(a);
        int m = 0 + (48)/2;
        System.out.println(a[m]);
    }
    public static String smallestByOneReverse(String s) {
        String min = s;
        int n = s.length();

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j <= n; j++) {
                // Reverse substring s[i..j-1]
                String reversed = new StringBuilder(s.substring(i, j)).reverse().toString();
                String candidate = s.substring(0, i) + reversed + s.substring(j);

                if (candidate.compareTo(min) < 0) {
                    min = candidate;
                }
            }
        }
        return min;
    }

    public static void main(String[] args) {
        String s = "ddefttteefff";
        System.out.println(smallestByOneReverse(s));  // Output: "acdb"
    }



}