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
	
	//从网络中获取数据的相关变量
	private String myurl;
	private String res;
	//private String cursid;
	private InputStream myInputSteam;
	
	private long exitTime = 0;

	private List<Data> dataList = new ArrayList<Data>();
	private HightKeywordsAdapter mAdapter;
	
	private String SHARE_APP_TAG;
	
	List<Integer> listItemID = new ArrayList<Integer>();//qcl
	
	private String truth_table[] = new String[200];//真值表数组
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
		   if(user_first){//第一次  
			   
			   update();
			   NavigationDisplay();
			   
		        setting.edit().putBoolean("FIRST", false).commit();  
		        Toast.makeText(AActivity.this, "第一次", Toast.LENGTH_LONG).show();  
		    }else{  
		    	
		    	NavigationDisplay();
		       Toast.makeText(AActivity.this, "不是第一次", Toast.LENGTH_LONG).show();  
		   }  
		*/
	}

	private void initGPS() {
		LocationManager locationManager = (LocationManager) this
				.getSystemService(Context.LOCATION_SERVICE);
		// 判断GPS模块是否开启，如果没有则开启
		if (!locationManager
				.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER)) {
			Toast.makeText(getApplicationContext(), "请打开GPS",
					Toast.LENGTH_SHORT).show();
			AlertDialog.Builder dialog = new AlertDialog.Builder(this);
			dialog.setMessage("请打开GPS");
			dialog.setPositiveButton("确定",
					new android.content.DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// 转到手机设置界面，用户设置GPS
							Intent intent = new Intent(
									Settings.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0); // 设置完成后返回到原来的界面
						}
					});
			dialog.setNeutralButton("取消", new android.content.DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			} );
			dialog.show();
		} else {
			// 弹出Toast
//	          Toast.makeText(getApplicationContext(), "GPS is ready",
//	                  Toast.LENGTH_LONG).show();
//	          // 弹出对话框
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

				//下面是你的其他事务逻辑
//				Intent intent = new Intent();
//				intent.setClass(AActivity.this,A2Activity.class);
//				AActivity.this.startActivity(intent); 

				mAdapter.mChecked.set(position,!mAdapter.mChecked.get(position));


//				mAdapter.setSelectItem(position); // 记录当前选中的item
//				myApp.counttest = position;
				mAdapter.notifyDataSetInvalidated();	//更新UI界面
			}

		});
//		mAdapter.setSelectItem(myApp.counttest); // 记录当前选中的item

		Sats_listview.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean  onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {
				Toast.makeText(getApplicationContext(), "longclick " +  position,
						Toast.LENGTH_SHORT).show();

				//长按查看卫星详情
				//AActivity.this.cursid = myApp.getSid(position);
				Intent intent = new Intent();
				intent.setClass(AActivity.this,A2Activity.class);
				AActivity.this.startActivity(intent); 

				mAdapter.setSelectItem(position); // 记录当前选中的item
				myApp.counttest = position;
				mAdapter.notifyDataSetInvalidated();	//更新UI界面

				return true;
			}
		});
		mAdapter.setSelectItem(myApp.counttest); // 记录当前选中的item
		
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

		
			if (position == selectItem) {	//选中状态 高亮
				view.setBackgroundColor(Color.YELLOW);
				holder.sat_title.setTextColor(Color.BLACK);
				holder.sat_info.setTextColor(Color.BLACK);
			} else {	//正常状态
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
	                    builder1.setMessage("没有选中任何记录");  
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
		
		myInputSteam = streampost(myurl);//从网络上获取数据，输出格式是数据流格式
		res = convertStreamToString(myInputSteam);//将获取到的数据转换成字符串
		String result[] = res.split("/n");//将res字符串按照“/n”切割成小的字符串
		
		DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
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
		
		
    }*/
private void update(){
	
	myInputSteam = streampost(myurl);//从网络上获取数据，输出格式是数据流格式
	res = convertStreamToString(myInputSteam);//将获取到的数据转换成字符串
	//System.out.println(res);
	try
	{
		JSONObject result = new JSONObject(res.toString());
		if("success".equals(result.getString("message")))
		{
			
			DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
			//只有调用了DatabaseHelper对象的getReadableDatabase()方法，或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
			SQLiteDatabase db = dbHelper.getReadableDatabase();	
			
			//直接删除名为Navigation对应的那条记录
			db.delete("user", "category=?" ,new String[]{"Beidou"});
			JSONArray sats = result.getJSONArray("satinfo");
			
			for(int i=0 ; i < sats.length() ;i++)
			{
				JSONObject sat = sats.getJSONObject(i);
				//System.out.println(sat.toString());
			//String result[] = res.split("/n");//将res字符串按照“/n”切割成小的字符串
			
				
					
			//生成ContentValues对象
			ContentValues values = new ContentValues();		
			//想该对象当中插入键值对，其中键是列名，值是希望插入到这一列的值，值必须和数据库当中的数据类型一致		
						
				values.put("id", i);
				values.put("sid",sat.getString("sid"));
				values.put("name",sat.getString("namecn"));
				values.put("category","Beidou");
				values.put("TLE1",sat.getString("tle1"));	
				values.put("TLE2",sat.getString("tle2"));
				values.put("info",sat.getString("info"));
				values.put("launch",sat.getString("launch")+" "+sat.getString("launchtime"));
				//调用insert方法，就可以将数据插入到数据库当中
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
	
	
	
	//显示导航卫星的信息
	public void NavigationDisplay() {
		
		myApp = (LocationApplication)getApplication(); 
		if(myApp.sattype == null)
		{
			myApp.sattype = "Beidou";
			//setListener();
			//return ;
		}
		//System.out.println("NavigationDisplay:" + myApp.sattype);
	    //创建一个DatabaseHelper对象
		DatabaseHelper dbHelper = new DatabaseHelper(AActivity.this,"sats_db");
		//只有调用了DatabaseHelper对象的getReadableDatabase()方法，或者是getWritableDatabase()方法之后，才会创建，或打开一个数据库
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
		
		//清除原来list的内容
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
				//把查询到的数据放进公共的数组中以便传递到另一activity中

				
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
			//TLE newTLE = new TLE("北斗M6", 
			//		"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			//		"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
			String name = "北斗M6";
			String TLE1 = "1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999";
			String TLE2 = "2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993";
			
			myApp = (LocationApplication)getApplication(); 
			myApp.setSid("38775U",mycount1);
			myApp.setTitle(name,mycount1++);
			myApp.setTLE1(TLE1,mycount2++);
			myApp.setTLE2(TLE2,mycount3++);
			 		
			Data data = new Data(name+"  北斗导航",TLE1+"\n"+TLE2);		
			dataList.add(data);		
			myApp.setTotal(new Integer(mycount1));
		}
		setListener();
			
	}
	

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
         switch(resultCode) { //根据状态码，处理返回结果
         case RESULT_OK:
        	 try {
               Bundle bundle =data.getExtras(); //获取intent里面的bundle对象
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
	if (keyCode == KeyEvent.KEYCODE_MENU) { // 监控/拦截/屏蔽返回键
	//do something
		Intent intent = new Intent();
		intent.setClass(AActivity.this,SettingTabActivity.class);
		AActivity.this.startActivity(intent);
	}
	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
        if((System.currentTimeMillis()-exitTime) > 2000){  
            Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();                                
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
	  * 设置为横屏
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
			builder1.setMessage("没有选中任何记录");
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
