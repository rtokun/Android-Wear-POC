package com.artyom.androidwearpoc.report;

import com.artyom.androidwearpoc.report.log.DataMismatchEvent;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;

/**
 * Created by Artyom on 09/01/2017.
 */

public class ReportController {

    public void reportCustomEvent(Throwable throwable) {
        Crashlytics.logException(throwable);
    }

    public void sendCustomEvent(DataMismatchEvent dataMismatchEvent) {
        Answers.getInstance().logCustom(dataMismatchEvent);
    }

    public void setBool(String key, boolean bool) {
        Crashlytics.setBool(key, bool);
    }


    public void setString(String key, String value) {
        Crashlytics.setString(key, value);
    }
}
