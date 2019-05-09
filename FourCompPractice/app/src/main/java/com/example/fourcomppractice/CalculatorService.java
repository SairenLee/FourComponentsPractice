package com.example.fourcomppractice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class CalculatorService extends Service {

    //For Receiving broadcast from B and send broadcast back to B
    private IntentFilter intentFilter;
    private BroadcastReceiver localReceiver;

    //binder for ActivityA
    public CalculatorService() {
    }
    private CalculatorBinder mBinder = new CalculatorBinder();
    class CalculatorBinder extends Binder
    {
        public int calculate(int s) {
            return s*10;
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        receiveBroadcastFromB();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    //Receiving broadcast from B and send broadcast back to B
    public void receiveBroadcastFromB()
    {

        intentFilter = new IntentFilter();
        intentFilter.addAction("action.intent.action.serviceSet");
        localReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(context,"receivedInservice:" + intent.getIntExtra("data",0),Toast.LENGTH_LONG).show();
                intent.setAction("action.intent.action.serviceReturn");
                intent.putExtra("returnValue",intent.getIntExtra("data",0) * 10);
                intent.setPackage("com.example.fourcomppractice");
                sendBroadcast(intent);
            }
        };
        registerReceiver(localReceiver,intentFilter);
    }
}
