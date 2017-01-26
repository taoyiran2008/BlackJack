package policy;

import main.Player;
import prop.Card;

/**
 * The core policy of playing games in a casino.
 *
 * We use policy design pattern because there're different rules in different casinos.
 * For example:the double rate is 1.2 if we are playing a pitch game(play within 2 decks), but
 * normally the double rate is 1.5.
 */
public interface IPolicy {
    public final int STATUS_IM_OK = 0; // standby mode
    // MSG_TYPE_BUST
    public final int STATUS_BUST = 1;
    public final int STATUS_BLACKJACK = 2;
    public final int STATUS_HIGH_FIVE = 3;
    public final int STATUS_END_TURN = 4;
    // after surrender, i'm out of this round, means noneed to count me in when summing
    // up the result.
    public final int STATUS_IM_OUT = 5;

/*    // A special state for the dealer, under such state he must hit another card till reaches
    // the limited number(normally equals to 17).
    public final int STATUS_STILL_HUNGRY = 6;
    public final int STATUS_IM_FULL = 7;*/

    public final int BLACK_JACK_VALUE = 21;
    public final int HIGH_FIVE_NUMBER = 5;

    /**Policy for a player*/
    // Just gimme my card no matter what.
    // Return int(original design) is redundant, since we got Player himself to keep the status.
    public void hit(Player player, Card card);
    public void stand(Player player);
    public void doubleWager(Player player, Card card);
    public void surrender(Player player);
    public void split(Player player);
    public void insurance(Player player);


    /**Policy for a dealer*/
    // The dealer never doubles, splits, or surrenders. It only draw cards while the condition
    // is not met, normally the total value of cards is less than 17.
    // If the dealer busts, all remaining player hands win.
    public void draw(Player player, Card card);
}
