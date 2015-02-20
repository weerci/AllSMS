package com.production.kriate.allsms.fragments;

import android.app.Activity;
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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class CategorySmsFragment extends Fragment{
    private static final int CATEGORY_UPDATE = 0;
    private static final int CATEGORY_INSERT = 1;
    private CategoryListAdapter mCategoryListAdapter;

    @NotNull
    public static CategorySmsFragment newInstance(){
        return new CategorySmsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        ArrayList<DbCategory> categoryList = DbConnector.newInstance(getActivity()).getCategory().selectAll();
        mCategoryListAdapter = new CategoryListAdapter(categoryList);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.category_sms, container, false);
        ListView listView = (ListView) v.findViewById(R.id.list_view_category);
        listView.setAdapter(mCategoryListAdapter);
        registerForContextMenu(listView);

        return v;
    }
    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
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
    public boolean onContextItemSelected(@NotNull MenuItem item) {
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
    public void onActivityResult(int requestCode, int resultCode, @NotNull Intent data) {
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
        mCategoryListAdapter.setArrayDbContainer(DbConnector.newInstance(getActivity()).getCategory().selectAll());
        mCategoryListAdapter.notifyDataSetChanged();
    }

    private class CategoryListAdapter extends BaseAdapter{
        private ArrayList<DbCategory> arrayDbCategory;

        public CategoryListAdapter (ArrayList<DbCategory> arr) {
            setArrayDbContainer(arr);
        }
        public void setArrayDbContainer(ArrayList<DbCategory> arrayDbCategory) {
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
        @Nullable
        public View getView(int position, @Nullable View convertView, ViewGroup parent) {
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
