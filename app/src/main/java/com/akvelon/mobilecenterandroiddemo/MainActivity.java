package com.akvelon.mobilecenterandroiddemo;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DailyTotalResult;
import com.google.android.gms.fitness.result.DataReadResult;
import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.FitnessActivities.STILL;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnFragmentInteractionListener, StatsFragment.OnFragmentInteractionListener {

    public static final String TAG = "StepCounter";
    private GoogleApiClient mClient = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.main_navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showHomeFragment();

        // This ensures that if the user denies the permissions then uses Settings to re-enable
        // them, the app will start working.
//        buildFitnessClient();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * Build a {@link GoogleApiClient} to authenticate the user and allow the application
     * to connect to the Fitness APIs. The included scopes should match the scopes needed
     * by your app (see the documentation for details).
     * Use the {@link GoogleApiClient.OnConnectionFailedListener}
     * to resolve authentication failures (for example, the user has not signed in
     * before, or has multiple accounts and must specify which account to use).
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Subscribe to some data sources!
                                subscribe();
                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.w(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.w(TAG, "Connection lost.  Reason: Service Disconnected");
                                }
                            }
                        }
                )
                .enableAutoManage(this, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w(TAG, "Google Play services connection failed. Cause: " +
                                result.toString());
                    }
                })
                .build();
    }

    /**
     * Record step data by requesting a subscription to background step data.
     */
    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_STEP_COUNT_DELTA)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            if (status.getStatusCode()
                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
                                Log.i(TAG, "Existing subscription for activity detected.");
                            } else {
                                Log.i(TAG, "Successfully subscribed!");
                            }
                        } else {
                            Log.w(TAG, "There was a problem subscribing.");
                        }
                    }
                });


//        Fitness.RecordingApi.subscribe(mClient, DataType.TYPE_ACTIVITY_SAMPLES)
//                .setResultCallback(new ResultCallback<Status>() {
//                    @Override
//                    public void onResult(Status status) {
//                        if (status.isSuccess()) {
//                            if (status.getStatusCode()
//                                    == FitnessStatusCodes.SUCCESS_ALREADY_SUBSCRIBED) {
//                                Log.i(TAG, "Existing subscription for activity detected.");
//                            } else {
//                                Log.i(TAG, "Successfully subscribed!");
//                            }
//                        } else {
//                            Log.i(TAG, "There was a problem subscribing.");
//                        }
//                    }
//                });
    }



    /**
     * Read the current daily step total, computed from midnight of the current day
     * on the device's current timezone.
     */
    private class VerifyDataTask extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... params) {

            long total = 0;

            PendingResult<DailyTotalResult> result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_STEP_COUNT_DELTA);
            DailyTotalResult totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                total = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();
            } else {
                Log.w(TAG, "There was a problem getting the step count.");
            }

            String s = "Total steps: " + total + "\n";
            Log.i(TAG, "Total steps: " + total);

            result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_CALORIES_EXPENDED);
            totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();
                float calories = totalSet.isEmpty()
                        ? 0
                        : totalSet.getDataPoints().get(0).getValue(Field.FIELD_CALORIES).asFloat();
                s += "Calories: " + calories + "\n";
            } else {
                s += "There was a problem getting calories info\n";
            }

            result = Fitness.HistoryApi.readDailyTotal(mClient, DataType.TYPE_ACTIVITY_SEGMENT);
            totalResult = result.await(30, TimeUnit.SECONDS);
            if (totalResult.getStatus().isSuccess()) {
                DataSet totalSet = totalResult.getTotal();

                long duration = 0;
                for (DataPoint dp : totalSet.getDataPoints()) {
                    if (!dp.getValue(Field.FIELD_ACTIVITY).asActivity().equals(STILL)) {
                        duration = dp.getValue(Field.FIELD_DURATION).asInt();
                    }
                }

                s += "Active time: " + duration / 1000 + "\n";
            } else {
                s += "There was a problem getting active time\n";
            }
            // merge_calories_expended

            Calendar cal = Calendar.getInstance();
            cal.setTime(new Date());
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.add(Calendar.DAY_OF_MONTH, -5);

            Date startTime = cal.getTime();
            Date endTime = new Date();


            DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                    .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                    .setType(DataSource.TYPE_DERIVED)
                    .setStreamName("estimated_steps")
                    .setAppPackageName("com.google.android.gms")
                    .build();

            PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi.readData(
                    mClient,
                    new DataReadRequest.Builder()
                            .aggregate(ESTIMATED_STEP_DELTAS, DataType.AGGREGATE_STEP_COUNT_DELTA)
                            .setTimeRange(startTime.getTime(), endTime.getTime(), TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.HOURS)
                            .build());

            DataReadResult readDataResult = pendingResult.await(30, TimeUnit.SECONDS);
            if (readDataResult.getBuckets().size() > 0) {
                s += "5 days steps:\n";
                long steps = 0;
                for (Bucket bucket : readDataResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            s += "Data point: " + dp.getValue(Field.FIELD_STEPS).asInt() + "\n";
                            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));

                            steps += dp.getValue(Field.FIELD_STEPS).asInt();
                        }
                    }
                }
                s += "Total 5 days steps: " + steps + "\n";
            }

            pendingResult = Fitness.HistoryApi.readData(
                    mClient,
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                            .setTimeRange(startTime.getTime(), endTime.getTime(), TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.DAYS)
                            .build());

            readDataResult = pendingResult.await(30, TimeUnit.SECONDS);
            if (readDataResult.getBuckets().size() > 0) {
                s += "5 days calories:\n";
                float steps = 0;
                for (Bucket bucket : readDataResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            s += "Data point: " + dp.getValue(Field.FIELD_CALORIES).asFloat() + "\n";
                            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));

                            steps += dp.getValue(Field.FIELD_CALORIES).asFloat();
                        }
                    }
                }
                s += "Total 5 days calories: " + steps + "\n";
            }

            pendingResult = Fitness.HistoryApi.readData(
                    mClient,
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                            .setTimeRange(startTime.getTime(), endTime.getTime(), TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.HOURS)
                            .build());

            readDataResult = pendingResult.await(30, TimeUnit.SECONDS);
            if (readDataResult.getBuckets().size() > 0) {
                s += "5 days distance:\n";
                float steps = 0;
                for (Bucket bucket : readDataResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            s += "Data point: " + dp.getValue(Field.FIELD_DISTANCE).asFloat() + "\n";
                            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));

                            steps += dp.getValue(Field.FIELD_DISTANCE).asFloat();
                        }
                    }
                }
                s += "Total 5 days distance: " + steps + "\n";
            }

            pendingResult = Fitness.HistoryApi.readData(
                    mClient,
                    new DataReadRequest.Builder()
                            .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                            .setTimeRange(startTime.getTime(), endTime.getTime(), TimeUnit.MILLISECONDS)
                            .bucketByTime(1, TimeUnit.DAYS)
                            .build());

            readDataResult = pendingResult.await(30, TimeUnit.SECONDS);
            if (readDataResult.getBuckets().size() > 0) {
                s += "5 days activity:\n";
                float steps = 0;
                for (Bucket bucket : readDataResult.getBuckets()) {
                    List<DataSet> dataSets = bucket.getDataSets();
                    for (DataSet dataSet : dataSets) {
                        for (DataPoint dp : dataSet.getDataPoints()) {
                            s += "Data point: " + dp.getValue(Field.FIELD_ACTIVITY).asActivity() + " ";
                            s += dp.getValue(Field.FIELD_DURATION).asInt() / 1000 + " sec\n";
                            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
                            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));

                            steps += dp.getValue(Field.FIELD_DURATION).asInt() / 1000;
                        }
                    }
                }
                s += "Total 5 days activity: " + steps + "\n";
            }

            Log.d(TAG, s);

            return null;
        }
    }

    private void readData() {
        new VerifyDataTask().execute();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_stats:
                    showStatsFragment();
                    return true;
                default:
                    showHomeFragment();
                    return true;
            }
        }
    };

    private void showHomeFragment() {
        showFragment(new HomeFragment());
    }

    private void showStatsFragment() {
        showFragment(new StatsFragment());
    }

    private void showFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_fragment, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
