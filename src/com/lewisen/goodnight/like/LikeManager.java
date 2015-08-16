package com.lewisen.goodnight.like;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.lewisen.goodnight.GetJsonFromNet;
import com.lewisen.goodnight.MyServer;
import com.lewisen.goodnight.R;

public class LikeManager {
	public static final int GET_LIKE_SUCCESS = 21;
	private Context context;

	public LikeManager(Context context) {
		this.context = context;
	}

	/**
	 * 获取网络like子线程
	 * 
	 * @param item
	 * @param mHandler
	 * @param stopThread
	 * @param page
	 */
	public void getLikeCountFromNet(final String item, final Handler mHandler,
			final boolean stopThread, final int page) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				// 获取网络数据库资源
				// Log.d("DEBUG", "获取网络LIKE");
				JSONObject jsonObject = GetJsonFromNet.getJsonObj(MyServer.LIKE
						+ "?order=get&item=" + item, 0, 3);
				if ((!stopThread) && (jsonObject != null)) {
					Like like = new Like();
					try {
						like.setCount(jsonObject.getInt("count"));
						like.setItem(item);
						like.setPage(page);
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mHandler.obtainMessage(GET_LIKE_SUCCESS, like)
							.sendToTarget();
				}
			}
		}).start();
	}

	private void addLikeCount(final String item, final int count) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				GetJsonFromNet.getJsonObj(MyServer.LIKE + "?order=update&item="
						+ item + "&count=" + count, 0, 3);
				// Log.d("DEBUG", MyServer.LIKE + "?order=update&item=" + item
				// + "&count=" + count);
			}
		}).start();
	}

	/**
	 * 显示喜欢bar 在UI线程调用
	 * 
	 * @param view
	 * @param item
	 */
	public void disLikeState(Like like, View view) {
		Button likeButton = (Button) view.findViewById(R.id.like_bar_button);
		final TextView likeCount = (TextView) view
				.findViewById(R.id.like_bar_count);
		final ImageView likeImage = (ImageView) view
				.findViewById(R.id.like_bar_image);
		likeButton.setVisibility(View.VISIBLE);
		likeCount.setVisibility(View.VISIBLE);
		likeImage.setVisibility(View.VISIBLE);
		int count = like.getCount();
		final String item = like.getItem();

		// 显示喜欢次数
		likeCount.setText(count + "");

		boolean likeState = getLikeItemState(item);
		if (likeState) {
			likeImage.setImageResource(R.drawable.like);
		} else {
			likeImage.setImageResource(R.drawable.unlike);
		}

		likeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean likeState = getLikeItemState(item);
				if (likeState == false) {
					likeImage.setImageResource(R.drawable.like);
					likeCount.setText(Integer.parseInt((String) likeCount
							.getText()) + 1 + "");
					addLikeCount(item, 1);
					likeState = true;
				} else {
					likeImage.setImageResource(R.drawable.unlike);
					likeCount.setText(Integer.parseInt((String) likeCount
							.getText()) - 1 + "");
					addLikeCount(item, -1);
					likeState = false;
				}
				saveLikeItemState(item, likeState);
			}
		});
	}

	/**
	 * 保存喜欢状态
	 * 
	 * @param item
	 * @param state
	 */
	private void saveLikeItemState(String item, boolean state) {
		SharedPreferences preferences = context.getSharedPreferences("like",
				Context.MODE_PRIVATE);
		Editor editor = preferences.edit();
		editor.putBoolean(item, state);
		editor.commit();
	}

	/**
	 * 获取已经存储的喜欢状态， false不喜欢，true喜欢
	 * 
	 * @param item
	 * @return
	 */
	private boolean getLikeItemState(String item) {
		SharedPreferences preferences = context.getSharedPreferences("like",
				Context.MODE_PRIVATE);
		return preferences.getBoolean(item, false);
	}
}
