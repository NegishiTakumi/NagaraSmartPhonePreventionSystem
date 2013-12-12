package com.grad.nagara;

import android.app.ActionBar;
import android.app.Activity;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.Timer;

/**
 * Created by cst2 on 13/11/02.
 */
public class LatlngManager extends Activity {
    private TextView textView,distTextView;
    private LocationManager locationManager;
    private final int E_RADIUS = 6378137;
    private final double rad = Math.PI / 180.0;
    private double lat1=-1,lng1=-1,lat2=-1,lng2=-1;
    private double starttime=-1,nexttime =-1;
    DecimalFormat df1;
    public LatlngManager(TextView textView,TextView distTextView,LocationManager locationManager){
        this.textView = textView;
        this.distTextView = distTextView;
        this.locationManager = locationManager;
        df1 = new DecimalFormat("0.000");
    }
    //初期化
    public void onResume(MainActivity ma){
        //GPS情報
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER, 5 * 1000, 5, new tLocationListener(ma));
    }

    //GPSの値を取得するところ。
    class tLocationListener implements LocationListener {
        private MainActivity ma;

        public tLocationListener(MainActivity ma){
            this.ma = ma;
        }
        //GPSの値が代わった場合
        public void onLocationChanged(Location lc){
            nexttime = starttime;
            starttime = System.currentTimeMillis();
            double lt = lc.getLatitude();
            double ln = lc.getLongitude();
            textView.setText("現在地は\n緯度：" + lt + "\n経度：" + ln + "です。");

            lat2 = lat1;
            lng2 = lng1;
            lat1 = lt;
            lng1 = ln;
            if(lat2 == -1 || lat2 == lat1)return;//値が代入されていない時とGPSの値が前回と一緒の時はスルー

            double dist = GetDistFromGPS(lat1,lng1,lat2,lng2);
            distTextView.setText("前回からの距離："+dist +"");
            Toast.makeText(ma.getBaseContext(), "現在地は\n緯度：" + lt + "\n経度：" + ln + "です。"+"\n前回からの距離は"+dist+"です。", Toast.LENGTH_LONG).show();


            if(nexttime == -1 || nexttime == starttime)return;

            double vel = GetVelocityFromDistance(dist,(starttime - nexttime)/1000);
            distTextView.setText("現在のスピード："+df1.format(vel)+" m/s");
        }
        public void onProviderDisabled(String pv){
            Toast.makeText(ma.getBaseContext(), "GPSが切れました。", Toast.LENGTH_LONG).show();
        }
        public void onProviderEnabled(String pv){}
        public void onStatusChanged(String pv,int status,Bundle ex){}
    }

    /*GPSの値から距離の計算をする。(lat1,lng1 = 前回の経度と緯度、lat2,lng2 = 今回の経度と緯度)
    ----------------------------------------------------------------------------------*/
    public double GetDistFromGPS(double lat1,double lng1,double lat2,double lng2){
        double lat_variation = (lat2 - lat1)*rad;
        double lng_variation = (lng2 - lng1)*rad;

        double x_variation = E_RADIUS*lat_variation*Math.cos(lng1);
        double y_variation = E_RADIUS*lng_variation;

        return Math.sqrt(Math.pow(x_variation,2)+Math.pow(y_variation,2));
        //計算式は　http://www.palmdreams.net/workshop/sub_Workshop_DistanceByCoodinates.html　より
    }
    /*距離の値から移動速度の計算をする。
    ----------------------------------------------------------------------------------*/
    public double GetVelocityFromDistance(double distance,double time/*秒であること*/){
        return distance / time;//return 毎秒Xメートル
    }
}