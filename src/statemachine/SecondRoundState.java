package statemachine;

import java.util.ArrayList;

import main.Player;
import policy.IPolicy;
import util.LogMan;

public class SecondRoundState extends State {

    public SecondRoundState(CasinoStateMachine stateMachine) {
        super(stateMachine);
    }

    public void execute() {
        ArrayList<Player> list = stateMachine.mCasino.mGroup.getPlayersInOrder();
        for (Player player : list) {
			player.hit(stateMachine.mCasino.mDecks.deal(true));
			player.check(stateMachine.mCasino.mGroup.dealer);
		}
        stateMachine.mCasino.mGroup.dealer.draw(stateMachine.mCasino.mDecks.deal(true));
        
        LogMan.logCrutial("second round");
        stateMachine.mCasino.printPlayersInfo();

        stateMachine.transferTo(new PlayerRoundState(stateMachine));
    }
}
