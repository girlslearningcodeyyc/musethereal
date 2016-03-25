package com.musethereal;

import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.Intent;

import com.emotiv.EEGHeadset;
import com.felhr.serialportexample.DressController;

/**
 * Created by kbrockman on 16-03-22.
 */
public class MainActivity extends AppCompatActivity {

    private TextView display;

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

        display = (TextView) findViewById(R.id.textView1);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            starterUp();
        }
        });

        starterUp();
    }

    private void starterUp(){
        //Start EEGHeadset service
        Intent i = new Intent(this, EEGHeadset.class);
        startService(i);
    }
}
