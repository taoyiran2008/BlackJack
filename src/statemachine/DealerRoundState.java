package statemachine;

import java.util.ArrayList;

import main.Player;
import policy.IPolicy;
import util.CommonUtils;
import util.LogMan;

public class DealerRoundState extends State {

	public DealerRoundState(CasinoStateMachine stateMachine) {
		super(stateMachine);
	}

	public void execute() {
		while (stateMachine.mCasino.mGroup.dealer.status == IPolicy.STATUS_STAND_BY) {
			stateMachine.mCasino.mGroup.dealer.draw(stateMachine.mCasino.mDecks.deal(true));
		}
		
		// Qiu hou suan zhang.
		ArrayList<Player> list = stateMachine.mCasino.mGroup.getPlayersInOrder();
		for (Player player : list) {
			// In DealerRoundState, player should only got two status: out, end turn
			if (player.status == IPolicy.STATUS_END_TURN) {
				stateMachine.mCasino.mGroup.dealer.check(player);
			}
		}
		
		LogMan.logCrutial("dealer round");
		stateMachine.mCasino.printPlayersInfo();
		
		stateMachine.transferTo(new NewRoundState(stateMachine));
	}
}
