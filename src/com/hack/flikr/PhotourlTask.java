/**
 * 
 */
package com.hack.flikr;

import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.photos.Photo;
import com.hack.activity.BaseActivity;

public class PhotourlTask extends AsyncTask<OAuth, Void, String> {
	private BaseActivity activity;
	private String photoid;
	private Handler handler;
	private Boolean flag;
	public PhotourlTask(BaseActivity activity,String photoid,Handler handler,Boolean flag) {
		this.activity = activity;
		this.photoid = photoid;
		this.handler = handler;
		this.flag = flag;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(OAuth... params) {
		OAuth oauth = params[0];
		OAuthToken token = oauth.getToken();
		try {
			Flickr f = FlickrHelper.getInstance()
					.getFlickrAuthed(token.getOauthToken(), token.getOauthTokenSecret());
			Photo p = f.getPhotosInterface().getPhoto(photoid);
			if (flag)
				return p.getLargeUrl();
			else
				return p.getSmallSquareUrl();
		} catch (Exception e) {
			//activity.makeToast(e.toString());
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
	 */
	@Override
	protected void onPostExecute(String string) {
		Message msg = new Message();
		msg.obj = string;
		handler.sendMessage(msg);
	}
	
	
}