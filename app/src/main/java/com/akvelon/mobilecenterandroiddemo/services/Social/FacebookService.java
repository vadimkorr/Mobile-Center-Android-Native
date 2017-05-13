package com.akvelon.mobilecenterandroiddemo.services.Social;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.akvelon.mobilecenterandroiddemo.services.Social.SocialService;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

/**
 * Created by ruslan on 5/13/17.
 */

public class FacebookService implements SocialService, FacebookCallback<LoginResult> {

    private CallbackManager mFacebookCallbackManager;
    private LogInCallback mCallback;

    public FacebookService() {
        mFacebookCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, this);
    }

    @Override
    public void logIn(Activity activity, LogInCallback callback) {
        mCallback = callback;
        LoginManager.getInstance().logInWithReadPermissions(
                activity,
                Arrays.asList("user_photos", "public_profile")
        );
    }

    @Override
    public void onActivityResult(int requestCode, int responseCode, Intent intent) {
        mFacebookCallbackManager.onActivityResult(requestCode, responseCode, intent);
    }

    @Override
    public void onSuccess(LoginResult loginResult) {
        fetchUserProfile(loginResult);
    }

    @Override
    public void onCancel() {
        Error error = new Error("User has canceled log in process");
        mCallback.onFailure(error);
    }

    @Override
    public void onError(FacebookException exception) {
        Error error = new Error(exception.getLocalizedMessage());
        mCallback.onFailure(error);
    }

    private void fetchUserProfile(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        Log.v("LoginActivity", response.toString());

                        if (object == null) {
                            Error error = new Error(response.getError().getErrorMessage());
                            mCallback.onFailure(error);
                        }

                        try {

                            String userID = (String) object.get("id");
                            String userName = (String) object.get("name");
                            String accessToken = loginResult.getAccessToken().getToken();
                            String imageUrl = "https://graph.facebook.com/" + userID+ "/picture?type=large";

                            com.akvelon.mobilecenterandroiddemo.models.User user;
                            user = new com.akvelon.mobilecenterandroiddemo.models.User(
                                    userName,
                                    accessToken,
                                    imageUrl,
                                    com.akvelon.mobilecenterandroiddemo.models.User.SocialNetwork.FACEBOOK
                            );

                            mCallback.onSuccess(user);

                        } catch (JSONException e) {
                            Error error = new Error(e.getLocalizedMessage());
                            mCallback.onFailure(error);
                        }
                    }
                }
        );

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,picture");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
