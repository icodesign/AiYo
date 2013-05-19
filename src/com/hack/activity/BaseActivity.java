package com.hack.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

/**
 * 所有Activity的父类，用来定义所有activity的共有行为，如makeToast
 * @author lee
 *
 */
public class BaseActivity extends Activity{
	
	
	/**
	 * 显示toast
	 * @param msg
	 */
	public void makeToast(String msg){
		Toast.makeText(getApplicationContext(), msg,Toast.LENGTH_SHORT).show();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		((HackApplication)getApplication()).mLocClient.stop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((HackApplication)getApplication()).mLocClient.start();
	}
	
	
	
	
}
