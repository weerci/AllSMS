package com.production.kriate.allsms.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.production.kriate.allsms.EditCategoryActivity;
import com.production.kriate.allsms.R;
import com.production.kriate.allsms.db.DbCategory;
import com.production.kriate.allsms.db.DbConnector;

import java.util.ArrayList;

public class CategorySmsFragment extends Fragment{
    public static final int CATEGORY_UPDATE = 0;
    public static final int CATEGORY_INSERT = 1;
    private ListView mListView;
    private CategoryListAdapter mCategoryListAdapter;
    private ArrayList<DbCategory> mCategoryList;

    public static CategorySmsFragment newInstance(){
        return new CategorySmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        mCategoryList = DbConnector.newInstance(getActivity()).getCategory().selectAll();
        mCategoryListAdapter = new CategoryListAdapter(getActivity(), mCategoryList);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.category_sms, container, false);
        mListView = (ListView)v.findViewById(R.id.list_view_category);
        mListView.setAdapter(mCategoryListAdapter);
        registerForContextMenu(mListView);

        return v;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_template:
                Intent i = new Intent(getActivity(), EditCategoryActivity.class);
                startActivityForResult(i, CategorySmsFragment.CATEGORY_INSERT);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getActivity().getMenuInflater().inflate(R.menu.sms_list_item_context, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        int position = info.position;

        DbCategory dbCategory = mCategoryListAdapter.arrayDbCategory.get(position);
        switch (item.getItemId()) {
            case R.id.menu_item_delete_template:
                DbConnector.newInstance(getActivity()).getCategory().deleteOne(dbCategory.getId());
                updateList();
                return true;
            case R.id.menu_item_edit_template:
                Intent i = new Intent(getActivity(), EditCategoryActivity.class);
                i.putExtra(EditCategoryFragment.EXTRA_CATEGORY, dbCategory);
                startActivityForResult(i, CATEGORY_UPDATE);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            DbCategory dbCategory = (DbCategory) data.getExtras().getSerializable(EditCategoryFragment.EXTRA_CATEGORY);
            switch (requestCode) {
                case CATEGORY_INSERT:
                    DbConnector.newInstance(getActivity()).getCategory().insert(dbCategory);
                    break;
                case CATEGORY_UPDATE:
                    DbConnector.newInstance(getActivity()).getCategory().update(dbCategory);
                    break;
                default:
                    break;
            }
            updateList();
        }

    }
    private void updateList () {
        mCategoryListAdapter.setArrayDbContayner(DbConnector.newInstance(getActivity()).getCategory().selectAll());
        mCategoryListAdapter.notifyDataSetChanged();
    }

    private class CategoryListAdapter extends BaseAdapter{
        private LayoutInflater mLayoutInflater;
        private ArrayList<DbCategory> arrayDbCategory;

        public CategoryListAdapter (Context ctx, ArrayList<DbCategory> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayDbContayner(arr);
        }
        public void setArrayDbContayner(ArrayList<DbCategory> arrayDbCategory) {
            this.arrayDbCategory = arrayDbCategory;
        }
        public int getCount () {
            return arrayDbCategory.size();
        }
        public Object getItem (int position) {

            return position;
        }
        public long getItemId (int position) {
            DbCategory dbCategory = arrayDbCategory.get(position);
            if (dbCategory != null) {
                return dbCategory.getId();
            }
            return 0;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.category_list_item, null);
            }

            DbCategory dc = arrayDbCategory.get(position);
            TextView nameTextView = (TextView)convertView.findViewById(R.id.category_list_item_name_text_view);
            nameTextView.setText(dc.getName());

            return convertView;
        }

    }
}
