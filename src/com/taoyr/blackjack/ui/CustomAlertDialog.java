package com.taoyr.blackjack.ui;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.taoyr.blackjack.R;

public class CustomAlertDialog extends Dialog {

    public CustomAlertDialog(Context context) {
        super(context);
    }

    public CustomAlertDialog(Context context, int theme) {
        super(context, theme);
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.bottom_menu_animation);

        // WindowManager windowManager = ((Activity)context).getWindowManager();
/*        WindowManager windowManager = (WindowManager)
                getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*1/3;
        getWindow().setAttributes(lp);*/
        // Do not dismiss when click outside of the dialog.
        setCanceledOnTouchOutside(false);
    }

    public interface CustomListener {
        public void confirm();

        public void cancel();
        // CasinoActivity#showRestartDialog: setListener {dialog.dismiss()}
        // public void cancel(DialogInterface dialog)
    }

    public static class Builder {
        private Context mContext;
        private String mTitle;
        private String mMessage;
        private String mBtn1Text;
        private String mBtn2Text;
        private CustomListener mListener;

        public Builder(Context context) {
            mContext = context;
        }

        public Builder setMessage(String msg) {
            mMessage = msg;
            return this;
        }

        public Builder setTitle(String title) {
            mTitle = title;
            return this;
        }

        public Builder setButton(String btn1, String btn2, CustomListener listener) {
            mBtn1Text = btn1;
            mBtn2Text = btn2;
            mListener = listener;
            return this;
        }

        public CustomAlertDialog create() {
            LayoutInflater inflater = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // Instantiate the dialog with the custom Theme
            final CustomAlertDialog dialog = new CustomAlertDialog(mContext, R.style.Dialog);
            // A dialog without theme specified is ugly, use style or extends AlertDialog.
            // final CustomAlertDialog dialog = new CustomAlertDialog(mContext);
            View rootView = inflater.inflate(R.layout.dialog_confirm, null);
            dialog.setContentView(rootView);
            // addView(contentView, new LayoutParams(
            //       LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT)
            TextView title = (TextView) rootView.findViewById(R.id.title);
            TextView message = (TextView) rootView.findViewById(R.id.message);
            Button btnOk = (Button) rootView.findViewById(R.id.positiveButton);
            Button btnNg = (Button) rootView.findViewById(R.id.negativeButton);

            // No need to do null check, since title.setText(null) is OK.
            title.setText(mTitle);
            message.setText(mMessage);
            btnOk.setText(mBtn1Text);
            btnNg.setText(mBtn2Text);

            if (mListener != null) {
                btnOk.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        mListener.confirm();
                        dialog.dismiss();
                    }
                });

                btnNg.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View arg0) {
                        mListener.cancel();
                        dialog.dismiss();
                    }
                });
            }
            
            return dialog;
        }
    }

    // In dialog_confirm.xml, we can not set height of root layout, this would cause
    // layout disorder since a dialog is actually a window state managed by
    // window manager service. This is why the code snippet below use
    // WindowManager.LayoutParams to set width of the dialog.
/*    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);

        init();
    }

    public void init() {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.confirm_dialog, null);
        setContentView(view);

        TextView tvTitle = (TextView) view.findViewById(R.id.title);
        TextView tvConfirm = (TextView) view.findViewById(R.id.confirm);
        TextView tvCancel = (TextView) view.findViewById(R.id.cancel);

        tvTitle.setText(title);
        tvConfirm.setText(confirmButtonText);
        tvCancel.setText(cacelButtonText);

        tvConfirm.setOnClickListener(new clickListener());
        tvCancel.setOnClickListener(new clickListener());

        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics d = context.getResources().getDisplayMetrics(); // 获取屏幕宽、高用
        lp.width = (int) (d.widthPixels * 0.8); // 高度设置为屏幕的0.6
        dialogWindow.setAttributes(lp);
    }*/
}
