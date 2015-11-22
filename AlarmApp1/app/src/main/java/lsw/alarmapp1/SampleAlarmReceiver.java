package lsw.alarmapp1;

/**
 * Created by user on 2015-11-02.
 */

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

/**
 * When the alarm fires, this WakefulBroadcastReceiver receives the broadcast Intent
 * and then starts the IntentService {@code SampleSchedulingService} to do some work.
 */
//public class SampleAlarmReceiver extends WakefulBroadcastReceiver {
public class SampleAlarmReceiver extends BroadcastReceiver {
    // The app's AlarmManager, which provides access to the system alarm services.
    private AlarmManager alarmMgr;
    // The pending intent that is triggered when the alarm fires.
    private PendingIntent alarmIntent;
//    public AlarmActivity alarmActivity = new AlarmActivity();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BLEScan", "onReceive is called");
        Toast.makeText(context, "SampleAlarmReceiver Called", Toast.LENGTH_LONG).show();
        Intent AlarmActivityIntent = new Intent(context, AlarmActivity.class);
        AlarmActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(AlarmActivityIntent);
        //scanLeDevice(true);
    }

    // BEGIN_INCLUDE(set_alarm)
    /**
     * Sets a repeating alarm that runs once a day at approximately 8:30 a.m. When the
     * alarm fires, the app broadcasts an Intent to this WakefulBroadcastReceiver.
     * @param context
     */
    public void setAlarm(Context context, Calendar calendar) {
        Log.d("BLEScan", "onClick: alarm.setAlarm sampleAlarmReceiver");
        alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SampleAlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        /*
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        // Set the alarm's trigger time to 8:30 a.m.
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);

        String dateToString , timeToString ;

        dateToString = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        timeToString = String.format("%02d:%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), calendar.get(Calendar.SECOND));
        Log.i("BLEScan", "Date&Time : " + dateToString + ", " + timeToString);
        */

        alarmMgr.set(AlarmManager.RTC, calendar.getTimeInMillis(), alarmIntent);

    }
    // END_INCLUDE(set_alarm)

    /**
     * Cancels the alarm.
     * @param context
     */
    // BEGIN_INCLUDE(cancel_alarm)
    public void cancelAlarm(Context context) {
        // If the alarm has been set, cancel it.
        if (alarmMgr!= null) {
            alarmMgr.cancel(alarmIntent);
        }
    }
    // END_INCLUDE(cancel_alarm)
}