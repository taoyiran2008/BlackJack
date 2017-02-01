package com.taoyr.blackjack.policy;

import com.taoyr.blackjack.gameworld.Card;
import com.taoyr.blackjack.gameworld.Player;

/**
 * The core policy of playing games in a casino.
 *
 * We use policy design pattern because there're different rules in different casinos.
 * For example:the double rate is 1.2 if we are playing a pitch game(play within 2 decks), but
 * normally the double rate is 1.5.
 */
public interface IPolicy {

	// Standby means this player still in the game process so he can make his choice of policy.
    public static final int STATUS_STAND_BY = 0;
    // Bust means total value of cards exceeds 21 and this player will lose his money.
    public static final int STATUS_BUST = 1;
    // Blackjack is a bonus cards which means this player can got money
    // before the dealer finish his hand.
    public static final int STATUS_BLACKJACK = 2;
    // High five is a bonus cards which means this player can got money before the dealer
    // finish his hand.
    public static final int STATUS_HIGH_FIVE = 3;
    // End turn means this player stop to make any choice and wait to be checked.
    public static final int STATUS_END_TURN = 4;
    // I'm out means no need to check this player before next round.
    // This state can be switched from other status like STATUS_BUST, STATUS_BLACKJACK.
    public static final int STATUS_OUT = 5;

    public static final int BET_MONEY_BOTTOM = 10; // bottom line is 10RMB
    public static final int BLACK_JACK_VALUE = 21;
    public static final int HIGH_FIVE_NUMBER = 5;

    /**Policy for the player*/
    public void hit(Player player, Card card);
    public void stand(Player player);
    public boolean doubleWager(Player player, Card card);
    public void surrender(Player player);
    public void split(Player player);
    public void insurance(Player player);

    /**Policy for the dealer*/
    // The dealer never doubles, splits, or surrenders. It only draw cards while the condition
    // is not met, normally the total value of cards is less than 17.
    // If the dealer busts, all remaining players win.
    public void draw(Player player, Card card);

    /**Policy for both player and dealer*/
    public void check(Player firstPlayer, Player secondPlayer);
}
