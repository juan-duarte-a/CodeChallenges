package aux;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class InverseBD {

    private static final int scale = 7;

    public static void main(String[] argv) {
        double[][] a = {
                {   0, 1f/2,    0,    0,    0, 1f/2}, // s0
                {4f/9,    0,    0, 3f/9, 2f/9,    0}, // s1
                {   0,    0,    1,    0,    0,    0}, // s2
                {   0,    0,    0,    1,    0,    0}, // s3
                {   0,    0,    0,    0,    1,    0}, // s4
                {   0,    0,    0,    0,    0,    1}  // s5
        };

        int n = a.length;
        BigDecimal[][] bda = new BigDecimal[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                bda[i][j] = BigDecimal.valueOf(a[i][j]);
            }
        }

        BigDecimal[][] d = invert(bda);

        System.out.println("The inverse is: ");
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.print(d[i][j] + "  ");
            }
            System.out.println();
        }
    }

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

        // Transform the matrix into an upper triangle
        gaussian(a, index);

        // Update the matrix b[i][j] with the ratios stored
        for (int i = 0; i < n - 1; i++)
            for (int j = i + 1; j < n; j++)
                for (int k = 0; k < n; k++)
                    b[index[j]][k] = b[index[j]][k].subtract(a[index[j]][i].multiply(b[index[i]][k]));

        // Perform backward substitutions
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

// Method to carry out the partial-pivoting Gaussian
// elimination.  Here index[] stores pivoting order.

    public static void gaussian(BigDecimal[][] a, int[] index) {
        int n = index.length;
        BigDecimal[] c = new BigDecimal[n];

        // Initialize the index
        for (int i = 0; i < n; i++)
            index[i] = i;

        // Find the rescaling factors, one from each row
        for (int i = 0; i < n; i++) {
            BigDecimal c1 = BigDecimal.ZERO;
            for (int j = 0; j < n; j++) {
                BigDecimal c0 = a[i][j].abs();
                if (c0.compareTo(c1) > 0) c1 = c0;
            }
            c[i] = c1;
        }

        // Search the pivoting element from each column
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

            // Interchange rows according to the pivoting order
            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i = j + 1; i < n; i++) {
                BigDecimal pj = a[index[i]][j].divide(a[index[j]][j], scale, RoundingMode.HALF_EVEN);

                // Record pivoting ratios below the diagonal
                a[index[i]][j] = pj;

                // Modify other elements accordingly
                for (int l = j + 1; l < n; l++)
                    a[index[i]][l] = a[index[i]][l].subtract(pj.multiply(a[index[j]][l]));
            }
        }
    }
}
