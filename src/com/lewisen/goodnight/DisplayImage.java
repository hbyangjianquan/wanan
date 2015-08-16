package com.lewisen.goodnight;

import java.io.File;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

/**
 * 使用开源库universalimageloader
 * 
 * @author Lewisen
 * 
 */
public class DisplayImage {
	private ImageLoader imageLoader = null;
	DisplayImageOptions options;

	public DisplayImage() {
		initImageLoader();
	}

	public void displayImage(ImageView imageView, String image) {
		try {
			imageLoader.displayImage(MyServer.PICTURE_URL + image, imageView,
					options);
			// Log.d("DEBUG", "imageLoader= " + MyServer.PICTURE_URL + image);
		} catch (Exception e) {
			// System.out.println("displayImage 错误");
			e.printStackTrace();
		}
	}

	/**
	 * 获取缓存的图片文件
	 * 
	 * @param image
	 * @return
	 */
	public File getImageCachePath(String image) {

		try {
			return imageLoader.getDiskCache().get(MyServer.PICTURE_URL + image);
		} catch (Exception e) {
			// System.out.println("displayImage 错误");
			e.printStackTrace();
		}
		return null;
	}

	public void cleanCache() {
		imageLoader.clearMemoryCache();
		imageLoader.clearDiskCache();
	}

	private void initImageLoader() {
		imageLoader = ImageLoader.getInstance();
		options = new DisplayImageOptions.Builder().cacheInMemory(true)
				.cacheOnDisk(true)
				.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.displayer(new FadeInBitmapDisplayer(400)).build();
	}

}
