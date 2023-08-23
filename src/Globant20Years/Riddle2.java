package Globant20Years;

import java.io.*;

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

    private final long THREADS = 8L;
    private final File resultsFile = new File("/home/juan/IdeaProjects/CodeChallenges/src/" +
            "Globant20Years/results/results.txt");

    public static void main(String[] args) {
        System.out.println("Globant 20 years. Riddle 2 solution.");
        System.out.println(Long.MAX_VALUE);
        Riddle2 riddle = new Riddle2();
        riddle.start();
    }

    public void start() {
        long start = 19L;
        long step = 10_000_000_000L;

        for (long i = 0L; i < THREADS; i++) {
            RiddleThread riddleThread = new RiddleThread(start + (step * i), step, i);
            new Thread(riddleThread).start();
        }
//        long l1 = 1011235955056179779L;
//        long digits = String.valueOf(l1).length();
//        long lastDigit = (l1 - (10L * (l1 / 10L)));
//        long l2 = (l1 / 10L) + lastDigit * RiddleThread.powerOfTen(digits - 1L);
//        long l3 = l2 - l1*9;
//        System.out.println(l3);
//        System.out.println(String.valueOf(l3).length());
    }

    public void saveResult(String result) {
        try (FileWriter fileWriter = new FileWriter(resultsFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(String.format(result + "%n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class RiddleThread implements Runnable {

        private final long start;
        private final long step;
        private long actualStep;
        private final long id;

        public RiddleThread(long start, long step, long id) {
            this.id = id;
            this.start = start;
            this.step = step;
            this.actualStep = 0L;
        }

        @Override
        public void run() {
            long digits;
            long firstDigit;
            long lastDigit;
            long value;
            long factor = 9L;
            long difference;

            while (true) {
                long num = start + (step * THREADS * actualStep);
                System.out.println("Thread " + id + ": " + num);

                for ( ;num < start + (step * THREADS * actualStep) + step; num += 10L) {
                    digits = String.valueOf(num).length();
                    firstDigit = num / powerOfTen(digits - 1L);

                    if (firstDigit != 1) {
                        num += ((10L - firstDigit) * powerOfTen(digits - 1L)) - 10L;
                        continue;
                    }

                    lastDigit = (num - (10L * (num/10L)));
                    value = (num/10L) + lastDigit * powerOfTen(digits - 1L);

                    difference = Math.abs((factor * num) - value);

                    if (difference < 10L) {
                        String found = "Found! -> Thread " + id + ": " + num + " - " + value + ". Diff: " + difference;
                        saveResult(found);
                        System.out.println(found);
                        System.out.println("Check: " + num * factor + " - "
                                + (value/factor) + " Diff: " + difference);

                        if (difference == 0)
                            return;
                    }
                }

                actualStep++;
            }
        }

        private static long powerOfTen(long digits) {
            long power = 1L;
            for (int i = 0; i < digits; i++) {
                power *= 10L;
            }
            return power;
        }

    }

}

