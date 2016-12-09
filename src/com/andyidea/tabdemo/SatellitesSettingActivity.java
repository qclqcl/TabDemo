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
	
	//�������л�ȡ���ݵ���ر���
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
		
		myInputSteam = streampost(myurl);//�������ϻ�ȡ���ݣ������ʽ����������ʽ
		res = convertStreamToString(myInputSteam);//����ȡ��������ת�����ַ���
		String result[] = res.split("/n");//��res�ַ������ա�/n���и��С���ַ���
		
		DatabaseHelper dbHelper = new DatabaseHelper(SatellitesSettingActivity.this,"sats_db");
		//ֻ�е�����DatabaseHelper�����getReadableDatabase()������������getWritableDatabase()����֮�󣬲Żᴴ�������һ�����ݿ�
		SQLiteDatabase db = dbHelper.getReadableDatabase();	
		
		//ֱ��ɾ����ΪNavigation��Ӧ��������¼
		db.delete("user", "category=?" ,new String[]{"Navigation"});
				
		//����ContentValues����
		ContentValues values = new ContentValues();		
		//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��		
		for(int i=0;i<(result.length/5);i++)
		{			
			values.put("id", i);
			values.put("name",result[i*5]);
			values.put("category","Navigation");
			values.put("TLE1",result[i*5+1]);	
			values.put("TLE2",result[i*5+2]);
			values.put("info",result[i*5+3]);
			values.put("launch",result[i*5+4]);
			//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
			db.insert("user", null, values);
		}
    }
	
	//�������л�ȡ���ݷ���������	
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
	
	//���������л�ȡ������ת�����ַ���	
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
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK�Ƿ���״̬��
	        	   SatellitesSettingActivity.this.finish();	        	   	        	   	        		   	         	   				   				   				   				   			
					break;
					
	           case R.id.ButtonOthers: 
	        	   myurl = "http://www.4001149114.com/satellite/visual.txt";
	        	   new UpdateTLETask2().execute(4);
	        	   //update();  //���޸�
	        	   Bundle bundleOthers = new Bundle();
	        	   bundleOthers.putString("result", "Others");
	        	   intent.putExtras(bundleOthers); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK�Ƿ���״̬��
	        	   SatellitesSettingActivity.this.finish();	
	        	   break;
					
/*					
	           case R.id.ButtonCommunication: 
	        	   Bundle bundleCommunication = new Bundle();
	        	   bundleCommunication.putString("result", "Communication");
	        	   intent.putExtras(bundleCommunication); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK�Ƿ���״̬��
	        	   SatellitesSettingActivity.this.finish();	
	        	            	 
					break;
	           case R.id.ButtonRemote:     	   
	        	   Bundle bundleRemote = new Bundle();
	        	   bundleRemote.putString("result", "Remote");
	        	   intent.putExtras(bundleRemote); 
	        	   SatellitesSettingActivity.this.setResult(RESULT_OK, intent); //RESULT_OK�Ƿ���״̬��
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
