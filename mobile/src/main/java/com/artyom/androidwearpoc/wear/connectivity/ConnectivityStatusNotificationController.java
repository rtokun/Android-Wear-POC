package com.artyom.androidwearpoc.wear.connectivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.artyom.androidwearpoc.R;
import com.artyom.androidwearpoc.dagger.scopes.ForApplication;

import javax.inject.Inject;

/**
 * Created by Artyom on 24/12/2016.
 */
@ForApplication
public class ConnectivityStatusNotificationController {

    public static final int CONNECTIVITY_STATUS_NOTIFICATION_ID = 999006;

    private Context mApplicationContext;

    private NotificationManager mNotificationManager;

    @Inject
    public ConnectivityStatusNotificationController(Context applicationContext,
                                                    NotificationManager notificationManager) {
        this.mApplicationContext = applicationContext;
        this.mNotificationManager = notificationManager;
    }

    public Notification getNotification(ConnectivityStatusIndicatorType type) {
        int icon = getIcon(type);
        String msg = getMessage(type);
        return getNotification(icon, msg);
    }

    private String getMessage(ConnectivityStatusIndicatorType type) {
        switch (type) {
            case Unknown:
                return "Unknown status";
            case ConnectedToWatch:
                return "Connected to watch";
            case NotConnectedToWatch:
                return "Disconnected from watch";
            default:
                return "Unknown status";
        }
    }

    private int getIcon(ConnectivityStatusIndicatorType type) {
        switch (type) {
            case ConnectedToWatch:
                return R.drawable.connected;
            case Unknown:
                return R.drawable.unknown;
            case NotConnectedToWatch:
                return R.drawable.disconnected;
            default:
                return R.drawable.disconnected;
        }
    }

    private Notification getNotification(int icon, String msg) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(mApplicationContext)
                .setSmallIcon(icon)
                .setContentTitle(mApplicationContext
                        .getResources()
                        .getString(R.string.app_name))
                .setStyle(new NotificationCompat.
                        BigTextStyle().
                        bigText(msg)).
                        setContentText(msg).
                        setOngoing(true).
                        setPriority(NotificationCompat.PRIORITY_MAX - 1);

        return builder.build();
    }

    public void removeNotification() {
        mNotificationManager.cancel(CONNECTIVITY_STATUS_NOTIFICATION_ID);
    }

    public void sendNotification(ConnectivityStatusIndicatorType type) {
        Notification notification = getNotification(type);
        mNotificationManager.notify(CONNECTIVITY_STATUS_NOTIFICATION_ID, notification);
    }

}
