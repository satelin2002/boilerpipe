package de.l3s.boilerpipe.extract;

import java.util.ArrayList;

public class Article {
	private String url;
	private String title;
	private String description;
	private String keywords;
	private String authors;
	private String domain;
	private String content;
	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public ArrayList<ArticleImage> getImages() {
		return images;
	}

	public void setImages(ArrayList<ArticleImage> images) {
		this.images = images;
	}

	private ArrayList<ArticleImage> images;

	public Article(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getAuthors() {
		return authors;
	}

	public void setAuthors(String authors) {
		this.authors = authors;
	}

	public String getDomain() {
		return domain;
	}

	public void setDomain(String domain) {
		this.domain = domain;
	}

}
