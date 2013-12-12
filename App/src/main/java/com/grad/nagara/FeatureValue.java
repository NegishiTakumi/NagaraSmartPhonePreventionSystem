package com.grad.nagara;
/**
 * Created by CST on 13/12/05.
 */
public class FeatureValue {
    public float max;
    public float min;
    public float avr;
    public float var;

    public FeatureValue(){
        max = 0;
        min = 0;
        avr = 0;
        var = 0;
    }
    public FeatureValue(float maxv,float minv,float avrv,float varv){
        max = maxv;
        min = minv;
        avr = avrv;
        var = varv;
    }



}
