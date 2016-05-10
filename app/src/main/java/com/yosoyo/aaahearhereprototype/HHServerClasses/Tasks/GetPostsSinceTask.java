package com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHPostFullProcess;
import com.yosoyo.aaahearhereprototype.HHServerClasses.HHModels.HHUser;
import com.yosoyo.aaahearhereprototype.HHServerClasses.Tasks.TaskReturns.HHPostFullNested;
import com.yosoyo.aaahearhereprototype.ZZZUtility;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by adam on 18/02/16.
 *
 * Requests the posts from the server sinceTime the given time
 */
class GetPostsSinceTask extends AsyncTask<Void, Void, List<HHPostFullProcess>> {
	private static final String TAG = "GetPostsSinceTask";
	private static final String VM_SERVER_ADDRESS = WebHelper.SERVER_IP + "/posts/since/%1$s";
	private static final String VM_SERVER_ADDRESS_EXCLUDE = WebHelper.SERVER_IP + "/posts/since/%1$s/exclude/%2$s";

	//private final long userID;
	private final Timestamp sinceTime;
	private final Long[] excludeIDs;

	public interface Callback {
		void returnPosts(List<HHPostFullProcess> postsToProcess);
	}

	private final Callback callbackTo;

	public GetPostsSinceTask(final Timestamp sinceTime,
							 final Long[] excludeIDs,
							 final Callback callbackTo) {
		//this.userID = userID;
		this.excludeIDs = excludeIDs;
		this.sinceTime = sinceTime;
		this.callbackTo = callbackTo;
	}

	@Override
	protected List<HHPostFullProcess> doInBackground(Void... params) {
		String urlString;
		if (excludeIDs != null && excludeIDs.length > 0)
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS_EXCLUDE,
									  sinceTime.toString(),
									  ZZZUtility.formatURL(excludeIDs));
		else
			urlString = String.format(Locale.ENGLISH,
									  VM_SERVER_ADDRESS,
									  sinceTime.toString());
		urlString = urlString.replace(" ", "%20");
		Log.d(TAG, "Fetching Posts from " + urlString);
		try {
			URL url = new URL(urlString);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestProperty("Authorization", "Token token="+ HHUser.getAuthorisationToken());
			try {
				InputStream in = new BufferedInputStream(urlConnection.getInputStream());
				String streamString = ZZZUtility.convertStreamToString(in);
				HHPostFullNested[] postsNested = new Gson().fromJson(streamString, HHPostFullNested[].class);
				List<HHPostFullProcess> posts = new ArrayList<>(postsNested.length);
				for (HHPostFullNested postNested : postsNested) {
					posts.add(new HHPostFullProcess(postNested));
				}
				return posts;
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
	protected void onPostExecute(List<HHPostFullProcess> result) {
		callbackTo.returnPosts(result);	// sends results back
	}

}
