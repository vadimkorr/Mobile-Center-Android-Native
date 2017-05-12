package com.akvelon.mobilecenterandroiddemo;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import com.akvelon.mobilecenterandroiddemo.helpers.DateHelper;
import com.akvelon.mobilecenterandroiddemo.services.FitnessData;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.microsoft.azure.mobile.analytics.Analytics;
import com.microsoft.azure.mobile.crashes.Crashes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    public static final String TAG = "StepCounter";

    private LineChart mChart;
    private RadioGroup mRadioGroup;
    private Context mContext;
    private MyFitnessTask mFitnessTask;
    private List<FitnessData> mFitnessDataList;
    private Date mFitnessLastUpdatedDate;
    private FitnessDataType mSelectedFitnessType = FitnessDataType.STEPS;

    private enum FitnessDataType {
        STEPS,
        CALORIES,
        DISTANCE,
        ACTIVE_TIME
    }

    public StatsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StatsFragment.
     */
    public static StatsFragment newInstance() {
        StatsFragment fragment = new StatsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stats, container, false);

        mRadioGroup = (RadioGroup) view.findViewById(R.id.statistics_radio_buttons);
        mRadioGroup.setOnCheckedChangeListener(this);

        Button crashButton = (Button) view.findViewById(R.id.stats_crash_button);
        crashButton.setOnClickListener(this);

        mChart = (LineChart) view.findViewById(R.id.statistics_chart);
        initChart();
        updateChartValues();

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

    private void initChart() {
        // disable touch gestures
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleXEnabled(false);
        mChart.setScaleYEnabled(false);
        mChart.getDescription().setEnabled(false);
        mChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getAxisLeft().setLabelCount(5, false);

        mChart.getAxisRight().setEnabled(false);

        mChart.setDrawBorders(false);
        mChart.getLegend().setEnabled(false);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.stats_crash_button:
                Analytics.trackEvent("Crash button clicked");
                Crashes.generateTestCrash();
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
        switch (checkedId) {
            case R.id.stats_radio_steps:
                mSelectedFitnessType = FitnessDataType.STEPS;
                break;
            case R.id.stats_radio_calories:
                mSelectedFitnessType = FitnessDataType.CALORIES;
                break;
            case R.id.stats_radio_distance:
                mSelectedFitnessType = FitnessDataType.DISTANCE;
                break;
            case R.id.stats_radio_active_time:
                mSelectedFitnessType = FitnessDataType.ACTIVE_TIME;
                break;
        }
        updateChartValues();
    }

    private void updateChartValues() {
        if (mFitnessDataList == null || mFitnessDataList.size() == 0) {
            return;
        }

        // first timestamp in our data set
        long referenceTimestamp = mFitnessDataList.get(0).getDate().getTime();
        List<Entry> values = getEntryValues(mFitnessDataList, mSelectedFitnessType, referenceTimestamp);

        // create a data with values
        LineDataSet dataSet = lineDataSet(values);
        // create a data object with the data set
        LineData data = lineData(dataSet);

        configureXAxisFormatter(referenceTimestamp);

        // set data
        mChart.setData(data);
        mChart.invalidate();
    }

    private List<Entry> getEntryValues(List<FitnessData> fitnessDataList, FitnessDataType fitnessType, long referenceTimestamp) {
        ArrayList<Entry> values = new ArrayList<Entry>();
        for (FitnessData fitnessData : fitnessDataList) {
            float x = fitnessData.getDate().getTime() - referenceTimestamp;
            float y = (float)getAppropriateFitnessValue(fitnessData, fitnessType);
            values.add(new Entry(x, y));
        }
        return values;
    }

    private void configureXAxisFormatter(long referenceTimestamp) {
        ChartDateAxisValueFormatter xAxisFormatter = new ChartDateAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = mChart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
    }

    private LineDataSet lineDataSet(List<Entry> values) {
        // create a data set and give it a type
        LineDataSet set = new LineDataSet(values, "DataSet");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(ColorTemplate.getHoloBlue());
        set.setValueTextColor(ColorTemplate.getHoloBlue());
        set.setLineWidth(1.5f);
        set.setDrawCircles(false);
        set.setDrawValues(false);
        set.setDrawFilled(true);
        set.setFillAlpha(65);
        set.setFillColor(ColorTemplate.getHoloBlue());
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setDrawCircleHole(false);

        return set;
    }

    private LineData lineData(LineDataSet dataSet) {
        // create a data object with the datasets
        LineData data = new LineData(dataSet);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        return data;
    }

    private double getAppropriateFitnessValue(FitnessData fitnessData, FitnessDataType neededType) {
        switch (neededType) {
            case STEPS:
                return fitnessData.getSteps();
            case CALORIES:
                return fitnessData.getCalories();
            case DISTANCE:
                final int METERS_IN_KILOMETER = 1000;
                return fitnessData.getDistance() / METERS_IN_KILOMETER;
            default:
                final int MS_IN_HOUR = 1000 * 60 * 60;
                return (double)fitnessData.getActiveTime() / MS_IN_HOUR;
        }
    }

    class MyFitnessTask extends FitnessAsyncTask {

        public MyFitnessTask(Context context) {
            super(context);
        }

        @Override
        protected void updateUI(List<FitnessData> dataList) {
            if (dataList.size() > 0) {
                mFitnessDataList = dataList;
                updateChartValues();

                // update last update date to ensure outdating
                mFitnessLastUpdatedDate = new Date();
            }
        }

        @Override
        protected Date startDate() {
            int daysAgo = 4;
            return DateHelper.date(daysAgo);
        }

        @Override
        protected Date endDate() {
            return new Date();
        }
    }
}
