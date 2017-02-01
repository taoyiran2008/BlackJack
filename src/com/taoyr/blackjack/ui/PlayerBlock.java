package com.taoyr.blackjack.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Card;
import com.taoyr.blackjack.gameworld.Player;

public class PlayerBlock extends LinearLayout {

    private final Context mContext;
    private TextView mName;
    private TextView mTotalMoney;
    private TextView mBetInBox;
    private TextView mWinLose;
    private LinearLayout mCardsContainer;
    private View mRoot;

    public PlayerBlock(Context context, AttributeSet attr) {
        // super(context);
        super(context, attr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.player, this);
        mName = (TextView) mRoot.findViewById(R.id.name);
        mTotalMoney = (TextView) mRoot.findViewById(R.id.totalMoney);
        mBetInBox = (TextView) mRoot.findViewById(R.id.betInBox);
        mWinLose = (TextView) findViewById(R.id.winLose);

        mCardsContainer = (LinearLayout) findViewById(R.id.cards);
        mContext = context;
    }

    public void setContent(Player player) {
        if (player != null) {
            mName.setText(player.name);
            mTotalMoney.setText("" + player.totalMoney);
            mBetInBox.setText("" + player.betInBox);
            mWinLose.setText(player.getWinLose());
            
            for (Card card : player.cards) {
                CardBlock cardBlock = new CardBlock(mContext, null);
                cardBlock.setContent(card);
                mCardsContainer.addView(cardBlock);
            }
        }
    }
}
