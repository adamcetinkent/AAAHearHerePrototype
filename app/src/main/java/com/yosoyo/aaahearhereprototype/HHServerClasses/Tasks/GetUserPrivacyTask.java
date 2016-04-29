package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests from the server the privacy state of the given user with respect to the current user
 */
class GetUserPrivacyTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "GetUserPrivacyTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/privacy/by/%1$d/";

	public interface Callback {
		void returnUserPrivacy(boolean userPrivacy);
	}

	private final long userID;
	private final Callback callbackTo;

	public GetUserPrivacyTask(long userID, Callback callbackTo) {
		this.userID = userID;
		this.callbackTo = callbackTo;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String urlString = String.format(Locale.ENGLISH,
										 VM_SERVER_ADDRESS,
										 userID);
		Log.d(TAG, "Fetching user privacy by " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+HHUser.getAuthorisationToken());
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				Boolean userPrivacy = new Gson().fromJson(streamString, Boolean.class);
				return userPrivacy;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean userPrivacy) {
		callbackTo.returnUserPrivacy(userPrivacy);	// sends results back
	}

}
