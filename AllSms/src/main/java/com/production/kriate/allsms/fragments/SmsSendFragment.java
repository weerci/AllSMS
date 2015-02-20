package com.production.kriate.allsms.fragments;

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


public class SmsSendFragment extends DialogFragment{
    private DbSms mDbSms;
    private TextView mTextView;

    public static SmsSendFragment newInstance(DbSms dbSms){
        SmsSendFragment smsSendFragment = new SmsSendFragment();
        smsSendFragment.setDbSms(dbSms);
        return  smsSendFragment;
    }

    public DbSms getDbSms() {
        return mDbSms;
    }
    public void setDbSms(DbSms dbSms) {
        mDbSms = dbSms;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_send_sms, null);

        mTextView = (TextView)v.findViewById(R.id.dialog_send_sms_text);
        String s = String.format(getResources().getString(R.string.sms_sender_text), mDbSms.getTextSms(), mDbSms.getPhoneNumber());
        mTextView.setText(s);

        return new AlertDialog.Builder(getActivity()/*, R.style.AlertDialogCustom*/)
                .setView(v)
                .setTitle(R.string.sms_sender_title)
                .setPositiveButton(android.R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                sendResult(Activity.RESULT_OK);
                            }
                        }
                )
                .setNegativeButton(android.R.string.no, null)
                .create();
    }

    private void sendResult(int resultCode) {
        if (getTargetFragment() == null) {
            return;
        }

        Intent i = new Intent();
        i.putExtra(EditSmsFragment.EXTRA_SMS, mDbSms);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }
}
