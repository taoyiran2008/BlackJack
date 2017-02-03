package com.taoyr.blackjack.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Card;

public class CardBlock extends RelativeLayout {

    private TextView mName;
    private ImageView mType;
    private RelativeLayout mLayout;

    public CardBlock(Context context, AttributeSet attr) {
        super(context, attr);
        LayoutInflater.from(context).inflate(R.layout.card, this);
        mName = (TextView) findViewById(R.id.name);
        mType = (ImageView) findViewById(R.id.type);
        mLayout = (RelativeLayout) findViewById(R.id.card_container);
    }

    public void setContent(Card card) {
        if (card.visible) {
            mLayout.setBackgroundResource(R.drawable.shape_corner);
            mName.setText(card.displayName);
            // For type image, xml should not set a default image background, or otherwise
            // new image background would overlaps on top, instead of replacing it.
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
        } else {
            mLayout.setBackgroundResource(R.drawable.shape_corner_facedown);
            mName.setText("");
            // 0 to remove background, -1 will lead to crash.
            mType.setBackgroundResource(0);
        }
    }
}
