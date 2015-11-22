package lsw.alarmapp1;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import adparser.AdElement;
import adparser.AdParser;

/**
 * Created by user on 2015-11-09.
 */
public class BLEScan {

    private static final String LOG_TAG = "SensingAlarm_BLEScan";

    private Handler mHandler;
    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;
    boolean mScanning;
    public BluetoothAdapter mBluetoothAdapter;
    Context mContext;
    ArrayList<DeviceHolder> mDeviceHolderList;

    final static int BLESCAN_DEVICE_DETECTED = 0;

    public BLEScan(Context context,Handler handler) {
        Log.d(LOG_TAG, "onCreate BLEScan");
//        mHandler = new Handler();
        mHandler = handler;
        mContext = context;
        final BluetoothManager bluetoothManager =
                (BluetoothManager) mContext.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(mContext, "This device don't have bluetooth adapter", Toast.LENGTH_SHORT).show();
            //finish();
            return;
        }

    }

public void scanLeDevice(final boolean enable) {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        Log.d(LOG_TAG, "scanLeDevice postDelay");
                        mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    }
                }, SCAN_PERIOD);
                Log.d(LOG_TAG, "scanLeDevice debug spot 1 ");
                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);
                Log.d(LOG_TAG, "scanLeDevice debug spot 2 ");
            } else {
                mScanning = false;
                mBluetoothAdapter.stopLeScan(mLeScanCallback);
            }
            Log.d(LOG_TAG, "scanLeDevice debug spot 3 ");
            Log.d(LOG_TAG, "scanLeDevice " + enable );
        }

    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
                    String deviceName = device.getName();
                    StringBuffer b = new StringBuffer();
                    int byteCtr = 0;
                    for( int i = 0 ; i < scanRecord.length ; ++i ) {
                        if( byteCtr > 0 )
                            b.append( " ");
                        b.append( Integer.toHexString( ((int)scanRecord[i]) & 0xFF));
                        ++byteCtr;
                        if( byteCtr == 8 ) {
                            Log.d(LOG_TAG, new String(b));
                            byteCtr = 0;
                            b = new StringBuffer();
                        }
                    }
                    ArrayList<AdElement> ads = AdParser.parseAdData(scanRecord);
                    StringBuffer sb = new StringBuffer();
                    for( int i = 0 ; i < ads.size() ; ++i ) {
                        AdElement e = ads.get(i);
                        if( i > 0 )
                            sb.append(" ; ");
                        sb.append(e.toString());
                    }
                    String additionalData = new String(sb);
                    Log.d(LOG_TAG, "additionalData: " + additionalData);
                    DeviceHolder deviceHolder = new DeviceHolder(device,additionalData,rssi);

                    //mContext.runOnUiThread(new DeviceAddTask(deviceHolder));
                    Thread t = new Thread(new DeviceAddTask(deviceHolder));
                    t.start();
                }
            };


    class DeviceAddTask implements Runnable {
        DeviceHolder deviceHolder;

        public DeviceAddTask( DeviceHolder deviceHolder ) {
            this.deviceHolder = deviceHolder;
        }

        public void run() {
            String recoDevice = new String("RECO");
            Log.d(LOG_TAG, "DeviceAddTask recoDevice " + deviceHolder.device.getName());

            if(deviceHolder.device.getName() != null) {
                if (deviceHolder.device.getName().equals(recoDevice)) {
                    Message msg = mHandler.obtainMessage();
                    msg.what = BLESCAN_DEVICE_DETECTED;
                    msg.obj = deviceHolder;
                    mHandler.sendMessage(msg);
                    Log.d(LOG_TAG, "DeviceAddTask deviceNameView setText " + deviceHolder.device.getName());
                    Log.d(LOG_TAG, "DeviceAddTask addressView setText " + deviceHolder.device.getAddress());
                    Log.d(LOG_TAG, "DeviceAddTask rssiView setText " + Integer.toString(deviceHolder.rssi));
                }
            }
        }
    }
}
