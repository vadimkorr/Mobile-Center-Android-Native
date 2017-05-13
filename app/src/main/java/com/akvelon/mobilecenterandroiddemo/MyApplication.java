package com.akvelon.mobilecenterandroiddemo;

import android.app.Application;

import com.akvelon.mobilecenterandroiddemo.services.Analytics.AnalyticsService;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessService;
import com.akvelon.mobilecenterandroiddemo.services.ServicesFactory;
import com.akvelon.mobilecenterandroiddemo.services.Social.SocialService;

/**
 * Created by ruslan on 5/11/17.
 */

public class MyApplication extends Application {

    private ServicesFactory mServicesFactory;
    private FitnessService mFitnessService;
    private SocialService mFacebookService;
    private SocialService mTwitterService;
    private AnalyticsService mAnalyticsService;

    public MyApplication() {
        mServicesFactory = new ServicesFactory(this);
    }

    public FitnessService getFitnessService() {
        if (mFitnessService == null) {
            mFitnessService = mServicesFactory.getFitnessService();
        }
        return mFitnessService;
    }

    public SocialService getFacebookService() {
        if (mFacebookService == null) {
            mFacebookService = mServicesFactory.getFacebookService();
        }
        return mFacebookService;
    }

    public SocialService getTwitterService() {
        if (mTwitterService == null) {
            mTwitterService = mServicesFactory.getTwitterService();
        }
        return mTwitterService;
    }

    public AnalyticsService getAnalyticsService() {
        if (mAnalyticsService == null) {
            mAnalyticsService = mServicesFactory.getAnalyticsService();
        }
        return mAnalyticsService;
    }
}
