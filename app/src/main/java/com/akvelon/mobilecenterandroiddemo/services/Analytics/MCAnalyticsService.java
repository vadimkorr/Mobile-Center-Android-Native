package com.akvelon.mobilecenterandroiddemo.services.Analytics;

import android.app.Application;
import android.content.Context;

import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ruslan on 5/13/17.
 */

public class MCAnalyticsService implements AnalyticsService {

    public MCAnalyticsService(Context context) {
        MobileCenter.start(
                (Application) context.getApplicationContext(),
                "6a9dd562-124f-4632-84ee-dfd3361d2e67",
                Analytics.class,
                Crashes.class
        );
    }

    @Override
    public void trackLoginFacebookClick() {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Facebook login button clicked", properties);
    }

    @Override
    public void trackLoginTwitterClick() {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Twitter login button clicked", properties);
    }

    @Override
    public void trackSocialSignInResult(final SocialNetwork socialNetwork, final boolean success, final String errorMessage) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Request");
            put("API", "Social network");
            put("Social network", socialNetwork.toString());
            put("Result", String.valueOf(success));
            put("Error message", errorMessage);
        }};
        Analytics.trackEvent("Trying to login in Facebook/Twitter", properties);
    }

    @Override
    public void trackGoogleFitConnectResult(final boolean success, final String errorMessage) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Request");
            put("API", "Google Fit");
            put("Result", String.valueOf(success));
            put("Error message", errorMessage);
        }};
        Analytics.trackEvent("Trying to connect to Google Fit API", properties);
    }

    @Override
    public void trackGoogleFitRetrieveResult(final boolean success, final String errorMessage) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Main");
            put("Category", "Request");
            put("API", "Google Fit");
            put("Result", String.valueOf(success));
            put("Error message", errorMessage);
        }};
        Analytics.trackEvent("Trying to retrieve data from Google Fit API", properties);
    }

    @Override
    public void trackStatisticsClick() {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Main");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("View statistics button clicked", properties);
    }

    @Override
    public void trackCrashClick() {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Profile");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Crash application button clicked", properties);
    }
}
