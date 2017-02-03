package com.taoyr.blackjack.ui;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Card;
import com.taoyr.blackjack.gameworld.Group;
import com.taoyr.blackjack.gameworld.Player;
import com.taoyr.blackjack.policy.IPolicy;

public class PlayerBlock extends LinearLayout {

    private final Context mContext;
    private TextView mName;
    private TextView mTotalMoney;
    private TextView mBetInBox;
    private TextView mWinLose;
    private LinearLayout mCardsContainer;
    private RelativeLayout mLayout;
    private View mRoot;

    public PlayerBlock(Context context, AttributeSet attr) {
        // super(context); // Result to null pointer by using findViewById(id)
        super(context, attr);
        mRoot = LayoutInflater.from(context).inflate(R.layout.player, this);
        mName = (TextView) mRoot.findViewById(R.id.name);
        mTotalMoney = (TextView) mRoot.findViewById(R.id.totalMoney);
        mBetInBox = (TextView) mRoot.findViewById(R.id.betInBox);
        mWinLose = (TextView) findViewById(R.id.winLose);

        mCardsContainer = (LinearLayout) findViewById(R.id.cards);
        mLayout = (RelativeLayout) findViewById(R.id.main_container);
        mContext = context;
    }

    public void setContent(Group group, Player player) {
        if (player != null) {
            if (player.type == Player.PLAYER_TYPE_DEALER) {
                mName.setText(player.name + "(Dealer)");
            } else if (group.currentPlayer == player) {
                mName.setText(player.name + "(Player)");
            } else {
                mName.setText(player.name + "(Bot)");
            }

            if (player.status == IPolicy.STATUS_OUT) {
                mLayout.setBackgroundColor(Color.parseColor("#000000")); // black
            } else {
                // Should pass resolved color instead of resource id here.
                // mName.setBackgroundColor(android.R.color.black);
                
                // Take no effect(transparent)
                // mName.setBackgroundColor(0xFFFFFF);
                if (group.currentPlayer == player) {
                    mLayout.setBackgroundColor(Color.parseColor("#FFFFFF")); // white
                } else {
                    mLayout.setBackgroundColor(Color.parseColor("#FFF104")); // yellow
                }
            }

            mTotalMoney.setText("" + player.totalMoney);
            mBetInBox.setText("" + player.betInBox);
            mWinLose.setText(player.getWinLose());

            // Re-rendering cards.
            mCardsContainer.removeAllViews();
            for (Card card : player.cards) {
                CardBlock cardBlock = new CardBlock(mContext, null);
                cardBlock.setContent(card);
                // We can make cards stack on top of each other by using
                // margin attribute for RelativeLayout or x/zindex attribute for
                // AbsoluteLayout(Framelayout).
//               RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams)
//                        cardBlock.getLayoutParams();
//               RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
//                       RelativeLayout.LayoutParams.MATCH_PARENT,
//                       RelativeLayout.LayoutParams.WRAP_CONTENT);
                mCardsContainer.addView(cardBlock);
            }
        }
    }
}
