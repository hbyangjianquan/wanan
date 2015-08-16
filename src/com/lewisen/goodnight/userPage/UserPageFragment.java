package com.lewisen.goodnight.userPage;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;

import com.lewisen.goodnight.MyApplication;
import com.lewisen.goodnight.R;
import com.lewisen.goodnight.collected.CollectedActivity;
import com.lewisen.goodnight.mainview.MainView;
import com.umeng.fb.FeedbackAgent;

public class UserPageFragment extends Fragment {

	private View userPageView = null;
	private Button feedBackButton = null;
	private Button aboutButton = null;
	private Button modeButton = null;
	private Button collectedButton = null;
	private CheckBox modeCheckBox = null;
	private boolean isNight;
	private List<Button> listButtons;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewInit(inflater, container);

		return userPageView;
	}

	private void viewInit(LayoutInflater inflater, ViewGroup container) {
		userPageView = inflater.inflate(R.layout.userpage, container, false);
		feedBackButton = (Button) userPageView
				.findViewById(R.id.feedback_button_user);
		aboutButton = (Button) userPageView
				.findViewById(R.id.about_button_user);
		modeButton = (Button) userPageView
				.findViewById(R.id.nightmode_button_user);
		collectedButton = (Button) userPageView
				.findViewById(R.id.collected_button_user);
		modeCheckBox = (CheckBox) userPageView.findViewById(R.id.check_user);

		listButtons = new ArrayList<Button>();
		listButtons.add(aboutButton);
		listButtons.add(collectedButton);
		listButtons.add(feedBackButton);
		listButtons.add(modeButton);

		MyButtonListener buttonListener = new MyButtonListener();
		feedBackButton.setOnClickListener(buttonListener);
		aboutButton.setOnClickListener(buttonListener);
		modeButton.setOnClickListener(buttonListener);
		collectedButton.setOnClickListener(buttonListener);

		modeCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				nightModeChange(isChecked);
			}
		});

		isNight = MyApplication.appConfig.getNightModeSwitch();
		modeCheckBox.setChecked(isNight);

	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	// 夜间模式设置
	private void nightModeChange(boolean isChecked) {
		int textColor = 0;
		int bgColor = 0;

		isNight = isChecked;
		MyApplication.appConfig.setNightModeSwitch(isNight);
		MainView mainView = (MainView) getActivity();

		ImageView modeImage = (ImageView) userPageView
				.findViewById(R.id.nightmode_user);
		ImageView collectedImage = (ImageView) userPageView
				.findViewById(R.id.collected_user);
		ImageView feedbackImage = (ImageView) userPageView
				.findViewById(R.id.feedback_user);
		ImageView infoImage = (ImageView) userPageView
				.findViewById(R.id.about_user);

		if (isNight) {
			mainView.setTheme(R.style.NightTheme);
			textColor = getResources().getColor(R.color.text_night);
			bgColor = getResources().getColor(R.color.bg_night);
			modeImage.setImageResource(R.drawable.nightmode_night);
			collectedImage.setImageResource(R.drawable.collected_night);
			feedbackImage.setImageResource(R.drawable.feedback_night);
			infoImage.setImageResource(R.drawable.info_night);
		} else {
			mainView.setTheme(R.style.DayTheme);
			textColor = getResources().getColor(R.color.text_day);
			bgColor = getResources().getColor(R.color.bg_day);
			modeImage.setImageResource(R.drawable.nightmode);
			collectedImage.setImageResource(R.drawable.collected);
			feedbackImage.setImageResource(R.drawable.feedback);
			infoImage.setImageResource(R.drawable.info);
		}
		mainView.nightModeSwitch(isNight);
		userPageView.setBackgroundColor(bgColor);
		for (Button button : listButtons) {
			button.setTextColor(textColor);
		}

	}

	class MyButtonListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.feedback_button_user:
				FeedbackAgent fb = new FeedbackAgent(getActivity());
				fb.closeAudioFeedback();
				fb.startFeedbackActivity();
				break;

			case R.id.nightmode_button_user:
				modeCheckBox.setChecked(!isNight);
				break;

			case R.id.about_button_user:
				Intent intent = new Intent(getActivity(), AboutMe.class);
				startActivity(intent);
				getActivity().overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
				break;

			case R.id.collected_button_user:
				MainView mainView = (MainView) getActivity();
				mainView.removeFragment();//移除当前已经加载的视图 播放器等
				Intent intent1 = new Intent(getActivity(), CollectedActivity.class);
				startActivity(intent1);
				getActivity().overridePendingTransition(android.R.anim.fade_in,
						android.R.anim.fade_out);
				break;

			default:
				break;
			}
		}

	}
}
