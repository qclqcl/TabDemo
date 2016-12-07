package com.andyidea.tabdemo;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

import android.app.Application;
import android.app.Service;
import android.os.Handler;
import android.os.Vibrator;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class LocationApplication extends Application {
    public LocationClient mLocationClient;
    public MyLocationListener mMyLocationListener;
    public Vibrator mVibrator;  
    
	public  Handler handlerB,handlerC,handlerC2,handlerD;
	public  Runnable runnableB,runnableC,runnableC2,runnableD;
	   
    public int counttest = 0;
    public int counttest_passforecast = 100;
    public double myLatitude,myLongitude,myRadius,myAltitude;
    public String myTime;
    
    public String[] title = new String[200];
    public String[] TLE1 = new String[200];
    public String[] TLE2 = new String[200];
    public int total,delay,angle;
    //risetime,settime,riseazimuth,setazimuth risetimecount
    public String[] risetime = new String[200];
    public String[] settime = new String[200];
    public String[] riseazimuth = new String[200];
    public String[] setazimuth = new String[200];
    public int risetimecount = 0;
    
    public String getTitle(Integer j) {  
        return title[j];  
    } 
    public String getTLE1(Integer i) {  
        return TLE1[i];  
    } 
    public String getTLE2(Integer k) {  
        return TLE2[k];  
    }
    public int getTotal() {  
        return total;  
    } 
    public int getDelay() {  
        return delay;  
    }
    public int getAngle() {  
        return angle;  
    }
    //get risetime,settime,riseazimuth,setazimuth risetimecount
    public String getrisetime(Integer i){
    	return risetime[i];
    }
    public String getsettime(Integer j){
    	return settime[j];
    }
    public String getriseazimuth(Integer k){
    	return riseazimuth[k];
    }
    public String getsetazimuth(Integer l){
    	return setazimuth[l];
    }
    public int getrisetimecount(){
    	return risetimecount;
    }
    
    public void setTitle(String title,Integer j) {  
        this.title[j] = title;  
    }
    public void setTLE1(String TLE1,Integer i) {  
        this.TLE1[i] = TLE1;  
    }
    public void setTLE2(String TLE2,Integer k) {  
        this.TLE2[k] = TLE2;  
    }
    public void setTotal(Integer total) {  
        this.total = total;  
    }
    public void setDelay(Integer delay) {  
        this.delay = delay;  
    }
    public void setAngle(Integer angle) {  
        this.angle = angle;  
    }
    //get risetime,settime,riseazimuth,setazimuth
    public void setrisetime(String risetime,Integer i){
    	this.risetime[i] = risetime;
    }
    public void setsettime(String settime,Integer j){
    	this.settime[j] = settime;
    }
    public void setriseazimuth(String riseazimuth,Integer k){
    	this.riseazimuth[k] = riseazimuth;
    }
    public void setsetazimuth(String setazimuth,Integer l){
    	this.setazimuth[l] = setazimuth;
    }
    public void setrisetimecount(Integer risetimecount) {  
        this.risetimecount = risetimecount;  
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        mLocationClient = new LocationClient(this.getApplicationContext());
        mMyLocationListener = new MyLocationListener();
        mLocationClient.registerLocationListener(mMyLocationListener);
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
    }


    /**
     * 实现实时位置回调监听
     */
    public class MyLocationListener implements BDLocationListener {
 
		@Override
        public void onReceiveLocation(BDLocation location) {						
			myLatitude = location.getLatitude();
			myLongitude = location.getLongitude();
			myRadius = location.getRadius();
			myTime = location.getTime();
			
			if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
				myAltitude=location.getAltitude();
			}else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
				myAltitude=0;
			}			
        }
        
    }
    
    public void initLocation(){
    	
    	mLocationClient = new LocationClient(this.getApplicationContext());       
        mLocationClient.registerLocationListener(mMyLocationListener);
    	
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//高精度
        option.setCoorType("gcj02");//gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIgnoreKillProcess(true);//可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
       
        mLocationClient.setLocOption(option);
        
        mLocationClient.start();//定位SDK start之后会默认发起一次定位请求，开发者无须判断isstart并主动调用request
        mLocationClient.requestLocation();
    }

}
