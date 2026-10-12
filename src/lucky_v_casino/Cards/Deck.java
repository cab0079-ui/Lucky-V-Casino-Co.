/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lucky_v_casino.Cards;

/**
 * @author Chase
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;
    
    public Deck(){
        cards = new ArrayList<>();
        
        for (Suit suit : Suit.values()){
            for (Rank rank : Rank.values()){
                cards.add(new Card(suit, rank));
            }
        }
    }
    public void shuffle(){
        Collections.shuffle(cards);
    }
}
