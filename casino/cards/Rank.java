/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.cards;

/**
 * @author chase
 */

public enum Rank {      //treats each Rank as an enum constant
    Ace(1),
    King(13),
    Queen(12),
    Jack(11),
    ten(10),
    nine(9),
    eight(8),
    seven(7),
    six(6),
    five(5),
    four(4),
    three(3),
    two(2);
    
    private final int value;
    
    Rank(int value) {
        this.value = value;
    }
    
    public int getValue(){
        return value;
    }
}
