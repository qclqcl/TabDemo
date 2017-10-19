package com.andyidea.tabdemo;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.text.SimpleDateFormat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.andyidea.tabdemo.A2Activity.UpdateTLETask;
import com.andyidea.tabdemo.EActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.service.LocationService;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.model.LatLng;

import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TabHost;

import com.andyidea.tabdemo.db.DatabaseHelper;
import com.andyidea.tabdemo.download.DownloadApk;
import com.andyidea.tabdemo.download.DownLoadUtils;
import com.andyidea.tabdemo.download.ApkInstallReceiver;

public class MainTabActivity extends TabActivity implements OnCheckedChangeListener{
	private Handler handler;
	private Runnable runnable;
	
	private LocationApplication myApp;
	
	private TabHost mTabHost;
	private Intent mAIntent;
	private Intent mBIntent;
	private Intent mCIntent;
	private Intent mDIntent;
	private Intent mEIntent;

	private String time=null;

	private Button RadioButtonE;
	private Button RadioButtonHelp;
	Intent intent = new Intent();

	//�������л�ȡapk������Ϣ����ر���
	private String checkupdateurl;
	private String res_qcl;
	private String localVersion;
	private String serverVersion;
	private String downloadUrl;
	private InputStream InputSteam;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.maintabs);

        checkupdateurl = "https://www.4001149114.com/NLJJ/ddapp/updateapp?appname=SATT";
//        new UpdateTLETask().execute(4);

        myApp = (LocationApplication)getApplication();
		myApp.locationService.registerListener(myApp.mListener);
		//ע�����
		myApp.locationService.setLocationOption(myApp.locationService.getDefaultLocationClientOption());
		myApp.locationService.start();

        RadioButtonE = (Button) findViewById(R.id.radio_button4);
        RadioButtonE.setOnClickListener(new ButtonOnClickListener());

        RadioButtonHelp = (Button) findViewById(R.id.radio_help);
        RadioButtonHelp.setOnClickListener(new ButtonOnClickListener());

        handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {				
				handler.postDelayed(this, 1000);
				getTime();
//				myApp.locationService.stop();
			}
		};

		handler.postDelayed(runnable, 0);
        this.mAIntent = new Intent(this,AActivity.class);
        this.mBIntent = new Intent(this,BActivity.class);
        this.mCIntent = new Intent(this,CActivity.class);
        this.mDIntent = new Intent(this,DActivity.class);
        this.mEIntent = new Intent(this,EActivity.class);

        myApp = (LocationApplication)getApplication();
        
		((RadioButton) findViewById(R.id.radio_button0))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button1))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button2))
		.setOnCheckedChangeListener(this);
        ((RadioButton) findViewById(R.id.radio_button3))
		.setOnCheckedChangeListener(this);

        setupIntent();
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

	//��ȡ�汾��
	public String getlocalVersion() {

		String versionName = "";
		try {
	        PackageManager manager = this.getPackageManager();
	        PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
	        versionName = info.versionName;
	        if (versionName == null || versionName.length() <= 0) {
	            return "";
	        }
	    } catch (Exception e) {
	    	Log.e("VersionInfo", "Exception", e);
	    }
	    return versionName;
	}

	// �Ƚϰ汾�ŵĴ�С,ǰ�ߴ��򷵻�һ������,���ߴ󷵻�һ������,����򷵻�0
	public int compareVersion(String version1, String version2){
	    if (version1 == null || version2 == null) {
	        Log.e("...","compareVersion error:illegal params.");
	    }
	    String[] versionArray1 = version1.split("\\.");//ע��˴�Ϊ����ƥ�䣬������"."��
	    String[] versionArray2 = version2.split("\\.");
	    int idx = 0;
	    int minLength = Math.min(versionArray1.length, versionArray2.length);//ȡ��С����ֵ
	    int diff = 0;
	    while (idx < minLength
	            && (diff = versionArray1[idx].length() - versionArray2[idx].length()) == 0//�ȱȽϳ���
	            && (diff = versionArray1[idx].compareTo(versionArray2[idx])) == 0) {//�ٱȽ��ַ�
	        ++idx;
	    }
	    //����Ѿ��ֳ���С����ֱ�ӷ��أ����δ�ֳ���С�����ٱȽ�λ�������Ӱ汾��Ϊ��
	    diff = (diff != 0) ? diff : versionArray1.length - versionArray2.length;
	    return diff;
	}

	private void downloadApk(String downloadURL) {
		//1.ע�����ع㲥������
        DownloadApk.registerBroadcast(this);
        //2.ɾ���Ѵ��ڵ�Apk
        DownloadApk.removeFile(this);

        //3.����ֻ��Ѿ��������س���ִ��downloadApk��������ת�����ý���
        if (DownLoadUtils.getInstance(getApplicationContext()).canDownload()) {
            DownloadApk.downloadApk(getApplicationContext(), downloadURL, "���Ǹ��ٸ���", "���Ǹ���");
        } else {
            DownLoadUtils.getInstance(getApplicationContext()).skipToDownloadManager();
        }
	}

    private void showNoticeDialog() {
    	AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage("����汾����");
		dialog.setPositiveButton("����",
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Toast.makeText(MainTabActivity.this, "��������......", Toast.LENGTH_LONG).show();
						downloadApk(downloadUrl);
						arg0.dismiss();
					}
				});
		dialog.setNeutralButton("ȡ��", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		} );
		dialog.show();
    }

	public void getupdateinfo() {
		InputSteam = streampost(checkupdateurl);//�������ϻ�ȡ���ݣ������ʽ����������ʽ
		res_qcl = convertStreamToString(InputSteam);//����ȡ��������ת�����ַ���
		try{
			JSONObject result = new JSONObject(res_qcl.toString());
			if("success".equals(result.getString("message"))){
				serverVersion = result.getString("serverVersion");
				Log.e("...",serverVersion);
				downloadUrl = result.getString("updateurl");
				Log.e("...",downloadUrl);
			}else{
				System.out.println("get sat info error");
			}
		}
		catch (JSONException e)
		{
			System.out.println("get sat info JSONException");
		}
	}

	public void updateapk() {
		localVersion = getlocalVersion();
		Log.e("...",localVersion);
		if (serverVersion == null) {
			Toast.makeText(MainTabActivity.this, "�������磬δ��ȡ���汾��Ϣ", Toast.LENGTH_LONG).show();
		}
		else{
			if( compareVersion(serverVersion, localVersion) > 0 ){
				showNoticeDialog();
			}
			else{
				Toast.makeText(MainTabActivity.this, "�Ѿ������°汾", Toast.LENGTH_LONG).show();
			}
		}
	}

    private void getTime() {
		// ��ȡϵͳʱ��
		Time t = new Time();
		t.setToNow(); // ȡ��ϵͳʱ��
		int year = t.year - 2000;
		int month = t.month + 1;
		int date = t.monthDay;
		int hour = t.hour; // 24Сʱ��
		int minute = t.minute;
		int second = t.second;
		time = (year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second).toString();
		RadioButtonE.setText(time);
		RadioButtonE.setTextSize(7);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				
		if(isChecked){
			switch (buttonView.getId()) {
			case R.id.radio_button0:
//				myApp.locationService.start();

				this.mTabHost.setCurrentTabByTag("A_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //ֹͣ������
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //ֹͣ������
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //ֹͣ������
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //ֹͣ������
				}
				break;
			case R.id.radio_button1:
//				myApp.locationService.start();

				this.mTabHost.setCurrentTabByTag("B_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.postDelayed(myApp.runnableB, 0);  //��ʼ������
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //ֹͣ������
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //ֹͣ������
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //ֹͣ������
				}
				break;
			case R.id.radio_button2:
//				myApp.locationService.start();

				this.mTabHost.setCurrentTabByTag("C_TAB");
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //ֹͣ������
				}
				if ( myApp.handlerD != null ){
					myApp.handlerD.removeCallbacks(myApp.runnableD); //ֹͣ������
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.postDelayed(myApp.runnableC, 0);  //��ʼ������
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.postDelayed(myApp.runnableC2, 0);  //��ʼ������
				}
				break;
			case R.id.radio_button3:
//				myApp.locationService.start();

				this.mTabHost.setCurrentTabByTag("D_TAB");
				if ( myApp.handlerD != null ){
					myApp.handlerD.postDelayed(myApp.runnableD, 0);  //��ʼ������
				}
				if ( myApp.handlerB != null ){
					myApp.handlerB.removeCallbacks(myApp.runnableB); //ֹͣ������
				}
				if ( myApp.handlerC != null ){
					myApp.handlerC.removeCallbacks(myApp.runnableC); //ֹͣ������
				}
				if ( myApp.handlerC2 != null ){
					myApp.handlerC2.removeCallbacks(myApp.runnableC2); //ֹͣ������
				}
				break;
			default:
				break;
			}
		}
	}

	public boolean isNetworkConnected() {
        ConnectivityManager mConnectivityManager = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (mNetworkInfo != null) {
            return mNetworkInfo.isAvailable();
        }
	    return false;
	}

	class ButtonOnClickListener implements OnClickListener{
		
		@Override
		public void onClick(View v) {		
			switch (v.getId()) {			
				case R.id.radio_help:
					//this.mTabHost.setCurrentTabByTag("E_TAB");
					intent.setClass(MainTabActivity.this,EActivity.class);
					MainTabActivity.this.startActivity(intent);
					break;
				case R.id.radio_button4:

					if(isNetworkConnected()){
						new UpdateTLETask().execute(4);
						try {
				            Thread.sleep(500);
				        } catch (InterruptedException e) {
				            e.printStackTrace();
				        }
					}
					else{
						serverVersion = null;
					}
					updateapk();
					break;
			   default:
					break;
			}
		}
	}

         

	private void setupIntent() {
		this.mTabHost = getTabHost();
		TabHost localTabHost = this.mTabHost;

		localTabHost.addTab(buildTabSpec("A_TAB", R.string.main_stats,
				R.drawable.icon,this.mAIntent));
		localTabHost.addTab(buildTabSpec("B_TAB", R.string.main_map,
				R.drawable.icon, this.mBIntent));
		localTabHost.addTab(buildTabSpec("C_TAB",R.string.main_sky, 
				R.drawable.icon,this.mCIntent));
		localTabHost.addTab(buildTabSpec("D_TAB", R.string.main_pass,
				R.drawable.icon, this.mDIntent));
		localTabHost.addTab(buildTabSpec("E_TAB", R.string.main_more,
				R.drawable.icon, this.mEIntent));
	}
	
	private TabHost.TabSpec buildTabSpec(String tag, int resLabel, int resIcon,
			final Intent content) {
		return this.mTabHost.newTabSpec(tag).setIndicator(getString(resLabel),
				getResources().getDrawable(resIcon)).setContent(content);
	}
	
	class UpdateTLETask extends AsyncTask<Integer, Void, String>
	{
		@Override
		protected String doInBackground(Integer... params)
		{
			try
			{
				getupdateinfo();
				return "OK";//mDatas = mNewsItemBiz.getNews(url).getNewses();
			} catch (Exception e)
			{
				Looper.prepare();
//				Toast.makeText(getApplicationContext(), e.getMessage(), 1).show();
				Looper.loop();
			}
			return null;
		}

		@Override
		protected void onPostExecute(String lla)
		{
			if(lla == null)
				return;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // ���/����/���η��ؼ�
	//do something
		Intent intent = new Intent();
		intent.setClass(MainTabActivity.this,PassSettingActivity.class);
		MainTabActivity.this.startActivity(intent);
	}  
	return super.onKeyDown(keyCode, event);
	}

	/***
	 * Stop location service
	 */
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		myApp.locationService.unregisterListener(myApp.mListener); //ע��������
		myApp.locationService.stop(); //ֹͣ��λ����
		super.onStop();
	}
	@Override
	protected void onResume() {
		myApp.locationService.start();
		super.onResume();
	}

}
