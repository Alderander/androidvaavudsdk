package com.vaavud.vaavudSDK.core.mjolnir;

import android.content.Context;

import com.vaavud.vaavudSDK.core.listener.SpeedListener;
import com.vaavud.vaavudSDK.core.listener.StatusListener;
import com.vaavud.vaavudSDK.core.model.FreqAmp;
import com.vaavud.vaavudSDK.core.model.MeasureStatus;
import com.vaavud.vaavudSDK.core.model.event.SpeedEvent;
import com.vaavud.vaavudSDK.core.orientation.OrientationController;

public class MjolnirController implements FrequencyReceiver {

    private static final String TAG = "SDK:MjolnirCore";
    private static final double FREQUENCY_FACTOR = 1.07D;
    private static final double FREQUENCY_START = 0.238D;
    private MeasureStatus status;

    private SpeedListener speedListener;
    private StatusListener statusListener;
    private MagneticFieldSensorManager myMagneticFieldSensorManager;
    private OrientationController orientation;

    private MagneticDataManager dataManager;
    private FFTManager myFFTManager;


    public MjolnirController(Context context, OrientationController orientation) {
        this.orientation = orientation;
        dataManager = new MagneticDataManager();
        myMagneticFieldSensorManager = new MagneticFieldSensorManager(context, dataManager);
        myFFTManager = new FFTManager(dataManager, this);

    }

    public void start() {
        clearData();
        myMagneticFieldSensorManager.startLogging();
        myFFTManager.start();
        status = MeasureStatus.MEASURING;
    }

    public void stop() {
        myMagneticFieldSensorManager.stopLogging();
        clearData();
        myFFTManager.stop();
    }

    public void clearData() {
        dataManager.clearData();
        myMagneticFieldSensorManager.clear();
    }

    private void updateMeasureStatus() {
        if (statusListener!=null) {
            MeasureStatus newStatus = MeasureStatus.MEASURING;
            if (orientation.isAvailable() && !orientation.isVertical()) {
                newStatus = MeasureStatus.KEEP_VERTICAL;
            }
            if (!status.equals(newStatus)) {
                statusListener.statusChanged(newStatus);
            }
        }
    }

    @Override
    public void newFrequency(FreqAmp data) {
        double windspeed = FREQUENCY_FACTOR * data.frequency + FREQUENCY_START;
        if (data.frequency > 17.65D && data.frequency < 28.87D) {
            windspeed = windspeed + -0.068387D * Math.pow((data.frequency - 23.2667D), 2) + 2.153493D;
        }
        updateMeasureStatus();
        speedListener.speedChanged(new SpeedEvent(data.time, (float) windspeed));
    }

    public void setSpeedListener(SpeedListener speedListener) {
        this.speedListener = speedListener;
    }

    public void setStatusListener(StatusListener _statusListener) {
        statusListener = _statusListener;
    }
}
