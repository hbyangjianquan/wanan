package com.lewisen.goodnight.picturePage;

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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import com.lewisen.goodnight.mainview.Share;
import com.lewisen.goodnight.mainview.ShareInterface;
import com.umeng.analytics.MobclickAgent;

public class PicturePageFragment extends Fragment implements ShareInterface {
	private View picturePageView = null;
	private ViewPager viewPager = null;// 页卡内容
	private List<View> listViews = null; // Tab页面列表
	private Handler mHandler = null;
	private DisplayImage displayImage = null;
	private MaxID maxID = null;
	private MyPagerAdapter myPagerAdapter = null;
	private PicturePageDBManager picturePageDBManager = null;
	private PageLoad pageLoad = null;
	private int currentMaxId = 0;// 当前的最大ID
	private boolean netState = true;// 网络的状态 true 可以连接网络 ； false 网络不通
	private static boolean stopThread = false;
	private int loadedPage = 0;
	private LikeManager likeManager = null;
	private final String mPageName = "PicturePage";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		stopThread = false;
		displayImage = new DisplayImage();
		pageLoad = new PageLoad();
		maxID = new MaxID();
		picturePageDBManager = new PicturePageDBManager(getActivity());
		likeManager = new LikeManager(getActivity());
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// System.out.println("进入 PicturePageFragment onCreateView");
		viewPagerInit(inflater, container);
		mHandlerInit();
		// 获取本地存储的最大主页id
		currentMaxId = maxID.getMaxIdFromProperties(getActivity(),
				"picturePageMaxId", 0);

		return picturePageView;
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
	public void onDestroy() {
		stopThread = true;
		// picturePageDBManager.closeDB();
		super.onDestroy();
	}

	/**
	 * @author Lewisen
	 * 
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {

		@Override
		public void onPageSelected(int arg0) {
			// System.out.println("page 选择" + arg0);
			if (arg0 == 0) {
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
		public void onPageScrolled(int arg0, float arg1, int arg2) {

		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
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
					stopThread, picturePageDBManager, MyServer.PICTURE_PAGE,
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

	private void mHandlerInit() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				PicturePage picturePage = null;
				netState = true;// 默认网络是通畅的
				if (!stopThread) {
					if ((msg.what > 0) && (msg.what < 11)) {
						picturePage = (PicturePage) msg.obj;
						if (picturePage != null) {
							picturePageDisplay(picturePage,
									listViews.get(msg.what));
							likeManager.getLikeCountFromNet(mPageName
									+ picturePage.getPicturePageID(), mHandler,
									stopThread, msg.what);
						}
					} else {
						switch (msg.what) {
						case MaxID.GET_ID_SUCCESS:
							int maxId = currentMaxId;// 暂时存储
							currentMaxId = maxID.getMaxIdFromProperties(
									getActivity(), "picturePageMaxId", 0);

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
	 * 页面内容更新
	 * 
	 * @param picturePage
	 *            要显示的内容
	 * @param page
	 *            在哪个布局页面显示 1,2,3,
	 * @param view
	 */
	private void picturePageDisplay(PicturePage picturePage, View view) {
		TextView date = (TextView) view.findViewById(R.id.date_picture);
		TextView readCount = (TextView) view
				.findViewById(R.id.readcount_picture);
		TextView text = (TextView) view.findViewById(R.id.text_picture);
		TextView authorIntro = (TextView) view
				.findViewById(R.id.author_intro_picture);
		ImageView imageView = (ImageView) view
				.findViewById(R.id.picture_picture);

		ImageView eye = (ImageView) view.findViewById(R.id.eye_picture);
		View topLine = (View) view.findViewById(R.id.line_picture);
		View bottomLine = (View) view.findViewById(R.id.line_bottom_picture);
		ProgressBar loading = (ProgressBar) view
				.findViewById(R.id.loading_bar_picture);

		loading.setVisibility(View.GONE);
		eye.setVisibility(View.VISIBLE);
		topLine.setVisibility(View.VISIBLE);
		bottomLine.setVisibility(View.VISIBLE);

		final String imageSrc = picturePage.getImageSrc();
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
		date.setText(picturePage.getDate());
		readCount.setText(picturePage.getReadCount() + "");
		text.setText(picturePage.getText());
		authorIntro.setText(picturePage.getAuthorIntro());

		// 加载完主内容后，显示出likebar的布局
		LinearLayout likeBar = (LinearLayout) view
				.findViewById(R.id.like_bar_picture);
		likeBar.setVisibility(View.VISIBLE);
	}

	private void viewPagerInit(LayoutInflater inflater, ViewGroup container) {
		picturePageView = inflater
				.inflate(R.layout.viewpaper, container, false);
		viewPager = (ViewPager) picturePageView.findViewById(R.id.viewpaper);
		// 将要分页显示的View装入list中
		listViews = new ArrayList<View>();
		listViews.add(inflater.inflate(R.layout.blank_viewpaper, null));
		// 添加十个页面
		for (int i = 0; i < 10; i++) {
			listViews.add(inflater.inflate(R.layout.picture_page, null));
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
		share.setTargetUrl(MyServer.SHARE_URL + "type=3&id=" + pageID);
		share.setName(mPageName + pageID);
		// 从数据库获取当前显示的内容
		PicturePage picturePage = picturePageDBManager.getPicturePage(pageID);
		if (picturePage != null) {
			int len = picturePage.getText().length();
			if (len > 50) {
				len = 50;
			}
			share.setContent(picturePage.getText().substring(0, len));
			share.setTitle("图文--晚安阅读");

			// 如果是添加收藏
			if (isCollected) {
				CollectedManager manager = new CollectedManager(getActivity());
				// 如果当前页面没有被收藏过，那么获取的值为默认的
				if (manager.getIdFromProperties(share.getName()) == CollectedManager.DEFAULT_COUNT) {
					int id = manager
							.getIdFromProperties(CollectedManager.PIC_C);
					picturePage.setId(id);
					try {
						picturePageDBManager.add(picturePage);
					} catch (SQLException e) {
						picturePageDBManager.deleteFromDb(id);
						try {
							picturePageDBManager.add(picturePage);
						} catch (SQLException e1) {
							e1.printStackTrace();
						}
					}
					// 用来记录当前存储的数量
					manager.saveIdToProperties(CollectedManager.PIC_C, id + 1);
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
