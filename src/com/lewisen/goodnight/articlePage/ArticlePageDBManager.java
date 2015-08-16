package com.lewisen.goodnight.articlePage;

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

public class ArticlePageDBManager extends OperateDB {
	private PageDBHelper helper;
	private SQLiteDatabase db;

	public ArticlePageDBManager(Context context) {
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
		return getArticlePage(pageID);
	}

	@Override
	public Object getFromDbUseId(int id) {
		// TODO Auto-generated method stub
		return getArticlePageCollected(id);
	}

	@Override
	public void deleteFromDb(int id) {
		// TODO Auto-generated method stub
		deleteArticlePage(id);
	}

	/**
	 * 保存接收自服务器的json数据
	 * 
	 * @param jsonObj
	 * @param id
	 */
	public ArticlePage saveJsonObjToDB(JSONObject jsonObj, int id) {
		ArticlePage articlePage = new ArticlePage();
		articlePage.setId(id);
		try {
			articlePage.setAuthor(jsonObj.getString("author"));
			articlePage.setAuthorIntro(jsonObj.getString("authorIntro"));
			articlePage.setDate(jsonObj.getString("date"));
			articlePage.setArticlePageID(jsonObj.getInt("articlePageID"));
			articlePage.setReadCount(jsonObj.getInt("readCount"));
			articlePage.setText(jsonObj.getString("text"));
			articlePage.setTitle(jsonObj.getString("title"));
			// articlePage.setComment(jsonObj.getString("comment"));暂时没有加入文章评论

			try {
				add(articlePage);
			} catch (SQLException e) {
				// System.out.println("存储数据库 该id已存在  更新存储内容");
				updateHomePage(articlePage);
			}
			return articlePage;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param articlePage
	 */
	public void add(ArticlePage articlePage) throws SQLException {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		db.beginTransaction(); // 开始事务
		try {
			db.execSQL(
					"INSERT INTO articlePage VALUES(?, ?, ?, ?, ?, ?, ?, ? ,null)",
					new Object[] { articlePage.getId(),
							articlePage.getArticlePageID(),
							articlePage.getDate(), articlePage.getReadCount(),
							articlePage.getTitle(), articlePage.getAuthor(),
							articlePage.getText(), articlePage.getAuthorIntro() });// articlePage.getComment()
			db.setTransactionSuccessful(); // 设置事务成功完成
		} finally {
			db.endTransaction(); // 结束事务
		}
	}

	/**
	 * @param articlePage
	 */
	public void updateHomePage(ArticlePage articlePage) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return;
		}
		ContentValues cv = new ContentValues();
		cv.put("articlePageID", articlePage.getArticlePageID());
		cv.put("date", articlePage.getDate());
		cv.put("readCount", articlePage.getReadCount());
		cv.put("title", articlePage.getTitle());
		cv.put("author", articlePage.getAuthor());
		cv.put("text", articlePage.getText());
		cv.put("authorIntro", articlePage.getAuthorIntro());
		// cv.put("comment", articlePage.getComment());
		db.update("articlePage", cv, "_id = ?",
				new String[] { String.valueOf(articlePage.getId()) });
	}

	/**
	 * @param homePage
	 */
	public void deleteArticlePage(int id) {
		db.delete("articlePage", "_id = ?", new String[] { String.valueOf(id) });
	}

	public ArrayList<ArticlePage> query() {
		ArrayList<ArticlePage> articlePages = new ArrayList<ArticlePage>();
		Cursor c = db.rawQuery("SELECT * FROM articlePage", null);
		while (c.moveToNext()) {
			ArticlePage articlePage = new ArticlePage();
			articlePage.setId(c.getInt(c.getColumnIndex("_id")));
			articlePage.setArticlePageID(c.getInt(c
					.getColumnIndex("articlePageID")));
			articlePage.setDate(c.getString(c.getColumnIndex("date")));
			articlePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			articlePage.setTitle(c.getString(c.getColumnIndex("title")));
			articlePage.setAuthor(c.getString(c.getColumnIndex("author")));
			articlePage.setText(c.getString(c.getColumnIndex("text")));
			articlePage.setAuthorIntro(c.getString(c
					.getColumnIndex("authorIntro")));
			// articlePage.setComment(c.getString(c.getColumnIndex("comment")));
			articlePages.add(articlePage);
		}
		c.close();
		return articlePages;
	}

	public ArticlePage getArticlePage(int articlePageID) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery(
				"SELECT * FROM articlePage WHERE articlePageID=?",
				new String[] { String.valueOf(articlePageID) });
		ArticlePage articlePage = new ArticlePage();
		if (c.moveToNext()) {
			articlePage.setId(c.getInt(c.getColumnIndex("_id")));
			articlePage.setArticlePageID(articlePageID);
			articlePage.setDate(c.getString(c.getColumnIndex("date")));
			articlePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			articlePage.setTitle(c.getString(c.getColumnIndex("title")));
			articlePage.setAuthor(c.getString(c.getColumnIndex("author")));
			articlePage.setText(c.getString(c.getColumnIndex("text")));
			articlePage.setAuthorIntro(c.getString(c
					.getColumnIndex("authorIntro")));
			// articlePage.setComment(c.getString(c.getColumnIndex("comment")));
		}
		if (articlePage.getId() == 0) {
			// System.out.println("数据库内容空=" + articlePageID);
			return null;
		} else {
			return articlePage;
		}
	}

	public ArticlePage getArticlePageCollected(int id) {
		if (!db.isOpen()) {// 如果数据库已经关闭，则不操作
			return null;
		}
		Cursor c = db.rawQuery("SELECT * FROM articlePage WHERE _id=?",
				new String[] { String.valueOf(id) });
		ArticlePage articlePage = new ArticlePage();
		if (c.moveToNext()) {
			articlePage.setId(id);
			articlePage.setArticlePageID(c.getInt(c
					.getColumnIndex("articlePageID")));
			articlePage.setDate(c.getString(c.getColumnIndex("date")));
			articlePage.setReadCount(c.getInt(c.getColumnIndex("readCount")));
			articlePage.setTitle(c.getString(c.getColumnIndex("title")));
			articlePage.setAuthor(c.getString(c.getColumnIndex("author")));
			articlePage.setText(c.getString(c.getColumnIndex("text")));
			articlePage.setAuthorIntro(c.getString(c
					.getColumnIndex("authorIntro")));
			// articlePage.setComment(c.getString(c.getColumnIndex("comment")));
		}
		if (articlePage.getArticlePageID() == 0) {
			// System.out.println("数据库内容空=" + articlePageID);
			return null;
		} else {
			return articlePage;
		}
	}

	/**
	 * close database
	 */
	// public void closeDB() {
	// db.close();
	// }
}
