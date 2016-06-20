package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHMute;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

/**
 * Created by adam on 20/06/16.
 *
 * Posts a new {@link HHMute} to the server
 */
class PostMutePostTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "PostMutePostTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/mutes/%1$s";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnPostMute(Boolean success, HHMute mute);
	}

	private final Callback callbackTo;
	private final long postID;
	private final String authToken;
	private HHMute muteReturned;

	public PostMutePostTask(String authToken, long postID, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.postID = postID;
		this.authToken = authToken;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String urlString = String.format(Locale.ENGLISH,
										 VM_SERVER_ADDRESS,
										 postID);
		Log.d(TAG, "Posting mute to " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestProperty("Authorization", "Token token="+ authToken);
				urlConnection.setRequestMethod("POST");

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){

					InputStream in = new BufferedInputStream(urlConnection.getInputStream());
					String inString = ZZZUtility.convertStreamToString(in);
					in.close();

					muteReturned = new Gson().fromJson(inString, HHMute.class);

					return (muteReturned != null);

				} else {
					Log.e(TAG, "HTTP ERROR! " + httpResult);
				}

				return true;
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
	protected void onPostExecute(Boolean result) {
		callbackTo.returnPostMute(result, muteReturned);	// sends results back
	}

}
