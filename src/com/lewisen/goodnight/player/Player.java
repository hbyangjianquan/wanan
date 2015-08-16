package com.lewisen.goodnight.player;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.widget.SeekBar;
import android.widget.TextView;

public class Player implements OnBufferingUpdateListener, OnCompletionListener,
		OnPreparedListener {

	public MediaPlayer mediaPlayer; // 媒体播放器
	private SeekBar seekBar; // 拖动条
	private Timer mTimer = new Timer(); // 计时器
	private MyTimerTask timerTask;
	private Handler handler;
	private TextView musicTimeText;// 音乐时间
	private boolean startPlayState = false;// 播放状态 ，true是播放，false为不播放

	// 初始化播放器
	public Player() {
		super();
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
			mediaPlayer.setLooping(true);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void initPlayer(SeekBar seekBar, TextView musicTimeText) {
		this.seekBar = seekBar;
		this.musicTimeText = musicTimeText;
		if (timerTask != null) {
			timerTask.cancel();
		}
		timerTask = new MyTimerTask();
		handler = new MyHandler();

	}

	// 计时器
	class MyTimerTask extends TimerTask {
		@Override
		public void run() {
			if (mediaPlayer == null) {
				return;
			} else if (mediaPlayer.isPlaying() && seekBar.isPressed() == false) {
				handler.sendEmptyMessage(0); // 发送消息
			}
		}
	}

	class MyHandler extends Handler {
		public void handleMessage(android.os.Message msg) {
			if (mediaPlayer == null) {
				return;
			}
			int position = mediaPlayer.getCurrentPosition();
			int duration = mediaPlayer.getDuration();// 毫秒
			if (duration > 0) {
				// 计算进度（获取进度条最大刻度*当前音乐播放位置 / 当前音乐时长）
				long pos = seekBar.getMax() * position / duration;
				seekBar.setProgress((int) pos);

				SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
				String ms = formatter.format(duration - position);
				musicTimeText.setText("-" + ms);
			}
		}
	}

	public void play() {
		try {
			mediaPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param url
	 *            url地址
	 */
	public void playUrl(String url) {
		try {
			mediaPlayer.reset();
			mediaPlayer.setDataSource(url); // 设置数据源
			mediaPlayer.prepare(); // prepare自动播放
			mediaPlayer.setLooping(true);

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 暂停
	public void pause() {
		try {
			mediaPlayer.pause();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	// 暂停
	public void start() {
		try {
			mediaPlayer.start();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}

	// 停止
	public void stop() {
		if (mediaPlayer != null) {
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			// mediaPlayer.release();
			// mediaPlayer = null;
		}
	}

	// 释放资源
	public void release() {
		if (mediaPlayer != null) {
			mTimer.cancel();
			try {
				mediaPlayer.stop();
				mediaPlayer.release();
			} catch (IllegalStateException e) {
				e.printStackTrace();
			}
			mediaPlayer = null;
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		if (startPlayState) {// 解决缓存期间改变播放状态问题
			mp.start();
			// 每一秒触发一次
			mTimer.schedule(timerTask, 0, 1000);
		}

		// Log.e("mediaPlayer", "onPrepared");
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// Log.e("mediaPlayer", "onCompletion");
	}

	/**
	 * 缓冲更新
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
		seekBar.setSecondaryProgress(percent);
		// int currentProgress = seekBar.getMax()
		//		* mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration();
		// Log.e(currentProgress + "% play", percent + " buffer");
	}

	public boolean isStartPlayState() {
		return startPlayState;
	}

	public void setStartPlayState(boolean startPlayState) {
		this.startPlayState = startPlayState;
	}

}
