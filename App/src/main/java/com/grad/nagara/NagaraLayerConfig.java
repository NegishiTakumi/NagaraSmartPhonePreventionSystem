package com.grad.nagara;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.Log;

/**
 * Created by CST on 13/12/09.
 */

public class NagaraLayerConfig {
    public static final String KEY_COLOR = "key_color";

    private Context mContext;
    private SharedPreferences mSharedPreferences;

    public NagaraLayerConfig(Context context){
        this(context, PreferenceManager.getDefaultSharedPreferences(context));
    }
    public NagaraLayerConfig(Context context,
                                  SharedPreferences sharedPreferences) {
        mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        initialize();
    }

    private void initialize() {
        initialize(false);
    }
    private void initialize(boolean force){
        mSharedPreferences.edit()
                .putInt(KEY_COLOR,mContext.getResources().getColor(R.color.default_color))
        .commit();
    }

    public int getColor(){
        return mSharedPreferences.getInt(KEY_COLOR,mContext.getResources().getInteger(R.color.default_color));
    }
    public Context getmContext(){
        return mContext;
    }

    public void setColor(int colorMode){
        int color = -1;

        switch(colorMode){
            case 0:
                color = mContext.getResources().getInteger(R.color.default_color); break;
            case 1:
                color = mContext.getResources().getInteger(R.color.bg20); break;
            case 2:
                color = mContext.getResources().getInteger(R.color.bg40);break;
            case 3:
                color =  mContext.getResources().getInteger(R.color.bg60);break;
            case 4:
                color =  mContext.getResources().getInteger(R.color.bg80);break;
            case 5:
                color =  mContext.getResources().getInteger(R.color.bg90);break;
            default:
                color = mContext.getResources().getInteger(R.color.default_color);break;
        }


        mSharedPreferences.edit()
                .putInt(KEY_COLOR,color).commit();
    }

}
