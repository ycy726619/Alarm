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

import com.ycy.myalarm.Main2Activity;
import com.ycy.myalarm.R;

public class AlarmReceiver extends BroadcastReceiver {

    private final String              TAG      = "AlarmReceiverTAG";
    private final String ACTION = "receiver.AlarmReceiver";
    private       NotificationManager manager;
    private       long[]              vibrates = {0, 2000, 1000, 2000}; //震动范围
    private       int                 notifyId = 0; //通知id

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "AlarmReceiverTAG:onReceive");
        if (ACTION.equals(intent.getAction())) {
            int requestCode1 = intent.getIntExtra("SINGLEREQUESTCODE", 0);
            int requestCode2 = intent.getIntExtra("REPEATINGREQUESTCODE", 0);
            if (requestCode1 == 0 && requestCode2 != 0)
                notifyId = requestCode2;
            else if (requestCode2 == 0 && requestCode1 != 0)
                notifyId = requestCode1;

            manager = (NotificationManager) context.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
            Intent playIntent = new Intent(context, Main2Activity.class);
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
            manager.notify(notifyId, builder.build());

        }
    }
}
