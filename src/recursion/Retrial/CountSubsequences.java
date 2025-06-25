package recursion.Retrial;

public class CountSubsequences {

    public static void main(String[] args) {
        int[] nums = {1,2,1};
        recu(0,nums);
    }

    private static void recu(int t, int[] nums) {
        System.out.println(rec(0, t, nums,0));
    }

    private static int rec(int i, int t, int[] nums, int sum) {
        if(i == nums.length) {
            if(sum == t)
                return 1;
            return 0;
        }
        sum += nums[i];
        int l = rec(i+1, t, nums, sum);

        sum -= nums[i];
        int r = rec(i+1, t, nums, sum);

        return l+r;
    }
}
