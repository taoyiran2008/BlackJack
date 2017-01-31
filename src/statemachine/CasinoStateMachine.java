package statemachine;

import main.Casino;

/**
 * Take control over casino.
 */
public class CasinoStateMachine {
	public State mCurrentState;
    public Casino mCasino;

    public CasinoStateMachine(Casino casino) {
    	mCasino = casino;
    }

    public void transferTo(State newState) {
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