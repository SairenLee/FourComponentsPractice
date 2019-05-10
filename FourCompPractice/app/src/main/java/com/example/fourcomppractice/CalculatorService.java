package com.example.fourcomppractice;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
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

        public double operate(String a, String b, String op){
            switch (op){
                case "+": return Double.valueOf(a) + Double.valueOf(b);
                case "-": return Double.valueOf(a) - Double.valueOf(b);
                case "x": return Double.valueOf(a) * Double.valueOf(b);
                case "÷": try{
                    return Double.valueOf(a) / Double.valueOf(b);
                }catch (Exception e){
                    Log.d("Calc", e.getMessage());
                }
                default: return -1;
            }
        }
        public void test()
        {
            Log.d("dddd", "test: adsadsadasdsadasdsa");
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
