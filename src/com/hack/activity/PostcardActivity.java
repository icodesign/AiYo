package com.hack.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.hack.core.Hack;
import com.hack.view.MyViewPager.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;

public class PostcardActivity extends BaseActivity {
	String url;
	ImageView postCard;
	ImageButton sendPostcard;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.postcard);
		//initDownloadPic();
		Intent intent = getIntent();
		url = intent.getStringExtra("URL");
		postCard = (ImageView) findViewById(R.id.imageView_postcard);
		sendPostcard = (ImageButton) findViewById(R.id.imageButton_sendPostcard);
		initView();
	}
	
	private void initView() {
		Handler handler = new Handler(){

			
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				imageLoader.displayImage((String)msg.obj, postCard, Hack.options, animateFirstListener);
			}
			
			
		};
		Hack.downloadPhoto(this, url, handler);
		
		
		final AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("发送明信片给您的好友吧~")
		.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				Intent intent = new Intent(android.content.Intent.ACTION_SEND); 
				startActivity(Intent.createChooser(intent, "朋友的祝福"));
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		sendPostcard.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				builder.show();
			}
		});
	}

	private void initDownloadPic() {
		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
		.cacheInMemory()
        .cacheOnDisc()
        .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
		.defaultDisplayImageOptions(defaultOptions)
		.build();
		ImageLoader.getInstance().init(config);		
	}
}
