package lsw.alarmapp1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

public class AlarmActivity extends AppCompatActivity {

    static MediaPlayer mPlayer;
    public String MediaPath = new String("/sdcard/bell.mp3");
    private static final String LOG_TAG = "SensingAlarm_AlarmActivity";
    //private BluetoothAdapter mBluetoothAdapter;
    private static final int REQUEST_ENABLE_BT = 1;
    TextView deviceNameView, addressView, rssiView;
    ArrayList<DeviceHolder> mDeviceHolderList = new ArrayList<DeviceHolder>();

    Button StopAlarmButton;
    BLEScan mBLEScan;
    Handler mHandler;
    WakeUpDecision mWakeUpDecision;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(LOG_TAG, "onCreate AlarmActivity");

        setContentView(R.layout.activity_alarm);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        StopAlarmButton = (Button)findViewById(R.id.StopAlarm);
        StopAlarmButton.setOnClickListener(mClickListener);

        try {
        mPlayer = new MediaPlayer();
        mPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
        mPlayer.setDataSource(MediaPath);
        mPlayer.prepare();
        mPlayer.start();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        deviceNameView = (TextView)findViewById(R.id.DeviceName);
        addressView = (TextView)findViewById(R.id.Address);
        rssiView = (TextView)findViewById(R.id.Rssi);

        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Log.d(LOG_TAG, "handleMessage what" + msg.what);

                switch(msg.what) {
                    case BLEScan.BLESCAN_DEVICE_DETECTED:
                        DeviceHolder deviceHolder = (DeviceHolder)msg.obj;
                        Log.d(LOG_TAG, "handleMessage device name " + deviceHolder.device.getName());
                        deviceNameView.setText("device name : " + deviceHolder.device.getName());
                        addressView.setText("address : " + deviceHolder.device.getAddress());
                        rssiView.setText("rssi : " + Integer.toString(deviceHolder.rssi));

                        mWakeUpDecision.addDetection(deviceHolder);
                        break;
                    case WakeUpDecision.USER_WAKEUP:
                        mPlayer.stop();
                        mPlayer.release();
                        Log.d(LOG_TAG, "handleMessage USER_WAKEUP");
                        break;

                }
            }
        };

        mBLEScan = new BLEScan(getApplicationContext(), mHandler);
        mWakeUpDecision = new WakeUpDecision(mHandler);
        mWakeUpDecision.start();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBLEScan.mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        mBLEScan.scanLeDevice(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
/*
            if (!mBLEScan.mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.StopAlarm:
                    mPlayer.stop();
                    mPlayer.release();
                    Log.i(LOG_TAG, "onClick StopAlarm button click");
                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(LOG_TAG, "onDestroy");
        mWakeUpDecision.mThreadRun = false;
    }
}
