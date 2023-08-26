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

Write a function solution(m) that takes an array of array of nonnegative ints representing how many times that state has
gone to the next state and return an array of ints for each terminal state giving the exact probabilities of each
terminal state, represented as the numerator for each state, then the denominator for all of them at the end and in
simplest form.

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

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;

public class Solution {

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
        BigDecimal[][] bdm = new BigDecimal[d][d];

        for (int i = 0; i < d; i++) {
            int rowSum = 0;

            for (int j = 0; j < d; j++)
                rowSum += m[i][j];

            for (int j = 0; j < d; j++) {
                if (rowSum == 0) {
                    if (i == j)
                        bdm[i][j] = BigDecimal.ONE;
                    else
                        bdm[i][j] = BigDecimal.ZERO;
                } else
                    bdm[i][j] = BigDecimal.valueOf(m[i][j])
                            .divide(BigDecimal.valueOf(rowSum), 11, RoundingMode.HALF_EVEN);
            }

            if (rowSum != 0)
                nt++;

            ntm[i] = rowSum != 0;
        }

        BigDecimal[][] q = new BigDecimal[nt][nt];
        BigDecimal[][] r = new BigDecimal[nt][d - nt];

        int x = 0, y, ty;
        for (int i = 0; i < d; i++) {
            if (ntm[i]) {
                y = ty = 0;
                for (int j = 0; j < d; j++) {
                    if (ntm[j]) {
                        q[x][y] = bdm[i][j];
                        y++;
                    } else {
                        r[x][ty] = bdm[i][j];
                        ty++;
                    }
                }
                x++;
            }
        }

        BigDecimal[][] iSubQ = new BigDecimal[nt][nt];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                if (i == j)
                    iSubQ[i][j] = BigDecimal.ONE.subtract(q[i][j]);
                else
                    iSubQ[i][j] = BigDecimal.ZERO.subtract(q[i][j]);
            }
        }

        BigDecimal[][] n = InverseBD.invert(iSubQ);

        System.out.println("The inverse is: ");
        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < nt; j++) {
                System.out.print(n[i][j] + "  ");
            }
            System.out.println();
        }

        int t = d - nt;
        BigDecimal[][] b = new BigDecimal[nt][t];

        for (int i = 0; i < nt; i++) {
            for (int j = 0; j < t; j++) {
                b[i][j] = BigDecimal.ZERO;
                for (int k = 0; k < nt; k++) {
                    b[i][j] = b[i][j].add(n[i][k].multiply(r[k][j]));
                }
            }
        }

        int[] result = new int[t];

        for (int i = 0; i < t; i++) {
            result[i] = b[0][i].intValue();
        }

        return result;
    }

}


class InverseBD {

    private static final int scale = 9;

    public static BigDecimal[][] invert(BigDecimal[][] a) {
        int n = a.length;
        BigDecimal[][] x = new BigDecimal[n][n];
        BigDecimal[][] b = new BigDecimal[n][n];
        int[] index = new int[n];

        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++) {
                if (i == j)
                    b[i][j] = BigDecimal.ONE;
                else
                    b[i][j] = BigDecimal.ZERO;
            }

        gaussian(a, index);

        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                for (int k = 0; k < n; k++)
                    b[index[j]][k] = b[index[j]][k].subtract(a[index[j]][i].multiply(b[index[i]][k]));

        for (int i = 0; i < n; i++) {
            x[n - 1][i] = b[index[n - 1]][i].divide(a[index[n - 1]][n - 1],scale,  RoundingMode.HALF_EVEN);
            for (int j = n - 2; j >= 0; j--) {
                x[j][i] = b[index[j]][i];

                for (int k = j + 1; k < n; k++) {
                    x[j][i] = x[j][i].subtract(a[index[j]][k].multiply(x[k][i]));
                }
                x[j][i] = x[j][i].divide(a[index[j]][j], scale, RoundingMode.HALF_EVEN);
            }
        }
        return x;
    }

    public static void gaussian(BigDecimal[][] a, int[] index) {
        int n = index.length;
        BigDecimal[] c = new BigDecimal[n];

        for (int i = 0; i < n; i++)
            index[i] = i;

        for (int i = 0; i < n; i++) {
            BigDecimal c1 = BigDecimal.ZERO;
            for (int j = 0; j < n; j++) {
                BigDecimal c0 = a[i][j].abs();

                if (c0.compareTo(c1) > 0) c1 = c0;
            }
            c[i] = c1;
        }

        int k = 0;
        for (int j = 0; j < n - 1; j++) {
            BigDecimal pi1 = BigDecimal.ZERO;

            for (int i = j; i < n; i++) {
                BigDecimal pi0 = a[index[i]][j].abs();
                pi0 = pi0.divide(c[index[i]], scale, RoundingMode.HALF_EVEN);

                if (pi0.compareTo(pi1) > 0) {
                    pi1 = pi0;
                    k = i;
                }
            }

            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;

            for (int i = j + 1; i < n; i++) {
                BigDecimal pj = a[index[i]][j].divide(a[index[j]][j], scale, RoundingMode.HALF_EVEN);

                a[index[i]][j] = pj;

                for (int l = j + 1; l < n; l++)
                    a[index[i]][l] = a[index[i]][l].subtract(pj.multiply(a[index[j]][l]));
            }
        }
    }
}
