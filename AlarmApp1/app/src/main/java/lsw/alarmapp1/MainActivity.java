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

public class MainActivity extends AppCompatActivity implements TimePicker.OnTimeChangedListener {

    private TimePicker mTime;
    private GregorianCalendar mCalendar;;
    SampleAlarmReceiver mAlarm = new SampleAlarmReceiver();
    private static final String LOG_TAG = "SensingAlarm_MainActivity";
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = getApplicationContext();
        mCalendar = new GregorianCalendar();
        mCalendar.setTimeInMillis(System.currentTimeMillis());

//        mDate.init (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);
        mTime = (TimePicker)findViewById(R.id.time_picker);
        mTime.setCurrentHour(mCalendar.get(Calendar.HOUR_OF_DAY));
        mTime.setCurrentMinute(mCalendar.get(Calendar.MINUTE));
        mTime.setOnTimeChangedListener(this);

        findViewById(R.id.set).setOnClickListener(mClickListener);
        findViewById(R.id.reset).setOnClickListener(mClickListener);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE can not be supported in this device.", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "BLE can be supported in this device.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
            menu.findItem(R.id.menu1).setVisible(true);
            menu.findItem(R.id.menu2).setVisible(true);
            menu.findItem(R.id.menu3).setVisible(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu1:
                Log.d(LOG_TAG, "onOptionsItemSelected Device Pairing");
                Intent intent = new Intent(mContext, DevicePairingActivity.class);
                startActivity(intent);
                break;
            case R.id.menu2:
                Log.d(LOG_TAG, "onOptionsItemSelected menu_stop");
                break;
            case R.id.menu3:
                Log.d(LOG_TAG, "onOptionsItemSelected menu_stop");
                break;
        }
        return true;
    }

/*    //일자 설정 클래스의 상태변화 리스너
    public void onDateChanged (DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        mCalendar.set (year, monthOfYear, dayOfMonth, mTime.getCurrentHour(), mTime.getCurrentMinute());
        Log.i("HelloAlarmActivity", mCalendar.getTime().toString());
    }
*/
    //시각 설정 클래스의 상태변화 리스너
    public void onTimeChanged (TimePicker view, int hourOfDay, int minute) {
//        mCalendar.set (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), hourOfDay, minute);
        mCalendar.set (mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE),mCalendar.get(Calendar.SECOND) + 3);
        Log.i("HelloAlarmActivity",mCalendar.getTime().toString());
    }

    Button.OnClickListener mClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.set:
                    Log.i(LOG_TAG,"onClick set button click");
                    mAlarm.setAlarm(MainActivity.this, mCalendar);
                    break;
            }
        }
    };
}
