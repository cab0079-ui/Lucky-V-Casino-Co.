/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.cards;
/**
 * @author Chase
 */

public class Card {                     //class that creates a card object
    private final Suit suit;            //declares a suit from class suit
    private final Rank rank;            //declares a rank from class rank

    public Card(Suit suit, Rank rank){  //constructor takes in a suit&rank enum from the suit&rank classes
        this.suit = suit;               //initializes the suit from the parameter as the suit used for the card object
        this.rank = rank;               //initializes the rank from the parameter as the rank used for the card object
    }
    public Suit getSuit(){              //allows the game/evaluator to access the suit
        return suit;                    //gives the suit
    }
    public Rank getRank(){              //allows the game/evaluator to access the rank
        return rank;                    //gives the rank
    }
}