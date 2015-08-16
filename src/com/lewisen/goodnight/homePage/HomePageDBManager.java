package com.lewisen.goodnight.homePage;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.lewisen.goodnight.OperateDB;
import com.lewisen.goodnight.PageDBHelper;

public class HomePageDBManager extends OperateDB {
	private PageDBHelper helper;
	private SQLiteDatabase db;

	public HomePageDBManager(Context context) {
		helper = PageDBHelper.getInstance(context);
		// 因为getWritableDatabase内部调用了mContext.openOrCreateDatabase(mName, 0,
		// mFactory);
		// 所以要确保context已初始化,我们可以把实例化DBManager的步骤放在Activity的onCreate里
		try {
			db = helper.getWritableDatabase();
		} catch (SQLiteException e) {
			e.printStackTrace();
			db = helper.getReadableDatabase();
		}
	}

	@Override
	public Object saveToDb(JSONObject jsonObj, int id) {
		return saveJsonObjToDB(jsonObj, id);
	}

	@Override
	public Object getFromDb(int pageID) {
		// TODO Auto-generated method stub
		return getHomePage(pageID);
	}

	
	@Override
	public Object getFromDbUseId(int id) {
		// TODO Auto-generated method stub
		return getHomePageCollected(id);
	}

	@Override
	public void deleteFromDb(int id) {
		// TODO Auto-generated method stub
		deleteHomePage(id);
	}

	/**
	 * 保存接收自服务器的json数据 如果getString中的name没有的话，会抛出异常，可以在服务器数据库端设置为默认空字符串 而不是null
	 * 
	 * @param jsonObj
	 * @param id
	 */
	public HomePage saveJsonObjToDB(JSONObject jsonObj, int id) {
		HomePage homePage = new HomePage();
		try {
			homePage.setId(id);
			homePage.setAuthor(jsonObj.getString("author"));
			homePage.setAuthorIntro(jsonObj.getString("authorIntro"));
			homePage.setDate(jsonObj.getString("date"));
			homePage.setHomePageID(jsonObj.getInt("homePageID"));
			homePage.setImageSrc(jsonObj.getString("image"));
			homePage.setReadCount(jsonObj.getInt("readCount"));
			homePage.setText(jsonObj.getString("text"));
			homePage.setTitle(jsonObj.getString("title"));

			// 判断是否有音乐链接
			String musicURL = jsonObj.getString("musicURL");
			// Log.d("DEBUG", "musicURL.length()" + musicURL.length());
			if (musicURL.length() > 5) {
				homePage.setMusicURL(musicURL);
				homePage.setMusicAuthor(jsonObj.getString("musicAuthor"));
				homePage.setMusicTitle(jsonObj.getString("musicTitle"));
				homePage.setMusicImage(jsonObj.getString("musicImage"));
			}

			// 存储到本地数据库
			try {
				add(homePage);
			} catch (SQLException e) {
				// System.out.println("存储数据库 该id已存在  更新存储内容");
				updateHomePage(homePage);
			}
			return homePage;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param homePage
	 */
	public void add(HomePage homePage) throws SQLException {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO homePage VALUES(?, ?, ?, ?, ?, ?, ?, ? ,? ,? ,? ,? ,?)",
					new Object[] { homePage.getId(), homePage.getHomePageID(),
							homePage.getDate(), homePage.getReadCount(),
							homePage.getTitle(), homePage.getAuthor(),
							homePage.getText(), homePage.getAuthorIntro(),
							homePage.getImageSrc(), homePage.getMusicAuthor(),
							homePage.getMusicTitle(), homePage.getMusicURL(),
							homePage.getMusicImage() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * @param homePage
	 */
	public void updateHomePage(HomePage homePage) {
		// System.out.println("HomePageDBManager-->updateHomePage");
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put("homePageID", homePage.getHomePageID());
		cv.put("date", homePage.getDate());
		cv.put("readCount", homePage.getReadCount());
		cv.put("title", homePage.getTitle());
		cv.put("author", homePage.getAuthor());
		cv.put("text", homePage.getText());
		cv.put("authorIntro", homePage.getAuthorIntro());
		cv.put("imageSrc", homePage.getImageSrc());
		cv.put("musicAuthor", homePage.getMusicAuthor());
		cv.put("musicTitle", homePage.getMusicTitle());
		cv.put("musicURL", homePage.getMusicURL());
		cv.put("musicImage", homePage.getMusicImage());
		db.update("homePage", cv, "_id = ?",
				new String[] { String.valueOf(homePage.getId()) });
	}

	/**
	 * @param homePage
	 */
	public void deleteHomePage(int id) {
		db.delete("homePage", "_id = ?", new String[] { String.valueOf(id) });
	}

	public ArrayList<HomePage> query() {
		ArrayList<HomePage> homePages = new ArrayList<HomePage>();
		Cursor c = db.rawQuery("SELECT * FROM homePage", null);
		while (c.moveToNext()) {
			HomePage homePage = new HomePage();
			homePage.setId(c.getInt(c.getColumnIndex("_id")));
			homePage.setHomePageID(c.getInt(c.getColumnIndex("homePageID")));
			homePage.setDate(c.getString(c.getColumnIndex("date")));
			homePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			homePage.setTitle(c.getString(c.getColumnIndex("title")));
			homePage.setAuthor(c.getString(c.getColumnIndex("author")));
			homePage.setText(c.getString(c.getColumnIndex("text")));
			homePage.setAuthorIntro(c.getString(c.getColumnIndex("authorIntro")));
			homePage.setImageSrc(c.getString(c.getColumnIndex("imageSrc")));
			homePage.setMusicAuthor(c.getString(c.getColumnIndex("musicAuthor")));
			homePage.setMusicTitle(c.getString(c.getColumnIndex("musicTitle")));
			homePage.setMusicURL(c.getString(c.getColumnIndex("musicURL")));
			homePage.setMusicImage(c.getString(c.getColumnIndex("musicImage")));

			homePages.add(homePage);
		}
		c.close();
		return homePages;
	}

	/**
	 * 使用文章id获取对象
	 * 
	 * @param homePageID
	 * @return
	 */
	public HomePage getHomePage(int homePageID) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery("SELECT * FROM homePage WHERE homePageID=?",
				new String[] { String.valueOf(homePageID) });
		HomePage homePage = new HomePage();
		if (c.moveToNext()) {
			homePage.setId(c.getInt(c.getColumnIndex("_id")));
			homePage.setHomePageID(homePageID);
			homePage.setDate(c.getString(c.getColumnIndex("date")));
			homePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			homePage.setTitle(c.getString(c.getColumnIndex("title")));
			homePage.setAuthor(c.getString(c.getColumnIndex("author")));
			homePage.setText(c.getString(c.getColumnIndex("text")));
			homePage.setAuthorIntro(c.getString(c.getColumnIndex("authorIntro")));
			homePage.setImageSrc(c.getString(c.getColumnIndex("imageSrc")));
			homePage.setMusicAuthor(c.getString(c.getColumnIndex("musicAuthor")));
			homePage.setMusicTitle(c.getString(c.getColumnIndex("musicTitle")));
			homePage.setMusicURL(c.getString(c.getColumnIndex("musicURL")));
			homePage.setMusicImage(c.getString(c.getColumnIndex("musicImage")));
		}
		if (homePage.getId() == 0) {
			return null;
		} else {
			return homePage;
		}
	}

	/**
	 * 使用重定义id获取收藏的对象
	 * 
	 * @param id
	 * @return
	 */
	public HomePage getHomePageCollected(int id) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery("SELECT * FROM homePage WHERE _id=?",
				new String[] { String.valueOf(id) });
		HomePage homePage = new HomePage();
		if (c.moveToNext()) {
			homePage.setId(id);
			homePage.setHomePageID(c.getInt(c.getColumnIndex("homePageID")));
			homePage.setDate(c.getString(c.getColumnIndex("date")));
			homePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			homePage.setTitle(c.getString(c.getColumnIndex("title")));
			homePage.setAuthor(c.getString(c.getColumnIndex("author")));
			homePage.setText(c.getString(c.getColumnIndex("text")));
			homePage.setAuthorIntro(c.getString(c.getColumnIndex("authorIntro")));
			homePage.setImageSrc(c.getString(c.getColumnIndex("imageSrc")));
			homePage.setMusicAuthor(c.getString(c.getColumnIndex("musicAuthor")));
			homePage.setMusicTitle(c.getString(c.getColumnIndex("musicTitle")));
			homePage.setMusicURL(c.getString(c.getColumnIndex("musicURL")));
			homePage.setMusicImage(c.getString(c.getColumnIndex("musicImage")));
		}
		if (homePage.getHomePageID() == 0) {
			return null;
		} else {
			return homePage;
		}
	}
	/**
	 * close database
	 */
	// public void closeDB() {
	// db.close();
	// }

}
