package com.akvelon.mobilecenterandroiddemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.akvelon.mobilecenterandroiddemo.helpers.DateHelper;
import com.akvelon.mobilecenterandroiddemo.models.User;
import com.akvelon.mobilecenterandroiddemo.services.Fitness.FitnessData;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {
    public static final String ARG_USER = "user";

    private User mUser;
    private Context mContext;
    private MyFitnessTask mFitnessTask;
    private FitnessData mFitnessData;
    private Date mFitnessLastUpdatedDate;
    private TextView mCaloriesTextView;
    private TextView mStepsTextView;
    private TextView mDistanceTextView;
    private TextView mActiveTimeHourTextView;
    private TextView mActiveTimeMinuteTextView;
    private TextView mGreetingTextView;
    private ImageView mUserImage;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of this fragment
     *
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(User user) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUser = getArguments().getParcelable(ARG_USER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mGreetingTextView = (TextView)view.findViewById(R.id.home_greeting);
        mUserImage = (ImageView)view.findViewById(R.id.home_user_image);
        mStepsTextView = (TextView)view.findViewById(R.id.home_steps_value);
        mCaloriesTextView = (TextView)view.findViewById(R.id.home_calories_value);
        mDistanceTextView = (TextView)view.findViewById(R.id.home_distance_value);
        mActiveTimeHourTextView = (TextView)view.findViewById(R.id.home_active_time_hour_value);
        mActiveTimeMinuteTextView = (TextView)view.findViewById(R.id.home_active_time_minute_value);

        updateUserData();
        updateFitnessValues();

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

        final int MS_IN_MINUTE = 60 * 1000;
        long outdatedTimeout = 1 * MS_IN_MINUTE;
        boolean dataOutdated = mFitnessLastUpdatedDate == null || mFitnessLastUpdatedDate.getTime() < new Date().getTime() - outdatedTimeout;
        boolean asyncTaskNotRunning = mFitnessTask == null || mFitnessTask.getStatus() == AsyncTask.Status.FINISHED;
        if (dataOutdated && asyncTaskNotRunning) {
            mFitnessTask = new MyFitnessTask(mContext);
            mFitnessTask.execute();
        }
    }

    private void updateUserData() {
        String greeting = "HI, " + mUser.getFullName();
        mGreetingTextView.setText(greeting);

        // loading user image
        Picasso.with(mContext)
                .load(mUser.getImageUrlString())
                .into(mUserImage, new Callback() {
                    @Override
                    public void onSuccess() {
                        // creating round image
                        Bitmap imageBitmap = ((BitmapDrawable) mUserImage.getDrawable()).getBitmap();
                        RoundedBitmapDrawable imageDrawable = RoundedBitmapDrawableFactory.create(getResources(), imageBitmap);
                        imageDrawable.setCircular(true);
                        imageDrawable.setCornerRadius(Math.max(imageBitmap.getWidth(), imageBitmap.getHeight()) / 2.0f);
                        mUserImage.setImageDrawable(imageDrawable);
                    }
                    @Override
                    public void onError() {
                        // do nothing
                    }
                });
    }

    private void resetFitnessValues() {
        mStepsTextView.setText("0");
        mCaloriesTextView.setText("0");
        mDistanceTextView.setText("0");
        mActiveTimeHourTextView.setText("0");
        mActiveTimeMinuteTextView.setText("0");
    }

    private void updateFitnessValues() {
        if (mFitnessData == null) {
            resetFitnessValues();
            return;
        }

        // steps
        mStepsTextView.setText(String.valueOf(mFitnessData.getSteps()));

        // calories
        mCaloriesTextView.setText(String.valueOf((int)mFitnessData.getCalories()));

        // distance
        DecimalFormat df = new DecimalFormat("0.00");
        final int METERS_IN_KILOMETER = 1000;
        double km = mFitnessData.getDistance() / METERS_IN_KILOMETER;
        mDistanceTextView.setText(df.format(km));

        // active time
        final int MS_IN_MINUTE = 1000 * 60;
        final int MS_IN_HOUR = MS_IN_MINUTE * 60;
        int hours = mFitnessData.getActiveTime() / MS_IN_HOUR;
        int minutes = (mFitnessData.getActiveTime() - hours * MS_IN_HOUR) / MS_IN_MINUTE;
        mActiveTimeHourTextView.setText(String.valueOf(hours));
        mActiveTimeMinuteTextView.setText(String.valueOf(minutes));
    }

    class MyFitnessTask extends FitnessAsyncTask {

        public MyFitnessTask(Context context) {
            super(context);
        }

        @Override
        protected void updateUI(List<FitnessData> dataList) {
            // if dataList is not null, then result is success
            boolean success = dataList != null;

            // track Google Fit retrieve result event
            ((MyApplication)mContext.getApplicationContext()).getAnalyticsService().trackGoogleFitRetrieveResult(
                    success,
                    null
            );

            if (dataList != null && dataList.size() > 0) {
                mFitnessData = dataList.get(0);
                updateFitnessValues();

                // update last update date to ensure outdating
                mFitnessLastUpdatedDate = new Date();
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
