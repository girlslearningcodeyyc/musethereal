package com.musethereal;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;
import com.emotiv.insight.IEdk;
import com.emotiv.widget.ChooseHeadsetDialog;
import com.felhr.serialportexample.UsbService;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by kbrockman on 16-03-22.
 */
public class MainActivity extends AppCompatActivity {
    private String debugTag = "MainActivity";
    private boolean startDress = false;
    ColorCalculator colorCalculator;
    private boolean readyToTransmit;

    //UI Elements
    private TextView display;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //main screen turn on
        Log.d("Online", "Goliath Online");

        //Set up EEG headset stuff
        setChannelList();

        colorCalculator = new ColorCalculator(_channelList);

        //Set up BT manager
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        //Attach button click to headset selection
        Button headsetConnectButton = (Button) findViewById(R.id.epocButton);
        headsetConnectButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO: need this step to select the headset - maybe there's just a way to grab it or complain if it doesnt exist
                ChooseHeadsetDialog dialog = new ChooseHeadsetDialog(MainActivity.this);
                dialog.show();
            }
        });

        //Connect to emoEngine
        IEdk.IEE_EngineConnect(this, "");
    }

    @Override
    protected void onStart(){
        super.onStart();

        final Button startButton = (Button) findViewById(R.id.buttonOn);
        display = (TextView) findViewById(R.id.textView1);
        mHandler = new MyHandler(this);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDress = !startDress;

                String buttonText = startDress ? "Off" : "On";
                startButton.setText(buttonText);

                Thread thr = new Thread() {
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
        readyToTransmit = true;
        while(startDress){

            try{
                if (usbService != null && readyToTransmit) {
                    readyToTransmit = false;

                    //Get reading from headset
                    double[][] reading = new double[_channelList.length][5];
                    boolean readingIsNull = false;
                    for(int i = 0; i < _channelList.length; i++) {
                        double[] f = IEdk.IEE_GetAverageBandPowers(_channelList[i]);

                        //sometimes reading is empty - in which case just ignore and just reloop cause we cant do anything with them anyways
                        readingIsNull = f == null || f.length == 0;

                        if (!readingIsNull) {
                            for (int j = 0; j < f.length; j++) {
                                reading[i][j] = f[j];
                            }
                        } else {
                            //give up, we dont have a full record so just abandon and keep going
                            break;
                        }
                    }

//                    Log.d(debugTag, "After reading");
//                    Log.d(debugTag, reading.keySet().toString());
//                    Log.d(debugTag, reading.values().toString());

                    //Run through color calculator
                    if(!readingIsNull) {
                        String vals = colorCalculator.ConvertToColors(reading);

                        //Send to dress controller
                        new SendToScreen().execute(vals);
                        usbService.write(vals.getBytes());
                    }
                }

                //TODO: uncomment the transmit line here if youre debugging and you dont have a peripheral USB device responding with a ready to transmit byte
                //readyToTransmit = true;

                Thread.sleep(300);
            } catch (InterruptedException ex){
                Log.d(debugTag, "Error in running: " + ex.getMessage());
            }

        }

        if (!startDress && usbService != null){
            usbService.write(colorCalculator.EmptyString().getBytes());
        }
    }

    //See: http://developer.android.com/guide/components/processes-and-threads.html
    private class SendToScreen extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... params) {
            return params[0];
        }

        protected void onPostExecute(String result){
            writeToDisplay(result);
        }
    }

    private void writeToDisplay(String text){
        display.setText(text + "\n" + display.getText());
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
    private class MyHandler extends Handler {
        private final WeakReference<MainActivity> mActivity;

        public MyHandler(MainActivity activity) {
            mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UsbService.MESSAGE_FROM_SERIAL_PORT:
                    //TODO: any return data is a ready to transmit byte - change this logic if you want to refine the android/arduino interaction
                    mActivity.get().readyToTransmit = true;;
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

    private String eegHeadset = "eegHeadset";

    private IEdk.IEE_DataChannel_t [] _channelList;

    private void setChannelList() {
        _channelList = new IEdk.IEE_DataChannel_t[] {
            IEdk.IEE_DataChannel_t.IED_AF4,
            IEdk.IEE_DataChannel_t.IED_T8,
            IEdk.IEE_DataChannel_t.IED_O2,
            IEdk.IEE_DataChannel_t.IED_O1,
            IEdk.IEE_DataChannel_t.IED_T7,
            IEdk.IEE_DataChannel_t.IED_AF3,
            IEdk.IEE_DataChannel_t.IED_F3,
            IEdk.IEE_DataChannel_t.IED_F8,
            IEdk.IEE_DataChannel_t.IED_FC6,
            IEdk.IEE_DataChannel_t.IED_P8,
            IEdk.IEE_DataChannel_t.IED_FC5,
            IEdk.IEE_DataChannel_t.IED_F7,
            IEdk.IEE_DataChannel_t.IED_P7,
        };
    }

    private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;

    //endregion
}
