package com.example.myplayandroid.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myplayandroid.R;

/**
 * Created by zhongzhiqiang on 19-4-18.
 */

public class MyProgressBarDialog extends Dialog {

    private ProgressBar mProgressBar;
    private Context mContext;
    private Button mCancelButton;
    private TextView mProgressPercent;
    private Button mPausedButton;

    public MyProgressBarDialog(Context context) {
        super(context);
        this.mContext = context;
        init();
    }

    private void init() {
        //提前设置Dialog的一些样式
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(Gravity.CENTER);//设置dialog显示居中



        WindowManager windowManager = ((Activity)mContext).getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.width = display.getWidth()*4/5;// 设置dialog宽度为屏幕的4/5
        getWindow().setAttributes(lp);
        setCanceledOnTouchOutside(false);//点击外部Dialog消失

        View view  = LayoutInflater.from(mContext).inflate(R.layout.mydialog_layout,null);
        mProgressBar = view.findViewById(R.id.progress);
        mCancelButton = view.findViewById(R.id.button_cancelled);
        mPausedButton = view.findViewById(R.id.button_paused);
        mProgressPercent = view.findViewById(R.id.progress_percent);
        setContentView(view);
    }


    public void setmProgressBar(int progress){
        mProgressBar.setProgress(progress);
    }

    public Button getmCancelButton(){
        return mCancelButton;
    }

    public Button getmPausedButton(){
        return mPausedButton;
    }

    public void setmProgressPercent(int percent) {
        mProgressPercent.setText(percent + "%");
    }


}
