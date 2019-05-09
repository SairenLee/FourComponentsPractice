package com.example.fourcomppractice;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //IntentFilter for BroadcastReceiver to receive the broadcast from sender and start ActivityB
    private IntentFilter intentFilterForActivityB;
    private BroadcastReceiver localReceiverForActivityB;

    //Bind the CalculatorService
    private CalculatorService.CalculatorBinder calculatorBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            calculatorBinder = (CalculatorService.CalculatorBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    //For showing the data in UI
    private List<String> listForView = new ArrayList<>();
    private ArrayAdapter<String> adapterForView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeButton();
        initializeBroadcastReceiver();
        initializeListView();
    }

    //Initialize all the buttons in UI
    private void initializeButton()
    {
        //Direct way to start Activity
        Button directStartActivity = findViewById(R.id.DirectStartActivity);
        directStartActivity.setOnClickListener(this);

        //Send the Broadcast when the Button is clicked.
        Button broadcastStartActivity = findViewById(R.id.BroadcastStartActivity);
        broadcastStartActivity.setOnClickListener(this);

        //BindService and use the service as the calculator
        Button bindService = findViewById(R.id.BindService);
        bindService.setOnClickListener(this);

        //use the binder to get the value from service, save the value into ContentProvider, query the data and show in the ListView
        Button calculate = findViewById(R.id.Calculate);
        calculate.setOnClickListener(this);
    }

    //Receive the Broadcast from the button BroadcastStartActivity and start ActivityB
    private void initializeBroadcastReceiver()
    {
        intentFilterForActivityB = new IntentFilter();
        intentFilterForActivityB.addAction("action.intent.action.MYRECEIVER");
        localReceiverForActivityB = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent();
                i.setClass(context,ActivityB.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        registerReceiver(localReceiverForActivityB,intentFilterForActivityB);
    }

    //setup the ListView
    private void initializeListView()
    {
        ListView view = findViewById(R.id.view);
        adapterForView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,listForView);
        view.setAdapter(adapterForView);
    }

    //when the buttons are clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.DirectStartActivity:
                Intent intent = new Intent(getBaseContext(), ActivityB.class);
                startActivity(intent);
                break;
            case R.id.BroadcastStartActivity:
                Intent intent1 = new Intent("action.intent.action.MYRECEIVER");
                intent1.setPackage("com.example.fourcomppractice");
                sendBroadcast(intent1);
                break;
            case R.id.BindService:
                Intent bindIntent = new Intent(getBaseContext(), CalculatorService.class);
                bindService(bindIntent, connection, BIND_AUTO_CREATE);
                break;
            case R.id.Calculate:
                EditText editText = findViewById(R.id.input_number);
                Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value");
                ContentValues values = new ContentValues();
                values.put("value", calculatorBinder.calculate(Integer.valueOf(editText.getText().toString())));
                getContentResolver().insert(uri, values);
                adapterForView.clear();
                Cursor cursor = getContentResolver().query(uri,null,null,null,null,null);
                if (cursor != null) {
                    while (cursor.moveToNext()) {
                        int value = cursor.getInt(cursor.getColumnIndex("value"));
                        listForView.add(Integer.toString(value));
                    }
                    adapterForView.notifyDataSetChanged();
                    cursor.close();
                }
                break;
            default:
                break;
        }
    }
}
