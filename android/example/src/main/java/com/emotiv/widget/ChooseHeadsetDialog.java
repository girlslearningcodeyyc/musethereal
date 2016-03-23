package com.emotiv.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.emotiv.connectionmanager.ConnectionManager;
//import com.example.fftsample.R;
import com.musethereal.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Quang Nguyen on 13/10/2015.
 */
public class ChooseHeadsetDialog extends Dialog {
    private Context context;
    private ConnectionManager connectionManager;
    private DeviceListAdapter insightAdapter, epocAdapter;
    private ListView lvInsights;
    private ListView lvEpocPluses;
    Handler handler = new Handler();

    public ChooseHeadsetDialog(Context context) {
        super(context);
        this.context = context;
        connectionManager = new ConnectionManager(context);
        connectionManager.refreshDevices();
        getWidgets();
    }


    public void getWidgets() {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_choose_headset);
        this.setCanceledOnTouchOutside(true);


        //Set up Insight list
        insightAdapter = new DeviceListAdapter(context, new ArrayList<String>());
        lvInsights = (ListView) this.findViewById(R.id.lvInsights);
        lvInsights.setAdapter(insightAdapter);
        lvInsights.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectionManager.connectToInsight(position);
                ChooseHeadsetDialog.this.dismiss();
            }
        });

        //Set up EpocPlus list
        epocAdapter = new DeviceListAdapter(context, new ArrayList<String>());
        lvEpocPluses = (ListView) this.findViewById(R.id.lvEpocPluses);
        lvEpocPluses.setAdapter(epocAdapter);
        lvEpocPluses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                connectionManager.connectToEpocPlus(position);
                ChooseHeadsetDialog.this.dismiss();
            }
        });
        ImageButton btRefresh = (ImageButton)this.findViewById(R.id.ibRefresh);
        btRefresh.findViewById(R.id.ibRefresh);
        btRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectionManager.refreshDevices();
            }
        });

    }

    @Override
    public void show() {
        super.show();
        handler.postDelayed(updateDeviceList, 100);
    }

    @Override
    public void dismiss() {
        handler.removeCallbacks(updateDeviceList);
        super.dismiss();
    }

    private Runnable updateDeviceList = new Runnable() {
        @Override
        public void run() {
            refreshList();
            handler.postDelayed(updateDeviceList, 100);
        }
    };


    void refreshList() {
        //update Insights list
        insightAdapter.clear();
        insightAdapter.addAll(connectionManager.getInsights());

        //update EpocPlus list
        epocAdapter.clear();
        epocAdapter.addAll(connectionManager.getEpocPlus());

    }

}

class DeviceListAdapter extends ArrayAdapter<String> {
    private Context context;
    private List<String> devices;

    public DeviceListAdapter(Context context, List<String> devices) {
        super(context, 0, devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.list_item_devices, parent, false);
        }
        TextView tvDevice = (TextView) convertView.findViewById(R.id.tvDeviceName);
        tvDevice.setText(devices.get(position));

        return convertView;
    }
}
