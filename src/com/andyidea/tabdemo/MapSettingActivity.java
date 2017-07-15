package com.andyidea.tabdemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MapSettingActivity extends Activity{

	private List<String> list = new ArrayList<String>();
	private Button ButtonDone;
	private ToggleButton  SatellitenameBtn,SatellitecoverageBtn;
	private TextView SatellitenameTv,SatellitecoverageTv,SpinnerText;
	private Spinner Spinner1;
	private LocationApplication myApp;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mapsetting);

		ButtonDone = (Button)findViewById(R.id.ButtonDone);
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
		
		SpinnerText = (TextView) findViewById(R.id.SpinnerText);
		Spinner1 = (Spinner) findViewById(R.id.Spinner1);

		myApp = (LocationApplication)getApplication();

		SatellitenameBtn = (ToggleButton) findViewById(R.id.SatellitenameBtn);
		SatellitenameTv = (TextView) findViewById(R.id.SatellitenameTv);

		SatellitecoverageBtn = (ToggleButton) findViewById(R.id.SatellitecoverageBtn);
		SatellitecoverageTv = (TextView) findViewById(R.id.SatellitecoverageTv);

		showSpinner1();
		
		SatellitenameBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//ѡ��
					myApp.setdisSatname(true);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatName", myApp.getdisSatname()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "��ʾ��������", Toast.LENGTH_SHORT)
//	                .show();
				}else{
					//δѡ��
					myApp.setdisSatname(false);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatName", myApp.getdisSatname()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "����ʾ��������", Toast.LENGTH_SHORT)
//	                .show();
				}
			}
		});// ��Ӽ����¼�
		
		SatellitecoverageBtn.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//ѡ��
					myApp.setdisSatcoverage(true);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatCoverage", myApp.getdisSatcoverage()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "��ʾ���Ǹ��Ƿ�Χ", Toast.LENGTH_SHORT)
//	                .show();
				}else{
					//δѡ��
					myApp.setdisSatcoverage(false);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("DisSatCoverage", myApp.getdisSatcoverage()==true?1:0);
					editor.commit();
//	            	Toast.makeText(MapSettingActivity.this, "����ʾ���Ǹ��Ƿ�Χ", Toast.LENGTH_SHORT)
//	                .show();
				}
			}
		});// ��Ӽ����¼�
	}

	public void showSpinner1() {
		// ��һ�������һ�������б����list��������ӵ�����������б�Ĳ˵���
		list.add("map0");
		list.add("map1");
		list.add("map2");
		// �ڶ�����Ϊ�����б���һ����������������õ���ǰ�涨���list��
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice , list);
		// ��������Ϊ���������������б�����ʱ�Ĳ˵���ʽ�� simple_spinner_item
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		// ���Ĳ�������������ӵ������б���
		Spinner1.setAdapter(adapter);
		// ���岽��Ϊ�����б����ø����¼�����Ӧ���������Ӧ�˵���ѡ��
		Spinner1.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				/* ����ѡmySpinner ��ֵ����myTextView �� */
				SpinnerText.setText("��ͼѡ����ǣ�" + adapter.getItem(position));
				myApp.setMapNo(position);
				SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
				Editor editor = sharedPreferences.edit();
				editor.putInt("MapNo", myApp.getMapNo());
				editor.commit();
				/* ��mySpinner ��ʾ */
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				SpinnerText.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
	           case R.id.ButtonDone:
	        	   finish();
	        	   break;
			   default:
				   break;
			}
		}
	}

	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        int DisSatName = sharedPreferences.getInt("DisSatName", 0);
        int DisSatCoverage = sharedPreferences.getInt("DisSatCoverage", 0);
        int MapNo = sharedPreferences.getInt("MapNo", 0);

        SatellitenameBtn.setChecked((DisSatName==1)?true:false);
        SatellitecoverageBtn.setChecked((DisSatCoverage==1)?true:false);
		Spinner1.setSelection(MapNo);

		super.onResume();
	}

}
