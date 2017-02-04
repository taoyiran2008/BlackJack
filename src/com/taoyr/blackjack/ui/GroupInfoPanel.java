package com.taoyr.blackjack.ui;

import android.content.Context;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Chronometer;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Group;
import com.taoyr.blackjack.gameworld.Player;
import com.taoyr.blackjack.policy.IPolicy;

/**
 * A UI panel displays information of a group, the UI(layout) and relating logic are
 * removed from casino.xml and CasinoActivity. This helps a lot to manage modules
 * separately.
 */
public class GroupInfoPanel extends LinearLayout {

    private final Context mContext;
    private Chronometer mTimer;
    private TextView mMaxRound;
    private TextView mSetNumber;
    private TextView mRoundNumber;
    private TextView mTotalBet;
    private TextView mAllMoney;
    private TextView mMinBet;
    private TextView mBottomBet;
    private TextView mWinner;
    private View mRoot;

    public GroupInfoPanel(Context context, AttributeSet attr) {
        // super(context); // Result to null pointer by using findViewById(id)
        super(context, attr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.group_info, this);
        mTimer = (Chronometer) mRoot.findViewById(R.id.timer);
        mMaxRound = (TextView) mRoot.findViewById(R.id.max_round);
        mSetNumber = (TextView) mRoot.findViewById(R.id.set_number);
        mRoundNumber = (TextView) findViewById(R.id.round_number);
        mTotalBet = (TextView) findViewById(R.id.total_bet);
        mAllMoney = (TextView) findViewById(R.id.total_money);
        mMinBet = (TextView) findViewById(R.id.min_bet);
        mBottomBet = (TextView) findViewById(R.id.bottom_bet);
        mWinner = (TextView) findViewById(R.id.winner);
        
        mContext = context;
        initTimer();
    }

    public void initTimer() {
        mTimer.setBase(SystemClock.elapsedRealtime());
        int hour = (int) ((SystemClock.elapsedRealtime() - mTimer.getBase())/1000/60);
        mTimer.setFormat("0" + String.valueOf(hour) + ":%s");
        mTimer.start();
    }

    public void setContent(Group group) {
        mMaxRound.setText("" + IPolicy.ROUNDS_EACH_SET);
        mSetNumber.setText("" + group.set);
        mRoundNumber.setText("" + group.round);
        mTotalBet.setText("" + group.getTotalBetInBettingBox());
        mAllMoney.setText("" + group.getTotalMoneyInPool());
        mMinBet.setText("" + group.getMinBetInBettingBox());
        mBottomBet.setText("" + IPolicy.BET_MONEY_BOTTOM);

        Player winner = group.getWinner();
        mWinner.setText((winner == null) ? "No winner" : winner.name);
    }
}
