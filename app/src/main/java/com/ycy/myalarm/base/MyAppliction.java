package com.ycy.myalarm.base;


import org.litepal.LitePal;
import org.litepal.LitePalApplication;


public class MyAppliction extends LitePalApplication {



    @Override
    public void onCreate() {
        super.onCreate();
        LitePal.initialize(this);
    }
}
