package com.grad.nagara;
/**
 * Created by CST on 13/12/07.
 */
public class AcceRawData {
    public float x,y,z;

    public AcceRawData(){
        x  =-1;
        y = -1;
        z = -1;
    }
    public AcceRawData(float xData,float yData,float zData){
        x = xData;
        y = yData;
        z = zData;
    }
/*
    public boolean isDataNotInserted(){
        return this.x==-1 &&this.y ==-1 && this.z == -1;
    }*/
}
