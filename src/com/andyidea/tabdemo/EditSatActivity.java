package com.andyidea.tabdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.andyidea.tabdemo.A3Activity.ButtonOnClickListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class EditSatActivity extends Activity {

	private ImageButton img_btn;
	private Button submit_btn;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	public static final String url = "http://app.api.fx.yijia.com/faxing/json_user.php?action=user_file_upload";//要上传的地址测试用
	public String img_src;
	
	private EditText Sat_namecn,Sat_name,Sat_satno,Sat_typename,Sat_valid,Sat_weight,Sat_launch,Sat_launchtime,Sat_tle1,Sat_tle2,Sat_info;
	String result;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_edit_sat);
		
		init();

	}

	private void init() {
		img_btn = (ImageButton) findViewById(R.id.img_btn);
		img_btn.setOnClickListener(new ButtonOnClickListener());
		
		submit_btn = (Button) findViewById(R.id.submit);
		submit_btn.setOnClickListener(new ButtonOnClickListener());
		
		Sat_namecn = (EditText)findViewById(R.id.sat_namecn);
		Sat_name = (EditText)findViewById(R.id.sat_name);
		Sat_satno = (EditText)findViewById(R.id.sat_satno);
		Sat_typename = (EditText)findViewById(R.id.sat_typename);
		Sat_valid = (EditText)findViewById(R.id.sat_valid);
		Sat_weight = (EditText)findViewById(R.id.sat_weight);
		Sat_launch = (EditText)findViewById(R.id.sat_launch);
		Sat_launchtime = (EditText)findViewById(R.id.sat_launchtime);
		Sat_tle1 = (EditText)findViewById(R.id.sat_tle1);
		Sat_tle2 = (EditText)findViewById(R.id.sat_tle2);
		Sat_info = (EditText)findViewById(R.id.sat_info);
		
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {				          	           		
	           case R.id.img_btn: 
	        	   showDialog();
					break;
				
	           case R.id.submit: 
	        	   if(img_src == null){
						Toast.makeText(getApplicationContext(), "请选择一张图片 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_namecn.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星名称 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_name.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星英文名称 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_satno.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星编号 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_typename.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星类型",Toast.LENGTH_SHORT).show();
					}else if(Sat_valid.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星是否在轨 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_weight.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星重量",Toast.LENGTH_SHORT).show();
					}else if(Sat_launch.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星发射工具",Toast.LENGTH_SHORT).show();
					}else if(Sat_launchtime.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星发射时间",Toast.LENGTH_SHORT).show();
					}else if(Sat_tle1.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星tle1",Toast.LENGTH_SHORT).show();
					}else if(Sat_tle2.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星tle2 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_info.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "请输入卫星资料 ",Toast.LENGTH_SHORT).show();
					}else{
						submit_satinfo();
						Toast.makeText(getApplicationContext(), "上传成功",Toast.LENGTH_SHORT).show();
						result = null;
					}
					break;
					
			   default:
					break;
			}
		}
	}
	
   private void showDialog() {
		new AlertDialog.Builder(this)
				.setTitle("选择图片")
				.setPositiveButton("相机", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
									
						Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
						
						intent.putExtra(MediaStore.EXTRA_OUTPUT,Uri.fromFile(tempFile));
						Log.e("file", tempFile.toString());
						startActivityForResult(intent, PHOTO_REQUEST_TAKEPHOTO);
					}
				})
				.setNegativeButton("相册", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						dialog.dismiss();
						Intent intent = new Intent(Intent.ACTION_PICK, null);
						intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
						startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
					}
				}).show();
	}
   
   @Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub

		switch (requestCode) {
		case PHOTO_REQUEST_TAKEPHOTO:
			startPhotoZoom(Uri.fromFile(tempFile), 400,300);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 400,300);
			break;

		case PHOTO_REQUEST_CUT:
			 Log.e("zoom", "begin2");
			if (data != null) 
				setPicToView(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void startPhotoZoom(Uri uri, int Xsize, int Ysize) {
		 Log.e("zoom", "begin");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop为true是设置在开启的intent中设置显示的view可以剪裁
		intent.putExtra("crop", "true");

		// aspectX aspectY 是宽高的比例
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);

		// outputX,outputY 是剪裁图片的宽高
		intent.putExtra("outputX", Xsize);
		intent.putExtra("outputY", Ysize);
		intent.putExtra("return-data", true);
		Log.e("zoom", "begin1");

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	//将进行剪裁后的图片显示到UI界面上
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");

			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
			Log.e("save",dateFormat.format(date));
			saveMyBitmap(photo,dateFormat.format(date));//将图片进行存储！！！！！！！！！
			
			Drawable drawable = new BitmapDrawable(photo);
			img_btn.setBackgroundDrawable(drawable);
		}
	}

	//使用系统当前日期加以调整作为照片的名称
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	 public void saveMyBitmap(Bitmap mBitmap,String bitName){

		img_src = "/storage/emulated/0/DCIM/Screenshots/"+bitName + ".jpg";
//        File f = new File( "/storage/emulated/0/DCIM/Screenshots/"+bitName + ".jpg");
		File f = new File(img_src);
        FileOutputStream fOut = null;
        try {
                fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
                fOut.flush();
        } catch (IOException e) {
                e.printStackTrace();
        }
        try {
                fOut.close();
        } catch (IOException e) {
                e.printStackTrace();
        }
	}

	 public void submit_satinfo(){
		 // TODO Auto-generated method stub
			new Thread() {
				public void run() {
//					File f = new File("/storage/emulated/0/DCIM/Screenshots/IMG_20161219_180923.jpg");
					File f = new File(img_src);
					final Map<String, String> map = new HashMap<String, String>();
					map.put("namecn", Sat_namecn.getText().toString());
					map.put("name", Sat_name.getText().toString());
					map.put("satno", Sat_satno.getText().toString());
					map.put("typename", Sat_typename.getText().toString());
					map.put("valid", Sat_valid.getText().toString());
					map.put("weight", Sat_weight.getText().toString());
					map.put("launch", Sat_launch.getText().toString());
					map.put("launchtime", Sat_launchtime.getText().toString());
					map.put("tle1", Sat_tle1.getText().toString());
					map.put("tle2", Sat_tle2.getText().toString());
					map.put("info", Sat_info.getText().toString());
					try {
						result = CommantUtil.uploadSubmit(url, map, f);
						System.out.println("result:" + result);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
	 }


}
