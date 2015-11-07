package lsw.alarmapp1;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import adparser.AdElement;
import adparser.AdParser;
//import lsw.alarmapp1.R;

public class MainActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener {

//public class MainActivity extends ListActivity {

//    int hour, minute;
    private TimePicker mTime;
//    private DatePicker mDate;
    private GregorianCalendar mCalendar;;
    TextView deviceNameView, addressView, rssiView;
    SampleAlarmReceiver alarm = new SampleAlarmReceiver();

    private static final String LOG_TAG = "BLEScan";
//    private LeDeviceListAdapter mLeDeviceListAdapter;
    private Handler mHandler;
    private boolean mScanning;
    private BluetoothAdapter mBluetoothAdapter;

    private static final int REQUEST_ENABLE_BT = 1;
    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        getActionBar().setTitle("BLE Device Scan");

        mCalendar = new GregorianCalendar();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

//        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        findViewById(R.id.set).setOnClickListener(mClickListener);
        findViewById(R.id.reset).setOnClickListener(mClickListener);

        deviceNameView = (TextView)findViewById(R.id.DeviceName);
        addressView = (TextView)findViewById(R.id.Address);
        rssiView = (TextView)findViewById(R.id.Rssi);

        mHandler = new Handler();

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE can not be supported in this device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "BLE can be supported in this device.", Toast.LENGTH_SHORT).show();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "This device don't have bluetooth adapter", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
//        if (!mScanning) {
            menu.findItem(R.id.menu1).setVisible(true);
            menu.findItem(R.id.menu2).setVisible(true);
            menu.findItem(R.id.menu3).setVisible(true);
/*        } else {
            menu.findItem(R.id.menu1).setVisible(true);
            menu.findItem(R.id.menu2).setVisible(false);
            menu.findItem(R.id.menu3).setActionView(
                    R.layout.actionbar_indeterminate_progress); // res/layout/actionbar_indeterminate_progress.xml
        }
        */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Log.d(LOG_TAG, "onOptionsItemSelected menu_scan");
//                mLeDeviceListAdapter.clear();
//                scanLeDevice(true);
                break;
            case R.id.menu2:
                Log.d(LOG_TAG, "onOptionsItemSelected menu_stop");
//                scanLeDevice(false);
                break;
            case R.id.menu3:
                Log.d(LOG_TAG, "onOptionsItemSelected menu_stop");
//                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
//        mLeDeviceListAdapter = new LeDeviceListAdapter();
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

    @Override
    protected void onPause() {
        super.onPause();
        scanLeDevice(false);
//        mLeDeviceListAdapter.clear();
    }

/*    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
*/
/*        final Intent intent = new Intent(this, DeviceControlActivity.class);
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
            mScanning = false;
        }
        startActivity(intent);
        */
//    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                    invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        Log.d(LOG_TAG, "scanLeDevice " + enable );
		invalidateOptionsMenu();
	}

    /*
    static class ViewHolder {
        TextView deviceName;
        TextView deviceAd;
        TextView deviceRssi;
        TextView deviceAddress;
    }
    */
    class DeviceHolder {
        BluetoothDevice device;
        String additionalData;
        int rssi;

        public DeviceHolder(BluetoothDevice device, String additionalData, int rssi) {
            this.device = device;
            this.additionalData = additionalData;
            this.rssi = rssi;
        }
    }
    /*
    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<DeviceHolder> mLeHolders;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mLeHolders = new ArrayList<DeviceHolder>();
            mInflator = MainActivity.this.getLayoutInflater();
        }

        public void addDevice(DeviceHolder deviceHolder) {
            if(!mLeDevices.contains(deviceHolder.device)) {
                mLeDevices.add(deviceHolder.device);
                mLeHolders.add(deviceHolder);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
            mLeHolders.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Log.d(LOG_TAG, "getView() is called ");
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceAd = (TextView) view.findViewById(R.id.device_ad);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            DeviceHolder deviceHolder = mLeHolders.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.deviceAd.setText(deviceHolder.additionalData);
            viewHolder.deviceRssi.setText("rssi: "+Integer.toString(deviceHolder.rssi));
            return view;
        }
    }
    */
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

                    runOnUiThread(new DeviceAddTask(deviceHolder));
                }
            };

    class DeviceAddTask implements Runnable {
        DeviceHolder deviceHolder;

        public DeviceAddTask( DeviceHolder deviceHolder ) {
            this.deviceHolder = deviceHolder;
        }

        public void run() {
            String recoDevice = new String("RECO");
//            Log.d(LOG_TAG, "DeviceAddTask recoDevice " + deviceHolder.device.getName());

            if(deviceHolder.device.getName() != null) {
                if (deviceHolder.device.getName().equals(recoDevice)) {
                    Log.d(LOG_TAG, "DeviceAddTask deviceNameView setText " + deviceHolder.device.getName());
                    deviceNameView.setText("device name : " + deviceHolder.device.getName());
                    Log.d(LOG_TAG, "DeviceAddTask addressView setText " + deviceHolder.device.getAddress());
                    addressView.setText("address : " + deviceHolder.device.getAddress());
                    Log.d(LOG_TAG, "DeviceAddTask rssiView setText " + Integer.toString(deviceHolder.rssi));
                    rssiView.setText("rssi : " + Integer.toString(deviceHolder.rssi));
                }
            }
//            mLeDeviceListAdapter.addDevice(deviceHolder);
//            mLeDeviceListAdapter.notifyDataSetChanged();
        }
    }



/*    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }
*/
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
        mCalendar.set (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
        Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {
//            EditText hour = (EditText)findViewById(R.id.alarmHour);
//            EditText minute = (EditText)findViewById(R.id.alarmMinute);

            switch (v.getId()) {
                case R.id.set:
                    Log.i(LOG_TAG,"onClick set button click");
                    alarm.setAlarm(MainActivity.this, mCalendar);
                    break;

/*                case R.id.alarmSet:
                    String hourString = hour.getText().toString();
                    int hourInt = Integer.valueOf(hourString);
                    String minutesString = minute.getText().toString();
                    int minuteInt = Integer.valueOf(minutesString);

                    Log.d(LOG_TAG, "onClick: alarm.setAlarm");
                    alarm.setAlarm(MainActivity.this, hourInt, minuteInt);

//                    setAlarm(MainActivity.this, 11);
//                    Toast.makeText(MainActivity.this, hourString, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(MainActivity.this, minutesString, Toast.LENGTH_SHORT).show();
                    /*
                    Intent intent = new Intent(MainActivity.this,
                            AlarmReceive.class);
                    PendingIntent pender = PendingIntent.getBroadcast(
                            MainActivity.this, 0, intent, 0);
                    Calendar calendar = Calendar.getInstance();
                    Log.d(LOG_TAG, "onClick set alarm 2015 11 2 , " + hourInt + " " + minuteInt);
                    calendar.set(2015, 11, 2, hourInt, minuteInt);
                    alarm.set(AlarmManager.RTC, calendar.getTimeInMillis(), pender);
                    */
//                    break;

            }
        }
    };

    /*
    private void setAlarm(Context context, long second){

        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(getApplicationContext(), AlarmReceive.class);
        PendingIntent pIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        Log.i(LOG_TAG, "setAlarm()" + System.currentTimeMillis());
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, pIntent);
    }
    */

}
