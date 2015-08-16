package com.lewisen.goodnight;

/**
 * 存放与服务器相关的数据
 * 
 * @author Lewisen
 * 
 */
public class MyServer {
	public static final String URL = "http://182.92.9.95:8080/goodNightServer/";
	public static final String HOME_PAGE = "HomePageServlet";
	public static final String ARTICLE_PAGE = "ArticlePageServlet";
	public static final String PICTURE_PAGE = "PicturePageServlet";
	public static final String LIKE = "LikeServlet";
	public static final String SECOND_IMAGE = "SecondImageServlet";
	public static final String PICTURE_URL = URL + "PictureServlet?path=";
	public static final String SHARE_URL = URL + "ShareContent?";
	public static final String APP_ICON = PICTURE_URL + "C://pic//app.jpg";
}
