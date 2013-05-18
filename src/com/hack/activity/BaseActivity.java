package com.hack.activity;

import android.app.Activity;
import android.widget.Toast;

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
	
}
