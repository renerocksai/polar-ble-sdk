package com.polar.polarsdkecghrdemo;

import android.content.Context;
import android.graphics.Color;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeriesFormatter;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import polar.com.sdk.api.model.PolarHrData;

/**
 * Implements two series for HR and RR using time for the x values.
 */
public class TimePlotter {
    private static final int NVALS = 300;  // 5 min

    String title;
    private String TAG = "Polar_Plotter";
    private double RR_SCALE = .1;
    private PlotterListener listener;
    private Context context;
    private XYSeriesFormatter hrFormatter;
    private XYSeriesFormatter rrFormatter;
    private SimpleXYSeries hrSeries;
    private SimpleXYSeries rrSeries;
    private Double[] xHrVals = new Double[NVALS];
    private Double[] yHrVals = new Double[NVALS];
    private Double[] xRrVals = new Double[NVALS];
    private Double[] yRrVals = new Double[NVALS];

    public TimePlotter(Context context, String title) {
        this.context = context;
        this.title = title;  // Not used
        Date now = new Date();
        double endTime = now.getTime();
        double startTime = endTime - NVALS * 1000;
        double delta = (endTime - startTime) / (NVALS - 1);

        // Specify initial values to keep it from auto sizing
        for (int i = 0; i < NVALS; i++) {
            xHrVals[i] = new Double(startTime + i * delta);
            yHrVals[i] = new Double(60);
        }

        hrFormatter = new LineAndPointFormatter(Color.RED,
                null, null, null);
        hrFormatter.setLegendIconEnabled(false);
        hrSeries = new SimpleXYSeries(Arrays.asList(xHrVals),
                Arrays.asList(yHrVals),
                "HR");
    }

    public SimpleXYSeries getHrSeries() {
        return (SimpleXYSeries) hrSeries;
    }

    public XYSeriesFormatter getHrFormatter() {
        return hrFormatter;
    }


    /**
     * Implements a strip chart by moving series data backwards and adding
     * new data at the end.
     *
     * @param polarHrData The HR data that came in.
     */
    public void addValues(PolarHrData polarHrData) {
        Date now = new Date();
        long time = now.getTime();
        for (int i = 0; i < NVALS - 1; i++) {
            xHrVals[i] = xHrVals[i + 1];
            yHrVals[i] = yHrVals[i + 1];
            hrSeries.setXY(xHrVals[i], yHrVals[i], i);
        }
        xHrVals[NVALS - 1] = new Double(time);
        yHrVals[NVALS - 1] = new Double(polarHrData.hr);
        hrSeries.setXY(xHrVals[NVALS - 1], yHrVals[NVALS - 1], NVALS - 1);

        listener.update();
    }

    public void setListener(PlotterListener listener) {
        this.listener = listener;
    }
}
