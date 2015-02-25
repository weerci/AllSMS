package com.production.kriate.allsms.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import com.production.kriate.allsms.R;
import com.production.kriate.allsms.db.DbCategory;
import com.production.kriate.allsms.db.DbConnector;
import com.production.kriate.allsms.db.DbSms;

import org.jetbrains.annotations.NotNull;

public class EditSmsFragment extends Fragment {
    public static final String EXTRA_SMS = "com.production.kriate.allsms.EditSmsFragment.EXTRA_SMS";
    private static final int REQUEST_CONTACT = 0;
    private DbSms mSms;
    private EditText mTitleField, mTextField, mPhoneField;
    private CheckBox mIsFavorite;
    private Spinner mCategroySpinner;
    private ArrayAdapter<DbCategory> mAdapterCategory;
    private long mId;


    @NotNull
    public static EditSmsFragment newInstance(DbSms ds) {
        EditSmsFragment fragment = new EditSmsFragment();
        fragment.setSms(ds);
        return fragment;
    }

    private int checkToPriority()
    {
        return mIsFavorite.isChecked()? 1 : 0;
    }
    void setSms(DbSms ds)
    {
        mSms = ds;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mAdapterCategory = new ArrayAdapter<DbCategory>(getActivity(), R.layout.spinner_item_categroy,
                DbConnector.newInstance(getActivity()).getCategory().selectAll());
    }
    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        menu.findItem(R.id.menu_item_new_template).setVisible(false);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_sms_layout, container, false);

        mTitleField = (EditText) v.findViewById(R.id.sms_title_edit_text);
        mTextField = (EditText) v.findViewById(R.id.sms_text_edit_text);
        mIsFavorite = (CheckBox) v.findViewById(R.id.sms_is_favorite_checkbox);
        mPhoneField = (EditText) v.findViewById(R.id.phone_number_edit_text);
        mCategroySpinner = (Spinner)v.findViewById(R.id.sms_category_spinner);
        mCategroySpinner.setAdapter(mAdapterCategory);
        mId = DbSms.EMPTY_ID;

        if (mSms != null) {
            mId = mSms.getId();
            mTitleField.setText(mSms.getTitleSms());
            mTextField.setText(mSms.getTextSms());
            mPhoneField.setText(mSms.getPhoneNumber());
            mIsFavorite.setChecked(mSms.getPriority() != 0);
        }

        // Кнопки
        Button saveButton = (Button) v.findViewById(R.id.butSave);
        Button cancelButton = (Button) v.findViewById(R.id.butCancel);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbSms dbSms = new DbSms(mId, mTitleField.getText().toString(), mTextField.getText().toString(),
                        mPhoneField.getText().toString(), checkToPriority(),
                        (DbCategory)checkPositionArray(mCategroySpinner.getSelectedItemPosition(), mAdapterCategory));

                Intent intent = new Intent();
                intent.putExtra(EXTRA_SMS, dbSms);
                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });
        Button selectPhoneButton = (Button) v.findViewById(R.id.select_phone_button);
        selectPhoneButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, REQUEST_CONTACT);
            }
        });

        return v;
    }
    private Object checkPositionArray(int position, ArrayAdapter<?> arrayAdapter) {
        return position == -1? null : arrayAdapter.getItem(position);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @NotNull Intent data) {
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