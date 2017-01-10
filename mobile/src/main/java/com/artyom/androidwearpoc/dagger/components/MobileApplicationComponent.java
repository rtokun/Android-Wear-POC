package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.ui.main.MainActivity;
import com.artyom.androidwearpoc.MyMobileApplication;
import com.artyom.androidwearpoc.dagger.modules.NotificationModule;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.util.CrashReportController;
import com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusNotificationController;

import dagger.Component;

/**
 * Created by Artyom on 15/12/2016.
 */
@ForApplication
@Component(modules = {NotificationModule.class})
public interface MobileApplicationComponent {

    ConnectivityStatusNotificationController getConnectivityStatusNotificationController();

    CrashReportController getCrashReportController();

    void inject(MyMobileApplication myMobileApplication);

    void inject(MainActivity mainActivity);
}
