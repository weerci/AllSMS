package com.production.kriate.allsms;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.production.kriate.allsms.db.DbCategory;
import com.production.kriate.allsms.fragments.EditCategoryFragment;

/**
 * Активность вызова фрагмента создания/редактирования категорий
 */
public class EditCategoryActivity extends ActivitySingle {

    protected Fragment createFragment() {
        getIntent().hasExtra(EditCategoryFragment.EXTRA_CATEGORY);
        DbCategory category = (DbCategory) getIntent().getSerializableExtra(EditCategoryFragment.EXTRA_CATEGORY);
        return EditCategoryFragment.newInstance(category);
    }

    protected int getLayoutResId() {
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
