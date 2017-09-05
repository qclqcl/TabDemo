package com.andyidea.tabdemo;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.andyidea.tabdemo.data.Data;
import name.gano.astro.time.Time;
import satellite.tle.utilities.TLE;
import satellite.tle.utilities.TLECompute;
import satellite.tle.utilities.Trackdata;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;

public class D2Activity extends Activity{
	
	private ListView Pass_listview;
	
	private Button Buttonleft,Buttonright,ButtonPrefs;	
	private TextView TextView0;
//	private TextView TextView1;
	
	private long exitTime = 0;
	private List<Data> dataList = new ArrayList<Data>();
	private HightKeywordsAdapter mAdapter;
	
	private LocationApplication myApp;
	private int factorOneInt = 0;
	private TLE newTLE = new TLE("北斗M6", 
			"1 38775U 12050B   16187.42076194  .00000033  00000-0  00000+0 0  9999",
			"2 38775  54.8355 184.9940 0021924 230.5817 240.7757  1.86233165 25993");	
	double[] gsLLA={40,116.2,10};
	double gsElevation =10.0;
	Time timer;
	Trackdata trackdata;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.fragment_4_2);

		Buttonleft = (Button)findViewById(R.id.Buttonleft);
		Buttonleft.setText("<<");
		Buttonleft.setOnClickListener(new ButtonOnClickListener());
		
		TextView0 = (TextView)findViewById(R.id.TextView0);
//		TextView1 = (TextView)findViewById(R.id.TextViewD1);
		
		Buttonright = (Button)findViewById(R.id.Buttonright);
		Buttonright.setOnClickListener(new ButtonOnClickListener());
		
		ButtonPrefs = (Button)findViewById(R.id.ButtonPrefs);
		ButtonPrefs.setOnClickListener(new ButtonOnClickListener());
		
		Pass_listview = (ListView)findViewById(R.id.pass_listview);
		
		myApp = (LocationApplication)getApplication();
		Intent intent = getIntent();
		
		String factorOneStr = intent.getStringExtra("one");
		
		if( factorOneStr != null){
			factorOneInt = Integer.parseInt(factorOneStr);
			myApp.counttest = factorOneInt;
		}
		
		TextView0.setTextSize(10);
		TextView0.setGravity(Gravity.CENTER);
//		TextView1.setTextSize(10);	
		TextView0.setText(myApp.getTitle(myApp.counttest));
//		TextView1.setText("Latitude: "+myApp.myLatitude+"     "+"Longtitude: "+myApp.myLongitude+"     "+"Altitude: "+myApp.myAltitude);
		
		newTLE= new TLE(myApp.getTitle(myApp.counttest),
				myApp.getTLE1(myApp.counttest),
				myApp.getTLE2(myApp.counttest));
		timer = new Time();
		//gsLLA[0] = myApp.myLatitude;
		//gsLLA[1] = myApp.myLongitude;
		//gsLLA[2] = myApp.myAltitude;
//		final List<Trackdata> trackdatas = TLECompute.runPassPrediction(newTLE,gsLLA,timer,10,60);

		//定时器
		myApp.handlerD = new Handler();
		myApp.runnableD = new Runnable() {
			@Override
			public void run() {				
				myApp.handlerD.postDelayed(this, 1000);
					
				TextView0.setText(myApp.getTitle(myApp.counttest));
				TextView0.setGravity(Gravity.CENTER);												
//				TextView1.setText("Latitude: "+myApp.myLatitude+"   "+"Longtitude: "+myApp.myLongitude+"   "+"Altitude: "+myApp.myAltitude+"\\角度"+myApp.getAngle()+"\\时间"+myApp.getDelay());			
				myApp.getAngle();
				myApp.getDelay();
							
				gsLLA[0] = myApp.myLatitude;
				gsLLA[1] = myApp.myLongitude;
				gsLLA[2] = myApp.myAltitude;
							
				newTLE= new TLE(myApp.getTitle(myApp.counttest),
						myApp.getTLE1(myApp.counttest),
						myApp.getTLE2(myApp.counttest));
				gsElevation = myApp.getAngle();
				//List<Trackdata> trackdatas = TLECompute.runPassPrediction(newTLE,gsLLA,gsElevation,timer,10,60);
				//dataList.clear();
			    /*for(Trackdata trackdata:trackdatas)
			    {
			    	String name = "第"+Integer.toString(trackdata.getPasscount())+"次"+"　时长:"+trackdata.getDurationStr();
			    	String en = "起时刻:"+trackdata.getRisetimeStr()+"　方位角:"+trackdata.getRiseazimuthStr()+"\n"
			    			+"落时刻:"+trackdata.getSettimeStr()+"　方位角:"+trackdata.getSetazimuthStr();
		    		Data data = new Data(name,en);		
					dataList.add(data);	
			    }*/
			}
		};
		myApp.handlerD.postDelayed(myApp.runnableD, 0);	

	}
	
	private void setListener() {

		mAdapter = new HightKeywordsAdapter();

		Pass_listview.setAdapter(mAdapter);
		Pass_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				
				Toast.makeText(getApplicationContext(), "click " + position,
						Toast.LENGTH_SHORT).show();

				mAdapter.setSelectItem(position); // 记录当前选中的item
//				myApp.counttest_passforecast = position;
				mAdapter.notifyDataSetInvalidated();	//更新UI界面
			}
		});
//		mAdapter.setSelectItem(myApp.counttest_passforecast); // 记录当前选中的item

	}
	
	/**
	 * Customize Adapter, so that realize highlighted keywords
	 */

	private class HightKeywordsAdapter extends BaseAdapter {

		private int selectItem = -1;

		public void setSelectItem(int selectItem) {
			this.selectItem = selectItem;
		}

		@Override
		public int getCount() {

			return dataList.size();
		}

		@Override
		public Object getItem(int position) {

			return dataList.get(position);
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View view;
			ViewHolder holder;

			if (convertView == null) {

				holder = new ViewHolder();
				view = View.inflate(getApplicationContext(),
						R.layout.item2, null);

				holder.pass_dur = (TextView) view.findViewById(R.id.pass_dur);
				holder.pass_time = (TextView) view.findViewById(R.id.pass_time);
				view.setTag(holder);
			} else {
				view = convertView;
				holder = (ViewHolder) view.getTag();
			}

			if (position == selectItem) {	//选中状态 高亮
				view.setBackgroundColor(Color.YELLOW);
				holder.pass_dur.setTextColor(Color.BLACK);
				holder.pass_time.setTextColor(Color.BLACK);
				//设置risetimecount
				myApp.setrisetimecount(position);
				
			} else {	//正常状态
				view.setBackgroundColor(Color.BLACK);
				holder.pass_dur.setTextColor(Color.WHITE);
				holder.pass_time.setTextColor(Color.WHITE);
			}

			Data data = dataList.get(position);
			holder.pass_dur.setText(data.getName());
			holder.pass_time.setText(data.getEn());
						
			return view;
		}
	}

	static class ViewHolder {
		public TextView pass_dur;
		public TextView pass_time;
		public CheckBox checked;

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
		       		    
		       		onResume();
	       		    
	           		break;
	           case R.id.Buttonright:
	        	    myApp.counttest = myApp.counttest + 1;
		        	if(myApp.counttest == myApp.getTotal())
		        		myApp.counttest = 0;
		        	
		            TextView0.setText(" "+myApp.getTitle(myApp.counttest));
		            
	       		    newTLE= new TLE(myApp.getTitle(myApp.counttest),
	     				myApp.getTLE1(myApp.counttest),
	     				myApp.getTLE2(myApp.counttest));	
	       		    
	       		    onResume();
	       		    
	           		break;
/*	           		
	           case R.id.ButtonPrefs:
	        	   	intent.setClass(DActivity.this,PassSettingActivity.class);
	        	   	DActivity.this.startActivity(intent);
	        	   
	        	   	if ( myApp.handlerD != null ){
						myApp.handlerD.removeCallbacks(myApp.runnableD); //停止计数器
					}
	        	   
					break;   
*/					 	   			        	   
			   default:
					break;
			}
		}
	}

	@Override
	protected void onResume() {
		int risetimecounter = 0;
    	int settimecounter = 0;
    	int riseazimuthcounter = 0;
    	int setazimuthcounter = 0;
		
		List<Trackdata> trackdatas = TLECompute.runPassPrediction(newTLE,gsLLA,gsElevation,timer,10,60);
		dataList.clear();
	    for(Trackdata trackdata:trackdatas)
	    {
	    	String name = "第"+Integer.toString(trackdata.getPasscount())+"次"+"　时长:"+trackdata.getDurationStr();
	    	String en = "起　时刻:"+trackdata.getRisetimeStr()+"，方位角:"+trackdata.getRiseazimuthStr()+"\n"
	    			+"落　时刻:"+trackdata.getSettimeStr()+"，方位角:"+trackdata.getSetazimuthStr();
    		Data data = new Data(name,en);		
			dataList.add(data);	
			
			myApp.setrisetime(trackdata.getRisetimeStr(),risetimecounter++);
			myApp.setsettime(trackdata.getSettimeStr(),settimecounter++);
			myApp.setriseazimuth(trackdata.getRiseazimuthStr(),riseazimuthcounter++);
			myApp.setsetazimuth(trackdata.getSetazimuthStr(),setazimuthcounter++);
	    }	              
        setListener();	
	 	
	 super.onResume();
	}
	
}
