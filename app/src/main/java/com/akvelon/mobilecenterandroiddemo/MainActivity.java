package com.akvelon.mobilecenterandroiddemo;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.akvelon.mobilecenterandroiddemo.models.User;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessService;
import com.google.android.gms.common.ConnectionResult;

public class MainActivity extends AppCompatActivity {

    public static final String ARG_USER = "user";

    private Fragment mHomeFragment;
    private Fragment mStatsFragment;
    private Fragment mCurrentFragment;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUser = getIntent().getExtras().getParcelable(ARG_USER);

        FitnessService fitnessService = ((MyApplication)getApplication()).getFitnessService();
        fitnessService.initFitnessClient(this, new FitnessService.FitnessServiceInitCallback() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFail(ConnectionResult result) {

            }
        });

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showFragment(getHomeFragment());
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_stats:
                    fragment = getStatsFragment();
                    break;
                default:
                    fragment = getHomeFragment();
                    break;
            }

            if (mCurrentFragment != fragment) {
                showFragment(fragment);
            }
            return true;
        }
    };

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        fragmentTransaction.commit();

        mCurrentFragment = fragment;
    }

    public Fragment getHomeFragment() {
        if (mHomeFragment == null) {
            mHomeFragment = HomeFragment.newInstance(mUser);
        }
        return mHomeFragment;
    }

    public Fragment getStatsFragment() {
        if (mStatsFragment == null) {
            mStatsFragment = StatsFragment.newInstance();
        }
        return mStatsFragment;
    }
}
