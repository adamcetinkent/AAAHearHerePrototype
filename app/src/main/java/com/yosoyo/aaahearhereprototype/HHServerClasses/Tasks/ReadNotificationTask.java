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
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests that a notification be marked as read
 */
class ReadNotificationTask extends AsyncTask<Void, Void, HHNotification> {
	private static final String TAG = ReadNotificationTask.class.getSimpleName();
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/notifications/read/%1$s/";

	private final HHNotification notification;

	public interface Callback {
		void returnReadNotification(HHNotification readNotifications);
	}

	private final Callback callbackTo;

	public ReadNotificationTask(final HHNotification notification,
								final Callback callbackTo) {
		this.notification = notification;
		this.callbackTo = callbackTo;
	}

	@Override
	protected HHNotification doInBackground(Void... params) {
		String urlString;
		urlString = String.format(Locale.ENGLISH,
								  VM_SERVER_ADDRESS,
								  notification.getID());
		urlString = urlString.replace(" ", "%20");
		Log.d(TAG, "Reading Notifications from " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+ HHUser.getAuthorisationToken());
			urlConnection.setRequestMethod("POST");
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHNotification notification = new Gson().fromJson(streamString, HHNotification.class);
				return notification;
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
	protected void onPostExecute(HHNotification result) {
		callbackTo.returnReadNotification(result);	// sends results back
	}

}
