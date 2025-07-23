package MiscConcepts;
import java.util.*;
public class MergeIntervalStream {
    public static void main(String[] args) {
        StreamingIntervalMerger merger = new StreamingIntervalMerger();
        merger.insert(new int[]{1, 5});
        merger.insert(new int[]{10, 12});
        merger.insert(new int[]{4, 11});
        merger.insert(new int[]{20, 25});

        for (int[] interval : merger.getMerged()) {
            System.out.println(Arrays.toString(interval));
        }
    }
}


class StreamingIntervalMerger {
    TreeMap<Integer, int[]> map = new TreeMap<>();

    public void insert(int[] newInterval) {
        int start = newInterval[0];
        int end = newInterval[1];

        // 1. Merge with overlapping interval on the left
        Map.Entry<Integer, int[]> lower = map.floorEntry(start);
        if (lower != null && lower.getValue()[1] >= start) {
            start = Math.min(start, lower.getValue()[0]);
            end = Math.max(end, lower.getValue()[1]);
            map.remove(lower.getKey());
        }

        // 2. Merge with all overlapping intervals on the right
        while (true) {
            Integer key = map.ceilingKey(start);
            if (key != null && map.get(key)[0] <= end) {
                end = Math.max(end, map.get(key)[1]);
                map.remove(key);
            } else {
                break;
            }
        }

        // 3. Insert the merged interval
        map.put(start, new int[]{start, end});
    }

    public List<int[]> getMerged() {
        return new ArrayList<>(map.values());
    }
}
