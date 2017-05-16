package com.akvelon.mobilecenterandroiddemo.services.Fitness;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataSource;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResult;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.google.android.gms.fitness.FitnessActivities.STILL;

/**
 * Created by ruslan on 5/9/17.
 */

public class GoogleFitService implements FitnessService {

    public static final String TAG = "GoogleFitService";
    private GoogleApiClient mClient = null;
    private boolean mResolvingError;

    /**
     * Build a {@link GoogleApiClient} to authenticate the user and allow the application
     * to connect to the Fitness APIs. The included scopes should match the scopes needed
     * by your app (see the documentation for details).
     * Use the {@link GoogleApiClient.OnConnectionFailedListener}
     * to resolve authentication failures (for example, the user has not signed in
     * before, or has multiple accounts and must specify which account to use).
     */
    public void initFitnessClient(final FragmentActivity activity, final FitnessServiceInitCallback callback) {
        // resetting error resolving flag
        mResolvingError = false;

        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(activity.getApplicationContext())
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Subscribe to some data sources!
                                subscribe();

                                if (callback != null) {
                                    callback.onSuccess();
                                }
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
                .enableAutoManage(activity, 0, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.w(TAG, "Google Play services connection failed. Cause: " + result.toString());

                        if (!result.hasResolution()) {
                            if (callback != null) {
                                callback.onFail(result);
                            }
                            return;
                        }

                        if (!mResolvingError) {
                            try {
                                mResolvingError = true;
                                result.startResolutionForResult(activity, ConnectionResult.CANCELED);
                            } catch (IntentSender.SendIntentException e) {
                                if (callback != null) {
                                    callback.onFail(result);
                                }
                            }
                        }
                    }
                })
                .build();
    }

    /**
     * Record fitness data by requesting a subscription to background step data.
     */
    private void subscribe() {

        DataType[] dataTypes = {
                DataType.TYPE_STEP_COUNT_DELTA,
                DataType.TYPE_CALORIES_EXPENDED,
                DataType.TYPE_DISTANCE_DELTA,
                DataType.TYPE_ACTIVITY_SEGMENT
        };

        for (final DataType dataType : dataTypes) {
            Fitness.RecordingApi.subscribe(mClient, dataType)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            Log.w(TAG, "Subscribing to " + dataType + " finished with status: " + status);
                        }
                    });
        }
    }

    private DataReadRequest getDataRequest(Date startTime, Date endTime) {
        DataSource ESTIMATED_STEP_DELTAS = new DataSource.Builder()
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .setAppPackageName("com.google.android.gms")
                .build();
        DataReadRequest readRequest = new DataReadRequest.Builder()
                .aggregate(ESTIMATED_STEP_DELTAS /*DataType.TYPE_STEP_COUNT_DELTA*/, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .setTimeRange(startTime.getTime(), endTime.getTime(), TimeUnit.MILLISECONDS)
                .bucketByTime(1, TimeUnit.DAYS)
                .build();
        return readRequest;
    }

    public List<FitnessData> fetchData(Date startTime, Date endTime) {

        if (!mClient.isConnected() && !mClient.isConnecting()) {
            return null;
        }

        List<FitnessData> resultList = new ArrayList<FitnessData>();

        PendingResult<DataReadResult> pendingResult = Fitness.HistoryApi.readData(
                mClient,
                getDataRequest(startTime, endTime));

        DataReadResult readDataResult = pendingResult.await(30, TimeUnit.SECONDS);
        if (readDataResult.getBuckets().size() > 0) {
            // each bucket represents day
            for (Bucket bucket : readDataResult.getBuckets()) {
                int steps = 0;
                double calories = 0.0;
                double distance = 0.0;
                int activityTime = 0;
                Date date = new Date(bucket.getStartTime(TimeUnit.MILLISECONDS));

                List<DataSet> dataSets = bucket.getDataSets();
                for (DataSet dataSet : dataSets) {
                    for (DataPoint dp : dataSet.getDataPoints()) {
                        if (dp.getDataType().equals(DataType.TYPE_STEP_COUNT_DELTA)) {
                            steps = dp.getValue(Field.FIELD_STEPS).asInt();
                        } else if (dp.getDataType().equals(DataType.TYPE_CALORIES_EXPENDED)) {
                            calories = dp.getValue(Field.FIELD_CALORIES).asFloat();
                        } else if (dp.getDataType().equals(DataType.TYPE_DISTANCE_DELTA)) {
                            distance = dp.getValue(Field.FIELD_DISTANCE).asFloat();
                        } else if (dp.getDataType().equals(DataType.AGGREGATE_ACTIVITY_SUMMARY)) {
                            String activity = dp.getValue(Field.FIELD_ACTIVITY).asActivity();
                            // calculating sum of activities durations except "still"
                            if (!activity.equals(STILL)) {
                               activityTime += dp.getValue(Field.FIELD_DURATION).asInt();
                            }
                        }
                    }
                }

                FitnessData data = new FitnessData(steps, calories, distance, activityTime, date);
                resultList.add(data);
            }
        }

        return resultList;
    }
}
