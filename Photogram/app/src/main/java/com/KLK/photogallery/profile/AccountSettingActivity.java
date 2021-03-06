package com.KLK.photogallery.profile;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.KLK.photogallery.R;
import com.KLK.photogallery.helper.BottomNavigationViewUtils;
import com.KLK.photogallery.helper.SectionsStatePagerAdapter;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;

public class AccountSettingActivity extends AppCompatActivity {
    // For debugging
    private static final String TAG = "AccountSettingActivity";

    private Context context;
    private SectionsStatePagerAdapter statePagerAdapter;
    private ViewPager viewPager;
    private RelativeLayout relativeLayout;
    private static final int ACTIVITY_NUM = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_setting);
        context = AccountSettingActivity.this;
        Log.d(TAG, "onCreate(): Start!");
        viewPager = (ViewPager)findViewById(R.id.viewpager_container);
        relativeLayout = (RelativeLayout)findViewById(R.id.relLayout1);

        createSettingList();
        configBottomNavigationView();
        initFragments();
        getIncomingIntent();

        //navigate back to profile activity
        ImageView backArrow = (ImageView)findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: navigate back to profile activity!");
                finish();
            }
        });
    }

    private void getIncomingIntent(){
        Intent intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))){
            Log.d(TAG, "getIncomingIntent: receive intent from " + getString(R.string.profile_activity));
            // set view pager to EditProfileFragment
            setViewPager(statePagerAdapter.getFragmentNumber(getString(R.string.edit_profile_fragment)));
        }
    }

    private void initFragments(){
        statePagerAdapter = new SectionsStatePagerAdapter(getSupportFragmentManager());

        statePagerAdapter.addFragment(new EditProfileFragment(), getString(R.string.edit_profile_fragment)); // fragment 0
        statePagerAdapter.addFragment(new SignoutFragment(), getString(R.string.sign_out_fragment)); // fragment 1
    }

    // responsible for navigating to each fragment
    private void setViewPager(int fragmentNumber){
        relativeLayout.setVisibility(View.GONE);
        Log.d(TAG, "setViewPager: navigate to fragment number " + fragmentNumber);

        // instantiate viewPager (fragment will get setup) and navigate to the chosen fragment
        viewPager.setAdapter(statePagerAdapter);
        viewPager.setCurrentItem(fragmentNumber);
    }

    // options for Settings
    private void createSettingList(){
        Log.d(TAG, "createSettingList: create a setting list!");
        ListView listView = (ListView)findViewById(R.id.lvAccountSettings);

        ArrayList<String>options = new ArrayList<>();
        options.add(getString(R.string.edit_profile_fragment)); // fragment 0
        options.add(getString(R.string.sign_out_fragment));   // fragment 1

        ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1,options);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Log.d(TAG, "onItemClick: navigate to fragment " + position);
                setViewPager(position);
            }
        });
    }

    private void configBottomNavigationView(){
        Log.d(TAG, "Config Bottom Navigation View!");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx)findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewUtils.configBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewUtils.navigating(context,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}