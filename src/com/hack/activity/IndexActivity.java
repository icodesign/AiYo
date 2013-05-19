package com.hack.activity;

import java.util.Locale;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.hack.core.Hack;
import com.hack.flikr.GetOAuthTokenTask;
import com.hack.flikr.OAuthTask;
import com.hack.utils.HttpClient;
import com.hack.view.MarkOverlay;
import com.hack.view.MyViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class IndexActivity extends BaseActivity implements OnPageChangeListener{
	ImageButton take_photo;
    SlidingMenu menu;
    ListView menuList;
    MyViewPager viewpager;
    TextView username;
    OAuthTask task;
	OAuth oauth;
	private BMapManager mBMapMan = null;
	private MapView mMapView = null;
	private MapController mMapController = null;
	private MarkOverlay mMarkOverlay = null;
    private static final int NONE = 0;
	private static final int PHOTO_GRAPH = 1;// 拍照
    private static final int PHOTO_RESOULT = 3;// 结果
    private static final String IMAGE_UNSPECIFIED = "image/*";
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init("161FBC3C583A46B39F62EB361F5A1B1D35C8457D", null);
		setContentView(R.layout.index);
		task = new OAuthTask(this);
		initMenu();
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		
		take_photo = (ImageButton)findViewById(R.id.btn_head_menu_rightBtn);
		take_photo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(IndexActivity.this,TakePhotoActivity.class);
                startActivity(intent);
			}
		});
		findViewById(R.id.btn_head_menu_leftBtn).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (menu.isMenuShowing()){
					menu.showContent(true);
				}else{
					menu.showMenu(true);
				}
			}
		});
//		IndexModel model = new IndexModel();
//		model.coverImgUrl = "http://www.xutour.com/picture/2007-6/2007628121417.jpg";
//		model.description = "一个美丽的地方";
//		model.location = "马尔代夫";
//		model.time = "5月19日";
//		ArrayList<IndexModel> data = new ArrayList<IndexModel>();
//		for (int i=0;i<100;i++){
//			data.add(model);
//		}
		viewpager = (MyViewPager)findViewById(R.id.viewpager);
		viewpager.setActivity(this);
		
		mMapView = (MapView) viewpager.mapLayout.findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		
		GeoPoint point = new GeoPoint(39911900, 116391000);
		mMapController.setCenter(point);
		mMapController.setZoom(12);

		Drawable marker = getResources().getDrawable(R.drawable.iconmarka);
		mMarkOverlay = new MarkOverlay(marker, this);		

		mMapView.getOverlays().add(mMarkOverlay);
		mMarkOverlay.update();
		mMapView.refresh();		
//		viewpager.timeLineAdaper.setData(data);
//		viewpager.timeLineAdaper.notifyDataSetChanged();
		//viewpager.setOnPageChangeListener(this);
		initTimeLine();
		viewpager.lv_home.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onRefresh(PullToRefreshBase<ListView> refreshView) {
				// TODO Auto-generated method stub
				initTimeLine();
			}
		});
		
	}
	
	private void initTimeLine() {
		// TODO Auto-generated method stub
		User user = Hack.getUser(getApplicationContext());
		if (user==null){
			makeToast("请连接到Flickr账号");
			return;
		}
		RequestParams params = new RequestParams();
		params.put("userid", user.getId());
		
		HttpClient.get(Hack.TIMELINE_URL, params, new AsyncHttpResponseHandler(){
			
			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
//				viewpager.timeLineAdaper.setData(Hack.parseTimeLineData(res));
//				viewpager.timeLineAdaper.notifyDataSetChanged();
				viewpager.lv_home.onRefreshComplete();
			}

		

			@Override
			public void onSuccess(String arg0) {
				// TODO Auto-generated method stub
				super.onSuccess(arg0);
				viewpager.timeLineAdaper.setData(Hack.parseTimeLineData(arg0));
				viewpager.timeLineAdaper.notifyDataSetChanged();
			}
			
		});
	}

	private void initMenu() {
		// TODO Auto-generated method stub
		// configure the SlidingMenu
        menu = new SlidingMenu(this);
        menu.setMode(SlidingMenu.LEFT);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
        menu.setShadowWidthRes(R.dimen.shadow_width);
        //menu.setShadowDrawable(R.drawable.shadow);
        menu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        menu.setFadeDegree(0.35f);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu);
        menuList = (ListView)findViewById(R.id.menuList);
        menuList.setAdapter(new MenuListAdapter());
        menuList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View v, int position,
					long id) {
				// TODO Auto-generated method stub
				switch(position){
				case 0:
				case 1:
					viewpager.setCurrentItem(position);
				case 3:
				case 4:
					menu.showContent(true);
					break;
				case 2:
					oauth = task.checkOauth();
					if (oauth == null || oauth.getUser()==null){
						task.execute();
					}else{
						makeToast("您已连接到Flickr");
						System.out.println(oauth.getUser().getId());
					}
					break;
				}
				
			}
		});
        username = (TextView)findViewById(R.id.username);
        username.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				oauth = task.checkOauth();
				if (oauth == null || oauth.getUser()==null){
					task.execute();
				}else{
					makeToast("您已连接到Flickr");
				}
			}
		});
        User user = Hack.getUser(getApplicationContext());
        if (user!=null){
        	username.setText(user.getUsername());
        }
	}
    
	@Override
	protected void onNewIntent(Intent intent) {
		//this is very important, otherwise you would get a null Scheme in the onResume later on.
		setIntent(intent);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Intent intent = getIntent();
		String scheme = intent.getScheme();
		OAuth savedToken = task.getOAuthToken();
		if (task.CALLBACK.equals(scheme) && (savedToken == null || savedToken.getUser() == null)) {
			Uri uri = intent.getData();
			String query = uri.getQuery();
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				OAuth oauth = task.getOAuthToken();
				if (oauth != null && oauth.getToken() != null && oauth.getToken().getOauthTokenSecret() != null) {
					GetOAuthTokenTask task = new GetOAuthTokenTask(this);
					task.execute(oauthToken, oauth.getToken().getOauthTokenSecret(), oauthVerifier);
				}
			}
		}

	}
	
	public void onOAuthDone(OAuth result) {
		if (result == null) {
			Toast.makeText(this,
					"Authorization failed", //$NON-NLS-1$
					Toast.LENGTH_LONG).show();
		} else {
			com.googlecode.flickrjandroid.people.User user = result.getUser();
			OAuthToken token = result.getToken();
			if (user == null || user.getId() == null || token == null
					|| token.getOauthToken() == null
					|| token.getOauthTokenSecret() == null) {
				Toast.makeText(this,
						"Authorization failed", //$NON-NLS-1$
						Toast.LENGTH_LONG).show();
				return;
			}
			String message = String.format(Locale.US, "Authorization Succeed: user=%s, userId=%s, oauthToken=%s, tokenSecret=%s", //$NON-NLS-1$
					user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			Toast.makeText(this,
					message,
					Toast.LENGTH_LONG).show();
			task.saveOAuthToken(user.getUsername(), user.getId(), token.getOauthToken(), token.getOauthTokenSecret());
			username.setText(user.getUsername());
			initTimeLine();
		}
	}
    
	private class MenuListAdapter extends BaseAdapter{
		int[] icons = new int[]{R.drawable.icon_01,R.drawable.icon_02,R.drawable.icon_03,
				R.drawable.icon_04,R.drawable.icon_05};
		String[] names = getResources().getStringArray(R.array.menu);
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return names.length;
		}

		@Override
		public Object getItem(int arg0) {
			// TODO Auto-generated method stub
			return names[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			// TODO Auto-generated method stub
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup arg2) {
			// TODO Auto-generated method stub
			ViewHodler holder;
			if (convertView==null){
				convertView = LayoutInflater.from(getApplicationContext())
						.inflate(R.layout.menuitem, null);
				holder = new ViewHodler();
				holder.icon = (ImageView)convertView.findViewById(R.id.menuIcon);
				holder.name = (TextView)convertView.findViewById(R.id.menuName);
				convertView.setTag(holder);
			}else{
				holder = (ViewHodler)convertView.getTag();
			}
			if (position<icons.length){
				holder.icon.setImageDrawable(getResources().getDrawable(icons[position]));
				holder.name.setText(names[position]);
			}
			return convertView;
			
		}
		
	}
	
	private class ViewHodler{
		ImageView icon;
		TextView name;
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPageSelected(int arg0) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		super.onDestroy();
	}
	
}
