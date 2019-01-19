package com.ycy.myalarm.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.ycy.myalarm.MainActivity;
import com.ycy.myalarm.R;
import com.ycy.myalarm.db.AlarmBean;
import com.ycy.myalarm.utils.AlarmManagerUtils;
import com.ycy.myalarm.utils.Constants;

import org.litepal.LitePal;

public class AlarmReceiver extends BroadcastReceiver {

    private final String              TAG      = "AlarmReceiverTAG";
    private       NotificationManager manager;
    private       long[]              vibrates = {0, 2000, 1000, 2000}; //震动范围

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiverTAG:onReceive");
        if (Constants.BrodCastKey.ACTION.equals(intent.getAction())) {
            int alarmId = intent.getIntExtra(Constants.IntentKey.ALARMID, 0);
            if(alarmId != 0){
                AlarmBean alarmBean = LitePal.find(AlarmBean.class, alarmId);
                if(!alarmBean.isCancel()){
                    alarmBean.setInterVal(true);
                    alarmBean.save();
                    AlarmManagerUtils.newInstance(context).creatBroadcastRepeatingAlarm(new Intent().setAction(Constants.BrodCastKey.ACTION)
                            .addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
                            .putExtra(Constants.IntentKey.ALARMID, alarmBean.getId()));
                }
                manager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
                Intent playIntent = new Intent(context, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, playIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                builder.setContentTitle("测试标题")
                        .setContentText("测试内容")
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)) //通知声音
                        .setLights(Color.GREEN, 2000, 1000) //LED 灯闪烁
                        .setVibrate(vibrates)  //手机振动
                        .setPriority(Notification.PRIORITY_MAX)
                        // 通知首次出现在通知栏，带上升动画效果的
                        .setWhen(System.currentTimeMillis())
                        .setSmallIcon(R.mipmap.ic_launcher) //通知栏图标
                        .setDefaults(Notification.DEFAULT_VIBRATE | Notification.DEFAULT_ALL | Notification.DEFAULT_SOUND)
                        .setContentIntent(pendingIntent).setAutoCancel(true);
                manager.notify(alarmBean.getRequestCode(), builder.build());
            }
        }
    }
}
