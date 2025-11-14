package circlebuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        CircleBuffer<Integer> intBuffer = new CircleBuffer<>(10);
        for (int j = 0; j < 10; j++) {
            for (int i = 1; i <= 9; i++) {
                intBuffer.write(i);
            }
            List<Integer> temp = new ArrayList<>();
            while (!intBuffer.isEmpty()) {
                temp.add(intBuffer.read());
            }
            System.out.println(temp);
        }
        for (int i = 1; i <= 9; i++) {
            intBuffer.write(i);
        }
        System.out.println(Arrays.toString(intBuffer.toArray()));

    }
}
