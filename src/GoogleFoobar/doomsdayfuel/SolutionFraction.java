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

public class SolutionFraction {

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
        Fraction[][] fm = new Fraction[d][d];

        for (int i = 0; i < d; i++) {
            int rowSum = 0;

            for (int j = 0; j < d; j++)
                rowSum += m[i][j];

            for (int j = 0; j < d; j++) {
                if (rowSum == 0)
                    fm[i][j] = Fraction.ONE;
                else
                    fm[i][j] = Fraction.valueOf(m[i][j])
                            .divide(Fraction.valueOf(rowSum));
            }

            if (rowSum != 0)
                nt++;

            ntm[i] = rowSum != 0;
        }

        Fraction[][] q = new Fraction[nt][nt];
        Fraction[][] r = new Fraction[nt][d - nt];

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

        Fraction[][] iSubQ = new Fraction[nt][nt];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                if (i == j)
                    iSubQ[i][j] = Fraction.ONE.subtract(q[i][j]);
                else
                    iSubQ[i][j] = Fraction.ZERO.subtract(q[i][j]);
            }
        }

        Fraction[][] n = invert(iSubQ);

        System.out.println("The inverse is: ");
        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                System.out.print(n[i][j] + "  ");
            }
            System.out.println();
        }

        int t = d - nt;
        Fraction[][] b = new Fraction[nt][t];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < t; j++) {
                b[i][j] = Fraction.ZERO;
                for (int k = 0; k < nt; k++) {
                    b[i][j] = b[i][j].add(n[i][k].multiply(r[k][j]));
                }
            }
        }

        int[] result = new int[t + 1];

        int maxDenominator = 0;
        for (int i = 0; i < t; i++) {
            result[i] = b[0][i].getNumerator();
            if (b[0][i].getDenominator() > maxDenominator)
                maxDenominator = b[0][i].getDenominator();
            System.out.print(b[0][i].doubleValue() + "  ");
        }

        for (int i = 0; i < result.length - 1; i++) {
            result[i] *= (int) ((double) maxDenominator / b[0][i].getDenominator());
        }

        result[t] = maxDenominator;

        return result;
    }

    private static Fraction[][] invert(Fraction[][] a) {
        int n = a.length;
        Fraction[][] x = new Fraction[n][n];
        Fraction[][] b = new Fraction[n][n];
        int[] index = new int[n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i == j)
                    b[i][j] = Fraction.ONE;
                else
                    b[i][j] = Fraction.ZERO;
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

    private static void gaussian(Fraction[][] a, int[] index) {
        int n = index.length;
        Fraction[] c = new Fraction[n];

        for (int i = 0; i < n; i++)
            index[i] = i;

        for (int i = 0; i < n; i++) {
            Fraction c1 = Fraction.ZERO;
            for (int j = 0; j < n; j++) {
                Fraction c0 = a[i][j].abs();

                if (c0.compareTo(c1) > 0) c1 = c0;
            }
            c[i] = c1;
        }

        int k = 0;
        for (int j = 0; j < n - 1; j++) {
            Fraction pi1 = Fraction.ZERO;

            for (int i = j; i < n; i++) {
                Fraction pi0 = a[index[i]][j].abs();
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
                Fraction pj = a[index[i]][j].divide(a[index[j]][j]);

                a[index[i]][j] = pj;

                for (int l = j + 1; l < n; l++)
                    a[index[i]][l] = a[index[i]][l].subtract(pj.multiply(a[index[j]][l]));
            }
        }
    }

}

class Fraction {

    private final int numerator;
    private final int denominator;
    public final static Fraction ZERO = new Fraction(0);
    public final static Fraction ONE = new Fraction(1);

    public Fraction(int numerator) {
        this.numerator = numerator;
        this.denominator = 1;
    }

    public Fraction(int numerator, int denominator) {
        this.numerator = numerator;
        this.denominator = denominator;
    }

    public static Fraction valueOf(int n) {
        return new Fraction(n);
    }

    public int getNumerator() {
        return numerator;
    }

    public int getDenominator() {
        return denominator;
    }

    public double doubleValue() {
        return (double) numerator / denominator;
    }

    public Fraction abs() {
        return new Fraction(numerator < 0 ? -numerator : numerator, denominator);
    }

    public Fraction multiply(Fraction f) {
        return getReducedFraction(numerator * f.numerator, denominator * f.denominator);
    }

    public Fraction divide(Fraction f) {
        return multiply(new Fraction(f.denominator, f.numerator));
    }

    private static Fraction getReducedFraction(int numerator, int denominator) {
        if (numerator==0)
            return ZERO;

        boolean negative = numerator < 0;
        numerator = Math.abs(numerator);

        int gcd = gcd(numerator, denominator);
        numerator /= gcd;
        denominator /= gcd;

        return new Fraction(negative ? -numerator : numerator, denominator);
    }

    public Fraction add(Fraction f) {
        if (denominator == f.denominator)
            return getReducedFraction(numerator + f.numerator, denominator);
        return getReducedFraction(numerator * f.denominator + f.numerator * denominator,
                denominator * f.denominator);
    }

    public Fraction subtract(Fraction f) {
        return add(new Fraction(-f.numerator, f.denominator));
    }

    private static int gcd(int a, int b) {
        if (a == 0)
            return b;
        else if (b == 0)
            return a;

        final int aTwos = Integer.numberOfTrailingZeros(a);
        a >>= aTwos;
        final int bTwos = Integer.numberOfTrailingZeros(b);
        b >>= bTwos;
        final int shift = Math.min(aTwos, bTwos);

        while (a != b) {
            final int delta = a - b;
            b = Math.min(a, b);
            a = Math.abs(delta);
            a >>= Integer.numberOfTrailingZeros(a);
        }

        return a << shift;
    }

    public int compareTo(Fraction f) {
        return numerator * f.denominator - f.numerator * denominator;
    }

    @Override
    public String toString() {
        return numerator + "/" + denominator;
    }

}