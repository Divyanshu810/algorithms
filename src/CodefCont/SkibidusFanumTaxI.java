package CodefCont;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SkibidusFanumTaxI {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read number of test cases
        int t = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        List<int[]> firstArrays = new ArrayList<>();
        List<int[]> secondArrays = new ArrayList<>();

        for (int test = 0; test < t; test++) {
            // Read array sizes
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            // Read first array
            int[] firstArray = new int[n];
            for (int i = 0; i < n; i++) {
                firstArray[i] = scanner.nextInt();
            }
            scanner.nextLine(); // Consume newline

            // Read second array
            int[] secondArray = new int[m];
            for (int i = 0; i < m; i++) {
                secondArray[i] = scanner.nextInt();
            }
            scanner.nextLine(); // Consume newline
            firstArrays.add(firstArray);
            secondArrays.add(secondArray);
        }

        for(int i = 0; i<t; i++) {
            func(firstArrays.get(i), secondArrays.get(i));
        }



        scanner.close();
    }

    private static void func(int[] a, int[] b) {
        int dif = b[0];
        boolean f = false;
        if(a.length == 1){
            System.out.println("YES");
            return;
        }
        for(int i = 0; i<a.length-1; i++) {
            if(a[i] > a[i+1]){
                a[i]  = dif-a[i];
                if(a[i]>a[i+1]){
                    f = false;
                    break;
                } else if(i>0 && (a[i] < a[i-1])){
                    f = false;
                    break;
                }
                else
                    f = true;
            }
        }

        if(f)
            System.out.println("YES");
        else
            System.out.println("NO");
    }


}
