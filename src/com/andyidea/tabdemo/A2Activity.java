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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.andyidea.tabdemo.AActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.AActivity.UpdateTLETask;
import com.andyidea.tabdemo.db.DatabaseHelper;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;


public class A2Activity extends Activity{

	private Button ButtonDone_a2;
	private Button ButtonEdit;

	private ListView Satinof_listview;
	private TextView Satinfo;
	private String satinfourl;
	private InputStream satinfoInputSteam;
	private String satinofres;

	private ImageView imageView = null;
	private Handler handler=null;

	private String imageUrl;

	private JSONObject dataJson,satinfoJson;
	Bitmap bitmap;

	String str2;

	StringBuffer sb = new StringBuffer();

	//定义数据，一个ArrayList，元素类型为Map<String, Object>  
    ArrayList<Map<String, Object>> array = new ArrayList<Map<String, Object>>();  
    Map<String, Object> map1 = new HashMap<String, Object>();  

    MyAdapter adapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_a2);

		//创建属于主线程的handler  
        handler=new Handler(); 
		
		ButtonDone_a2 = (Button)findViewById(R.id.ButtonDone_a2);		
		ButtonDone_a2.setOnClickListener(new ButtonOnClickListener());
		
		ButtonEdit = (Button)findViewById(R.id.ButtonEdit);
		ButtonEdit.setOnClickListener(new ButtonOnClickListener());

		Satinof_listview = (ListView)findViewById(R.id.satinfo_listview);
		imageView = (ImageView)findViewById(R.id.img_test);

//		Satinfo = (TextView)findViewById(R.id.satinfo);
		satinfourl = "http://www.4001149114.com/NLJJ/sattrack/satinfo?name=Beidou_G1";

		new UpdateTLETask().execute(4);

		//Satinfoupdate();

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

	private void Satinfoupdate(){

		satinfoInputSteam = streampost(satinfourl);//从网络上获取数据，输出格式是数据流格式
		satinofres = convertStreamToString(satinfoInputSteam);//将获取到的数据转换成字符串
/*
		if(satinofres.startsWith("\ufeff")){
			satinofres = satinofres.substring(1);
		}
*/
		System.out.println(satinofres);

		try {
			dataJson = new JSONObject(satinofres);
			satinfoJson = dataJson.getJSONObject("satinfo");

			imageUrl = satinfoJson.getString("imgurl");

			 map1 = new HashMap<String, Object>();
		     map1.put("user", "名称:");
		     map1.put("value", satinfoJson.getString("namecn"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "英文:");
		     map1.put("value", satinfoJson.getString("name"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "卫星编号:");
		     map1.put("value", satinfoJson.getString("satno"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "类型:");
		     map1.put("value", satinfoJson.getString("typename"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "在轨:");
		     map1.put("value", satinfoJson.getInt("valid"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "重量:");
		     map1.put("value", satinfoJson.getString("weight"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "发射工具:");
		     map1.put("value", satinfoJson.getString("launch"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "发射时间:");
		     map1.put("value", satinfoJson.getString("launchtime"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "tle1:");
		     map1.put("value", satinfoJson.getString("tle1"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "tle2:");
		     map1.put("value", satinfoJson.getString("tle2"));
		     array.add(map1);

		     map1 = new HashMap<String, Object>();
		     map1.put("user", "卫星资料:");
		     map1.put("value", satinfoJson.getString("info"));
		     array.add(map1);

		     /** 自定义一个Adapter
		         * 第一个参数为当前Activity，第二个array为指定需要显示的数据集合
		         * 第三个参数是将要显示每行数据的View布局
		         * 第四个参数是字符数组，对应View布局中的id名称
		         * 第五个参数是int数组，对应布局文件中的id.
		         */
		     adapter = new MyAdapter(this, array, R.layout.item_test,
	                    new String[]{"user", "value"},
	                    new int[]{R.id.user, R.id.value});


			Log.i("qcl", satinfoJson.getString("imgurl"));

		} catch (JSONException e) {
           System.out.println("Something wrong...");
            e.printStackTrace();
        }



/*
		String result[] = satinofres.split(",");//将res字符串按照“/n”切割成小的字符串
		for (int i=4;i<result.length;i++){
			System.out.println("qcl"+result[i]+"\n");

			str2=result[i].replaceAll("\\\\\"", "");
			System.out.println(str2+"\n");
			if(i == result.length-1){
				result[i] = result[i].substring(result[i].indexOf("w"), result[i].lastIndexOf("g")+1);
			}
			sb. append(result[i].replaceAll("\\\\\"", "")+"\n\n");

		}
*/

		new Thread(){
            public void run(){
            	 try {
                     byte[] data = ImageService.getImage(imageUrl);
                     bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);  //生成位图  
//                     imageView.setImageBitmap(bitmap);   //显示图片 
                 } catch (IOException e) {
                     Toast.makeText(A2Activity.this, "Network error", Toast.LENGTH_LONG).show();  //通知用户连接超时信息
                 }
                handler.post(runnableUi);
                }
        }.start();


    }

	Runnable   runnableUi=new  Runnable(){
        @Override
        public void run() {
            //更新界面  
//        	Satinfo.setText(sb.toString());

        	imageView.setImageBitmap(bitmap);   //显示图片  

        	//设置适配器 
        	Satinof_listview.setAdapter(adapter);
        }

    };

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

	class UpdateTLETask extends AsyncTask<Integer, Void, String>
	{

		@Override
		protected String doInBackground(Integer... params)
		{
			try
			{
				Satinfoupdate();
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
//			Satinfoupdate();
		}

	}

	  class MyAdapter extends SimpleAdapter
	  {
	        private LayoutInflater layout;
	        public MyAdapter(Context context, List<? extends Map<String, ?>> data,
	                int resource, String[] from, int[] to) {
	            super(context, data, resource, from, to);
	        }

	        @Override
	        public View getView(int position, View convertView, ViewGroup parent) {
	            // TODO Auto-generated method stub
	            View result = super.getView(position, convertView, parent);
	            //获取LayoutInflater的实例对象
	            layout = getLayoutInflater();
	            if (result == null) {
	                //载入指定布局 
	                layout.inflate(R.layout.item_test, null);
	            }
	            return result;
	        }

	    }

}
