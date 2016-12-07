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
 * ��Application�����аٶȶ�λSDK�Ľӿ�˵����ο������ĵ���http://developer.baidu.com/map/loc_refer/index.html
 *
 * �ٶȶ�λSDK�ٷ���վ��http://developer.baidu.com/map/index.php?title=android-locsdk
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
     * ʵ��ʵʱλ�ûص�����
     */
    public class MyLocationListener implements BDLocationListener {
 
		@Override
        public void onReceiveLocation(BDLocation location) {						
			myLatitude = location.getLatitude();
			myLongitude = location.getLongitude();
			myRadius = location.getRadius();
			myTime = location.getTime();
			
			if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS��λ���
				myAltitude=location.getAltitude();
			}else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// ���綨λ���
				myAltitude=0;
			}			
        }
        
    }
    
    public void initLocation(){
    	
    	mLocationClient = new LocationClient(this.getApplicationContext());       
        mLocationClient.registerLocationListener(mMyLocationListener);
    	
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationMode.Hight_Accuracy);//�߾���
        option.setCoorType("gcj02");//gcj02�����÷��صĶ�λ�������ϵ
        int span=1000;
        option.setScanSpan(span);//��ѡ��Ĭ��0��������λһ�Σ����÷���λ����ļ����Ҫ���ڵ���1000ms������Ч��
        option.setOpenGps(true);//��ѡ��Ĭ��false,�����Ƿ�ʹ��gps
        option.setLocationNotify(true);//��ѡ��Ĭ��false�������Ƿ�gps��Чʱ����1S1��Ƶ�����GPS���
        option.setIgnoreKillProcess(true);//��ѡ��Ĭ��true����λSDK�ڲ���һ��SERVICE�����ŵ��˶������̣������Ƿ���stop��ʱ��ɱ��������̣�Ĭ�ϲ�ɱ��
       
        mLocationClient.setLocOption(option);
        
        mLocationClient.start();//��λSDK start֮���Ĭ�Ϸ���һ�ζ�λ���󣬿����������ж�isstart����������request
        mLocationClient.requestLocation();
    }

}
