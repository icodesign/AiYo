/**
 * 
 */
package com.hack.flikr;

import android.os.AsyncTask;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthInterface;
import com.hack.activity.IndexActivity;

/**
 * @author Toby Yu(yuyang226@gmail.com)
 *
 */
public class GetOAuthTokenTask extends AsyncTask<String, Integer, OAuth> {

	private IndexActivity activity;

	public GetOAuthTokenTask(IndexActivity context) {
		this.activity = context;
	}

	@Override
	protected OAuth doInBackground(String... params) {
		String oauthToken = params[0];
		String oauthTokenSecret = params[1];
		String verifier = params[2];

		Flickr f = FlickrHelper.getInstance().getFlickr();
		OAuthInterface oauthApi = f.getOAuthInterface();
		try {
			return oauthApi.getAccessToken(oauthToken, oauthTokenSecret,
					verifier);
		} catch (Exception e) {
			return null;
		}

	}

	@Override
	protected void onPostExecute(OAuth result) {
		if (activity != null) {
			activity.onOAuthDone(result);
		}
	}


}
