package com.emotiv;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.emotiv.connectionmanager.ConnectionManager;
import com.emotiv.widget.ChooseHeadsetDialog;

import android.app.Service;
import android.content.ComponentName;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.emotiv.insight.IEdk.IEE_DataChannel_t;
import com.emotiv.insight.IEdk.IEE_Event_t;
import com.musethereal.MainActivity;;
import android.os.Process;

/**
 * Created by kbrockman on 16-03-23.
 */
public class EEGHeadset extends Service {
    private String debugTag = "EEGHeadset";
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    // Handler that receives messages from the thread
    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            // Normally we would do some work here, like download a file.
            // For our sample, we just sleep for 5 seconds.
            try {
                Log.d(debugTag, "handleMessage");
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                // Restore interrupt status.
                Thread.currentThread().interrupt();
            }
            // Stop the service using the startId, so that we don't stop
            // the service in the middle of handling another job
            stopSelf(msg.arg1);
        }
    }

    @Override
    public void onCreate() {
        // Start up the thread running the service.  Note that we create a
        // separate thread because the service normally runs in the process's
        // main thread, which we don't want to block.  We also make it
        // background priority so CPU-intensive work will not disrupt our UI.
        HandlerThread thread = new HandlerThread("ServiceStartArguments", Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        // Get the HandlerThread's Looper and use it for our Handler
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // For each start request, send a message to start a job and deliver the
        // start ID so we know which request we're stopping when we finish the job
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        // If we get killed, after returning from here, restart
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {

    }

    public EEGHeadset() {
        setChannelList();
    }

    private Map<String, IEE_DataChannel_t> _channelList;
    private void setChannelList() {
        Log.d(debugTag, "Setup Channel List");
        _channelList = new HashMap<String, IEE_DataChannel_t>();
        _channelList.put("AF3", IEE_DataChannel_t.IED_AF3);
        _channelList.put("T7", IEE_DataChannel_t.IED_T7);
        _channelList.put("Pz", IEE_DataChannel_t.IED_Pz);
        _channelList.put("T8", IEE_DataChannel_t.IED_T8);
        _channelList.put("AF4", IEE_DataChannel_t.IED_AF4);
        _channelList.put("O2", IEE_DataChannel_t.IED_O2);
        _channelList.put("F7", IEE_DataChannel_t.IED_F7);
        _channelList.put("P8", IEE_DataChannel_t.IED_P8);
        _channelList.put("F3", IEE_DataChannel_t.IED_F3);
        _channelList.put("FC5", IEE_DataChannel_t.IED_FC5);
        _channelList.put("FC6", IEE_DataChannel_t.IED_FC6);
        _channelList.put("F4", IEE_DataChannel_t.IED_F4);
        _channelList.put("P7", IEE_DataChannel_t.IED_P7);
        _channelList.put("F8", IEE_DataChannel_t.IED_F8);
        _channelList.put("O1", IEE_DataChannel_t.IED_O1);
    }
}
