package DP;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RussianDollEnvelopes {
// https://leetcode.com/problems/russian-doll-envelopes/description/
    public static void main(String[] args) {
        int[][] envelopes1 = {{5,4},{6,4},{6,7},{2,3}};
        System.out.println(binarySearchLIS(envelopes1));
        System.out.println(dpLIS(envelopes1));
        int[][] envelopes3 = {{4,5},{4,6},{6,7},{2,3},{1,1}};
        int[][] envelopes2 = {{1,1},{1,1},{1,1}};
        System.out.println(binarySearchLIS(envelopes2));
        System.out.println(dpLIS(envelopes2));
        System.out.println(binarySearchLIS(envelopes3));
        System.out.println(dpLIS(envelopes3));
    }

    private static int binarySearchLIS(int[][] arr) {
        Arrays.sort(arr, (a,b) -> {
            if(a[0] == b[0]) return b[1]-a[1];
            else return a[0]-b[0];
        });
        List<Integer> tail = new ArrayList<>();

        for(int[] i : arr) {
            int idx = Collections.binarySearch(tail, i[1]);
            if(idx < 0) {
                idx = -(idx + 1);
            }
            if(idx == tail.size()) tail.add(i[1]);
            else tail.set(idx, i[1]);
        }

        return tail.size();
    }

    private static int dpLIS(int[][] arr) {
        Arrays.sort(arr, (a,b) -> {
            if(a[0] == b[0]) return a[1]-b[1];
            else return a[0]-b[0];
        });
        int n = arr.length, ans = 1, li = 0;
        int[] dp = new int[n];
        int[] hash = new int[n];

        Arrays.fill(dp, 1);
        Arrays.fill(hash, 1);

        for(int i = 0; i<n; i++) {
            hash[i] = i;
            for(int j = 0; j<i; j++) {
                if(arr[i][0] > arr[j][0] && arr[i][1] > arr[j][1]) {
                    dp[i] = Math.max(dp[i], dp[j] + 1);
                    hash[i] = j;
                }
            }
            if(ans<dp[i]) {
                ans = dp[i];
                li =i;
            }
        }
        // LIS sequence
        List<int[]> lis = new ArrayList<>();
        lis.add(arr[li]);
        while(li != hash[li]) {
            li = hash[li];
            lis.add(arr[li]);
        }
        for(int[] k : lis) {
            System.out.println(k[0] + " :" + k[1]);
        }
        return ans;
    }
}
