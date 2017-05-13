package com.akvelon.mobilecenterandroiddemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.akvelon.mobilecenterandroiddemo.services.Social.SocialService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



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

        SocialService facebookService = ((MyApplication)getApplication()).getFacebookService();
        facebookService.logIn(this, new SocialService.LogInCallback() {
            @Override
            public void onSuccess(com.akvelon.mobilecenterandroiddemo.models.User user) {

            }

            @Override
            public void onFailure(Error error) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    public void onLoginTwitterClick(View view) {
        Map<String, String> properties = new HashMap<String, String>() {{
            put("Page", "Login");
            put("Category", "Clicks");
        }};
        Analytics.trackEvent("Facebook login button clicked", properties);

        SocialService twitterService = ((MyApplication)getApplication()).getTwitterService();
        twitterService.logIn(this, new SocialService.LogInCallback() {
            @Override
            public void onSuccess(com.akvelon.mobilecenterandroiddemo.models.User user) {
                showMainActivity(user);
            }

            @Override
            public void onFailure(Error error) {

            }

            @Override
            public void onCancel() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        ((MyApplication)getApplication()).getTwitterService().onActivityResult(requestCode, responseCode, intent);
        ((MyApplication)getApplication()).getFacebookService().onActivityResult(requestCode, responseCode, intent);
    }

    private void showMainActivity(com.akvelon.mobilecenterandroiddemo.models.User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        startActivity(intent);
        this.finish();
    }
}
