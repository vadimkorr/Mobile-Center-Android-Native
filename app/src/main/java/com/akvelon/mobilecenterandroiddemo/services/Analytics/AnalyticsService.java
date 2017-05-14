package com.akvelon.mobilecenterandroiddemo.services.Analytics;

/**
 * Created by ruslan on 5/13/17.
 */

public interface AnalyticsService {

    enum SocialNetwork {
        FACEBOOK,
        TWITTER
    }

    /**
     * Track Facebook login button click event
     */
    void trackLoginFacebookClick();

    /**
     * Track Twitter login button click event
     */
    void trackLoginTwitterClick();

    /**
     * Track social sign in request result event
     */
    void trackSocialSignInResult(final SocialNetwork socialNetwork, final boolean success, final String errorMessage);

    /**
     * Track Google Fit connection result event
     */
    void trackGoogleFitConnectResult(final boolean success, final String errorMessage);

    /**
     * Track Google Fit retrieve result event
     */
    void trackGoogleFitRetrieveResult(final boolean success, final String errorMessage);

    /**
     * Track statistics button click event
     */
    void trackStatisticsClick();

    /**
     * Track crash button click event
     */
    void trackCrashClick();
}
