package com.example.fourcomppractice;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
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

public class ActivityB extends AppCompatActivity implements View.OnClickListener{

    //IntentFilter for BroadcastReceiver to receive the broadcast from sender and start ActivityB
    private IntentFilter intentFilterForActivityA;
    private BroadcastReceiver localReceiverForActivityA;

    //IntentFilter for CalculatorService to receive the broadcast from sender and show the data from service to ListView
    private IntentFilter intentFilterForCalculatorService;
    private BroadcastReceiver localReceiverForCalculator;

    //For showing the data in UI
    private List<String> listForView = new ArrayList<>();
    private ArrayAdapter<String> adapterForView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
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

        //Start the Calculate service
        Button start_Service = (Button) findViewById(R.id.StartService);
        start_Service.setOnClickListener(this);

        //Broadcast to the Calculate service
        Button calculate = (Button) findViewById(R.id.Calculate);
        calculate.setOnClickListener(this);

        //delete all the data in ContentProvider
        Button deleteData = (Button) findViewById(R.id.Delete_data);
        deleteData.setOnClickListener(this);
    }

    //setup the ListView
    private void initializeListView()
    {
        ListView view = findViewById(R.id.view);
        adapterForView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,listForView);
        view.setAdapter(adapterForView);
    }

    private void initializeBroadcastReceiver() {
        //Receive the Broadcast from the button BroadcastStartActivity and start MainActivitys
        intentFilterForActivityA = new IntentFilter();
        intentFilterForActivityA.addAction("action.intent.action.MYRECEIVER");
        localReceiverForActivityA = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent();
                i.setClass(context,MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        registerReceiver(localReceiverForActivityA,intentFilterForActivityA);

        //Receive broadcast from Calculate service
        intentFilterForCalculatorService = new IntentFilter();
        intentFilterForCalculatorService.addAction("action.intent.action.serviceReturn");
        localReceiverForCalculator = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value");
                ContentValues values = new ContentValues();
                values.put("value", intent.getIntExtra("returnValue",0));
                getContentResolver().insert(uri, values);

                //Query and show in list
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

            }
        };
        registerReceiver(localReceiverForCalculator,intentFilterForCalculatorService);
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
            case R.id.StartService:
                Intent intentb = new Intent(getBaseContext(), CalculatorService.class);
                intentb.putExtra("data","fromB");
                startService(intentb);
                break;
            case R.id.Calculate:
                Intent intenta = new Intent("action.intent.action.serviceSet");
                intenta.setPackage("com.example.fourcomppractice");
                EditText editText = (EditText) findViewById(R.id.input_number);
                intenta.putExtra("data",Integer.valueOf(editText.getText().toString()));
                sendBroadcast(intenta);
                break;
            case R.id.Delete_data:
                Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value" + "#");
                getContentResolver().delete(uri, null, null);
                adapterForView.clear();
            default:
                break;
        }
    }

}
