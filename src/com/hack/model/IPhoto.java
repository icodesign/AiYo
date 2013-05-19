package com.hack.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class IPhoto {
	public Calendar calendar;
	public String URL;
	
	public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	public IPhoto(String time, String url){
		calendar = Calendar.getInstance();
		try {
			calendar.setTime(sdf.parse(time));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		this.URL = url;
	}
	
	public String getURL(){
		return URL;
	}
	
	public Calendar getTime(){
		return calendar;
	}
}
