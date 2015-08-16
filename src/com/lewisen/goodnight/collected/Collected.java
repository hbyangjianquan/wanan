package com.lewisen.goodnight.collected;

import java.io.Serializable;

/**
 * 用来传递收藏对象的类
 * 
 * @author Lewisen
 * 
 */
public class Collected implements Serializable {

	private static final long serialVersionUID = 1L;

	String type;
	int id;
	String date;
	String title;
	String author;
	String text;
	String authorIntro;
	String imageSrc;
	String musicTitle;
	String musicAuthor;
	String musicSrc;
	String musicImage;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMusicImage() {
		return musicImage;
	}

	public void setMusicImage(String musicImage) {
		this.musicImage = musicImage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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

	public String getAuthorIntro() {
		return authorIntro;
	}

	public void setAuthorIntro(String authorIntro) {
		this.authorIntro = authorIntro;
	}

	public String getImageSrc() {
		return imageSrc;
	}

	public void setImageSrc(String imageSrc) {
		this.imageSrc = imageSrc;
	}

	public String getMusicTitle() {
		return musicTitle;
	}

	public void setMusicTitle(String musicTitle) {
		this.musicTitle = musicTitle;
	}

	public String getMusicAuthor() {
		return musicAuthor;
	}

	public void setMusicAuthor(String musicAuthor) {
		this.musicAuthor = musicAuthor;
	}

	public String getMusicSrc() {
		return musicSrc;
	}

	public void setMusicSrc(String musicSrc) {
		this.musicSrc = musicSrc;
	}

}
