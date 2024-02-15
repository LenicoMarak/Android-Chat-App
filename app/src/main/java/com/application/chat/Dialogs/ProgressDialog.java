package com.application.chat.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.application.chat.R;

public class ProgressDialog extends Dialog {
    public ProgressDialog(Context context) {
        super(context);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.custom_progress);
        setCancelable(false);
    }
    public void setMessage(String message){
        TextView textView=findViewById(R.id.textMessage);
        textView.setText(message);
    }
}
