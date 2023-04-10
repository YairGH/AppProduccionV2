package com.ygh.produccion.appproduccionv2;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class LoadingDialog {
    private Activity activity;
    private AlertDialog dialog;
    private TextView lblLoadingText;

    LoadingDialog(Activity myActivity) {
        activity = myActivity;
    }

    void startLoadingDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.activity_custom_dialog, null));
        builder.setCancelable(true);

        View v = inflater.inflate(R.layout.activity_custom_dialog, null);

        lblLoadingText = (TextView)v.findViewById(R.id.lblLoadingText);
        lblLoadingText.setText(msg);

        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    void dismissDialog() {
        dialog.dismiss();
    }
}
