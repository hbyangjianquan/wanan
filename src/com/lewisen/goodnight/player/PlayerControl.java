package com.lewisen.goodnight.player;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.lewisen.goodnight.MyApplication;
import com.lewisen.goodnight.R;

/**
 * 播放网络音乐控制
 * 
 * @author Lewisen
 * 
 */
public class PlayerControl {
	private Player player = null;
	private String lastUrl = null;

	public PlayerControl() {
		player = new Player();
	}

	public void playerControlInit(final SeekBar seekBar,
			final TextView musicTimeText, final ImageButton playButton,
			final String url, final Context context) {

		playButton.setOnClickListener(new OnClickListener() {
			boolean state = false;// 当前状态，false为停止 true为播放

			@Override
			public void onClick(View v) {
				if (!state) {// 播放

					if (player.isStartPlayState()) {
						Toast.makeText(context, "请先停止当前正在播放的音乐",
								Toast.LENGTH_SHORT).show();
						return;
					}

					// 在播放音乐前判断是否为wifi连接，否则提示用户是否播放
					ConnectivityManager manager = (ConnectivityManager) context
							.getSystemService(Context.CONNECTIVITY_SERVICE);
					NetworkInfo info = manager.getActiveNetworkInfo();
					if (info == null) {
						Toast.makeText(context, "没有网络", Toast.LENGTH_SHORT)
								.show();
						return;
					} else if (info.getType() != ConnectivityManager.TYPE_WIFI) {
						new AlertDialog.Builder(context)
								.setMessage("现在没有连接WIFI,确认要播放网络音乐?")
								.setPositiveButton("确认",
										new DialogInterface.OnClickListener() {// 添加确定按钮
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {// 确定按钮的响应事件
												state = true;
												startPlay(seekBar,
														musicTimeText,
														playButton, url,
														context);
											}
										})
								.setNegativeButton("取消",
										new DialogInterface.OnClickListener() {// 添加返回按钮
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {// 响应事件
												return;
											}
										}).show();
					} else {// WIFI网络状态
						state = true;
						startPlay(seekBar, musicTimeText, playButton, url,
								context);
					}
				} else {// 停止
					state = false;
					Toast.makeText(context, "停止播放", Toast.LENGTH_SHORT).show();
					if (MyApplication.appConfig.getNightModeSwitch()) {
						playButton
								.setImageResource(R.drawable.music_play_night);
					} else {
						playButton.setImageResource(R.drawable.music_play);
					}

					pauseMusic();
				}
			}
		});
	}

	/**
	 * 开始播放音乐
	 * 
	 * @param url
	 */
	public void playMusic(final String url) {

		new Thread(new Runnable() {

			@Override
			public void run() {
				player.setStartPlayState(true);
				player.playUrl(url);
			}
		}).start();

	}

	public void stopMusic() {
		if (player != null) {
			player.setStartPlayState(false);
			player.stop();
			// player = null;
		}
	}

	public void pauseMusic() {
		if (player != null) {
			player.setStartPlayState(false);
			// Log.d("DEBUG", "startPlayState player != null");
			player.pause();
		}
	}

	public void startMusic() {
		if (player != null) {
			player.setStartPlayState(true);
			player.start();
		}
	}

	public void release() {
		if (player != null) {
			player.setStartPlayState(false);
			player.release();
			player = null;
		}
	}

	private synchronized void startPlay(final SeekBar seekBar,
			final TextView musicTimeText, final ImageButton playButton,
			final String url, final Context context) {
		if (MyApplication.appConfig.getNightModeSwitch()) {
			playButton.setImageResource(R.drawable.music_stop_night);
		} else {
			playButton.setImageResource(R.drawable.music_stop);
		}
		if ((seekBar.getSecondaryProgress() == 0) || (!url.equals(lastUrl))) {
			Toast.makeText(context, "音乐正在加载", Toast.LENGTH_SHORT).show();
			// 初始化当前播放的进度条
			player.initPlayer(seekBar, musicTimeText);
			seekBar.setOnSeekBarChangeListener(new SeekBarChangeEvent());
			playMusic(url);
			lastUrl = url;
		} else {
			startMusic();
		}
	}

	class SeekBarChangeEvent implements OnSeekBarChangeListener {
		int progress;

		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// 原本是(progress/seekBar.getMax())*player.mediaPlayer.getDuration()
			this.progress = progress * player.mediaPlayer.getDuration()
					/ seekBar.getMax();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// seekTo()的参数是相对与影片时间的数字，而不是与seekBar.getMax()相对的数字
			player.mediaPlayer.seekTo(progress);
		}

	}

}
