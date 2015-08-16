package com.lewisen.goodnight.userPage;

import com.lewisen.goodnight.MyApplication;
import com.lewisen.goodnight.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

public class AboutMe extends Activity {

	private ImageButton backButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 显示夜间 日间模式
		if (MyApplication.appConfig.getNightModeSwitch()) {
			this.setTheme(R.style.NightTheme);
		} else {
			this.setTheme(R.style.DayTheme);
		}

		// 自定义标题
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.about);
		// 设置标题为某个layout
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);

		backButton = (ImageButton) findViewById(R.id.back);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});

		backButton.setVisibility(View.VISIBLE);

	}

}
