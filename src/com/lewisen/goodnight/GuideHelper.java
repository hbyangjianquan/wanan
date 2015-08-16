package com.lewisen.goodnight;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.lewisen.goodnight.ScrollLayout.PageEndListener;

/**
 * @Description: 引导图助手类
 * @author yanzw
 * @date 2012-11-30上午11:12:46
 */
public class GuideHelper {
	private Activity context;
	private ViewGroup rootLayout;
	private ScrollLayout scrollLayout;
	private int[] guideResIds = { R.drawable.guide_1, R.drawable.guide_2, };
	private static final String GUIDE_VERSION_NAME = "GUIDEVERSION";
	private static final int GUIDE_VERSION_CODE = 3;// 升级版本时，如再需要引导界面，则需要修改版本

	public GuideHelper(Context context) {
		this.context = (Activity) context;
		if (guideCheck()) {
			createGuideLayout();
			initGuideView();
		}
	}

	/**
	 * @Description: 创建引导图层
	 * @param @return
	 * @return ViewGroup
	 * @throws
	 */
	private void createGuideLayout() {
		ViewGroup rootView = (ViewGroup) context.getWindow().getDecorView();
		LayoutInflater lf = context.getLayoutInflater();
		rootLayout = (ViewGroup) lf.inflate(R.layout.guide_helper, null);
		scrollLayout = (ScrollLayout) rootLayout
				.findViewById(R.id.scroll_layout);
		rootView.addView(rootLayout);
	}

	/**
	 * @Description: 初始化引导视图
	 * @param
	 * @return void
	 * @throws
	 */
	public void initGuideView() {
		for (int resId : guideResIds) {
			scrollLayout.addView(makeGuideView(resId));
		}

		scrollLayout.setPageEndListener(new PageEndListener() {

			@Override
			public void scrollEnd() {
				closeGuide();
			}

		});
	}

	/**
	 * @Description: 生成每个引导视图
	 * @param @param resId
	 * @param @return
	 * @return View
	 * @throws
	 */
	public View makeGuideView(int resId) {
		ImageView guideView = new ImageView(context);
		guideView.setImageResource(resId);
		guideView.setPadding(0, 0, 0, 0);
		guideView.setScaleType(ScaleType.FIT_XY);
		return guideView;
	}

	/**
	 * @Description: 打开引导层
	 * @param
	 * @return void
	 * @throws
	 */
	public void openGuide() {
		if (guideCheck()) {
			rootLayout.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * @Description: 关闭引导层
	 * @param
	 * @return void
	 * @throws
	 */
	public void closeGuide() {

		AlphaAnimation alphaAnim = new AlphaAnimation(1.0f, 0.2f);
		alphaAnim.setDuration(500);
		ScaleAnimation scaleAnim = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		scaleAnim.setDuration(500);
		AnimationSet AnimSet = new AnimationSet(false);
		AnimSet.setDuration(500);
		AnimSet.addAnimation(scaleAnim);
		AnimSet.addAnimation(alphaAnim);

		AnimSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				rootLayout.clearAnimation();
				rootLayout.setVisibility(View.GONE);
				saveGuideVersion();
			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

		});

		rootLayout.startAnimation(AnimSet);
	}

	/**
	 * @Description: 保存引导版本记录
	 * @param
	 * @return void
	 * @throws
	 */
	private void saveGuideVersion() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		Editor edit = sp.edit();
		edit.putInt(GUIDE_VERSION_NAME, GUIDE_VERSION_CODE);
		edit.commit();
	}

	/**
	 * @Description: 检测引导图版本，判断是否启动引导
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	private boolean guideCheck() {
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(context);
		int guideVer = sp.getInt(GUIDE_VERSION_NAME, 0);
		if (GUIDE_VERSION_CODE > 0 && GUIDE_VERSION_CODE > guideVer) {
			return true;
		} else {
			return false;
		}
	}
}
