package com.musethereal;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by kbrockman on 16-03-22.
 */
public class MainActivity extends AppCompatActivity {

    private TextView display;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //main screen turn on
        Log.d("Online", "Goliath Online");

        display = (TextView) findViewById(R.id.textView1);
        Button sendButton = (Button) findViewById(R.id.buttonSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display.append("You clicked me!\r\n");
            }
        });
    }
}
