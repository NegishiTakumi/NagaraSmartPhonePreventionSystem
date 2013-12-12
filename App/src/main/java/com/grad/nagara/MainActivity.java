package com.grad.nagara;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
//import android.support.v4.widget.SearchViewCompatIcs;
import android.text.Layout;
import android.view.WindowManager.LayoutParams;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.hardware.*;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.*;

import static android.provider.Settings.*;

public class MainActivity extends ActionBarActivity implements SensorEventListener,View.OnClickListener{
    public static String TAG = "_m_MainActivity";
    public static int MODE = 2;
    private TextView acceValueText,gpsValueText,distValueText,naturalizedText;
    private Button dbButton,stopButton,paleButton;
    private LocationManager lm;
    private SensorManager manager;
    private AcceleratorManager a_manager;
    private LatlngManager l_manager;

    IntentFilter intentFilter;
    NagaraLayerReceiver receiver;

    //初期化
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        {
            lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
            gpsValueText = (TextView)findViewById(R.id.gpsTextView);
            acceValueText = (TextView)findViewById(R.id.acceTextView);
            distValueText = (TextView)findViewById(R.id.distTextView);
            naturalizedText = (TextView)findViewById(R.id.NatralizedTextView);
            dbButton = (Button)findViewById(R.id.debugButton);
            dbButton.setOnClickListener(this);
            stopButton = (Button)findViewById(R.id.StopButton);
            stopButton.setOnClickListener(this);
            paleButton = (Button)findViewById(R.id.paleButton);
            paleButton.setOnClickListener(this);
            manager = (SensorManager)getSystemService(SENSOR_SERVICE);
            a_manager = new AcceleratorManager(acceValueText,naturalizedText,manager);
            l_manager = new LatlngManager(gpsValueText,distValueText,lm);
        }

    }

    //Sensor & GPS方面
    //------------------------------------------------------------------------------------

    @Override
    public void onResume(){
        super.onResume();
        a_manager.onResume(this);
        l_manager.onResume(this);
    }

    @Override//SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        a_manager.onAcceleratorSensorChanged(sensorEvent);
    }

    @Override//SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int i) {
    }

    //System方面
    //------------------------------------------------------------------------------------
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if(v == dbButton){
            startService(new Intent(MainActivity.this, NagaraLayerService.class));
            FlipButton();
            Log.d("IntentTest", "StartIntent1 - cst");
        }
        if(v==stopButton){
            stopService(new Intent(MainActivity.this,NagaraLayerService.class));
            FlipButton();
            Log.d("IntentTest", "StopIntent1&2 - cst");
        }
        if(v==paleButton){
            int i = new Random().nextInt(5);
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("colorMode", i);
            Log.d(TAG,i+"");
            broadcastIntent.setAction("MY_ACTION");
            getBaseContext().sendBroadcast(broadcastIntent);
        }
    }

    private void FlipButton(){
        boolean running = isServiceRunning(this);
        dbButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }

    public static boolean isServiceRunning(Context context) {
        ActivityManager activityManager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> services = activityManager
                .getRunningServices(Integer.MAX_VALUE);
        final String mServiceName = NagaraLayerService.class
                .getCanonicalName();

        for (ActivityManager.RunningServiceInfo info : services) {
            if (mServiceName.equals(info.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    @Override
    public void onStop(){
        super.onStop();
        //manager.unregisterListener(this);

    }
    @Override
    public void onDestroy(){
        super.onDestroy();
        stopService(new Intent(MainActivity.this,NagaraLayerService.class));
    }



}