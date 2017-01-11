package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.dagger.modules.ReportModule;
import com.artyom.androidwearpoc.dagger.modules.SharedPrefsModule;
import com.artyom.androidwearpoc.ui.main.MainActivity;
import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.modules.NotificationModule;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.report.ReportController;
import com.artyom.androidwearpoc.util.SharedPrefsController;
import com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusNotificationController;

import dagger.Component;

/**
 * Created by Artyom on 15/12/2016.
 */
@ForApplication
@Component(modules = {NotificationModule.class, ReportModule.class, SharedPrefsModule.class})
public interface MobileApplicationComponent {

    ConnectivityStatusNotificationController getConnectivityStatusNotificationController();

    ReportController getReportController();

    SharedPrefsController getSharedPrefsController();

    void inject(MyMobileApplication myMobileApplication);

    void inject(MainActivity mainActivity);
}
