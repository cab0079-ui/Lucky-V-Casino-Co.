/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package lucky_v_casino.Cards;

/**
 * @author Chase
 */
public class Card {
    private Suit suit;
    private Rank rank; 
        
    public Card(Suit suit, Rank rank){
        this.suit = suit;
        this.rank = rank;
    }
    public Suit getSuit(){
        return suit;
    }
    public Rank getRank(){
        return rank;
    }
}
