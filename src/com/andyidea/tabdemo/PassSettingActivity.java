package com.andyidea.tabdemo;



import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
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
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("ʱ���趨").setView(mPicker).setPositiveButton("ȷ��",null).create();
                Window window = mAlertDialog.getWindow();
                window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM);
                mAlertDialog.show();
              //���ô�С
                WindowManager.LayoutParams layoutParams = mAlertDialog.getWindow().getAttributes();
                layoutParams.width = 800;
                layoutParams.height = 800;
                mAlertDialog.getWindow().setAttributes(layoutParams);
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
//                        myApp.setAngle(new Integer(newVal));
                    }
                });
                
                
                AlertDialog mAlertDialog = new AlertDialog.Builder(PassSettingActivity.this)
                .setTitle("�Ƕ��趨").setView(mPicker).setPositiveButton("ȷ��",null).create();
                Window window = mAlertDialog.getWindow();
                window.setGravity(Gravity.CENTER);   //window.setGravity(Gravity.BOTTOM);
                mAlertDialog.show();
              //���ô�С
                WindowManager.LayoutParams layoutParams = mAlertDialog.getWindow().getAttributes();
                layoutParams.width = 800;
                layoutParams.height = 800;
                mAlertDialog.getWindow().setAttributes(layoutParams);
            }
        });


		Savebtn.setOnClickListener(new OnClickListener() {
				
            @Override
            public void onClick(View v)
            {
            	String delay_text = (String) et.getText().toString();
            	String angle_text = (String) et2.getText().toString();
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
            	
            	myApp.setDelay(Integer.parseInt(delay_string));
            	myApp.setAngle(Integer.parseInt(angle_string));
            	
            	SharedPreferences sharedPreferences = getSharedPreferences("test", 0);  
            	Editor editor = sharedPreferences.edit();  
                
            	editor.putInt("Delay", myApp.getDelay());  
            	editor.putInt("Angle", myApp.getAngle());  
            	editor.commit();  
                Toast.makeText(PassSettingActivity.this, "����ɹ�", Toast.LENGTH_LONG)  
                .show();
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
	           case R.id.saveBtn:
	        	   
			   default:
					break;
			}
		}
	}

	@Override
	protected void onResume() {

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);  
        int delay = sharedPreferences.getInt("Delay", 0);  
        int angle = sharedPreferences.getInt("Angle", 0);
		
//		et.setText("ʱ��:"+String.valueOf(myApp.getDelay())+"��");
//		et2.setText("�Ƕ�:"+String.valueOf(myApp.getAngle())+"��");
		
		et.setText("ʱ��:"+String.valueOf(delay)+"��");
		et2.setText("�Ƕ�:"+String.valueOf(angle)+"��");
        
	 super.onResume();
	 
	}

}
