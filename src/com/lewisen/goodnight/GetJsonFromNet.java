package com.lewisen.goodnight;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

/**
 * 获取服务器json对象
 * 
 * @author Lewisen
 */
public class GetJsonFromNet {

	/**
	 * @param requestName请求的Servlet的名称
	 * @param requestID请求的页面ID码
	 * @param sort请求的页面类型
	 *            1:HomePage/ArticlePage/PicturePage; 2:MaxId; 3:like功能,以及其他命令
	 * @return
	 */
	public static JSONObject getJsonObj(String requestName, int requestID,
			int sort) {
		// 创建HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 设置连接超时时长，单位毫秒
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// 设置读取超时,单位毫秒
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				30000);
		// 创建代表请求的对象,参数是访问服务器地址
		String url = null;
		if (sort == 1) {
			url = MyServer.URL + requestName + "?id=" + requestID;
		} else if (sort == 2) {
			url = MyServer.URL + requestName + "?order=get";
		} else if (sort == 3) {
			url = MyServer.URL + requestName;
		}
		// Log.d("DEBUG", "url = " + url);
		HttpGet httpGet = new HttpGet(url);

		// 执行请求，获取服务器发还的对象
		try {
			HttpResponse response = httpClient.execute(httpGet);
			// 检查响应的状态是否正常，如果返回的数据是200则正常，如果是404则是客户端错误，如果是505则是服务器错误
			int result = response.getStatusLine().getStatusCode();
			if (200 == result) {
				// 从响应对象中取出数据
				HttpEntity entity = response.getEntity();
				InputStream in = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in));
				JSONTokener jsonTokener = new JSONTokener(reader.readLine());
				
				in.close();
				return (JSONObject) jsonTokener.nextValue();
			}
		} catch (Exception e) {
			// System.out.println("GetJsonFromNet 连接服务器错误");
			e.printStackTrace();
		}
		return null;

	}
}
