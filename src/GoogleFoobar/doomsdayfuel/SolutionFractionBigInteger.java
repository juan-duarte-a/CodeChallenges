package GoogleFoobar.doomsdayfuel;

/*
Making fuel for the LAMBCHOP's reactor core is a tricky process because of the exotic matter involved. It starts as raw
ore, then during processing, begins randomly changing between
forms, eventually reaching a stable form. There may be multiple stable forms that a sample could ultimately reach, not
all of which are useful as fuel.

Commander Lambda has tasked you to help the scientists increase fuel creation efficiency by predicting the end state of
a given ore sample. You have carefully studied the different structures that the ore can take and which transitions it
undergoes. It appears that, while random, the probability of each structure transforming is fixed. That is, each time
the ore is in 1 state, it has the same probabilities of entering the next state (which might be the same state).

You have recorded the observed transitions in a matrix. The others in the lab have hypothesized more exotic forms that
the ore can become, but you haven't seen all of them.

Write a function solution(m) that takes an array of array of non-negative ints representing how many times that state has
gone to the next state and return an array of ints for each terminal state giving the exact probabilities of each
terminal state, represented as the numerator for each state, then the denominator for all of them at the end and in
the simplest form.

The matrix is at most 10 by 10. It is guaranteed that no matter which state the ore is in, there is a path from that
state to a terminal state. That is, the processing will always eventually end in a stable state.

The ore starts in state 0. The denominator will fit within a signed 32-bit integer during the calculation, as long as
the fraction is simplified regularly.

For example, consider the matrix m:

[
[0,1,0,0,0,1],  # s0, the initial state, goes to s1 and s5 with equal probability
[4,0,0,3,2,0],  # s1 can become s0, s3, or s4, but with different probabilities
[0,0,0,0,0,0],  # s2 is terminal, and unreachable (never observed in practice)
[0,0,0,0,0,0],  # s3 is terminal
[0,0,0,0,0,0],  # s4 is terminal
[0,0,0,0,0,0],  # s5 is terminal
]

So, we can consider different paths to terminal states, such as:
s0 -> s1 -> s3
s0 -> s1 -> s0 -> s1 -> s0 -> s1 -> s4
s0 -> s1 -> s0 -> s5

Tracing the probabilities of each, we find that

s2 has probability 0
s3 has probability 3/14
s4 has probability 1/7
s5 has probability 9/14

So, putting that together, and making a common denominator, gives an answer in the form of
[s2.numerator, s3.numerator, s4.numerator, s5.numerator, denominator] which is
[0, 3, 2, 9, 14].
*/

import java.util.Arrays;

public class SolutionFractionBigInteger {

    public static void main(String[] args) {
        int[][] a = {{0, 1, 0, 0, 0, 1},   // s0
                     {4, 0, 0, 3, 2, 0},   // s1
                     {0, 0, 0, 0, 0, 0},   // s2
                     {0, 0, 0, 0, 0, 0},   // s3
                     {0, 0, 0, 0, 0, 0},   // s4
                     {0, 0, 0, 0, 0, 0}};  // s5

        int[][] b = {{0, 2, 1, 0, 0},
                     {0, 0, 0, 3, 4},
                     {0, 0, 0, 0, 0},
                     {0, 0, 0, 0, 0},
                     {0, 0, 0, 0, 0}};

        System.out.println(Arrays.toString(solution(a)));
        System.out.println();
        System.out.println(Arrays.toString(solution(b)));
    }

    public static int[] solution(int[][] m) {
        int d = m.length, nt = 0;
        boolean[] ntm = new boolean[d];
        FractionBigInteger[][] fm = new FractionBigInteger[d][d];

        for (int i = 0; i < d; i++) {
            long rowSum = 0;

            for (int j = 0; j < d; j++)
                rowSum += m[i][j];

            for (int j = 0; j < d; j++) {
                if (rowSum == 0)
                    fm[i][j] = FractionBigInteger.ONE;
                else
                    fm[i][j] = FractionBigInteger.valueOf(m[i][j])
                            .divide(FractionBigInteger.valueOf(rowSum));
            }

            if (rowSum != 0)
                nt++;

            ntm[i] = rowSum != 0;
        }

        FractionBigInteger[][] q = new FractionBigInteger[nt][nt];
        FractionBigInteger[][] r = new FractionBigInteger[nt][d - nt];

        int x = 0, y, ty;
        for (int i = 0; i < d; i++) {
            if (ntm[i]) {
                y = ty = 0;
                for (int j = 0; j < d; j++) {
                    if (ntm[j]) {
                        q[x][y] = fm[i][j];
                        y++;
                    } else {
                        r[x][ty] = fm[i][j];
                        ty++;
                    }
                }
                x++;
            }
        }

        FractionBigInteger[][] iSubQ = new FractionBigInteger[nt][nt];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                if (i == j)
                    iSubQ[i][j] = FractionBigInteger.ONE.subtract(q[i][j]);
                else
                    iSubQ[i][j] = FractionBigInteger.ZERO.subtract(q[i][j]);
            }
        }

        FractionBigInteger[][] n = invert(iSubQ);

        System.out.println("The inverse is: ");
        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                System.out.print(n[i][j] + "  ");
            }
            System.out.println();
        }

        int t = d - nt;
        FractionBigInteger[][] b = new FractionBigInteger[nt][t];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < t; j++) {
                b[i][j] = FractionBigInteger.ZERO;
                for (int k = 0; k < nt; k++) {
                    b[i][j] = b[i][j].add(n[i][k].multiply(r[k][j]));
                }
            }
        }

        long[] resultLong = new long[t + 1];
        long[] denominators = new long[t];

        for (int i = 0; i < t; i++) {
            resultLong[i] = b[0][i].getNumerator();
            denominators[i] = b[0][i].getDenominator();
        }

        long lcm = lcm(denominators);

        for (int i = 0; i < resultLong.length - 1; i++) {
            resultLong[i] *= (long) ((double) lcm / b[0][i].getDenominator());
            System.out.print(b[0][i].doubleValue() + "  ");
        }

        resultLong[t] = lcm;

        int[] result = new int[t + 1];

        for (int i = 0; i < result.length; i++) {
            result[i] = (int) resultLong[i];
        }

        return result;
    }

    private static boolean isPrime(long n) {
        if (n < 2L) return false;

        for (long i = 2; i <= Math.sqrt(n) + 1; i++) {
            if (i != n && n % i == 0)
                return false;
        }
        return true;
    }

    private static long lcm(long[] n) {
        long lcm = 1L;

        while (true) {
            boolean check = true;
            for (long l : n) {
                if (l != 1) {
                    check = false;
                    break;
                }
            }
            if (check) break;

            long p = 2L;
            while (true) {
                if (isPrime(p)) {
                    for (int i = 0; i < n.length; i++) {
                        if (n[i] != 1 && n[i] % p == 0) {
                            n[i] /= p;
                            check = true;
                        }
                    }

                    if (check) {
                        lcm *= p;
                        break;
                    }
                }
                p++;
            }
        }

        return lcm;
    }

    private static FractionBigInteger[][] invert(FractionBigInteger[][] a) {
        int n = a.length;
        FractionBigInteger[][] x = new FractionBigInteger[n][n];
        FractionBigInteger[][] b = new FractionBigInteger[n][n];
        int[] index = new int[n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i == j)
                    b[i][j] = FractionBigInteger.ONE;
                else
                    b[i][j] = FractionBigInteger.ZERO;
            }

        gaussian(a, index);

        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                for (int k = 0; k < n; k++)
                    b[index[j]][k] = b[index[j]][k].subtract(a[index[j]][i].multiply(b[index[i]][k]));

        for (int i = 0; i < n; i++) {
            x[n - 1][i] = b[index[n - 1]][i].divide(a[index[n - 1]][n - 1]);
            for (int j = n - 2; j >= 0; j--) {
                x[j][i] = b[index[j]][i];

                for (int k = j + 1; k < n; k++) {
                    x[j][i] = x[j][i].subtract(a[index[j]][k].multiply(x[k][i]));
                }
                x[j][i] = x[j][i].divide(a[index[j]][j]);
            }
        }
        return x;
    }

    private static void gaussian(FractionBigInteger[][] a, int[] index) {
        int n = index.length;
        FractionBigInteger[] c = new FractionBigInteger[n];

        for (int i = 0; i < n; i++)
            index[i] = i;

        for (int i = 0; i < n; i++) {
            FractionBigInteger c1 = FractionBigInteger.ZERO;
            for (int j = 0; j < n; j++) {
                FractionBigInteger c0 = a[i][j].abs();

                if (c0.compareTo(c1) > 0) c1 = c0;
            }
            c[i] = c1;
        }

        int k = 0;
        for (int j = 0; j < n - 1; j++) {
            FractionBigInteger pi1 = FractionBigInteger.ZERO;

            for (int i = j; i < n; i++) {
                FractionBigInteger pi0 = a[index[i]][j].abs();
                pi0 = pi0.divide(c[index[i]]);

                if (pi0.compareTo(pi1) > 0) {
                    pi1 = pi0;
                    k = i;
                }
            }

            int tmp = index[j];
            index[j] = index[k];
            index[k] = tmp;

            for (int i = j + 1; i < n; i++) {
                FractionBigInteger pj = a[index[i]][j].divide(a[index[j]][j]);

                a[index[i]][j] = pj;

                for (int l = j + 1; l < n; l++)
                    a[index[i]][l] = a[index[i]][l].subtract(pj.multiply(a[index[j]][l]));
            }
        }
    }

}

class FractionBigInteger {

    private final long numerator;
    private final long denominator;
    public final static FractionBigInteger ZERO = new FractionBigInteger(0L);
    public final static FractionBigInteger ONE = new FractionBigInteger(1L);

    public FractionBigInteger(long numerator) {
        this.numerator = numerator;
        this.denominator = 1L;
    }

    public FractionBigInteger(long numerator, long denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static FractionBigInteger valueOf(long n) {
        return new FractionBigInteger(n);
    }

    public long getNumerator() {
        return numerator;
    }

    public long getDenominator() {
        return denominator;
    }

    public double doubleValue() {
        return (double) numerator / denominator;
    }

    public FractionBigInteger abs() {
        return new FractionBigInteger(numerator < 0L ? -numerator : numerator, denominator);
    }

    public FractionBigInteger multiply(FractionBigInteger f) {
        return getReducedFraction(numerator * f.numerator, denominator * f.denominator);
    }

    public FractionBigInteger divide(FractionBigInteger f) {
        return multiply(new FractionBigInteger(f.denominator, f.numerator));
    }

    private static FractionBigInteger getReducedFraction(long numerator, long denominator) {
        if (numerator == 0L)
            return ZERO;

        boolean negative = numerator < 0L;
        numerator = Math.abs(numerator);

        long gcd = gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;

        return new FractionBigInteger(negative ? -numerator : numerator, denominator);
    }

    public FractionBigInteger add(FractionBigInteger f) {
        if (denominator == f.denominator)
            return getReducedFraction(numerator + f.numerator, denominator);
        return getReducedFraction(numerator * f.denominator + f.numerator * denominator,
                denominator * f.denominator);
    }

    public FractionBigInteger subtract(FractionBigInteger f) {
        return add(new FractionBigInteger(-f.numerator, f.denominator));
    }

    private static long gcd(long a, long b) {
        if (a == 0L)
            return b;
        else if (b == 0L)
            return a;

        final long aTwos = Long.numberOfTrailingZeros(a);
        a >>= aTwos;
        final long bTwos = Long.numberOfTrailingZeros(b);
        b >>= bTwos;
        final long shift = Math.min(aTwos, bTwos);

        while (a != b) {
            final long delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);
            a >>= Long.numberOfTrailingZeros(a);
        }

        return a << shift;
    }

    public long compareTo(FractionBigInteger f) {
        return numerator * f.denominator - f.numerator * denominator;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

}