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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;

public class PassSettingActivity extends Activity{
	
	private List<String> list = new ArrayList<String>();
	private Button ButtonDone,Savebtn;
	private Button Datebtn,Anglebtn;
	private TextView et,et2,et3;
	private Spinner Spinner_position;
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
		
		Spinner_position = (Spinner) findViewById(R.id.Spinner_position);
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

		showSpinner_position();
		
		myApp = (LocationApplication)getApplication();
/*		
		Datebtn.setOnClickListener(new View.OnClickListener() {
			Calendar c = Calendar.getInstance();
			@Override
			public void onClick(View v) {
				// 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
				new DoubleDatePickerDialog(PassSettingActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
							int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
							int endDayOfMonth) {
																		
						if(startYear>endYear){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "开始时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
						}
						else if((startYear==endYear) && (startMonthOfYear>endMonthOfYear)){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "开始时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
						}
						else if((startYear==endYear) && (startMonthOfYear==endMonthOfYear) && (startDayOfMonth>endDayOfMonth)){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "开始时间不能晚于结束时间", Toast.LENGTH_SHORT).show();
						}
						else{
						String textString = String.format("开始时间：%d-%d-%d\n结束时间：%d-%d-%d", startYear,
								startMonthOfYear + 1, startDayOfMonth, endYear, endMonthOfYear + 1, endDayOfMonth);
						et.setText(textString);
						}
					}
				}, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
			}
		});
*/

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

	public void showSpinner_position() {
		// 第一步：添加一个下拉列表项的list，这里添加的项就是下拉列表的菜单项
		list.add("当前位置");
		list.add("观测站位置");
		// 第二步：为下拉列表定义一个适配器，这里就用到里前面定义的list。
		final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_single_choice , list);
		// 第三步：为适配器设置下拉列表下拉时的菜单样式。 simple_spinner_item
		adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
		// 第四步：将适配器添加到下拉列表上
		Spinner_position.setAdapter(adapter);
		// 第五步：为下拉列表设置各种事件的响应，这个事响应菜单被选中
		Spinner_position.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				/* 将所选mySpinner 的值带入myTextView 中 */
				et3.setText("位置选择的是：" + adapter.getItem(position));
				if(position == 0){
					linearLayout_latitude.setVisibility(View.GONE);
					linearLayout_longitude.setVisibility(View.GONE);
					linearLayout_altitude.setVisibility(View.GONE);
				}else{
					linearLayout_latitude.setVisibility(View.VISIBLE);
					linearLayout_longitude.setVisibility(View.VISIBLE);
					linearLayout_altitude.setVisibility(View.VISIBLE);
				}
				myApp.setWatchPosNo(position);
				SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
				Editor editor = sharedPreferences.edit();
				editor.putInt("WatchPosNo", myApp.getWatchPosNo());
				editor.commit();
				/* 将mySpinner 显示 */
				arg0.setVisibility(View.VISIBLE);
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				et3.setText("NONE");
				arg0.setVisibility(View.VISIBLE);
			}
		});
	}
	
	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);  
        int delay = sharedPreferences.getInt("Delay", 0);  
        int angle = sharedPreferences.getInt("Angle", 0);
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
		Spinner_position.setSelection(WatchPosNo);

	 super.onResume();
	 
	}

}
