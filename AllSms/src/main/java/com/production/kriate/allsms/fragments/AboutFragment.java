package com.production.kriate.allsms.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.production.kriate.allsms.Tools.AllSmsUtils;
import com.production.kriate.allsms.R;

import org.jetbrains.annotations.NotNull;

/*
*   Класс отображает информацию для формы "О программе".
*   Сязанный с ней layout - about_layout
*/
public class AboutFragment extends Fragment {

    private TextView mTextView;

    @NotNull
    public static AboutFragment newInstance(){
        return new AboutFragment();
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
        View v = inflater.inflate(R.layout.about_layout, container, false);
        mTextView = (TextView)v.findViewById(R.id.about_text_view_id);
        String s = String.format(getResources().getString(R.string.about_string_2), AllSmsUtils.getVersionName(getActivity()));
        mTextView.setText(s);
        return v;
    }
}
