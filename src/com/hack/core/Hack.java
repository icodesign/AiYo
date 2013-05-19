package com.hack.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.googlecode.flickrjandroid.uploader.UploadMetaData;
import com.hack.activity.BaseActivity;
import com.hack.activity.R;
import com.hack.flikr.ImageUploadTask;
import com.hack.flikr.PhotourlTask;
import com.hack.model.IndexModel;
import com.hack.model.LocModel;
import com.hack.utils.HttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;




/**
 * 核心API
 * @author lee
 *
 */
public class Hack {
	public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken"; //$NON-NLS-1$
	public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret"; //$NON-NLS-1$
	public static final String KEY_USER_NAME = "flickrj-android-userName"; //$NON-NLS-1$
	public static final String KEY_USER_ID = "flickrj-android-userId"; //$NON-NLS-1$
	public static final String PREFS_NAME = "flickrj-android-sample-pref"; //$NON-NLS-1$
	public static final String PHOTOID = "flickrj-android-sample-pref"; //$NON-NLS-1$
	public static final String PREFS_NAME_GALLERY = "flickrj-android-sample-pref-gallery";
	
	public static final String BASE_URL = "http://hackday.sinaapp.com"; 
	public static final String TIMELINE_URL = BASE_URL+"/timeline/";
	public static final String ADD_PTOTO_URL = BASE_URL+"/insert_photo/";
	public static final String GET_PTOTO_URL = BASE_URL+"/get_photo/";
	
	public static int SCREEN_WIDTH_PIXEL = 0;
	public static int SCREEN_HEIGHT_PIXEL = 0;
	public static DisplayImageOptions options = new DisplayImageOptions.Builder()
	.showStubImage(R.drawable.ic_stub)
	.showImageForEmptyUri(R.drawable.ic_empty)
	.showImageOnFail(R.drawable.ic_error)
	.cacheInMemory()
	.cacheOnDisc()
	.displayer(new RoundedBitmapDisplayer(20))
	.build();
	public static String galleryID;
	/**
	 * 获取flickr用户
	 * @param mContext
	 * @return
	 */
	public static User getUser(Context mContext){
		try{
		return getOAuthToken(mContext).getUser();
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 上传图像到flickr
	 * @param activity
	 * @param data
	 * @param imageName
	 * @param metaData
	 */
	public static void uploadImage(BaseActivity activity,byte[] data,String imageName,UploadMetaData metaData){
		new ImageUploadTask(activity, data, imageName, metaData).execute(getOAuthToken(activity));
	}
	
	/**
	 * 
	 * @param mContext
	 * @return
	 */
	public static String getNextPhotoId(Context mContext){
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		String next = settings.getString(PHOTOID, "0");
		Editor edit = settings.edit();
		edit.putString(PHOTOID, String.valueOf(Integer.valueOf(next)+1));
		edit.commit();
		return next;
	}
	
	public static ArrayList<String> getGalleryArray(Context mContext) {
		// TODO Auto-generated method stub
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME_GALLERY, Context.MODE_PRIVATE);
		Map<String,String> map = (Map<String, String>) settings.getAll();
		Set<String> s = map.keySet();
		ArrayList<String> list = new ArrayList<String>();
		for (String t:s){
			list.add(t);
		}
		return list;
	}
	
	public static void addGallery(Context mContext,String name) {
		// TODO Auto-generated method stub
		SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME_GALLERY, Context.MODE_PRIVATE);
		Editor edit = settings.edit();
		edit.putString(name, "");
		edit.commit();
	}
	
	public static void addNewPhoto(Context mContext,String photoid,String description,String galleryid){
		User user = getUser(mContext);
		System.out.println(1);
		if (user!=null){
			String userid=user.getId();
			RequestParams params = new RequestParams();
			params.put("userid", userid);
			params.put("photoid", photoid);
			params.put("lat", LocModel.getLat());
			params.put("lng", LocModel.getLng());
			params.put("place", LocModel.getCity());
			params.put("description", description);
			params.put("galleryid", galleryid);
			System.out.println(galleryid);
			HttpClient.get(ADD_PTOTO_URL, params,new AsyncHttpResponseHandler());
		}
	}
		
	public static OAuth getOAuthToken(Context mContext) {
	   SharedPreferences settings = mContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
	   String oauthTokenString = settings.getString(KEY_OAUTH_TOKEN, null);
	   String tokenSecret = settings.getString(KEY_TOKEN_SECRET, null);
	   if (oauthTokenString == null && tokenSecret == null) {
	   	return null;
	   }
	   OAuth oauth = new OAuth();
	   String userName = settings.getString(KEY_USER_NAME, null);
	   String userId = settings.getString(KEY_USER_ID, null);
	   if (userId != null) {
	   	User user = new User();
	   	user.setUsername(userName);
	   	user.setId(userId);
	   	oauth.setUser(user);
	   }
	   OAuthToken oauthToken = new OAuthToken();
	   oauth.setToken(oauthToken);
	   oauthToken.setOauthToken(oauthTokenString);
	   oauthToken.setOauthTokenSecret(tokenSecret);
	   return oauth;
   }

	public static ArrayList<IndexModel> parseTimeLineData(String arg0) {
		// TODO Auto-generated method stub
		ArrayList<IndexModel> list = new ArrayList<IndexModel>();
		IndexModel model;
		try {
			JSONArray jArray = new JSONObject(arg0).getJSONArray("data");
			for (int i=0;i<jArray.length();i++){
				JSONObject ob = jArray.getJSONObject(i);
				model = new IndexModel();
				model.galleryID = ob.getString("galleryid");
				model.location = ob.getString("location");
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat time=new SimpleDateFormat("MM月dd日"); 
				try {
					model.timeStart = time.format(sdf.parse(ob.getString("timestart")));
					model.timeEnd = time.format(sdf.parse(ob.getString("timeend")));
					System.out.println(model.timeStart);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				model.coverImgUrl = ob.getString("coverurl");
				list.add(model);
				System.out.println(list.size());
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return list;
	}

	public static void downloadPhoto(BaseActivity activity, String photoid, Handler handler) {
		// TODO Auto-generated method stub
		new PhotourlTask(activity, photoid, handler,true).execute(getOAuthToken(activity));
	}
	
	public static void downloadSmallPhoto(BaseActivity activity, String photoid, Handler handler) {
		// TODO Auto-generated method stub
		new PhotourlTask(activity, photoid, handler,false).execute(getOAuthToken(activity));
	}

}
