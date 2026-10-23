//self-contained logic for roulette; wheel spin, number, etc.
/**
 * generate random number 0-37 evaluate the number in regards to color, 
 * odd/even/zero and return to be measured by whatever calls wheel
 *
 * @author joseph
 */
package casino.games.roulette.wheel;

import java.util.List;

//self-contained logic for roulette; wheel spin, number, etc.

//Generate random number when called, then return number with color odd ore even

public class Wheel {

    private final List<Integer> red = List.of(
            1, 3, 5, 7, 9, 12, 14, 16, 18, 19, 21, 23, 25, 27, 30, 32, 34, 36
    ); // otherwise green or black

    // Hold the result of a wheel spin
    public static class WheelResult {

        private final String number;
        private final String color;
        private final String oddEven;

        public WheelResult(String number, String color, String oddEven) {
            this.number = number;
            this.color = color;
            this.oddEven = oddEven;
        }

        public String getNumber() {
            return number;
        }

        public String getColor() {
            return color;
        }

        public String getOddEven() {
            return oddEven;
        }
    }

    // Spin the wheel and return all result qualifications (color/OddEvenZero)
    public WheelResult spin() {

        int randomNumber = (int) (Math.random() * 37);

        String number;
        String color;
        String oddEven;

        switch (randomNumber) {
            case 0 -> {
                number = "0";
                color = "green";
                oddEven = "zero";
            }
            case 37 -> {
                number = "00";
                color = "green";
                oddEven = "zero";
            }
            default -> {
                number = String.valueOf(randomNumber);
                color = red.contains(randomNumber) ? "red" : "black";
                oddEven = (randomNumber % 2 == 0) ? "even" : "odd";
            }
        }

        return new WheelResult(number, color, oddEven);
    }
}
