package com.andyidea.tabdemo;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.andyidea.tabdemo.AActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.AActivity.UpdateTLETask;
import com.andyidea.tabdemo.db.DatabaseHelper;
import com.andyidea.tabdemo.utils.SharePreferenceUtil;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class A3Activity extends Activity{
	
	private Button ButtonDone_a3;
	
	private EditText mUserName_ET, mPassword_ET;
	private Button mLogin_btn;

	private String username;
	private String password;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_a3);

		SharePreferenceUtil.initPreference(this);
		initView();
	}

	@Override
	protected void onResume() {
		// ���SharedPreferences�ļ����Ƿ񱣴����û���������		
		super.onResume();
		
		String usernameStr = SharePreferenceUtil.getString("username", null);
		String passwordStr = SharePreferenceUtil.getString("password", null);
		if (!TextUtils.isEmpty(usernameStr)) {
			mUserName_ET.setText(usernameStr);
		} else if (!TextUtils.isEmpty(passwordStr)) {

			mPassword_ET.setText(passwordStr);

		}

	}
	
	private void initView() {
		ButtonDone_a3 = (Button)findViewById(R.id.ButtonDone_a3);		
		ButtonDone_a3.setOnClickListener(new ButtonOnClickListener());
		
		mLogin_btn = (Button) findViewById(R.id.login_btn);
		mUserName_ET = (EditText) findViewById(R.id.user_name_ET);
		mPassword_ET = (EditText) findViewById(R.id.pass_word_ET);

		mLogin_btn.setOnClickListener(new ButtonOnClickListener());
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {				          	           		
	           case R.id.ButtonDone_a2: 
	        	   finish();
					break;
	           case R.id.login_btn:	        	  	        
	        	   login();    	   	        	   	        		   	         	   				   				   				   				   			
					break;

			   default:
					break;
			}
		}
	}

	private void login() {
		username = mUserName_ET.getText().toString().trim();
		password = mPassword_ET.getText().toString().trim();
        //����ֻ��֤��ʽ�Ƿ�Ϸ�������֤�û������������ȷ�ԣ�SharedPreferences�����ݴ洢������
		if (checkPhone(username) && checkNotNull(password)) {
			
			SharePreferenceUtil.putString("username", username);
			SharePreferenceUtil.putString("password", password);
			toEditSatActivity();
			
		}

	}
	private void toEditSatActivity() {
		Intent intent = new Intent(this, EditSatActivity.class);
//		Bundle bundle=new Bundle();
//		bundle.putString("username", username);
//		bundle.putString("password", password);
//		intent.putExtras(bundle);
		startActivity(intent);
		A3Activity.this.finish();
	}

	/**
	 * @param value ��Ҫ��֤���û���
	 * @return
	 */
	private boolean checkNotNull(String value) {
		if (!TextUtils.isEmpty(value)) {
			if (value.length() < 6) {
				Toast.makeText(this, "������д���Ϸ���", Toast.LENGTH_LONG).show();// ������д����ȷ
				return false;
			} else {
				return true;
			}

		} else {
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_LONG).show();
			return false;
		}
	}

	/**
	 * @param phone ��Ҫ��֤���ֻ�����
	 * @return
	 */
	private boolean checkPhone(String phone) {
		if (!TextUtils.isEmpty(phone)) {
			if (ismobileNO(phone)) {
				return true;
			} else {
				Toast.makeText(this, "������д����ȷ", Toast.LENGTH_LONG).show();// ������д����ȷ
				return false;
			}
		} else {
			Toast.makeText(this, "���벻��Ϊ��", Toast.LENGTH_LONG).show();// ����Ϊ��
																		
			return false;
		}
	}

	/**
	 * �ֻ��������֤���ϸ���֤
	 * 
	 * @param /mobiles Ҫ��֤���ֻ�����
	 * @return
	 */
	public static boolean ismobileNO(String mobiles) {
		if (TextUtils.isEmpty(mobiles)) {
			return false;
		}
	Pattern p = Pattern
	.compile("^((13[0-9])|(14[5,7])|(15[^4,\\D])|(17[0,6,7,8])|(18[0-9]))\\d{8}$");
		Matcher m = p.matcher(mobiles);
		return m.matches();
	}

}
