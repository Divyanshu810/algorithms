package practice.airbnb;

import java.util.*;

public class TextJustification {

    public static List<String> fullJustify(String[] words, int maxWidth) {
        List<String> result = new ArrayList<>();
        int i = 0;

        while (i < words.length) {
            int lineLength = words[i].length();
            int j = i + 1;

            // Try to fit as many words as possible
            while (j < words.length && lineLength + words[j].length() + j-i <= maxWidth) {
                lineLength += words[j].length() ; // +1 for the space
                j++;
            }

            int numWords = j - i;
            int totalSpaces = maxWidth - lineLength;
            StringBuilder line = new StringBuilder();

            // Last line or single word line -> left justify
            if (j == words.length || numWords == 1) {
                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k != j - 1) line.append(" ");
                }
                int remaining = maxWidth - line.length();
                while (remaining-- > 0) line.append(" ");
            } else {
                // Fully justify the line
                int spacesBetween = totalSpaces / (numWords - 1);
                int extraSpaces = totalSpaces % (numWords - 1);

                for (int k = i; k < j; k++) {
                    line.append(words[k]);
                    if (k != j - 1) {
                        int spacesToApply = spacesBetween + (extraSpaces-- > 0 ? 1 : 0);
                        while (spacesToApply-- > 0) line.append(" ");
                    }
                }
            }

            result.add(line.toString());
            i = j;
        }

        return result;
    }

    // Sample test
    public static void main(String[] args) {
        String[] words = {
            "The", "day", "began", "as", "still", "as", "the",
            "night", "abruptly", "lighted", "with", "brilliant", "flame"
        };
        int maxWidth = 24;

        List<String> justified = fullJustify(words, maxWidth);
        for (String line : justified) {
            System.out.println("\"" + line + "\"");
        }
    }
}
