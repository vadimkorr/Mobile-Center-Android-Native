package com.akvelon.mobilecenterandroiddemo.services.Fitness;

import java.util.Date;

/**
 * Created by ruslan on 5/11/17.
 */

public class FitnessData {

    private final int mSteps;
    private final double mCalories;
    private final double mDistance;
    private final int mActiveTime;
    private final Date mDate;

    public FitnessData(int steps, double calories, double distance, int activeTime, Date date) {
        mSteps = steps;
        mCalories = calories;
        mDistance = distance;
        mActiveTime = activeTime;
        mDate = date;
    }

    public Date getDate() {
        return mDate;
    }

    public int getSteps() {
        return mSteps;
    }

    public double getCalories() {
        return mCalories;
    }

    public double getDistance() {
        return mDistance;
    }

    public int getActiveTime() {
        return mActiveTime;
    }
}
