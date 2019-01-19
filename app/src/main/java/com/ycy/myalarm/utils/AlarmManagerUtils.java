package com.ycy.myalarm.utils;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.SystemClock;

import com.ycy.myalarm.db.AlarmBean;

import org.litepal.LitePal;

/**
 * Description:
 * AlarmManagerUtils 对 AlarmManager进行封装,可完成定时、单次、多次、循环发送 服务、广播
 * 且可以退出app保持后台运行(因为AlarmManager是系统级别的定时器,所以一般不会被Kill) 需注意权限
 */
public class AlarmManagerUtils {


    private static AlarmManager      am;
    private static AlarmManagerUtils mTaskUtils;
    private static Context           context;
    private static PendingIntent     pendingIntent;

    private AlarmManagerUtils() {
    }

    private AlarmManagerUtils(Context context) {
        this.context = context;
        am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
    }

    public static AlarmManagerUtils newInstance(Context context) {
        if (null == mTaskUtils) {
            mTaskUtils = new AlarmManagerUtils(context);
        }
        return mTaskUtils;
    }

    /**
     * 检查服务定时器是否在运行
     *
     * @param requestCode
     * @param intent
     * @return
     */
    private static boolean isServiceAlarmOn(int requestCode, Intent intent) {
        pendingIntent = PendingIntent.getService(
                context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    /**
     * 检查广播定时器是否在运行
     *
     * @param requestCode
     * @param intent
     * @return true pendingIntent = null | false pendingIntent 不为空
     */
    private static boolean isBrodCastAlarmOn(int requestCode, Intent intent) {
        pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        return null == pendingIntent;
    }

    /**
     * 创建单次定时广播(可在锁屏下唤醒手机[设备])
     * @param intent 传递数据
     */
    public void creatBroadcastAlarm(final Intent intent) {
        int alarmId = intent.getIntExtra(Constants.IntentKey.ALARMID, 0);
        if(alarmId != 0 ){
            AlarmBean alarmBean = LitePal.find(AlarmBean.class, alarmId);
            if (isBrodCastAlarmOn(alarmBean.getRequestCode(), intent)) {//PendingIntent 为空则创建
                /**
                 * Flags为 0 : 不携带数据
                 * Flags为PendingIntent.FLAG_UPDATE_CURRENT : requestCode 最后一次PendingIntent数据则为最新数据
                 *                                           requestCode 不同 所有PendingIntent数据都会被保留
                 * Flags为PendingIntent.FLAG_CANCEL_CURRENT : 不论requestCode相同不相同 永远只保留最后一次PendingIntent
                 * Flags为PendingIntent.FLAG_NO_CREATE  :  不创建PendingIntent
                 */
                pendingIntent = PendingIntent.getBroadcast(context, alarmBean.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                /**
                 * AlarmManager.RTC，硬件闹钟，不唤醒手机（也可能是其它设备）休眠；当手机休眠时不发射闹钟。
                 * AlarmManager.RTC_WAKEUP，硬件闹钟，当闹钟发躰时唤醒手机休眠；
                 * AlarmManager.ELAPSED_REALTIME，真实时间流逝闹钟，不唤醒手机休眠；当手机休眠时不发射闹钟。
                 * AlarmManager.ELAPSED_REALTIME_WAKEUP，真实时间流逝闹钟，当闹钟发躰时唤醒手机休眠；
                 */
                am.set(AlarmManager.RTC_WAKEUP, alarmBean.getStartTime(), pendingIntent);
            } else {
                cancelBroadCastAlarmByRequestCode(alarmBean.getRequestCode(), intent);
                creatBroadcastAlarm(intent);
            }
        }
    }


    /**
     * 创建定时循环广播(可在锁屏下唤醒手机[设备])
     * @param intent 传递数据
     */
    public void creatBroadcastRepeatingAlarm(final Intent intent) {
        int alarmId = intent.getIntExtra(Constants.IntentKey.ALARMID, 0);
        if(alarmId != 0){
            AlarmBean alarmBean = LitePal.find(AlarmBean.class, alarmId);
            if (isBrodCastAlarmOn(alarmBean.getRequestCode(), intent)) { //PendingIntent 为空则创建
                pendingIntent = PendingIntent.getBroadcast(context, alarmBean.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // 此处必须使用SystemClock.elapsedRealtime，否则闹钟无法接收
                long triggerAtMillis = SystemClock.elapsedRealtime();
                // 更新开启时间
                if (!alarmBean.isInterVal()) {
                    triggerAtMillis += alarmBean.getCycleTime();
                }
                // pendingIntent 为发送广播
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis,
                            pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
                } else {// api19以前还是可以使用setRepeating重复发送广播
                    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, alarmBean.getCycleTime(),
                            pendingIntent);
                }
            } else {
                cancelBroadCastAlarmByRequestCode(alarmBean.getRequestCode(), intent);
                creatBroadcastRepeatingAlarm(intent);
            }
        }
    }

    /**
     * 创建单次定时服务(可在锁屏下唤醒手机[设备])
     * @param intent      传递数据
     */
    public void creatServiceAlarm(final Intent intent) {
        int alarmId = intent.getIntExtra(Constants.IntentKey.ALARMID, 0);
        if(alarmId != 0 ){
            AlarmBean alarmBean = LitePal.find(AlarmBean.class, alarmId);
            if (isServiceAlarmOn(alarmBean.getRequestCode(), intent)) {//PendingIntent 为空则创建
                pendingIntent = PendingIntent.getBroadcast(context, alarmBean.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                am.set(AlarmManager.RTC_WAKEUP, alarmBean.getStartTime(), pendingIntent);
            } else {
                cancelServiceAlarmByRequestCode(alarmBean.getRequestCode(), intent);
                creatServiceAlarm(intent);
            }
        }
    }

    /**
     * 创建定时循环服务(可在锁屏下唤醒手机[设备])
     *
     * @param intent      传递数据
     */
    public void creatRepeatingServiceAlarm(final Intent intent) {
        int alarmId = intent.getIntExtra(Constants.IntentKey.ALARMID, 0);
        if(alarmId != 0){
            AlarmBean alarmBean = LitePal.find(AlarmBean.class, alarmId);
            if (isServiceAlarmOn(alarmBean.getRequestCode(), intent)) { //PendingIntent 为空则创建
                pendingIntent = PendingIntent.getBroadcast(context, alarmBean.getRequestCode(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                // 此处必须使用SystemClock.elapsedRealtime，否则闹钟无法接收
                long triggerAtMillis = SystemClock.elapsedRealtime();
                // 更新开启时间
                if (!alarmBean.isInterVal()) {
                    triggerAtMillis += alarmBean.getCycleTime();
                }
                // pendingIntent 为发送广播
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    am.setExactAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis,
                            pendingIntent);
                } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    am.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, pendingIntent);
                } else {// api19以前还是可以使用setRepeating重复发送广播
                    am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, alarmBean.getCycleTime(),
                            pendingIntent);
                }
            } else {
                cancelServiceAlarmByRequestCode(alarmBean.getRequestCode(), intent);
                creatRepeatingServiceAlarm(intent);
            }
        }
    }

    /**
     * 根据requestCode取消服务定时器
     *
     * @param
     */
    public void cancelServiceAlarmByRequestCode(int requestCode, final Intent intent) {
        if (!isServiceAlarmOn(requestCode, intent)) {
            pendingIntent = PendingIntent.getService(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
            if (null != pendingIntent) {
                am.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    /**
     * 根据requestCode取消广播定时器
     *
     * @param
     */
    public void cancelBroadCastAlarmByRequestCode(int requestCode, final Intent intent) {
        if (!isBrodCastAlarmOn(requestCode, intent)) {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
            if (null != pendingIntent) {
                am.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }
}
