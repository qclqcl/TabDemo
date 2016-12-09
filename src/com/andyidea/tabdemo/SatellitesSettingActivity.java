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

public class SatellitesSettingActivity extends Activity{
	
	private Button ButtonDone;
	private Button ButtonOthers;
	
//	private Button ButtonUpdate;
	private Button ButtonNavigation;
//	private Button ButtonCommunication;
//	private Button ButtonRemote;
	
	//从网络中获取数据的相关变量
	private String myurl;
	private String res;
	private InputStream myInputSteam;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.satellitessetting);
		
		ButtonDone = (Button)findViewById(R.id.ButtonDone);		
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
		
		ButtonNavigation = (Button)findViewById(R.id.ButtonNavigation);
		ButtonNavigation.setOnClickListener(new ButtonOnClickListener());
		
		ButtonOthers = (Button)findViewById(R.id.ButtonOthers);
		ButtonOthers.setOnClickListener(new ButtonOnClickListener());

/*
		ButtonUpdate = (Button)findViewById(R.id.ButtonUpdate);
		ButtonUpdate.setOnClickListener(new ButtonOnClickListener());
		
		ButtonCommunication = (Button)findViewById(R.id.ButtonCommunication);
		ButtonCommunication.setOnClickListener(new ButtonOnClickListener());
		
		ButtonRemote = (Button)findViewById(R.id.ButtonRemote);
		ButtonRemote.setOnClickListener(new ButtonOnClickListener());
*/		
		//myurl = "http://www.celestrak.com/NORAD/elements/beidou.txt";
		myurl = "http://www.4001149114.com/satellite/beidou.txt";
	}
	
	
private void update(){
		
		myInputSteam = streampost(myurl);//从网络上获取数据，输出格式是数据流格式
		res = convertStreamToString(myInputSteam);//将获取到的数据转换成字符串
		String result[] = res.split("/n");//将res字符串按照“/n”切割成小的字符串
		
		DatabaseHelper dbHelper = new DatabaseHelper(SatellitesSettingActivity.this,"sats_db");
		//只有调用了DatabaseHelper对象的getReadableDatabase()方法，或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
		SQLiteDatabase db = dbHelper.getReadableDatabase();	
		
		//直接删除名为Navigation对应的那条记录
		db.delete("user", "category=?" ,new String[]{"Navigation"});
				
		//生成ContentValues对象
		ContentValues values = new ContentValues();		
		//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致		
		for(int i=0;i<(result.length/5);i++)
		{			
			values.put("id", i);
			values.put("name",result[i*5]);
			values.put("category","Navigation");
			values.put("TLE1",result[i*5+1]);	
			values.put("TLE2",result[i*5+2]);
			values.put("info",result[i*5+3]);
			values.put("launch",result[i*5+4]);
			//调用insert方法，就可以将数据插入到数据库当中
			db.insert("user", null, values);
		}
    }
	
	//从网络中获取数据返回数据流	
	public InputStream streampost(String remote_addr){  
	    URL infoUrl = null;  
	    InputStream inStream = null;  
	    try {  
	        infoUrl = new URL(remote_addr);  
	        URLConnection connection = infoUrl.openConnection();  
	        HttpURLConnection httpConnection = (HttpURLConnection)connection;  
	        int responseCode = httpConnection.getResponseCode();  
	        if(responseCode == HttpURLConnection.HTTP_OK){  
	            inStream = httpConnection.getInputStream();  
	        }  
	    } catch (MalformedURLException e) {  
	        // TODO Auto-generated catch block   
	        e.printStackTrace();  
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block   
	        e.printStackTrace();  
	    }  
	    return inStream;  
	}
	
	//将从网络中获取数据流转换成字符串	
	public String convertStreamToString(InputStream is) {   
		   BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
		   StringBuilder sb = new StringBuilder();   		    
		   String line = null;   
	       try {   
	            while ((line = reader.readLine()) != null) {   
	                sb.append(line + "/n");   
	            }   
	        } catch (IOException e) {   
	            e.printStackTrace();   
	        } finally {   
	            try {   
	                is.close();   
	            } catch (IOException e) {   
	                e.printStackTrace();   
	            }   
	        }   		    
	        return sb.toString();   
	  }

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent = intent.setClass(SatellitesSettingActivity.this, SettingTabActivity.class);
			switch (v.getId()) {				          	           		
	           case R.id.ButtonDone:
	        	 //  intent.setClass(SatellitesSettingActivity.this,SettingTabActivity.class);
	        	 //  SatellitesSettingActivity.this.startActivity(intent);
	        	   finish();
					break;
	           case R.id.ButtonNavigation:
	        	   //update();
	        	   myurl = "http://www.4001149114.com/satellite/beidou.txt";
	        	   new UpdateTLETask2().execute(4);
	        	   Bundle bundleNavigation = new Bundle();
	        	   bundleNavigation.putString("result", "Navigation");
	        	   intent.putExtras(bundleNavigation); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
					
	           case R.id.ButtonOthers: 
	        	   myurl = "http://www.4001149114.com/satellite/visual.txt";
	        	   new UpdateTLETask2().execute(4);
	        	   //update();  //待修改
	        	   Bundle bundleOthers = new Bundle();
	        	   bundleOthers.putString("result", "Others");
	        	   intent.putExtras(bundleOthers); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	
	        	   break;
					
/*					
	           case R.id.ButtonCommunication: 
	        	   Bundle bundleCommunication = new Bundle();
	        	   bundleCommunication.putString("result", "Communication");
	        	   intent.putExtras(bundleCommunication); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	
	        	            	 
					break;
	           case R.id.ButtonRemote:     	   
	        	   Bundle bundleRemote = new Bundle();
	        	   bundleRemote.putString("result", "Remote");
	        	   intent.putExtras(bundleRemote); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();
					break;
	           case R.id.ButtonUpdate:     	   
	        	   update();
					break;
*/					
			   default:
					break;
			}
		}
	}
	class UpdateTLETask2 extends AsyncTask<Integer, Void, String>
	{
		
		@Override
		protected String doInBackground(Integer... params)
		{
			try
			{
				update();
				return "OK";//mDatas = mNewsItemBiz.getNews(url).getNewses();
			} catch (Exception e)
			{
				Looper.prepare();
				Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
				Looper.loop();
			}

			return null;
		}

		@Override
		protected void onPostExecute(String lla)
		{
			if(lla == null)
				return ; 
			//NavigationDisplay();
			//satellitesView.repaintSatellites(llas);
			//gpsStatusText.setText("SATELLITE TRACK:"
			//	+ llas.size());
		}

	}
}
