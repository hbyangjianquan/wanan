package com.lewisen.goodnight;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PageDBHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "goodNight.db";
	private static final int DATABASE_VERSION = 1;
	private static PageDBHelper mInstance = null;

	public static PageDBHelper getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new PageDBHelper(context);
		}
		return mInstance;
	}

	private PageDBHelper(Context context) {
		// CursorFactory设置为null,使用默认值
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// 数据库第一次被创建时onCreate会被调用
	@Override
	public void onCreate(SQLiteDatabase db) {
		// 删除数据表
		// db.execSQL("DROP TABLE homePage");
		// db.execSQL("DROP TABLE articlePage");
		// db.execSQL("DROP TABLE picturePage");

		db.execSQL("CREATE TABLE IF NOT EXISTS homePage"
				+ "(_id INTEGER PRIMARY KEY, homePageID INTEGER, date VARCHAR, readCount INTEGER, title VARCHAR, author VARCHAR, text TEXT, authorIntro VARCHAR, imageSrc VARCHAR, musicAuthor VARCHAR, musicTitle VARCHAR, musicURL VARCHAR, musicImage VARCHAR)");

		db.execSQL("CREATE TABLE IF NOT EXISTS articlePage"
				+ "(_id INTEGER PRIMARY KEY, articlePageID INTEGER, date VARCHAR, readCount INTEGER, title VARCHAR, author VARCHAR, text TEXT, authorIntro VARCHAR, comment VARCHAR)");

		db.execSQL("CREATE TABLE IF NOT EXISTS picturePage"
				+ "(_id INTEGER PRIMARY KEY, picturePageID INTEGER, date VARCHAR, readCount INTEGER, title VARCHAR, author VARCHAR, text TEXT, authorIntro VARCHAR, imageSrc VARCHAR)");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("ALTER TABLE homePage ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE articlePage ADD COLUMN other STRING");
		db.execSQL("ALTER TABLE picturePage ADD COLUMN other STRING");
	}

}
