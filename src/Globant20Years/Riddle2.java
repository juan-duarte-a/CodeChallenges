package Globant20Years;

import java.util.ArrayList;

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

    private long result = -1;
    private final int THREADS = 8;

    public static void main(String[] args) {
        System.out.println("Globant 20 years. Riddle 2.");
//        System.out.println(Long.MAX_VALUE);
        Riddle2 riddle = new Riddle2();
        riddle.start();
    }

    public void start() {
        long start = 1011235955046179L;
        long step = 10_000_000_000L;
//        ArrayList<RiddleThread> threads = new ArrayList<>();
//
//        for (int i = 0; i < THREADS; i++) {
//            RiddleThread riddleThread = new RiddleThread(start + (step * i), step, i);
//            threads.add(riddleThread);
//            new Thread(riddleThread).start();
//        }
        long l1 = 1011235955056179779L;
        long digits = String.valueOf(l1).length();
        long lastDigit = (l1 - (10L * (l1 / 10L)));
        long l2 = (l1 / 10L) + lastDigit * RiddleThread.powerOfTen(digits - 1L);
        long l3 = l2 - l1*9;
        System.out.println(l3);
        System.out.println(String.valueOf(l3).length());
    }

    class RiddleThread implements Runnable {

        private final long start;
        private final long step;
        private long actualStep;
        private final int id;

        public RiddleThread(long start, long step, int id) {
            this.id = id;
            this.start = start;
            this.step = step;
            this.actualStep = 0;
        }

        @Override
        public void run() {
            long digits;
            long firstDigit;
            long lastDigit;
            long value;

            while (actualStep < 10000) {
                long num = start + (step * THREADS * actualStep);
                System.out.println("Thread " + id + ": " + num);

                for ( ;num < start + (step * THREADS * actualStep) + step; num += 10) {
                    digits = String.valueOf(num).length();
                    firstDigit = num / powerOfTen(digits - 1L);

                    if (firstDigit != 1) {
                        num += (10L - firstDigit) * powerOfTen(digits - 1L) - 10L;
                        continue;
                    }

                    lastDigit = (num - (10L * (num / 10L)));
                    value = (num / 10L) + lastDigit * powerOfTen(digits - 1L);

                    if (value == num * 9L) {
                        System.out.println("Found! -> Thread " + id + " " + result + ": " + num + " - " + value);
                        System.out.println("Check: " + num * 9L + " - " + value / 9L);
                        if (result == -1 || num < result)
                            result = num;
                    }
                }

                actualStep++;
            }
        }

        private static long powerOfTen(long digits) {
            long power = 1;
            for (int i = 0; i < digits; i++) {
                power *= 10;
            }

            return power;
        }
    }
}

