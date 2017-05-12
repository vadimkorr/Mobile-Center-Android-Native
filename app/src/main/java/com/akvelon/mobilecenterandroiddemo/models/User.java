package com.akvelon.mobilecenterandroiddemo.models;

/**
 * Created by ruslan on 5/12/17.
 */

public class User {

    public enum SocialNetwork {
        FACEBOOK,
        TWITTER
    }

    private String mFullName;
    private String mAccessToken;
    private String mImageUrlString;
    private SocialNetwork mSocialNetwork;

    public User(String fullName, String accessToken, String imageUrlString, SocialNetwork socialNetwork) {
        mFullName = fullName;
        mAccessToken = accessToken;
        mImageUrlString = imageUrlString;
        mSocialNetwork = socialNetwork;
    }

    public String getFullName() {
        return mFullName;
    }

    public String getAccessToken() {
        return mAccessToken;
    }

    public String getImageUrlString() {
        return mImageUrlString;
    }

    public SocialNetwork getSocialNetwork() {
        return mSocialNetwork;
    }
}
