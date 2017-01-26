package policy;

import main.Player;
import prop.Card;

public class PolicyForDealer extends PolicyAdapter {
    
    private static final int LIMITED_VALUE = 17;

    @Override
    public void draw(Player player, Card card) {
        addCard(player, card);
    }

    private void addCard(Player player, Card card) {
        player.cards.add(card);
        int totalValue = player.getTotalValue();
        if (player.getTotalValue() < BLACK_JACK_VALUE) {
            if (player.getCardsNumber() == HIGH_FIVE_NUMBER) {
                player.status = STATUS_HIGH_FIVE;
            }
            if (totalValue < LIMITED_VALUE) {
                player.status = STATUS_IM_OK; // still hungry
            } else {
                player.status = STATUS_END_TURN; // I'm full
            }
        } else if (player.getTotalValue() == BLACK_JACK_VALUE) {
            player.status = STATUS_BLACKJACK;
        } else {
            player.status = STATUS_BUST;
        }
    }
}
