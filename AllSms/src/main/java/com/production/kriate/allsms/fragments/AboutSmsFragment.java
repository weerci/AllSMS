package com.production.kriate.allsms.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.production.kriate.allsms.R;

import org.jetbrains.annotations.NotNull;

public class AboutSmsFragment extends Fragment {

    @NotNull
    public static AboutSmsFragment newInstance(){
        return new AboutSmsFragment();
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
        return inflater.inflate(R.layout.about_sms, container, false);
    }
}
