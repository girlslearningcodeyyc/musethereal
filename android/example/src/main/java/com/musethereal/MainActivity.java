package com.musethereal;

import android.app.Activity;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import com.felhr.serialportexample.UsbService;
import android.widget.EditText;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

import com.emotiv.EEGHeadset;
import com.felhr.serialportexample.DressController;

import java.lang.ref.WeakReference;
import java.util.Set;

/**
 * Created by kbrockman on 16-03-22.
 */
public class MainActivity extends AppCompatActivity {
    private String debugTag = "MainActivity";
    private boolean startDress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //main screen turn on
        Log.d("Online", "Goliath Online");
    }

    @Override
    protected void onStart(){
        super.onStart();

        final Button sendButton = (Button) findViewById(R.id.buttonOn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDress = !startDress;

                String buttonText = startDress ? "Off" : "On";
                sendButton.setText(buttonText);

                Thread thr = new Thread(){
                    @Override
                    public void run() {
                        StartSequence();
                    }
                };
                thr.start();
            }
        });

        //starterUp();
    }

    @Override
    public void onResume() {
        super.onResume();

        //Handle USB Serial
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbService.ACTION_USB_PERMISSION_GRANTED);
        filter.addAction(UsbService.ACTION_NO_USB);
        filter.addAction(UsbService.ACTION_USB_DISCONNECTED);
        filter.addAction(UsbService.ACTION_USB_NOT_SUPPORTED);
        filter.addAction(UsbService.ACTION_USB_PERMISSION_NOT_GRANTED);
        registerReceiver(mUsbReceiver, filter);  // Start listening notifications from UsbService
        startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
    }

    @Override
    public void onPause() {
        super.onPause();

        //Handle USB Serial
        unregisterReceiver(mUsbReceiver);
        unbindService(usbConnection);
    }

    private void StartSequence()  {
        while(startDress){

            try{
                this.runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                TextView display = (TextView) findViewById(R.id.textView1);
                                display.append("I am some data\r\n");
                            }
                        }
                );

                Thread.sleep(1000);
                //Get reading from headset

                //Run through color calculator

                //Send to dress controller
            } catch (InterruptedException ex){
                Log.d(debugTag, "Error in running: " + ex.getMessage());
            }

        }
    }

    //region USB Service

    private UsbService usbService;
    private MyHandler mHandler;

    private void startService(Class<?> service, ServiceConnection serviceConnection, Bundle extras) {
        if (!UsbService.SERVICE_CONNECTED) {
            Intent startService = new Intent(this, service);
            if (extras != null && !extras.isEmpty()) {
                Set<String> keys = extras.keySet();
                for (String key : keys) {
                    String extra = extras.getString(key);
                    startService.putExtra(key, extra);
                }
            }
            startService(startService);
        }
        Intent bindingIntent = new Intent(this, service);
        bindService(bindingIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    //This handler will be passed to UsbService. Data received from serial port is displayed through this handler
    private static class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    String data = (String) msg.obj;
                    //mActivity.get().display.append(data);
                    break;
                case UsbService.CTS_CHANGE:
                    Toast.makeText(mActivity.get(), "CTS_CHANGE", Toast.LENGTH_LONG).show();
                    break;
                case UsbService.DSR_CHANGE:
                    Toast.makeText(mActivity.get(), "DSR_CHANGE",Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private final ServiceConnection usbConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            usbService = ((UsbService.UsbBinder) arg1).getService();
            usbService.setHandler(mHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            usbService = null;
        }
    };

    //Notifications from UsbService will be received here.
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UsbService.ACTION_USB_PERMISSION_GRANTED: // USB PERMISSION GRANTED
                    Toast.makeText(context, "USB Ready", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_PERMISSION_NOT_GRANTED: // USB PERMISSION NOT GRANTED
                    Toast.makeText(context, "USB Permission not granted", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_NO_USB: // NO USB CONNECTED
                    Toast.makeText(context, "No USB connected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_DISCONNECTED: // USB DISCONNECTED
                    Toast.makeText(context, "USB disconnected", Toast.LENGTH_SHORT).show();
                    break;
                case UsbService.ACTION_USB_NOT_SUPPORTED: // USB NOT SUPPORTED
                    Toast.makeText(context, "USB device not supported", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    //endregion

    //region EEG Headset

    private void starterUp(){
        //Start EEGHeadset service
        Intent i = new Intent(this, EEGHeadset.class);
        startService(i);
    }

    //endregion
}
