package CodefCont;

import java.util.Scanner;

public class SkibidusAmog {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Read the integer value
        int n = scanner.nextInt();
        scanner.nextLine(); // Consume the leftover newline

        // Read 'n' lines of string input
        String[] words = new String[n];
        for (int i = 0; i < n; i++) {
            words[i] = scanner.nextLine();
        }

        for(String s : words) {
            func(s);
        }

        scanner.close();
    }

    private static String func(String s) {
        String b = s.substring(0,s.length()-2);
        b += "i";
        System.out.println(b);
        return b;
    }

}
