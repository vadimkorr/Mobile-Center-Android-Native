package com.akvelon.mobilecenterandroiddemo.services;

/**
 * Created by ruslan on 5/11/17.
 */

public class ServicesFactory {
    public FitnessService getFitnessService() {
        return new GoogleFitService();
    }
}
