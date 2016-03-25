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
import android.widget.AdapterView;
import android.widget.Button;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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
    private BluetoothAdapter mBluetoothAdapter;
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

        Log.d(debugTag, "Set EPOC+ channel list");
        setChannelList();

        Log.d(debugTag, "Setup BT Manager...");
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Log.d(debugTag, "Bluetooth not enabled, sending request to enable");
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBtIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(enableBtIntent);
        } else {
            Log.d(debugTag, "Bluetooth enabled");
        }

        Log.d(debugTag, "Get EPOC+");
        int wat = IEdk.IEE_EngineConnect(EEGHeadset.this, "");
        Log.d(debugTag, "Instantiate connection manager");
        final ConnectionManager connectionManager = new ConnectionManager(EEGHeadset.this);
        connectionManager.refreshDevices();
        List<String> devices = connectionManager.getEpocPlus();
        Log.d(debugTag, "Devices Found: " + devices.size());
        Log.d(debugTag, "EPOC+ 0: " + connectionManager.isConnected());

        Thread processingThread=new Thread()
        {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                super.run();
                while(true)
                {
                    try
                    {
                        //handler.sendEmptyMessage(0);
                        //handler.sendEmptyMessage(1);
//						if(isEnablGetData && isEnableWriteFile)handler.sendEmptyMessage(2);
                        Log.d(debugTag, "Trying to send messages to thread");
                        Log.d(debugTag, "Connection state: " + connectionManager.isConnected);
                        if(connectionManager.isConnected){
                            handler.sendEmptyMessage(2);
                        }
                        Thread.sleep(1000);
                    }

                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                }
            }
        };
        processingThread.start();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case 0:
//                    Log.d("HANDLER", "CASE 0");
//                    int state = IEdk.IEE_EngineGetNextEvent();
//                    if (state == IEdkErrorCode.EDK_OK.ToInt()) {
//                        int eventType = IEdk.IEE_EmoEngineEventGetType();
//                        userId = IEdk.IEE_EmoEngineEventGetUserId();
//                        if(eventType == IEE_Event_t.IEE_UserAdded.ToInt()){
//                            Log.e("SDK","User added");
//                            IEdk.IEE_FFTSetWindowingType(userId, IEdk.IEE_WindowsType_t.IEE_BLACKMAN);
//                            isEnablGetData = true;
//                        }
//                        if(eventType == IEE_Event_t.IEE_UserRemoved.ToInt()){
//                            Log.e("SDK","User removed");
//                            isEnablGetData = false;
//                        }
//                    }

                    break;
                case 1:
//                    Log.d("HANDLER", "CASE 1");
//                    int number = IEdk.IEE_GetInsightDeviceCount();
//                    if(number != 0) {
//                        if(!lock){
//                            lock = true;
//                            IEdk.IEE_ConnectInsightDevice(0);
//                        }
//                    }
//                    else lock = false;
                    break;
                case 2:
                    Log.d("HANDLER", "CASE 2");
                    IEE_DataChannel_t[] channels = (IEE_DataChannel_t[]) _channelList.values().toArray();
                    for(int i=0; i < channels.length; i++)
                    {
                        double[] data = IEdk.IEE_GetAverageBandPowers(channels[i]);
                        if(data != null && data.length == 5){
                            try {
                                Log.d(debugTag, "You could get data");
//                                motion_writer.write(Name_Channel[i] + ",");
//                                for(int j=0; j < data.length;j++)
//                                    addData(data[j]);
//                                motion_writer.newLine();
                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    break;
            }

        }

    };


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

    }

    private Map<String, IEE_DataChannel_t> _channelList;
    private void setChannelList() {
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
