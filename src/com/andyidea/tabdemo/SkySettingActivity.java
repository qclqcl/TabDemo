package com.andyidea.tabdemo;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class SkySettingActivity extends Activity{
	
	private Button ButtonDone;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.skysetting);
		
		ButtonDone = (Button)findViewById(R.id.ButtonDone);		
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
		
	}
	
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
		//	Intent intent = new Intent();			
			switch (v.getId()) {				          	           		
	           case R.id.ButtonDone:
	        	 //  intent.setClass(SatellitesSettingActivity.this,SettingTabActivity.class);
	        	 //  SatellitesSettingActivity.this.startActivity(intent);
	        	   finish();
					break;    	   			        	   
			   default:
					break;
			}
		}
	}
	
}
