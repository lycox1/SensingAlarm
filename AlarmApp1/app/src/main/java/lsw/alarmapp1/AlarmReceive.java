package lsw.alarmapp1;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

/**
 * Created by user on 2015-11-01.
 */
public class AlarmReceive extends BroadcastReceiver {

        private static final String LOG_TAG = "BLEScan_AlarmReceive";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
            Log.d(LOG_TAG, "onReceive is called");
        Toast.makeText(context, "Alarm Received Called", Toast.LENGTH_LONG).show();
/*
        NotificationCompat.Builder mBuilder =  new NotificationCompat.Builder(View.getContext())
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("My notification")
                .setContentText("Hello World!").setNumber(3);

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(View.getContext(), MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
*/
        /*
        NotificationManager notifier = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notify = new Notification(R.drawable.icon, "text",
                System.currentTimeMillis());

        Intent intent2 = new Intent(context, MainActivity.class);
        PendingIntent pender = PendingIntent
                .getActivity(context, 0, intent2, 0);

        //notify.setLatestEventInfo(context, "alimtitle", "hackjang", pender);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

        notify.flags |= Notification.FLAG_AUTO_CANCEL;
        notify.vibrate = new long[]{200, 200, 500, 300};
        // notify.sound=Uri.parse("file:/");
        notify.number++;

        notifier.notify(1, notify);
        */
    }
}
