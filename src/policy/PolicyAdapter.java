package policy;

import main.Player;
import prop.Card;

/**
 * We use adapter pattern here to make our work simple in the future, so we can implement the
 * very methods we want to use, for instance, Dealer might want to implement draw() in particular.
 * 
 * com.android.internal.policy.PolicyManager(policies)
 */
public class PolicyAdapter implements IPolicy {

    @Override
    public void hit(Player player, Card card) {
    }

    @Override
    public void stand(Player player) {
    }

    @Override
    public void doubleWager(Player player, Card card) {
    }

    @Override
    public void surrender(Player player) {
    }

    @Override
    public void split(Player player) {
    }

    @Override
    public void insurance(Player player) {
    }

    @Override
    public void draw(Player player, Card card) {
    }

}
