package com.example.fourcomppractice;

import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //For Calculator
    private TextView mScreen;
    private String mDisplay = "";
    private String mCurrentOperator = "";
    private String mResult = "";

    //Bind the CalculatorService
    private CalculatorService.CalculatorBinder mCalculatorBinder;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mCalculatorBinder = (CalculatorService.CalculatorBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        FloatingActionButton fab = findViewById(R.id.fab);                      //initialize FloatingButton
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActivityB.class);
                startActivity(intent);
            }
        });

        mScreen = (TextView)findViewById(R.id.textView);     //initialize calculator TextView
        mScreen.setText(mDisplay);                           //initialize calculator TextView
        Intent bindIntent = new Intent(getBaseContext(), CalculatorService.class);  //initialize bindService
        bindService(bindIntent, mConnection, BIND_AUTO_CREATE);                     //initialize bindService


    }
    // For the drawer
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    // For the NavigationItem and if select history then start activity
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.history) {
            Intent intent = new Intent(getBaseContext(), ActivityB.class);
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void updateScreen(){
        mScreen.setText(mDisplay);
    }

    public void onClickNumber(View v){
        if(mResult != ""){
            clear();
            updateScreen();
        }
        Button b = (Button) v;
        mDisplay += b.getText();
        updateScreen();
    }

    private boolean isOperator(char op){
        switch (op){
            case '+':
            case '-':
            case 'x':
            case '÷':return true;
            default: return false;
        }
    }

    public void onClickOperator(View v){
        if(mDisplay == "") return;

        Button b = (Button)v;

        if(mResult != ""){
            String _display = mResult;
            clear();
            mDisplay = _display;
        }

        if(mCurrentOperator != ""){
            Log.d("CalcX", ""+mDisplay.charAt(mDisplay.length()-1));
            if(isOperator(mDisplay.charAt(mDisplay.length()-1))){
                mDisplay = mDisplay.replace(mDisplay.charAt(mDisplay.length()-1), b.getText().charAt(0));
                updateScreen();
                return;
            }else{
                getResult();
                mDisplay = mResult;
                mResult = "";
            }
            mCurrentOperator = b.getText().toString();
        }
        mDisplay += b.getText();
        mCurrentOperator = b.getText().toString();
        updateScreen();
    }

    private void clear(){
        mDisplay = "";
        mCurrentOperator = "";
        mResult = "";
    }

    public void onClickClear(View v){
        clear();
        updateScreen();
    }
    // Call the calculator service to calculate the value
    private boolean getResult(){
        mCalculatorBinder.test();
        if(mCurrentOperator == "") return false;
        String[] operation = mDisplay.split(Pattern.quote(mCurrentOperator));
        if(operation.length < 2) return false;

        Uri uri = Uri.parse("content://com.example.fourcomppractice.provider/value");
        ContentValues values = new ContentValues();
        mResult = String.valueOf(mCalculatorBinder.operate(operation[0], operation[1], mCurrentOperator));
        values.put("value",mCalculatorBinder.operate(operation[0], operation[1], mCurrentOperator));
        getContentResolver().insert(uri, values);

        return true;
    }

    public void onClickEqual(View v){
        if(mDisplay == "") return;
        if(!getResult()) return;
        mScreen.setText(mDisplay + "\n" + String.valueOf(mResult));
    }

}
