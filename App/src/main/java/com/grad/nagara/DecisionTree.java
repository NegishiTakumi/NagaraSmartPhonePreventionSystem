package com.grad.nagara;

import android.util.Log;

/**
 * Created by CST on 13/12/16.
 */
public class DecisionTree {
        public  static final String TAG = "__DecisionTree";
    //歩行しているか、ここでチェック。
    public static boolean isWalk(float featureValue1,float featureValue2,float featureValue3){
        Log.d(TAG,"featureValue1:" + featureValue1 + "featureValue2 : " + featureValue2 + "featureValue3 : " + featureValue3);
        return (featureValue1 < -25 && featureValue2 > 25 && featureValue3 > 300);
    }
}
