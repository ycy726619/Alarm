package com.ycy.myalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ycy.myalarm.db.AlarmBean;
import com.ycy.myalarm.utils.AlarmManagerUtils;
import com.ycy.myalarm.utils.Constants;

public class MainActivity extends AppCompatActivity {

    private final int     SINGLEREQUESTCODE    = 1;
    private final int     REPEATINGREQUESTCODE = 2;
    private       long    startTime            = System.currentTimeMillis();
    private       long    cycleTime            = 1000 * 60;
    private       Context context              = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //单次执行
        findViewById(R.id.but1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmBean alarmBean = new AlarmBean(SINGLEREQUESTCODE, startTime, 0, false);
                alarmBean.save();
                Log.d("litepal", "onClick: "+alarmBean.toString());
                //在startTime开始 执行一次
                AlarmManagerUtils.newInstance(context).creatBroadcastAlarm(
                        new Intent().setAction(Constants.BrodCastKey.ACTION)
                                .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                                .putExtra(Constants.IntentKey.ALARMID, alarmBean.getId()));
            }
        });

        //循环多次执行
        findViewById(R.id.but2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmBean alarmBean = new AlarmBean(REPEATINGREQUESTCODE, startTime, cycleTime, false);
                alarmBean.save();
                // 在startTime开始 6秒执行一次(理论) 亲测部分真机接收时间有误差，例如 vivo Y75s休眠后
                AlarmManagerUtils.newInstance(context)
                        .creatBroadcastRepeatingAlarm(
                                new Intent().setAction(Constants.BrodCastKey.ACTION)
                                        .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                                        .putExtra(Constants.IntentKey.ALARMID, alarmBean.getId()));
            }
        });

        //结束
        findViewById(R.id.but3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlarmManagerUtils.newInstance(context).
                        cancelBroadCastAlarmByRequestCode(REPEATINGREQUESTCODE, new Intent().setAction(Constants.BrodCastKey.ACTION));
            }
        });

    }
}
