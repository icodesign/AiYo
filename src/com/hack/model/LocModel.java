package com.hack.model;

import com.baidu.location.BDLocation;

public class LocModel {
	
	public static BDLocation loc = new BDLocation(116.404, 39.915, 0);
	
	public static void update(BDLocation loc) {
		//Location.loc.setAddrStr(loc.getAddrStr()); // BUG!!!! set but can't get!
		LocModel.loc.mAddr = loc.mAddr;
		LocModel.loc.setAltitude(loc.getAltitude());
		LocModel.loc.setCoorType(loc.getCoorType());
		LocModel.loc.setDerect(loc.getDerect());
		LocModel.loc.setLatitude(loc.getLatitude());
		LocModel.loc.setLocType(loc.getLocType());
		LocModel.loc.setLongitude(loc.getLongitude());
		LocModel.loc.setRadius(loc.getRadius());
		LocModel.loc.setSatelliteNumber(loc.getSatelliteNumber());
		LocModel.loc.setSpeed(loc.getSpeed());
		LocModel.loc.setTime(loc.getTime());
	}
	
	public static String getLat() {
		return LocModel.loc.getLatitude()+"";
	}
	
	public static String getLng() {
		return LocModel.loc.getLongitude()+"";
	}
	
	public static String getProvince() {
		return LocModel.loc.getProvince();
	}
	
	public static String getCity() {
		return LocModel.loc.getCity();
	}

}