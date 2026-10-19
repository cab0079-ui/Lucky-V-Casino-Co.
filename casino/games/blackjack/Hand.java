/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.games.blackjack;

/**
 * @author Chase
 */

import java.util.ArrayList;
import java.util.List;

public class Hand {                     
    private List<Card> cards;               //creates a list called cards from the Card class
    
    public Hand(){                          //constructor
        cards = new ArrayList<>();          //the list of cards is held in an array
    }
    public void addCard(Card card){         //this method allows the games to give a card to the hand
        cards.add(card);                    //takes in a card object and adds it to the list
    }
    public Card getCard(int index){         //calls on the card within whatever index of the hand's list
        return cards.get(index);            //outputs the card at that index
    }
    public Card removeCard(int index){      //removes a card within whatever index of the hand's list
        return cards.remove(index);         //outputs the deleted card at that index
    }
    public int getSize(){                   //returns the value of the hand
        return cards.size();                //outputs the size of the hand/cards list
    }
    public List<Card> getCards(){           //gives access to the entire hand/list of cards
        return cards;                       //shows hand
    }
    public void clear(){                    //removes all cards from the hand
        cards.clear();                      //reclaims memory
    }
}