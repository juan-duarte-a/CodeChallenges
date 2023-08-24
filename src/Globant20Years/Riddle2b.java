package Globant20Years;

import java.math.BigInteger;

public class Riddle2b {

    public static void main(String[] args) {
        int aux, digit = 9, remainder = 0;
        BigInteger result = new BigInteger("0");
        BigInteger position = new BigInteger("1");

        do {
            aux = 9 * digit + remainder;
            digit = aux % 10;
            remainder = aux / 10;
            result = result.add(position.multiply(BigInteger.valueOf(digit)));
            position = position.multiply(BigInteger.TEN);
        } while (remainder != 0 || digit != 9);

        System.out.println(result);
        System.out.println(result.divide(BigInteger.valueOf(9)));
    }

}
