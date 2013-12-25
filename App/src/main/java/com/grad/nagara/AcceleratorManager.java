package com.grad.nagara;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

/**
 * Created by cst2 on 13/11/02.
 */
public class AcceleratorManager extends Activity  {
    private TextView textView,nTextView;
    private SensorManager manager;
    int count = 0;//DatalistX,Y,Zの初期化に必要なカウント
    int count2=0;//AcceDatanaturalizedSetの初期化に必要なカウント
    int count3 = 0;
    int Opacitystage = 0;
    public int getOpacityStage(){return  Opacitystage;}
    boolean isShake = false;
    public boolean getIsShake(){return isShake;}
    public void setIsShake(boolean value){isShake = value;}

    int elemcnt = 100;//配列群の要素数の一括定義
    private float[] dataListX = new float[elemcnt]; private float[] dataListY = new float[elemcnt]; private float[] dataListZ = new float[elemcnt];
    private float[] AcceDataGravity = new float[3];
    private float[] AcceDataNaturalized = new float[3];
    private float[] featureValueSet = new float[elemcnt];
    private float[] featureValue2Set = new float[elemcnt];
    private NagaraLayerService mNLS;
    public PowerManager powerManager;
    public void setPowerManager(PowerManager p){powerManager = p;}
    FeatureValue f1 = new FeatureValue();
    FeatureValue f2 = new FeatureValue();


    public AcceleratorManager(TextView acceTv,SensorManager manager, NagaraLayerService nls){
        textView = acceTv; //加速度を書くTextViewをセット
        this.manager = manager;
        this.mNLS = nls;

    }

    public AcceleratorManager(TextView acceTv,TextView nTv,SensorManager manager){
        textView = acceTv; //加速度を書くTextViewをセット
        nTextView = nTv;
        this.manager = manager;
    }
    public void onResume(SensorEventListener listener){
        List<Sensor> sensors = manager.getSensorList(Sensor.TYPE_ACCELEROMETER);
        if(sensors.size() >0){
            Sensor s = sensors.get(0);
            manager.registerListener(listener,s,SensorManager.SENSOR_DELAY_UI);
        }

    }

    public void onAcceleratorSensorChanged(SensorEvent sensorEvent){
        float rx,ry,rz;
        if(sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            rx = sensorEvent.values[SensorManager.DATA_X];
            ry = sensorEvent.values[SensorManager.DATA_Y];
            rz = sensorEvent.values[SensorManager.DATA_Z];
            String str = "加速度センサー値："+
                    "\nX軸："+sensorEvent.values[SensorManager.DATA_X]+
                    "\nY軸："+sensorEvent.values[SensorManager.DATA_Y]+
                    "\nZ軸："+sensorEvent.values[SensorManager.DATA_Z]+
                    "\ncount="+count+"\ncount2="+count2;
            textView.setText(str);
            if(isShake == false){
            //datalistXYZに値を格納する。
            RawAcceDataSetUpdate(rx, ry, rz, count);
            //datalistの値を格納終了したことからV_gとV_nを計算できるようになるので、それを計算する。
            if(count >= elemcnt){
                AcceDataGravity = GetAcceDataGravityUpdate();
                AcceDataNaturalized = GetAcceDataNaturalizedUpdate();
                //特徴量の値を計算してfeatureValueSet(float型)に格納する。そしてあふれた場合更新する。
                FeatureResourceDataUpdate(AcceDataGravity,AcceDataNaturalized,count2);
                //特徴量の集合があふれるようになったとき、特徴量の平均、最大、分散、最小を計算できるようになるので、これをf1,f2に格納する。
                if(count2 >=elemcnt){
                    setFeatureValueData(f1,featureValueSet);
                    setFeatureValueData(f2,featureValue2Set);

                    if(mNLS != null){
                    setServiceOpacity(count2,mNLS);
                    OpacityInit();
                        if(count2-100> count3 && Opacitystage >=4)
                        ShakeEventHandle();
                    }
                }
                count2++;
            }

        count++;
            }
        }
    }
    /*XYZ軸それぞれの加速度の生データを更新する。
    * ---------------------------------------------------------------------*/
    private void RawAcceDataSetUpdate(float x,float y,float z,int cnt){
        if( cnt < elemcnt){
            dataListX[cnt] = x;
            dataListY[cnt] = y;
            dataListZ[cnt] = z;
        }
        else {
            dataListX = ExCalc.InsertArrFirst(x,dataListX);
            dataListY = ExCalc.InsertArrFirst(y,dataListY);
            dataListZ = ExCalc.InsertArrFirst(z,dataListZ);
        }
    }
    /*float型の特徴量(||V_n||とV_n*V_g)を計算し、それを特徴量の集合(float型)として更新する。
    * ----------------------------------------------------------*/
    private void FeatureResourceDataUpdate(float[] vn,float[] vg,int cnt){
        float temp = vn[0]*vn[0]+vn[1]*vn[1]+vn[2]*vn[2];
        float temp2 = vn[0]*vg[0]+vn[1]*vg[1]+vn[2]*vg[2];
        if(cnt < elemcnt){
            featureValueSet[cnt] = temp;
            featureValue2Set[cnt] = temp2;
        }
        else{
            featureValueSet = ExCalc.InsertArrFirst(temp,featureValueSet);
            featureValue2Set = ExCalc.InsertArrFirst(temp2,featureValue2Set);
        }

    }
    /*重力としてXYZそれぞれの平均値を取得する。それを配列として返す。
    * -----------------------------------------------------------*/
    private float[] GetAcceDataGravityUpdate(){
        float avrX = ExCalc.GetArrAvr(dataListX);
        float avrY = ExCalc.GetArrAvr(dataListY);
        float avrZ = ExCalc.GetArrAvr(dataListZ);
        return new float[]{avrX,avrY,avrZ};
    }

    /*正規化した値を計算し、それを配列としてまとめて、返す。
    *----------------------------------------------------------- */
    private float[] GetAcceDataNaturalizedUpdate(){
        float nX = dataListX[0] - AcceDataGravity[0];
        float nY = dataListY[0] - AcceDataGravity[1];
        float nZ = dataListZ[0] - AcceDataGravity[2];
        return new float[]{nX,nY,nZ};
    }
    /*特徴量データを計算して返す。プリミティブ型ではないのでそのまま第一引数に格納される。
    * ----------------------------------------------------------*/
    private void setFeatureValueData(FeatureValue fv,float[] fvSet){
        //FeatureValue fv = new FeatureValue();
        fv.min = ExCalc.GetArrMin(fvSet);
        fv.max = ExCalc.GetArrMax(fvSet);
        fv.avr = ExCalc.GetArrAvr(fvSet);
        fv.var = ExCalc.GetArrVar(fvSet);
    }
    /*他クラスから特徴量にアクセスしたいときに必要なクラス。
    * ----------------------------------------------------------*/
    public FeatureValue getF1(int i){
       if(i ==1)
        return f1;
        if(i==2)
            return f2;
        return null;
    }

    public float[] getDataListX(){
        return dataListX;
    }
    public float[] getAcceDataNaturalized(){
        return AcceDataNaturalized;
    }

    /*
    * Serviceの色を変える。*/
    private void setServiceOpacity(int cnt,NagaraLayerService nls){
        if(cnt % (elemcnt - (elemcnt/10)*Opacitystage) == 0 ){
            if(DecisionTree.isWalk(dataListZ))
                Opacitystage++;
            else
                Opacitystage--;
            Opacitystage = Opacitystage > 5? 5:
                    Opacitystage < 0? 0 : Opacitystage;
            Log.d("_m_a","Opacity Stage = " + Opacitystage + "");

            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("colorMode", Opacitystage);
            broadcastIntent.setAction("MY_ACTION");
            nls.getBaseContext().sendBroadcast(broadcastIntent);

        }
    }
    private void ShakeEventHandle(){
        count3 = count2;
        float[] tmp = new float[20];
        System.arraycopy(dataListX,0,tmp,0,20);
        isShake = DecisionTree.isShake(tmp);
        Log.d("__Acce",isShake + "で");
        if(isShake){
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("colorMode",10);
            broadcastIntent.setAction("MY_ACTION");
            mNLS.getBaseContext().sendBroadcast(broadcastIntent);
        }

    }

    /* 電源を切った時全てを初期化する。
    * ------------------------------------------------------------------------*/
    private void OpacityInit(){
        if(!powerManager.isScreenOn()){
            if(Opacitystage > 0){
                Intent broadcastIntent = new Intent();
                broadcastIntent.putExtra("colorMode", 0);
                broadcastIntent.setAction("MY_ACTION");
                mNLS.getBaseContext().sendBroadcast(broadcastIntent);
            }
            Opacitystage = 0;
            isShake = false;

        }
        Log.d("_m_a",powerManager.isScreenOn()+"/OpacityStage^"+Opacitystage );
    }
 }
