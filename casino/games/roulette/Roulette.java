//extends game
package casino.games.roulette;

import casino.games.roulette.Wheel;
import java.util.Scanner;

public class Roulette {
    //call wheel and get roll of wheel with color, number and its odd/even/zeroness
    public static void main(String[] args){

//need to get player's bid (wager amount and range of winning numbers
        Scanner input = new Scanner(System.in);
        
        System.out.println("\nChoose your bet:");
        System.out.println("1. Red");
        System.out.println("2. Black");
        System.out.println("3. Green (0 or 00)");
        System.out.println("4. Even");
        System.out.println("5. Odd");
        System.out.println("6. Range of numbers (1-36)");

        System.out.print("Enter your choice (1-6): ");
        String betType = input.next();

        int min = 1;
        int max = 36;
        
        if (betType.equals("6")) {
            System.out.print("Enter the lowest number (1-36): ");
            min = input.nextInt();

            System.out.print("Enter the highest number (1-36): ");
            max = input.nextInt();

            if (min < 1 || max > 36 || min > max) {
                System.out.println("Invalid number range.");
                input.close();
                return;
            }
        }
        if (!betType.matches("[1-6]")) {
            System.out.println("Invalid bet selection.");
            input.close();
            return;
        }
        
        Wheel wheel = new Wheel();
        Wheel.WheelResult result = wheel.spin();

        String number = result.getNumber();
        String color = result.getColor();
        String oddEven = result.getOddEven();
        
        boolean won = switch (betType) {
            case "1" -> color.equals("red");
            case "2" -> color.equals("black");
            case "3" -> color.equals("green");
            case "4" -> oddEven.equals("even");
            case "5" -> oddEven.equals("odd");
            case "6" -> {
                int landedNumber = Integer.parseInt(number);
                yield landedNumber >= min && landedNumber <= max;
            }
            default -> false;
        };
//temp output
        System.out.println("\n--- Roulette Results ---");
        System.out.println("Number: " + number);
        System.out.println("Color: " + color);
        System.out.println("Odd/Even: " + oddEven);
        //  wager
        if (won) {
            System.out.println("You won!");
        } else {
            System.out.println("You lost!");
        }

        input.close();
    }
}
