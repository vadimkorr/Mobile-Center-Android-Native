package com.akvelon.mobilecenterandroiddemo.models;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ruslan on 5/12/17.
 */

public class User implements Parcelable {

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

    public User(Parcel source) {
        mFullName = source.readString();
        mAccessToken = source.readString();
        mImageUrlString = source.readString();
        mSocialNetwork = SocialNetwork.valueOf(source.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mFullName);
        dest.writeString(mAccessToken);
        dest.writeString(mImageUrlString);
        dest.writeString(mSocialNetwork.name());
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        public User[] newArray(int size) {
            return new User[size];
        }
    };

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
