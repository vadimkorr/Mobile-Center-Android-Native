package com.akvelon.mobilecenterandroiddemo.services.Social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Created by ruslan on 5/13/17.
 */

public class TwitterService extends Callback<TwitterSession> implements SocialService {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RpQDj4XFdHRvHp4l3uOKkyDJq";
    private static final String TWITTER_SECRET = "qqOILC0EPMvOFdsYXbE5zkgccU5Dsuo8P7PwcDR3cGoRLRm21c";
    private TwitterAuthClient mTwitterAuthClient;
    private Context mContext;
    private LogInCallback mCallback;

    public TwitterService(Context context) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new Twitter(authConfig));
        mTwitterAuthClient = new TwitterAuthClient();
    }

    @Override
    public void logIn(Activity activity, LogInCallback callback) {
        mCallback = callback;
        mTwitterAuthClient.authorize(activity, this);
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void success(Result<TwitterSession> result) {
        fetchUserProfile(result);
    }

    @Override
    public void failure(TwitterException exception) {
        Error error = new Error(exception.getLocalizedMessage());
        mCallback.onFailure(error);
    }

    private void fetchUserProfile(Result<TwitterSession> loginResult) {
        // The TwitterSession is also available through:
        // Twitter.getInstance().core.getSessionManager().getActiveSession()
        final TwitterSession session = loginResult.data;

        Call<User> userResult = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false);
        userResult.enqueue(new Callback<User>() {

            @Override
            public void failure(TwitterException e) {
                Error error = new Error(e.getLocalizedMessage());
                mCallback.onFailure(error);
            }

            @Override
            public void success(Result<User> userResult) {

                User twitterUser = userResult.data;
                String fullName = twitterUser.name;
                String accessToken = session.getAuthToken().token;
                String imageUrlBiggerSize = twitterUser.profileImageUrl.replace("_normal", "");

                com.akvelon.mobilecenterandroiddemo.models.User user;
                user = new com.akvelon.mobilecenterandroiddemo.models.User(
                        fullName,
                        accessToken,
                        imageUrlBiggerSize,
                        com.akvelon.mobilecenterandroiddemo.models.User.SocialNetwork.TWITTER
                );

                mCallback.onSuccess(user);
            }

        });
    }
}
