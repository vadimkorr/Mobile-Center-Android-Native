package com.akvelon.mobilecenterandroiddemo;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.akvelon.mobilecenterandroiddemo.helpers.DateHelper;
import com.akvelon.mobilecenterandroiddemo.services.FitnessData;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    private Context mContext;
    private MyFitnessTask mFitnessTask;
    private TextView mCaloriesTextView;
    private TextView mStepsTextView;
    private TextView mDistanceTextView;
    private TextView mActiveTimeHourTextView;
    private TextView mActiveTimeMinuteTextView;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mStepsTextView = (TextView)view.findViewById(R.id.home_steps_value);
        mCaloriesTextView = (TextView)view.findViewById(R.id.home_calories_value);
        mDistanceTextView = (TextView)view.findViewById(R.id.home_distance_value);
        mActiveTimeHourTextView = (TextView)view.findViewById(R.id.home_active_time_hour_value);
        mActiveTimeMinuteTextView = (TextView)view.findViewById(R.id.home_active_time_minute_value);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (mFitnessTask == null || mFitnessTask.getStatus() == AsyncTask.Status.FINISHED) {
            mFitnessTask = new MyFitnessTask(mContext);
            mFitnessTask.execute();
        }
    }

    class MyFitnessTask extends FitnessAsyncTask {

        public MyFitnessTask(Context context) {
            super(context);
        }

        @Override
        protected void updateUI(List<FitnessData> dataList) {
            if (dataList.size() > 0) {
                FitnessData fitnessData = dataList.get(0);

                mStepsTextView.setText(String.valueOf(fitnessData.getSteps()));

                mCaloriesTextView.setText(String.valueOf((int)fitnessData.getCalories()));

                DecimalFormat df = new DecimalFormat("0.00");
                final int METERS_IN_KILOMETER = 1000;
                double km = fitnessData.getDistance() / METERS_IN_KILOMETER;
                mDistanceTextView.setText(df.format(km));

                final int MS_IN_MINUTE = 1000 * 60;
                final int MS_IN_HOUR = MS_IN_MINUTE * 60;
                int hours = fitnessData.getActiveTime() / MS_IN_HOUR;
                int minutes = (fitnessData.getActiveTime() - hours * MS_IN_HOUR) / MS_IN_MINUTE;
                mActiveTimeHourTextView.setText(String.valueOf(hours));
                mActiveTimeMinuteTextView.setText(String.valueOf(minutes));
            }
        }

        @Override
        protected Date startDate() {
            return DateHelper.today();
        }

        @Override
        protected Date endDate() {
            return new Date();
        }
    }
}
