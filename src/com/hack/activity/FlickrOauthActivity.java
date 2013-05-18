package com.hack.activity;

import java.util.Locale;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.googlecode.flickrjandroid.oauth.OAuth;
import com.googlecode.flickrjandroid.oauth.OAuthToken;
import com.googlecode.flickrjandroid.people.User;
import com.hack.flikr.GetOAuthTokenTask;
import com.hack.flikr.OAuthTask;

public class FlickrOauthActivity extends BaseActivity{
	OAuthTask task;
	private static final Logger logger = LoggerFactory.getLogger(FlickrOauthActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		task = new OAuthTask(this);
		OAuth oauth = task.getOAuthToken();
		if (oauth == null || oauth.getUser() == null) {
			System.out.println(1);
			task.execute();
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
			logger.debug("Returned Query: {}", query); //$NON-NLS-1$
			String[] data = query.split("&"); //$NON-NLS-1$
			if (data != null && data.length == 2) {
				String oauthToken = data[0].substring(data[0].indexOf("=") + 1); //$NON-NLS-1$
				String oauthVerifier = data[1]
						.substring(data[1].indexOf("=") + 1); //$NON-NLS-1$
				logger.debug("OAuth Token: {}; OAuth Verifier: {}", oauthToken, oauthVerifier); //$NON-NLS-1$

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
			User user = result.getUser();
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
			//load(result);
		}
	}
	
}
