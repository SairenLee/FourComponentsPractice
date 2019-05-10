package com.example.fourcomppractice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;
import java.util.List;


public class ActivityB extends AppCompatActivity implements View.OnClickListener{

    //IntentFilter for BroadcastReceiver to receive the broadcast from sender and start ActivityB
    private IntentFilter mIntentFilterForMainActivity;
    private BroadcastReceiver mLocalReceiverForMainActivity;

    //For showing the data in UI
    private List<String> mListForView = new ArrayList<>();
    private ArrayAdapter<String> mAdapterForView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b);
        initializeButton();
        initializeBroadcastReceiver();
        initializeListView();

        Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value");
        mAdapterForView.clear();
        Cursor cursor = getContentResolver().query(uri,null,null,null,null,null);
        if (cursor != null) {
            cursor.moveToLast();
            cursor.moveToNext();
            while (cursor.moveToPrevious()) {
                double value = cursor.getDouble(cursor.getColumnIndex("value"));
                mListForView.add(Double.toString(value));
            }
            mAdapterForView.notifyDataSetChanged();
            cursor.close();
        }


    }

    //Initialize all the buttons in UI
    private void initializeButton()
    {
        //back by Broadcast
        FloatingActionButton backByBroadcast = findViewById(R.id.back);
        backByBroadcast.setOnClickListener(this);
        //delete all the data in ContentProvider
        FloatingActionButton deleteData =findViewById(R.id.Delete_data);
        deleteData.setOnClickListener(this);
    }

    //setup the ListView
    private void initializeListView()
    {
        ListView view = findViewById(R.id.view);
        mAdapterForView = new ArrayAdapter<>(this, android.R.layout.simple_list_item_activated_1,mListForView);
        view.setAdapter(mAdapterForView);
    }

    private void initializeBroadcastReceiver() {
        //Receive the Broadcast from the button BroadcastStartActivity and start MainActivitys
        mIntentFilterForMainActivity = new IntentFilter();
        mIntentFilterForMainActivity.addAction("action.intent.action.MYRECEIVER");
        mLocalReceiverForMainActivity = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Intent i = new Intent();
                i.setClass(context, MainActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);
            }
        };
        registerReceiver(mLocalReceiverForMainActivity, mIntentFilterForMainActivity);

    }

    //when the buttons are clicked
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                Intent intent = new Intent("action.intent.action.MYRECEIVER");
                intent.setPackage("com.example.fourcomppractice");
                sendBroadcast(intent);
                break;
            case R.id.Delete_data:
                Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value" + "#");
                getContentResolver().delete(uri, null, null);
                mAdapterForView.clear();
            default:
                break;
        }
    }

}
