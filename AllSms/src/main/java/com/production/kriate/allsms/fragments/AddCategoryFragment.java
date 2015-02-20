package com.production.kriate.allsms.fragments;

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

/**
 * Created by dima on 16.02.2015.
 */
public class AddCategoryFragment extends DialogFragment {
    public final static int ADD_SMS = 0;
    public final static int REMOVE_SMS = 1;
    public final static String EXTRA_SMS = "com.production.kriate.allsms.addCategoryFragment.EXTRA_ID_SMS";
    private TextView mTextView;
    private DbSms mDbSms;


    public static AddCategoryFragment newInstance(DbSms sms) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_SMS, sms);
        AddCategoryFragment fragment = new AddCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        View v = getActivity().getLayoutInflater().inflate(R.layout.dialog_send_sms, null);

        mDbSms = (DbSms)getArguments().getSerializable(AddCategoryFragment.EXTRA_SMS);

        mTextView = (TextView)v.findViewById(R.id.dialog_send_sms_text);
        String dialogTitle;
        switch (getTargetRequestCode()) {
            case ADD_SMS:
                mTextView.setText(getResources().getString(R.string.category_add_sms));
                dialogTitle = getResources().getString(R.string.category_add_title);
                break;
            default:
                mTextView.setText(getResources().getString(R.string.categroy_remove_sms));
                dialogTitle = getResources().getString(R.string.category_remove_title);
                break;
        }

        return new AlertDialog.Builder(getActivity()/*, R.style.AlertDialogCustom*/)
                .setView(v)
                .setTitle(dialogTitle)
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
        i.putExtra(AddCategoryFragment.EXTRA_SMS, mDbSms);
        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, i);
    }

}
