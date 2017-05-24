package com.akvelon.mobilecenterandroiddemo.services.Social;

import android.app.Activity;
import android.content.Intent;

import com.akvelon.mobilecenterandroiddemo.models.User;

public interface SocialService {
    interface LogInCallback {
        void onSuccess(User user);
        void onFailure(Error error);
    }

    void logIn(Activity activity, LogInCallback callback);

    void onActivityResult(int requestCode, int responseCode, Intent intent);
}
