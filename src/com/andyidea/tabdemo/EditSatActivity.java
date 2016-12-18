package com.andyidea.tabdemo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

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
import android.widget.ImageButton;
import android.os.Build;
import android.provider.MediaStore;

public class EditSatActivity extends Activity {

	private ImageButton img_btn;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	
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
		
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {				          	           		
	           case R.id.img_btn: 
	        	   showDialog();
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
        File f = new File( "/storage/emulated/0/DCIM/Screenshots/"+bitName + ".jpg");
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


}
