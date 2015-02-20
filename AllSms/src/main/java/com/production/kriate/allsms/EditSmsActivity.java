package com.production.kriate.allsms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.production.kriate.allsms.db.DbSms;
import com.production.kriate.allsms.fragments.EditSmsFragment;

import org.jetbrains.annotations.NotNull;

/*Активность вызова фрагмента изменения шаблона SMS*/
public class EditSmsActivity extends ActivitySingle{

    @NotNull
    Fragment createFragment() {
        if(getIntent().hasExtra(EditSmsFragment.EXTRA_SMS)) {
            DbSms sms = (DbSms) getIntent().getSerializableExtra(EditSmsFragment.EXTRA_SMS);
            return EditSmsFragment.newInstance(sms);
        } else
            return EditSmsFragment.newInstance(null);
    }

    @SuppressWarnings("SameReturnValue")
    int getLayoutResId() {
        return R.layout.main_activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutResId());
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);

        if (fragment == null) {
            fragment = createFragment();
            manager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

}