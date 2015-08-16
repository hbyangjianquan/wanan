package com.lewisen.goodnight.homePage;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.lewisen.goodnight.DisplayImage;
import com.lewisen.goodnight.MaxID;
import com.lewisen.goodnight.MyServer;
import com.lewisen.goodnight.PageLoad;
import com.lewisen.goodnight.R;
import com.lewisen.goodnight.cache.SaveImage;
import com.lewisen.goodnight.collected.CollectedManager;
import com.lewisen.goodnight.like.Like;
import com.lewisen.goodnight.like.LikeManager;
import com.lewisen.goodnight.mainview.MainView;
import com.lewisen.goodnight.mainview.Share;
import com.lewisen.goodnight.mainview.ShareInterface;
import com.lewisen.goodnight.player.PlayerControl;
import com.umeng.analytics.MobclickAgent;

/**
 * @author Lewisen
 * 
 */
public class HomePageFragment extends Fragment implements ShareInterface {
	private View homePageView = null;// 该fragment界面
	private ViewPager viewPager = null;// 页卡内容
	private List<View> listViews = null; // Tab页面列表
	private Handler mHandler = null;
	LayoutInflater mInflater = null;
	private DisplayImage displayImage = null;
	private MaxID maxID = null;
	private HomePageDBManager homePageDBManager = null;
	private MyPagerAdapter myPagerAdapter = null;
	private PageLoad pageLoad = null;
	private int currentMaxId = 0;// 当前的最大ID
	private boolean netState = true;// 网络的状态 true 可以连接网络 ； false 网络不通
	private static boolean stopThread = false;// 当前界面不显示时，要停止网络加载的线程发送message，否则空指针
	private int loadedPage = 0;// 本次启动已经从网络加载的页面最大值
	private LikeManager likeManager = null;
	private final String mPageName = "HomePage";

	private PlayerControl playerControl;
	boolean[] pageViewState = { false, false, false, false, false, false,
			false, false, false, false, false };// 布局显示状态
												// false为没有加载到布局,确保布局里面不会重复添加子view

	@Override
	public void onCreate(Bundle savedInstanceState) {
		stopThread = false;
		displayImage = new DisplayImage();
		pageLoad = new PageLoad();
		maxID = new MaxID();
		homePageDBManager = new HomePageDBManager(getActivity());
		likeManager = new LikeManager(getActivity());

		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();
		MobclickAgent.onPageStart(mPageName);
	}

	@Override
	public void onPause() {
		super.onPause();
		MobclickAgent.onPageEnd(mPageName);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("进入 HomePageFragment onCreateView");
		mInflater = inflater;
		viewPagerInit(inflater, container);
		mHanderInit();
		// 获取本地存储的最大主页id
		currentMaxId = maxID.getMaxIdFromProperties(getActivity(),
				"homePageMaxId", 0);
		return homePageView;
	}

	@Override
	public void onDestroy() {
		// displayImage.cleanCache();
		stopThread = true;// 注意! 禁止网络线程发送handler

		for (int i = 0; i < pageViewState.length; i++) {
			pageViewState[i] = false;
		}
		if (playerControl != null) {
			playerControl.stopMusic();
		}
		// Log.d("DEBUG", "onDestroy");
		super.onDestroy();
	}

	/**
	 * viewPager页面切换监听器
	 * 
	 * @author Lewisen
	 * 
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			// System.out.println("page 选择" + arg0);
			if (arg0 == 0) {
				// 获取更新Id
				maxID.saveMaxIdToProper(getActivity(), mHandler);
				viewPager.setCurrentItem(1);
				Toast.makeText(getActivity(), "正在更新...", Toast.LENGTH_SHORT)
						.show();
			} else if (arg0 == (listViews.size() - 1)) {
				viewPager.setCurrentItem((listViews.size() - 2));
				Toast.makeText(getActivity(), "没有更多内容了", Toast.LENGTH_SHORT)
						.show();
			}
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {

		}

		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}
	}

	/**
	 * handler初始化
	 */
	private void mHanderInit() {

		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				HomePage homePage = null;
				netState = true;// 默认网络是通畅的
				if (!stopThread) {
					if ((msg.what > 0) && (msg.what < 11)) {
						homePage = (HomePage) msg.obj;
						if (homePage != null) {
							homePageDisplay(homePage, listViews.get(msg.what));
							// 在内容显示完毕后 加载likebar
							likeManager.getLikeCountFromNet(mPageName
									+ homePage.getHomePageID(), mHandler,
									stopThread, msg.what);
						}
					} else {
						switch (msg.what) {
						case MaxID.GET_ID_SUCCESS:
							int maxId = currentMaxId;// 暂时存储
							currentMaxId = maxID.getMaxIdFromProperties(
									getActivity(), "homePageMaxId", 0);

							if (maxId == currentMaxId) {
								Toast.makeText(getActivity(), "已是最新内容了",
										Toast.LENGTH_LONG).show();
							} else {
								// 手动更新页面 会调用instantiateItem
								myPagerAdapter.notifyDataSetChanged();
							}
							// 限制联网加载 当页面已经加载到第二页时，再返回刷新才可以重新联网
							if (loadedPage > 2) {
								loadedPage = 0;// 手动刷新 清空已经加载的页面值
								// 手动更新页面 会调用instantiateItem
								myPagerAdapter.notifyDataSetChanged();
							}

							break;
						case PageLoad.GET_RESOURCE_ERR:
							netState = false;
							myPagerAdapter.notifyDataSetChanged();// 手动更新页面
							Toast.makeText(getActivity(), "获取更新失败",
									Toast.LENGTH_LONG).show();
							break;
						case LikeManager.GET_LIKE_SUCCESS:
							Like like = (Like) msg.obj;

							likeManager.disLikeState(like,
									listViews.get(like.getPage()));

							break;
						}

					}
				}
				super.handleMessage(msg);
			}
		};
	}

	/**
	 * ViewPager适配器
	 */
	public class MyPagerAdapter extends PagerAdapter {
		public List<View> mListViews;

		public MyPagerAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		// 从当前container中删除指定位置（position arg1）的View;
		public void destroyItem(View arg0, int arg1, Object arg2) {
			((ViewPager) arg0).removeView(mListViews.get(arg1));
			// System.out.println("destroyItem position " + arg1);
		}

		@Override
		// 返回要滑动的VIew的个数
		public int getCount() {
			return mListViews.size();
		}

		@Override
		// 作用 第一：将当前视图添加到container中，第二：返回当前View
		public Object instantiateItem(View arg0, int arg1) {

			pageLoad.pageUpdateControl(currentMaxId, arg1, netState, mHandler,
					stopThread, homePageDBManager, MyServer.HOME_PAGE,
					loadedPage);

			if (arg1 > loadedPage) { // 保存已经加载的页面最大值
				loadedPage = arg1;
			}

			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public int getItemPosition(Object object) {
			// return super.getItemPosition(object);
			// 返回NONE是为了手动更新界面
			return POSITION_NONE;
		}

	}

	/**
	 * 页面内容更新
	 * 
	 * @param homePage
	 *            要显示的内容
	 * @param view
	 */
	private void homePageDisplay(HomePage homePage, View view) {

		TextView date = (TextView) view.findViewById(R.id.date_home);
		TextView readCount = (TextView) view.findViewById(R.id.readcount_home);
		TextView title = (TextView) view.findViewById(R.id.title_home);
		TextView author = (TextView) view.findViewById(R.id.author_home);

		TextView authorIntro = (TextView) view
				.findViewById(R.id.author_intro_home);
		ImageView eye = (ImageView) view.findViewById(R.id.eye_home);
		View topLine = (View) view.findViewById(R.id.line_home);
		View bottomLine = (View) view.findViewById(R.id.line_bottom_home);
		ProgressBar loading = (ProgressBar) view
				.findViewById(R.id.loading_bar_home);
		loading.setVisibility(View.GONE);
		eye.setVisibility(View.VISIBLE);
		topLine.setVisibility(View.VISIBLE);
		bottomLine.setVisibility(View.VISIBLE);

		date.setText(homePage.getDate());
		readCount.setText(homePage.getReadCount() + "");
		title.setText(homePage.getTitle());
		author.setText(homePage.getAuthor());
		authorIntro.setText(homePage.getAuthorIntro());
		// 显示图文的线性布局
		LinearLayout imageTextLayout = (LinearLayout) view
				.findViewById(R.id.layout_image_text_home);
		// 先移除可能已经绘制的界面
		// imageTextLayout.removeAllViews();

		// 第一次加载该页面
		if (!pageViewState[homePage.getId()]) {

			// 图文分割显示 、播放器显示
			imageTextDisplay(homePage, imageTextLayout);
			pageViewState[homePage.getId()] = true;
		}
		// 加载完主内容后，显示出likebar的布局
		LinearLayout likeBar = (LinearLayout) view
				.findViewById(R.id.like_bar_home);
		likeBar.setVisibility(View.VISIBLE);
	}

	/**
	 * 音乐播放器bar
	 * 
	 * @param imageTextLayout
	 */
	private void musicPlayer(LinearLayout imageTextLayout, HomePage homePage) {

		// 如果homePage对象中没有music数据，则获取的String为null
		String title = homePage.getMusicTitle();
		String author = homePage.getMusicAuthor();
		String musicURL = homePage.getMusicURL();
		String musicImage = homePage.getMusicImage();

		// Log.d("DEBUG", "  musicTitle=" + (title == null) + "   musicAuthor="
		// + author + "   musicURL=" + musicURL);
		// 为null,跳转，不显示
		if ((title == null) || (author == null) || (musicURL == null)) {
			return;
		}
		RelativeLayout musicView = (RelativeLayout) mInflater.inflate(
				R.layout.music_view, imageTextLayout, false);
		SeekBar seekBar = (SeekBar) musicView.findViewById(R.id.music_progress);
		TextView musicTimeText = (TextView) musicView
				.findViewById(R.id.music_time);
		ImageButton playButton = (ImageButton) musicView
				.findViewById(R.id.music_button);
		TextView musicTitle = (TextView) musicView
				.findViewById(R.id.music_title);
		TextView musicAuthor = (TextView) musicView
				.findViewById(R.id.music_author);
		if (musicImage != null) {
			ImageView musicIcon = (ImageView) musicView
					.findViewById(R.id.music_icon);
			displayImage.displayImage(musicIcon, musicImage);
		}

		musicTitle.setText(title);
		musicAuthor.setText(author);
		// 播放音乐初始化
		MainView mainView = (MainView) this.getActivity();
		playerControl = mainView.getPlayContler();
		playerControl.playerControlInit(seekBar, musicTimeText, playButton,
				musicURL, getActivity());
		// 添加音乐播放界面到主视图
		imageTextLayout.addView(musicView);
	}

	/**
	 * 图文分割显示
	 * 
	 * @param homePage
	 * @param imageTextLayout
	 */
	private void imageTextDisplay(HomePage homePage,
			LinearLayout imageTextLayout) {
		String[] imageSrcPart = homePage.getImageSrc().split("###");
		String[] textPart = homePage.getText().split("###");

		RelativeLayout imageText = null;

		int textLength = textPart.length;// 文本分割为几个部分，从1开始
		// int imageLength = imageSrcPart.length;
		// 7.28去掉对主页图文分割必须一致的要求，改为以文分割为主
		// if (textLength == imageLength) {
		for (int i = 0; i < textLength; i++) {
			// 加载一个布局
			imageText = (RelativeLayout) mInflater.inflate(R.layout.image_text,
					imageTextLayout, false);
			ImageView imageView = (ImageView) imageText
					.findViewById(R.id.picture_image_text);

			TextView textView = (TextView) imageText
					.findViewById(R.id.text_image_text);
			textView.setVisibility(View.VISIBLE);
			final String imageSrc = imageSrcPart[i];
			//对图片进行判断
			if ((imageSrc != null) && (!imageSrc.isEmpty())) {
				imageView.setVisibility(View.VISIBLE);
				displayImage.displayImage(imageView, imageSrc);
				imageView.setOnLongClickListener(new OnLongClickListener() {

					@Override
					public boolean onLongClick(View v) {
						SaveImage saveImage = new SaveImage();
						String path = saveImage.saveImage(displayImage
								.getImageCachePath(imageSrc));
						if (path != null) {
							Toast.makeText(getActivity(), "图片已保存到" + path,
									Toast.LENGTH_LONG).show();
						}
						return true;
					}
				});
			}

			// 如果有@@@ 代表这里需要播放器 插入在图文显示的前面
			if (textPart[i].contains("@@@")) {
				musicPlayer(imageTextLayout, homePage);// 插入音乐播放器
				textView.setText(textPart[i].replace("@@@", ""));
			} else {
				textView.setText(textPart[i]);
			}

			imageTextLayout.addView(imageText);

			// 注释掉的为 在插入@@@的地方插入播放器。现在使用的是，在有音乐播放的界面的最上端显示
			// 检查内容中是否有@@@
			// String[] text = textPart[i].split("@@@");
			// int length = text.length;
			// 如果有@@@
			// if (length == 2) {
			// textView.setText(text[0]);
			// // 显示@@@以上的内容
			// imageTextLayout.addView(imageText);
			// // 插入播放器界面
			// musicPlayer(imageTextLayout, homePage);
			// // 接着显示@@@以下的文字内容
			// imageText = (RelativeLayout) mInflater.inflate(
			// R.layout.image_text, imageTextLayout, false);
			// ImageView imageView1 = (ImageView) imageText
			// .findViewById(R.id.picture_image_text);
			// imageView1.setVisibility(View.GONE);// 不可见
			// TextView textView1 = (TextView) imageText
			// .findViewById(R.id.text_image_text);
			// textView1.setVisibility(View.VISIBLE);
			// textView1.setText(text[1]);
			// imageTextLayout.addView(imageText);
			// } else {
			// textView.setText(textPart[i]);
			// imageTextLayout.addView(imageText);
			// }

			// }
		}
	}

	private void viewPagerInit(LayoutInflater inflater, ViewGroup container) {
		homePageView = inflater.inflate(R.layout.viewpaper, container, false);
		viewPager = (ViewPager) homePageView.findViewById(R.id.viewpaper);
		// 将要分页显示的View装入list中
		listViews = new ArrayList<View>();
		listViews.add(inflater.inflate(R.layout.blank_viewpaper, null));
		// 添加十个页面
		for (int i = 0; i < 10; i++) {
			listViews.add(inflater.inflate(R.layout.home_page, null));
		}
		listViews.add(inflater.inflate(R.layout.blank_viewpaper, null));
		myPagerAdapter = new MyPagerAdapter(listViews);
		viewPager.setAdapter(myPagerAdapter);
		// 设置当前从1开始 0位空白的viewpaper
		viewPager.setCurrentItem(1);
		viewPager.setOnPageChangeListener(new MyOnPageChangeListener());
	}

	@Override
	public Share shareContent(boolean isCollected) {
		Share share = new Share();
		// 获取当前页面显示的文章ID
		int pageID = currentMaxId + 1 - viewPager.getCurrentItem();
		// 从数据库获取当前显示的内容
		share.setTargetUrl(MyServer.SHARE_URL + "type=1&id=" + pageID);
		share.setName(mPageName + pageID);
		HomePage homePage = homePageDBManager.getHomePage(pageID);
		if (homePage != null) {
			share.setTitle(homePage.getTitle());
			int len = homePage.getText().length();
			if (len > 50) {
				len = 50;
			}
			share.setContent(homePage.getText().substring(0, len)
					.replace("###", "").replace("@@@", ""));// 分享截取前50字

			// 如果是添加收藏
			if (isCollected) {
				CollectedManager manager = new CollectedManager(getActivity());
				// 如果当前页面没有被收藏过，那么获取的值为默认的
				if (manager.getIdFromProperties(share.getName()) == CollectedManager.DEFAULT_COUNT) {
					int id = manager
							.getIdFromProperties(CollectedManager.HOME_C);
					homePage.setId(id);
					try {
						homePageDBManager.add(homePage);
					} catch (SQLException e) {
						homePageDBManager.deleteFromDb(id);
						try {
							homePageDBManager.add(homePage);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					// 用来记录当前存储的数量
					manager.saveIdToProperties(CollectedManager.HOME_C, id + 1);
					// 用来记录当前保存的名称
					manager.saveIdToProperties(share.getName(), id + 1);

					share.setName("success");
				} else {
					share.setName("haved");
				}
			}
		}
		return share;
	}
}
