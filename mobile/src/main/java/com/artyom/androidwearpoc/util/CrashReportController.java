package com.artyom.androidwearpoc.util;

import com.crashlytics.android.Crashlytics;

/**
 * Created by Artyom on 09/01/2017.
 */

public class CrashReportController {

    public void reportCrash(Throwable throwable) {
        Crashlytics.logException(throwable);
    }

}
