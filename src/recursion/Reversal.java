package recursion;


public class Reversal {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        reversal(arr,0);
        for (int x : arr) {
            System.out.print(x);
        }

    }
    static void reversal(int[] a, int n){
        if(n >= a.length/2)
            return;
        int t = a[n];
        a[n] = a[a.length-n-1];
        a[a.length-n-1] = t;
        reversal(a,n+1);
    }
}
