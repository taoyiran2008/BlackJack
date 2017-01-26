package main;

import java.util.ArrayList;

import policy.IPolicy;
import policy.PolicyForDealer;
import policy.PolicyForPlayer;
import prop.Card;
import prop.Decks;
import util.LogMan;

public class Casino {

    private static final int BOTTOM_BET_MONEY = 10; // 10RMB
    private static final String[] NAMES = {"Richard", "Tom", "Jerry", "Mayun", "Obama"};
    private Decks mDecks;
    private Player mDealer;
    private ArrayList<Player> mPlayers = new ArrayList<>();
    private IPolicy mPolicyForPlayerImpl;
    private IPolicy mPolicyForDealerImpl;
    private CasinoStateMachine mStateMachine;

    public void startBusiness() {
        mStateMachine = new CasinoStateMachine();
        mStateMachine.setInitialState(new InitialState(mStateMachine));
        
        // Time consuming job is handled in a working thread.
        new Thread(new Runnable() {
            @Override
            public void run() {
                mStateMachine.process();
            }
        }).start();
    }

    /**
     * Players coming into the house, we can get started.
     */
    public void setPlayers(int num) {
        if (num <= 1 || num > 5) {
            LogMan.logCrutial("the specified number should greater than 1 and no more than 5");
            return;
        }
        mPlayers = new ArrayList<>(num);
        // Dealer is a rich guy.
        for (int i = 0; i < num; i++) {
            // The first player is initialized as the dealer.
            int type = ((i == 0) ? Player.PLAYER_TYPE_DEALER : Player.PLAYER_TYPE_NORMAL);
            if (i == 0) {
                mDealer = new Player(1000, BOTTOM_BET_MONEY, NAMES[i], type);
            } else {
                mPlayers.add(new Player(100, BOTTOM_BET_MONEY, NAMES[i], type));
            }
        }
    }

    private void printCards() {
        LogMan.logCrutial("dealer: " + mDealer.cards);
        for (Player player : mPlayers) {
            LogMan.logCrutial(player.name + ": " + player.cards);
        }
    }

    private class CasinoStateMachine {
        private State mCurrentState;
        private static final int CMD_FIRST_ROUND = 0;
        private static final int CMD_SECOND_ROUND = 1;
        private static final int CMD_PLAYER_HIT_ROUND = 2;
        private static final int CMD_DEALER_DRAW_ROUND = 3;
        private static final int CMD_SUM_UP_RESULT = 4;

        private void transferTo(State newState) {
            if (newState != null) {
                mCurrentState.exist();
                newState.enter();
                mCurrentState = newState;
                process();
            }
        }

        public void process() {
            if (mCurrentState != null) {
                mCurrentState.execute();
            }
        }

        public void setInitialState(State state) {
            mCurrentState = state;
        }
    }

    private class InitialState extends State {

        public InitialState(CasinoStateMachine stateMachine) {
            super(stateMachine);
        }

        public void execute() {
            // Find a set of decks to play.
            mDecks = new Decks();
            mDecks.shuffle();

            // Seat the players.
            // Current demo we set num to 2, since we need AI support for more than 2 players.
            setPlayers(2);

            mPolicyForPlayerImpl = new PolicyForPlayer();
            mPolicyForDealerImpl = new PolicyForDealer();
            
            stateMachine.transferTo(new FirstRoundState(stateMachine));
        }
    }

    private class FirstRoundState extends State {

        public FirstRoundState(CasinoStateMachine stateMachine) {
            super(stateMachine);
        }

        public void execute() {
            // Deal cards from dealer's left.
            for (int i = mPlayers.size() - 1; i >= 0; i--) {
                Player player = mPlayers.get(i);
                mPolicyForPlayerImpl.hit(player, mDecks.deal(false));
            }
            // Last one is the dealer.
            mPolicyForDealerImpl.draw(mDealer, mDecks.deal(false));
            
            printCards();
            stateMachine.transferTo(new SecondRoundState(stateMachine));
        }
    }
    
    private class SecondRoundState extends State {

        public SecondRoundState(CasinoStateMachine stateMachine) {
            super(stateMachine);
        }

        public void execute() {
            // Deal cards from dealer's left.
            for (int i = mPlayers.size() - 1; i >= 0; i--) {
                Player player = mPlayers.get(i);
                mPolicyForPlayerImpl.hit(player, mDecks.deal(true));
                if (player.status == IPolicy.STATUS_BLACKJACK) {
                    /*player.takeBackMoney(money)
                    player.getMoney*/
                }
            }
            mPolicyForDealerImpl.draw(mDealer, mDecks.deal(true));
            printCards();
            
            stateMachine.transferTo(null);
        }
    }

    private abstract class State {
        
        CasinoStateMachine stateMachine;
        
        public State(CasinoStateMachine stateMachine) {
            this.stateMachine = stateMachine;
        }
        
        public void enter() {
            LogMan.logDebug(getClass().getSimpleName() + ": enter()");
        }

        public void exist() {
            LogMan.logDebug(getClass().getSimpleName() + ": exist()");
        }

        public abstract void execute();
    }
}
