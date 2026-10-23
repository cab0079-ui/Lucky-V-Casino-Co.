/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package casino.games.blackjack;
import casino.cards.Deck;
import casino.cards.Card;
import casino.core.Wager;

/**
 * @author Chase
 * Acknowledgment: BlackJack class developed with assistance from
 * OpenAI ChatGPT (GPT-6) based on the assignment outline and 
 * requirements provided by the student, October 14, 2026.
 * https://chatgpt.com/
 */

public class Blackjack {            //Basic card game logic for Blackjack
    private Deck deck;              //stores the Deck of 52 cards
    private Hand humanplayer;       //stores the user's hand of cards
    private Hand aiplayer;          //stores the other AI player's hand of cards
    private Hand player;            //stores the Dealer's hand of cards
    
    private Wager humanWager;       //manages the user's credits and bets
    
    private boolean humanStayed;    //tracks T||F if the user stayed
    private boolean aiStayed;       //tracks T||F if the ai stayed
    private boolean gameOver;       //determines T||F if the game has ended
    
    public Blackjack(){             //default starting value
        this(1000);
    }
    
    public Blackjack(int startingCredits){             //constructor to create new game
        deck = new Deck();          //creates a new deck of 52 cards
        deck.shuffle();             //shuffles the deck at the start of the new game 
        
        humanplayer = new Hand();   //creates a new hand for the user
        aiplayer = new Hand();      //creates a new hand for the aiplayer
        player = new Hand();        //creates a new hand for the dealer
        
        humanWager = new Wager(startingCredits);       //creates a new bet for the user
        
        humanStayed = false;        //initalizes user status to not having stayed
        aiStayed = false;           //initalizes ai status to not having stayed
        gameOver = false;           //indicates the game has not ended
    }
    public boolean placeBet(int amount){                        //allows the user to place bets
        if (!gameOver && (!humanplayer.getCards().isEmpty()     //unless anyone has no cards
                || !aiplayer.getCards().isEmpty() 
                || !player.getCards().isEmpty())){
            return false;                                       //then not possible
        }
        return humanWager.placeBet(amount);                     //otherwise the bets placed
    }
    
    public int getCredits(){                                    //lets the user know how many credits they have
        return humanWager.getCredits();
    }
    
    public int currentBet(){                                    //checks the bet that is placed
        return humanWager.getCurrentBet();
    }
    
    public void dealCards(){                                    //starts a new round
        if (!humanWager.hasActiveBet()){                        //program cannot deal unless there's a bet
            return;
        }
        else if (!gameOver && (!humanplayer.getCards().isEmpty() ||  //if the game hasn't ended & the user has cards
                !aiplayer.getCards().isEmpty() ||               //or if the aiplayer has cards
                !player.getCards().isEmpty())){                 //or if the dealer has cards 
            return;                                             //exits without dealing b/c they already have cards/prevents dealing extra
        }
        
        humanplayer.clear();    //removes the user's old cards
        aiplayer.clear();       //removes the ai's old cards
        player.clear();         //removes the dealer's old cards 
        
        humanStayed = false;    //user's status is not stay so it resets the user's turn  
        aiStayed = false;       //ai's status is not stay so it resets the ai's turn
        gameOver = false;       //determines the game as being started
        
        humanplayer.addCard(deck.drawCard());   //deals the user's 1st card
        aiplayer.addCard(deck.drawCard());      //deals the ai's 1st card 
        player.addCard(deck.drawCard());        //deals the dealer's 1st card
                
        humanplayer.addCard(deck.drawCard());   //deals the user's 2nd card
        aiplayer.addCard(deck.drawCard());      //deals the ai's 2nd card
        player.addCard(deck.drawCard());        //deals the dealer's 2nd card
    };
    
    public void Hit(){                  //let's the user draw another card
        if (gameOver || humanStayed){    //checks whether the user can still play
            return;                     //stops if the game has ended or the user stayed
        }    
        
        humanplayer.addCard(deck.drawCard());   //gives the user another card
        
        if (Bust(humanplayer)){  //checks whether the user busted
        humanStayed = true;     //ends the user's turn & bust check
        }                         
    
        checkGameOver();        //checks whether all players have finished
    }
        
    public void Stay(){         //lets the user end there code
        if (gameOver || humanStayed){
            return;
        }
        humanStayed = true;     //marks the user as being finished
        playAI();               //allows the ai to play it's turn
        checkGameOver();        //checks whether the round can finish
    }
    
    public void playAI(){           //controls the ai's turn
        if (gameOver || aiStayed){  //checks whether the ai can play
            return;                 //stops if the ai has finish or the game has ended
        }
        
        while (calculateHand(aiplayer) < 17 && !Bust(aiplayer)){ //while ai hits below 17 & hasn't busted
            aiplayer.addCard(deck.drawCard());                   //add a card to it's hand
        }
        
        aiStayed = true;            //marks the ai's turn done
    }
    
    public int calculateHand(Hand hand){    //calculates the value of a hand
        int total = 0;                      //stores the total points 
        int aces = 0;                       //counts aces
        
        for (Card card : hand.getCards()){  //iterates through each card in hand for examination
            int value = card.getRank().getValue();     //gets the card's rank & names them value (as an int)
            if (value == 1){                //checks whether the card/s are an ace
                aces++;                     //records the ace/s
                total += 11;                //initially counts the ace as an 11
            }
            else if (value >= 10){          //checks for a 10 or a face card
                total += 10;                //assigns the value as a 10
            }
            else{                           //handles cards valued at 2-9
                total += value;             //adds the cards values
            }
        }                                   //ends the card loop
        
        while (total > 21 && aces > 0){     //checks if an ace must count as 1
            total -= 10;                    //if the user will bust w/ ace being 11, it changes the value to 1
            aces--;                         //records that the ace was adjusted
        }
        
        return total;   //outputs the hands total value
    }
    
    public boolean Bust(Hand hand){         //checks if the hand is a bust
        return calculateHand(hand) > 21;    //true only when the hand exceeds 21
    }
    
    public boolean isBlackjack(Hand hand){  //checks for a blackjack
        return hand.getCards().size() == 2  //requires exactly two cards
            && calculateHand(hand) == 21;   //the 2 cards total must be 21
    }
    
    public void dealerPlay(){   //controls the dealer's turn
        if (gameOver){          //check if round has the round ended
            return;             //stops the dealer
        }
        
        while (calculateHand(player) < 17){     //while dealer's hand is below 17
            player.addCard(deck.drawCard());    //the dealer recieves another card
        }
        
        gameOver = true;        //marks the round as finished
        deck.shuffle();         //shuffles the deck after the round
    }
    
    private void settleBet(){                   //determines where to the send the credits to 
        if (!humanWager.hasActiveBet()){        //can't start if there's already a bet
            return;
        }
        
        int humanTotal = calculateHand(humanplayer);        //calculate user's hand
        int dealerTotal = calculateHand(player);            //calculate dealer's hand
        
        boolean humanBlackjack = isBlackjack(humanplayer);  //checks for blackjack
        boolean dealerBlackjack = isBlackjack(player);
        
        if (humanBlackjack && dealerBlackjack){             //if they both have black then give them their bets back
            humanWager.push();
        }
        else if (humanBlackjack){                           //if user has blackjack, user wins
            humanWager.win(2.5);
        }
        else if (dealerBlackjack){                          //if dealer has blackjack human loses
            humanWager.lose();
        }
        else if (humanTotal > 21){                          //if user busts they lose
            humanWager.lose();
        }
        else if (dealerTotal > 21 || humanTotal > dealerTotal){ //if dealer loses or if the user has a higher hand then user wins
            humanWager.win(2.0);
        }
        else if (humanTotal == dealerTotal){                //if the hands are equal they tie 
            humanWager.push();                              //their bets are returned
        }
        else {
            humanWager.lose();                              //anything else the user loses 
        }
    }
    
    private void checkGameOver(){       //checks whether the round should finish
        if (humanStayed && aiStayed){   //checks whether both player are finished
            dealerPlay();               //proceed to dealer's turn 
        }
    } 
    
    public String Outcome(){                            //determines the winner
        int humanTotal = calculateHand(humanplayer);    //creates an integer total from the calculated user's hand
        int aiTotal = calculateHand(aiplayer);          //creates an integer total from the calculated ai's hand
        int dealerTotal = calculateHand(player);         //creates an integer total form the calculated dealer's hand
        
        String result = "Result:\n";                    //starts the result output
        result += "Human: " + humanTotal + "\n";        //gives user's total
        result += "AI: " + aiTotal + "\n";              //gives player 2's total
        result += "Dealer: " + dealerTotal + "\n";      //gives dealer's total
        
        if (humanTotal > 21){                           //checks if the user busted
            result += "Player 1 busted.\n";}            //outputs that result
        else if (aiTotal > 21){                         //checks if player 2 busted
            result += "Player 2 busted.\n";}            //outputs that result
        else if (dealerTotal > 21){                     //checks if the dealer busted
            result += "The Dealer busted.\n";           //outputs that result
        }
        
        if (humanTotal <= 21 && humanTotal > dealerTotal){          //checks if the user beat the dealer
            result += "Player 1 beats the Dealer's hand!\n";        //outputs that
        }
        else if (humanTotal <= 21 && humanTotal < dealerTotal       //checks if the dealer beats the user
                && dealerTotal <= 21){
            result += "The Dealer beats Player 1's hand!\n";        //outputs that
        }
        else if (humanTotal <= 21 && humanTotal == dealerTotal){    //checks if the user and dealer tie
            result += "Player 1 and the Dealer tie!\n";             //outputs that
        }
        
        if (aiTotal <= 21 && aiTotal > dealerTotal){                //checks if player 2 beats the dealer
            result += "Player 2 beats the Dealer's hand!\n";        //outputs that 
        }
        else if (aiTotal <= 21 && aiTotal < dealerTotal             //checks if the dealer beats player 2
                && dealerTotal <= 21){                      
            result += "The Dealer beats Player 2's hand!\n";        //outputs that
        }
        else if (aiTotal <= 21 && aiTotal == dealerTotal){          //checks if the dealer and player 2 tie
            result += "Player 2 & the Dealer tie!\n";               //outputs that
        }
        
        result += "Remaining Credits: " + humanWager.getCredits() + "\n";
        
        return result;                                              //outputs the results of the winner's and/or loser's
    }
}   
