package com.grad.nagara;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

import java.util.List;

/**
 * Created by cst2 on 13/11/02.
 */
public class AcceleratorManager  {



    private TextView textView,nTextView;
    private SensorManager manager;
    int count = 0;//DatalistX,Y,Zの初期化に必要なカウント
    int count2=0;//AcceDatanaturalizedSetの初期化に必要なカウント

    int elemcnt = 100;//配列群の要素数の一括定義
    private float[] dataListX = new float[elemcnt]; private float[] dataListY = new float[elemcnt]; private float[] dataListZ = new float[elemcnt];
    private float[] AcceDataGravity = new float[3];
    private float[] AcceDataNaturalized = new float[3];
    private float[] featureValueSet = new float[elemcnt];
    private float[] featureValue2Set = new float[elemcnt];

    FeatureValue f1 = new FeatureValue();
    FeatureValue f2 = new FeatureValue();


    public AcceleratorManager(TextView acceTv,SensorManager manager){
        textView = acceTv; //加速度を書くTextViewをセット
        this.manager = manager;
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
            textView.setText(str);//dataListX[0]+"\n"+dataListX[99]

            RawAcceDataSetUpdate(rx, ry, rz, count);
            //初期化完了
            if(count >= elemcnt){
                AcceDataGravity = GetAcceDataGravityUpdate();
                AcceDataNaturalized = GetAcceDataNaturalizedUpdate();
                FeatureResourceDataUpdate(AcceDataGravity,AcceDataNaturalized,count2);
                if(count2 >=elemcnt){
                    setFeatureValueData(f1,featureValueSet);
                    setFeatureValueData(f2,featureValue2Set);
             //   nTextView.setText(f1.avr+"\n"+f2.avr);
                }
                count2++;
            }
        }

        count++;
    }
    /*
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
    /*特徴量を計算し、
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
    private float[] GetAcceDataGravityUpdate(){
        float avrX = ExCalc.GetArrAvr(dataListX);
        float avrY = ExCalc.GetArrAvr(dataListY);
        float avrZ = ExCalc.GetArrAvr(dataListZ);
        return new float[]{avrX,avrY,avrZ};
    }

    private float[] GetAcceDataNaturalizedUpdate(){

        float nX = dataListX[0] - AcceDataGravity[0];
        float nY = dataListY[0] - AcceDataGravity[1];
        float nZ = dataListZ[0] - AcceDataGravity[2];
        return new float[]{nX,nY,nZ};
    }

    private void setFeatureValueData(FeatureValue fv,float[] fvSet){
        //FeatureValue fv = new FeatureValue();
        fv.min = ExCalc.GetArrMin(fvSet);
        fv.max = ExCalc.GetArrMax(fvSet);
        fv.avr = ExCalc.GetArrAvr(fvSet);
        fv.var = ExCalc.GetArrVar(fvSet);
    }
    public FeatureValue getF1(int i){
       if(i ==1)
        return f1;
        if(i==2)
            return f2;
        return null;
    }
}
