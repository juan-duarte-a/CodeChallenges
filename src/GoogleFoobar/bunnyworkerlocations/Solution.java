package GoogleFoobar.bunnyworkerlocations;

public class Solution {

    public static void main(String[] args) {
        System.out.println(solution(8, 6));
    }

    public static String solution(int x, int y) {
        long value = 1;

        for (int i = 0; i < y; i ++) {
            System.out.println();
            value += i;
            System.out.print(value);
        }

        for (int j = 1; j < x; j++) {
            value += y + j;
            System.out.print(" " + value);
        }
        System.out.println();

        return String.valueOf(value);
    }
}
