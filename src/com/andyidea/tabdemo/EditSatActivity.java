package com.andyidea.tabdemo;

import java.io.File;
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
	private Button btn;
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
		btn = (Button) findViewById(R.id.btn);

		img_btn.setOnClickListener(new ButtonOnClickListener());
		btn.setOnClickListener(new ButtonOnClickListener());
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {				          	           		
	           case R.id.img_btn: 
	        	   showDialog();
					break;
	           case R.id.btn:	        	  	        
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
			startPhotoZoom(Uri.fromFile(tempFile), 150);
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
				startPhotoZoom(data.getData(), 150);
			break;

		case PHOTO_REQUEST_CUT:
			 Log.e("zoom", "begin2");
			if (data != null) 
				setPicToView(data);
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	private void startPhotoZoom(Uri uri, int size) {
		 Log.e("zoom", "begin");
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// crop涓簍rue鏄缃湪寮�惎鐨刬ntent涓缃樉绀虹殑view鍙互鍓
		intent.putExtra("crop", "true");

		// aspectX aspectY 鏄楂樼殑姣斾緥
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);

		// outputX,outputY 鏄壀瑁佸浘鐗囩殑瀹介珮
		intent.putExtra("outputX", size);
		intent.putExtra("outputY", size);
		intent.putExtra("return-data", true);
		 Log.e("zoom", "begin1");
		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	//灏嗚繘琛屽壀瑁佸悗鐨勫浘鐗囨樉绀哄埌UI鐣岄潰涓�	
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");
			Drawable drawable = new BitmapDrawable(photo);
			img_btn.setBackgroundDrawable(drawable);
		}
	}

	// 浣跨敤绯荤粺褰撳墠鏃ユ湡鍔犱互璋冩暣浣滀负鐓х墖鐨勫悕绉�	
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}


}
