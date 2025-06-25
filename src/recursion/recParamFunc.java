package recursion;

public class recParamFunc {
    public static void main(String[] args) {
        rec(5,0);
        recZero(5,0,1);
        System.out.println(recFunc(5));
    }

    static void rec(int n, int m) {
        if(n<1){
            System.out.println(m);
            return;
        }
        rec(n-1, m+n);
    }

    static void recZero(int n, int m, int i) {
        if(i>n){
            System.out.println(m);
            return;
        }
        recZero(n, m+i, i+1);
    }

    static int recFunc(int n){
        if(n == 0)
             return 0;
        return n + recFunc(n-1);
    }
}
