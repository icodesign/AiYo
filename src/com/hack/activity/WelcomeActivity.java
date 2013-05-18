package com.hack.activity;

import com.hack.core.Hack;

import android.os.Bundle;
import android.util.DisplayMetrics;


/**
 * 欢迎屏
 * @author lee
 *
 */
public class WelcomeActivity extends BaseActivity{
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenMatrix();
    }

	private void getScreenMatrix() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);   
		Hack.SCREEN_HEIGHT_PIXEL = dm.heightPixels;
		Hack.SCREEN_WIDTH_PIXEL = dm.widthPixels;
	}
}
