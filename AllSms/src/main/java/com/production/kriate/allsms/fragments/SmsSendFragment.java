package com.production.kriate.allsms.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.production.kriate.allsms.db.DbSms;
import com.production.kriate.allsms.R;

import org.jetbrains.annotations.NotNull;


public class SmsSendFragment extends DialogFragment{
    private DbSms mDbSms;

    @NotNull
    public static SmsSendFragment newInstance(DbSms dbSms){
        SmsSendFragment smsSendFragment = new SmsSendFragment();
        smsSendFragment.setDbSms(dbSms);
        return  smsSendFragment;
    }
    void setDbSms(DbSms dbSms) {
        mDbSms = dbSms;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_send_sms_layout, null);

        TextView textView = (TextView) v.findViewById(R.id.dialog_send_sms_text);
        String s = String.format(getResources().getString(R.string.sms_sender_text), mDbSms.getTextSms(), mDbSms.getPhoneNumber());
        textView.setText(s);

        return new AlertDialog.Builder(getActivity()/*, R.style.AlertDialogCustom*/)
                .setView(v)
                .setTitle(R.string.sms_sender_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult();
                            }
                        }
                )
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    private void sendResult() {
        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EditSmsFragment.EXTRA_SMS, mDbSms);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }
}
