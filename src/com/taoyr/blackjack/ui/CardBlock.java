package com.taoyr.blackjack.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Card;

public class CardBlock extends View {

    private TextView mName;
    private ImageView mType;

    public CardBlock(Context context, AttributeSet attr) {
        super(context, attr);
        LayoutInflater.from(context).inflate(R.layout.card, null);
        mName = (TextView) findViewById(R.id.name);
        mType = (ImageView) findViewById(R.id.type);
    }

    public void setContent(Card card) {
        mName.setText(card.displayName);
        switch (card.type) {
        case spades:
            mType.setBackgroundResource(R.drawable.spades);
            break;
        case hearts:
            mType.setBackgroundResource(R.drawable.hearts);
            break;
        case clubs:
            mType.setBackgroundResource(R.drawable.trefle);
            break;
        case diamonds:
            mType.setBackgroundResource(R.drawable.diamonds);
            break;
        default:
            break;
        }
    }
}
