package com.lewisen.goodnight.collected;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.lewisen.goodnight.MyApplication;
import com.lewisen.goodnight.OperateDB;
import com.lewisen.goodnight.R;
import com.lewisen.goodnight.articlePage.ArticlePage;
import com.lewisen.goodnight.articlePage.ArticlePageDBManager;
import com.lewisen.goodnight.homePage.HomePage;
import com.lewisen.goodnight.homePage.HomePageDBManager;
import com.lewisen.goodnight.picturePage.PicturePage;
import com.lewisen.goodnight.picturePage.PicturePageDBManager;

public class CollectedActivity extends Activity {
	private final int OK = 1;
	private ListView listView;
	private ImageButton backButton;
	private CollectedManager collectedManager;
	private int homeCount = 0;
	private int articleCount = 0;
	private int pictureCount = 0;
	private Handler mHandler = null;
	private ProgressBar loading;
	private List<HomePage> homePages;
	private List<ArticlePage> articlePages;
	private List<PicturePage> picturePages;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		if (MyApplication.appConfig.getNightModeSwitch()) {
			this.setTheme(R.style.NightTheme);
		} else {
			this.setTheme(R.style.DayTheme);
		}
		// 自定义标题
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.collected);
		// 设置标题为某个layout
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.titlebar);
		context = this;
		collectedManager = new CollectedManager(context);
		handlerInit();
		init();

	}

	private void handlerInit() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);

				// Log.d("DEBUG", " handler" + msg.what);
				switch (msg.what) {
				case OK:
					loading.setVisibility(View.GONE);
					// 子线程读取成功 在主线程显示内容
					listViewInit();

					if ((homeCount == 0) && (articleCount == 0)
							&& (pictureCount == 0)) {
						findViewById(R.id.collect_text).setVisibility(
								View.VISIBLE);
					}
					// Log.d("DEBUG", "homeId = " + homeCount);
					// Log.d("DEBUG", "articleId = " + articleCount);
					// Log.d("DEBUG", "pictureId = " + pictureCount);

					break;

				default:
					break;
				}

			}

		};
	}

	private void init() {
		backButton = (ImageButton) findViewById(R.id.back);
		loading = (ProgressBar) findViewById(R.id.loading_collected);
		backButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		backButton.setVisibility(View.VISIBLE);

		listView = (ListView) findViewById(R.id.listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 行数从0开始

				// Log.d("DEBUG", " type =" + getTypeCollected(position));

				transObj(position);

			}

		});

		listView.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view,
					int position, long id) {

				String type = getTypeCollected(position);
				int collId = 0;
				String typeId;
				String key;
				OperateDB db;
				if ("HomePage".equals(type)) {
					int size = homePages.size();
					if (position > size) {
						return true;
					}
					HomePage homePage = homePages.get(position);
					collId = homePage.getId();
					key = CollectedManager.HOME_C;
					typeId = "HomePage" + homePage.getHomePageID();
					db = new HomePageDBManager(context);

				} else if ("ArticlePage".equals(type)) {
					int size = articlePages.size();
					int pos = position - homeCount;
					if (pos > size) {
						return true;
					}
					ArticlePage articlePage = articlePages.get(pos);
					collId = articlePage.getId();
					key = CollectedManager.ART_C;
					typeId = "ArticlePage" + articlePage.getArticlePageID();
					db = new ArticlePageDBManager(context);

				} else if ("PicturePage".equals(type)) {
					int size = picturePages.size();
					int pos = position - homeCount - articleCount;
					if (pos > size) {
						return true;
					}
					PicturePage picturePage = picturePages.get(pos);
					collId = picturePage.getId();
					key = CollectedManager.PIC_C;
					typeId = "ArticlePage" + picturePage.getPicturePageID();
					db = new PicturePageDBManager(context);
				} else {
					return true;
				}

				collectedManager.deleteCollected(collId, key, typeId, db);
				Toast.makeText(context, "已删除该收藏", Toast.LENGTH_LONG).show();
				// 重新加载内容
				readDbDateThread(mHandler);

				return true;// 不再触发ClickListener
			}

		});

		loading.setVisibility(View.VISIBLE);
		readDbDateThread(mHandler);

	}

	/**
	 * 向activity传输对象
	 * 
	 * @param position
	 */
	private void transObj(int position) {
		String type = getTypeCollected(position);
		Collected collected = new Collected();
		collected.setType(type);
		if ("HomePage".equals(type)) {
			int size = homePages.size();
			if (position > size) {
				return;
			}
			HomePage homePage = homePages.get(position);
			collected.setId(homePage.getHomePageID());
			collected.setDate(homePage.getDate());
			collected.setTitle(homePage.getTitle());
			collected.setAuthor(homePage.getAuthor());
			collected.setText(homePage.getText());
			collected.setAuthorIntro(homePage.getAuthorIntro());
			collected.setImageSrc(homePage.getImageSrc());
			collected.setMusicAuthor(homePage.getMusicAuthor());
			collected.setMusicSrc(homePage.getMusicURL());
			collected.setMusicImage(homePage.getMusicImage());
			collected.setMusicTitle(homePage.getMusicTitle());

		} else if ("ArticlePage".equals(type)) {
			int size = articlePages.size();
			int pos = position - homeCount;
			if (pos > size) {
				return;
			}
			ArticlePage articlePage = articlePages.get(pos);
			collected.setId(articlePage.getArticlePageID());
			collected.setDate(articlePage.getDate());
			collected.setTitle(articlePage.getTitle());
			collected.setAuthor(articlePage.getAuthor());
			collected.setText(articlePage.getText());
			collected.setAuthorIntro(articlePage.getAuthorIntro());
		} else if ("PicturePage".equals(type)) {
			int size = picturePages.size();
			int pos = position - homeCount - articleCount;
			if (pos > size) {
				return;
			}
			PicturePage picturePage = picturePages.get(pos);
			collected.setId(picturePage.getPicturePageID());
			collected.setDate(picturePage.getDate());
			collected.setImageSrc(picturePage.getImageSrc());
			collected.setText(picturePage.getText());
			collected.setAuthorIntro(picturePage.getAuthorIntro());
		}

		Intent intent = new Intent(context, CollectedDisplayActivity.class);
		Bundle bundle = new Bundle();
		bundle.putSerializable("collected", collected);
		intent.putExtras(bundle);
		startActivity(intent);
		((Activity) context).overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);
	}

	/**
	 * 获取当前位置类型
	 * 
	 * @param position
	 * @return
	 */
	public String getTypeCollected(int position) {
		String type;
		if ((position < homeCount) && (homeCount != 0)) {
			type = "HomePage";
		} else if ((position < (homeCount + articleCount))
				&& (articleCount != 0)) {
			type = "ArticlePage";
		} else if (pictureCount != 0) {
			type = "PicturePage";
		} else {
			type = "err";
		}

		return type;
	}

	/**
	 * 读取数据库内容线程
	 * 
	 * @param mHandler
	 */
	void readDbDateThread(final Handler mHandler) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				readDb();
				mHandler.obtainMessage(OK).sendToTarget();
			}
		}).start();
	}

	/**
	 * 读取数据库内容 在子线程操作
	 */
	private void readDb() {
		HomePageDBManager homePageDBManager = new HomePageDBManager(context);
		HomePage homePage = null;
		homePages = collectedManager.queryDb(
				collectedManager.getIdFromProperties(CollectedManager.HOME_C),
				homePageDBManager, homePage);

		ArticlePageDBManager articlePageDBManager = new ArticlePageDBManager(
				context);
		ArticlePage articlePage = null;
		articlePages = collectedManager.queryDb(
				collectedManager.getIdFromProperties(CollectedManager.ART_C),
				articlePageDBManager, articlePage);

		PicturePageDBManager picturePageDBManager = new PicturePageDBManager(
				context);
		PicturePage picturePage = null;
		picturePages = collectedManager.queryDb(
				collectedManager.getIdFromProperties(CollectedManager.PIC_C),
				picturePageDBManager, picturePage);

		if (homePages != null) {
			homeCount = homePages.size();
		}
		if (articlePages != null) {
			articleCount = articlePages.size();
		}
		if (picturePages != null) {
			pictureCount = picturePages.size();
		}
	}

	/**
	 * listview初始化
	 */
	private void listViewInit() {
		ArrayList<HashMap<String, String>> listItem = new ArrayList<HashMap<String, String>>();

		if (homePages != null) {

			for (HomePage homePage : homePages) {
				HashMap<String, String> map = new HashMap<String, String>();
				int len = homePage.getText().length();
				len = (len > 17) ? 17 : len;
				map.put("text",
						homePage.getText().replace("###", "")
								.replace("@@@", "").replace(" ", "")
								.replace("\r\n", "").substring(0, len)
								+ "...");
				map.put("title", homePage.getTitle());

				listItem.add(map);
			}
		}
		if (articlePages != null) {
			for (ArticlePage articlePage : articlePages) {
				HashMap<String, String> map = new HashMap<String, String>();
				int len = articlePage.getText().length();
				len = (len > 17) ? 17 : len;
				map.put("text", articlePage.getText().replace("###", "")
						.replace("@@@", "").replace(" ", "")
						.replace("\r\n", "").substring(0, len)
						+ "...");
				map.put("title", "文章｜" + articlePage.getTitle());
				listItem.add(map);
			}
		}

		if (picturePages != null) {
			for (PicturePage picturePage : picturePages) {
				HashMap<String, String> map = new HashMap<String, String>();
				int len = picturePage.getText().length();
				map.put("title", "图｜晚安阅读");
				len = (len > 17) ? 17 : len;
				map.put("text", picturePage.getText().replace("###", "")
						.replace("@@@", "").replace(" ", "")
						.replace("\r\n", "").substring(0, len)
						+ "...");
				listItem.add(map);
			}
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listItem,
				R.layout.listview, new String[] { "title", "text" }, new int[] {
						R.id.title, R.id.text });
		listView.setAdapter(simpleAdapter);

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

}
