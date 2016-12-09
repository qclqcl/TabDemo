package com.andyidea.tabdemo;



import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;

public class PassSettingActivity extends Activity{
	
	private Button ButtonDone,Savebtn;
	private Button Datebtn,Anglebtn;
	private TextView et,et2;	
	private LocationApplication myApp;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.passsetting);
		
		ButtonDone = (Button)findViewById(R.id.ButtonDone);		
		ButtonDone.setOnClickListener(new ButtonOnClickListener());
		
		Datebtn = (Button) findViewById(R.id.dateBtn);
		et = (TextView) findViewById(R.id.et);
		
		Anglebtn = (Button) findViewById(R.id.angleBtn);
		et2 = (TextView) findViewById(R.id.et2);
		
		Savebtn = (Button)findViewById(R.id.saveBtn);		
		Savebtn.setOnClickListener(new ButtonOnClickListener());
		
		myApp = (LocationApplication)getApplication();
/*		
		Datebtn.setOnClickListener(new View.OnClickListener() {
			Calendar c = Calendar.getInstance();
			@Override
			public void onClick(View v) {
				// ���һ��false��ʾ����ʾ���ڣ����Ҫ��ʾ���ڣ�������������true���߲�������
				new DoubleDatePickerDialog(PassSettingActivity.this, 0, new DoubleDatePickerDialog.OnDateSetListener() {

					@Override
					public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
							int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
							int endDayOfMonth) {
																		
						if(startYear>endYear){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "��ʼʱ�䲻�����ڽ���ʱ��", Toast.LENGTH_SHORT).show();
						}
						else if((startYear==endYear) && (startMonthOfYear>endMonthOfYear)){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "��ʼʱ�䲻�����ڽ���ʱ��", Toast.LENGTH_SHORT).show();
						}
						else if((startYear==endYear) && (startMonthOfYear==endMonthOfYear) && (startDayOfMonth>endDayOfMonth)){
							et.setText(" ");
							Toast.makeText(PassSettingActivity.this, "��ʼʱ�䲻�����ڽ���ʱ��", Toast.LENGTH_SHORT).show();
						}
						else{
						String textString = String.format("��ʼʱ�䣺%d-%d-%d\n����ʱ�䣺%d-%d-%d", startYear,
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
                        et.setText("ʱ��:"+String.valueOf(newVal)+"��");
                        myApp.setDelay(newVal);
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("ʱ���趨").setView(mPicker).setPositiveButton("ȷ��",null).create();
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
                        et2.setText("�Ƕ�:"+String.valueOf(newVal)+"��");
                        myApp.setAngle(newVal);
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("�Ƕ��趨").setView(mPicker).setPositiveButton("ȷ��",null).create();
                mAlertDialog.show();
            }
        });
		
		
		Savebtn.setOnClickListener(new OnClickListener() {
				
            @Override
            public void onClick(View v)
            {
            	SharedPreferences sharedPreferences = getSharedPreferences("test", Context.MODE_WORLD_READABLE);  
                Editor editor = sharedPreferences.edit();  
                
                editor.putString("Delay", String.valueOf(myApp.getDelay()));  
                editor.putString("Angle", String.valueOf(myApp.getAngle()));  
                editor.commit();  
                Toast.makeText(PassSettingActivity.this, "����ɹ�", Toast.LENGTH_LONG)  
                .show();
                //Intent intent = new Intent();
    			//intent.setClass(PassSettingActivity.this,DActivity.class);
    			//PassSettingActivity.this.startActivity(intent);
                finish();
            }
        });
		
	}
	
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
		//	Intent intent = new Intent();			
			switch (v.getId()) {	
			  
	           case R.id.ButtonDone:
	        	 //  intent.setClass(SatellitesSettingActivity.this,SettingTabActivity.class);
	        	 //  SatellitesSettingActivity.this.startActivity(intent);
	        	   
//	        	   if ( myApp.handlerD != null ){
//						myApp.handlerD.postDelayed(myApp.runnableD, 0);  //��ʼ������
//					}
	        	   	        	
//	        	   finish();
					break;    	   			        	   
			   default:
					break;
			}
		}
	}
	
	
	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", Context.MODE_PRIVATE);  
        String delay = sharedPreferences.getString("Delay", "");  
        String angle = sharedPreferences.getString("Angle", "");
		
//		et.setText("ʱ��:"+String.valueOf(myApp.getDelay())+"��");
//		et2.setText("�Ƕ�:"+String.valueOf(myApp.getAngle())+"��");
		
		et.setText("ʱ��:"+delay+"��");
		et2.setText("�Ƕ�:"+angle+"��");
        
	 super.onResume();
	 
	}
	
}
