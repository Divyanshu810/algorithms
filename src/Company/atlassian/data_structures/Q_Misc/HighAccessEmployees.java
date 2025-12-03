package Company.atlassian.data_structures.Q_Misc;

import java.util.*;

/**
 * LeetCode: High Access Employees
 * Find employees with >= 3 accesses within any 60-minute window.
 *
 * Time: O(N log N) due to per-employee sort
 * Space: O(N)
 */
public class HighAccessEmployees {

    // ===== Public API expected by LeetCode =====
    // If your judge uses a different signature, rename accordingly.
    public List<String> findHighAccessEmployees(List<List<String>> access_times) {
        AccessAnalyzer analyzer = new AccessAnalyzer(new ThreeInWindowRule(3, 59));
        return analyzer.findHighAccessEmployees(access_times);
    }

    // ===== Domain model =====
    static final class AccessRecord {
        final String employee;
        final int minutes; // minutes since 00:00
        AccessRecord(String employee, String timeHHMM) {
            this.employee = employee;
            this.minutes = parseToMinutes(timeHHMM);
        }
        static int parseToMinutes(String hhmm) {
            int h = (hhmm.charAt(0) - '0') * 10 + (hhmm.charAt(1) - '0');
            int m = (hhmm.charAt(3) - '0') * 10 + (hhmm.charAt(4) - '0');
            return h * 60 + m;
        }
    }

    // Rule SPI so we can scale thresholds or window sizes later
    interface HighAccessRule {
        boolean isHighAccess(int[] sortedMinutes);
    }

    // Default LeetCode rule: >=K accesses within any W-minute window (inclusive)
    static final class ThreeInWindowRule implements HighAccessRule {
        private final int k;      // e.g., 3
        private final int window; // e.g., 60
        ThreeInWindowRule(int k, int window) {
            this.k = k;
            this.window = window;
        }
        @Override
        public boolean isHighAccess(int[] t) {
            if (t.length < k) return false;
            // Sliding window over sorted times; only need to compare i with i+k-1
            for (int i = 0; i + (k - 1) < t.length; i++) {
                int j = i + (k - 1);
                if (t[j] - t[i] <= window) return true;
            }
            return false;
        }
    }

    // Orchestrates parsing, bucketing, sorting, and applying the rule
    static final class AccessAnalyzer {
        private final HighAccessRule rule;
        AccessAnalyzer(HighAccessRule rule) { this.rule = rule; }

        List<String> findHighAccessEmployees(List<List<String>> accessTimes) {
            // 1) Parse & bucket by employee
            Map<String, List<Integer>> byEmp = new HashMap<>();
            for (List<String> row : accessTimes) {
                // row: [employee, "HH:MM"]
                String emp = row.get(0);
                String time = row.get(1);
                int minutes = AccessRecord.parseToMinutes(time);
                byEmp.computeIfAbsent(emp, k -> new ArrayList<>()).add(minutes);
            }

            // 2) For each employee, sort times and apply rule
            List<String> ans = new ArrayList<>();
            for (Map.Entry<String, List<Integer>> e : byEmp.entrySet()) {
                List<Integer> list = e.getValue();
                Collections.sort(list);
                int[] arr = new int[list.size()];
                for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
                if (rule.isHighAccess(arr)) ans.add(e.getKey());
            }

            // 3) Lexicographically sort employee names as per problem expectation
            Collections.sort(ans);
            return ans;
        }
    }
}
