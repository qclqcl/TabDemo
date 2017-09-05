package com.andyidea.tabdemo;



import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.ToggleButton;

public class PassSettingActivity extends Activity{
	
	private List<String> list = new ArrayList<String>();
	private Button ButtonDone,Savebtn;
	private Button Datebtn,Anglebtn;
	private TextView et,et2,et3;
	private ToggleButton Toggle_position;
	private LinearLayout linearLayout_latitude,linearLayout_longitude,linearLayout_altitude;
	private EditText watch_latitude,watch_longitude,watch_altitude;
	private LocationApplication myApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.passsetting);

		ButtonDone = (Button)findViewById(R.id.ButtonDone);

		Datebtn = (Button) findViewById(R.id.dateBtn);
		et = (TextView) findViewById(R.id.et);
		
		Anglebtn = (Button) findViewById(R.id.angleBtn);
		et2 = (TextView) findViewById(R.id.et2);
		
		Toggle_position = (ToggleButton) findViewById(R.id.Toggle_position);
		et3 = (TextView) findViewById(R.id.et3);

		watch_latitude = (EditText)findViewById(R.id.watch_latitude);
		watch_longitude = (EditText)findViewById(R.id.watch_longitude);
		watch_altitude = (EditText)findViewById(R.id.watch_altitude);

		linearLayout_latitude = (LinearLayout) findViewById(R.id.linearLayout_latitude);
		linearLayout_latitude.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
		linearLayout_longitude = (LinearLayout) findViewById(R.id.linearLayout_longitude);
		linearLayout_longitude.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域
		linearLayout_altitude = (LinearLayout) findViewById(R.id.linearLayout_altitude);
		linearLayout_altitude.setVisibility(View.GONE);//这一句即隐藏布局LinearLayout区域

		myApp = (LocationApplication)getApplication();

		Datebtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                NumberPicker mPicker = new NumberPicker(PassSettingActivity.this);       
                mPicker.setMinValue(0);
                mPicker.setMaxValue(10);
                mPicker.setOnValueChangedListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
                    {
                        // TODO Auto-generated method stub
                        et.setText("时间:"+String.valueOf(newVal)+"天");
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("时间设定").setView(mPicker).setPositiveButton("确定",null).create();
                mAlertDialog.show();
            }
        });
		
		Anglebtn.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v)
            {
                // TODO Auto-generated method stub
                NumberPicker mPicker = new NumberPicker(PassSettingActivity.this);
                mPicker.setMinValue(0);
                mPicker.setMaxValue(180);
                mPicker.setOnValueChangedListener(new OnValueChangeListener() {
                    
                    @Override
                    public void onValueChange(NumberPicker picker, int oldVal, int newVal)
                    {
                        // TODO Auto-generated method stub
                        et2.setText("角度:"+String.valueOf(newVal)+"°");
//                        myApp.setAngle(new Integer(newVal));
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("角度设定").setView(mPicker).setPositiveButton("确定",null).create();
                mAlertDialog.show();
            }
        });

		Toggle_position.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if(isChecked){
					//未选中
					linearLayout_latitude.setVisibility(View.VISIBLE);
					linearLayout_longitude.setVisibility(View.VISIBLE);
					linearLayout_altitude.setVisibility(View.VISIBLE);
					myApp.setWatchPosNo(1);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("WatchPosNo", myApp.getWatchPosNo());
					editor.commit();
				}else{
					//选中
					linearLayout_latitude.setVisibility(View.GONE);
					linearLayout_longitude.setVisibility(View.GONE);
					linearLayout_altitude.setVisibility(View.GONE);
					myApp.setWatchPosNo(0);
					SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
					Editor editor = sharedPreferences.edit();
					editor.putInt("WatchPosNo", myApp.getWatchPosNo());
					editor.commit();
				}
			}
		});// 添加监听事件		

		ButtonDone.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v)
            {
            	String delay_text = (String) et.getText().toString();
            	String angle_text = (String) et2.getText().toString();
            	String watch_latitude_text = (String) watch_latitude.getText().toString();
            	String watch_longitude_text = (String) watch_longitude.getText().toString();
            	String watch_altitude_text = (String) watch_altitude.getText().toString();
            	Log.i("hhhhhhhhhhh",delay_text);
            	
            	delay_text=delay_text.trim();
            	String delay_string="";
            	if(delay_text != null && !"".equals(delay_text)){
            		for(int i=0;i<delay_text.length();i++){
            			if(delay_text.charAt(i)>=48 && delay_text.charAt(i)<=57){
            				delay_string+=delay_text.charAt(i);
            			}
            		}
            	}
            	
            	angle_text=angle_text.trim();
            	String angle_string="";
            	if(angle_text != null && !"".equals(angle_text)){
            		for(int i=0;i<angle_text.length();i++){
            			if(angle_text.charAt(i)>=48 && angle_text.charAt(i)<=57){
            				angle_string+=angle_text.charAt(i);
            			}
            		}
            	}

            	watch_latitude_text=watch_latitude_text.trim();
            	watch_longitude_text=watch_longitude_text.trim();
            	watch_altitude_text=watch_altitude_text.trim();

            	myApp.setDelay(Integer.parseInt(delay_string));
            	myApp.setAngle(Integer.parseInt(angle_string));
            	myApp.setWatchLatitude(Float.parseFloat(watch_latitude_text));
            	myApp.setWatchLongitude(Float.parseFloat(watch_longitude_text));
            	myApp.setWatchAltitude(Float.parseFloat(watch_altitude_text));

            	SharedPreferences sharedPreferences = getSharedPreferences("test", 0);  
            	Editor editor = sharedPreferences.edit();  

            	editor.putInt("Delay", myApp.getDelay());  
            	editor.putInt("Angle", myApp.getAngle()); 
            	editor.putFloat("WatchLatitude", myApp.getWatchLatitude());
            	editor.putFloat("WatchLongitude", myApp.getWatchLongitude());
            	editor.putFloat("WatchAltitude", myApp.getWatchAltitude());
            	editor.commit();  
                Toast.makeText(PassSettingActivity.this, "保存成功", Toast.LENGTH_LONG)  
                .show();
                finish();
            }
        });
	}

	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);  
        int delay = sharedPreferences.getInt("Delay", 10);  
        int angle = sharedPreferences.getInt("Angle", 10);
        int WatchPosNo = sharedPreferences.getInt("WatchPosNo", 0);
        float WatchLatitude = sharedPreferences.getFloat("WatchLatitude",0);
        float WatchLongitude = sharedPreferences.getFloat("WatchLongitude",0);
        float WatchAltitude = sharedPreferences.getFloat("WatchAltitude",0);
//		et.setText("时间:"+String.valueOf(myApp.getDelay())+"天");
//		et2.setText("角度:"+String.valueOf(myApp.getAngle())+"°");
		
		et.setText("时间:"+String.valueOf(delay)+"天");
		et2.setText("角度:"+String.valueOf(angle)+"°");
		watch_latitude.setText(WatchLatitude + "");
		watch_longitude.setText(WatchLongitude + "");
		watch_altitude.setText(WatchAltitude + "");
		Toggle_position.setChecked((WatchPosNo==1)?true:false);
		if(WatchPosNo==1){
			//未选中
			linearLayout_latitude.setVisibility(View.VISIBLE);
			linearLayout_longitude.setVisibility(View.VISIBLE);
			linearLayout_altitude.setVisibility(View.VISIBLE);
		}else{
			//选中
			linearLayout_latitude.setVisibility(View.GONE);
			linearLayout_longitude.setVisibility(View.GONE);
			linearLayout_altitude.setVisibility(View.GONE);
		}
	 super.onResume();
	 
	}

}
