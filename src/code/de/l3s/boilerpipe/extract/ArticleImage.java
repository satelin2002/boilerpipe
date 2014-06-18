package de.l3s.boilerpipe.extract;
public class ArticleImage {
	private String url;
	private int width;
	private int height;
	private int area;
	public int getArea() {
		return area;
	}
	public void setArea(int area) {
		this.area = area;
	}
	private int main = 0;
	public int getMain() {
		return main;
	}
	public void setMain(int main) {
		this.main = main;
	}
	public ArticleImage() {
		
	}
	
	public ArticleImage(String url, int width,  int height, int area) {
		this.url = url;
		this.height = height;
		this.width = width;
		this.area = area;
	}

	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
}
