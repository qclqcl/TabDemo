package com.andyidea.tabdemo;



import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class EActivity extends Activity{
	
	private Button ButtonDone;
	Intent intent = new Intent();
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_5);
		
		ButtonDone = (Button)findViewById(R.id.ButtonDone);
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
	}
	
	class ButtonOnClickListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {		
			switch (v.getId()) {			
	           case R.id.ButtonDone:
	        	   //intent.setClass(EActivity.this,MainTabActivity.class);
	        	   //EActivity.this.startActivity(intent);
	        	   finish();
	           		break;	             	   			        	   
			   default:
					break;
			}
		}
	}
	
}
