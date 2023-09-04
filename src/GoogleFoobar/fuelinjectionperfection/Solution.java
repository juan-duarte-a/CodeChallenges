package GoogleFoobar.fuelinjectionperfection;

/*
Fuel Injection Perfection=========================

Commander Lambda has asked for your help to refine the automatic quantum antimatter fuel injection system for the
LAMBCHOP doomsday device. It's a great chance for you to get a closer look at the LAMBCHOP -- and maybe sneak in a bit
of sabotage while you're at it -- so you took the job gladly. Quantum antimatter fuel comes in small pellets, which is
convenient since the many moving parts of the LAMBCHOP each need to be fed fuel one pellet at a time. However, minions
dump pellets in bulk into the fuel intake. You need to figure out the most efficient way to sort and shift the pellets
down to a single pellet at a time. The fuel control mechanisms have three operations:

1) Add one fuel pellet
2) Remove one fuel pellet
3) Divide the entire group of fuel pellets by 2 (due to the destructive energy released when a quantum
antimatter pellet is cut in half, the safety controls will only allow this to happen if there is an even number of
pellets)

Write a function called solution(n) which takes a positive integer as a string and returns the minimum number of
operations needed to transform the number of pellets to 1. The fuel intake control panel can only display a number up
to 309 digits long, so there won't ever be more pellets than you can express in that many digits.For example:

solution(4) returns 2: 4 -> 2 -> 1

solution(15) returns 5: 15 -> 16 -> 8 -> 4 -> 2 -> 1

 */

import java.math.BigInteger;
import java.util.HashMap;
import java.util.Objects;

public class Solution {

    private static boolean print;
    private static BigInteger pelletControl;
    private static final HashMap<BigInteger, Integer> savedCalculations = new HashMap<>();

    public static void main(String[] args) {
        int operations;

//        String number = "726754792013748615740123765074386572300129265748935619065290151673486753" +
//                "87912073645701283974651029387456748392016012376538712605748390128374567483901265" +
//                "93865748365910723465789340127654739012120938746530913872409237383838383383011001" +
//                "20929065567564848474748567481065473810201092238947567348929011023740569102783";
//
//        operations = solution(number);
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");

        operations = solutionRaw("351040149035");
        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");

//        operations = solution("15");
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");
//        operations = solution("77");
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");
//        operations = solution("55");
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");
//        operations = solution("4");
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");
//        operations = solution("7");
//        System.out.printf("%nMinimum number of operations: " + operations + "%n%n");

//        for (int i = 0; i < 55; i++) {
//            System.out.println(solution(String.valueOf(i)));
//            System.out.println("----------------------------------------------------");
//        }
    }

    public static int solution(String x) {
        BigInteger number = new BigInteger(x);
        return minimumNumberOfOperations(number);

//        pelletControl = null;
//        return minimumNumberOfOperationsStats(number);
    }

    public static int solutionRaw(String x) {
        BigInteger number = new BigInteger(x);
        return minimumNumberOfOperationsRaw(number);

//        pelletControl = null;
//        return minimumNumberOfOperationsStats(number);
    }

    private static int minimumNumberOfOperations(BigInteger pellets) {
        System.out.print(pellets + (pellets.equals(BigInteger.ONE) ? "" : " -> "));

        if (savedCalculations.containsKey(pellets)) {
            System.out.println("already calculated and saved.");
            return savedCalculations.get(pellets);
        }

        if (pellets.equals(BigInteger.ONE) || pellets.compareTo(BigInteger.ZERO) <= 0) {
            return 0;
        } else if (!pellets.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            System.out.print("\nAdd: ");
            int operationsAdd = minimumNumberOfOperations(pellets.add(BigInteger.ONE));
            System.out.print("\nSubtract: ");
            int operationsRemove = minimumNumberOfOperations(pellets.subtract(BigInteger.ONE));

            int minimum = 1 + Math.min(operationsAdd, operationsRemove);
            savedCalculations.put(pellets, minimum);

            return minimum;
        } else {
            int result = 1 + minimumNumberOfOperations(pellets.divide(BigInteger.valueOf(2)));
            savedCalculations.put(pellets, result);

            return result;
        }
    }

    private static int minimumNumberOfOperationsRaw(BigInteger pellets) {
        System.out.print(pellets + (pellets.equals(BigInteger.ONE) ? "" : " -> "));
        if (pellets.equals(BigInteger.ONE) || pellets.compareTo(BigInteger.ZERO) <= 0) {
            System.out.println();
            return 0;
        } else if (!pellets.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            int operationsAdd = minimumNumberOfOperationsRaw(pellets.add(BigInteger.ONE));
            int operationsRemove = minimumNumberOfOperationsRaw(pellets.subtract(BigInteger.ONE));
            return 1 + Math.min(operationsAdd, operationsRemove);
        } else {
            return 1 + minimumNumberOfOperationsRaw(pellets.divide(BigInteger.valueOf(2)));
        }
    }

    private static int minimumNumberOfOperationsStats(BigInteger pellets) {
        if (pellets.equals(BigInteger.ONE) || pellets.compareTo(BigInteger.ZERO) <= 0) {
            return 0;
        } else if (!pellets.remainder(BigInteger.valueOf(2)).equals(BigInteger.ZERO)) {
            if (pelletControl == null) {
                System.out.println("Pellets: " + pellets);
                pelletControl = pellets;
            }

            int operationsAdd = minimumNumberOfOperationsStats(pellets.add(BigInteger.ONE));
            int operationsRemove = minimumNumberOfOperationsStats(pellets.subtract(BigInteger.ONE));

            if (Objects.equals(pelletControl, pellets)) {
                System.out.println("**** ADD:      " + operationsAdd);
                System.out.println("**** SUBTRACT: " + operationsRemove);
            }

            return 1 + Math.min(operationsAdd, operationsRemove);
        } else {
            return 1 + minimumNumberOfOperationsStats(pellets.divide(BigInteger.valueOf(2)));
        }
    }

}
