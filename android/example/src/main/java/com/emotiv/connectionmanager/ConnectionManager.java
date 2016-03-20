package com.emotiv.connectionmanager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdk.IEE_Event_t;
import com.emotiv.insight.IEdkErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

//import com.emotiv.emotivcloud.EmotivCloudPrivate;
//import com.emotiv.insightapp.SplashScreen;
//import com.emotiv.recording.RecordingLiveEeg;

public class ConnectionManager {
    Context mContext;
    public static boolean isConnected = false, isInsightConnected = true;
    boolean lock = false;
    static boolean isRunEngineConnect = false;
    String tag = "ConnectionManager";
    public static int[] SignalData = new int[5];
    Timer timer;
    //UpdateStatusInterface fetchListener;

    public ConnectionManager(Context context) {
        this.mContext = context;
        connectToHeadset();
    }

    public boolean isConnected() {
        return isConnected;
    }

    private void setConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

//    public void setListener(UpdateStatusInterface listener) {
//        this.fetchListener = listener;
//    }

    private void connectToHeadset() {
        if (!isRunEngineConnect) {
            new Thread() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    super.run();
                    while (true) {
                        try {
                            int state = IEdk.IEE_EngineGetNextEvent();
                            if (state == IEdkErrorCode.EDK_OK.ToInt()) {
                                int eventType = IEdk.IEE_EmoEngineEventGetType();
                                if (eventType == IEE_Event_t.IEE_UserAdded.ToInt()) {
                                    setConnected(true);
                                    int userId = IEdk.IEE_EmoEngineEventGetUserId();
                                    IEdk.IEE_FFTSetWindowingType(userId, IEdk.IEE_WindowsType_t.IEE_BLACKMAN);
                                    lock = true;
                                    Log.e(tag, "Connected");
                                }
                                if (eventType == IEE_Event_t.IEE_UserRemoved.ToInt()) {
                                    setConnected(false);
                                    lock = false;
                                    if (isInsightConnected) SignalData = new int[5];
                                    else SignalData = new int[16];
                                    for (int m = 0; m < SignalData.length; m++)
                                        SignalData[m] = 0;
                                    Log.e(tag, "Disconnected");
                                }


                            }
                            if (!isConnected) {
                                handleConnect.sendEmptyMessage(0);
                            }
                            Thread.sleep(100);
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            }.start();
            timer = new Timer();
            timer.schedule(new TimerTask() {
                               @Override
                               public void run() {
                                   handleConnect.sendEmptyMessage(1); // signal status and battery
                               }
                           },
                    500, 500);
            isRunEngineConnect = true;
        }
    }

    public void update() {
//        if (this.fetchListener == null) return;
//        this.fetchListener.updateSignalStatus(SignalData);
//        this.fetchListener.updateSensorStatus();
//        this.fetchListener.updateBatteryStatus();
    }


    Handler handleConnect = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    update();
                    break;
                case 0:
                    getInsights();
                    getEpocPlus();
                    break;
            }
        }
    };

    public void refreshDevices() {
        IEdk.IEE_RefreshScanDevice();
    }

    //Return the list of Insights
    public List<String> getInsights() {
        int insightNumber = IEdk.IEE_GetInsightDeviceCount();
//        Log.v(ConnectionManager.class.getSimpleName(), "Number Insight: " + insightNumber);

        List<String> results = new ArrayList<String>();
        if (insightNumber > 0) {
            for (int i = 0; i < insightNumber; i++) {
                String insightName = IEdk.IEE_GetInsightDeviceName(i);
                results.add(insightName);
//                Log.d("ConnectionManager", "Insight " + insightName + " :" +IEdk.IEE_GetInsightDeviceState(i));
            }
        }
        return results;
    }

    //Return the list of Epoc Plus
    public List<String> getEpocPlus() {
        int epocNumber = IEdk.IEE_GetEpocPlusDeviceCount();
//        Log.v(ConnectionManager.class.getSimpleName(), "Number Epoc+: "+ epocNumber);
        List<String> results = new ArrayList<String>();
        if (epocNumber > 0) {
            for (int i = 0; i < epocNumber; i++) {
                String epocName = IEdk.IEE_GetEpocPlusDeviceName(i);
                results.add(epocName);
            }
        }
        return results;
    }

    public void connectToInsight(int position) {
        int insightNumber = IEdk.IEE_GetInsightDeviceCount();
        if (insightNumber != 0) {
            if (!lock) {
                if (IEdk.IEE_ConnectInsightDevice(position)) {
                    isInsightConnected = true;
                    lock = true;
                    handleConnect.sendEmptyMessage(1);
                }
            }
        }
    }

    public void connectToEpocPlus(int position) {
        int epocNumber = IEdk.IEE_GetEpocPlusDeviceCount();
        if (epocNumber != 0) {
            if (!lock) {
                if (IEdk.IEE_ConnectEpocPlusDevice(position, false)) {
                    isInsightConnected = false;
                    lock = true;
                    handleConnect.sendEmptyMessage(1);
                }
            }
        }
    }

}
