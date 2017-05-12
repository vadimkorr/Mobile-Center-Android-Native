package com.akvelon.mobilecenterandroiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.microsoft.azure.mobile.MobileCenter;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.models.User;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "RpQDj4XFdHRvHp4l3uOKkyDJq";
    private static final String TWITTER_SECRET = "qqOILC0EPMvOFdsYXbE5zkgccU5Dsuo8P7PwcDR3cGoRLRm21c";
    private TwitterAuthClient mTwitterAuthClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        mTwitterAuthClient = new TwitterAuthClient();

        setContentView(R.layout.activity_login);

        MobileCenter.start(getApplication(), "6a9dd562-124f-4632-84ee-dfd3361d2e67",
                Analytics.class, Crashes.class);
    }

    public void onLoginFacebookClick(View view) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Facebook login button clicked", properties);

        showMainActivity(null);
    }

    public void onLoginTwitterClick(View view) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Facebook login button clicked", properties);

        mTwitterAuthClient.authorize(this, new com.twitter.sdk.android.core.Callback<TwitterSession>() {

            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {

                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                final TwitterSession session = twitterSessionResult.data;

                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();

                Call<User> userResult = TwitterCore.getInstance().getApiClient().getAccountService().verifyCredentials(false, false);
                userResult.enqueue(new Callback<User>() {

                    @Override
                    public void failure(TwitterException e) {

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

                        showMainActivity(user);
                    }

                });


            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        mTwitterAuthClient.onActivityResult(requestCode, responseCode, intent);
    }

    private void showMainActivity(com.akvelon.mobilecenterandroiddemo.models.User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }
}
