package com.lewisen.goodnight;

/**
 * È«¾Ö
 *
 **/
import android.app.Application;
import android.content.Context;

import com.lewisen.goodnight.cache.AppConfig;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class MyApplication extends Application {
	public static Context mContext;
	public static AppConfig appConfig;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();

		ImageLoaderConfiguration imageConfig = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(4 * 1024 * 1024)
				.diskCacheSize(30 * 1024 * 1024)
				.diskCacheFileNameGenerator(new Md5FileNameGenerator())
				.diskCacheFileCount(100)
				.imageDownloader(
						new BaseImageDownloader(getApplicationContext(),
								5 * 1000, 20 * 1000)).writeDebugLogs().build();
		ImageLoader.getInstance().init(imageConfig);

		mContext = getApplicationContext();
		appConfig = new AppConfig(mContext);
	}

}
