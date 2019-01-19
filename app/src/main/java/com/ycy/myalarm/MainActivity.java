package com.ycy.myalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ycy.myalarm.utils.AlarmManagerUtils;

public class MainActivity extends AppCompatActivity {

    private final String ACTION = "receiver.AlarmReceiver";
    private final int SINGLEREQUESTCODE = 1;
    private final int REPEATINGREQUESTCODE = 2;
    private long startTime = System.currentTimeMillis();
    private Context context = MainActivity.this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.but1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //在startTime开始 执行一次
                AlarmManagerUtils.newInstance(context).creatBroadcastAlarm(
                        SINGLEREQUESTCODE,
                        startTime,
                        new Intent().setAction(ACTION).putExtra("SINGLEREQUESTCODE",SINGLEREQUESTCODE));
            }
        });

        findViewById(R.id.but2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 在startTime开始 一分钟秒执行一次(理论) 亲测部分真机接收时间有误差，例如 vivo Y75s 间隔时间低于一分钟的 都按照一分钟计算
                AlarmManagerUtils.newInstance(context)
                        .creatBroadcastRepeatingAlarm(
                                REPEATINGREQUESTCODE,
                                startTime,
                                1000*60,
                                new Intent().setAction(ACTION).putExtra("REPEATINGREQUESTCODE",REPEATINGREQUESTCODE));
            }
        });

        findViewById(R.id.but3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManagerUtils.newInstance(context).cancelBroadCastAlarmByRequestCode(REPEATINGREQUESTCODE,new Intent().setAction(ACTION));
            }
        });

    }
}
