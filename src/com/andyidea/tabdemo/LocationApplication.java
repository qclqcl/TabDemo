package com.andyidea.tabdemo;

import satellite.tle.image.Satinfo;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.andyidea.tabdemo.service.*;

import android.app.Application;
import android.app.Service;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Vibrator;

/**
 * 主Application，所有百度定位SDK的接口说明请参考线上文档：http://developer.baidu.com/map/loc_refer/index.html
 *
 * 百度定位SDK官方网站：http://developer.baidu.com/map/index.php?title=android-locsdk
 */
public class LocationApplication extends Application {
	private LocationService locationService;
	public Vibrator mVibrator;

	public  Handler handlerB,handlerC,handlerC2,handlerD;
	public  Runnable runnableB,runnableC,runnableC2,runnableD;

    public int counttest = 0;
    public int counttest_passforecast = 100;
    public double myLatitude,myLongitude,myRadius,myAltitude;
    public String myTime;
    public Satinfo cursat = null;
    public String sattype = null;
    public String[] sid = new String[200];
    public String[] title = new String[200];
    public String[] TLE1 = new String[200];
    public String[] TLE2 = new String[200];
    public int listtotal,total;
    public int delay = 10;
    public int angle = 10;
    public boolean disSatname = true;
    public boolean disSatcoverage = true;
    public int MapNo = 1;
    public int WatchPosNo = 1;
    public float watchLatitude,watchLongitude,watchAltitude;
    //risetime,settime,riseazimuth,setazimuth risetimecount
    public String[] risetime = new String[200];
    public String[] settime = new String[200];
    public String[] riseazimuth = new String[200];
    public String[] setazimuth = new String[200];
    public int risetimecount = 0;
    public Bitmap bitmap = null;
    
    public String getSid(Integer j) {  
        return sid[j];  
    }    
    public String getTitle(Integer j) {  
        return title[j];  
    } 
    public String getTLE1(Integer i) {  
        return TLE1[i];  
    } 
    public String getTLE2(Integer k) {  
        return TLE2[k];  
    }
    public int getListTotal() {
        return listtotal;
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
    public boolean getdisSatname(){
    	return disSatname;
    }
    public boolean getdisSatcoverage(){
    	return disSatcoverage;
    }
    public int getMapNo(){
    	return MapNo;
    }
    public int getWatchPosNo(){
    	return WatchPosNo;
    }
    public float getWatchLatitude(){
    	return watchLatitude;
    }
    public float getWatchLongitude(){
    	return watchLongitude;
    }
    public float getWatchAltitude(){
    	return watchAltitude;
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
    public Bitmap getbitmap(){
    	return bitmap;
    }
    public void setSid(String sid,Integer j) {  
        this.sid[j] = sid;  
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
    public void setListTotal(Integer listtotal) {
    	this.listtotal = listtotal;
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
    public void setdisSatname(boolean disSatname){
    	this.disSatname = disSatname;
    }
    public void setdisSatcoverage(boolean disSatcoverage){
    	this.disSatcoverage = disSatcoverage;
    }
    public void setMapNo(int MapNo){
    	this.MapNo = MapNo;
    }
    public void setWatchPosNo(int WatchPosNo){
    	this.WatchPosNo = WatchPosNo;
    }
    public void setWatchLatitude(float watchLatitude){
    	this.watchLatitude = watchLatitude;
    }
    public void setWatchLongitude(float watchLongitude){
    	this.watchLongitude = watchLongitude;
    }
    public void setWatchAltitude(float watchAltitude){
    	this.watchAltitude = watchAltitude;
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
    public void setbitmap(Bitmap bitmap){
    	this.bitmap = bitmap;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        locationService = new LocationService(getApplicationContext());
        mVibrator =(Vibrator)getApplicationContext().getSystemService(Service.VIBRATOR_SERVICE);
        SDKInitializer.initialize(getApplicationContext());
		//获取locationservice实例，建议应用中只初始化1个location实例，然后使用，可以参考其他示例的activity，都是通过此种方式获取locationservice实例的
		locationService.registerListener(mListener);
		//注册监听
		locationService.setLocationOption(locationService.getOption());
		locationService.start();
    }
    /*****
	 *
	 * 定位结果回调，重写onReceiveLocation方法，可以直接拷贝如下代码到自己工程中修改
	 *
	 */
	private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {
				
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
	};

}
