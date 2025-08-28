package practice.atlassian.karat;

import java.util.Arrays;

/**Word-wrap : Given a list of strings and an integer maxLen. You have to wrap the words in to lines(which will be another string) '-' separated.
 If line length is exceeding maxLen, start new Line.
 e.g.
 I/P : ["Hello", "Sir", "Please", "Upvote", "If", "You", "Like", "My", "Post"], maxLen=10

 O/P : ["Hello-Sir", "Please", "Upvote-If", "You-Like", "My-Post"]
 */
public class WordWrap {
    public static String[] wordWrap(String[] words, int maxLen) {
        StringBuilder currentLine = new StringBuilder();
        StringBuilder result = new StringBuilder();

        for (String word : words) {
            if (currentLine.length() + word.length() + (currentLine.length() > 0 ? 1 : 0) > maxLen) {
                if (result.length() > 0) {
                    result.append(", ");
                }
                result.append(currentLine);
                currentLine.setLength(0); // Reset current line
            }
            if (currentLine.length() > 0) {
                currentLine.append("-");
            }
            currentLine.append(word);
        }

        if (currentLine.length() > 0) {
            if (result.length() > 0) {
                result.append(", ");
            }
            result.append(currentLine);
        }

        return result.toString().split(", ");
    }

    public static void main(String[] args) {
        String[] words = {"Hello", "Sir", "Please", "Upvote", "If", "You", "Like", "My", "Post"};
        int maxLen = 10;
        String[] wrappedLines = wordWrap(words, maxLen);

        System.out.println(Arrays.toString(wrappedLines));
    }
}
