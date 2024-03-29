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
import android.os.PowerManager;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
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
    PowerManager powerManager;

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

    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public int onStartCommand(Intent intent,int flags, int startId){
        super.onStart(intent,startId);
        mView = LayoutInflater.from(this).inflate(R.layout.overlay,null);
      //  TextView textView = (TextView) mView.findViewById(R.id.textView1);

        mTts = new TextToSpeech(this,this);

        sensorManager = (SensorManager)getApplicationContext().getSystemService(SENSOR_SERVICE);
        powerManager = (PowerManager)getSystemService(POWER_SERVICE);
        mAManager = new AcceleratorManager(sensorManager,this);
        mAManager.onResume(this);
        mAManager.setPowerManager(powerManager);
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

        Log.d(TAG,mAManager.MODE + "");
        clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
        clipboardManager.addPrimaryClipChangedListener(new ClipboardManager.OnPrimaryClipChangedListener() {
            @Override
            public void onPrimaryClipChanged() {
            //もしクリップボードの文字列が替わって、OpacityStageが4以上ならやる。
                Log.d("__N",mAManager.getOpacityStage()+"%");
                if(mAManager.getOpacityStage() >=4){

                StartSpeech(clipboardManager.getText().toString());
                Intent clipBoardIntent = new Intent();
                clipBoardIntent.putExtra("isClipBoardChanged",true);
                clipBoardIntent.setAction("CLIPBOARD_CHANGED_ACTION");
                getBaseContext().sendBroadcast(clipBoardIntent);
                    mAManager.setIsShake(false);
            }
            }
        });


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

            int mode = intent.getIntExtra("colorMode",R.color.default_color);
            mConfig.setColor(mode);
            ImageView imageView = (ImageView)mView.findViewById(R.id.imageView);
            imageView.setBackground(getResources().getDrawable(GetBackGround(mode)));


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
    /*画像を返す奴。
    * --------------------------------------------------------------*/
    public int GetBackGround(int mode){
        int BGID = R.drawable.bg00n;
        switch (mode){
            case 0: BGID = R.drawable.bg00g;break;
            case 1: BGID = R.drawable.bg20g;break;
            case 2: BGID = R.drawable.bg40g;break;
            case 3: BGID = R.drawable.bg60g;break;
            case 4: BGID = R.drawable.bg80g;break;
            case 5: BGID = R.drawable.bg90g;break;
            case 10:BGID = R.drawable.bgarticleg;break;
            default:BGID = R.drawable.bg00g;break;
        }
        return BGID;
    }    
     /*【TTS】TTS初期化用
    * --------------------------------------------------------------*/
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

/*【TTS】引数に与えた文字列を読む。
* -----------------------------------------------------------------*/
    public void StartSpeech(String speechText){
        if ( speechText.length() > 0 ) {
            mTts.speak(speechText, TextToSpeech.QUEUE_ADD, null);
            Log.d("__D","isLog?");
        }
    }
}
