package statemachine;

import java.util.ArrayList;

import policy.IPolicy;
import prop.Card;
import prop.Decks;
import util.LogMan;
import main.Player;

public class FirstRoundState extends State {

    public FirstRoundState(CasinoStateMachine stateMachine) {
        super(stateMachine);
    }

    public void execute() {
        // Deal cards from dealer's left.
    	ArrayList<Player> list = stateMachine.mCasino.mGroup.getPlayersInOrder();
        for (Player player : list) {
        	player.hit(stateMachine.mCasino.mDecks.deal(false));
		}
        // Last one is the dealer.
        stateMachine.mCasino.mGroup.dealer.draw(stateMachine.mCasino.mDecks.deal(false));
        stateMachine.mCasino.mGroup.currentPlayer.showCards();

        LogMan.logCrutial("first round");
        stateMachine.mCasino.printPlayersInfo();
        stateMachine.transferTo(new SecondRoundState(stateMachine));
    }
}
