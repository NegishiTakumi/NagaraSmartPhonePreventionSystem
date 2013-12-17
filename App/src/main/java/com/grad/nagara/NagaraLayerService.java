package com.grad.nagara;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.Space;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class NagaraLayerService extends IntentService implements SensorEventListener,TextToSpeech.OnInitListener{
    public static final String TAG= "__NagaraLayerServiceDebug";
    View mView;
    WindowManager mWindowManager;
    NagaraLayerConfig mConfig;
    LayoutInflater layoutInflater;
    WindowManager.LayoutParams params;
    /*加速度センサー実装のための変数↓*/
    MainActivity mainActivity;
    AcceleratorManager mAManager;
    LatlngManager mLManager;
    SensorManager sensorManager;
    ClipboardManager clipboardManager;
    TextToSpeech mTts;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.

     */
    public NagaraLayerService() {
        super("NagaraLayerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mConfig = new NagaraLayerConfig(getApplicationContext());

        /*
        List<Sensor> sensors = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        Sensor sensor = sensors.get(0);
        if(sensors.size() > 0){
            sensorManager.registerListener((SensorEventListener)this,sensor,SensorManager.SENSOR_DELAY_UI);
        }
        */

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStart(intent,startId);
        mView = LayoutInflater.from(this).inflate(R.layout.overlay,null);
        TextView textView = (TextView) mView.findViewById(R.id.textView1);

        mTts = new TextToSpeech(this,this);
        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
                Log.d(TAG, clipboardManager.getText().toString() + "tt");
                StartSpeech(clipboardManager.getText().toString());

            }
        });

        sensorManager = (SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        mAManager = new AcceleratorManager(textView,sensorManager,this);
        mAManager.onResume(this);
        mConfig.setColor(3);
        //int color = mConfig.getColor();
        //Log.d(this.TAG,color+"");
        //setTextViewColor(textView, color);

        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        mWindowManager.addView(mView, params);

        IntentFilter filter = new IntentFilter();
        filter.addAction("MY_ACTION");
        registerReceiver(broadcastReceiver, filter);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            unregisterReceiver(broadcastReceiver);
        } catch (IllegalArgumentException e) {

        }
        sensorManager.unregisterListener(this);
        mTts.stop();
        mTts.shutdown();

        if (mWindowManager != null) {
            mWindowManager.removeView(mView);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    } 
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onHandleIntent(Intent intent){
            Log.d(this.TAG,"on Handle Intent called");
           //Update関数と同等の働きをするwhile(true)
            while (true)
            {
           // intent.putExtra("message",1);
            //intent.setAction("MYACTION");
            //getBaseContext().sendBroadcast(intent);
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("_m_a",intent.getAction());
            int mode = intent.getIntExtra("colorMode",R.color.default_color);
            mConfig.setColor(mode);


            if(intent.getAction().equals("CLIPBOARD_ACTION")){
                String str = intent.getStringExtra("ClipboardString");

            }

            TextView textView = (TextView)mView.findViewById(R.id.textView1);
            setTextViewColor(textView,mConfig.getColor());

        }
    };

    private void setTextViewColor(TextView textView,int color){
        textView.setBackgroundColor(Color.argb(Color.alpha(color), Color.red(color),
                Color.green(color), Color.blue(color)));
        textView.setTextColor(Color.argb(Color.alpha(color),255,255,255));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        mAManager.onAcceleratorSensorChanged(event);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onInit(int status) {
        // TextToSpeechが使える環境？？
        if ( status == TextToSpeech.SUCCESS ) {
            // お話するための言語コードを設定する
            int result = mTts.setLanguage( Locale.JAPAN );
            // 対応する言語データがない、または対応外の言語の場合はエラーとする
            if ( result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED ) {
                // エラーメッセージをログに出力
            }
            else {
            }
        }
        // そもそも初期化に失敗していた場合はログ出力
        else {
        }
    }


    public void StartSpeech(String speechText){
        if ( speechText.length() > 0 ) {
            mTts.speak(speechText, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
}
