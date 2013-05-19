package com.hack.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class ImageViewActivity extends Activity {
	private ImageView iv_img;
	private TextView tv_loc;
	private TextView tv_time;
	private TextView tv_desc;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.imageview);
		
		Bundle p = this.getIntent().getExtras();

		iv_img = (ImageView)findViewById(R.id.dlg_img);
		tv_loc = (TextView)findViewById(R.id.dlg_loc);
		tv_time = (TextView)findViewById(R.id.dlg_time);
		tv_desc = (TextView)findViewById(R.id.dlg_desc);
				
		iv_img.setImageResource(p.getInt("image"));
		tv_loc.setText(p.getString("location"));
		tv_time.setText(p.getString("time") );
		tv_desc.setText(p.getString("desc"));
		
	}
}
