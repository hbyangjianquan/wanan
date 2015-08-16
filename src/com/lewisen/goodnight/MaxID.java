package com.lewisen.goodnight;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

/**
 * 
 * 
 * @author Lewisen
 * 
 */
public class MaxID {
	public static final int GET_ID_SUCCESS = 20;
	private int articlePageMaxId = 0;
	private int homePageMaxId = 0;
	private int picturePageMaxId = 0;

	public void saveMaxIdToProper(final Context context, final Handler mHandler) {

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 获取最新ID： MaxIdManageServlet?order=get
				JSONObject jsonObject = GetJsonFromNet.getJsonObj(
						"MaxIdManageServlet", 0, 2);
				if (jsonObject != null) {
					try {
						articlePageMaxId = jsonObject
								.getInt("articlePageMaxId");
						homePageMaxId = jsonObject.getInt("homePageMaxId");
						picturePageMaxId = jsonObject
								.getInt("picturePageMaxId");

						SharedPreferences preferences = context
								.getSharedPreferences("maxId",
										Context.MODE_PRIVATE);
						Editor editor = preferences.edit();
						editor.putInt("articlePageMaxId", articlePageMaxId);
						editor.putInt("homePageMaxId", homePageMaxId);
						editor.putInt("picturePageMaxId", picturePageMaxId);
						editor.commit();

						if (mHandler != null) {
							mHandler.obtainMessage(GET_ID_SUCCESS)
									.sendToTarget();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		}).start();
	}

	/**
	 * @param context
	 * @param key
	 *            homePageMaxId,picturePageMaxId,articlePageMaxId
	 * @param defaultValue
	 * @return
	 */
	public int getMaxIdFromProperties(Context context, String key,
			int defaultValue) {

		SharedPreferences preferences = context.getSharedPreferences("maxId",
				Context.MODE_PRIVATE);
		return preferences.getInt(key, defaultValue);

	}

}
