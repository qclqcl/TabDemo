package com.andyidea.tabdemo;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

public class CommantUtil {
	
	
	/**
	 * 提交参数里有文件的数据
	 * @param url 服务器地址
	 * @param param 参数
	 * @return 服务器返回结果
	 * @throws Exception
	 */
	//发送个人头像
	public static String uploadSubmit(String url, Map<String, String> param,File file) throws Exception {
		System.out.println("11111");
		HttpPost post = new HttpPost(url);  
		HttpClient httpClient=new DefaultHttpClient();
		MultipartEntity entity = new MultipartEntity();
		if (param != null && !param.isEmpty()) {
			for (Map.Entry<String, String> entry : param.entrySet()) {     
				if (entry.getValue() != null
						&& entry.getValue().trim().length() > 0) {
					entity.addPart(entry.getKey(),new StringBody(entry.getValue(),
							Charset.forName(org.apache.http.protocol.HTTP.UTF_8)));
				}
			}
		}
		// 添加文件参数
		if (file != null && file.exists()) {
			entity.addPart("file", new FileBody(file));
		}
		post.setEntity(entity);  
		HttpResponse response = httpClient.execute(post);
		int stateCode = response.getStatusLine().getStatusCode();
		StringBuffer sb = new StringBuffer();
		System.out.println("222");
		if (stateCode == HttpStatus.SC_OK) {
			System.out.println("333");
			HttpEntity result = response.getEntity();
			if (result != null) {
				InputStream is = result.getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				String tempLine;
				while ((tempLine = br.readLine()) != null) {
					sb.append(tempLine);
				}
			}
		}
		post.abort();
		return sb.toString();
	}
}
