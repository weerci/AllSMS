package com.production.kriate.allsms.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.production.kriate.allsms.ActivitySingle;
import com.production.kriate.allsms.EditSmsActivity;
import com.production.kriate.allsms.R;
import com.production.kriate.allsms.SmsSend;
import com.production.kriate.allsms.db.DbConnector;
import com.production.kriate.allsms.db.DbSms;
import com.production.kriate.allsms.view.SlidingTabLayout;

import java.util.ArrayList;

public class PageSmsFragment extends Fragment {
    public static final int SMS_UPDATE = 0;
    public static final int SMS_INSERT = 1;
    private final int REQUEST_SEND_SMS = 3;
    public static final String DIALOG_SEND_SMS = "send_sms";
    public final static String CURRENT_PAGE_ID = "com.production.kriate.allsms.current_page_id";
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    public static PageSmsFragment newInstance(int indexPage) {
        Bundle args = new Bundle();
        args.putSerializable(CURRENT_PAGE_ID, indexPage);
        PageSmsFragment fragment = new PageSmsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.page_sms, container, false);

        return rootView;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        int currentPage = (int)getArguments().getSerializable(PageSmsFragment.CURRENT_PAGE_ID);
        mViewPager.setCurrentItem(currentPage, true);

        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_item_new_template:
                Intent i = new Intent(getActivity(), EditSmsActivity.class);
                startActivityForResult(i, PageSmsFragment.SMS_INSERT);
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
        View v = (View)info.targetView.getParent();
        ListView listView = (ListView)v.findViewById(R.id.list_view_sms);

        ListAdapter adapter = (ListAdapter)listView.getAdapter();
        DbSms dbSms = adapter.arrayDbSms.get(position);
        switch (item.getItemId()) {
            case R.id.menu_item_delete_template:
                DbConnector.newInstance(getActivity()).deleteOne(dbSms.getId());
                //updateList(adapter, mViewPager.getCurrentItem());
                ((ActivitySingle)getActivity()).selectItem(0, mViewPager.getCurrentItem());
                return true;
            case R.id.menu_item_edit_template:
                Intent i = new Intent(getActivity(), EditSmsActivity.class);
                i.putExtra(EditSmsFragment.EXTRA_SMS, dbSms);
                startActivityForResult(i, SMS_UPDATE);
                return true;
        }
        return super.onContextItemSelected(item);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            DbSms dbSms = (DbSms) data.getExtras().getSerializable(EditSmsFragment.EXTRA_SMS);
            int indexPage = mViewPager.getCurrentItem();
            switch (requestCode) {
                case SMS_INSERT:
                    DbConnector.newInstance(getActivity()).insert(dbSms);
                    //updateList(adapter, mViewPager.getCurrentItem());
                    ((ActivitySingle)getActivity()).selectItem(0, indexPage);
                    break;
                case SMS_UPDATE:
                    DbConnector.newInstance(getActivity()).update(dbSms);
                    //updateList(adapter, mViewPager.getCurrentItem());
                    ((ActivitySingle)getActivity()).selectItem(0, indexPage);
                    break;
                case REQUEST_SEND_SMS:
                    SmsSend.Send(getActivity(), dbSms);
                    break;
                default:
                    break;
            }
        }
    }

    private class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public int getItemPosition(Object object) {
            return super.getItemPosition(object);
        }

        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 1:
                    return getResources().getString(R.string.action_favorite);
                case 2:
                    return getResources().getString(R.string.action_other);
                default:
                    return getResources().getString(R.string.action_all);
            }
        }
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.page_item, container, false);
            container.addView(v);
            final ListView listView = (ListView)v.findViewById(R.id.list_view_sms);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    ListAdapter adapter = (ListAdapter)listView.getAdapter();
                    DbSms dbSms = adapter.arrayDbSms.get(position);
                    SmsSendFragment dialog = SmsSendFragment.newInstance(dbSms);
                    dialog.setTargetFragment(PageSmsFragment.this, REQUEST_SEND_SMS);
                    dialog.show(fm, DIALOG_SEND_SMS);
                }
            });
            ArrayList<DbSms> smsList;
            switch (position) {
                case 1:
                    smsList = DbConnector.newInstance(getActivity()).selectFavorite();
                    break;
                case 2:
                    smsList = DbConnector.newInstance(getActivity()).selectOther();
                    break;
                default:
                    smsList = DbConnector.newInstance(getActivity()).selectAll();
                    break;
            }

            ListAdapter adapter = new ListAdapter(getActivity(), smsList);
            listView.setAdapter(adapter);
            registerForContextMenu(listView);

            return v;
        }
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
    private class ListAdapter extends BaseAdapter {
        private LayoutInflater mLayoutInflater;
        private ArrayList<DbSms> arrayDbSms;

        public ListAdapter (Context ctx, ArrayList<DbSms> arr) {
            mLayoutInflater = LayoutInflater.from(ctx);
            setArrayDbSms(arr);
        }

        public ArrayList<DbSms> getArrayMyData() {
            return arrayDbSms;
        }

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

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater().inflate(R.layout.list_item_sms, null);
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
            ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.sms_list_img_favorite);
            if (isFavorite) {
                imageButton.setImageResource(R.drawable.ic_action_important);
            } else {
                imageButton.setImageResource(R.drawable.ic_action_not_important);
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