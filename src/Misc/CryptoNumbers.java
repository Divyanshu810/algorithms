package Misc;

/*
Given two integers n and m, find all the crypto numbers in the range [n, m]. A number is called a crypto number if all adjacent digits have an absolute difference of 1.
Example:
Input: n = 0, m = 15
Output: 0 1 2 3 4 5 6 7 8 9 10 12
Input: n = 20, m = 25
Output: 21 23
 */

import java.util.*;

public class CryptoNumbers {
        public static List<Integer> findCryptoNumbers(int n, int m) {
            List<Integer> result = new ArrayList<>();
            Queue<Integer> queue = new LinkedList<>();

            // Start from digits 1 to 9
            for (int i = 1; i <= 9; i++) {
                queue.offer(i);
            }

            while (!queue.isEmpty()) {
                int current = queue.poll();

                if (current > m) continue;

                if (current >= n && current <= m) {
                    result.add(current);
                }

                int lastDigit = current % 10;

                // Append next digits with abs diff = 1
                if (lastDigit > 0) {
                    int next = current * 10 + (lastDigit - 1);
                    if (next <= m) queue.offer(next);
                }

                if (lastDigit < 9) {
                    int next = current * 10 + (lastDigit + 1);
                    if (next <= m) queue.offer(next);
                }
            }

            Collections.sort(result);
            return result;
        }

        public static void main(String[] args) {
            int n = 0, m = 15;
            List<Integer> cryptoNumbers = findCryptoNumbers(n, m);
            System.out.println("Crypto Numbers: " + cryptoNumbers);
        }
}


