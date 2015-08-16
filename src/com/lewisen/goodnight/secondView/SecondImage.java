package com.lewisen.goodnight.secondView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.lewisen.goodnight.GetJsonFromNet;
import com.lewisen.goodnight.MyServer;

public class SecondImage {
	private Context context;

	public SecondImage(Context context) {
		this.context = context;
	}

	public void newPathThread() {

		new Thread(new Runnable() {
			@Override
			public void run() {

				String path = null;

				// 获取最新ID： MaxIdManageServlet?order=get
				JSONObject jsonObject = GetJsonFromNet.getJsonObj(
						MyServer.SECOND_IMAGE + "?order=get", 0, 3);

				if (jsonObject != null) {
					try {
						path = jsonObject.getString("path");
						// Log.d("DEBUG", "path=" + path);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					if (!"null".equals(path)) {
						if (path.equals(getPath()) && (getState())) {
							// Log.d("DEBUG", "跳出...");
							return;
						} else {
							saveInfo("state", false);

							try {
								path = URLEncoder.encode(path, "UTF-8");
							} catch (UnsupportedEncodingException e) {
								e.printStackTrace();
							}
							String url = MyServer.PICTURE_URL + path;
							String filesDir = context.getExternalFilesDir(null)
									.getPath();
							downloadImage(url, filesDir);

							saveInfo("lastPath", path);
						}
					}

				}
			}
		}).start();
	}

	/**
	 * 保存信息
	 * 
	 * @param context
	 * @param key
	 * @param value
	 */
	public void saveInfo(String key, boolean value) {
		SharedPreferences preferences = context.getSharedPreferences("second",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	public void saveInfo(String key, String value) {
		SharedPreferences preferences = context.getSharedPreferences("second",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putString(key, value);
		editor.commit();
	}

	/**
	 * 获取状态
	 * 
	 * @param context
	 * @return
	 */
	public boolean getState() {
		SharedPreferences preferences = context.getSharedPreferences("second",
				Context.MODE_PRIVATE);
		return preferences.getBoolean("state", false);
	}

	/**
	 * 获取已经下载的图片路径
	 * 
	 * @param context
	 * @return
	 */
	public String getPath() {
		SharedPreferences preferences = context.getSharedPreferences("second",
				Context.MODE_PRIVATE);
		return preferences.getString("lastPath", null);
	}

	/**
	 * 获取保存的图片路径
	 * 
	 * @return
	 */
	public String getImageDir() {
		SharedPreferences preferences = context.getSharedPreferences("second",
				Context.MODE_PRIVATE);
		return preferences.getString("imageDir", null);
	}

	/**
	 * 下载网络图片到sd卡，存储路径为imageDir
	 * 
	 * @param url
	 * @param filesDir
	 */
	private void downloadImage(String url, String filesDir) {
		// 创建HttpClient对象
		HttpClient httpClient = new DefaultHttpClient();
		// 设置连接超时时长，单位毫秒
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
		// 设置读取超时,单位毫秒
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				30000);
		// Log.d("DEBUG", "url=" + url);
		HttpGet httpGet = new HttpGet(url);

		// 执行请求，获取服务器发还的对象
		try {
			HttpResponse response = httpClient.execute(httpGet);
			// 检查响应的状态是否正常，如果返回的数据是200则正常，如果是404则是客户端错误，如果是505则是服务器错误
			int result = response.getStatusLine().getStatusCode();
			if (200 == result) {
				// 从响应对象中取出数据
				HttpEntity entity = response.getEntity();
				byte[] data = EntityUtils.toByteArray(entity);
				String[] imageFiles = url.split("%5C");
				String imageDir = filesDir + File.separator
						+ imageFiles[imageFiles.length - 1];
				FileOutputStream out = new FileOutputStream(new File(imageDir));
				// Log.d("DEBUG", "imageDir=" + imageDir);

				saveInfo("imageDir", imageDir);
				out.write(data);
				out.close();

				saveInfo("state", true);// 保存下载成功状态
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 显示已经下载的图片
	 * 
	 * @param imageView
	 * @return 显示结果是否成功
	 */
	public boolean displayImage(ImageView imageView) {
		String imageDir = getImageDir();

		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;// 优化内存
		Bitmap bitmap = BitmapFactory.decodeFile(imageDir, options);
		if (bitmap != null) {
			imageView.setImageBitmap(bitmap);
			return true;
		} else {
			return false;
		}
	}

}
