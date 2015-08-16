package com.lewisen.goodnight.homePage;


public class HomePage {
	private int id;
	private int homePageID;
	private String date;
	private String title;
	private String imageSrc;
	private String author;
	private String text;
	private int readCount;
	private String authorIntro;
	private String musicAuthor;
	private String musicTitle;
	private String musicURL;
	private String musicImage;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HomePage() {
		// TODO Auto-generated constructor stub
	}

	public int getHomePageID() {
		return homePageID;
	}
	public void setHomePageID(int homePageID) {
		this.homePageID = homePageID;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImageSrc() {
		return imageSrc;
	}
	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public int getReadCount() {
		return readCount;
	}
	public void setReadCount(int readCount) {
		this.readCount = readCount;
	}
	public String getAuthorIntro() {
		return authorIntro;
	}
	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}

	public String getMusicAuthor() {
		return musicAuthor;
	}

	public void setMusicAuthor(String musicAuthor) {
		this.musicAuthor = musicAuthor;
	}

	public String getMusicTitle() {
		return musicTitle;
	}

	public void setMusicTitle(String musicTitle) {
		this.musicTitle = musicTitle;
	}

	public String getMusicURL() {
		return musicURL;
	}

	public void setMusicURL(String musicURL) {
		this.musicURL = musicURL;
	}

	public String getMusicImage() {
		return musicImage;
	}

	public void setMusicImage(String musicImage) {
		this.musicImage = musicImage;
	}

	
}
