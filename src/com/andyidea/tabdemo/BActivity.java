package com.andyidea.tabdemo;


import com.andyidea.tabdemo.image.*;
import com.andyidea.tabdemo.service.*;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.ArcOptions;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolygonOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
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
import android.os.Message;
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

	private static final String ACTIVITY_TAG="BActivity";
    // 普通折线，点击时改变宽度
    Polyline mPolyline;
    // 多颜色折线，点击时消失
    Polyline mColorfulPolyline;
    // 纹理折线，点击时获取折线上点数及width
    Polyline mTexturePolyline;

    BitmapDescriptor mRedTexture = BitmapDescriptorFactory.fromAsset("icon_road_red_arrow.png");
    BitmapDescriptor mBlueTexture = BitmapDescriptorFactory.fromAsset("icon_road_blue_arrow.png");
    BitmapDescriptor mGreenTexture = BitmapDescriptorFactory.fromAsset("icon_road_green_arrow.png");
	
	private MapView mMapView = null;
	private BaiduMap mBaiduMap;
	private LinkedList<LocationEntity> locationList = new LinkedList<LocationEntity>(); // 存放历史定位结果的链表，最大存放当前结果的前5次定位结果

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
	private Bitmap satellitenoBitmap;
	TLE newTLE = new TLE("北斗M6", 
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
    int MapNo = 0;
    int DisSatName = 0;
    int DisSatCoverage = 0;	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_2);

		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap = mMapView.getMap();
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
//		比例尺            {"10m", "20m", "50m", "100m", "200m", "500m", "1km", "2km", "5km", "10km", "20km", "25km", "50km", "100km", "200km", "500km", "1000km", "2000km"} 
//		Level依次为：20、           19、           18、              17、              16、              15、            14、           13、           12、           11、              10、               9、                 8、                      7、                      6、                 5、                   4、                       3
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.zoomTo(6));//设置缩放级别

		// 界面加载时添加绘制图层
        addCustomElementsDemo();

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
		
		
		// 获取可見区域高度 
		WindowManager manager = getWindowManager();
		window_width = manager.getDefaultDisplay().getWidth();
		window_height = manager.getDefaultDisplay().getHeight();

		dragImageView = (DragImageView) findViewById(R.id.div_main);
		
		Bitmap bmp = BitmapUtil.ReadBitmapById(this, R.drawable.transparent1080,window_width, window_height);
		
		//关于画线的背景的设置
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
		initgslla();
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
        MapNo = sharedPreferences.getInt("MapNo", 0);
        DisSatName = sharedPreferences.getInt("DisSatName", 0);
        DisSatCoverage = sharedPreferences.getInt("DisSatCoverage", 0);
//		if(MapNo == 0)
//			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
//		if(MapNo == 1)
//			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
//		if(MapNo == 2)
//			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);
        if(MapNo == 0){
        	mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
		}
		else if(MapNo == 1){
			mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
		}
		else if(MapNo == 2){
			mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);
		}
		else if(MapNo == 3)
		{
			mMapView.setVisibility(View.VISIBLE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);
		}

		width = imgMarker.getWidth();
		height = imgMarker.getHeight();
		//System.out.println(width+","+height+","+window_width+"," + window_height);
		initDraw();

		dragImageView.setBackgroundDrawable(createDrawable('A',200,200));
		dragImageView.setImageBitmap(bmp);
		dragImageView.setmActivity(this);//注入Activity.
//		dragImageView.setScale(5.0f);
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
				myApp.handlerB.postDelayed(this, 2000);
				if(!didRunFlag){
					    didRunFlag = true;
					    dragImageView.setScale(6.0f);
				}

				addCustomElementsDemo();

//				dragImageView.setScale(5.0f);
				dragImageView.setBackgroundDrawable(createDrawable('A',x++,y++));

				newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
				TextView0.setText("      "+myApp.getTitle(myApp.counttest));
				
				//gsLLA[0] = myApp.myLatitude;
				//gsLLA[1] = myApp.myLongitude;
				//gsLLA[2] = myApp.myAltitude;
				initgslla();
				timer.addSeconds(interval);
				curlla = TLECompute.getMapLLA(newTLE,timer);
				LLA gs = new LLA(gsLLA[0],gsLLA[1],gsLLA[2]);
				//TLEDraw.GetAzimuth(curlla.getLon(), curlla.getLat(), gs.getLon(), gs.getLat());
				
				TextView1.setText(/*"X ="+dragImageView.geteventX()+"//Y ="+dragImageView.geteventY()+newTLE.getSatName().trim()+*/"卫星（经度:"+curlla.getLatStr()+"，"+"纬度:"+curlla.getLonStr()+"，"+"高度:"+curlla.getAltStr()+"）\n"+
						"观测站（经度:"+gs.getLatStr()+"，"+"纬度:"+gs.getLonStr()+"，"+"高度:"+gs.getAltStr()+"）");
			}
		};
		myApp.handlerB.postDelayed(myApp.runnableB, 0);  //开始计数器
//		handler.removeCallbacks(runnable); //停止计数器
	}

    /**
     * 添加点、线、多边形、圆、文字
     */
    public void addCustomElementsDemo() {
        // 添加普通折线绘制
        LatLng p1 = new LatLng(39.97923, 116.357428);
        LatLng p2 = new LatLng(39.94923, 116.397428);
        LatLng p3 = new LatLng(39.97923, 116.437428);
        List<LatLng> points = new ArrayList<LatLng>();
        points.add(p1);
        points.add(p2);
        points.add(p3);
        OverlayOptions ooPolyline = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points);
        mPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline);
        mPolyline.setDottedLine(true);
        
        // 添加多颜色分段的折线绘制
        LatLng p11 = new LatLng(39.965, 116.444);
        LatLng p21 = new LatLng(39.925, 116.494);
        LatLng p31 = new LatLng(39.955, 116.534);
        LatLng p41 = new LatLng(39.905, 116.594);
        LatLng p51 = new LatLng(39.965, 116.644);
        List<LatLng> points1 = new ArrayList<LatLng>();
        points1.add(p11);
        points1.add(p21);
        points1.add(p31);
        points1.add(p41);
        points1.add(p51);
        List<Integer> colorValue = new ArrayList<Integer>();
        colorValue.add(0xAAFF0000);
        colorValue.add(0xAA00FF00);
        colorValue.add(0xAA0000FF);
        OverlayOptions ooPolyline1 = new PolylineOptions().width(10)
                .color(0xAAFF0000).points(points1).colorsValues(colorValue);
        mColorfulPolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline1);
        
        // 添加多纹理分段的折线绘制
        LatLng p111 = new LatLng(39.865, 116.444);
        LatLng p211 = new LatLng(39.825, 116.494);
        LatLng p311 = new LatLng(39.855, 116.534);
        LatLng p411 = new LatLng(39.805, 116.594);
        List<LatLng> points11 = new ArrayList<LatLng>();
        points11.add(p111);
        points11.add(p211);
        points11.add(p311);
        points11.add(p411);
        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
        textureList.add(mRedTexture);
        textureList.add(mBlueTexture);
        textureList.add(mGreenTexture);
        List<Integer> textureIndexs = new ArrayList<Integer>();
        textureIndexs.add(0);
        textureIndexs.add(1);
        textureIndexs.add(2);
        OverlayOptions ooPolyline11 = new PolylineOptions().width(20)
                .points(points11).dottedLine(true).customTextureList(textureList).textureIndex(textureIndexs);
        mTexturePolyline = (Polyline) mBaiduMap.addOverlay(ooPolyline11);
        
        // 添加弧线
        OverlayOptions ooArc = new ArcOptions().color(0xAA00FF00).width(4)
                .points(p1, p2, p3);
        mBaiduMap.addOverlay(ooArc);

        // 添加圆
        LatLng llCircle = new LatLng(39.90923, 116.447428);
        OverlayOptions ooCircle = new CircleOptions().fillColor(0x000000FF)
                .center(llCircle).stroke(new Stroke(5, 0xAA000000))
                .radius(1400);
        mBaiduMap.addOverlay(ooCircle);

        LatLng llDot = new LatLng(39.98923, 116.397428);
        OverlayOptions ooDot = new DotOptions().center(llDot).radius(6)
                .color(0xFF0000FF);
        mBaiduMap.addOverlay(ooDot);

        // 添加多边形
        LatLng pt1 = new LatLng(39.93923, 116.357428);
        LatLng pt2 = new LatLng(39.91923, 116.327428);
        LatLng pt3 = new LatLng(39.89923, 116.347428);
        LatLng pt4 = new LatLng(39.89923, 116.367428);
        LatLng pt5 = new LatLng(39.91923, 116.387428);
        List<LatLng> pts = new ArrayList<LatLng>();
        pts.add(pt1);
        pts.add(pt2);
        pts.add(pt3);
        pts.add(pt4);
        pts.add(pt5);
        OverlayOptions ooPolygon = new PolygonOptions().points(pts)
                .stroke(new Stroke(5, 0xAA00FF00)).fillColor(0xAAFFFF00);
        mBaiduMap.addOverlay(ooPolygon);

        // 添加文字
        LatLng llText = new LatLng(39.86923, 116.397428);
        OverlayOptions ooText = new TextOptions().bgColor(0xAAFFFF00)
                .fontSize(24).fontColor(0xFFFF00FF).text("百度地图SDK").rotate(-30)
                .position(llText);
        mBaiduMap.addOverlay(ooText);
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
		LLA mylla = new LLA();
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
		initgslla();
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
					if(DisSatCoverage == 0)
					{
						TLEDraw.drawFootprint(canvas,footprint,sat,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);		
					}
				}
				TLEDraw.drawMapSatellite(canvas, newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,curflag,DisSatName);
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
					if(DisSatCoverage == 0)
					{
						textPaint.setColor(Color.RED);
						TLEDraw.drawFootprint(canvas,footprint,sat,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint);		
					}
				}
				textPaint.setColor(Color.GRAY);
				TLEDraw.drawMapSatellite(canvas, newTLE.getSatName(),curlla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,curflag,DisSatName);

			}
		}
		
		textPaint.setColor(Color.GREEN);
		TLEDraw.drawMapSatellite(canvas, "观测点",mylla,canvas.getWidth()/2,canvas.getHeight()/2,canvas.getWidth()/2,canvas.getHeight()/2,textPaint,false,0);	

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
	
	
	@Override
	protected void onResume() {
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
		mBaiduMap.clear();
//		myApp.getloctioninfo();

		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
		initgslla();
		timer = new Time();

		LatLng point = new LatLng(myApp.getcurrentLatitude(), myApp.getcurrentLongitude());
		// 构建Marker图标
		BitmapDescriptor bitmap = null;
		bitmap = BitmapDescriptorFactory.fromResource(R.drawable.huaji); // 非推算结果
		// 构建MarkerOption，用于在地图上添加Marker
		OverlayOptions option = new MarkerOptions().position(point).icon(bitmap);
		// 在地图上添加Marker，并显示
		mBaiduMap.addOverlay(option);
//		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));


		//timer.add(Time.HOUR,-12);
		
		llas= null;//TLECompute.getMapLead(newTLE,timer);
		lags= null;//TLECompute.getMapLag(newTLE,timer);

		SharedPreferences sharedPreferences = getSharedPreferences("test", 0);
        MapNo = sharedPreferences.getInt("MapNo", 0);
        DisSatName = sharedPreferences.getInt("DisSatName", 0);
        DisSatCoverage = sharedPreferences.getInt("DisSatCoverage", 0);
        if(MapNo == 0){
        	mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background1);
		}
		else if(MapNo == 1){
			mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background2);
		}
		else if(MapNo == 2){
			mMapView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);
		}
		else if(MapNo == 3)
		{
			mMapView.setVisibility(View.VISIBLE);
//			dragImageView.setVisibility(View.GONE);
			imgMarker = BitmapFactory.decodeResource(getResources(), R.drawable.background3);
		}

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
	
//--------------------------------------------------------//
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
//		WriteLog.getInstance().close();
		mMapView.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();

	}
	
	@Override
	protected void onStart() {
		super.onStart();
		LatLng point = new LatLng(myApp.myLatitude, myApp.myLongitude);
		mBaiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(point));//以point点为中心显示
	}

	/**
	 * 封装定位结果和时间的实体类
	 * 
	 * @author baidu
	 *
	 */
	class LocationEntity {
		BDLocation location;
		long time;
	}
}
