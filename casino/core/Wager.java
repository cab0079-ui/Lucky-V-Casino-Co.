/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.core;

/**
 *
 * @author Chase
 * Acknowledgment: Wager class developed with assistance from
 * OpenAI ChatGPT (GPT-6) based on the assignment outline and 
 * requirements provided by the student, October 23, 2026.
 * https://chatgpt.com/
 */

public class Wager {
    private int credits;
    private int currentBet;
    
    //constructor that ensures the start of each bet is some non-negative number
    public Wager(int startingCredits){
        if (startingCredits < 0){       
            throw new IllegalArgumentException(
                "Starting Credits Cannot be Negative!");
        }
    
    credits = startingCredits;
    currentBet = 0;
    }

    //this allows the user to place a bet and deducts from their credits
    public boolean placeBet(int amount){
        if (amount <= 0 || amount > credits || currentBet > 0){
            return false;
        }
    
        currentBet = amount;
        credits -= amount;
        return true;
    }
    
    //pay the winner the total return, including original bet.
    public void win(double payoutMultiplier){
        if (currentBet <= 0){
            throw new IllegalStateException(
                "No Active Bet!");
        }
        if (payoutMultiplier < 1.0){
            throw new IllegalArgumentException(
                "Payout Multiplier must be at least 1.0!");
        }
        
        credits += (int) Math.round(currentBet * payoutMultiplier);
        currentBet = 0;
    }
    
    //return the original bet in the event of a tie.
    public void push(){
        if (currentBet <= 0){
            throw new IllegalStateException(
                "No Active Bet!");
        }
        
        credits += currentBet;
        currentBet = 0;
    }
    
    //the user loses their credits
    public void lose(){
        if (currentBet <= 0){
            throw new IllegalStateException(
                "No Active Bet!");
        }
        currentBet = 0;
    }
    
    //award credits from a shared pot, for poker.
    public void awardPot(int amount){
        if (amount < 0){
            throw new IllegalArgumentException(
                "Award Cannot be Negative!");
        }
        
        credits += amount;
    }
    
    public int getCredits(){
        return credits;
    }
    
    public int getCurrentBet(){
        return currentBet;
    }
    
    public boolean hasActiveBet(){
        return currentBet > 0;
    }
}
