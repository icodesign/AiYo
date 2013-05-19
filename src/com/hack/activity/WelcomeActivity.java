package com.hack.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.hack.core.Hack;


/**
 * 欢迎屏
 * @author lee
 * 
 *
 */
public class WelcomeActivity extends Activity{
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getScreenMatrix();
        
        Intent intent = new Intent(WelcomeActivity.this, IndexActivity.class);  
		startActivity(intent);
		this.finish();
		
    }

	private void getScreenMatrix() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);   
		Hack.SCREEN_HEIGHT_PIXEL = dm.heightPixels;
		Hack.SCREEN_WIDTH_PIXEL = dm.widthPixels;
	}
}
