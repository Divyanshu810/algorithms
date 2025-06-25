package CodefCont;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Scanner;

public class SkibidusOhio {


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

    private static void func(String s) {
        ArrayList<Character> list = new ArrayList<>();

        for(char ch : s.toCharArray()) {
            list.add(ch);
        }

        boolean f = false;
        for(int i = 0; i<list.size()-1; i++) {
            if(list.get(i) == list.get(i+1))
                f = true;
        }
        if(f){
            System.out.println(1);
        } else
        System.out.println(list.size());

    }


}
