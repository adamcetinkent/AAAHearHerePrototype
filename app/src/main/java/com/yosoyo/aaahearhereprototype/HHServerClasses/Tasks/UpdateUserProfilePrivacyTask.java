package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by adam on 28/06/16.
 *
 * Updates the profile privacy setting of the current user
 */
class UpdateUserProfilePrivacyTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = UpdateUserProfilePrivacyTask.class.getSimpleName();
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/users/profile_privacy/%1$s";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnUpdateUserProfilePrivacy(Boolean success, int profilePrivacy);
	}

	private final Callback callbackTo;
	private final int profilePrivacy;
	private final String authToken;

	private int result;

	public UpdateUserProfilePrivacyTask(String authToken, int profilePrivacy, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.profilePrivacy = profilePrivacy;
		this.authToken = authToken;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String urlString = String.format(Locale.ENGLISH,
										 VM_SERVER_ADDRESS,
										 profilePrivacy);
		Log.d(TAG, "Updating profile privacy to " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty("Authorization", "Token token="+ authToken);
				urlConnection.setRequestMethod("PATCH");

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					result = new Gson().fromJson(inString, Integer.class);

					return result == profilePrivacy;

				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}

				return false;
			} finally {
				urlConnection.disconnect();
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	// Fires once doInBackground is completed
	protected void onPostExecute(Boolean success) {
		callbackTo.returnUpdateUserProfilePrivacy(success, result);	// sends results back
	}

}
