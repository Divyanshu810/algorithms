package JavaConcepts.Concurrency;

import java.util.concurrent.CompletableFuture;

public class Basics {

    public static void main(String[] args) {
        concurrencyFunc();
    }
    private static void concurrencyFunc() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(()-> {
            return "Hello World";
        });
        future.thenAccept(System.out::println);

        CompletableFuture<String> future1 = CompletableFuture.supplyAsync(()-> {
            return "Hello World1";
        });


    }


    class Solution {
        public long countSubarrays(int[] nums, long k) {
            int s = 0, ans = 0;

            for(int i = 0, j = 0; j<nums.length; j++) {
                s += nums[j];
                if(s*(j-i+1) < k)ans += j-i +1;
                System.out.println(j + " "  + i + " " + ans);

                while(i<=j && s*(j-i+1)>=k){
                    s -= nums[i];
                    i++;
                    if(s*(j-i+1) < k)ans += j-i+1;
                    System.out.println(j + " "  + i + " " + ans + " ::");
                }
            }
//            StringBuilder s = new StringBuilder();
//            s.
            String ts = "asdhiauj";
//            ts.sub
//            Integer.pars
            return ans;
        }
    }
}
