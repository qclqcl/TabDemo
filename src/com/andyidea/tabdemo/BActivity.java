package com.andyidea.tabdemo;


import com.andyidea.tabdemo.image.*;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import name.gano.astro.AER;
import name.gano.astro.time.Time;
import satellite.tle.image.TLEDraw;
import satellite.tle.objects.GroundStation;
import satellite.tle.objects.SatelliteTleSGP4;
import satellite.tle.utilities.LLA;
import satellite.tle.utilities.TLE;
import satellite.tle.utilities.TLECompute;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Rect;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


public class BActivity extends Activity{
	private Button Buttonleft,Buttonright,ButtonPrefs;
	private Button Buttonback,Buttonnormal,Buttonfast;
	
	private TextView TextView0,TextView1;
	
	private LocationApplication myApp;
	private int factorOneInt = 0;
	
	//ͼƬ����ر���
	private int window_width, window_height;// �ؼ����
	private DragImageView dragImageView;// �Զ���ؼ�
	private int state_height;// ״̬���ĸ߶�
	private ViewTreeObserver viewTreeObserver;
	
	//���߷���ĳ�ʼ��
	ImageView img;
	private Bitmap imgMarker;
	private int width,height;  //ͼƬ�ĸ߶ȺͿ��
	private Bitmap imgTemp;    //��ʱ���ͼ
	
	//��ʱ���ı�����ʼ��
//	public  Handler handlerB;
//	public  Runnable runnableB;
	private int x,y;
	
	public Canvas canvas;
	public Paint textPaint;
	
	private long exitTime = 0;

	private Bitmap personBitmap;
	private Bitmap satelliteBitmap;
	private Bitmap satellitenoBitmap;
	TLE newTLE = new TLE("����M6", 
			"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
	double[] gsLLA={21.56694,-158.2519,317.9};
	private List<LLA> llas;
	private List<LLA> lags;
	private List<LLA> footprint;
	LLA curlla;
	Time timer ;
	int interval = 1;
	boolean didRunFlag = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_2);
		
		((LocationApplication)getApplication()).initLocation();//��ʼ���ٶȵ�ͼSDK
					
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
		TextView0.setText("      "+myApp.getTitle(myApp.counttest));
//		Log.e("??????????????",myApp.counttest+"");
//		Log.e("??????????????",myApp.getTotal()+"");
		
		
		// ��ȡ��Ҋ����߶� 
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
		
		Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.transparent1024,window_width, window_height);
		
		//���ڻ��ߵı���������
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		gsLLA[0] = myApp.myLatitude;
		gsLLA[1] = myApp.myLongitude;
		gsLLA[2] = myApp.myAltitude;
		timer = new Time();
		
		//timer.add(Time.HOUR,-12);
		//llas= TLECompute.getMapLead(newTLE,timer);
		//lags= TLECompute.getMapLag(newTLE,timer);
		/*
		lags= TLECompute.getMapLag(newTLE,timer);
		System.out.println("llas:"+llas.size());
		for(LLA lla:llas)
		{
			System.out.println(lla.getLon()+","+lla.getLat()+",");
		}
		System.out.println("lags:"+lags.size());
		for(LLA lla:lags)
		{
			System.out.println(lla.getLon()+","+lla.getLat()+",");
		}*/
		//timer.add(Time.HOUR,12);
		curlla = TLECompute.getMapLLA(newTLE,timer);
		//footprint = TLECompute.getFootPrintLatLonList(curlla, 64);
		//personBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.person_mark);
		//satelliteBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.satellite_mark);
		//satellitenoBitmap = BitmapFactory.decodeResource(getResources(),	R.drawable.satellite_no);

//		imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        int MapNo = sharedPreferences.getInt("MapNo", 0);
		if(MapNo == 0)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
		if(MapNo == 1)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
		if(MapNo == 2)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);

		width = imgMarker.getWidth();
		height = imgMarker.getHeight();
		//System.out.println(width+","+height+","+window_width+"," + window_height);
		initDraw();

		dragImageView.setBackgroundDrawable(createDrawable('A',200,200));
		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//ע��Activity.
//		dragImageView.setScale(5.0f);
		// ����״̬���߶� 
		viewTreeObserver = dragImageView.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {
						if (state_height == 0) {
							// ��ȡ״�����߶�
							Rect frame = new Rect();
							getWindow().getDecorView()
									.getWindowVisibleDisplayFrame(frame);
							state_height = frame.top;
							dragImageView.setScreen_H(window_height-state_height);							
							dragImageView.setScreen_W(window_width);
						}

					}
				});

		//��ʱ��
		myApp.handlerB = new Handler();
		myApp.runnableB = new Runnable() {
			@SuppressWarnings("deprecation")
			@Override
			public void run() {
				myApp.handlerB.postDelayed(this, 2000);
				if(!didRunFlag){
					    didRunFlag = true;
					    dragImageView.setScale(5.0f);
				}
//				dragImageView.setScale(5.0f);
				dragImageView.setBackgroundDrawable(createDrawable('A',x++,y++));

				newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
				TextView0.setText("      "+myApp.getTitle(myApp.counttest));
				
				gsLLA[0] = myApp.myLatitude;
				gsLLA[1] = myApp.myLongitude;
				gsLLA[2] = myApp.myAltitude;
				timer.addSeconds(interval);
				curlla = TLECompute.getMapLLA(newTLE,timer);
				LLA gs = new LLA(gsLLA[0],gsLLA[1],gsLLA[2]);
				//TLEDraw.GetAzimuth(curlla.getLon(), curlla.getLat(), gs.getLon(), gs.getLat());
				
				TextView1.setText(/*"X ="+dragImageView.geteventX()+"//Y ="+dragImageView.geteventY()+newTLE.getSatName().trim()+*/"���ǣ�����:"+curlla.getLatStr()+"��"+"γ��:"+curlla.getLonStr()+"��"+"�߶�:"+curlla.getAltStr()+"��\n"+
						"�Լ�������:"+gs.getLatStr()+"��"+"γ��:"+gs.getLonStr()+"��"+"�߶�:"+gs.getAltStr()+"��");
			}
		};
		myApp.handlerB.postDelayed(myApp.runnableB, 0);  //��ʼ������
//		handler.removeCallbacks(runnable); //ֹͣ������
		
 
	}
	

	private void initDraw(){
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // ��������
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
		textPaint.setTextSize(12);
		textPaint.setTextAlign(Align.CENTER);
	}
	
	private Drawable createDrawable(char letter,int x,int y) {
/*
		imgTemp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(imgTemp);
		Paint paint = new Paint(); // ��������
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
		LLA mylla = new LLA();
		gsLLA[0] = myApp.myLatitude;
		gsLLA[1] = myApp.myLongitude;
		gsLLA[2] = myApp.myAltitude;
		mylla.setLat(gsLLA[0]);
		mylla.setLon(gsLLA[1]);
		mylla.setAlt(gsLLA[2]);
		GroundStation gsnew = new GroundStation("GS",gsLLA,10);
		LLA sat = new LLA();
		for(int i=0;i<myApp.getTotal();i++)
		{
			newTLE= new TLE(myApp.getTitle(i),
					myApp.getTLE1(i),
					myApp.getTLE2(i));
			timer.addSeconds(interval);
			curlla = TLECompute.getMapLLA(newTLE,timer);
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
			
			
			if(h1 > 0)
			{
				textPaint.setColor(Color.RED);
				if(i == myApp.counttest)
				{
					//textPaint.setAlpha(255);
					curflag = true;
					footprint = TLECompute.getFootPrintLatLonList(curlla, 64);
					sat = curlla;
					//textPaint.setColor(Color.RED);
					llas= TLECompute.getMapLead(newTLE,timer);
					lags= TLECompute.getMapLag(newTLE,timer);
					
					TLEDraw.drawMapGround(canvas,llas,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);
					TLEDraw.drawMapGround(canvas,lags,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);
					TLEDraw.drawFootprint(canvas,footprint,sat,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);		
				}
				
				TLEDraw.drawMapSatellite(canvas, satelliteBitmap,newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,curflag);
			}else
			{
				
				
				if(i == myApp.counttest)
				{
					//textPaint.setAlpha(255);
					curflag = true;
					footprint = TLECompute.getFootPrintLatLonList(curlla, 64);
					sat = curlla;
					//textPaint.setColor(Color.RED);
					llas= TLECompute.getMapLead(newTLE,timer);
					lags= TLECompute.getMapLag(newTLE,timer);
					textPaint.setColor(Color.GRAY);
					TLEDraw.drawMapGround(canvas,llas,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);
					TLEDraw.drawMapGround(canvas,lags,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);
					textPaint.setColor(Color.RED);
					TLEDraw.drawFootprint(canvas,footprint,sat,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);		
				}
				textPaint.setColor(Color.GRAY);
				TLEDraw.drawMapSatellite(canvas, satellitenoBitmap,newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,curflag);

			}
		}
		
		textPaint.setColor(Color.GREEN);
		TLEDraw.drawMapSatellite(canvas, personBitmap,"�Լ�",mylla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,false);	

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
	        	   	TextView0.setText("      "+myApp.getTitle(myApp.counttest));
	        	   	
        	    	newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     			myApp.getTLE1(myApp.counttest),
	     			myApp.getTLE2(myApp.counttest));
        	    	
	       		    llas.clear();
	       			llas= TLECompute.getMapLead(newTLE,timer);
	       			lags.clear();
	       			lags= TLECompute.getMapLag(newTLE,timer);
	       			footprint.clear();
	       			footprint = TLECompute.getFootPrintLatLonList(curlla, 64);
	           		break;
	           case R.id.Buttonright:
	        	  
	        	   	myApp.counttest = myApp.counttest + 1;
	        	    if(myApp.counttest == myApp.getTotal())
	        	    	myApp.counttest = 0;
	        	    TextView0.setText("      "+myApp.getTitle(myApp.counttest));

	       		    newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     				myApp.getTLE1(myApp.counttest),
	     				myApp.getTLE2(myApp.counttest));
	       		    llas.clear();
	       			llas= TLECompute.getMapLead(newTLE,timer);
	       			lags.clear();
	       			lags= TLECompute.getMapLag(newTLE,timer);
	       			footprint.clear();
	       			footprint = TLECompute.getFootPrintLatLonList(curlla, 64);
	           		break;
	           case R.id.ButtonPrefs:
	        	   intent.setClass(BActivity.this,MapSettingActivity.class);
	        	   BActivity.this.startActivity(intent);
					break;  
	           case R.id.Buttonback:
	        	   interval = -600;
	        	   break;
	           case R.id.Buttonnormal:
//	        	   handler.removeCallbacks(runnable); //ֹͣ������
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
	 * ��ȡ������Դ��ͼƬ
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
		// ��ȡ��ԴͼƬ
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	if (keyCode == KeyEvent.KEYCODE_MENU) { // ���/����/���η��ؼ�
	//do something
		Intent intent = new Intent();
		intent.setClass(BActivity.this,SettingTabActivity.class);
		BActivity.this.startActivity(intent);
	}
	
	if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){   
        if((System.currentTimeMillis()-exitTime) > 2000){  
            Toast.makeText(getApplicationContext(), "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();                                
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
	 //����Ϊ����
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		gsLLA[0] = myApp.myLatitude;
		gsLLA[1] = myApp.myLongitude;
		gsLLA[2] = myApp.myAltitude;
		timer = new Time();
		
		//timer.add(Time.HOUR,-12);
		
		llas= null;//TLECompute.getMapLead(newTLE,timer);
		lags= null;//TLECompute.getMapLag(newTLE,timer);

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        int MapNo = sharedPreferences.getInt("MapNo", 0);

		if(MapNo == 0)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
		if(MapNo == 1)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
		if(MapNo == 2)
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);

	 super.onResume();
	}
}
