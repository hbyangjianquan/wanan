package com.lewisen.goodnight.mainview;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lewisen.goodnight.GuideHelper;
import com.lewisen.goodnight.MyApplication;
import com.lewisen.goodnight.PageDBHelper;
import com.lewisen.goodnight.R;
import com.lewisen.goodnight.articlePage.ArticlePageFragment;
import com.lewisen.goodnight.homePage.HomePageFragment;
import com.lewisen.goodnight.picturePage.PicturePageFragment;
import com.lewisen.goodnight.player.PlayerControl;
import com.lewisen.goodnight.userPage.UserPageFragment;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.sso.UMSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

public class MainView extends Activity {
	private ImageButton homeButton = null;
	private ImageButton articleButton = null;
	private ImageButton pictureButton = null;
	private ImageButton userButton = null;
	private ImageButton menuButton = null;
	private HomePageFragment homePageFragment = null;
	private ArticlePageFragment articlePageFragment = null;
	private PicturePageFragment picturePageFragment = null;
	private UserPageFragment userPageFragment = null;
	private PageDBHelper pageDBHelper = null;
	private long mExitTime;
	// 友盟
	private Context mContext;
	private final String mPageName = "MainView";
	final com.umeng.socialize.controller.UMSocialService umSocialService = UMServiceFactory
			.getUMSocialService("com.umeng.share");

	private PlayerControl playerControl;

	private MyPopupWindow myPopupWindow;
	private ShareInterface shareInterface;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 显示夜间 日间模式
		if (MyApplication.appConfig.getNightModeSwitch()) {
			this.setTheme(R.style.NightTheme);
		} else {
			this.setTheme(R.style.DayTheme);
		}
		mContext = this;
		// 自定义标题
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.main_view);
		// 设置标题为某个layout
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		buttonInit();
		// 设置默认显示主页界面
		setDefaultFragment();

		MobclickAgent.setDebugMode(false);// 设置友盟统计模式
		MobclickAgent.openActivityDurationTrack(false);// 关闭Activity自带的页面统计
		MobclickAgent.updateOnlineConfig(mContext);

		// 实例化数据库操作对象
		pageDBHelper = PageDBHelper.getInstance(this);

		// 加载引导界面
		GuideHelper guideHelper = new GuideHelper(this);
		guideHelper.openGuide();
		// 播放器实例化
		playerControl = new PlayerControl();
		// 友盟分享初始化
		shareInit();
	}

	private void buttonInit() {
		homeButton = (ImageButton) findViewById(R.id.home_button);
		articleButton = (ImageButton) findViewById(R.id.article_button);
		pictureButton = (ImageButton) findViewById(R.id.picture_button);
		userButton = (ImageButton) findViewById(R.id.user_button);
		menuButton = (ImageButton) findViewById(R.id.menu);

		menuButton.setVisibility(View.VISIBLE);
		// 按键监听器
		ButtOnClickListener buttOnClickListener = new ButtOnClickListener();
		homeButton.setOnClickListener(buttOnClickListener);
		articleButton.setOnClickListener(buttOnClickListener);
		pictureButton.setOnClickListener(buttOnClickListener);
		userButton.setOnClickListener(buttOnClickListener);
		menuButton.setOnClickListener(buttOnClickListener);
	}

	private void shareInit() {
		// 友盟调试日志
		com.umeng.socialize.utils.Log.LOG = false;
		umSocialService.getConfig().removePlatform(SHARE_MEDIA.SINA,
				SHARE_MEDIA.TENCENT);

		String appID = "xx";
		String appSecret = "xx";
		// 添加微信平台
		UMWXHandler wxHandler = new UMWXHandler(this, appID, appSecret);
		wxHandler.addToSocialSDK();
		// 支持微信朋友圈
		UMWXHandler wxCircleHandler = new UMWXHandler(this, appID, appSecret);
		wxCircleHandler.setToCircle(true);
		wxCircleHandler.addToSocialSDK();

		// QQ平台
		String appIDQQ = "xx";
		String appKeyQQ = "xx";
		// QQ
		UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(this, appIDQQ,
				appKeyQQ);
		qqSsoHandler.addToSocialSDK();
		// QQ空间
		QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(this, appIDQQ,
				appKeyQQ);
		qZoneSsoHandler.addToSocialSDK();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/** 使用SSO授权必须添加如下代码 */
		UMSsoHandler ssoHandler = umSocialService.getConfig().getSsoHandler(
				requestCode);
		if (ssoHandler != null) {
			ssoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
		MobclickAgent.onResume(mContext);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
		MobclickAgent.onPause(mContext);
	}

	@Override
	protected void onDestroy() {
		// 关闭数据库
		pageDBHelper.close();
		playerControl.release();
		super.onDestroy();
	}

	private void setDefaultFragment() {
		showFragment(1);
		shareInterface = homePageFragment;
	}

	private void showFragment(int index) {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();

		// 先隐藏已经存在的Fragment
		if (homePageFragment != null) {
			transaction.hide(homePageFragment);
		}
		if (articlePageFragment != null) {
			transaction.hide(articlePageFragment);
		}
		if (picturePageFragment != null) {
			transaction.hide(picturePageFragment);
		}
		if (userPageFragment != null) {
			transaction.hide(userPageFragment);
		}
		switch (index) {
		case 1:
			if (homePageFragment != null)
				transaction.show(homePageFragment);
			else {
				homePageFragment = new HomePageFragment();
				transaction.add(R.id.frame_layout, homePageFragment);
			}
			break;
		case 2:
			if (articlePageFragment != null)
				transaction.show(articlePageFragment);
			else {
				articlePageFragment = new ArticlePageFragment();
				transaction.add(R.id.frame_layout, articlePageFragment);
			}
			break;
		case 3:
			if (picturePageFragment != null)
				transaction.show(picturePageFragment);
			else {
				picturePageFragment = new PicturePageFragment();
				transaction.add(R.id.frame_layout, picturePageFragment);
			}
			break;
		case 4:
			if (userPageFragment != null)
				transaction.show(userPageFragment);
			else {
				userPageFragment = new UserPageFragment();
				transaction.add(R.id.frame_layout, userPageFragment);
			}
			break;
		}
		transaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ((System.currentTimeMillis() - mExitTime) > 2000) {
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				mExitTime = System.currentTimeMillis();
			} else {
				finish();
			}
			return true;
		}

		return super.onKeyDown(keyCode, event);
	}

	public PlayerControl getPlayContler() {
		return playerControl;
	}

	/**
	 * 按键监听器
	 * 
	 * @author Lewisen
	 * 
	 */
	private class ButtOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.home_button:
				homeButton.setImageDrawable(getResources().getDrawable(
						R.drawable.home_pressed));
				articleButton.setImageDrawable(getResources().getDrawable(
						R.drawable.list));
				pictureButton.setImageDrawable(getResources().getDrawable(
						R.drawable.picture));
				userButton.setImageDrawable(getResources().getDrawable(
						R.drawable.user));
				showFragment(1);
				shareInterface = homePageFragment;
				menuButton.setVisibility(View.VISIBLE);
				break;
			case R.id.article_button:
				homeButton.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				articleButton.setImageDrawable(getResources().getDrawable(
						R.drawable.list_pressed));
				pictureButton.setImageDrawable(getResources().getDrawable(
						R.drawable.picture));
				userButton.setImageDrawable(getResources().getDrawable(
						R.drawable.user));
				showFragment(2);
				menuButton.setVisibility(View.VISIBLE);
				shareInterface = articlePageFragment;
				break;
			case R.id.picture_button:
				homeButton.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				articleButton.setImageDrawable(getResources().getDrawable(
						R.drawable.list));
				pictureButton.setImageDrawable(getResources().getDrawable(
						R.drawable.picture_pressed));
				userButton.setImageDrawable(getResources().getDrawable(
						R.drawable.user));
				showFragment(3);
				menuButton.setVisibility(View.VISIBLE);
				shareInterface = picturePageFragment;
				break;
			case R.id.user_button:
				homeButton.setImageDrawable(getResources().getDrawable(
						R.drawable.home));
				articleButton.setImageDrawable(getResources().getDrawable(
						R.drawable.list));
				pictureButton.setImageDrawable(getResources().getDrawable(
						R.drawable.picture));
				userButton.setImageDrawable(getResources().getDrawable(
						R.drawable.user_pressed));
				showFragment(4);
				menuButton.setVisibility(View.INVISIBLE);
				break;
			case R.id.menu:
				if (myPopupWindow == null) {
					myPopupWindow = new MyPopupWindow(mContext,
							umSocialService, true);
				}
				myPopupWindow.showMenuWindow(menuButton, shareInterface);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 由切换夜间模式监听器调用
	 * 
	 * @param isNight
	 */
	public void nightModeSwitch(boolean isNight) {
		int actionbarColor = 0;
		int textColor = 0;
		if (isNight) {
			actionbarColor = getResources().getColor(R.color.actionbar_night);
			textColor = getResources().getColor(R.color.text_night);
			menuButton.setImageResource(R.drawable.menu_night);
		} else {
			actionbarColor = getResources().getColor(R.color.actionbar_day);
			textColor = getResources().getColor(R.color.text_day);
			menuButton.setImageResource(R.drawable.menu);
		}
		View tb = findViewById(R.id.titlebar);
		if ((tb != null) && (actionbarColor != 0)) {
			tb.setBackgroundColor(actionbarColor);
		}

		TextView title = (TextView) findViewById(R.id.titlebar_text);
		title.setTextColor(textColor);

		View navBar = findViewById(R.id.nav_bar);
		navBar.setBackgroundColor(actionbarColor);

		removeFragment();
	}

	public void removeFragment() {
		FragmentManager fragmentManager = getFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		// 先移除已经存在的Fragment
		if (homePageFragment != null) {
			transaction.remove(homePageFragment);
			homePageFragment = null;
		}
		if (articlePageFragment != null) {
			transaction.remove(articlePageFragment);
			articlePageFragment = null;
		}
		if (picturePageFragment != null) {
			transaction.remove(picturePageFragment);
			picturePageFragment = null;
		}
		transaction.commit();
	}
}
