//extends game
package casino.games.roulette;

import lucky_v_casino_Co.roulette.Wheel;


public class Roulette {
    //call wheel and get roll of wheel with color, number and its odd/even/zeroness
    public static void main(String[] args){
            Wheel wheel = new Wheel();
            
            Wheel.Weelresult = wheel.spin();
 
//need to get player's bid (wager amount and range of winning numbers

//black, red
//zeros
//even/odd

            
//temp output
    system.out.println("Numbah" + result.getNumber() );
    system.out.println("Colah"+ result.getColor() );
    system.out.println("powah of two" + result.getOddeven() );
    }
//end temp output
}
