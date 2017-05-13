package com.akvelon.mobilecenterandroiddemo.services.Fitness;

import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;

import java.util.Date;
import java.util.List;

/**
 * Created by ruslan on 5/11/17.
 */
public interface FitnessService {

    interface FitnessServiceInitCallback {
        void onSuccess();
        void onFail(ConnectionResult result);
    }

    void initFitnessClient(FragmentActivity activity, FitnessServiceInitCallback callback);

    List<FitnessData> fetchData(Date startTime, Date endTime);

}
