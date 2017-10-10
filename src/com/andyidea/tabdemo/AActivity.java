package com.andyidea.tabdemo;



import com.andyidea.tabdemo.data.Data;
import com.andyidea.tabdemo.db.DatabaseHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;


public class AActivity extends Activity{

	private Button ButtonAll;
	private Button ButtonPrefs;

	private ListView Sats_listview;
	
	
	private LocationApplication myApp;
	
	//�������л�ȡ���ݵ���ر���
	private String myurl;
	private String res;
	//private String cursid;
	private InputStream myInputSteam;
	
	private long exitTime = 0;

	private List<Data> dataList = new ArrayList<Data>();
	private HightKeywordsAdapter mAdapter;
	
	private String SHARE_APP_TAG;
	
	List<Integer> listItemID = new ArrayList<Integer>();//qcl
	
	private String truth_table[] = new String[200];//��ֵ������
	private boolean selected = false;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_1);

		ButtonAll = (Button)findViewById(R.id.ButtonAll);
		ButtonAll.setOnClickListener(new ButtonOnClickListener());
		
		ButtonPrefs = (Button)findViewById(R.id.ButtonPrefs);
		ButtonPrefs.setOnClickListener(new ButtonOnClickListener());
		
		Sats_listview = (ListView)findViewById(R.id.sats_listview);	
		
		myurl = "http://www.4001149114.com/sattrack/satmanage/getsatinfo?type=NAV0";

		//new UpdateTLETask().execute(4);
		
		for(int i=0;i<200;i++){
			truth_table[i] = "position"+i;
		}

		initGPS();

		//SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
		//Editor editor = sharedPreferences.edit();
		//editor.putInt("DisSatName", 0);
		//editor.putInt("DisSatName", 0);
		//editor.commit();
		/*SharedPreferences setting = getSharedPreferences(SHARE_APP_TAG, 0); 
        
		Boolean user_first = setting.getBoolean("FIRST",true);  
		   if(user_first){//��һ��  
			   
			   update();
			   NavigationDisplay();
			   
		        setting.edit().putBoolean("FIRST", false).commit();  
		        Toast.makeText(AActivity.this, "��һ��", Toast.LENGTH_LONG).show();  
		    }else{  
		    	
		    	NavigationDisplay();
		       Toast.makeText(AActivity.this, "���ǵ�һ��", Toast.LENGTH_LONG).show();  
		   }  
		*/
	}

	private void initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// �ж�GPSģ���Ƿ��������û������
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getApplicationContext(), "���GPS",
					Toast.LENGTH_SHORT).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("���GPS");
			dialog.setPositiveButton("ȷ��",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// ת���ֻ����ý��棬�û�����GPS
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0); // ������ɺ󷵻ص�ԭ���Ľ���
						}
					});
			dialog.setNeutralButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			} );
			dialog.show();
		} else {
			// ����Toast
//	          Toast.makeText(getApplicationContext(), "GPS is ready",
//	                  Toast.LENGTH_LONG).show();
//	          // �����Ի���
//	          new AlertDialog.Builder(this).setMessage("GPS is ready")
//	                  .setPositiveButton("OK", null).show();
		}
	}

	private void setListener() {

		mAdapter = new HightKeywordsAdapter();

		Sats_listview.setAdapter(mAdapter);
		Sats_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Toast.makeText(getApplicationContext(), "click " + position,
						Toast.LENGTH_SHORT).show();

				//������������������߼�
//				Intent intent = new Intent();
//				intent.setClass(AActivity.this,A2Activity.class);
//				AActivity.this.startActivity(intent); 

				mAdapter.mChecked.set(position,!mAdapter.mChecked.get(position));


//				mAdapter.setSelectItem(position); // ��¼��ǰѡ�е�item
//				myApp.counttest = position;
				mAdapter.notifyDataSetInvalidated();	//����UI����
			}

		});
//		mAdapter.setSelectItem(myApp.counttest); // ��¼��ǰѡ�е�item

		Sats_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean  onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), "longclick " +  position,
						Toast.LENGTH_SHORT).show();

				//�����鿴��������
				//AActivity.this.cursid = myApp.getSid(position);
				Intent intent = new Intent();
				intent.setClass(AActivity.this,A2Activity.class);
				AActivity.this.startActivity(intent); 

				mAdapter.setSelectItem(position); // ��¼��ǰѡ�е�item
				myApp.counttest = position;
				mAdapter.notifyDataSetInvalidated();	//����UI����

				return true;
			}
		});
		mAdapter.setSelectItem(myApp.counttest); // ��¼��ǰѡ�е�item
		
	}

	/**
	 * Customize Adapter, so that realize highlighted keywords
	 */

	private class HightKeywordsAdapter extends BaseAdapter {

		List<Boolean> mChecked;//qcl
		HashMap<Integer,View> map = new HashMap<Integer,View>();//qcl
		public HightKeywordsAdapter(){  //qcl
            mChecked = new ArrayList<Boolean>();

            SharedPreferences settings = getSharedPreferences("setting", 0);
            
            for(int i=0;i<myApp.getListTotal();i++){
        		if(settings != null){
        			if(i == settings.getInt(truth_table[i],0)){
        				mChecked.add(true); 
        			}else{
        				mChecked.add(false); 
        			}
        		}else{
        			mChecked.add(false);
        		}
           } 
            

        }
		
		
		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {

			return dataList.size();
		}

		@Override
		public Object getItem(int position) {

			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
//			ViewHolder holder;
			ViewHolder holder = null;

//			if (convertView == null) {
			if (map.get(position) == null) {

				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.item, null);

				holder.checked = (CheckBox) view.findViewById(R.id.checked);//qcl
				holder.sat_title = (TextView) view.findViewById(R.id.sat_title);
				holder.sat_info = (TextView) view.findViewById(R.id.sat_info);
				final int p = position;//qcl
				map.put(position, view);
				holder.checked.setOnClickListener(new View.OnClickListener() {//qcl                
                    @Override  
                    public void onClick(View v) {  
                        CheckBox cb = (CheckBox)v;    
                        mChecked.set(p, cb.isChecked());  

//                        SharedPreferences settings = getSharedPreferences("setting", 0);//qcl
//                        SharedPreferences.Editor editor = settings.edit();//qcl
//                        editor.putInt(truth_table[p], p);//qcl
 //                       editor.commit();
                    }  
                });
				
				view.setTag(holder);
			} else {
//				view = convertView;
				view = map.get(position);
				holder = (ViewHolder) view.getTag();
			}

		
			if (position == selectItem) {	//ѡ��״̬ ����
				view.setBackgroundColor(Color.YELLOW);
				holder.sat_title.setTextColor(Color.BLACK);
				holder.sat_info.setTextColor(Color.BLACK);
			} else {	//����״̬
				view.setBackgroundColor(Color.BLACK);
				holder.sat_title.setTextColor(Color.WHITE);
				holder.sat_info.setTextColor(Color.WHITE);
			}

			Data data = dataList.get(position);
			holder.sat_title.setText(data.getName());
			holder.sat_info.setText(data.getEn());
			holder.checked.setChecked(mChecked.get(position));//qcl		
			return view;
		}
	}

	static class ViewHolder {
		public TextView sat_title;
		public TextView sat_info;
		public CheckBox checked;

	}
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();			
			switch (v.getId()) {			
	           case R.id.ButtonAll:
//	        	   intent.setClass(AActivity.this,CategoriesActivity.class);
//	        	   AActivity.this.startActivity(intent);

	        	   listItemID.clear();

	        	   SharedPreferences settings = getSharedPreferences("setting", 0);//qcl
	        	   SharedPreferences.Editor editor = settings.edit();//qcl

	        	   for(int i=0;i<myApp.getListTotal();i++){
	        		   editor.putInt(truth_table[i], 100);//qcl
	        		   editor.commit();
	        	   }
	        	   editor.putInt(truth_table[0], 0);//qcl
               		editor.commit();
	        	   for(int i=1;i<mAdapter.mChecked.size();i++){
	        		   //mAdapter.mChecked.set(i, false);
                   	//editor.putInt(truth_table[i], i);//qcl
                   	//editor.commit();
	        		   
	                    if(selected){
	                    	//listItemID.add(i);  
	                    	editor.putInt(truth_table[i], i);//qcl
	                    	editor.commit();

	                    }
	                }
	        	    selected = !selected;
	        	    NavigationDisplay();
	        	    /*
	                if(listItemID.size()==0){ 
	                    AlertDialog.Builder builder1 = new AlertDialog.Builder(AActivity.this);  
	                    builder1.setMessage("û��ѡ���κμ�¼");  
	                    builder1.show();  
	                    NavigationDisplay();
	                }else{ 
	                    StringBuilder sb = new StringBuilder();  
	                      
	                    for(int i=0;i<listItemID.size();i++){  
	                        sb.append("ItemID="+listItemID.get(i)+" . ");  
	                    }  
	                    AlertDialog.Builder builder2 = new AlertDialog.Builder(AActivity.this);  
	                    builder2.setMessage(sb.toString());  
	                    builder2.show();  
	                    NavigationDisplay();
	                }*/
	           		break;
	           case R.id.ButtonPrefs:
	        	   intent.setClass(AActivity.this,SatellitesSettingActivity.class);
	        	   AActivity.this.startActivityForResult(intent,0);
					break;
			   default:
					break;
			}
		}
	}
/*	
private void update(){
		
		myInputSteam = streampost(myurl);//�������ϻ�ȡ���ݣ������ʽ����������ʽ
		res = convertStreamToString(myInputSteam);//����ȡ��������ת�����ַ���
		String result[] = res.split("/n");//��res�ַ������ա�/n���и��С���ַ���
		
		DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
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
		
		
    }*/
private void update(){
	
	myInputSteam = streampost(myurl);//�������ϻ�ȡ���ݣ������ʽ����������ʽ
	res = convertStreamToString(myInputSteam);//����ȡ��������ת�����ַ���
	//System.out.println(res);
	try
	{
		JSONObject result = new JSONObject(res.toString());
		if("success".equals(result.getString("message")))
		{
			
			DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
			//ֻ�е�����DatabaseHelper�����getReadableDatabase()������������getWritableDatabase()����֮�󣬲Żᴴ�������һ�����ݿ�
			SQLiteDatabase db = dbHelper.getReadableDatabase();	
			
			//ֱ��ɾ����ΪNavigation��Ӧ��������¼
			db.delete("user", "category=?" ,new String[]{"Beidou"});
			JSONArray sats = result.getJSONArray("satinfo");
			
			for(int i=0 ; i < sats.length() ;i++)
			{
				JSONObject sat = sats.getJSONObject(i);
				//System.out.println(sat.toString());
			//String result[] = res.split("/n");//��res�ַ������ա�/n���и��С���ַ���
			
				
					
			//����ContentValues����
			ContentValues values = new ContentValues();		
			//��ö����в����ֵ�ԣ����м���������ֵ��ϣ�����뵽��һ�е�ֵ��ֵ��������ݿ⵱�е���������һ��		
						
				values.put("id", i);
				values.put("sid",sat.getString("sid"));
				values.put("name",sat.getString("namecn"));
				values.put("category","Beidou");
				values.put("TLE1",sat.getString("tle1"));	
				values.put("TLE2",sat.getString("tle2"));
				values.put("info",sat.getString("info"));
				values.put("launch",sat.getString("launch")+" "+sat.getString("launchtime"));
				//����insert�������Ϳ��Խ����ݲ��뵽���ݿ⵱��
				db.insert("user", null, values);
				System.out.println(i+","+sat.getString("name"));
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
	
	
	
	//��ʾ�������ǵ���Ϣ
	public void NavigationDisplay() {
		
		myApp = (LocationApplication)getApplication(); 
		if(myApp.sattype == null)
		{
			myApp.sattype = "Beidou";
			//setListener();
			//return ;
		}
		//System.out.println("NavigationDisplay:" + myApp.sattype);
	    //����һ��DatabaseHelper����
		DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
		//ֻ�е�����DatabaseHelper�����getReadableDatabase()������������getWritableDatabase()����֮�󣬲Żᴴ�������һ�����ݿ�
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		
		if(db == null )	
		{
			setListener();
			System.out.println("NavigationDisplay:" + "error");
			return ;
		}
		int listcount = 0;
		int mycount1 = 0;
		int mycount2 = 0;
		int mycount3 = 0;
		
		//���ԭ��list������
		dataList.clear();	
		
		SharedPreferences settings = getSharedPreferences("setting", 0);
		
		Cursor cursor = db.query("user", new String[]{"id","sid","name","category","TLE1","TLE2","info","launch"}, "category=?", new String[]{myApp.sattype}, null, null, null);
		if(cursor !=null)
		{
			 
			while(cursor.moveToNext()){
				
				//System.out.println(cursor.getString(cursor.getColumnIndex("name")));			
				Integer id = cursor.getInt(cursor.getColumnIndex("id"));
				String sid = cursor.getString(cursor.getColumnIndex("sid"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String category = cursor.getString(cursor.getColumnIndex("category"));
				String TLE1 = cursor.getString(cursor.getColumnIndex("TLE1"));
				String TLE2 = cursor.getString(cursor.getColumnIndex("TLE2"));
				String info = cursor.getString(cursor.getColumnIndex("info"));
				String launch = cursor.getString(cursor.getColumnIndex("launch"));
				//�Ѳ�ѯ�������ݷŽ��������������Ա㴫�ݵ���һactivity��

				
				myApp.setSid(sid,listcount++);
				if(id == settings.getInt(truth_table[id],0)){
					
					myApp.setTitle(name,mycount1++);
    				myApp.setTLE1(TLE1,mycount2++);
    				myApp.setTLE2(TLE2,mycount3++);
				}
//				 myApp.setTitle(name,mycount1++);
//				 myApp.setTLE1(TLE1,mycount2++);
//				 myApp.setTLE2(TLE2,mycount3++);
				
				Data data = new Data(name,info+"\n"+launch);		
				dataList.add(data);		
			}
			
			myApp.setListTotal(new Integer(listcount));
			myApp.setTotal(new Integer(mycount1));
		}else
		{
			//TLE newTLE = new TLE("����M6", 
			//		"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			//		"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
			String name = "����M6";
			String TLE1 = "1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999";
			String TLE2 = "2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993";
			
			myApp = (LocationApplication)getApplication(); 
			myApp.setSid("38775U",mycount1);
			myApp.setTitle(name,mycount1++);
			myApp.setTLE1(TLE1,mycount2++);
			myApp.setTLE2(TLE2,mycount3++);
			 		
			Data data = new Data(name+"  ��������",TLE1+"\n"+TLE2);		
			dataList.add(data);		
			myApp.setTotal(new Integer(mycount1));
		}
		setListener();
			
	}
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         switch(resultCode) { //����״̬�룬�����ؽ��
         case RESULT_OK:
        	 try {
               Bundle bundle =data.getExtras(); //��ȡintent�����bundle����
               String result = bundle.getString("result");                          
               
               myApp = (LocationApplication)getApplication(); 
               Thread.sleep(500);
               if(result.equals("Beidou")){
            	   myApp.sattype = "Beidou";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
               }
               else if(result.equals("GPS")){
            	   myApp.sattype = "GPS";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Glonass")){
            	   myApp.sattype = "Glonass";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Galileo")){
            	   myApp.sattype = "Galileo";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Weather")){
            	   myApp.sattype = "Weather";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
               }
               else if(result.equals("Global")){
            	   myApp.sattype = "Global";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Iridium")){
            	   myApp.sattype = "Iridium";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Science")){
            	   //System.out.println("update,"+result);
            	   myApp.sattype = "Science";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               } 
               else if(result.equals("Space")){
            	   myApp.sattype = "Space";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }               
               else if(result.equals("Radar")){
            	   //System.out.println("NavigationDisplay:" + result);
            	   myApp.sattype = "Radar";
            	   NavigationDisplay();
            	   //new UpdateTLETask().execute(4);
            	  // CommunicationDisplay();
               }
               else if(result.equals("Remote")){
            	  // RemoteDisplay();
               }
         break; 
         } catch (Exception e) {
        	 e.printStackTrace();
         }         
         default:
         break;
         } 

  }
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // ���/����/���η��ؼ�
	//do something
		Intent intent = new Intent();
		intent.setClass(AActivity.this,SettingTabActivity.class);
		AActivity.this.startActivity(intent);
	}
	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
        if((System.currentTimeMillis()-exitTime) > 2000){  
            Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();                                
            exitTime = System.currentTimeMillis();   
        } else {
            finish();
            System.exit(0);
        }
        return true;   
    }
	return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
	 /**
	  * ����Ϊ����
	  */
//	 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//	 }

	 NavigationDisplay();	
	 super.onResume();
	}
	
	protected void onPause() {
		listItemID.clear();

		SharedPreferences settings = getSharedPreferences("setting", 0);//qcl
		SharedPreferences.Editor editor = settings.edit();//qcl

		for(int i=0;i<myApp.getListTotal();i++){
			editor.putInt(truth_table[i], 100);//qcl
			editor.commit();
		}

		for(int i=0;i<mAdapter.mChecked.size();i++){
			if(mAdapter.mChecked.get(i)){
				listItemID.add(i);
				editor.putInt(truth_table[i], i);//qcl
				editor.commit();
			}
		}
		NavigationDisplay();
/*
		if(listItemID.size()==0){
			AlertDialog.Builder builder1 = new AlertDialog.Builder(AActivity.this);
			builder1.setMessage("û��ѡ���κμ�¼");
			builder1.show();
			NavigationDisplay();
		}else{
             StringBuilder sb = new StringBuilder();

             for(int i=0;i<listItemID.size();i++){
                 sb.append("ItemID="+listItemID.get(i)+" . ");
             }
             AlertDialog.Builder builder2 = new AlertDialog.Builder(AActivity.this);
             builder2.setMessage(sb.toString());
             builder2.show();
             NavigationDisplay();
         }
*/
        super.onPause();
        System.out.println("LifecycleActivity onPause");
    }

	class UpdateTLETask extends AsyncTask<Integer, Void, String>
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
			NavigationDisplay();
			//satellitesView.repaintSatellites(llas);
			//gpsStatusText.setText("SATELLITE TRACK:"
			//	+ llas.size());
		}

	}

}
