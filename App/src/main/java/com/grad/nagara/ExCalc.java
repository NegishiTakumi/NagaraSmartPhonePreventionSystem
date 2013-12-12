package com.grad.nagara;

import java.util.Arrays;

/**
 * Created by CST on 13/12/05.
 * 配列操作プログラム(LINQに相当するもの)の実装
 */
public class ExCalc {
    /*配列が全て値が入っているか確かめる。
    * --------------------------------------------------------*/

    /*public static boolean isAcceRawDataSetFull(AcceRawData[] data){
        for(AcceRawData ard:data){
            if(ard.isDataNotInserted()) return false;
        }
        return true;
    }*/

    /*配列の合計を返す
    * --------------------------------------------------------*/
    public static float GetArrSum(float[] arr){
        float sum = 0;
        for(float n : arr){sum += n;}
        return sum;
    }
    /*配列の最大値を返す
    * --------------------------------------------------------*/
    public static float GetArrMax(float[] arr){
        Arrays.sort(arr);
        return arr[arr.length-1];
    }
    /*配列の最小値を返す
* --------------------------------------------------------*/
    public static float GetArrMin(float[] arr){
        Arrays.sort(arr);
        return arr[0];
    }
    /*配列の分散を返す。
* --------------------------------------------------------*/
    public static float GetArrVar(float[] arr){
        float sum = 0;     // 合計
        float sq_sum = 0;  // 二乗の合計

        // データ列の合計と二乗の合計を求める
        for(float n : arr)
        {
            sum += n;
            sq_sum += n*n;
        }
        // 平均値と分散を計算
        float mean = sum / arr.length;
        float variance = sq_sum / arr.length - mean*mean;
    return variance;
    }
    /*配列の平均値を返す。
* --------------------------------------------------------*/
    public static float GetArrAvr(float[] arr){
        return GetArrSum(arr)/arr.length;
    }
    /*配列の最初にデータを入れて残りを一つずらしたものを返す。
* --------------------------------------------------------*/
/*            float[] t = data;
            foreach (var f in data)
            {
                t = ExCalc.InsertDataFirst(10, t);
                foreach (var f1 in t)
                {
                    Console.Write(f1+",");
                }
                Console.WriteLine("\n");
            }*/
     public static float[] InsertArrFirst(float data,float[] arr){
        float[] tmp = new float[arr.length];
        System.arraycopy(arr,0,tmp,1,arr.length-1);
        tmp[0] = data;
        return tmp;
    }

    public static AcceRawData[] InsertArrFirst(float x,float y,float z,AcceRawData[] arr){

        System.arraycopy(arr,0,arr,1,arr.length-1);
        arr[0].x = x;
        arr[0].y = y;
        arr[0].z = z;

        return arr;
    }
}
