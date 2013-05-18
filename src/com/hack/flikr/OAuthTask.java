
package com.hack.flikr;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.googlecode.flickrjandroid.Flickr;
import com.googlecode.flickrjandroid.auth.Permission;
import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.hack.core.Hack;

public class OAuthTask extends AsyncTask<Void, Integer, String> {

	private static final Logger logger = LoggerFactory
			.getLogger(OAuthTask.class);
	public static final String CALLBACK = "hack-oauth"; //$NON-NLS-1$
	private static final Uri OAUTH_CALLBACK_URI = Uri.parse(CALLBACK+"://oauth"); //$NON-NLS-1$
	public static final String KEY_OAUTH_TOKEN = "flickrj-android-oauthToken"; //$NON-NLS-1$
	public static final String KEY_TOKEN_SECRET = "flickrj-android-tokenSecret"; //$NON-NLS-1$
	public static final String KEY_USER_NAME = "flickrj-android-userName"; //$NON-NLS-1$
	public static final String KEY_USER_ID = "flickrj-android-userId"; //$NON-NLS-1$
	public static final String PREFS_NAME = "flickrj-android-sample-pref"; //$NON-NLS-1$

	/**
	 * The context.
	 */
	private Context mContext;

	/**
	 * The progress dialog before going to the browser.
	 */
	private ProgressDialog mProgressDialog;

	/**
	 * Constructor.
	 * 
	 * @param context
	 */
	public OAuthTask(Context context) {
		super();
		this.mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = ProgressDialog.show(mContext,
				"", "正在准备认证..."); //$NON-NLS-1$ //$NON-NLS-2$
		mProgressDialog.setCanceledOnTouchOutside(true);
		mProgressDialog.setCancelable(true);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dlg) {
				OAuthTask.this.cancel(true);
			}
		});
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.os.AsyncTask#doInBackground(Params[])
	 */
	@Override
	protected String doInBackground(Void... params) {
		try {
			Flickr f = FlickrHelper.getInstance().getFlickr();
			OAuthToken oauthToken = f.getOAuthInterface().getRequestToken(
					OAUTH_CALLBACK_URI.toString());
			saveTokenSecrent(oauthToken.getOauthTokenSecret());
			URL oauthUrl = f.getOAuthInterface().buildAuthenticationUrl(
					Permission.WRITE, oauthToken);
			return oauthUrl.toString();
		} catch (Exception e) {
			logger.error("Error to oauth", e); //$NON-NLS-1$
			return "error:" + e.getMessage(); //$NON-NLS-1$
		}
	}

	/**
	 * Saves the oauth token secrent.
	 * 
	 * @param tokenSecret
	 */
	private void saveTokenSecrent(String tokenSecret) {
		logger.debug("request token: " + tokenSecret); //$NON-NLS-1$
		saveOAuthToken(null, null, null, tokenSecret);
		logger.debug("oauth token secret saved: {}", tokenSecret); //$NON-NLS-1$
	}
	
	public void saveOAuthToken(String userName, String userId, String token, String tokenSecret) {
    	logger.debug("Saving userName=%s, userId=%s, oauth token={}, and token secret={}", new String[]{userName, userId, token, tokenSecret}); //$NON-NLS-1$
    	SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, token);
		editor.putString(KEY_TOKEN_SECRET, tokenSecret);
		editor.putString(KEY_USER_NAME, userName);
		editor.putString(KEY_USER_ID, userId);
		editor.commit();
    }
	
	public OAuth getOAuthToken() {
   	 //Restore preferences
		return Hack.getOAuthToken(mContext);
       
   }

	@Override
	protected void onPostExecute(String result) {
		if (mProgressDialog != null) {
			mProgressDialog.dismiss();
		}
		if (result != null && !result.startsWith("error") ) { //$NON-NLS-1$
			mContext.startActivity(new Intent(Intent.ACTION_VIEW, Uri
					.parse(result)));
		} else {
			Toast.makeText(mContext, result, Toast.LENGTH_LONG).show();
		}
	}
	
	/**
	 * 查询是否已经认证，如果已经认证返回oauth对象，否则返回null
	 * @return
	 */
	public OAuth checkOauth(){
		OAuth oauth = getOAuthToken();
		if (oauth == null || oauth.getUser() == null) {
			return null;
		}else{
			return oauth;
		}
	}

	public void clearOauth() {
		// TODO Auto-generated method stub
		SharedPreferences sp = mContext.getSharedPreferences(PREFS_NAME,
				Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(KEY_OAUTH_TOKEN, null);
		editor.putString(KEY_TOKEN_SECRET, null);
		editor.putString(KEY_USER_NAME, null);
		editor.putString(KEY_USER_ID, null);
		editor.commit();
	}

}
