package atlassian;

import java.util.Arrays;
import java.util.List;

/**Words = ["baby", "cat", "dada", "dog"]
 * Find if any word in the list can be formed by a given word
 * word1 = ctay
 * find(words, word1) => cat
 * word2 = dad
 * find(words, word2) => -
 *
 */
public class AnagramSubsetMatch {
    public static String find(List<String> words, String given) {
        int[] givenCount = charCount(given);
        for (String word : words) {
            if (canForm(word, givenCount)) {
                return word;
            }
        }
        return "-";
    }

    private static boolean canForm(String word, int[] givenCount) {
        int[] wordCount = charCount(word);
        for (int i = 0; i < 26; i++) {
            if (wordCount[i] > givenCount[i]) {
                return false;
            }
        }
        return true;
    }

    private static int[] charCount(String s) {
        int[] count = new int[26];
        for (char c : s.toCharArray()) {
            count[c - 'a']++;
        }
        return count;
    }

    // Example usage
    public static void main(String[] args) {
        List<String> words = Arrays.asList("baby", "cat", "dada", "dog");
        System.out.println(find(words, "ctay")); // Output: cat
        System.out.println(find(words, "dad"));  // Output: -
    }
}