package com.andyidea.tabdemo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import satellite.tle.image.Satinfo;

import com.andyidea.tabdemo.A3Activity.ButtonOnClickListener;

import android.app.Activity;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.os.Build;
import android.provider.MediaStore;

public class AddSatActivity extends Activity {

	private ImageButton img_btn_addsat;
	private Button submit_addsat,ButtonDone_addsat;
	private LocationApplication myApp;
	private Uri photouri;
	//private LinearLayout editlinearLayout;
	private static final int PHOTO_REQUEST_TAKEPHOTO = 1;
	private static final int PHOTO_REQUEST_GALLERY = 2;
	private static final int PHOTO_REQUEST_CUT = 3;
	
	File tempFile = new File(Environment.getExternalStorageDirectory(),getPhotoFileName());
	//public static final String url = "http://app.api.fx.yijia.com/faxing/json_user.php?action=user_file_upload";//Ҫ�ϴ��ĵ�ַ������
	public static final String url = "http://www.4001149114.com/sattrack/satmanage/sataddapp";//Ҫ�ϴ��ĵ�ַ������	
	public String img_src;
	
	private EditText Sat_namecn,Sat_name,Sat_satno,Sat_typename,Sat_valid,Sat_weight,Sat_launch,Sat_launchtime,Sat_tle1,Sat_tle2,Sat_info;
	String result;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_add_sat);
		init();
	}

	private void init() {
		myApp = (LocationApplication)getApplication();
		
		img_btn_addsat = (ImageButton) findViewById(R.id.img_btn_addsat);
		img_btn_addsat.setOnClickListener(new ButtonOnClickListener());
		
		submit_addsat = (Button) findViewById(R.id.submit_addsat);
		submit_addsat.setOnClickListener(new ButtonOnClickListener());
		
		ButtonDone_addsat = (Button) findViewById(R.id.ButtonDone_addsat);
		ButtonDone_addsat.setOnClickListener(new ButtonOnClickListener());
		
		Sat_namecn = (EditText)findViewById(R.id.addsat_namecn);
		Sat_name = (EditText)findViewById(R.id.addsat_name);
		
		Sat_satno = (EditText)findViewById(R.id.addsat_satno);

		Sat_typename = (EditText)findViewById(R.id.addsat_typename);
		Sat_valid = (EditText)findViewById(R.id.addsat_valid);
		Sat_weight = (EditText)findViewById(R.id.addsat_weight);
		Sat_launch = (EditText)findViewById(R.id.addsat_launch);
		Sat_launchtime = (EditText)findViewById(R.id.addsat_launchtime);

		Sat_tle1 = (EditText)findViewById(R.id.addsat_tle1);
		Sat_tle2 = (EditText)findViewById(R.id.addsat_tle2);
		Sat_info = (EditText)findViewById(R.id.addsat_info);		
	}

	class ButtonOnClickListener implements OnClickListener{

		@Override
		public void onClick(View v) {
			
			switch (v.getId()) {
			  case R.id.ButtonDone_addsat: 
	        	   finish();
					break;
	           case R.id.img_btn_addsat: 
	        	   showDialog();
					break;
	           case R.id.submit_addsat:
					if(Sat_namecn.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "�������������� ",Toast.LENGTH_SHORT).show();
					}else if(Sat_name.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "����������Ӣ������ ",Toast.LENGTH_SHORT).show();
					}else if(Sat_satno.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "���������Ǳ�� ",Toast.LENGTH_SHORT).show();
					}else if(Sat_typename.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "��������������",Toast.LENGTH_SHORT).show();
					}else if(Sat_valid.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "�����������Ƿ��ڹ� ",Toast.LENGTH_SHORT).show();
					}else if(Sat_weight.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "��������������",Toast.LENGTH_SHORT).show();
					}else if(Sat_launch.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "���������Ƿ��乤��",Toast.LENGTH_SHORT).show();
					}else if(Sat_launchtime.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "���������Ƿ���ʱ��",Toast.LENGTH_SHORT).show();
					}else if(Sat_tle1.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "����������tle1",Toast.LENGTH_SHORT).show();
					}else if(Sat_tle2.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "����������tle2 ",Toast.LENGTH_SHORT).show();
					}else if(Sat_info.getText().toString().length()==0){
						Toast.makeText(getApplicationContext(), "�������������� ",Toast.LENGTH_SHORT).show();
					}else{
						submit_satinfo();
						Toast.makeText(getApplicationContext(), "�ϴ��ɹ�",Toast.LENGTH_SHORT).show();
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
					.setTitle("ѡ��ͼƬ")
					.setPositiveButton("���", new DialogInterface.OnClickListener() {

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
					.setNegativeButton("���", new DialogInterface.OnClickListener() {

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
			//photouri = Uri.fromFile(tempFile);
			//startPhotoZoom(Uri.fromFile(tempFile), 2048,2048);
			try {
				FileInputStream fin = new FileInputStream(tempFile);  
	            //�������,������Ͳ�����̫��,���̫���Ҫ������취������һ�δ���̶�����byte[]  
	            byte[] bytes  = new byte[fin.available()];  
	            //���ļ�����д���ֽ����飬�ṩ���Ե�case  
	            fin.read(bytes);  
	              
	            fin.close(); 
	            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);	        
				Drawable drawable = new BitmapDrawable(bitmap);
				img_btn_addsat.setBackgroundDrawable(drawable);
			} catch (Exception e) {
               e.printStackTrace();
			}
			break;

		case PHOTO_REQUEST_GALLERY:
			if (data != null)
			{	
				photouri = data.getData();
				//Uri uri = data.getData();
				 
				String[] proj = { MediaStore.Images.Media.DATA };
				 
				Cursor actualimagecursor = managedQuery(photouri,proj,null,null,null);
				 
				int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				 
				actualimagecursor.moveToFirst();
				 
				String img_path = actualimagecursor.getString(actual_image_column_index);
				 
				tempFile = new File(img_path);
				try {
					FileInputStream fin = new FileInputStream(tempFile);  
		            //�������,������Ͳ�����̫��,���̫���Ҫ������취������һ�δ���̶�����byte[]  
		            byte[] bytes  = new byte[fin.available()];  
		            //���ļ�����д���ֽ����飬�ṩ���Ե�case  
		            fin.read(bytes);  
		              
		            fin.close(); 
		            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);	           
					Drawable drawable = new BitmapDrawable(bitmap);
					img_btn_addsat.setBackgroundDrawable(drawable);
				} catch (Exception e) {
	                e.printStackTrace();
				}
				//startPhotoZoom(data.getData(), 2048,2048);
			}
			//if (data != null) 
				//setPicToView(data);
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
		// cropΪtrue�������ڿ�����intent��������ʾ��view���Լ��ã�
		intent.putExtra("crop", "false");

		// aspectX aspectY �ǿ�ߵı���
//		intent.putExtra("aspectX", 1);
//		intent.putExtra("aspectY", 1);

		// outputX,outputY �Ǽ���ͼƬ�Ŀ��
		intent.putExtra("outputX", Xsize);
		intent.putExtra("outputY", Ysize);
		intent.putExtra("return-data", true);
		Log.e("zoom", "begin1");

		startActivityForResult(intent, PHOTO_REQUEST_CUT);
	}

	//�����м��ú��ͼƬ��ʾ��UI������
	private void setPicToView(Intent picdata) {
		Bundle bundle = picdata.getExtras();
		if (bundle != null) {
			Bitmap photo = bundle.getParcelable("data");

			Date date = new Date(System.currentTimeMillis());
			SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
			Log.e("save",dateFormat.format(date));
			//saveMyBitmap(photo,dateFormat.format(date));//��ͼƬ���д洢������������������
			
			Drawable drawable = new BitmapDrawable(photo);
			img_btn_addsat.setBackgroundDrawable(drawable);
		}
	}

	//ʹ��ϵͳ��ǰ���ڼ��Ե�����Ϊ��Ƭ������
	private String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
		return dateFormat.format(date) + ".jpg";
	}

	 public void saveMyBitmap(Bitmap mBitmap,String bitName){

		img_src = Environment.getExternalStorageDirectory()+"/"+bitName + ".jpg";
//       File f = new File( "/storage/emulated/0/DCIM/Screenshots/"+bitName + ".jpg");
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
					File f = tempFile;//new File(img_path);
					//File f = new File(img_src);
					final Map<String, String> map = new HashMap<String, String>();
					map.put("namecn", Sat_namecn.getText().toString());
					map.put("name", Sat_name.getText().toString());
					map.put("sid", Sat_satno.getText().toString());
					map.put("type", Sat_typename.getText().toString());
					map.put("valid", Sat_valid.getText().toString());
					map.put("weight", Sat_weight.getText().toString());
					map.put("launch", Sat_launch.getText().toString());
					map.put("launchtime", Sat_launchtime.getText().toString());
					map.put("tle1", Sat_tle1.getText().toString());
					map.put("tle2", Sat_tle2.getText().toString());
					map.put("info", Sat_info.getText().toString());
					try {
						result = CommantUtil.uploadSubmit(url, map, f);
						//System.out.println("result:" + result);
						//Toast.makeText(getApplicationContext(), result,Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
	 }


}
