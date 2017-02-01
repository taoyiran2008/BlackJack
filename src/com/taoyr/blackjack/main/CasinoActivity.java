package com.taoyr.blackjack.main;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Decks;
import com.taoyr.blackjack.gameworld.Group;
import com.taoyr.blackjack.gameworld.Player;
import com.taoyr.blackjack.policy.IPolicy;
import com.taoyr.blackjack.ui.PlayerBlock;
import com.taoyr.blackjack.util.CommonUtils;
import com.taoyr.blackjack.util.Logger;

public class CasinoActivity extends Activity implements OnClickListener {

    private Decks mDecks;
    private Group mGroup;

    private static final int MSG_INITIAL = 1;
    private static final int MSG_NEW_ROUND = 2;
    private static final int MSG_FIRST_ROUND = 3;
    private static final int MSG_SECOND_ROUND = 4;
    private static final int MSG_PLAYER_ROUND = 5;
    private static final int MSG_DEALER_ROUND = 6;

    private Button mHitBtn;
    private Button mStandBtn;
    private Button mDoubleBtn;
    private Button mSurrenderBtn;

    private EditText mBetText;
    private Button mDealBtn;
    private Button mResetBtn;
    
    private PlayerBlock dealer;
    private PlayerBlock player1;
    private PlayerBlock player2;
    private PlayerBlock player3;
    private PlayerBlock player4;
    
    private Context mContext;
    private Object mLock = new Object();
    
    private void blockProcess() {
        synchronized (mLock) {
            try {
                mLock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void resumeProcess() {
        synchronized (mLock) {
            mLock.notifyAll();
        }
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Logger.logDebug("wuwuwu");
            final int what = msg.what;
            new Thread(new Runnable() {
                public void run() {
                    switch (what) {
                    case MSG_INITIAL:
                        Logger.logDebug("initial.......");
                        doInitial();
                        break;
                    case MSG_NEW_ROUND:
                        doNewRound();
                        break;
                    case MSG_FIRST_ROUND:
                        doFirstRound();
                        break;
                    case MSG_SECOND_ROUND:
                        doSecondRound();
                        break;
                    case MSG_PLAYER_ROUND:
                        doPlayerRound();
                        break;
                    case MSG_DEALER_ROUND:
                        doDealerRound();
                        break;
                    default:
                        break;
                    } 
                }
            }).start();
        };
    };

    private void doInitial() {
        // Find a set of decks to play.
        mDecks = new Decks();

        // Seat the players.
        mGroup = new Group();
        mGroup.setPlayers(5);

        disablePlayerBtn(true);
        disableBetBtn(true);
        
        mHandler.sendEmptyMessage(MSG_NEW_ROUND);
    }

    private void doNewRound() {
        // Reset cards.
        mDecks.shuffle();
        mGroup.nextRound();

        disablePlayerBtn(true);
        disableBetBtn(false);
        
        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            if (player == mGroup.currentPlayer) {
                // Wait for the user operation.
                Logger.logDebug("Please throw your money into the betting box");
                blockProcess();
                int money = Integer.parseInt(mBetText.getText().toString());
                if (!player.startTurn(money)) {
                    CommonUtils.showToast(mContext,
                            "Error: not enough cash! for " + player.name);
                    return;
                }
            } else {
                // TODO: AI support.
                if (!player.startTurn(IPolicy.BET_MONEY_BOTTOM)) {
                    Logger.logDebug("Error: not enough cash! for " + player.name);
                    continue;
                }
            }
        }
        updateUI();
        mHandler.sendEmptyMessage(MSG_FIRST_ROUND);
    }

    private void printPlayersInfo() {
        Logger.logDebug(mGroup.toString());
    }

    private void updateUI() {
        mHandler.post(new Runnable() {
            public void run() {
                dealer.setContent(mGroup.dealer);
                ArrayList<Player> list = mGroup.getPlayersInOrder();
                player1.setContent(list.get(0));
                player2.setContent(list.get(1));
                player3.setContent(list.get(2));
                player4.setContent(list.get(3));
            }
        });
        CommonUtils.sleepInSec(2);
    }
 
    private void doFirstRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);
        
        // Deal cards from dealer's left.
        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            player.hit(mDecks.deal(false));
            if (player == mGroup.currentPlayer) {
                player.showCards();
            }
            updateUI();
        }
        // Last one is the dealer.
        mGroup.dealer.draw(mDecks.deal(false));
        updateUI();

        Logger.logDebug("first round");
        printPlayersInfo();
    }

    private void doSecondRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);
        
        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            player.hit(mDecks.deal(true));
            player.check(mGroup.dealer);
            updateUI();
        }
        mGroup.dealer.draw(mDecks.deal(true));

        Logger.logDebug("second round");
        printPlayersInfo();

        mHandler.sendEmptyMessage(MSG_PLAYER_ROUND);
    }

    private void doPlayerRound() {
        disablePlayerBtn(false);
        disableBetBtn(true);
        
        ArrayList<Player> list = mGroup
                .getPlayersInOrder();
        boolean endChoose = true;
        boolean allOut = true;
        for (Player player : list) {
            if (player == mGroup.currentPlayer
                    && player.status == IPolicy.STATUS_STAND_BY) {
                blockProcess();
            } else if (player.status == IPolicy.STATUS_STAND_BY) {
                // TODO: AI support.
                player.hit(mDecks.deal(true));
            }
            player.check(mGroup.dealer);
            updateUI();
            if (player.status == IPolicy.STATUS_STAND_BY) {
                // If players can choose, then no need to figure out allOut.
                endChoose = false;
            } else {
                if (player.status == IPolicy.STATUS_END_TURN) {
                    allOut = false;
                }
            }
        }
        Logger.logDebug("player round");
        printPlayersInfo();
        
        if (endChoose) {
            Logger.logDebug("all players end choice");
            if (allOut) {
                Logger.logDebug("all players are out");
                mHandler.sendEmptyMessage(MSG_NEW_ROUND);
            } else {
                mHandler.sendEmptyMessage(MSG_DEALER_ROUND);
            }
        } else {
            mHandler.sendEmptyMessage(MSG_PLAYER_ROUND);
        }
    }

    private void doDealerRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);
        
        while (mGroup.dealer.status == IPolicy.STATUS_STAND_BY) {
            mGroup.dealer.draw(mDecks.deal(true));
            updateUI();
        }

        // Qiu hou suan zhang.
        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            // In DealerRoundState, player should only got two status: out, end turn
            if (player.status == IPolicy.STATUS_END_TURN) {
                mGroup.dealer.check(player);
                updateUI();
            }
        }

        Logger.logDebug("dealer round");
        printPlayersInfo();

        mHandler.sendEmptyMessage(MSG_NEW_ROUND);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mContext = this;
        initView();
        
        Logger.logDebug("okok");
        mHandler.sendEmptyMessage(MSG_INITIAL);
    }

    private void initView() {
        mHitBtn = (Button) findViewById(R.id.hit);
        mStandBtn = (Button) findViewById(R.id.stand);
        mDoubleBtn = (Button) findViewById(R.id.doubleWager);
        mSurrenderBtn = (Button) findViewById(R.id.surrender);

        mBetText = (EditText) findViewById(R.id.bet);
        mDealBtn = (Button) findViewById(R.id.deal);
        mResetBtn = (Button) findViewById(R.id.reset);
        
        dealer = (PlayerBlock) findViewById(R.id.dealer);
        player1 = (PlayerBlock) findViewById(R.id.player1);
        player2 = (PlayerBlock) findViewById(R.id.player2);
        player3 = (PlayerBlock) findViewById(R.id.player3);
        player4 = (PlayerBlock) findViewById(R.id.player4);

        mHitBtn.setOnClickListener(this);
        mStandBtn.setOnClickListener(this);
        mDoubleBtn.setOnClickListener(this);
        mSurrenderBtn.setOnClickListener(this);
        mDealBtn.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);
    }

    private void disableBetBtn(final boolean disable) {
        mHandler.post(new Runnable() {
            public void run() {
                mBetText.setEnabled(!disable);
                mDealBtn.setEnabled(!disable);
                mResetBtn.setEnabled(!disable);
            }
        });
    }

    private void disablePlayerBtn(final boolean disable) {
        mHandler.post(new Runnable() {
            public void run() {
                mHitBtn.setEnabled(!disable);
                mStandBtn.setEnabled(!disable);
                mDoubleBtn.setEnabled(!disable);
                mSurrenderBtn.setEnabled(!disable);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
        case R.id.hit:
            mGroup.currentPlayer.hit(mDecks.deal(true));
            resumeProcess();
            break;
        case R.id.stand:
            mGroup.currentPlayer.stand();
            resumeProcess();
            break;
        case R.id.doubleWager:
            mGroup.currentPlayer.doubleWager(mDecks.deal(true));
            resumeProcess();
            break;
        case R.id.surrender:
            mGroup.currentPlayer.surrender();
            resumeProcess();
            break;
        case R.id.deal:
            if (TextUtils.isEmpty(mBetText.getText())) {
                CommonUtils.showToast(mContext, "bet shall not be empty!");
            } else {
                resumeProcess();
            }
            break;
        case R.id.reset:
            mBetText.setText("");
            break;
        default:
            break;
        }
    }
}
