package com.akvelon.mobilecenterandroiddemo;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

        // user data to show user name and photo
        mUser = getIntent().getExtras().getParcelable(ARG_USER);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        // initiate Google Fit sign in process
        initFitnessSignInProcess();

        // show home fragment
        showFragment(getHomeFragment());
    }

    private void initFitnessSignInProcess() {
        FitnessService fitnessService = ((MyApplication)getApplication()).getFitnessService();
        fitnessService.initFitnessClient(this, new FitnessService.FitnessServiceInitCallback() {
            @Override
            public void onSuccess() {
                // track Google Fit connection success event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackGoogleFitConnectResult(
                        true,
                        null
                );
                // do nothing, a fragment will start fetching fitness data automatically
            }

            @Override
            public void onFail(ConnectionResult result) {
                // track Google Fit connection failure event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackGoogleFitConnectResult(
                        false,
                        result.getErrorMessage()
                );
                //http://stackoverflow.com/questions/41019909/reopen-sign-in-dialog-to-connect-with-google-fit
                showFailDialog();
            }
        });
    }

    private void showFailDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Failed to connect to Google Fit Services. Unable to continue.")
                .setCancelable(false)
                .setPositiveButton("Exit", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.navigation_stats:
                    fragment = getStatsFragment();
                    // track statistics click event
                    ((MyApplication)getApplication()).getAnalyticsService().trackStatisticsClick();
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
