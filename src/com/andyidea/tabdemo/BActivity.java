package com.andyidea.tabdemo;


import com.andyidea.tabdemo.image.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import name.gano.astro.time.Time;
import satellite.tle.image.TLEDraw;
import satellite.tle.utilities.LLA;
import satellite.tle.utilities.TLE;
import satellite.tle.utilities.TLECompute;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class BActivity extends Activity{
	private Button Buttonleft,Buttonright,ButtonPrefs;
	private Button Buttonback,Buttonnormal,Buttonfast;
	
	private TextView TextView0,TextView1;
	
	private LocationApplication myApp;
	private int factorOneInt = 0;
	
	//图片的相关变量
	private int window_width, window_height;// 控件宽度
	private DragImageView dragImageView;// 自定义控件
	private int state_height;// 状态栏的高度
	private ViewTreeObserver viewTreeObserver;
	
	//画线方面的初始化
	ImageView img;
	private Bitmap imgMarker;
	private int width,height;  //图片的高度和宽带
	private Bitmap imgTemp;    //临时标记图
	


	//定时器的变量初始化
//	public  Handler handlerB;
//	public  Runnable runnableB;
	private int x,y;
	
	public Canvas canvas;
	public Paint textPaint;
	
	private long exitTime = 0;
	

	private Bitmap personBitmap;
	private Bitmap satelliteBitmap;	
	TLE newTLE = new TLE("北斗M6", 
			"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
	double[] gsLLA={21.56694,-158.2519,317.9};
	private List<LLA> llas;
	LLA curlla;
	Time timer ;
	int interval = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_2);
		
		((LocationApplication)getApplication()).initLocation();//初始化百度地图SDK
					
		Buttonleft = (Button)findViewById(R.id.Buttonleft);
		Buttonleft.setText("<<");
		Buttonleft.setOnClickListener(new ButtonOnClickListener());
		
		TextView0 = (TextView)findViewById(R.id.TextViewB0);	
		TextView1 = (TextView)findViewById(R.id.TextViewB1);	
		
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
		Intent intent = getIntent();
		
		String factorOneStr = intent.getStringExtra("one");
		
		if( factorOneStr != null){
			factorOneInt = Integer.parseInt(factorOneStr);
			myApp.counttest = factorOneInt;
		}				

		TextView0.setTextSize(10);
		TextView1.setTextSize(10);
		TextView0.setGravity(Gravity.CENTER);
		TextView0.setText("      "+myApp.getTitle(myApp.counttest));
//		Log.e("??????????????",myApp.counttest+"");
//		Log.e("??????????????",myApp.getTotal()+"");
		
		
		// 获取可見区域高度 
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
		final Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.transparent500,window_width, window_height);
		

				
		//关于画线的背景的设置
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		timer = new Time();
		
		//timer.add(Time.HOUR,-12);
		llas= TLECompute.getMapLead(newTLE,timer);
		//timer.add(Time.HOUR,12);
		curlla = TLECompute.getMapLLA(newTLE,timer);
		personBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.person_mark);
		satelliteBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.satellite_mark);

		
		
		imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.map);
		width = imgMarker.getWidth();
		height = imgMarker.getHeight();
		
		
		initDraw();
		
		dragImageView.setBackgroundDrawable(createDrawable('A',200,200));
		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//注入Activity.
					
		// 测量状态栏高度 
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// 获取状况栏高度
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);							
							dragImageView.setScreen_W(window_width);
						}

					}
				});

	
		
		//定时器
		myApp.handlerB = new Handler();
		myApp.runnableB = new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {				
				myApp.handlerB.postDelayed(this, 1000);		
				
				dragImageView.setBackgroundDrawable(createDrawable('A',x++,y++));
				
				newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
				TextView0.setGravity(Gravity.CENTER);
				TextView0.setText("      "+myApp.getTitle(myApp.counttest));
				
				gsLLA[0] = myApp.myLatitude;
				gsLLA[1] = myApp.myLongitude;
				gsLLA[2] = myApp.myAltitude;
				timer.addSeconds(interval);
				curlla = TLECompute.getMapLLA(newTLE,timer);
				LLA gs = new LLA(gsLLA[0],gsLLA[1],gsLLA[2]);
				TextView1.setText("卫星　经度:"+curlla.getLatStr()+"，"+"纬度:"+curlla.getLonStr()+"，"+"高度:"+curlla.getAltStr()+"\n"+
						"自己　经度:"+gs.getLatStr()+"，"+"纬度:"+gs.getLonStr()+"，"+"高度:"+gs.getAltStr());
			}
		};
		myApp.handlerB.postDelayed(myApp.runnableB, 0);  //开始计数器
//		handler.removeCallbacks(runnable); //停止计数器
		
 
	}
	

	private void initDraw(){
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 0, width, height);
		canvas.drawBitmap(imgMarker, src, dst, paint);
		
		/*textPaint = new Paint();
		
		textPaint.setSubpixelText(true);
		textPaint.setAntiAlias(true);
		textPaint.setFilterBitmap(true);
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(24);
		textPaint.setTextAlign(Align.CENTER);
		textPaint.setStyle(Style.STROKE);*/
		textPaint = new Paint();
		
		textPaint.setSubpixelText(true);
		textPaint.setAntiAlias(true);
		textPaint.setFilterBitmap(true);
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(16);
		textPaint.setTextAlign(Align.CENTER);
	}
	
	private Drawable createDrawable(char letter,int x,int y) {
/*
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // 建立画笔
		paint.setDither(true);
		paint.setFilterBitmap(true);
		Rect src = new Rect(0, 0, width, height);
		Rect dst = new Rect(0, 0, width, height);
		canvas.drawBitmap(imgMarker, src, dst, paint);
		

		Paint textPaint = new Paint();
		
		textPaint.setSubpixelText(true);
		textPaint.setAntiAlias(true);
		textPaint.setFilterBitmap(true);
		textPaint.setColor(Color.RED);
		textPaint.setTextSize(24);
		textPaint.setTextAlign(Align.CENTER);
*/
		

		initDraw();
		TLEDraw.drawMapGround(canvas,llas,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);

		TLEDraw.drawMapSatellite(canvas, satelliteBitmap,newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);	
		LLA mylla = new LLA();
		mylla.setLat(gsLLA[0]);
		mylla.setLon(gsLLA[1]);
		mylla.setAlt(gsLLA[2]);
		TLEDraw.drawMapSatellite(canvas, personBitmap,"自己",mylla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);	

		//canvas.drawCircle(x, y, 50, textPaint);
			
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
	        	   	
	        	    TextView0.setText(" "+myApp.getTitle(myApp.counttest));
	        	    
        	    	newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     			myApp.getTLE1(myApp.counttest),
	     			myApp.getTLE2(myApp.counttest));
        	    	
	       		    llas.clear();
	       			llas= TLECompute.getMapLead(newTLE,timer);

	        	    
	           		break;
	           case R.id.Buttonright:
	        	  
	        	   	myApp.counttest = myApp.counttest + 1;
	        	    if(myApp.counttest == myApp.getTotal())
	        	    	myApp.counttest = 0;		        	    	        	          
	        	    TextView0.setText(" "+myApp.getTitle(myApp.counttest));

	       		    newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     				myApp.getTLE1(myApp.counttest),
	     				myApp.getTLE2(myApp.counttest));
	       		    llas.clear();
	       			llas= TLECompute.getMapLead(newTLE,timer);

	           		break;
	           case R.id.ButtonPrefs:
	        	 //  intent.setClass(BActivity.this,MapSettingActivity.class);
	        	 //  BActivity.this.startActivity(intent);
					break;  
	           case R.id.Buttonback:
	        	   //Log.e("??????????????","bbbbbbbbbbbbbbb");
	        	   interval = -600;
	        	   break;
	           case R.id.Buttonnormal:
//	        	   handler.removeCallbacks(runnable); //停止计数器
	        	   timer = new Time();
	        	   interval =1;
	        	   break;   
	           case R.id.Buttonfast:
	        	   interval = 600;
	        	   break;
	        	   
	        	   
			   default:
					break;
			}
		}
	}
	
	/**
	 * 读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap ReadBitmapById(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // 监控/拦截/屏蔽返回键
	//do something
		Intent intent = new Intent();
		intent.setClass(BActivity.this,SettingTabActivity.class);
		BActivity.this.startActivity(intent);
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
	
	/*
	@Override
	protected void onResume() {
	 //设置为横屏
	 if(getRequestedOrientation()!=ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
	  setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	 }
	 super.onResume();
	}
	*/
	
}
