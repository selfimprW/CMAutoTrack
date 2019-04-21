package me.cangming.cmautotrack;

import android.app.Application;

import me.cangming.autotrack.CmDataApi;

public class APP extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        CmDataApi.init(this);
    }
}
