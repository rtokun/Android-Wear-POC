package com.artyom.androidwearpoc.util;

import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by Artyom on 28/12/2016.
 */
public class ServiceUtils {

    private Context mApplicationContext;

    @Inject
    public ServiceUtils(Context applicationContext) {
        this.mApplicationContext = applicationContext;
    }

    public boolean isServiceRunning(Class<?> serviceClass) {

        ActivityManager activityManager = (ActivityManager) mApplicationContext
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);

        for (ActivityManager.RunningServiceInfo runningServiceInfo : services) {

            if (runningServiceInfo.service.getClassName().equals(serviceClass.getName())) {
                return true;
            }
        }

        return false;
    }
}
