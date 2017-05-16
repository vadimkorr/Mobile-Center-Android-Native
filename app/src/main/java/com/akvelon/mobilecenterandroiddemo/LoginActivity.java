package com.akvelon.mobilecenterandroiddemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.akvelon.mobilecenterandroiddemo.models.User;
import com.akvelon.mobilecenterandroiddemo.services.Analytics.AnalyticsService;
import com.akvelon.mobilecenterandroiddemo.services.Social.SocialService;

public class LoginActivity extends AppCompatActivity {

    private static final long ANIMATION_DURATION = 800;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private ImageView mLogoImage;
    private ImageView mMobileCenterImage;
    private ImageView mErrorImage;
    private TextView mErrorTitle;
    private TextView mErrorText;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLogoImage = (ImageView)findViewById(R.id.login_logo);
        mMobileCenterImage = (ImageView)findViewById(R.id.login_mobile_center);
        mErrorImage = (ImageView)findViewById(R.id.login_error_image);
        mErrorTitle = (TextView)findViewById(R.id.login_error_title);
        mErrorText = (TextView)findViewById(R.id.login_error_text);
    }

    public void getLocationPermission() {
        boolean isPermissionGranted = ContextCompat.checkSelfPermission(LoginActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (!isPermissionGranted) {
            ActivityCompat.requestPermissions(LoginActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else {
            Log.i("PERMISSIONS","Permissions were already granted");
            showMainActivity(mUser);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.i("PERMISSIONS","Permissions were granted");
                    // proceed to main screen
                    showMainActivity(mUser);
                } else {
                    showError();
                    Log.i("PERMISSIONS","Permissions were denied");
                }
                return;
            }
        }
    }

    public void onLoginFacebookClick(View view) {
        // track click event
        ((MyApplication)getApplication()).getAnalyticsService().trackLoginFacebookClick();

        // hide error in case if it was shown before
        hideError();

        // authorize using Facebook service
        SocialService facebookService = ((MyApplication)getApplication()).getFacebookService();
        facebookService.logIn(this, new SocialService.LogInCallback() {
            @Override
            public void onSuccess(User user) {
                // track social sign in success event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackSocialSignInResult(
                        AnalyticsService.SocialNetwork.FACEBOOK,
                        true,
                        null
                );
                mUser = user;
                getLocationPermission();
            }

            @Override
            public void onFailure(Error error) {
                // track social sign in error event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackSocialSignInResult(
                        AnalyticsService.SocialNetwork.FACEBOOK,
                        false,
                        error.getMessage()
                );

                showError();
            }
        });
    }

    public void onLoginTwitterClick(View view) {
        // track click event
        ((MyApplication)getApplication()).getAnalyticsService().trackLoginTwitterClick();

        // hide error in case if it was shown
        hideError();

        // authorize using Twitter service
        SocialService twitterService = ((MyApplication)getApplication()).getTwitterService();
        twitterService.logIn(this, new SocialService.LogInCallback() {
            @Override
            public void onSuccess(User user) {
                // track social sign in success event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackSocialSignInResult(
                        AnalyticsService.SocialNetwork.TWITTER,
                        true,
                        null
                );
                mUser = user;
                getLocationPermission();
            }

            @Override
            public void onFailure(Error error) {
                // track social sign in error event
                ((MyApplication)getApplicationContext()).getAnalyticsService().trackSocialSignInResult(
                        AnalyticsService.SocialNetwork.TWITTER,
                        false,
                        error.getMessage()
                );

                showError();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
        ((MyApplication)getApplication()).getTwitterService().onActivityResult(requestCode, responseCode, intent);
        ((MyApplication)getApplication()).getFacebookService().onActivityResult(requestCode, responseCode, intent);
    }

    private void showMainActivity(User user) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        intent.putExtra(MainActivity.ARG_USER, user);
        startActivity(intent);
        this.finish();
    }

    private void showError() {
        mErrorImage.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
        mErrorTitle.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
        mErrorText.animate().alpha(1).setDuration(ANIMATION_DURATION).start();

        mLogoImage.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
        mMobileCenterImage.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
    }

    private void hideError() {
        mErrorImage.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
        mErrorTitle.animate().alpha(0).setDuration(ANIMATION_DURATION).start();
        mErrorText.animate().alpha(0).setDuration(ANIMATION_DURATION).start();

        mLogoImage.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
        mMobileCenterImage.animate().alpha(1).setDuration(ANIMATION_DURATION).start();
    }
}
