package GoogleFoobar.solardoomsday;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.IntStream;

public class Solution {

    public static void main(String[] args) {
        System.out.println(Arrays.toString(solution(12)));
        System.out.println(Arrays.toString(solution(15324)));
    }

    public static int[] solution(int area) {
        ArrayList<Integer> areas = new ArrayList<>();
        int biggestArea;
        int remainingArea = area;

        do {
            biggestArea = (int) Math.pow((int) Math.sqrt(remainingArea), 2);
            remainingArea -= biggestArea;
            areas.add(biggestArea);
        } while ((int) (Math.sqrt(remainingArea)) >= 2);

        for (int i = 0; i < remainingArea; i++) {
            areas.add(1);
        }

        int[] areasArray = new int[areas.size()];
        IntStream.range(0, areas.size()).forEach(i -> areasArray[i] = areas.get(i));

        return areasArray;
    }

}
