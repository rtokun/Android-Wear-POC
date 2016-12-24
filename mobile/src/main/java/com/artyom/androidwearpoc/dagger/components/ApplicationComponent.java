package com.artyom.androidwearpoc.dagger.components;

import com.artyom.androidwearpoc.MainActivity;
import com.artyom.androidwearpoc.MyApplication;
import com.artyom.androidwearpoc.dagger.modules.NotificationModule;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusNotificationController;

import dagger.Component;

/**
 * Created by Artyom on 15/12/2016.
 */
@ForApplication
@Component(modules = {NotificationModule.class})
public interface ApplicationComponent {

    ConnectivityStatusNotificationController connectivityStatusNotificationController();

    void inject(MyApplication myApplication);

    void inject(MainActivity mainActivity);
}
