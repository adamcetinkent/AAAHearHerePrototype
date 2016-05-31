package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests the posts from the server that fall near the given location
 */
class GetPostsAtLocationTask extends AsyncTask<Void, Void, List<HHNotification>> {
	private static final String TAG = "GetPostsTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/at/%1$.6f/%2$.6f";

	public interface Callback {
		void returnPostsAtLocation(List<HHNotification> notifications);
	}

	private final Location location;
	private final String authToken;
	private final Callback callbackTo;

	public GetPostsAtLocationTask(Location location, long userID, String authToken, Callback callbackTo) {
		this.location = location;
		this.authToken = authToken;
		this.callbackTo = callbackTo;
	}

	@Override
	protected List<HHNotification> doInBackground(Void... params) {
		String urlString = String.format(Locale.ENGLISH,
										 VM_SERVER_ADDRESS,
										 location.getLatitude(),
										 location.getLongitude());
		Log.d(TAG, "Fetching Posts from " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+ authToken);
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHNotification[] notifications = new Gson().fromJson(streamString,HHNotification[].class);
				return new ArrayList<>(Arrays.asList(notifications));
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
	protected void onPostExecute(List<HHNotification> result) {
		callbackTo.returnPostsAtLocation(result);	// sends results back
	}

}
