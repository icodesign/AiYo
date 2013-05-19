package com.hack.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.hack.activity.ImageViewActivity;
import com.hack.activity.R;

public class MarkOverlay extends ItemizedOverlay<OverlayItem> {

	private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
	private Context mContext;
 
	private double mLat1 = 39.88227;
	private double mLon1 = 116.40660;
 
	private double mLat2 = 40.01026;
	private double mLon2 = 116.29299;
 
	private double mLat3 = 39.9119;
	private double mLon3 = 116.3910;

	private String locs[] = new String[] {
			"天坛", "圆明园", "故宫"
	};
	private String times[] = new String[] {
			"5月1日", "5月3日", "5月2日"
	};
	private String descs[] = new String[] {
			"真好玩！", "勿忘国耻>_<.....", "依然雄伟。"
	};
	private int imgs[] = new int[] {
			R.drawable.demo1, R.drawable.demo2, R.drawable.demo3
	};
	
 
	public MarkOverlay(Drawable marker, Context context) {
		super(marker);
		this.mContext = context;
 
	}
	
	public void update() {
			GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
			GeoPoint p2 = new GeoPoint((int) (mLat2 * 1E6), (int) (mLon2 * 1E6));
			GeoPoint p3 = new GeoPoint((int) (mLat3 * 1E6), (int) (mLon3 * 1E6));
	 
			GeoList.add(new OverlayItem(p1, "P1", "point1"));
			GeoList.add(new OverlayItem(p2, "P2", "point2"));
			GeoList.add(new OverlayItem(p3, "P3", "point3"));
			
			populate();
	}
 
	@Override
	protected OverlayItem createItem(int i) {
		return GeoList.get(i);
	}
 
	@Override
	public int size() {
		return GeoList.size();
	}
 
	@Override
	protected boolean onTap(int i) {

		Intent intent = new Intent();
		intent.setClass(mContext, ImageViewActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		Bundle myBundle = new Bundle();
		myBundle.putString("location", locs[i]);
		myBundle.putString("time", times[i]);
		myBundle.putString("desc", descs[i]);
		myBundle.putInt("image", imgs[i]);
		intent.putExtras(myBundle);
		mContext.startActivity(intent);
		
		return true;
	}
}
