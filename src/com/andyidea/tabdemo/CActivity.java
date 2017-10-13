package com.andyidea.tabdemo;

import java.util.List;

import name.gano.astro.AER;
import name.gano.astro.time.Time;
import satellite.tle.image.TLEDraw;
import satellite.tle.objects.GroundStation;
import satellite.tle.objects.SatelliteTleSGP4;
import satellite.tle.utilities.LLA;
import satellite.tle.utilities.TLE;
import satellite.tle.utilities.TLECompute;

import com.andyidea.tabdemo.BActivity.ButtonOnClickListener;
import com.andyidea.tabdemo.image.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CActivity extends Activity{
	//定时器的变量初始化
//	private Handler handler;
//	private Runnable runnable;
	private int x,y;
	
	private Button Buttonleft,Buttonright,ButtonPrefs;
	private Button Buttonback,Buttonnormal,Buttonfast;
	private TextView TextView0,TextView1;
	private LocationApplication myApp;
//	private int counttest = 0;
	private int factorOneInt = 0;
	
	//画线方面的初始化
	ImageView img;
	private Bitmap imgMarker;
	private int width,height;  //图片的高度和宽带
	private Bitmap imgTemp;    //临时标记图
	
	private long exitTime = 0;
	
	private Bitmap personBitmap;
	private Bitmap satelliteBitmap;
	private Bitmap satellitenoBitmap;
	TLE newTLE = new TLE("北斗M6", 
			"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
	double[] gsLLA={21.56694,-158.2519,317.9};
	private List<LLA> llas;
	LLA curlla;
	Time timer ;
	int interval = 1;
	int DisSatName = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_3);

		Buttonleft = (Button)findViewById(R.id.Buttonleft);
		Buttonleft.setText("<<");
		Buttonleft.setOnClickListener(new ButtonOnClickListener());
		
		TextView0 = (TextView)findViewById(R.id.TextView0);
		TextView1 = (TextView)findViewById(R.id.TextView1);
		
		Buttonright = (Button)findViewById(R.id.Buttonright);
		Buttonright.setOnClickListener(new ButtonOnClickListener());
		
		ButtonPrefs = (Button)findViewById(R.id.ButtonPrefs);
		ButtonPrefs.setOnClickListener(new ButtonOnClickListener());
		
		Buttonback = (Button)findViewById(R.id.Buttonback);
		Buttonback.setOnClickListener(new ButtonOnClickListener());
		
		Buttonnormal = (Button)findViewById(R.id.Buttonnormal);
		Buttonnormal.setOnClickListener(new ButtonOnClickListener());
		
		Buttonfast = (Button)findViewById(R.id.Buttonfast);
		Buttonfast.setOnClickListener(new ButtonOnClickListener());
		
		myApp = (LocationApplication)getApplication();

		TextView0.setTextSize(10);
		TextView1.setTextSize(10);	
		TextView0.setGravity(Gravity.CENTER);
		TextView0.setText("      "+myApp.getTitle(myApp.counttest));
		
		//关于图片的背景的设置
		img = (ImageView) findViewById(R.id.map11);
		imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.sky);

		width = imgMarker.getWidth();
		height = imgMarker.getHeight();
		
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		timer = new Time();
		//timer.add(Time.HOUR,-12);
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
		initgslla();
		//llas= TLECompute.getPolarLead(newTLE,gsLLA,timer);
		//timer.add(Time.HOUR,12);
		curlla = TLECompute.getPolarAE(newTLE,gsLLA,timer);
		//personBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.person_mark);
		//satelliteBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.satellite_mark);
		//satellitenoBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.satellite_no);
		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        DisSatName = sharedPreferences.getInt("DisSatName", 0);		
		//定时器
		myApp.handlerC = new Handler();
		myApp.runnableC = new Runnable() {
			@Override
			public void run() {				
				myApp.handlerC.postDelayed(this, 2000);
				
				img.setBackgroundDrawable(createDrawable('A',x++,y++));	
				if(x==200) x=0;
				if(y==200) y=0;
				
				//myApp.getTitle(myApp.counttest);
				//myApp.getTLE1(myApp.counttest);
				//myApp.getTLE2(myApp.counttest);
				
				newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
				TextView0.setText("      "+myApp.getTitle(myApp.counttest));
				TextView0.setGravity(Gravity.CENTER);
				
				//gsLLA[0] = myApp.myLatitude;
				//gsLLA[1] = myApp.myLongitude;
				//gsLLA[2] = myApp.myAltitude;
				initgslla();
				timer.addSeconds(interval);
				curlla = TLECompute.getPolarAE(newTLE,gsLLA,timer);
				LLA gs = new LLA(gsLLA[0],gsLLA[1],gsLLA[2]);
				TextView1.setText(/*newTLE.getSatName().trim()+*/"卫星（方位角:"+curlla.getAStr()+"，高度角:"+curlla.getEStr()+"，半径:"+curlla.getAltStr()+"）\n"+
						"观测站（经度:"+gs.getLatStr()+"，"+"纬度:"+gs.getLonStr()+"，"+"高度:"+gs.getAltStr()+"）");
				//TextView1.setText("Latitude: "+myApp.myLatitude+"     "+"Longtitude: "+myApp.myLongitude+"     "+"Altitude: "+myApp.myAltitude);
			}
		};
		myApp.handlerC.postDelayed(myApp.runnableC, 0);	
		
}
	
	
	private Drawable createDrawable(char letter,int x,int y) {
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 0, width, height);
		//canvas.drawBitmap(imgMarker, src, dst, paint);

		Paint textPaint = new Paint();
		//textPaint.setTextSize(20.0f);
		//textPaint.setTypeface(Typeface.DEFAULT_BOLD); // 采用默认的宽度
		//textPaint.setColor(Color.RED);
		
		textPaint.setSubpixelText(true);
		textPaint.setAntiAlias(true);
		textPaint.setFilterBitmap(true);
		
		textPaint.setTextSize(32);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setColor(Color.BLUE);
		
		TLEDraw.drawPolarBackdrop(canvas, canvas.getWidth()/2,canvas.getHeight()/2, canvas.getWidth()/2,textPaint);
		textPaint.setTextSize(24);
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.RED);
		LLA mylla = new LLA();
		mylla.setLat(gsLLA[0]);
		mylla.setLon(gsLLA[1]);
		mylla.setAlt(gsLLA[2]);
		GroundStation gsnew = new GroundStation("GS",gsLLA,10);
		for(int i=0;i<myApp.getTotal();i++)
		{
			newTLE= new TLE(myApp.getTitle(i),
					myApp.getTLE1(i),
					myApp.getTLE2(i));
			timer.addSeconds(interval);
			curlla = TLECompute.getPolarAE(newTLE,gsLLA,timer);
			boolean curflag = false;

			double h1 = -1;
			try
			{
				SatelliteTleSGP4 satnew = new SatelliteTleSGP4(myApp.getTitle(i),myApp.getTLE1(i),myApp.getTLE2(i));
				//double[] gsLLA={21.56694,-158.2519,317.9};
				double jdStart = timer.getJulianDate();
				h1 = AER.calculate_AER(gsnew.getLla_deg_m(), satnew.calculateTemePositionFromUT(jdStart) , jdStart)[1] - gsnew.getElevationConst();
				//sat.setShowGroundTrack(false); // if we arn't using the JSatTrak plots midas well turn this off to save CPU time
			}
			catch(Exception e)
			{
				System.out.println("Error First Creating SGP4 Satellite");
				System.exit(1);
			}
			//System.out.println(h1);
			
			if(h1 > 0)
			{
				textPaint.setColor(Color.RED);
				if(i == myApp.counttest)
				{
					curflag = true;
					llas= TLECompute.getPolarLead(newTLE,gsLLA,timer);
					TLEDraw.drawPolarGround(canvas, llas,canvas.getWidth()/2,canvas.getHeight()/2, canvas.getWidth()/2,textPaint);
					
				}
				
				TLEDraw.drawPolarSatellite(canvas, newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2, canvas.getWidth()/2,textPaint,curflag,DisSatName);

			}else
			{
				textPaint.setColor(Color.GRAY);
				if(i == myApp.counttest)
				{
					curflag = true;
					llas= TLECompute.getPolarLead(newTLE,gsLLA,timer);
					TLEDraw.drawPolarGround(canvas, llas,canvas.getWidth()/2,canvas.getHeight()/2, canvas.getWidth()/2,textPaint);
					
				}
				
				TLEDraw.drawPolarSatellite(canvas, newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,textPaint,curflag,DisSatName);
				
			}
		}
		
		textPaint.setColor(Color.GREEN);
		
		TLEDraw.drawPolarSatellite(canvas, "观测站",new LLA(0,0,0),canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,textPaint,false,0);	

			
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		return (Drawable) new BitmapDrawable(getResources(), imgTemp);

	}
	
	
	
	
	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			Intent intent = new Intent();			
			switch (v.getId()) {			
	           case R.id.Buttonleft:
	        	   	   
	        	   myApp.counttest = myApp.counttest-1;
	        	    if(myApp.counttest == -1)	        	   
	        	    	myApp.counttest = myApp.getTotal()-1;	        	    	        	  	        	 
	        	    TextView0.setText("      "+myApp.getTitle(myApp.counttest));
	       		    if(myApp.counttest == -1)
	       		    	myApp.counttest = myApp.getTotal()-1;
	       		    newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     				myApp.getTLE1(myApp.counttest),
	     				myApp.getTLE2(myApp.counttest));
	       		    llas.clear();
	       			//timer.add(Time.HOUR,-12);
	       			llas= TLECompute.getPolarLead(newTLE,gsLLA,timer);
	       			//timer.add(Time.HOUR,12);
	           		break;
	           case R.id.Buttonright:
	        	  
	        	   myApp.counttest = myApp.counttest+1;
	        	    if(myApp.counttest == myApp.getTotal())
	        	    	myApp.counttest = 0;		        	    	        	   	        
	        	    TextView0.setText("      "+myApp.getTitle(myApp.counttest));
	        	    if(myApp.counttest == myApp.getTotal())
	        	    	myApp.counttest = 0;
	       		    newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     				myApp.getTLE1(myApp.counttest),
	     				myApp.getTLE2(myApp.counttest));
	       		    llas.clear();
	       			//timer.add(Time.HOUR,-12);
	       			llas= TLECompute.getPolarLead(newTLE,gsLLA,timer);
	       			//timer.add(Time.HOUR,12);
	           		break;
	           case R.id.ButtonPrefs:
	        	    intent.setClass(CActivity.this,SkySettingActivity.class);
	        	    CActivity.this.startActivity(intent);
					break;  
	           case R.id.Buttonback:
	        	   //Log.e("??????????????","cccccccccccccccccc");
	        	   interval = -600;
	        	   break;
	           case R.id.Buttonnormal:
	        	   timer = new Time();
	        	   interval = 1;
	        	   break;   
	           case R.id.Buttonfast:
	        	   interval = 600;
	        	   break;	
					
			   default:
					break;
			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // 监控/拦截/屏蔽返回键
	//do something
		Intent intent = new Intent();
		intent.setClass(CActivity.this,SettingTabActivity.class);
		CActivity.this.startActivity(intent);
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
//		myApp.getloctioninfo();
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		timer = new Time();
		//timer.add(Time.HOUR,-12);
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
		initgslla();
		llas= null;//TLECompute.getPolarLead(newTLE,gsLLA,timer);
		//System.out.println("CA restart,compute llas");
		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        DisSatName = sharedPreferences.getInt("DisSatName", 0);			
	 super.onResume();
	}
	private void initgslla()
	{
		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
		if(sharedPreferences.getInt("WatchPosNo", 0)==1)
		{
			gsLLA[0] = sharedPreferences.getFloat("WatchLatitude",0);
			gsLLA[1] = sharedPreferences.getFloat("WatchLongitude",0);
			gsLLA[2] = sharedPreferences.getFloat("WatchAltitude",0);
			
		}else
		{
//			gsLLA[0] = myApp.myLatitude;
//			gsLLA[1] = myApp.myLongitude;
//			gsLLA[2] = myApp.myAltitude;

			gsLLA[0] = myApp.getcurrentLatitude();
			gsLLA[1] = myApp.getcurrentLongitude();
			gsLLA[2] = myApp.getcurrentAltitude();
		}
	}
}
