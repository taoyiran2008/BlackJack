package com.taoyr.blackjack.main;

import java.util.ArrayList;

import android.R.integer;
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
import android.widget.Toast;

import com.taoyr.blackjack.R;
import com.taoyr.blackjack.gameworld.Decks;
import com.taoyr.blackjack.gameworld.Group;
import com.taoyr.blackjack.gameworld.Player;
import com.taoyr.blackjack.policy.IPolicy;
import com.taoyr.blackjack.ui.GroupInfoPanel;
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

    private static final int MSG_SHOW_TOAST = 10;

    private Button mHitBtn;
    private Button mStandBtn;
    private Button mDoubleBtn;
    private Button mSurrenderBtn;

    private EditText mBetText;
    private Button mDealBtn;
    private Button mResetBtn;

    private PlayerBlock mDealerBlock;
    private PlayerBlock mPlayer1Block;
    private PlayerBlock mPlayer2Block;
    private PlayerBlock mPlayer3Block;
    private PlayerBlock mPlayer4Block;
    private GroupInfoPanel mGroupInfoPanel;

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
        // TODO: Add a controller to control UI, like WifiController in aosp.
        public void handleMessage(final Message msg) {
            Logger.logDebug("handleMessage()");
            // Investigate: strange thing is that we can't use final msg as input
            // param directly. Because msg seems to be modified, msg.what=0.
            final int what = msg.what;
            final Object obj = msg.obj;
            if (what == MSG_SHOW_TOAST) {
                CommonUtils.showToast(mContext, obj.toString());
                return;
            }
            // All these time consuming jobs shall be done in the working thread,
            // which will run far more smoothly, even in some case seems not, for
            // instance take doInitial, doFirstRound out to the main UI thread,
            // we can feel the delay.
            new Thread(new Runnable() {
                public void run() {
                    Logger.logDebug("msg.what = " + msg.what);
                    Logger.logDebug("what = " + what);
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
        disablePlayerBtn(true);
        disableBetBtn(true);
        // Find a set of decks to play.
        mDecks = new Decks();

        // Seat the players.
        mGroup = new Group();
        mGroup.setPlayers(5);
        updateUI();
        
        mHandler.sendEmptyMessage(MSG_NEW_ROUND);
    }

    private void showToast(String msg) {
        Message message = Message.obtain();
        message.obj = msg;
        message.what = MSG_SHOW_TOAST;
        mHandler.sendMessage(message);
    }

    private void doNewRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);
        // Reset cards.
        mDecks.shuffle();
        mGroup.nextRound();

        if (mGroup.dealer.status == IPolicy.STATUS_OUT) {
            showToast("dealer not qualified since it's out, change dealer");
            mGroup.shiftDealer();
            mGroup.round--; // Restore previous number
            mHandler.sendEmptyMessage(MSG_NEW_ROUND);
            return;
        };

        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            if (player == mGroup.currentPlayer) {
                if (player.totalMoney < IPolicy.BET_MONEY_BOTTOM) {
                    // Current player is out, just restart the game.
                    showToast("your total money is below the bottom line "
                            + IPolicy.BET_MONEY_BOTTOM + ", resuming...");
                    mHandler.sendEmptyMessage(MSG_INITIAL);
                    return;
                }
                // Wait for the user operation.
                Logger.logDebug("Please throw your money into the betting box");
                showToast("Please throw your money into the betting box");
                disableBetBtn(false);
                blockProcess();
                int money = Integer.parseInt(mBetText.getText().toString());
                // For quick test.
/*                if (money == 10) {
                    showToast("your total money is below the bottom line "
                            + IPolicy.BET_MONEY_BOTTOM + ", resuming...");
                    mHandler.sendEmptyMessage(MSG_INITIAL);
                    return;
                }*/
                if (!player.startTurn(money)) {
                    Logger.logDebug("not enough cash for " + player.name + ", try smaller bet");
                    showToast("not enough cash for " + player.name + ", try smaller bet");
                    mGroup.round--; // Restore previous number
                    mHandler.sendEmptyMessage(MSG_NEW_ROUND);
                    // It's a must, or below sendMessage will be triggered, which
                    // would result in concurrency problem.
                    // Keep in mind: one working thread per game.
                    return;
                }
            } else {
                // TODO: AI support.
                // Need more strict check as current player do if we don't use
                // BET_MONEY_BOTTOM as always.
                if (player.status != IPolicy.STATUS_OUT
                        && !player.startTurn(IPolicy.BET_MONEY_BOTTOM)) {
                    // Current logic won't remove player since we assume the number
                    // of players is fixed to 5, and the number of player blocks
                    // which hold the players is also fixed to 5. The removal of
                    // player would lead to unexpected trouble.
                    // mGroup.players.remove(player);

                    Logger.logDebug("not enough cash for " + player.name);
                    showToast("AI player is out due to no money");
                    // ba ni qing(tai) chu qu.
                    player.status = IPolicy.STATUS_OUT;
                }
            }
        }

        updateUI();

        // Lastly check if the dealer has enough money to hold this round (gou bu gou ge).
        // Note that cash is transfered in the group(shou heng), so there always be a player
        // qualified, and don't worry about dead cycle.
        if (mGroup.dealer.totalMoney < mGroup.getTotalBetInBettingBox()) {
            showToast("not enough cash for dealer, change dealer");
            mGroup.shiftDealer();
            mGroup.round--; // Restore previous number
            mHandler.sendEmptyMessage(MSG_NEW_ROUND);
            return;
        }
  
        mHandler.sendEmptyMessage(MSG_FIRST_ROUND);
    }

    private void printPlayersInfo() {
        Logger.logDebug(mGroup.toString());
    }

    private void updateUI() {
        mHandler.post(new Runnable() {
            public void run() {
                // Player block is fixed, but players in the list may shift.
                mDealerBlock.setContent(mGroup, mGroup.dealer);
                ArrayList<Player> list = mGroup.getPlayersInOrder();
                mPlayer1Block.setContent(mGroup, list.get(0));
                mPlayer2Block.setContent(mGroup, list.get(1));
                mPlayer3Block.setContent(mGroup, list.get(2));
                mPlayer4Block.setContent(mGroup, list.get(3));
                
                mGroupInfoPanel.setContent(mGroup);
            }
        });
        CommonUtils.sleepInSec(1);
    }

    private void doFirstRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);

        // Deal cards from dealer's left.
        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            if (player.status != IPolicy.STATUS_STAND_BY) {
                continue;
            }
            player.hit(mDecks.deal(true));
            updateUI();
        }
        // Last one is the dealer.
        // No need to check dealer status, since it's not possible to be out.
        // If I got no money and I'm out, after dealer qualification check on previous
        // stage, it's not possible to be out.
        mGroup.dealer.draw(mDecks.deal(false));
        if (mGroup.dealer == mGroup.currentPlayer) {
            // Only dealer hide one card so that each player could decide his policy to use.
            mGroup.dealer.showCards();
        }
        updateUI();

        Logger.logDebug("first round");
        mHandler.sendEmptyMessage(MSG_SECOND_ROUND);
        printPlayersInfo();
    }

    private void doSecondRound() {
        disablePlayerBtn(true);
        disableBetBtn(true);

        ArrayList<Player> list = mGroup.getPlayersInOrder();
        for (Player player : list) {
            if (player.status != IPolicy.STATUS_STAND_BY) {
                continue;
            }
            player.hit(mDecks.deal(true));
            // Special card type check(blackjack, high five)
            player.check(mGroup.dealer);
            updateUI();
        }
        mGroup.dealer.draw(mDecks.deal(true));
        updateUI();

        Logger.logDebug("second round");
        printPlayersInfo();

        mHandler.sendEmptyMessage(MSG_PLAYER_ROUND);
    }

    private void doPlayerRound() {
        // For a hand of AI player, we should disable tool bar.
        disablePlayerBtn(true);
        disableBetBtn(true);

        ArrayList<Player> list = mGroup.getPlayersInOrder();
        boolean endChoose = true;
        boolean allOut = true;
        for (Player player : list) {
            if (player == mGroup.currentPlayer
                    && player.status == IPolicy.STATUS_STAND_BY) {
                disablePlayerBtn(false); // Enable tool bar
                showToast("Please choose your policy");
                blockProcess();
                player.check(mGroup.dealer);
                updateUI();
            } else if (player.status == IPolicy.STATUS_STAND_BY) {
                // TODO: AI support.
                player.hit(mDecks.deal(true));
                player.check(mGroup.dealer);
                updateUI();
            }
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
                // Wait a while for player to check the cards on desk.
                CommonUtils.sleepInSec(2);
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
            if (player.status == IPolicy.STATUS_END_TURN) {
                mGroup.dealer.check(player);
                updateUI();
            }
        }

        Logger.logDebug("dealer round");
        printPlayersInfo();

        // Wait a while for player to check the cards on desk.
        CommonUtils.sleepInSec(2);
        mHandler.sendEmptyMessage(MSG_NEW_ROUND);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.casino);

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

        mDealerBlock = (PlayerBlock) findViewById(R.id.dealer);
        mPlayer1Block = (PlayerBlock) findViewById(R.id.player1);
        mPlayer2Block = (PlayerBlock) findViewById(R.id.player2);
        mPlayer3Block = (PlayerBlock) findViewById(R.id.player3);
        mPlayer4Block = (PlayerBlock) findViewById(R.id.player4);
        mGroupInfoPanel = (GroupInfoPanel) findViewById(R.id.group_info_panel);

        mHitBtn.setOnClickListener(this);
        mStandBtn.setOnClickListener(this);
        mDoubleBtn.setOnClickListener(this);
        mSurrenderBtn.setOnClickListener(this);
        mDealBtn.setOnClickListener(this);
        mResetBtn.setOnClickListener(this);

        disablePlayerBtn(true);
        disableBetBtn(true);
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
                showToast("bet shall not be empty!");
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
