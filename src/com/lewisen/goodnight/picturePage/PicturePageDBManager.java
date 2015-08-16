package com.lewisen.goodnight.picturePage;

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

public class PicturePageDBManager extends OperateDB {
	private PageDBHelper helper;
	private SQLiteDatabase db;

	public PicturePageDBManager(Context context) {
		helper = PageDBHelper.getInstance(context);
		try {
			db = helper.getWritableDatabase();
		} catch (SQLiteException e) {
			e.printStackTrace();
			db = helper.getReadableDatabase();
		}
	}

	@Override
	public Object saveToDb(JSONObject jsonObj, int id) {
		// TODO Auto-generated method stub
		return saveJsonObjToDB(jsonObj, id);
	}

	@Override
	public Object getFromDb(int pageID) {
		// TODO Auto-generated method stub
		return getPicturePage(pageID);
	}

	@Override
	public Object getFromDbUseId(int id) {
		// TODO Auto-generated method stub
		return getPicturePageCollected(id);
	}

	@Override
	public void deleteFromDb(int id) {
		// TODO Auto-generated method stub
		deletePicturePage(id);
	}

	/**
	 * 保存接收自服务器的json数据
	 * 
	 * @param jsonObj
	 * @param id
	 */
	public PicturePage saveJsonObjToDB(JSONObject jsonObj, int id) {
		PicturePage picturePage = new PicturePage();
		picturePage.setId(id);
		try {
			// picturePage.setAuthor(jsonObj.getString("author"));
			picturePage.setAuthorIntro(jsonObj.getString("authorIntro"));
			picturePage.setDate(jsonObj.getString("date"));
			picturePage.setPicturePageID(jsonObj.getInt("picturePageID"));
			picturePage.setReadCount(jsonObj.getInt("readCount"));
			picturePage.setText(jsonObj.getString("text"));
			// picturePage.setTitle(jsonObj.getString("title"));
			picturePage.setImageSrc(jsonObj.getString("image"));

			try {
				add(picturePage);
			} catch (SQLException e) {
				// System.out.println("存储数据库 该id已存在  更新存储内容");
				updatePicturePage(picturePage);
			}
			return picturePage;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param picturePage
	 */
	public void add(PicturePage picturePage) throws SQLException {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO picturePage VALUES(?, ?, ?, ?, null, null, ?, ? ,?)",
					new Object[] { picturePage.getId(),
							picturePage.getPicturePageID(),
							picturePage.getDate(), picturePage.getReadCount(),
							/* picturePage.getTitle(), picturePage.getAuthor(), */
							picturePage.getText(),
							picturePage.getAuthorIntro(),
							picturePage.getImageSrc() });
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * @param picturePage
	 */
	public void updatePicturePage(PicturePage picturePage) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put("picturePageID", picturePage.getPicturePageID());
		cv.put("date", picturePage.getDate());
		cv.put("readCount", picturePage.getReadCount());
		// cv.put("title", picturePage.getTitle());
		// cv.put("author", picturePage.getAuthor());
		cv.put("text", picturePage.getText());
		cv.put("authorIntro", picturePage.getAuthorIntro());
		cv.put("imageSrc", picturePage.getImageSrc());
		db.update("picturePage", cv, "_id = ?",
				new String[] { String.valueOf(picturePage.getId()) });
	}

	/**
	 * @param
	 */
	public void deletePicturePage(int id) {
		db.delete("picturePage", "_id = ?", new String[] { String.valueOf(id) });
	}

	public PicturePage getPicturePage(int picturePageID) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery(
				"SELECT * FROM picturePage WHERE picturePageID=?",
				new String[] { String.valueOf(picturePageID) });
		PicturePage picturePage = new PicturePage();
		if (c.moveToNext()) {
			picturePage.setId(c.getInt(c.getColumnIndex("_id")));
			picturePage.setPicturePageID(picturePageID);
			picturePage.setDate(c.getString(c.getColumnIndex("date")));
			picturePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			// picturePage.setTitle(c.getString(c.getColumnIndex("title")));
			// picturePage.setAuthor(c.getString(c.getColumnIndex("author")));
			picturePage.setText(c.getString(c.getColumnIndex("text")));
			picturePage.setAuthorIntro(c.getString(c
					.getColumnIndex("authorIntro")));
			picturePage.setImageSrc(c.getString(c.getColumnIndex("imageSrc")));
		}

		if (picturePage.getId() == 0) {
			return null;
		} else {
			return picturePage;
		}
	}

	public PicturePage getPicturePageCollected(int id) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery(
				"SELECT * FROM picturePage WHERE _id=?",
				new String[] { String.valueOf(id) });
		PicturePage picturePage = new PicturePage();
		if (c.moveToNext()) {
			picturePage.setId(id);
			picturePage.setPicturePageID(c.getInt(c
					.getColumnIndex("picturePageID")));
			picturePage.setDate(c.getString(c.getColumnIndex("date")));
			picturePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			// picturePage.setTitle(c.getString(c.getColumnIndex("title")));
			// picturePage.setAuthor(c.getString(c.getColumnIndex("author")));
			picturePage.setText(c.getString(c.getColumnIndex("text")));
			picturePage.setAuthorIntro(c.getString(c
					.getColumnIndex("authorIntro")));
			picturePage.setImageSrc(c.getString(c.getColumnIndex("imageSrc")));
		}

		if (picturePage.getPicturePageID() == 0) {
			return null;
		} else {
			return picturePage;
		}
	}

	/**
	 * close database
	 */
	// public void closeDB() {
	// db.close();
	// }
}
