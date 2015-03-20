package com.production.kriate.allsms;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;

import com.production.kriate.allsms.util.IabHelper;

import org.jetbrains.annotations.Nullable;

/**
 * Активность предоставляющая возможность покупать категории в google pay
 */
public class PurchaseActivity extends ActionBarActivity {
    private IabHelper mHelper;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.purchase_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_drawer);

        String base64EncodedPublicKey = "";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
