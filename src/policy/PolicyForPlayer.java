package policy;

import main.Player;
import prop.Card;

/**
 * Currently not support spit, insurance.
 * TODO: Synchronize for concurrency sake.
 */
public class PolicyForPlayer extends PolicyAdapter {

    @Override
    public void hit(Player player, Card card) {
        addCard(player, card);
    }

    @Override
    public void stand(Player player) {
        // Do nothing just end the turn and wait for the upcoming result.
        player.status = STATUS_END_TURN;
    }

    @Override
    public void doubleWager(Player player, Card card) {
        // Double the bet.
        player.thrownMoney(player.betInBox);
        // Take a single card and finish.
        player.status = STATUS_END_TURN;
        // We took a shot for big money, still may bust out.
        addCard(player, card);
    }

    @Override
    public void surrender(Player player) {
        // Give up a half-bet and retire from the game.
        player.status = STATUS_IM_OUT;
        player.takeBackMoney(player.betInBox/2);
    }

    private void addCard(Player player, Card card) {
        player.cards.add(card);
        if (player.getTotalValue() < BLACK_JACK_VALUE) {
            if (player.getCardsNumber() == HIGH_FIVE_NUMBER) {
                player.status = STATUS_HIGH_FIVE;
            }
            player.status = STATUS_IM_OK;
        } else if (player.getTotalValue() == BLACK_JACK_VALUE) {
            player.status = STATUS_BLACKJACK;
        } else {
            player.status = STATUS_BUST;
        }
    }
}
