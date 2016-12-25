package com.artyom.androidwearpoc.dagger.modules;

import android.app.NotificationManager;
import android.content.Context;

import com.artyom.androidwearpoc.shared.dagger.modules.ApplicationContextModule;
import com.artyom.androidwearpoc.shared.dagger.scopes.ForApplication;
import com.artyom.androidwearpoc.wear.connectivity.ConnectivityStatusNotificationController;
import com.artyom.androidwearpoc.wear.connectivity.WearConnectivityServiceController;

import dagger.Module;
import dagger.Provides;

@Module(includes = ApplicationContextModule.class)
public class NotificationModule {

    @ForApplication
    @Provides
    ConnectivityStatusNotificationController connectivityStatusNotificationController(Context applicationContext,
                                                                                      NotificationManager notificationManager) {
        return new ConnectivityStatusNotificationController(applicationContext, notificationManager);
    }

    @ForApplication
    @Provides
    NotificationManager notificationManager(Context applicationContext) {
        return (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @ForApplication
    @Provides
    WearConnectivityServiceController wearConnectivityServiceController(Context applicationContext) {
        return new WearConnectivityServiceController(applicationContext);
    }

}
