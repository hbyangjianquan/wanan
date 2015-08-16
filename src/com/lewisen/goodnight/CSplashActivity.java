package com.lewisen.goodnight;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.baidu.mobads.SplashAd;
import com.baidu.mobads.SplashAd.SplashType;
import com.baidu.mobads.SplashAdListener;
import com.lewisen.goodnight.mainview.MainView;
import com.lewisen.goodnight.secondView.SecondActivity;

public class CSplashActivity extends Activity {
	private Timer timer = new Timer();
	private TimerTask task;
	private Handler handler;
	private int time = 5;
	private Button jumpButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		View view = View.inflate(this, R.layout.splash_ad, null);
		setContentView(view);
		RelativeLayout adsParent = (RelativeLayout) view
				.findViewById(R.id.ad_splash);
		jumpButton = (Button) view.findViewById(R.id.jump_ad);
		jumpButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				timer.cancel();
				jumpWhenCanClick();
			}
		});

		SplashAdListener listener = new SplashAdListener() {
			@Override
			public void onAdDismissed() {
				// Log.i("DEBUG", "onAdDismissed");
				timer.cancel();
				jumpWhenCanClick();// 跳转至您的应用主界面
			}

			@Override
			public void onAdFailed(String arg0) {
				// Log.i("DEBUG", "onAdFailed");
				timer.cancel();
				jump();
			}

			@Override
			public void onAdPresent() {
				// Log.i("DEBUG", "onAdPresent");
			}

			@Override
			public void onAdClick() {
				// Log.i("DEBUG", "onAdClick");
				// 设置开屏可接受点击时，该回调可用
			}
		};

		/**
		 * 构造函数： new SplashAd(Context context, ViewGroup adsParent,
		 * SplashAdListener listener, String adPlaceId, boolean canClick,
		 * SplashType splashType);
		 */
		String adPlaceId = "2010230";// 开屏广告位ID
		new SplashAd(this, adsParent, listener, adPlaceId, true,
				SplashType.CACHE);
		timeTask();
	}
	/**
	 * 倒计时5秒
	 */
	private void timeTask() {
		handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);

				time--;
				jumpButton.setText("跳过广告 " + time);
				if (time == 0) {
					timer.cancel();
				}
			}

		};
		task = new TimerTask() {

			@Override
			public void run() {
				// 发送空消息
				handler.obtainMessage().sendToTarget();
			}
		};

		timer.schedule(task, 1000, 1000);
	}

	/**
	 * 当设置开屏可点击时，需要等待跳转页面关闭后，再切换至您的主窗口。故此时需要增加waitingOnRestart判断。
	 * 另外，点击开屏还需要在onRestart中调用jumpWhenCanClick接口。
	 */
	public boolean waitingOnRestart = false;

	private void jumpWhenCanClick() {
		// Log.d("DEBUG", "this.hasWindowFocus():" + this.hasWindowFocus());
		if (this.hasWindowFocus() || waitingOnRestart) {
			this.startActivity(new Intent(CSplashActivity.this, MainView.class));
			this.finish();
		} else {
			waitingOnRestart = true;
		}

	}

	/**
	 * 
	 */
	private void jump() {
		// this.startActivity(new Intent(CSplashActivity.this, MainView.class));
		// this.finish();
		Intent intent = new Intent(this, SecondActivity.class);
		this.startActivity(intent);
		this.overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
		this.finish();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (waitingOnRestart) {
			jumpWhenCanClick();
		}
	}

}
