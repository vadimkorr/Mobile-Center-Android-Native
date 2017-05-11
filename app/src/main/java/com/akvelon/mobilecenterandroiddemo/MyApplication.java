package com.akvelon.mobilecenterandroiddemo;

import android.app.Application;
import android.content.pm.ApplicationInfo;

import com.akvelon.mobilecenterandroiddemo.services.FitnessService;
import com.akvelon.mobilecenterandroiddemo.services.ServicesFactory;

/**
 * Created by ruslan on 5/11/17.
 */

public class MyApplication extends Application {

    private ServicesFactory mServicesFactory;
    private FitnessService mFitnessService;

    public MyApplication() {
        mServicesFactory = new ServicesFactory();
    }

    public FitnessService getFitnessService() {
        if (mFitnessService == null) {
            mFitnessService = mServicesFactory.getFitnessService();
        }
        return mFitnessService;
    }
}
