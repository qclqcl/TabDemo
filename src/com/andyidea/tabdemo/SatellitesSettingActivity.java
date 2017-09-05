package com.andyidea.tabdemo;


import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
	private Button ButtonRadar;
	
//	private Button ButtonUpdate;
	private Button ButtonBeidou;
	private Button ButtonGPS;
	private Button ButtonGlonass;
	private Button ButtonGalileo;

	private Button ButtonWeather;
	private Button ButtonGlobal;
	private Button ButtonIridium;
	private Button ButtonScience;
	private Button ButtonSpace;
	
	private Button ButtonAddSat;
	
	//从网络中获取数据的相关变量
	private String myurl;
	private String sattype;
	private String res;
	private InputStream myInputSteam;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.satellitessetting);
		
		ButtonDone = (Button)findViewById(R.id.ButtonDone);		
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
		
		ButtonBeidou = (Button)findViewById(R.id.ButtonBeidou);
		ButtonBeidou.setOnClickListener(new ButtonOnClickListener());

		ButtonGPS = (Button)findViewById(R.id.ButtonGPS);
		ButtonGPS.setOnClickListener(new ButtonOnClickListener());		

		ButtonGlonass = (Button)findViewById(R.id.ButtonGlonass);
		ButtonGlonass.setOnClickListener(new ButtonOnClickListener());

		ButtonGalileo = (Button)findViewById(R.id.ButtonGalileo);
		ButtonGalileo.setOnClickListener(new ButtonOnClickListener());

		ButtonWeather = (Button)findViewById(R.id.ButtonWeather);		
		ButtonWeather.setOnClickListener(new ButtonOnClickListener());
		
		ButtonGlobal = (Button)findViewById(R.id.ButtonGlobal);
		ButtonGlobal.setOnClickListener(new ButtonOnClickListener());

		ButtonIridium = (Button)findViewById(R.id.ButtonIridium);
		ButtonIridium.setOnClickListener(new ButtonOnClickListener());		

		ButtonScience = (Button)findViewById(R.id.ButtonScience);
		ButtonScience.setOnClickListener(new ButtonOnClickListener());

		ButtonSpace = (Button)findViewById(R.id.ButtonSpace);
		ButtonSpace.setOnClickListener(new ButtonOnClickListener());
		
		ButtonRadar = (Button)findViewById(R.id.ButtonRadar);
		ButtonRadar.setOnClickListener(new ButtonOnClickListener());

		ButtonAddSat = (Button)findViewById(R.id.ButtonAddSat);
		ButtonAddSat.setOnClickListener(new ButtonOnClickListener());

/*
		ButtonUpdate = (Button)findViewById(R.id.ButtonUpdate);
		ButtonUpdate.setOnClickListener(new ButtonOnClickListener());
		
		ButtonCommunication = (Button)findViewById(R.id.ButtonCommunication);
		ButtonCommunication.setOnClickListener(new ButtonOnClickListener());
		
		ButtonRemote = (Button)findViewById(R.id.ButtonRemote);
		ButtonRemote.setOnClickListener(new ButtonOnClickListener());
*/		
		
		myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV0";
		sattype = "Beidou";
	}
	
	
private void update(){

		res = streampost(myurl);//从网络上获取数据，输出格式是数据流格式
		//res = convertStreamToString(myInputSteam);//将获取到的数据转换成字符串
		//System.out.println(res);
		try
		{
			JSONObject result = new JSONObject(res.toString());
			if("success".equals(result.getString("message")))
			{
				
				DatabaseHelper dbHelper = new DatabaseHelper(SatellitesSettingActivity.this,"sats_db");
				//只有调用了DatabaseHelper对象的getReadableDatabase()方法，或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
				SQLiteDatabase db = dbHelper.getReadableDatabase();	
				
				//直接删除名为Navigation对应的那条记录
				db.delete("user", "category=?" ,new String[]{sattype});
				
				JSONArray sats = result.getJSONArray("satinfo");
				//System.out.println("update,"+sattype+","+sats.length());
				for(int i=0 ; i < sats.length() ;i++)
				{
					JSONObject sat = sats.getJSONObject(i);
					//System.out.println("update,"+i+","+sat.toString());	
					//生成ContentValues对象
					ContentValues values = new ContentValues();		
					//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致		
							
					values.put("id", i);
					values.put("sid",sat.getString("sid"));
					values.put("name",sat.getString("namecn"));
					values.put("category",sattype);
					values.put("TLE1",sat.getString("tle1"));	
					values.put("TLE2",sat.getString("tle2"));
					values.put("info",sat.getString("info"));
					values.put("launch",sat.getString("launch")+" "+sat.getString("launchtime"));
					//调用insert方法，就可以将数据插入到数据库当中
					db.insert("user", null, values);
					//System.out.println("update,"+sat.getString("namecn")+","+i);
				}
				
			}else
			{
				System.out.println("get sat info error");
			}

		}
		catch (JSONException e)
		{
			System.out.println("get sat info JSONException");
		}

    }
	
	//从网络中获取数据返回数据流	
	public String streampost(String remote_addr){  
	    URL infoUrl = null;  
	    InputStream inStream = null;  
	    try {  
	        infoUrl = new URL(remote_addr);
			HttpURLConnection conn = (HttpURLConnection) infoUrl.openConnection();
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(true);
			// 设置请求方式（GET/POST）
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();

			if(code == 200){
			// 从输入流读取返回内容
			InputStream is = conn.getInputStream();
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			int len = -1;
			byte[] buffer = new byte[1024];
			while ((len = is.read(buffer)) != -1) {
				baos.write(buffer, 0, len);
			}
			is.close();

			String result = baos.toString();
			/*
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String str = null;
			StringBuffer buffer = new StringBuffer();
			while ((str = bufferedReader.readLine()) != null) {
				buffer.append(str);
				//buffer.append("\n");
			}

			// 释放资源
			bufferedReader.close();
			inputStreamReader.close();
			inputStream.close();
			inputStream = null;
			conn.disconnect();*/
	        return result;
			}else
			{
				return null;
			}
  
	    } catch (MalformedURLException e) {  
	        // TODO Auto-generated catch block   
	        e.printStackTrace(); 
	        return null;
	    } catch (IOException e) {  
	        // TODO Auto-generated catch block   
	        e.printStackTrace(); 
	        return null;
	    }  
	    //return inStream;  
	}
	
	//将从网络中获取数据流转换成字符串	
	public String convertStreamToString(InputStream is) {   
		   BufferedReader reader = new BufferedReader(new InputStreamReader(is));   
		   StringBuilder sb = new StringBuilder();   		    
		   String line = null;   
	       try {   
	            while ((line = reader.readLine()) != null) {   
	                sb.append(line);   
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
	           case R.id.ButtonBeidou:
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV0";
	       			sattype = "Beidou";
	        	   new UpdateTLETask2().execute(4);
	        	   Bundle bundleBeidou = new Bundle();
	        	   bundleBeidou.putString("result", "Beidou");
	        	   intent.putExtras(bundleBeidou); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonGPS:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV1";
	       			sattype = "GPS";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleGPS = new Bundle();
		        	bundleGPS.putString("result", "GPS");
		        	intent.putExtras(bundleGPS); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonGalileo:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV3";
	       			sattype = "Galileo";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleGalileo = new Bundle();
		        	bundleGalileo.putString("result", "Galileo");
		        	intent.putExtras(bundleGalileo); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonGlonass:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV2";
	       			sattype = "Glonass";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleGlonass = new Bundle();
		        	bundleGlonass.putString("result", "Glonass");
		        	intent.putExtras(bundleGlonass); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonWeather:
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=WER0";
	       			sattype = "Weather";
	        	   new UpdateTLETask2().execute(4);
	        	   Bundle bundleWeather = new Bundle();
	        	   bundleWeather.putString("result", "Weather");
	        	   intent.putExtras(bundleWeather); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonGlobal:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=COM6";
	       			sattype = "Global";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleGlobal = new Bundle();
		        	bundleGlobal.putString("result", "Global");
		        	intent.putExtras(bundleGlobal); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonIridium:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=COM4";
	       			sattype = "Iridium";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleIridium = new Bundle();
		        	bundleIridium.putString("result", "Iridium");
		        	intent.putExtras(bundleIridium); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonScience:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=SCI0";
	       			sattype = "Science";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleScience = new Bundle();
		        	bundleScience.putString("result", "Science");
		        	intent.putExtras(bundleScience); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
	           case R.id.ButtonSpace:
	        	   //update();
	       			myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=SCI0";
	       			sattype = "Space";
	       			new UpdateTLETask2().execute(4);
		        	Bundle bundleSpace = new Bundle();
		        	bundleSpace.putString("result", "Space");
		        	intent.putExtras(bundleSpace); 
		        	SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
		        	SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;					
	           case R.id.ButtonRadar: 
	        	   myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=MIS1";
	        	   new UpdateTLETask2().execute(4);
	        	   sattype = "Radar";
	        	   //update();  //待修改
	        	   Bundle bundleRadar = new Bundle();
	        	   bundleRadar.putString("result", "Radar");
	        	   intent.putExtras(bundleRadar); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK是返回状态码
	        	   SatellitesSettingActivity.this.finish();	
	        	   break;

	           case R.id.ButtonAddSat:
	        	   intent.setClass(SatellitesSettingActivity.this,AddSatActivity.class);
	        	   SatellitesSettingActivity.this.startActivity(intent);
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
