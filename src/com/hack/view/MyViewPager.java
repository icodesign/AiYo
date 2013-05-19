package com.hack.view;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hack.activity.BaseActivity;
import com.hack.activity.R;
import com.hack.activity.WaterfallActivity;
import com.hack.core.Hack;
import com.hack.model.IndexModel;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


public class MyViewPager extends ViewPager{
		BaseActivity activity;
        MyPagerAdapter pagerAdapter;
    	List<View> listViews;
    	public View timelineLayout,mapLayout;
    	protected ImageLoader imageLoader = ImageLoader.getInstance();
    	public PullToRefreshListView lv_home;
    	public TimeLineAdapter timeLineAdaper;
        public MyViewPager(Context context) {
            super(context);
            init();
        }
        
        public MyViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }
                     
		private void init() {
			// TODO Auto-generated method stub
			LayoutInflater inflater = LayoutInflater.from(getContext());
			timelineLayout = inflater.inflate(R.layout.timelinelayout, null);
			mapLayout = inflater.inflate(R.layout.maplayout, null);
			lv_home = (PullToRefreshListView) timelineLayout.findViewById(R.id.lv_home);
			timeLineAdaper = new TimeLineAdapter(getContext());
			lv_home.setAdapter(timeLineAdaper);
			
//			downloadLayout = inflater.inflate(R.layout.download, null);
//			//docCategory = (DocCategory)homeLayout.findViewById(R.id.doccategory);
//			doclist = (DocFall)homeLayout.findViewById(R.id.doclist);
//			collection = (DocListView)collectionLayout.findViewById(R.id.collection);
//			login = (LinearLayout)collectionLayout.findViewById(R.id.login);
//			btnLogin = (Button)collectionLayout.findViewById(R.id.btnLogin);
//			collection.setVisibility(View.GONE);
//			download = (DownloadListView)downloadLayout.findViewById(R.id.download);
			
			listViews = new ArrayList<View>();
			listViews.add(timelineLayout);
			listViews.add(mapLayout);
			
	        pagerAdapter = new MyPagerAdapter(listViews);
	        this.setAdapter(pagerAdapter);   
		}
		
		public void setActivity(BaseActivity activity){
			this.activity = activity;
		}
		
		public class TimeLineAdapter extends BaseAdapter {

			private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
			
			
			private Context context;
			private ArrayList<IndexModel> data = new ArrayList<IndexModel>();
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.ic_stub)
			.showImageForEmptyUri(R.drawable.ic_empty)
			.showImageOnFail(R.drawable.ic_error)
			.cacheInMemory()
			.cacheOnDisc()
			.displayer(new RoundedBitmapDisplayer(20))
			.build();
			public TimeLineAdapter(Context mContext){
				this.context = mContext;
			}
			
			public void setData(ArrayList<IndexModel> data){
				this.data=data;
			}
			
			@Override
			public int getCount() {
				return data.size();
			}

			@Override
			public Object getItem(int position) {
				return position;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) {
				View view = convertView;
				final ViewHolder holder;
				if (convertView == null) {
					view = LayoutInflater.from(context).inflate(R.layout.timeline_item, parent, false);
					holder = new ViewHolder();
					holder.location = (TextView) view.findViewById(R.id.tv_location);
					holder.time = (TextView) view.findViewById(R.id.tv_time);
					holder.description = (TextView) view.findViewById(R.id.tv_desc);
					holder.image = (ImageView) view.findViewById(R.id.iv_coverimg);
					view.setTag(holder);
				} else {
					holder = (ViewHolder) view.getTag();
				}
				final IndexModel model = data.get(position);
				holder.location.setText(model.location);
				Handler handler = new Handler(){

					@Override
					public void handleMessage(Message msg) {
						// TODO Auto-generated method stub
						super.handleMessage(msg);
						imageLoader.displayImage((String)msg.obj, holder.image, options, animateFirstListener);
					}
					
				};
				Hack.downloadPhoto(activity, model.coverImgUrl,handler);
				holder.image.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(activity,WaterfallActivity.class);
						intent.putExtra("galleryid", model.galleryID);
						activity.startActivity(intent);
					}
				});
				holder.time.setText(model.timeStart+"-"+model.timeEnd);
				holder.description.setText(model.galleryID);
				return view;
			}
			
		}
		
		private class ViewHolder {
			public TextView location;
			public TextView time;
			public TextView description;
			public ImageView image;
		}

		public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

			static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				if (loadedImage != null) {
					ImageView imageView = (ImageView) view;
					boolean firstDisplay = !displayedImages.contains(imageUri);
					if (firstDisplay) {
						FadeInBitmapDisplayer.animate(imageView, 500);
						displayedImages.add(imageUri);
					}
				}
			}
		}
		
}