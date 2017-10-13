package com.andyidea.tabdemo;



import java.sql.Date;
import java.text.SimpleDateFormat;

import com.andyidea.tabdemo.EActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.service.LocationService;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener{
	private LocationService mylocationService;
	private double testAltiude = 0.0;

	private Handler handler;
	private Runnable runnable;
	
	private LocationApplication myApp;
	
	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	private Intent mEIntent;

	private String time=null;

	private Button RadioButtonE;
	private Button RadioButtonHelp;
	Intent intent = new Intent();

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintabs);

		mylocationService = new LocationService(getApplicationContext());
		mylocationService.registerListener(mListener);
		//注册监听
		mylocationService.setLocationOption(mylocationService.getDefaultLocationClientOption());
		mylocationService.start();

        RadioButtonE = (Button) findViewById(R.id.radio_button4);
        RadioButtonE.setOnClickListener(new ButtonOnClickListener());

        RadioButtonHelp = (Button) findViewById(R.id.radio_help);
        RadioButtonHelp.setOnClickListener(new ButtonOnClickListener());

        handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {				
				handler.postDelayed(this, 1000);
				getTime();
				mylocationService.stop();
			}
		};

		handler.postDelayed(runnable, 0);
        this.mAIntent = new Intent(this,AActivity.class);
        this.mBIntent = new Intent(this,BActivity.class);
        this.mCIntent = new Intent(this,CActivity.class);
        this.mDIntent = new Intent(this,DActivity.class);
        this.mEIntent = new Intent(this,EActivity.class);

        myApp = (LocationApplication)getApplication();
        
		((RadioButton) findViewById(R.id.radio_button0))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button1))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button2))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button3))
		.setOnCheckedChangeListener(this);

        setupIntent();
    }
    
    
    private void getTime() {
		// 获取系统时间
		Time t = new Time();
		t.setToNow(); // 取得系统时间
		int year = t.year - 2000;
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour; // 24小时制
		int minute = t.minute;
		int second = t.second;
		time = (year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second).toString();
		RadioButtonE.setText(time);
		RadioButtonE.setTextSize(7);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.radio_button0:
				mylocationService.start();

				this.mTabHost.setCurrentTabByTag("A_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //停止计数器
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //停止计数器
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //停止计数器
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //停止计数器
				}
				break;
			case R.id.radio_button1:
				mylocationService.start();

				this.mTabHost.setCurrentTabByTag("B_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.postDelayed(myApp.runnableB, 0);  //开始计数器
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //停止计数器
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //停止计数器
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //停止计数器
				}
				break;
			case R.id.radio_button2:
				mylocationService.start();

				this.mTabHost.setCurrentTabByTag("C_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //停止计数器
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //停止计数器
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.postDelayed(myApp.runnableC, 0);  //开始计数器
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.postDelayed(myApp.runnableC2, 0);  //开始计数器
				}
				break;
			case R.id.radio_button3:
				mylocationService.start();

				this.mTabHost.setCurrentTabByTag("D_TAB");
				if ( myApp.handlerD != null ){
					myApp.handlerD.postDelayed(myApp.runnableD, 0);  //开始计数器
				}
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //停止计数器
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //停止计数器
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //停止计数器
				}
				break;
			default:
				break;
			}
		}
	}

	class ButtonOnClickListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {		
			switch (v.getId()) {			
				case R.id.radio_help:
					//this.mTabHost.setCurrentTabByTag("E_TAB");
					intent.setClass(MainTabActivity.this,EActivity.class);
					MainTabActivity.this.startActivity(intent);
					break;	             	   			        	   
			   default:
					break;
			}
		}
	}

         

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_stats,
				R.drawable.icon,this.mAIntent));
		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_map,
				R.drawable.icon, this.mBIntent));
		localTabHost.addTab(buildTabSpec("C_TAB",R.string.main_sky, 
				R.drawable.icon,this.mCIntent));
		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_pass,
				R.drawable.icon, this.mDIntent));
		localTabHost.addTab(buildTabSpec("E_TAB", R.string.main_more,
				R.drawable.icon, this.mEIntent));
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // 监控/拦截/屏蔽返回键
	//do something
		Intent intent = new Intent();
		intent.setClass(MainTabActivity.this,PassSettingActivity.class);
		MainTabActivity.this.startActivity(intent);
	}  
	return super.onKeyDown(keyCode, event);
	}

	private BDAbstractLocationListener mListener = new BDAbstractLocationListener() {
		@Override
		public void onReceiveLocation(BDLocation location) {

			// TODO Auto-generated method stub
			if (null != location && location.getLocType() != BDLocation.TypeServerError) {

				myApp.setCurrentLatitude(location.getLatitude());
				myApp.setCurrentLongitude(location.getLongitude());
				myApp.setCurrentRadius(location.getRadius());
//				myApp.setCurrentAltitude(location.getAltitude());

				testAltiude += 1000.0;
				myApp.setCurrentAltitude(testAltiude);

/*
				if (location.getLocType() == BDLocation.TypeGpsLocation){// GPS定位结果
					myAltitude=location.getAltitude();
				}else if (location.getLocType() == BDLocation.TypeNetWorkLocation){// 网络定位结果
					myAltitude=0;
				}
*/
			}
		}
	};

}
