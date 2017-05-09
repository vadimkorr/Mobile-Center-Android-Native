package com.akvelon.mobilecenterandroiddemo;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

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
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StatsFragment extends Fragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    private LineChart mChart;
    private RadioGroup mRadioGroup;

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

        return view;
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

        setData(50, 100);
        mChart.invalidate();
    }

    private void setData(int count, float range) {

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());

        ArrayList<Entry> values = new ArrayList<Entry>();

        float from = now;

        // count = hours
        float to = now + count;

        // increment by 1 hour
        for (float x = from; x < to; x++) {

            float y = getRandom(range, 50);
            values.add(new Entry(x, y)); // add one entry per hour
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(values, "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setDrawFilled(true);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);
    }

    protected float getRandom(float range, float startsfrom) {
        return (float) (Math.random() * range) + startsfrom;
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
                showStepsData();
                break;
            case R.id.stats_radio_calories:
                showCaloriesData();
                break;
            case R.id.stats_radio_distance:
                showDistanceData();
                break;
            case R.id.stats_radio_active_time:
                showActiveTimeData();
                break;
        }
    }

    private void showStepsData() {
        setData(45, 100);
        mChart.invalidate();
    }

    private void showCaloriesData() {
        setData(45, 100);
        mChart.invalidate();
    }

    private void showDistanceData() {
        setData(45, 100);
        mChart.invalidate();
    }

    private void showActiveTimeData() {
        setData(45, 100);
        mChart.invalidate();
    }
}
