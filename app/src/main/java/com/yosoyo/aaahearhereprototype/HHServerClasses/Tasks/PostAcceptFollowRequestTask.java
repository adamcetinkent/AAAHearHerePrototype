package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHFollowRequestUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHNotification;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHFollowRequestUserNested;
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
 * Posts the acceptance of a {@link HHFollowRequestUser} to the server
 */
class PostAcceptFollowRequestTask extends AsyncTask<Void, Void, Boolean> {
	private static final String TAG = "AcceptFollowRequestTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/follows/accept/%1$s";
	private static final String VM_SERVER_ADDRESS_NOTIFICATION = WebHelper.SERVER_IP + "/follows/accept/notification/%1$s/";

	// Interface for classes wanting to incorporate this class to post a user asynchronously
	public interface Callback {
		void returnPostAcceptFollowRequest(boolean success, HHFollowRequestUser acceptedFollowRequest);
	}

	private final Callback callbackTo;
	private final HHFollowRequestUser followRequest;
	private HHFollowRequestUser acceptedFollowRequest;

	private final HHNotification notification;
	private final String authToken;

	public PostAcceptFollowRequestTask(HHFollowRequestUser followRequest, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.followRequest = followRequest;
		this.notification = null;
		this.authToken = null;
	}

	public PostAcceptFollowRequestTask(String authToken, HHNotification notification, Callback callbackTo) {
		this.callbackTo = callbackTo;
		this.authToken = authToken;
		this.notification = notification;
		this.followRequest = null;
	}

	@Override
	protected Boolean doInBackground(Void... params) {
		String urlString;
		if (followRequest != null){
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS,
									  followRequest.getFollowRequest().getID());
		} else if (notification != null) {
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS_NOTIFICATION,
									  notification.getID());
		} else {
			return false;
		}
		Log.d(TAG, "Posting Follow Request to " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			try {
				urlConnection.setDoOutput(true);
				urlConnection.setDoInput(true);
				urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
				urlConnection.setRequestProperty("Accept", "application/json");
				urlConnection.setRequestMethod("POST");

				int httpResult = urlConnection.getResponseCode();
				if (httpResult == HttpURLConnection.HTTP_OK){
					if (notification != null) {
						InputStream in = new BufferedInputStream(urlConnection.getInputStream());
						String inString = ZZZUtility.convertStreamToString(in);
						in.close();

						HHFollowRequestUserNested acceptedFollowRequestNested = new Gson()
							.fromJson(inString, HHFollowRequestUserNested.class);
						acceptedFollowRequest = new HHFollowRequestUser(
							acceptedFollowRequestNested);
					}
					return true;
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
	protected void onPostExecute(Boolean result) {
		callbackTo.returnPostAcceptFollowRequest(result, acceptedFollowRequest);	// sends results back
	}

}
