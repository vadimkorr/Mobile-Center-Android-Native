package com.akvelon.mobilecenterandroiddemo.services;

import android.content.Context;

import com.akvelon.mobilecenterandroiddemo.services.Analytics.AnalyticsService;
import com.akvelon.mobilecenterandroiddemo.services.Analytics.MCAnalyticsService;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessService;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.GoogleFitService;
import com.akvelon.mobilecenterandroiddemo.services.Social.FacebookService;
import com.akvelon.mobilecenterandroiddemo.services.Social.SocialService;
import com.akvelon.mobilecenterandroiddemo.services.Social.TwitterService;

/**
 * Created by ruslan on 5/11/17.
 */

public class ServicesFactory {


    private final Context mContext;

    public ServicesFactory(Context context) {
        mContext = context;
    }

    public FitnessService getFitnessService() {
        return new GoogleFitService();
    }

    public SocialService getFacebookService() {
        return new FacebookService();
    }

    public SocialService getTwitterService() {
        return new TwitterService(mContext);
    }

    public AnalyticsService getAnalyticsService() {
        return new MCAnalyticsService(mContext);
    }
}
