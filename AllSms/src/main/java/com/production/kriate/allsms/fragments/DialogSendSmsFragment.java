package com.production.kriate.allsms.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.TextView;

import com.production.kriate.allsms.R;
import com.production.kriate.allsms.db.DbSms;

import org.jetbrains.annotations.NotNull;

/**
 * Фрагмент реализующий возможность добавления категорий
 */
public class DialogSendSmsFragment extends DialogFragment {
    public final static int ADD_SMS = 0;
    public final static int REMOVE_SMS = 1;
    public final static String EXTRA_SMS = "com.production.kriate.allsms.addCategoryFragment.EXTRA_ID_SMS";
    private DbSms mDbSms;


    @NotNull
    public static DialogSendSmsFragment newInstance(DbSms sms) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SMS, sms);
        DialogSendSmsFragment fragment = new DialogSendSmsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_send_sms_layout, null);

        mDbSms = (DbSms)getArguments().getSerializable(DialogSendSmsFragment.EXTRA_SMS);

        TextView textView = (TextView) v.findViewById(R.id.dialog_send_sms_text);
        String dialogTitle;
        switch (getTargetRequestCode()) {
            case ADD_SMS:
                textView.setText(getResources().getString(R.string.category_add_sms));
                dialogTitle = getResources().getString(R.string.category_add_title);
                break;
            default:
                textView.setText(getResources().getString(R.string.category_remove_sms));
                dialogTitle = getResources().getString(R.string.category_remove_title);
                break;
        }

        return new AlertDialog.Builder(getActivity()/*, R.style.AlertDialogCustom*/)
                .setView(v)
                .setTitle(dialogTitle)
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
        i.putExtra(DialogSendSmsFragment.EXTRA_SMS, mDbSms);
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }

}
