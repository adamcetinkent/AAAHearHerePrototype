package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests notifications from the server
 */
class GetNotificationsTask extends AsyncTask<Void, Void, List<HHNotification>> {
	private static final String TAG = GetNotificationsTask.class.getSimpleName();
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/notifications/%1$s";
	private static final String VM_SERVER_ADDRESS_SINCE = WebHelper.SERVER_IP + "/notifications/%1$s/since/%2$s";
	private static final String VM_SERVER_ADDRESS_EXCLUDE = WebHelper.SERVER_IP + "/notifications/%1$s/since/%2$s/exclude/%3$s";

	private final String authToken;
	private final Timestamp sinceTime;
	private final boolean newOnly;
	private final Long[] excludeIDs;

	public interface Callback {
		void returnGetNotifications(List<HHNotification> notifications);
	}

	private final Callback callbackTo;

	public GetNotificationsTask(final String authToken,
								final Timestamp sinceTime,
								boolean newOnly,
								final Long[] excludeIDs,
								final Callback callbackTo) {
		this.authToken = authToken;
		this.excludeIDs = excludeIDs;
		this.newOnly = newOnly;
		this.sinceTime = sinceTime;
		this.callbackTo = callbackTo;
	}

	@Override
	protected List<HHNotification> doInBackground(Void... params) {
		String urlString;
		String newPath = newOnly ? "unsent" : "all";
		if (excludeIDs != null && excludeIDs.length > 0)
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS_EXCLUDE,
									  newPath,
									  sinceTime.toString(),
									  ZZZUtility.formatURL(excludeIDs));
		else if (sinceTime != null)
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS_SINCE,
									  newPath,
									  sinceTime.toString());
		else
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS,
									  newPath);
		urlString = urlString.replace(" ", "%20");
		Log.d(TAG, "Fetching Notifications from " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			if (authToken == null)
				urlConnection.setRequestProperty("Authorization", "Token token="+ HHUser.getAuthorisationToken());
			else
				urlConnection.setRequestProperty("Authorization", "Token token="+ authToken);
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHNotification[] notifications = new Gson().fromJson(streamString, HHNotification[].class);
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
		callbackTo.returnGetNotifications(result);	// sends results back
	}

}
