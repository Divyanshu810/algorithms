package recursion;

public class SubsequencesCount {
    public static void main(String[] args) {
        int[] arr = {1,2,3,1};
        System.out.println(subsequencesCount(0,0,3,arr));
    }

    static int subsequencesCount(int i, int sum, int target, int[] arr) {
        if(i == arr.length){
            if( sum == target){
                return 1;
            }
            return 0;
        }
        sum += arr[i];
        int l = subsequencesCount(i+1, sum, target, arr);

        sum -= arr[i];
        int r = subsequencesCount(i+1, sum, target,arr);

        return l + r;
    }
}
