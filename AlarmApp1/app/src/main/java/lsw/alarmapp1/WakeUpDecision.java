package lsw.alarmapp1;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by user on 2015-11-15.
 */
public class WakeUpDecision extends Thread {

    private static final String LOG_TAG = "SensingAlarm_WakeUpDecision";
    Handler mHandler;
    boolean mThreadRun = true;
    final static int USER_WAKEUP = 10;

    public WakeUpDecision(Handler handler) {
        Log.d(LOG_TAG, "WakeUpDecision constructor");
        mHandler = handler;
    }

    public void addDetection(DeviceHolder deviceHolder) {
        Log.d(LOG_TAG, "WakeUpDecision addDetection " + deviceHolder.device.getName());
    }

    public void run() {
        super.run();

        int testCount = 0;
        while(mThreadRun && !Thread.currentThread().isInterrupted())
        {
            try {
                testCount += 1;
                sleep(1000);
                Log.d(LOG_TAG, "DecisionMaking thread running");
                if(testCount > 10) {
                    Log.d(LOG_TAG, "DecisionMaking thread USER_WAKEUP");
                    Message msg = mHandler.obtainMessage();
                    msg.what = USER_WAKEUP;
                    mHandler.sendMessage(msg);
                    mThreadRun = false;
                }

            } catch(InterruptedException e) {
                Log.e(LOG_TAG, "InterruptedException in thread. " + e.getMessage());
                e.printStackTrace();

                Thread.currentThread().interrupt();
            }
        }

    }
}
