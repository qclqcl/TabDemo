package com.andyidea.tabdemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import com.andyidea.tabdemo.AActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.AActivity.UpdateTLETask;
import com.andyidea.tabdemo.db.DatabaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class A2Activity extends Activity{
	
	private Button ButtonDone_a2;
	private Button ButtonEdit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_a2);
		
		ButtonDone_a2 = (Button)findViewById(R.id.ButtonDone_a2);		
		ButtonDone_a2.setOnClickListener(new ButtonOnClickListener());
		
		ButtonEdit = (Button)findViewById(R.id.ButtonEdit);
		ButtonEdit.setOnClickListener(new ButtonOnClickListener());
		
		
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {				          	           		
	           case R.id.ButtonDone_a2: 
	        	   finish();
					break;
	           case R.id.ButtonEdit:	        	  	        
	        	    Intent intent = new Intent();            	
	            	intent.setClass(A2Activity.this,A3Activity.class);
	            	A2Activity.this.startActivity(intent);      	   	        	   	        		   	         	   				   				   				   				   			
					break;

			   default:
					break;
			}
		}
	}
	
}
