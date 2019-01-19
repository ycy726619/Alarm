package com.ycy.myalarm.db;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;


public class AlarmBean extends LitePalSupport implements Serializable {

    private int id;
    private int requestCode;   //请求标识，用于取消对应Alarm
    private long startTime;   //执行时间
    private long cycleTime;   //间隔时间
    private boolean interVal;   //唤醒标志

    public AlarmBean() {
    }

    public AlarmBean(int requestCode, long startTime, long cycleTime, boolean interVal) {
        this.requestCode = requestCode;
        this.startTime = startTime;
        this.cycleTime = cycleTime;
        this.interVal = interVal;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCycleTime() {
        return cycleTime;
    }

    public void setCycleTime(long cycleTime) {
        this.cycleTime = cycleTime;
    }

    public boolean isInterVal() {
        return interVal;
    }

    public void setInterVal(boolean interVal) {
        this.interVal = interVal;
    }

    @Override
    public String toString() {
        return "AlarmBean{" +
                "id=" + id +
                ", requestCode=" + requestCode +
                ", startTime=" + startTime +
                ", cycleTime=" + cycleTime +
                ", interVal=" + interVal +
                '}';
    }
}
