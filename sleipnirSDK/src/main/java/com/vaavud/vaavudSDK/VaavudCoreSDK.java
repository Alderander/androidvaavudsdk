package com.vaavud.vaavudSDK;

import android.content.Context;

import com.vaavud.vaavudSDK.listener.SpeedListener;
import com.vaavud.vaavudSDK.listener.StatusListener;
import com.vaavud.vaavudSDK.mjolnir.MjolnirController;
import com.vaavud.vaavudSDK.orientation.OrientationController;
import com.vaavud.vaavudSDK.sleipnir.SleipnirController;

/**
 * Created by aokholm on 15/01/16.
 */
public class VaavudCoreSDK {

    Context context;

    private SpeedListener speedListener;
    private StatusListener statusListener;

    private SleipnirController _sleipnir;
    private MjolnirController _mjolnir;
    private OrientationController _orientation;


    public VaavudCoreSDK(Context context) {
        this.context = context;
    }

    public void startMjolnir() throws VaavudError {
        orientation().start();
        orientation().setMjolnir(true);
        mjolnir().start();
    }

    public void stopMjolnir() {
        mjolnir().stop();
    }

    public void startSleipnir() throws VaavudError {
        orientation().setMjolnir(false);
        orientation().setHeadingListener(sleipnir());

        orientation().start();
        sleipnir().start();
    }

    public void stopSleipnir() {
        sleipnir().stop();
        orientation().stop();
    }

    private MjolnirController mjolnir() {
        if (_mjolnir == null) {
            _mjolnir = new MjolnirController(context, orientation());
        }
        return _mjolnir;
    }

    private SleipnirController sleipnir() {
        if (_sleipnir == null) {
            _sleipnir = new SleipnirController(context);
        }
        return _sleipnir;
    }

    private OrientationController orientation() {
        if (_orientation == null) {
            _orientation = new OrientationController(context);
        }
        return _orientation;
    }
}
