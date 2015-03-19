package com.production.kriate.allsms.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.production.kriate.allsms.R;

import org.jetbrains.annotations.NotNull;

/**
 * Created by dima on 19.03.2015.
 */
public class SettingsFragment extends Fragment {
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
        return v;
    }

}
