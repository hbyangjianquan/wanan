package com.lewisen.goodnight.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.Environment;

/**
 * ±£´æImageViewÍ¼Æ¬Êý¾Ý
 * 
 * @author Lewisen
 * 
 */
public class SaveImage {

	public static String getSDPath() {
		boolean hasSDCard = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
		if (hasSDCard) {
			return Environment.getExternalStorageDirectory().toString()
					+ "/DCIM/saved_picture";
		} else
			return "/data/data/com.lewisen.goodnight/saved_picture";
	}

	public String saveImage(File imageFile) {
		String strPath = getSDPath();
		String strFileName = "xxxx";
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
		Date curDate = new Date(System.currentTimeMillis());
		strFileName = formatter.format(curDate);

		File destDir = new File(strPath);
		if (!destDir.exists()) {
			destDir.mkdirs();
		}

		if (imageFile == null) {
			return null;
		}
		try {
			FileInputStream inputStream = new FileInputStream(imageFile);
			FileOutputStream fos = new FileOutputStream(strPath + "/"
					+ strFileName + ".jpg");

			byte[] buffer = new byte[1024];
			int length;
			while ((length = inputStream.read(buffer)) != -1) {
				fos.write(buffer, 0, length);
			}
			fos.flush();
			fos.close();
			inputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			strPath = null;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			strPath = null;
		}
		return strPath;
	}

}
