package com.andyidea.tabdemo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MapSettingActivity extends Activity{

	private Button ButtonDone;
	private ToggleButton  SatellitenameBtn,SatellitecoverageBtn;
	private TextView SatellitenameTv,SatellitecoverageTv;
	private LocationApplication myApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapsetting);

		ButtonDone = (Button)findViewById(R.id.ButtonDone);
		ButtonDone.setOnClickListener(new ButtonOnClickListener());

		myApp = (LocationApplication)getApplication();

		SatellitenameBtn = (ToggleButton) findViewById(R.id.SatellitenameBtn);
		SatellitenameTv = (TextView) findViewById(R.id.SatellitenameTv);

		SatellitecoverageBtn = (ToggleButton) findViewById(R.id.SatellitecoverageBtn);
		SatellitecoverageTv = (TextView) findViewById(R.id.SatellitecoverageTv);

		SatellitenameBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//选中
					myApp.setdisSatname(true);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatName", myApp.getdisSatname()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "显示卫星名称", Toast.LENGTH_SHORT)
//	                .show();
				}else{
					//未选中
					myApp.setdisSatname(false);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatName", myApp.getdisSatname()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "不显示卫星名称", Toast.LENGTH_SHORT)
//	                .show();
				}
			}
		});// 添加监听事件
		
		SatellitecoverageBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//选中
					myApp.setdisSatcoverage(true);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatCoverage", myApp.getdisSatcoverage()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "显示卫星覆盖范围", Toast.LENGTH_SHORT)
//	                .show();
				}else{
					//未选中
					myApp.setdisSatcoverage(false);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatCoverage", myApp.getdisSatcoverage()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "不显示卫星覆盖范围", Toast.LENGTH_SHORT)
//	                .show();
				}
			}
		});// 添加监听事件
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
	           case R.id.ButtonDone:
	        	   finish();
	        	   break;
			   default:
				   break;
			}
		}
	}

	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        int DisSatName = sharedPreferences.getInt("DisSatName", 0);
        int DisSatCoverage = sharedPreferences.getInt("DisSatCoverage", 0);

        SatellitenameBtn.setChecked((DisSatName==1)?true:false);
        SatellitecoverageBtn.setChecked((DisSatCoverage==1)?true:false);

		super.onResume();
	}

}
