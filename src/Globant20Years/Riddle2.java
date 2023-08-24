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

    private long result = -1L;
    private final long THREADS = 1L;
    private final File resultsFile = new File("/home/juan/IdeaProjects/CodeChallenges/src/" +
            "Globant20Years/results.txt");

    public static void main(String[] args) {
        System.out.printf("Globant 20 years%n Riddle 2 solution.%n");
        System.out.println(Long.MAX_VALUE);
        Riddle2 riddle = new Riddle2();
        riddle.start();
    }

    public void start() {
        long start = 10L;
        long threadStep = 1_000L;

        for (long i = 0L; i < THREADS; i++) {
            RiddleThread riddleThread = new RiddleThread(start + (threadStep * i), threadStep, i);
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

    public synchronized void saveResult(String result) {
        try (FileWriter fileWriter = new FileWriter(resultsFile, true);
             BufferedWriter bufferedWriter = new BufferedWriter(fileWriter)) {
            bufferedWriter.write(String.format(result + "%n"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    class RiddleThread implements Runnable {

        private final long start;
        private final long threadStep;
        private long actualStep;
        private final long id;

        public RiddleThread(long start, long threadStep, long id) {
            this.id = id;
            this.start = start;
            this.threadStep = threadStep;
            this.actualStep = 0L;
        }

        @Override
        public void run() {
            long digits;
            long digits_ref;
            long firstDigit;
            long lastDigit;
            long value;
            long factor = 4L;
            long difference;
            long power = -1L;
            long step = 1L;

            while (true) {
                long num = start + (threadStep * THREADS * actualStep);
                digits_ref = String.valueOf(num).length();

                if (result != -1L && num > result) {
                    System.out.println("Thread end -> No need to search more with this thread.");
                    return;
                } else {
                    System.out.println("Thread " + id + ": " + num);
                }

                for (; num < start + (threadStep * THREADS * actualStep) + threadStep; num += step) {
                    digits = String.valueOf(num).length();

                    if (digits != digits_ref || power == -1L) {
                        power = powerOfTen(digits - 1L);
                        digits_ref = digits;
                    }

//                    firstDigit = num / power;
//
//                    if (firstDigit != 1L) {
//                        num += ((10L - firstDigit) * powerOfTen(digits - 1L)) - step;
//                        continue;
//                    }

                    lastDigit = (num - (10L * (num/10L)));
                    value = (num/10L) + lastDigit * power;

                    difference = Math.abs((factor * num) - value);

                    if (difference < 10L) {
                        String found = "Found! -> Thread " + id + ": " + num + " - " + value + ". Diff: " + difference;
                        saveResult(found);
                        System.out.println(found);
                        System.out.println("Check: " + num * factor + " - "
                                + (value/factor) + " Diff: " + difference);

                        if (difference == 0L) {
                            if (num == -1L || num < result)
                                result = num;
                            return;
                        }
                    }
                }

                actualStep++;
            }
        }

        private long powerOfTen(long digits) {
            long power = 1L;
            for (int i = 0; i < digits; i++) {
                power *= 10L;
            }
            return power;
        }

    }

}

