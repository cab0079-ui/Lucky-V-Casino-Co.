/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.cards;

/**
 * @author Chase
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {                                 //creates a deck of cards
    private List<Card> cards;                       //creates a list called cards from the card class

    public Deck(){                                  //constructor that initializes a standard 52 card deck
        cards = new ArrayList<>();                  //stores the list of cards within and array
        for (Suit suit : Suit.values()){            //nested inner loop that gives all enum values within suit
            for (Rank rank : Rank.values()){        //nested outer loop that gives all enum values within rank
                cards.add(new Card(suit, rank));    //creates each card and addsit to the deck, then proceeds with the next logical step until the end of the loop/enum constants
            }
        }
    }
    public void shuffle(){                          //this methods shuffles the cards in the list
        Collections.shuffle(cards);                 //uses the java utility method shuffle to randomize the cards
    }
    public Card drawCard(){
        return cards.remove(cards.size()- 1);
    }
}