package com.production.kriate.allsms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.production.kriate.allsms.R;
import com.production.kriate.allsms.db.DbSms;

public class EditSmsFragment extends Fragment {
    public static final String EXTRA_SMS = "com.production.kriate.allsms.EditSmsFragment.EXTRA_SMS";
    private static final int REQUEST_CONTACT = 0;
    private DbSms mSms;
    private EditText mTitleField, mTextField, mPhoneField;
    private CheckBox mIsFavorite;
    private Button mSaveButton, mCancelButton, mSelectPhoneButton;
    private long mId;

    public static EditSmsFragment newInstance(){
        return new EditSmsFragment();
    }
    public static EditSmsFragment newInstance(DbSms ds) {
        EditSmsFragment fragment = new EditSmsFragment();
        fragment.setSms(ds);
        return fragment;
    }

    private int checkToPriority()
    {
        return mIsFavorite.isChecked()? 1 : 0;
    }
    public void setSms(DbSms ds)
    {
        mSms = ds;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.sms_fragment, container, false);

        mTitleField = (EditText) v.findViewById(R.id.sms_title_edit_text);
        mTextField = (EditText) v.findViewById(R.id.sms_text_edit_text);
        mIsFavorite = (CheckBox) v.findViewById(R.id.sms_is_favorite_checkbox);
        mPhoneField = (EditText) v.findViewById(R.id.phone_number_edit_text);
        mId = DbSms.EMPTY_ID;

        if (mSms != null) {
            mId = mSms.getId();
            mTitleField.setText(mSms.getTitleSms());
            mTextField.setText(mSms.getTextSms());
            mPhoneField.setText(mSms.getPhoneNumber());
            mIsFavorite.setChecked(mSms.getPriority() != 0);
        }

        // Кнопки
        mSaveButton = (Button) v.findViewById(R.id.butSave);
        mCancelButton = (Button) v.findViewById(R.id.butCancel);
        mSaveButton.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbSms dbSms = new DbSms(mId, mTitleField.getText().toString(), mTextField.getText().toString(),
                        mPhoneField.getText().toString(), checkToPriority());
                Intent intent = new Intent();
                intent.putExtra(EXTRA_SMS, dbSms);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        mSelectPhoneButton = (Button)v.findViewById(R.id.select_phone_button);
        mSelectPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        return v;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == REQUEST_CONTACT) {
            Uri contactUri = data.getData();
            String[] queryFields = new String[] {
                    ContactsContract.CommonDataKinds.Phone.NUMBER
            };
            Cursor c = getActivity().getContentResolver().query(contactUri, queryFields, null, null, null);
            int indexNumber = c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

            if (c.getCount() == 0) {
                c.close();
                return;
            }

            c.moveToFirst();
            String suspect = c.getString(indexNumber);
            mPhoneField.setText(suspect);
        }
    }
}