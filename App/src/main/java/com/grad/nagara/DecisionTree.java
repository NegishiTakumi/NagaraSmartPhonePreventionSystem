package com.grad.nagara;

import android.util.Log;

/**
 * Created by CST on 13/12/16.
 */
public class DecisionTree {
        public  static final String TAG = "__DecisionTree";

    public static boolean isWalk_old(float featureValue1,float featureValue2,float featureValue3){
        Log.d(TAG,"featureValue1:" + featureValue1 + "featureValue2 : " + featureValue2 + "featureValue3 : " + featureValue3);
        return (featureValue1 < -25 && featureValue2 > 25 && featureValue3 > 300);
    }
    //歩行しているか、ここでチェック。
    public static boolean isWalk(float[] zDataSet,float[] yDataSet){
        int count = 0;
        float yAvr = ExCalc.GetArrAvr(yDataSet);
        boolean b = yAvr >-1 && yAvr<9;
        double ZF = 1.25;
        double threshold = ZF *  Math.cos(Math.toRadians(90 * (yAvr / 9.8)) ) + 1;
        Log.d(TAG,threshold + "t");
        for(int i = 0; i<zDataSet.length; i++){
            if(zDataSet[i] > threshold){
                count++;
                i = i<zDataSet.length -3 ? i+3 : zDataSet.length;
            }
        }
        Log.d(TAG,count +":count" );
        return count >= (zDataSet.length/8) -1 && b;
    }

    public static boolean isShake(float[] xDataSet){
       float t1 = ExCalc.GetArrMax(xDataSet);
        float t2 = ExCalc.GetArrMin(xDataSet);
        return (t1>3 &&t2<-3);

    }
}
