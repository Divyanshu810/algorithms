package atlassian;

/**I/P : [ "The day began as still as the", "night abruptly lighted with", "brilliant flame" ], exactLen=24

O/P :
        [
        "The--day--began-as-still",
        "as--the--night--abruptly",
        "lighted--with--brilliant",
        "flame" ] // <--- a single word on a line is not padded with spaces
 */
import java.util.*;
public class WordWrap2 {
    public static List<String> wrap(List<String> input, int exactLen) {
        List<String> words = new ArrayList<>();
        for (String line : input) {
            for (String w : line.split(" ")) {
                if (!w.isEmpty()) words.add(w);
            }
        }
        List<String> result = new ArrayList<>();
        int i = 0;
        while (i < words.size()) {
            List<String> lineWords = new ArrayList<>();
            int lineLen = 0;
            while (i < words.size()) {
                int sep = lineWords.isEmpty() ? 0 : 1;
                int nextLen = sep + words.get(i).length();
                if (lineLen + nextLen > exactLen) break;
                lineLen += nextLen;
                lineWords.add(words.get(i));
                i++;
            }
            if (i == words.size() || lineWords.size() == 1) {
                result.add(String.join("-", lineWords));
            } else {
                int gaps = lineWords.size() - 1;
                int wordLenSum = 0;
                for (String w : lineWords) wordLenSum += w.length();
                int totalDashes = exactLen - wordLenSum;
                int baseDash = totalDashes / gaps;
                int extraDash = totalDashes % gaps;
                System.out.println("gaps = " + gaps + ", totalDashes = " + totalDashes + ", baseDash = " + baseDash + ", extraDash = " + extraDash);
                StringBuilder sb = new StringBuilder();
                for (int j = 0; j < lineWords.size(); j++) {
                    sb.append(lineWords.get(j));
                    if (j < gaps) {
                        for (int d = 0; d < baseDash + (j < extraDash ? 1 : 0); d++){
                            sb.append("-");
                        }
                    }
                }
                result.add(sb.toString());
            }
        }
        return result;
    }
    public static void main(String[] args) {
        List<String> input = Arrays.asList(
                "The day began as still as the",
                "night abruptly lighted with",
                "brilliant flame"
        );
        int exactLen = 24;
        List<String> wrappedLines = wrap(input, exactLen);
        System.out.println(wrappedLines);
    }
}