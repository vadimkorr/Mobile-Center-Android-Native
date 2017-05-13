package com.akvelon.mobilecenterandroiddemo;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessData;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessService;

import java.util.Date;
import java.util.List;

/**
 * Created by ruslan on 5/12/17.
 */

public abstract class FitnessAsyncTask extends AsyncTask<String, String, List<FitnessData>> {
    Context mContext;
    private ProgressDialog mProgDailog;

    public FitnessAsyncTask(Context context) {
        mContext = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        showProgressDialog();
    }

    protected List<FitnessData> doInBackground(String... params) {
        FitnessService fitnessService = ((MyApplication) mContext.getApplicationContext()).getFitnessService();
        List<FitnessData> result = fitnessService.fetchData(startDate(), endDate());
        return result;
    }

    @Override
    protected void onPostExecute(List<FitnessData> result) {
        super.onPostExecute(result);

        updateUI(result);
        hideProgressDialog();
    }

    protected abstract void updateUI(List<FitnessData> dataList);

    protected abstract Date startDate();

    protected abstract Date endDate();

    protected void showProgressDialog() {
        mProgDailog = new ProgressDialog(mContext);
        mProgDailog.setMessage(mContext.getString(R.string.progress_dialog_loading));
        mProgDailog.setCancelable(true);
        mProgDailog.show();
    }

    protected void hideProgressDialog() {
        mProgDailog.dismiss();
    }
}
