package recursion;

public class recBasic {

    public static void main(String[] args) {
        rec(3,3);
    }
    static void rec(int i, int j){
        if(i>j)
            return;
        rec(i+1, j);
        System.out.println(i);
    }

}

