package HackerRank.hackerrank1;

import java.math.BigDecimal;

class Solution{

    public static void main(String []args){
        //Input
        int n = 9;
        String[] s = {
                "-100",
                "50",
                "0",
                "56.6",
                "90",
                "0.12",
                ".12",
                "02.34",
                "000.000"
        };

        //Write your code here
        String[] ns = new String[n];
        String[] temps = s;

        for (int i = 0; i < n; i++) {
            BigDecimal max = new BigDecimal(temps[0]);
            int posMax = 0;

            for (int j = 1; j < temps.length; j++) {
                BigDecimal bd = new BigDecimal(temps[j]);

                if (bd.compareTo(max) > 0) {
                    max = bd;
                    posMax = j;
                }
            }

            ns[i] = temps[posMax];
            String[] temps2 = temps;
            temps = new String[n - (i+1)];

            for (int j = 0; j < temps2.length; j++) {
                if (j < posMax)
                    temps[j] = temps2[j];
                else if (j > posMax)
                    temps[j-1] = temps2[j];
            }
        }

        s = ns;

        //Output
        for(int i=0;i<n;i++)
        {
            System.out.println(s[i]);
        }
    }
}