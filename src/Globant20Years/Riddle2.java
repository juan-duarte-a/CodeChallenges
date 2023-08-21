package Globant20Years;

/*
Riddle #2

What is the smallest natural number where the result of moving the digit on the far right to the front of the number is
a number 9 times greater?

Similarly, for 142857, when you move the rightmost digit to the first place, it becomes 714285, which is the original
number times 5.

Keep in mind:
- In this puzzle, you need the number to become the original times 9.- Provide your answer using digits only – no
spaces, letters, symbols, etc.
 */
public class Riddle2 {

    public static void main(String[] args) {
        System.out.println("Answer: " + riddle2());
//        System.out.println(Long.MAX_VALUE);
    }

    public static long riddle2() {
        for (long num = 10; ; num++) {
            int digits = String.valueOf(num).length();
            int digit = (int) (num - (10 * (num/10)));
            long value =  (num/10) + (long) digit * (int) Math.pow(10, digits-1);
            if ((num-1) % 100000000 == 0 || (num+5) % 100000000 == 0 || (num+1) % 100000000 == 0) {
                System.out.println(num + " - " + value);
            }
            if (value == num * 9) {
                System.out.println(num + " - " + value);
                System.out.println(num * 9 + " - " + value / 9);
                return num;
            }
        }
    }

}
