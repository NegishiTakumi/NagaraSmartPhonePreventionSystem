package com.grad.nagara;

import android.app.Activity;
import android.content.Intent;
import android.speech.tts.TextToSpeech;
import android.util.Log;

import java.util.Locale;

/**
 * Created by CST on 13/12/17.
 */
public class ClipBoardToSpeech extends Activity  implements TextToSpeech.OnInitListener{
        public static final String TAG = "__ClipBoardToSpeech";
        private static final int TTS_DATA_CHECK_REQUEST_CODE = 9998;

    private TextToSpeech mTts;

    public ClipBoardToSpeech(NagaraLayerService nls,TextToSpeech.OnInitListener listener){
        mTts = new TextToSpeech(nls.getBaseContext(),listener);
        checkTTS();
        Log.d(TAG,"ctor??");
    }

    /* 与えられた値を読む。
    * ---------------------------------------------*/
    public void ReadStr(String text){
        if(0 < text.length()){
            if(mTts.isSpeaking()){mTts.stop();}
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
    }
    /**
     * TTSエンジンがインストール済かどうかを判定
     */
    private void checkTTS() {
        Intent intent = new Intent();
        intent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(intent, TTS_DATA_CHECK_REQUEST_CODE);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data){
        if(requestCode == TTS_DATA_CHECK_REQUEST_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                ReadStr("開始");
            }
        }
    }


    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            if (mTts.isLanguageAvailable(Locale.JAPAN) >=
                    TextToSpeech.LANG_AVAILABLE) {
                //アメリカ英語に設定
                mTts.setLanguage(Locale.US);
                Log.d(TAG,"onInitCalled");
            }
        }
    }


}
