package com.example.azure.washout;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

public class StartActivity extends AppCompatActivity{

    private String firstData = ""; // tmp for the data that just get from server (eg. Temp_Display,1496721232964,26.1)

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent().setClass(StartActivity.this, MainActivity.class));
                finish();
            }
        }, 3000);
        //mHandler.sendEmptyMessageDelayed(GOTO_MAIN_ACTIVITY, 5000); // after 3 seconds, turn to mainActivity

    }



    private static final int GOTO_MAIN_ACTIVITY = 0;
//    private Handler mHandler = new Handler() {
//        public void handleMessage(android.os.Message msg) {
//
//            switch (msg.what) {
//                case GOTO_MAIN_ACTIVITY:
//                    Intent intent = new Intent();
//                    intent.setClass(StartActivity.this, MainActivity.class);
//                    System.out.println("Start~~~~~~ " + firstData);
//                    intent.putExtra("Data", firstData);
//                    startActivity(intent);
//                    finish();
//                    break;
//
//                default:
//                    break;
//            }
//        }
//
//    };



}
