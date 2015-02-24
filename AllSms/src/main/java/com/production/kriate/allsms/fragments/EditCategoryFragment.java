package com.production.kriate.allsms.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.production.kriate.allsms.R;
import com.production.kriate.allsms.db.DbCategory;
import com.production.kriate.allsms.db.DbConnector;
import com.production.kriate.allsms.db.DbSms;
import com.production.kriate.allsms.view.SlidingTabLayout;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

/**
 * Реализует создание/редактирование категорий
 */
public class EditCategoryFragment extends Fragment {
    public final static String EXTRA_CATEGORY = "com.production.kriate.allsms.EditCategoryFragment.db_category";
    private final static String DIALOG_ADD_REMOVE_CATEGORY_TO_SMS = "add_remove_category";
    private ArrayList<DbSms> mSelectedSms;
    private ArrayList<DbSms> mAvailableSms;


    private EditText mNameField;
    private long mIdCategory;
    private ViewPager mViewPager;
    private ListView mListView;

    @NotNull
    public static EditCategoryFragment newInstance(DbCategory dc) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_CATEGORY, dc);
        EditCategoryFragment fragment = new EditCategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.categroy_fragment, container, false);

        mNameField = (EditText) v.findViewById(R.id.category_name_edit_text);
        mIdCategory = DbCategory.EMPTY_ID;

        DbCategory dbCategory = (DbCategory)getArguments().getSerializable(EditCategoryFragment.EXTRA_CATEGORY);

        if (dbCategory != null) {
            mIdCategory = dbCategory.getId();
            mNameField.setText(dbCategory.getName());
        }
        mSelectedSms = DbConnector.newInstance(getActivity()).getCategory().getSelectedSms(dbCategory);
        mAvailableSms = DbConnector.newInstance(getActivity()).getCategory().getAvailableSms(dbCategory);

        // Кнопки
        Button saveButton = (Button) v.findViewById(R.id.categoryButtonSave);
        Button cancelButton = (Button) v.findViewById(R.id.categoryButtonCancel);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DbCategory dbCategory = new DbCategory(mIdCategory, mNameField.getText().toString(), mSelectedSms);
                Intent intent = new Intent();
                intent.putExtra(EXTRA_CATEGORY, dbCategory);
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

        return v;
    }
    @Override
    public void onViewCreated(@NotNull View view, @Nullable Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.category_viewpager);
        mViewPager.setAdapter(new SmsPagerAdapter());

        SlidingTabLayout slidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.category_sliding_tabs);
        slidingTabLayout.setViewPager(mViewPager);
    }
    @Override
    public void onPrepareOptionsMenu(@NotNull Menu menu) {
        menu.findItem(R.id.menu_item_new_template).setVisible(false);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @NotNull Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            DbSms dbSms = (DbSms)data.getSerializableExtra(AddCategoryFragment.EXTRA_SMS);
            switch (requestCode) {
                case 1:
                    mSelectedSms.remove(dbSms);
                    mAvailableSms.add(dbSms);
                    break;
                default:
                    mAvailableSms.remove(dbSms);
                    mSelectedSms.add(dbSms);
                    break;
            }
            ((ListSmsAdapter)mListView.getAdapter()).notifyDataSetChanged();
        }
    }

    private class SmsPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getResources().getString(R.string.category_sms_available);
                default:
                    return getResources().getString(R.string.category_sms_selected);
            }
        }
        @Override
        public Object instantiateItem(@NotNull ViewGroup container, int position) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.page_item, container, false);
            container.addView(v);
            mListView = (ListView)v.findViewById(R.id.list_view_sms);
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    mListView = (ListView)parent;
                    ListSmsAdapter listSmsAdapter = (ListSmsAdapter)mListView.getAdapter();
                    DbSms dbSms = listSmsAdapter.arrayDbSms.get(position);

                    AddCategoryFragment addCategoryFragment;
                    switch (mViewPager.getCurrentItem()) {
                        case 1:
                            addCategoryFragment = AddCategoryFragment.newInstance(dbSms);
                            addCategoryFragment.setTargetFragment(EditCategoryFragment.this, AddCategoryFragment.ADD_SMS);
                            break;
                        default:
                            addCategoryFragment = AddCategoryFragment.newInstance(dbSms);
                            addCategoryFragment.setTargetFragment(EditCategoryFragment.this, AddCategoryFragment.REMOVE_SMS);
                            break;
                    }
                    addCategoryFragment.show(fm, DIALOG_ADD_REMOVE_CATEGORY_TO_SMS);
                }
            });
            switch (position) {
                case 1:
                    mListView.setAdapter(new ListSmsAdapter(getActivity(), mAvailableSms));
                    break;
                default:
                    mListView.setAdapter(new ListSmsAdapter(getActivity(), mSelectedSms));
                    break;
            }
            registerForContextMenu(mListView);

            return v;
        }
        @Override
        public void destroyItem(@NotNull ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    private class ListSmsAdapter extends BaseAdapter {
        private ArrayList<DbSms> arrayDbSms;

        public ListSmsAdapter (@NotNull Context ctx, ArrayList<DbSms> arr) {
            setArrayDbSms(arr);
        }

// --Commented out by Inspection START (20.02.2015 21:11):
//        public ArrayList<DbSms> getArrayMyData() {
//            return arrayDbSms;
//        }
// --Commented out by Inspection STOP (20.02.2015 21:11)

        public void setArrayDbSms(ArrayList<DbSms> arrayDbSms) {
            this.arrayDbSms = arrayDbSms;
        }

        public int getCount () {
            return arrayDbSms.size();
        }

        public Object getItem (int position) {

            return position;
        }

        public long getItemId (int position) {
            DbSms dbSms = arrayDbSms.get(position);
            if (dbSms != null) {
                return dbSms.getId();
            }
            return 0;
        }

        @SuppressLint("InflateParams")
        @org.jetbrains.annotations.Nullable
        public View getView(int position, @org.jetbrains.annotations.Nullable View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.sms_list_item, null);
            }

            final DbSms ds = arrayDbSms.get(position);
            TextView titleTextView = (TextView)convertView.findViewById(R.id.sms_list_item_title_text_view);
            titleTextView.setText(ds.getTitleSms());
            TextView dateTextView = (TextView)convertView.findViewById(R.id.sms_list_item_text_text_view);
            dateTextView.setText(ds.getTextSms());
            TextView phoneTextView = (TextView)convertView.findViewById(R.id.sms_list_item_phone_text_view);
            phoneTextView.setText(getResources().getString(R.string.phone_prefix) + ds.getPhoneNumber());

            // Favorite button
            boolean isFavorite = ds.getPriority() != 0;
            ImageView imageView = (ImageView)convertView.findViewById(R.id.sms_list_img_favorite);
            if (isFavorite) {
                imageView.setImageResource(R.drawable.ic_action_important);
            } else {
                imageView.setImageResource(R.drawable.ic_action_not_important);
            }


//            imageButton.setOnClickListener( new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    View parentRow = (View) v.getParent();
//                    ListView listView = (ListView) parentRow.getParent();
//                    final int position = listView.getPositionForView(parentRow);
//                    ListAdapter listAdapter = (SmsAdapter)listView.getAdapter();
//                    DbSms dbSms = (DbSms)listAdapter.getItem(position);
//                    int priority = dbSms.getPriority() != 0 ? 0 : 1;
//                    DbSms newDbSms = new DbSms(dbSms.getId(), dbSms.getTitleSms(), dbSms.getTextSms(),
//                            dbSms.getPhoneNumber(), priority);
//                    DbSms dms =
//
//                    ListView listView = new ListView(getActivity(), null, null, null);
//                    listView.getPositionForView(v);
//                    DbSms dbSms = new DbSms(ds.getId(), ds.getTitleSms(), ds.getTextSms(), ds.getPhoneNumber(),
//                            );
//
//                }
//            });


            return convertView;
        }
    }

}
