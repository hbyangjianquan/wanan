package com.lewisen.goodnight;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.lewisen.goodnight.secondView.SecondActivity;
import com.umeng.onlineconfig.OnlineConfigAgent;

/**
 * APP启动图片界面
 * 
 * @author Lewisen
 * 
 */
public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		View view = View.inflate(this, R.layout.activity_main, null);
		view.findViewById(R.id.first_text).setVisibility(View.VISIBLE);
		view.findViewById(R.id.wanan_image).setVisibility(View.VISIBLE);
		view.findViewById(R.id.wanan_text).setVisibility(View.VISIBLE);
		setContentView(view);

		// 渐变展示启动屏,这里通过动画来设置了开启应用程序的界面
		AlphaAnimation aa = new AlphaAnimation(0.3f, 1.0f);
		aa.setDuration(2000);
		view.startAnimation(aa);
		// 给动画添加监听方法
		aa.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation arg0) {
				// 判断是否显示广告
				if (!MyApplication.appConfig.isDisplayAD()) {
					// 正常显示第二屏图片
					redirectTo();
				} else {
					// 显示开屏广告
					redirectToAd();
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationStart(Animation animation) {
			}

		});

		MaxID maxID = new MaxID();
		maxID.saveMaxIdToProper(this, null);

		onlineConfig();

	}

	private void onlineConfig() {
		// 加载在线参数
		OnlineConfigAgent.getInstance().updateOnlineConfig(this);
		String adSwitch = OnlineConfigAgent.getInstance().getConfigParams(this,
				"adSwitch");
		if ("false".equals(adSwitch)) {// 不显示广告
			MyApplication.appConfig.setDisplayAD(false);
		} else if ("true".equals(adSwitch)) {// 显示广告 下次
			MyApplication.appConfig.setDisplayAD(true);
		}
	}

	/**
	 * 跳转到第二屏界面的方法
	 */
	private void redirectTo() {
		Intent intent = new Intent(this, SecondActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);

		finish();
	}

	/**
	 * 跳转到广告界面的方法
	 */
	private void redirectToAd() {
		Intent intent = new Intent(this, CSplashActivity.class);
		startActivity(intent);
		finish();
	}

}
