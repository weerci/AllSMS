package com.production.kriate.allsms.fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.production.kriate.allsms.EditSmsActivity;
import com.production.kriate.allsms.PurchaseActivity;
import com.production.kriate.allsms.R;

import org.jetbrains.annotations.NotNull;

/**
 * Настройки приложения
 */
public class SettingsFragment extends Fragment {
    Button mStartPurchase;

    @NotNull
    public static SettingsFragment newInstance(){
        return new SettingsFragment();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        menu.findItem(R.id.menu_item_new_template).setVisible(false);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_layout, container, false);
        mStartPurchase = (Button) v.findViewById(R.id.setting_purshit_button_start);
        mStartPurchase.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PurchaseActivity.class);
                startActivity(i);
            }
        });
        return v;
    }

}
