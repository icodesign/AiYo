package com.hack.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hack.core.Hack;
import com.hack.model.IPhoto;
import com.hack.model.TimePhoto;
import com.hack.utils.HttpClient;
import com.hack.view.MyViewPager.AnimateFirstDisplayListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public class WaterfallActivity extends BaseActivity {
	
	List<IPhoto> photos;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	protected ImageLoader imageLoader = ImageLoader.getInstance();
	View headView;
	ListView timePhotos_ListView;
	MyAdapter myAdapter;
	
	List<TimePhoto> timePhotos;
	Calendar lastTime;
	String galleryid;
	int i;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		galleryid = getIntent().getExtras().getString("galleryid");
		setContentView(R.layout.travel_detail);
		timePhotos = new ArrayList<TimePhoto>();
		photos = new ArrayList<IPhoto>();
		headView = ((LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.travel_detail_head, null);
		timePhotos_ListView = (ListView)findViewById(R.id.listView_photos);
		timePhotos_ListView.addHeaderView(headView);
		myAdapter = new MyAdapter();
		timePhotos_ListView.setAdapter(myAdapter);
		initPhotos();
	}

	private void initPhotos() {
		// TODO Auto-generated method stub
		RequestParams params = new RequestParams();
		params.put("galleryid", galleryid);
		HttpClient.get(Hack.GET_PTOTO_URL, params, new AsyncHttpResponseHandler(){

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				System.out.println(arg0);
				JSONObject jo;
				try {
					jo = new JSONObject(arg0);
					JSONObject jGallery = jo.getJSONObject("gallerydata");
					initHeader(jGallery);
					JSONArray jphotos = jo.getJSONArray("photodata");
					IPhoto p;
					for (int i =0;i<jphotos.length();i++){
						jo = jphotos.getJSONObject(i);
						p=new IPhoto(jo.getString("recordTime"), jo.getString("photoid"));
						photos.add(p);
					}
					handlePhotoStream();
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
				
		});
	}

	protected void initHeader(JSONObject jGallery) throws JSONException {
		// TODO Auto-generated method stub
		final ImageView img = (ImageView)headView.findViewById(R.id.image_cover);
		TextView textView_travel_name = (TextView)headView.findViewById(R.id.textView_travel_namec);
		textView_travel_name.setText(jGallery.getString("galleryid"));
		TextView textView_travel_beginTime = (TextView)headView.findViewById(R.id.textView_travel_beginTime);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat time=new SimpleDateFormat("MM月dd日"); 
		try {
			String timeStart = time.format(sdf.parse(jGallery.getString("timestart")));
			String timeEnd = time.format(sdf.parse(jGallery.getString("timeend")));
			textView_travel_beginTime.setText(timeStart+"-"+timeEnd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Handler handler = new Handler(){

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				imageLoader.displayImage((String)msg.obj, img, Hack.options, animateFirstListener);
			}
			
		};
		Hack.downloadPhoto(this, jGallery.getString("coverurl"), handler);
	}

	private void handlePhotoStream() {
		
		if(photos == null || photos.size() == 0)
			return;
		lastTime = Calendar.getInstance();
		lastTime.set(1900, 0, 0);
		int dayNum = 1;
		for(int i = 0; i < photos.size(); i++){
			//buggy
			if(photos.get(i).getTime().get(Calendar.DAY_OF_YEAR) != lastTime.get(Calendar.DAY_OF_YEAR)){
				timePhotos.add(new TimePhoto(dayNum));
				timePhotos.add(new TimePhoto(photos.get(i).getURL()));
				dayNum++;
				lastTime = (Calendar) photos.get(i).getTime().clone();
			}
			else {
				if(timePhotos.get(timePhotos.size() - 1).isFull())
					timePhotos.add(new TimePhoto(photos.get(i).getURL()));
				else 
					timePhotos.get(timePhotos.size() - 1).addPhoto(photos.get(i).getURL());
			}
		}
		myAdapter.notifyDataSetChanged();
	}
	
	class Dataholder{
		TextView time;
		ImageView photo[];
		LinearLayout linearLayout;
		public Dataholder(){
			this.photo = new ImageView[3];
		}
	}

	class MyAdapter extends BaseAdapter{

		LayoutInflater mInflater;
		public MyAdapter(){
			this.mInflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
		
		@Override
		public int getCount() {
			return timePhotos.size();
		}

		@Override
		public Object getItem(int position) {
			return timePhotos.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Dataholder holder = null;
			if(convertView == null){
				convertView = mInflater.inflate(R.layout.travel_detail_photos_item, null);
				holder = new  Dataholder();
				holder.time = (TextView)convertView.findViewById(R.id.textView_photo_time);
				holder.linearLayout = (LinearLayout) convertView.findViewById(R.id.linearLayout_photos);
				holder.photo[0] = (ImageView)convertView.findViewById(R.id.imageView_photot1);
				holder.photo[1] = (ImageView)convertView.findViewById(R.id.imageView_photot2);
				holder.photo[2] = (ImageView)convertView.findViewById(R.id.imageView_photot3);
				convertView.setTag(holder);     
			}else{
				holder = (Dataholder)convertView.getTag();
			}
			
			final TimePhoto timePhoto = timePhotos.get(position);
			
			if(timePhoto.isTime){
				holder.time.setText("第" + String.valueOf(timePhoto.time) + "天");
				holder.linearLayout.setVisibility(View.GONE);
				for (int i = 0; i < holder.photo.length; i++) {
					holder.photo[i].setVisibility(View.GONE);
				}
			}
			else{
				holder.time.setVisibility(View.GONE);
				for (i = 0; i < timePhotos.get(position).count; i++) {
					if(timePhoto.imagesURL[i] == null || timePhoto.imagesURL[i].equals("") || timePhoto.imagesURL[i].equals("null"))
						continue;
					holder.photo[i].setVisibility(View.VISIBLE);
					final String url = timePhoto.imagesURL[i];
					final ImageView finalPhoto= holder.photo[i];
					finalPhoto.setOnClickListener(new View.OnClickListener() {
						
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(WaterfallActivity.this, PostcardActivity.class);
							intent.putExtra("URL", url);
							startActivity(intent);
						}
					});
					Handler handler = new Handler(){

						@Override
						public void handleMessage(Message msg) {
							// TODO Auto-generated method stub
							super.handleMessage(msg);
							imageLoader.displayImage((String)msg.obj, finalPhoto, Hack.options, animateFirstListener);
//							ImageLoader.getInstance().loadImage((String)msg.obj, new SimpleImageLoadingListener(){
//								@Override
//							    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//									finalPhoto.setImageBitmap(loadedImage);
//									myAdapter.notifyDataSetChanged();
//							    }
//							});
						}
						
					};
					Hack.downloadSmallPhoto(WaterfallActivity.this, url, handler);
					
				}
			}
			

			return convertView;
		}
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		timePhotos_ListView.setAdapter(myAdapter);
	}
	
}
