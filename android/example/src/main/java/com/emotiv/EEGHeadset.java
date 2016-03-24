package com.emotiv;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import com.emotiv.connectionmanager.ConnectionManager;
import com.emotiv.widget.ChooseHeadsetDialog;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import java.util.HashMap;
import java.util.Map;

import com.emotiv.insight.IEdk;
import com.emotiv.insight.IEdkErrorCode;
import com.emotiv.insight.IEdk.IEE_DataChannel_t;
import com.emotiv.insight.IEdk.IEE_Event_t;;

/**
 * Created by kbrockman on 16-03-23.
 */
public class EEGHeadset extends Activity {

    private static final int REQUEST_ENABLE_BT = 1;
    private Map<String, IEE_DataChannel_t> _channelList;
    private BluetoothAdapter mBluetoothAdapter;

    public EEGHeadset() {
        setChannelList();
    }

    public void Connect(){
        Log.d("HEADSET", "Connect to EPOC+");

        //Basically run through emotiv-sample
        //Try to skip having to do dialog stuff?

        //Set up BT manager
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }
    }

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
