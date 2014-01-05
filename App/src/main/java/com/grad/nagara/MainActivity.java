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
import android.os.Environment;
import android.provider.Settings;
//import android.support.v4.widget.SearchViewCompatIcs;
import android.speech.tts.TextToSpeech;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.grad.nagara.libsvm.svm;

import java.util.*;

import static android.provider.Settings.*;

public class MainActivity extends ActionBarActivity implements SensorEventListener,View.OnClickListener{
    public static String TAG = "__MainActivity";
    private TextView levelTextView;
    private Button dbButton,stopButton,paleButton;
    private LocationManager lm;
    private SensorManager manager;
    private AcceleratorManager a_manager;
    private LatlngManager l_manager;
    private SeekBar seekBar;
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
            levelTextView = (TextView)findViewById(R.id.levelTextView);
            dbButton = (Button)findViewById(R.id.debugButton);
            dbButton.setOnClickListener(this);
            stopButton = (Button)findViewById(R.id.StopButton);
            stopButton.setOnClickListener(this);
            manager = (SensorManager)getSystemService(SENSOR_SERVICE);
            AcceleratorManager.mContext = this.getBaseContext();

            //被験者実験用
            seekBar = (SeekBar)findViewById(R.id.seekBar);
            seekBar.setMax(3);
            seekBar.setProgress(0);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    levelTextView.setText("MODE" + String.valueOf(seekBar.getProgress()));
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    AcceleratorManager.MODE = seekBar.getProgress();
                }
            });
        }

    }

    //Sensor & GPS方面
    //------------------------------------------------------------------------------------

    @Override
    public void onResume(){
        super.onResume();
       // a_manager.onResume(this);
       // l_manager.onResume(this);
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
        }
        if(v==stopButton){
            stopService(new Intent(MainActivity.this,NagaraLayerService.class));
            FlipButton();
        }
        if(v==paleButton){
            for(int i = 0; i <a_manager.getDataListZ().length; i++){
            Debugger.Print(a_manager.getDataListZ()[i] + "\n","zDataEX");
            }
            Debugger.Print("\n\n","zDataEX");
        }
    }
    private void SVMTester(){


        FeatureValue f1 = a_manager.getF1(1);
        //str += "1:"+f1.var;
        //Debugger.Print(classIndex+ " " + f.avr+","+f.var+","+f.max+","+f.min+"\n",1);
        FeatureValue f2 = a_manager.getF1(2);
        //str += " 2:"+ f.max + " 3:" + f.min + " 4:"+ f.var + "\n";
      //  mSVM_manager = new MySVMManager(new float[]{f1.var,f2.max,f2.min,f2.var});
      // Debugger.Print("1:"+f1.var +" 2:"+ f2.max + " 3:" + f2.min + " 4:"+ f2.var + " = " + mSVM_manager.getAccuration()+"\n","getAccurationData");
        //int i = (int)mSVM_manager.getAccuration();
        int temp = 0;
        int i = 0;
        if(f2.min < -25 && f2.max > 25 && f2.var > 300){
            i = 3;
        }
        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("colorMode", i);
        broadcastIntent.setAction("MY_ACTION");
        getBaseContext().sendBroadcast(broadcastIntent);
            Log.d("_m_a",f2.min + "," + f2.max + "," + f2.var);
//        Log.d("_m_a",mSVM_manager.getAccuration() + "");

    }
    private void RecordData(){
        int classIndex = 2;
        String str = classIndex + " ";

        FeatureValue f = a_manager.getF1(1);
        str += "1:"+f.var;


        //Debugger.Print(classIndex+ " " + f.avr+","+f.var+","+f.max+","+f.min+"\n",1);
        f = a_manager.getF1(2);
        str += " 2:"+ f.max + " 3:" + f.min + " 4:"+ f.var + "\n";

        Debugger.Print(str,"SeisiLog");
        //Debugger.Print(f.avr+","+f.var+","+f.max+","+f.min+"\n",2);
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