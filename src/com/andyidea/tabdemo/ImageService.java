package com.andyidea.tabdemo;

import java.io.IOException;  
import java.io.InputStream;  
import java.net.HttpURLConnection;  
import java.net.URL;  
  
import com.andyidea.tabdemo.StreamTool;
  
public class ImageService {  
      
    public static byte[] getImage(String path) throws IOException {  
        URL url = new URL(path);  
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
        conn.setRequestMethod("GET");   //�������󷽷�ΪGET  
        conn.setReadTimeout(5*1000);    //���������ʱʱ��Ϊ5��  
        InputStream inputStream = conn.getInputStream();   //ͨ�����������ͼƬ����  
        byte[] data = StreamTool.readInputStream(inputStream);     //���ͼƬ�Ķ���������  
        return data;  
          
    }  
}