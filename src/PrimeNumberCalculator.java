import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrimeNumberCalculator {
    static int maxNumbers = 10000;
    @org.jetbrains.annotations.NotNull
    public static List<Integer> getFirstXPrimes(int x) {
        List<Integer> primes = new ArrayList<>();
        boolean[] isComposite = new boolean[maxNumbers]; // Assuming integer limit

        for (int i = 2; primes.size() < x; i++) {
            if (!isComposite[i]) {
                primes.add(i);
                for (int j = i * i; j > 0 && j < isComposite.length; j += i) {
                    isComposite[j] = true;
                }
            }
        }

        return primes;
    }

    public static int[] sieveOfEratosthenes(int n) {
        boolean[] primes = new boolean[n + 1];
        Arrays.fill(primes, true);
        primes[0] = false;
        primes[1] = false;
        for (int i = 2; i * i <= n; i++) {
            if (primes[i]) {
                for (int j = i * i; j <= n; j += i) {
                    primes[j] = false;
                }
            }
        }
        int[] result = new int[n];
        int index = 0;
        for (int i = 2; i <= n; i++) {
            if (primes[i]) {
                result[index++] = i;
            }
        }
        return Arrays.copyOf(result, index);
    }

    public static void main(String[] args) {
        int x = 10; // Change x to the desired number of primes
        List<Integer> primes = getFirstXPrimes(x);
        System.out.println("The first " + x + " prime numbers are: " + primes);

        int[] primes2 = sieveOfEratosthenes(30);
        System.out.println("The first " + x + " prime numbers are: " + Arrays.toString(primes2));
    }
}