package AirBnB;

import java.io.*;
import java.math.*;
import java.security.*;
import java.text.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.*;
import java.util.regex.*;
import java.util.stream.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;


class MaxSumWithBadEle {

    /*
     * Complete the 'maxIndex' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts following parameters:
     *  1. INTEGER steps
     *  2. INTEGER badIndex
     */

    public static int maxIndex(int steps, int badIndex) {
        // int result=0;
        int maxSteps=(steps*(steps+1))/2;
        int[][] dp=new int[steps+1][maxSteps+1];
        for(int i=0;i<dp.length;i++){
            dp[i][badIndex]=-(int)1e9;
            // Arrays.fill(dp[i],-1);
        }
        for(int j=0;j<dp[0].length;j++){
            dp[steps][j]=j;
        }
        for(int i=steps-1;i>=1;i--){
            for(int j=maxSteps;j>=0;j--){
                if(dp[i][j]!=-1e9){
                    int take=-(int)1e9;
                    if(i+j<=maxSteps){
                        take=dp[i+1][i+j];
                    }

                    int nottake=dp[i+1][j];
                    System.out.println(i+" "+j+" "+take+" "+nottake);
                    dp[i][j]=Math.max(take,nottake);
                }
            }
        }
        // return recursion(1, 0, badIndex, steps, dp);
        return dp[1][0];
    }
    static int recursion(int cursteps,int curTotal,int badIndex,int steps,int[][] dp){
         System.out.println(cursteps+" "+curTotal);
        if(cursteps>steps||curTotal==badIndex){
            return -(int)1e9;
        }
        if(cursteps==steps){
            return curTotal+cursteps;
        }
        if(dp[cursteps][curTotal]!=-1){
            return dp[cursteps][curTotal];
        }
        int take=recursion(cursteps+1,curTotal+cursteps,badIndex,steps,dp);
        int nottake=recursion(cursteps+1,curTotal,badIndex,steps,dp);

        return dp[cursteps][curTotal]=Math.max(take,nottake);
    }

    public static void main(String[] args) throws IOException {

        int steps = 4;

        int badIndex = 6;
        int s = (steps*(steps+1))/2;
        int[][] dp = new int[steps+1][s];
        for(int[] i : dp) Arrays.fill(i, -1);
        System.out.println(recursion(1, 0, badIndex, steps, dp));
//        System.out.println(maxIndex(steps, badIndex));

    }


}