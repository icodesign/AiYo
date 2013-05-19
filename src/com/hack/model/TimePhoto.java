package com.hack.model;

public class TimePhoto {

	public int time;
	public String[] imagesURL;
	public int count;
	public boolean isTime;
	
	public static final int ImageNumPerLine = 3;
	
	public TimePhoto(int time) {
		super();
		this.time = time;
		isTime = true;
	}
	
	public TimePhoto(String imageUrl) {
		super();
		imagesURL = new String[ImageNumPerLine];
		imagesURL[0] = imageUrl;
		isTime = false;
		count = 1;
	}
	
	public void addPhoto(String imageUrl){
		if(count  >= 3)
			return;
		this.imagesURL[count++] = imageUrl;
	}
	
	public Boolean isFull(){
		return ImageNumPerLine == count;
	}
}
